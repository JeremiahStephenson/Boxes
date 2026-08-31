package com.jerry.bit.shapes.util

import com.jerry.bit.shapes.cache.BoxesDao
import com.jerry.bit.shapes.cache.data.Layer
import com.jerry.bit.shapes.cache.data.Pixel
import com.jerry.bit.shapes.cache.data.Project
import com.jerry.bit.shapes.ui.shapes.Shape

class ProjectSeeder(
    private val boxesDao: BoxesDao,
) {
    suspend fun seedProjects() {
        val now = System.currentTimeMillis()

        // Cactus
        val cactusId =
            boxesDao.insertProject(
                Project(
                    name = "Desert Cactus",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFF4CAF50.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now,
                ),
            )
        val cactusLayer = boxesDao.insertLayer(Layer(projectId = cactusId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createCactusPixels(cactusLayer))

        // Kitty
        val kittyId =
            boxesDao.insertProject(
                Project(
                    name = "Pixel Kitty",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFFFFFFF.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 1000,
                ),
            )
        val kittyLayer = boxesDao.insertLayer(Layer(projectId = kittyId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createKittyPixels(kittyLayer))

        // House
        val houseId =
            boxesDao.insertProject(
                Project(
                    name = "Cozy Home",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFFFEB3B.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 2000,
                ),
            )
        val houseLayer = boxesDao.insertLayer(Layer(projectId = houseId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createHousePixels(houseLayer))

        // Sprite
        val spriteId =
            boxesDao.insertProject(
                Project(
                    name = "Adventurer",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFF2196F3.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 3000,
                ),
            )
        val spriteLayer = boxesDao.insertLayer(Layer(projectId = spriteId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createSpritePixels(spriteLayer))

        // 1. Duck
        val duckId =
            boxesDao.insertProject(
                Project(
                    name = "Rubber Duck",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFFFEB3B.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 4000,
                ),
            )
        val duckLayer = boxesDao.insertLayer(Layer(projectId = duckId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createDuckPixels(duckLayer))

        // 2. Mushroom
        val mushroomId =
            boxesDao.insertProject(
                Project(
                    name = "Magic Mushroom",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFF44336.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 5000,
                ),
            )
        val mushroomLayer = boxesDao.insertLayer(Layer(projectId = mushroomId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createMushroomPixels(mushroomLayer))

        // 3. Wizard
        val wizardId =
            boxesDao.insertProject(
                Project(
                    name = "Wise Wizard",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFF3F51B5.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 6000,
                ),
            )
        val wizardLayer = boxesDao.insertLayer(Layer(projectId = wizardId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createWizardPixels(wizardLayer))

        // 4. Castle
        val castleId =
            boxesDao.insertProject(
                Project(
                    name = "Pixel Castle",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFF9E9E9E.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 7000,
                ),
            )
        val castleLayer = boxesDao.insertLayer(Layer(projectId = castleId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createCastlePixels(castleLayer))

        // 5. Sword
        val swordId =
            boxesDao.insertProject(
                Project(
                    name = "Iron Sword",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFE0E0E0.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 8000,
                ),
            )
        val swordLayer = boxesDao.insertLayer(Layer(projectId = swordId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createSwordPixels(swordLayer))

        // 6. Heart
        val heartId =
            boxesDao.insertProject(
                Project(
                    name = "Pixel Heart",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFD32F2F.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 9000,
                ),
            )
        val heartLayer = boxesDao.insertLayer(Layer(projectId = heartId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createHeartPixels(heartLayer))

        // 7. Frog
        val frogId =
            boxesDao.insertProject(
                Project(
                    name = "Leaping Frog",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFF4CAF50.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 10000,
                ),
            )
        val frogLayer = boxesDao.insertLayer(Layer(projectId = frogId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createFrogPixels(frogLayer))

        // 8. Rose
        val roseId =
            boxesDao.insertProject(
                Project(
                    name = "Blooming Rose",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFE91E63.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 11000,
                ),
            )
        val roseLayer = boxesDao.insertLayer(Layer(projectId = roseId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createRosePixels(roseLayer))

        // 9. Fox
        val foxId =
            boxesDao.insertProject(
                Project(
                    name = "Clever Fox",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFFFFF5722.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 12000,
                ),
            )
        val foxLayer = boxesDao.insertLayer(Layer(projectId = foxId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createFoxPixels(foxLayer))

        // 10. Tree
        val treeId =
            boxesDao.insertProject(
                Project(
                    name = "Oak Tree",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFF795548.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 13000,
                ),
            )
        val treeLayer = boxesDao.insertLayer(Layer(projectId = treeId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createTreePixels(treeLayer))

        // 11. Panda
        val pandaId =
            boxesDao.insertProject(
                Project(
                    name = "Sleepy Panda",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFFFFFFF.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 14000,
                ),
            )
        val pandaLayer = boxesDao.insertLayer(Layer(projectId = pandaId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createPandaPixels(pandaLayer))

        // 12. Star
        val starId =
            boxesDao.insertProject(
                Project(
                    name = "Golden Star",
                    columns = 16,
                    rows = 16,
                    currentColor = 0xFFFFD600.toInt(),
                    currentShape = Shape.Box,
                    showGrid = true,
                    showPngBg = true,
                    timestamp = now + 15000,
                ),
            )
        val starLayer = boxesDao.insertLayer(Layer(projectId = starId, index = 0, name = "Layer 1", on = true))
        boxesDao.insertAllPixels(createStarPixels(starLayer))
    }

    private fun createCactusPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val green = 0xFF4CAF50.toInt()
        val darkGreen = 0xFF2E7D32.toInt()
        val brown = 0xFF795548.toInt()
        val darkBrown = 0xFF5D4037.toInt()
        val pink = 0xFFE91E63.toInt()
        val now = System.currentTimeMillis()

        for (x in 5..10) for (y in 13..15) pixels.add(pixel(layerId, x, y, brown, now))
        for (x in 4..11) pixels.add(pixel(layerId, x, 12, darkBrown, now))
        for (x in 7..9) for (y in 4..11) pixels.add(pixel(layerId, x, y, green, now))
        pixels.add(pixel(layerId, 8, 3, green, now))
        for (y in 7..9) pixels.add(pixel(layerId, 5, y, green, now))
        pixels.add(pixel(layerId, 6, 9, green, now))
        for (y in 6..8) pixels.add(pixel(layerId, 11, y, green, now))
        pixels.add(pixel(layerId, 10, 8, green, now))
        pixels.add(pixel(layerId, 7, 5, darkGreen, now))
        pixels.add(pixel(layerId, 9, 7, darkGreen, now))
        pixels.add(pixel(layerId, 8, 9, darkGreen, now))
        pixels.add(pixel(layerId, 7, 11, darkGreen, now))
        pixels.add(pixel(layerId, 11, 7, darkGreen, now))
        pixels.add(pixel(layerId, 5, 8, darkGreen, now))
        pixels.add(pixel(layerId, 8, 2, pink, now))

        return pixels
    }

    private fun createKittyPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val white = 0xFFFFFFFF.toInt()
        val gray = 0xFFBDBDBD.toInt()
        val black = 0xFF212121.toInt()
        val pink = 0xFFF48FB1.toInt()
        val now = System.currentTimeMillis()

        for (x in 4..11) for (y in 4..9) pixels.add(pixel(layerId, x, y, white, now))
        pixels.add(pixel(layerId, 4, 3, white, now))
        pixels.add(pixel(layerId, 5, 3, white, now))
        pixels.add(pixel(layerId, 10, 3, white, now))
        pixels.add(pixel(layerId, 11, 3, white, now))
        pixels.add(pixel(layerId, 4, 4, pink, now))
        pixels.add(pixel(layerId, 11, 4, pink, now))
        pixels.add(pixel(layerId, 6, 6, black, now))
        pixels.add(pixel(layerId, 9, 6, black, now))
        pixels.add(pixel(layerId, 7, 7, pink, now))
        pixels.add(pixel(layerId, 8, 7, pink, now))
        pixels.add(pixel(layerId, 7, 8, gray, now))
        pixels.add(pixel(layerId, 8, 8, gray, now))
        for (x in 5..10) for (y in 10..14) pixels.add(pixel(layerId, x, y, white, now))
        pixels.add(pixel(layerId, 5, 14, gray, now))
        pixels.add(pixel(layerId, 10, 14, gray, now))
        pixels.add(pixel(layerId, 6, 15, gray, now))
        pixels.add(pixel(layerId, 9, 15, gray, now))
        pixels.add(pixel(layerId, 11, 13, white, now))
        pixels.add(pixel(layerId, 12, 12, white, now))
        pixels.add(pixel(layerId, 13, 11, white, now))
        pixels.add(pixel(layerId, 13, 10, white, now))

        return pixels
    }

    private fun createHousePixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val red = 0xFFC62828.toInt()
        val darkRed = 0xFF8E0000.toInt()
        val yellow = 0xFFFFF176.toInt()
        val darkYellow = 0xFFFBC02D.toInt()
        val brown = 0xFF4E342E.toInt()
        val lightBlue = 0xFF81D4FA.toInt()
        val black = 0xFF212121.toInt()
        val white = 0xFFFFFFFF.toInt()
        val now = System.currentTimeMillis()

        for (i in 0..6) {
            val y = 1 + i
            for (x in (8 - i)..(8 + i)) {
                pixels.add(pixel(layerId, x, y, if (x == 8 - i || x == 8 + i) darkRed else red, now))
            }
        }
        pixels.add(pixel(layerId, 10, 1, brown, now))
        pixels.add(pixel(layerId, 10, 2, brown, now))
        for (x in 3..13) for (y in 8..14) pixels.add(pixel(layerId, x, y, yellow, now))
        for (y in 8..14) pixels.add(pixel(layerId, 3, y, darkYellow, now))
        for (x in 3..13) pixels.add(pixel(layerId, x, 14, darkYellow, now))
        for (x in 7..9) for (y in 11..14) pixels.add(pixel(layerId, x, y, brown, now))
        pixels.add(pixel(layerId, 9, 13, black, now))
        for (x in 4..5) for (y in 9..10) pixels.add(pixel(layerId, x, y, lightBlue, now))
        for (x in 11..12) for (y in 9..10) pixels.add(pixel(layerId, x, y, lightBlue, now))
        pixels.add(pixel(layerId, 4, 9, white, now))

        return pixels
    }

    private fun createSpritePixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val skin = 0xFFFFCCBC.toInt()
        val hair = 0xFF5D4037.toInt()
        val blue = 0xFF1976D2.toInt()
        val darkBlue = 0xFF0D47A1.toInt()
        val green = 0xFF388E3C.toInt()
        val black = 0xFF000000.toInt()
        val now = System.currentTimeMillis()

        for (x in 6..10) for (y in 3..4) pixels.add(pixel(layerId, x, y, hair, now))
        pixels.add(pixel(layerId, 5, 4, hair, now))
        pixels.add(pixel(layerId, 11, 4, hair, now))
        pixels.add(pixel(layerId, 5, 5, hair, now))
        pixels.add(pixel(layerId, 11, 5, hair, now))
        for (x in 6..10) for (y in 5..7) pixels.add(pixel(layerId, x, y, skin, now))
        pixels.add(pixel(layerId, 7, 6, black, now))
        pixels.add(pixel(layerId, 9, 6, black, now))
        for (x in 5..11) for (y in 8..11) pixels.add(pixel(layerId, x, y, blue, now))
        pixels.add(pixel(layerId, 8, 8, darkBlue, now))
        for (y in 8..10) pixels.add(pixel(layerId, 4, y, skin, now))
        for (y in 8..10) pixels.add(pixel(layerId, 12, y, skin, now))
        for (x in 6..7) for (y in 12..14) pixels.add(pixel(layerId, x, y, green, now))
        for (x in 9..10) for (y in 12..14) pixels.add(pixel(layerId, x, y, green, now))
        pixels.add(pixel(layerId, 6, 15, black, now))
        pixels.add(pixel(layerId, 7, 15, black, now))
        pixels.add(pixel(layerId, 9, 15, black, now))
        pixels.add(pixel(layerId, 10, 15, black, now))

        return pixels
    }

    private fun createDuckPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val yellow = 0xFFFFEB3B.toInt()
        val orange = 0xFFFF9800.toInt()
        val black = 0xFF000000.toInt()
        val white = 0xFFFFFFFF.toInt()
        val now = System.currentTimeMillis()

        for (x in 4..11) for (y in 8..12) pixels.add(pixel(layerId, x, y, yellow, now))
        for (x in 5..10) pixels.add(pixel(layerId, x, 7, yellow, now))
        for (x in 5..10) pixels.add(pixel(layerId, x, 13, yellow, now))
        for (x in 9..13) for (y in 3..6) pixels.add(pixel(layerId, x, y, yellow, now))
        pixels.add(pixel(layerId, 14, 5, orange, now))
        pixels.add(pixel(layerId, 14, 6, orange, now))
        pixels.add(pixel(layerId, 12, 4, black, now))
        pixels.add(pixel(layerId, 6, 10, white, now))
        pixels.add(pixel(layerId, 7, 10, white, now))

        return pixels
    }

    private fun createMushroomPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val red = 0xFFF44336.toInt()
        val white = 0xFFFFFFFF.toInt()
        val stem = 0xFFEEEEEE.toInt()
        val shadow = 0xFFBDBDBD.toInt()
        val now = System.currentTimeMillis()

        for (x in 3..12) for (y in 4..8) pixels.add(pixel(layerId, x, y, red, now))
        for (x in 4..11) pixels.add(pixel(layerId, x, 3, red, now))
        pixels.add(pixel(layerId, 5, 4, white, now))
        pixels.add(pixel(layerId, 10, 5, white, now))
        pixels.add(pixel(layerId, 7, 6, white, now))
        pixels.add(pixel(layerId, 4, 7, white, now))
        pixels.add(pixel(layerId, 11, 7, white, now))
        for (x in 6..9) for (y in 9..14) pixels.add(pixel(layerId, x, y, stem, now))
        for (y in 9..14) pixels.add(pixel(layerId, 9, y, shadow, now))

        return pixels
    }

    private fun createWizardPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val blue = 0xFF3F51B5.toInt()
        val face = 0xFFFFCCBC.toInt()
        val beard = 0xFFCFD8DC.toInt()
        val gold = 0xFFFFD600.toInt()
        val black = 0xFF000000.toInt()
        val now = System.currentTimeMillis()

        pixels.add(pixel(layerId, 8, 1, blue, now))
        for (x in 7..9) pixels.add(pixel(layerId, x, 2, blue, now))
        for (x in 7..9) pixels.add(pixel(layerId, x, 3, blue, now))
        for (x in 6..10) pixels.add(pixel(layerId, x, 4, blue, now))
        for (x in 5..11) pixels.add(pixel(layerId, x, 5, blue, now))
        for (x in 7..9) for (y in 6..7) pixels.add(pixel(layerId, x, y, face, now))
        pixels.add(pixel(layerId, 7, 6, black, now))
        pixels.add(pixel(layerId, 9, 6, black, now))
        for (x in 7..9) for (y in 8..10) pixels.add(pixel(layerId, x, y, beard, now))
        pixels.add(pixel(layerId, 8, 11, beard, now))
        for (x in 5..11) for (y in 11..15) pixels.add(pixel(layerId, x, y, blue, now))
        for (y in 11..15) pixels.add(pixel(layerId, 8, y, gold, now))

        return pixels
    }

    private fun createCastlePixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val gray = 0xFF9E9E9E.toInt()
        val darkGray = 0xFF616161.toInt()
        val blue = 0xFF2196F3.toInt()
        val brown = 0xFF795548.toInt()
        val black = 0xFF212121.toInt()
        val now = System.currentTimeMillis()

        for (x in 3..12) for (y in 8..14) pixels.add(pixel(layerId, x, y, gray, now))
        for (x in 2..4) for (y in 5..14) pixels.add(pixel(layerId, x, y, gray, now))
        for (x in 11..13) for (y in 5..14) pixels.add(pixel(layerId, x, y, gray, now))
        for (x in listOf(2, 4, 6, 8, 10, 12)) pixels.add(pixel(layerId, x, 4, gray, now))
        pixels.add(pixel(layerId, 3, 2, blue, now))
        pixels.add(pixel(layerId, 3, 3, blue, now))
        pixels.add(pixel(layerId, 12, 2, blue, now))
        pixels.add(pixel(layerId, 12, 3, blue, now))
        for (x in 7..8) for (y in 11..14) pixels.add(pixel(layerId, x, y, brown, now))
        pixels.add(pixel(layerId, 7, 10, black, now))
        pixels.add(pixel(layerId, 8, 10, black, now))
        for (y in 5..14) pixels.add(pixel(layerId, 2, y, darkGray, now))
        for (y in 8..14) pixels.add(pixel(layerId, 5, y, darkGray, now))

        return pixels
    }

    private fun createSwordPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val silver = 0xFFE0E0E0.toInt()
        val gray = 0xFF9E9E9E.toInt()
        val brown = 0xFF5D4037.toInt()
        val gold = 0xFFFFD600.toInt()
        val now = System.currentTimeMillis()

        for (i in 0..8) {
            pixels.add(pixel(layerId, 4 + i, 11 - i, silver, now))
            pixels.add(pixel(layerId, 5 + i, 10 - i, gray, now))
        }
        pixels.add(pixel(layerId, 13, 2, silver, now))
        pixels.add(pixel(layerId, 3, 10, gold, now))
        pixels.add(pixel(layerId, 4, 11, gold, now))
        pixels.add(pixel(layerId, 5, 12, gold, now))
        pixels.add(pixel(layerId, 3, 12, brown, now))
        pixels.add(pixel(layerId, 2, 13, brown, now))
        pixels.add(pixel(layerId, 1, 14, gold, now))

        return pixels
    }

    private fun createHeartPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val red = 0xFFD32F2F.toInt()
        val darkRed = 0xFFB71C1C.toInt()
        val lightRed = 0xFFFF5252.toInt()
        val now = System.currentTimeMillis()

        for (x in 4..11) {
            for (y in 4..12) {
                if (y == 4 && (x == 4 || x == 7 || x == 8 || x == 11)) continue
                if (y == 12 && (x != 7 && x != 8)) continue
                if (y == 11 && (x < 6 || x > 9)) continue
                if (y == 10 && (x < 5 || x > 10)) continue
                pixels.add(pixel(layerId, x, y, red, now))
            }
        }
        pixels.add(pixel(layerId, 11, 5, darkRed, now))
        pixels.add(pixel(layerId, 11, 6, darkRed, now))
        pixels.add(pixel(layerId, 10, 10, darkRed, now))
        pixels.add(pixel(layerId, 9, 11, darkRed, now))
        pixels.add(pixel(layerId, 8, 12, darkRed, now))
        pixels.add(pixel(layerId, 5, 5, lightRed, now))
        pixels.add(pixel(layerId, 6, 5, lightRed, now))
        pixels.add(pixel(layerId, 5, 6, lightRed, now))

        return pixels
    }

    private fun createFrogPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val green = 0xFF4CAF50.toInt()
        val lightGreen = 0xFF8BC34A.toInt()
        val white = 0xFFFFFFFF.toInt()
        val black = 0xFF000000.toInt()
        val now = System.currentTimeMillis()

        for (x in 4..11) for (y in 7..13) pixels.add(pixel(layerId, x, y, green, now))
        for (x in 6..9) for (y in 10..13) pixels.add(pixel(layerId, x, y, lightGreen, now))
        for (x in listOf(4, 10)) for (y in 5..6) pixels.add(pixel(layerId, x, y, green, now))
        pixels.add(pixel(layerId, 5, 5, white, now))
        pixels.add(pixel(layerId, 5, 6, white, now))
        pixels.add(pixel(layerId, 11, 5, white, now))
        pixels.add(pixel(layerId, 11, 6, white, now))
        pixels.add(pixel(layerId, 5, 6, black, now))
        pixels.add(pixel(layerId, 11, 6, black, now))
        pixels.add(pixel(layerId, 3, 12, green, now))
        pixels.add(pixel(layerId, 3, 13, green, now))
        pixels.add(pixel(layerId, 12, 12, green, now))
        pixels.add(pixel(layerId, 12, 13, green, now))

        return pixels
    }

    private fun createRosePixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val rose = 0xFFE91E63.toInt()
        val darkRose = 0xFFC2185B.toInt()
        val green = 0xFF4CAF50.toInt()
        val darkGreen = 0xFF2E7D32.toInt()
        val now = System.currentTimeMillis()

        for (x in 6..9) for (y in 4..7) pixels.add(pixel(layerId, x, y, rose, now))
        pixels.add(pixel(layerId, 7, 5, darkRose, now))
        pixels.add(pixel(layerId, 8, 6, darkRose, now))
        pixels.add(pixel(layerId, 5, 5, rose, now))
        pixels.add(pixel(layerId, 10, 5, rose, now))
        for (y in 8..15) pixels.add(pixel(layerId, 8, y, darkGreen, now))
        pixels.add(pixel(layerId, 7, 10, green, now))
        pixels.add(pixel(layerId, 6, 10, green, now))
        pixels.add(pixel(layerId, 9, 12, green, now))
        pixels.add(pixel(layerId, 10, 12, green, now))

        return pixels
    }

    private fun createFoxPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val orange = 0xFFFF5722.toInt()
        val white = 0xFFFFFFFF.toInt()
        val black = 0xFF212121.toInt()
        val now = System.currentTimeMillis()

        for (x in 5..10) for (y in 6..10) pixels.add(pixel(layerId, x, y, orange, now))
        for (x in 6..9) pixels.add(pixel(layerId, x, 11, white, now))
        pixels.add(pixel(layerId, 5, 4, black, now))
        pixels.add(pixel(layerId, 5, 5, orange, now))
        pixels.add(pixel(layerId, 10, 4, black, now))
        pixels.add(pixel(layerId, 10, 5, orange, now))
        pixels.add(pixel(layerId, 6, 8, black, now))
        pixels.add(pixel(layerId, 9, 8, black, now))
        pixels.add(pixel(layerId, 7, 10, black, now))
        pixels.add(pixel(layerId, 8, 10, black, now))
        for (x in 6..9) for (y in 12..14) pixels.add(pixel(layerId, x, y, orange, now))
        for (x in 7..8) pixels.add(pixel(layerId, x, 12, white, now))
        pixels.add(pixel(layerId, 10, 13, orange, now))
        pixels.add(pixel(layerId, 11, 12, orange, now))
        pixels.add(pixel(layerId, 12, 11, white, now))

        return pixels
    }

    private fun createTreePixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val brown = 0xFF795548.toInt()
        val green = 0xFF4CAF50.toInt()
        val darkGreen = 0xFF1B5E20.toInt()
        val now = System.currentTimeMillis()

        for (x in 4..11) for (y in 2..8) pixels.add(pixel(layerId, x, y, green, now))
        for (x in 5..10) pixels.add(pixel(layerId, x, 1, green, now))
        for (x in 7..8) for (y in 2..8) pixels.add(pixel(layerId, x, y, darkGreen, now))
        for (x in 7..8) for (y in 9..15) pixels.add(pixel(layerId, x, y, brown, now))
        pixels.add(pixel(layerId, 6, 15, brown, now))
        pixels.add(pixel(layerId, 9, 15, brown, now))

        return pixels
    }

    private fun createPandaPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val white = 0xFFFFFFFF.toInt()
        val black = 0xFF212121.toInt()
        val now = System.currentTimeMillis()

        for (x in 4..11) for (y in 4..9) pixels.add(pixel(layerId, x, y, white, now))
        pixels.add(pixel(layerId, 4, 3, black, now))
        pixels.add(pixel(layerId, 5, 3, black, now))
        pixels.add(pixel(layerId, 10, 3, black, now))
        pixels.add(pixel(layerId, 11, 3, black, now))
        pixels.add(pixel(layerId, 5, 6, black, now))
        pixels.add(pixel(layerId, 6, 6, black, now))
        pixels.add(pixel(layerId, 9, 6, black, now))
        pixels.add(pixel(layerId, 10, 6, black, now))
        pixels.add(pixel(layerId, 6, 6, white, now))
        pixels.add(pixel(layerId, 9, 6, white, now))
        pixels.add(pixel(layerId, 7, 8, black, now))
        pixels.add(pixel(layerId, 8, 8, black, now))
        for (x in 5..10) for (y in 10..14) pixels.add(pixel(layerId, x, y, white, now))
        for (x in 5..10) pixels.add(pixel(layerId, x, 10, black, now))
        pixels.add(pixel(layerId, 4, 11, black, now))
        pixels.add(pixel(layerId, 11, 11, black, now))
        pixels.add(pixel(layerId, 5, 15, black, now))
        pixels.add(pixel(layerId, 6, 15, black, now))
        pixels.add(pixel(layerId, 9, 15, black, now))
        pixels.add(pixel(layerId, 10, 15, black, now))

        return pixels
    }

    private fun createStarPixels(layerId: Long): List<Pixel> {
        val pixels = mutableListOf<Pixel>()
        val yellow = 0xFFFFD600.toInt()
        val gold = 0xFFFFC107.toInt()
        val now = System.currentTimeMillis()

        for (x in 7..8) for (y in 7..8) pixels.add(pixel(layerId, x, y, yellow, now))
        for (y in 3..6) {
            pixels.add(pixel(layerId, 7, y, yellow, now))
            pixels.add(pixel(layerId, 8, y, yellow, now))
        }
        for (y in 9..12) {
            pixels.add(pixel(layerId, 7, y, yellow, now))
            pixels.add(pixel(layerId, 8, y, yellow, now))
        }
        for (x in 3..6) {
            pixels.add(pixel(layerId, x, 7, yellow, now))
            pixels.add(pixel(layerId, x, 8, yellow, now))
        }
        for (x in 9..12) {
            pixels.add(pixel(layerId, x, 7, yellow, now))
            pixels.add(pixel(layerId, x, 8, yellow, now))
        }
        pixels.add(pixel(layerId, 6, 6, yellow, now))
        pixels.add(pixel(layerId, 9, 6, yellow, now))
        pixels.add(pixel(layerId, 6, 9, yellow, now))
        pixels.add(pixel(layerId, 9, 9, yellow, now))
        pixels.add(pixel(layerId, 8, 4, gold, now))
        pixels.add(pixel(layerId, 8, 11, gold, now))
        pixels.add(pixel(layerId, 11, 8, gold, now))
        pixels.add(pixel(layerId, 9, 9, gold, now))

        return pixels
    }

    private fun pixel(
        layerId: Long,
        x: Int,
        y: Int,
        color: Int,
        timestamp: Long,
    ): Pixel =
        Pixel(
            layerId = layerId,
            x = x,
            y = y,
            color = color,
            shape = Shape.Box,
            timestamp = timestamp,
        )
}
