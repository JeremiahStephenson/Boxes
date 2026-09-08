package com.jerry.bit.shapes.inject

import com.jerry.bit.shapes.navigation.Navigator
import com.jerry.bit.shapes.ui.boxes.BoxesMain
import com.jerry.bit.shapes.ui.boxes.BoxesNavKey
import com.jerry.bit.shapes.ui.create.CreateMain
import com.jerry.bit.shapes.ui.create.CreateNavKey
import com.jerry.bit.shapes.ui.home.HomeMain
import com.jerry.bit.shapes.ui.home.HomeNavKey
import com.jerry.bit.shapes.ui.howto.HowToMain
import com.jerry.bit.shapes.ui.howto.HowToNavKey
import com.jerry.bit.shapes.ui.layers.LayersEditMain
import com.jerry.bit.shapes.ui.layers.LayersEditNavKey
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule =
    module {
        navigation<HomeNavKey> { _ ->
            val navigator = koinInject<Navigator>()
            HomeMain(navigator = navigator)
        }

        navigation<BoxesNavKey> { key ->
            val navigator = koinInject<Navigator>()
            BoxesMain(
                projectId = key.projectId,
                projectName = key.projectName,
                navigator = navigator,
            )
        }

        navigation<LayersEditNavKey> { key ->
            val navigator = koinInject<Navigator>()
            LayersEditMain(
                projectId = key.projectId,
                navigator = navigator,
            )
        }

        navigation<CreateNavKey> { key ->
            val navigator = koinInject<Navigator>()
            CreateMain(
                projectId = key.projectId,
                navigator = navigator,
            )
        }

        navigation<HowToNavKey> { _ ->
            val navigator = koinInject<Navigator>()
            HowToMain(navigator = navigator)
        }
    }
