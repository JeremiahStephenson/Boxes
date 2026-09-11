package com.jerry.bit.shapes.repository

import android.content.ContentResolver
import android.content.Context
import android.graphics.Point
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.room.withTransaction
import com.google.firebase.analytics.FirebaseAnalytics
import com.jerry.bit.shapes.cache.BoxesDao
import com.jerry.bit.shapes.cache.BoxesDatabase
import com.jerry.bit.shapes.cache.data.ColorAndShape
import com.jerry.bit.shapes.cache.data.FullProject
import com.jerry.bit.shapes.cache.data.Layer
import com.jerry.bit.shapes.cache.data.LayerAndPixel
import com.jerry.bit.shapes.cache.data.Pixel
import com.jerry.bit.shapes.cache.data.Project
import com.jerry.bit.shapes.extensions.logError
import com.jerry.bit.shapes.testing.MainDispatcherExtension
import com.jerry.bit.shapes.ui.boxes.data.LayerState
import com.jerry.bit.shapes.ui.shapes.Shape
import com.jerry.bit.shapes.util.CoroutineContextProvider
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.IdentityHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@ExtendWith(MainDispatcherExtension::class)
class ProjectTransferTest {
    private val database = mockk<BoxesDatabase>()
    private val dao = mockk<BoxesDao>()
    private val resolver = mockk<ContentResolver>()
    private val context = mockk<Context>()
    private val analytics = mockk<FirebaseAnalytics>(relaxed = true)
    private val contexts =
        object : CoroutineContextProvider {
            override val main: CoroutineContext = Dispatchers.Main
            override val uiImmediate: CoroutineContext = Dispatchers.Main
            override val io: CoroutineContext = Dispatchers.Main
            override val commonPool: CoroutineContext = Dispatchers.Main
        }
    private val adapter = Moshi.Builder().build().adapter(ProjectTransfer::class.java)
    private lateinit var repository: BoxesRepository

    @BeforeEach
    fun setUp() {
        mockkStatic("com.jerry.bit.shapes.extensions.AnalyticsExtKt")
        every { analytics.logError(any()) } returns Unit
        every { context.contentResolver } returns resolver
        repository =
            BoxesRepository(
                database,
                dao,
                CoroutineScope(SupervisorJob() + Dispatchers.Main),
                contexts,
                context,
                analytics,
            )
    }

    @Test
    fun `export writes a versioned project with sorted layers and pixels`() =
        runTest {
            val uri = mockk<Uri>()
            val output = ByteArrayOutputStream()
            every { resolver.openOutputStream(uri, "w") } returns output

            val result =
                repository.exportProject(
                    project = project(),
                    layers = listOf(layerState(20, 1, false), layerState(10, 0, true)),
                    selections =
                        mapOf(
                            10L to
                                identityMapOf(
                                    point() to
                                        identityMapOf(
                                            point(2, 1) to color(),
                                            point(0, 0) to color(Shape.Circle),
                                        ),
                                ),
                            20L to emptyMap(),
                        ),
                    destinationDocument = uri,
                )

            assertEquals(uri, result)
            val transfer = adapter.fromJson(output.toString())!!
            assertEquals(ProjectTransfer.FORMAT, transfer.format)
            assertEquals(ProjectTransfer.CURRENT_VERSION, transfer.version)
            assertEquals(listOf(0, 1), transfer.project.layers.map { it.index })
            assertEquals(
                listOf(0 to 0, 2 to 1),
                transfer.project.layers
                    .first()
                    .pixels
                    .map { it.x to it.y },
            )
            assertFalse(
                transfer.project.layers
                    .last()
                    .visible,
            )
            assertEquals(
                Shape.Circle.name,
                transfer.project.layers
                    .first()
                    .pixels
                    .first()
                    .shape,
            )
        }

    @Test
    fun `export reports an unavailable destination`() =
        runTest {
            val uri = mockk<Uri>()
            every { resolver.openOutputStream(uri, "w") } returns null

            val error =
                assertThrows<IllegalStateException> {
                    repository.exportProject(project(), listOf(layerState()), emptyMap(), uri)
                }

            assertEquals("The project file could not be opened for writing", error.message)
        }

    @Test
    fun `export propagates write failures`() =
        runTest {
            val uri = mockk<Uri>()
            every { resolver.openOutputStream(uri, "w") } throws IOException("disk full")

            val error =
                assertThrows<IOException> {
                    repository.exportProject(project(), listOf(layerState()), emptyMap(), uri)
                }

            assertEquals("disk full", error.message)
        }

    @ParameterizedTest(name = "rejects {0}")
    @MethodSource("invalidTransfers")
    fun `import rejects every invalid project shape`(
        @Suppress("UNUSED_PARAMETER") name: String,
        transfer: ProjectTransfer,
        expectedMessage: String,
    ) = runTest {
        val error = assertThrows<IllegalArgumentException> { importJson(adapter.toJson(transfer)) }
        assertEquals(expectedMessage, error.message)
        coVerify(exactly = 0) { dao.insertProject(any()) }
    }

    @Test
    fun `import rejects malformed and empty JSON`() =
        runTest {
            assertThrows<Exception> { importJson("not json") }
            assertEquals("The project file is empty", assertThrows<IllegalStateException> { importJson("null") }.message)
        }

    @Test
    fun `import rejects an unreadable source`() =
        runTest {
            val uri = mockk<Uri>()
            every { resolver.openAssetFileDescriptor(uri, "r") } returns null
            every { resolver.openInputStream(uri) } returns null

            assertEquals(
                "The project file could not be opened",
                assertThrows<IllegalStateException> { repository.importProject(uri) }.message,
            )
        }

    @Test
    fun `import rejects content over the streaming size limit`() =
        runTest {
            val oversized = " ".repeat(20 * 1024 * 1024 + 1)
            assertEquals("That project file is too large", assertThrows<IllegalArgumentException> { importJson(oversized) }.message)
        }

    @Test
    fun `import remaps IDs and inserts all content`() =
        runTest {
            mockTransaction()
            coEvery { dao.insertProject(any()) } returns 101L
            coEvery { dao.insertLayer(any()) } returnsMany listOf(201L, 202L)
            coEvery { dao.insertAllPixels(any()) } returns Unit
            val transfer =
                validTransfer().copy(
                    project =
                        validTransfer().project.copy(
                            layers =
                                listOf(
                                    validLayer(0).copy(pixels = listOf(TransferPixel(1, 2, Color.Red.hashCode(), Shape.Star.name))),
                                    validLayer(1).copy(visible = false, pixels = emptyList()),
                                ),
                        ),
                )

            assertEquals(101L, importJson(adapter.toJson(transfer)))
            coVerify { dao.insertProject(match { it.id == 0L && it.name == "Test" }) }
            coVerify { dao.insertLayer(match { it.id == 0L && it.projectId == 101L && it.index == 0 }) }
            coVerify { dao.insertLayer(match { it.id == 0L && it.projectId == 101L && it.index == 1 && !it.on }) }
            coVerify {
                dao.insertAllPixels(
                    match { it.size == 1 && it[0].id == 0L && it[0].layerId == 201L },
                )
            }
        }

    @Test
    fun `import propagates database failure`() =
        runTest {
            mockTransaction()
            coEvery { dao.insertProject(any()) } throws IllegalStateException("database full")

            assertEquals(
                "database full",
                assertThrows<IllegalStateException> { importJson(adapter.toJson(validTransfer())) }.message,
            )
        }

    @Test
    fun `duplication rejects a missing source`() =
        runTest {
            coEvery { dao.getFullProjectById(5L) } returns null
            assertEquals(
                "The copied project no longer exists",
                assertThrows<IllegalStateException> { repository.duplicateProject(5L) }.message,
            )
        }

    @Test
    fun `duplication copies the complete graph with fresh IDs`() =
        runTest {
            mockTransaction()
            val source = fullProject()
            coEvery { dao.getFullProjectById(5L) } returns source
            coEvery { dao.insertProject(any()) } returns 105L
            coEvery { dao.insertLayer(any()) } returns 205L
            coEvery { dao.insertAllPixels(any()) } returns Unit

            assertEquals(105L, repository.duplicateProject(5L))
            coVerify {
                dao.insertProject(
                    match { it.id == 0L && it.name == "Copy of Source" && it.columns == source.project.columns },
                )
            }
            coVerify { dao.insertLayer(match { it.id == 0L && it.projectId == 105L && it.name == "Layer" }) }
            coVerify {
                dao.insertAllPixels(
                    match { pixels -> pixels.single().id == 0L && pixels.single().layerId == 205L && pixels.single().x == 2 },
                )
            }
        }

    @Test
    fun `duplication propagates database failure`() =
        runTest {
            mockTransaction()
            coEvery { dao.getFullProjectById(5L) } returns fullProject()
            coEvery { dao.insertProject(any()) } throws IllegalStateException("write failed")

            assertEquals("write failed", assertThrows<IllegalStateException> { repository.duplicateProject(5L) }.message)
        }

    private suspend fun importJson(json: String): Long {
        val uri = mockk<Uri>()
        every { resolver.openAssetFileDescriptor(uri, "r") } returns null
        every { resolver.openInputStream(uri) } returns ByteArrayInputStream(json.toByteArray())
        return repository.importProject(uri)
    }

    private fun mockTransaction() {
        mockkStatic("androidx.room.RoomDatabaseKt")
        coEvery { database.withTransaction<Long>(any()) } coAnswers {
            secondArg<suspend () -> Long>().invoke()
        }
    }

    private fun project(
        id: Long = 1L,
        name: String = "Test",
    ) = Project(id, name, 4, 4, Color.Black.hashCode(), Shape.Box, true, true, 1L)

    private fun layerState(
        id: Long = 10L,
        index: Int = 0,
        on: Boolean = true,
    ) = LayerState(id, 1L, index, "Layer $index", on, false, true, true)

    private fun color(shape: Shape = Shape.Box) = ColorAndShape(Color.Red.value, shape)

    private fun point(
        x: Int = 0,
        y: Int = 0,
    ) = Point().apply {
        this.x = x
        this.y = y
    }

    private fun <K, V> identityMapOf(vararg pairs: Pair<K, V>): Map<K, V> =
        IdentityHashMap<K, V>().apply { pairs.forEach { put(it.first, it.second) } }

    private fun validLayer(index: Int = 0) = TransferLayer(index, "Layer $index", true, emptyList())

    private fun validTransfer() =
        ProjectTransfer(
            project = TransferProject("Test", 4, 4, Color.Black.hashCode(), Shape.Box.name, true, true, listOf(validLayer())),
        )

    private fun fullProject() =
        FullProject(
            project = project(5L, "Source"),
            layers =
                listOf(
                    LayerAndPixel(
                        Layer(9L, 5L, 0, "Layer", true),
                        listOf(Pixel(12L, 9L, 2, 3, Color.Red.hashCode(), Shape.Circle, 10L)),
                    ),
                ),
        )

    companion object {
        @JvmStatic
        fun invalidTransfers(): List<Array<Any>> {
            val valid = ProjectTransferTest().validTransfer()
            val project = valid.project
            val layer = project.layers.single()

            fun case(
                name: String,
                value: ProjectTransfer,
                message: String,
            ) = arrayOf(name, value, message)
            return listOf(
                case("format", valid.copy(format = "other"), "This is not a BitShape project file"),
                case("version", valid.copy(version = 2), "This project file version is not supported"),
                case("blank project name", valid.copy(project = project.copy(name = " ")), "The project name is invalid"),
                case("long project name", valid.copy(project = project.copy(name = "a".repeat(101))), "The project name is invalid"),
                case("zero columns", valid.copy(project = project.copy(columns = 0)), "The canvas size is invalid"),
                case("too many rows", valid.copy(project = project.copy(rows = 201)), "The canvas size is invalid"),
                case(
                    "no layers",
                    valid.copy(project = project.copy(layers = emptyList())),
                    "The project must contain between 1 and 10 layers",
                ),
                case(
                    "too many layers",
                    valid.copy(
                        project =
                            project.copy(
                                layers =
                                    List(11) {
                                        TransferLayer(it, "Layer $it", true, emptyList())
                                    },
                            ),
                    ),
                    "The project must contain between 1 and 10 layers",
                ),
                case(
                    "no visible layer",
                    valid.copy(project = project.copy(layers = listOf(layer.copy(visible = false)))),
                    "At least one layer must be visible",
                ),
                case(
                    "duplicate layer index",
                    valid.copy(project = project.copy(layers = listOf(layer, layer))),
                    "Layer order values must be unique",
                ),
                case(
                    "invalid project shape",
                    valid.copy(project = project.copy(currentShape = "Nope")),
                    "No enum constant com.jerry.bit.shapes.ui.shapes.Shape.Nope",
                ),
                case(
                    "blank layer name",
                    valid.copy(project = project.copy(layers = listOf(layer.copy(name = " ")))),
                    "A layer name is invalid",
                ),
                case(
                    "long layer name",
                    valid.copy(project = project.copy(layers = listOf(layer.copy(name = "a".repeat(101))))),
                    "A layer name is invalid",
                ),
                case(
                    "too many pixels",
                    valid.copy(
                        project =
                            project.copy(
                                layers =
                                    listOf(
                                        layer.copy(
                                            pixels =
                                                List(17) {
                                                    TransferPixel(it, 0, 0, Shape.Box.name)
                                                },
                                        ),
                                    ),
                            ),
                    ),
                    "A layer contains too many pixels",
                ),
                case(
                    "duplicate pixel",
                    valid.copy(
                        project =
                            project.copy(
                                layers =
                                    listOf(
                                        layer.copy(
                                            pixels =
                                                List(2) {
                                                    TransferPixel(0, 0, 0, Shape.Box.name)
                                                },
                                        ),
                                    ),
                            ),
                    ),
                    "A layer contains duplicate pixels",
                ),
                case(
                    "negative x",
                    valid.copy(
                        project = project.copy(layers = listOf(layer.copy(pixels = listOf(TransferPixel(-1, 0, 0, Shape.Box.name))))),
                    ),
                    "A pixel is outside the canvas",
                ),
                case(
                    "x boundary",
                    valid.copy(
                        project = project.copy(layers = listOf(layer.copy(pixels = listOf(TransferPixel(4, 0, 0, Shape.Box.name))))),
                    ),
                    "A pixel is outside the canvas",
                ),
                case(
                    "negative y",
                    valid.copy(
                        project = project.copy(layers = listOf(layer.copy(pixels = listOf(TransferPixel(0, -1, 0, Shape.Box.name))))),
                    ),
                    "A pixel is outside the canvas",
                ),
                case(
                    "y boundary",
                    valid.copy(
                        project = project.copy(layers = listOf(layer.copy(pixels = listOf(TransferPixel(0, 4, 0, Shape.Box.name))))),
                    ),
                    "A pixel is outside the canvas",
                ),
                case(
                    "invalid pixel shape",
                    valid.copy(project = project.copy(layers = listOf(layer.copy(pixels = listOf(TransferPixel(0, 0, 0, "Nope")))))),
                    "No enum constant com.jerry.bit.shapes.ui.shapes.Shape.Nope",
                ),
            )
        }
    }
}
