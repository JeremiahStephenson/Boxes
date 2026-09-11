package com.jerry.bit.shapes.ui.home

import android.net.Uri
import app.cash.turbine.test
import com.jerry.bit.shapes.cache.BoxesDao
import com.jerry.bit.shapes.datastore.AppDataStore
import com.jerry.bit.shapes.repository.BoxesRepository
import com.jerry.bit.shapes.testing.MainDispatcherExtension
import com.jerry.bit.shapes.util.CoroutineContextProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MainDispatcherExtension::class)
class HomeViewModelTransferTest {
    private val dao = mockk<BoxesDao>()
    private val dataStore = mockk<AppDataStore>()
    private val repository = mockk<BoxesRepository>()
    private val contexts =
        object : CoroutineContextProvider {
            override val main: CoroutineContext = Dispatchers.Main
            override val uiImmediate: CoroutineContext = Dispatchers.Main
            override val io: CoroutineContext = Dispatchers.Main
            override val commonPool: CoroutineContext = Dispatchers.Main
        }
    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setUp() {
        every { dao.findAllProjects() } returns flowOf(emptyList())
        every { dataStore.hasLaunchedBefore } returns flowOf(true)
        viewModel = HomeViewModel(dao, dataStore, repository, contexts)
    }

    @Test
    fun `copy and clear update paste availability`() =
        runTest {
            viewModel.copiedProjectId.test {
                assertEquals(null, awaitItem())
                viewModel.copyProject(7L)
                assertEquals(7L, awaitItem())
                viewModel.clearCopiedProject()
                assertEquals(null, awaitItem())
            }
        }

    @Test
    fun `paste without a copied project does nothing`() =
        runTest {
            viewModel.pasteEvents.test {
                viewModel.pasteProject()
                expectNoEvents()
            }
        }

    @Test
    fun `paste emits successful duplicate result`() =
        runTest {
            coEvery { repository.duplicateProject(7L) } returns 8L
            viewModel.copyProject(7L)

            viewModel.pasteEvents.test {
                viewModel.pasteProject()
                advanceUntilIdle()
                assertEquals(8L, awaitItem().getOrThrow())
            }
        }

    @Test
    fun `paste emits duplication failure`() =
        runTest {
            coEvery { repository.duplicateProject(7L) } throws IllegalStateException("failed")
            viewModel.copyProject(7L)

            viewModel.pasteEvents.test {
                viewModel.pasteProject()
                advanceUntilIdle()
                assertEquals("failed", awaitItem().exceptionOrNull()?.message)
            }
        }

    @Test
    fun `import emits success`() =
        runTest {
            val uri = mockk<Uri>()
            coEvery { repository.importProject(uri) } returns 9L

            viewModel.importEvents.test {
                viewModel.importProject(uri)
                advanceUntilIdle()
                assertEquals(9L, awaitItem().getOrThrow())
            }
        }

    @Test
    fun `import toggles loading while repository is running`() =
        runTest {
            val uri = mockk<Uri>()
            val releaseImport = CompletableDeferred<Unit>()
            coEvery { repository.importProject(uri) } coAnswers {
                releaseImport.await()
                9L
            }

            viewModel.isImporting.test {
                assertFalse(awaitItem())
                viewModel.importProject(uri)
                runCurrent()
                assertTrue(awaitItem())
                releaseImport.complete(Unit)
                advanceUntilIdle()
                assertFalse(awaitItem())
            }
        }

    @Test
    fun `import emits failure`() =
        runTest {
            val uri = mockk<Uri>()
            coEvery { repository.importProject(uri) } throws IllegalArgumentException("invalid")

            viewModel.importEvents.test {
                viewModel.importProject(uri)
                advanceUntilIdle()
                assertEquals("invalid", awaitItem().exceptionOrNull()?.message)
            }
        }
}
