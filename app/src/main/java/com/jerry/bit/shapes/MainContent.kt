package com.jerry.bit.shapes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.unveilIn
import androidx.compose.animation.veilOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jerry.bit.shapes.navigation.Navigator
import com.jerry.bit.shapes.ui.common.FloatButtonProperties
import com.jerry.bit.shapes.ui.common.LocalFloatingActionBarButton
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun MainContent(onBackPressed: () -> Unit) {
    var fab by remember { mutableStateOf<FloatButtonProperties?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navigator = koinInject<Navigator>()
    navigator.Init()

    CompositionLocalProvider(
        LocalFloatingActionBarButton provides { fab = it },
    ) {
        Scaffold(
            modifier =
                Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentWindowInsets = WindowInsets(),
            floatingActionButton = {
                val derived by remember { derivedStateOf { fab != null } }
                val animateSize by animateFloatAsState(
                    targetValue = if (derived) 1F else 0F,
                    animationSpec = tween(durationMillis = 100),
                    label = "FAB Elevation",
                )
                FloatingActionButton(
                    modifier =
                        Modifier
                            .navigationBarsPadding()
                            .scale(animateSize),
                    onClick = fab?.onClick ?: {},
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_24),
                        contentDescription = null,
                    )
                }
            },
        ) { innerPadding ->
            NavDisplay(
                modifier =
                    Modifier
                        .padding(innerPadding),
                backStack = navigator.backStack,
                onBack = { navigator.popBackstack() },
                entryDecorators =
                    listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                entryProvider = koinEntryProvider<NavKey>(),
                transitionSpec = {
                    val enterTransition =
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(ANIM_DURATION),
                        )

                    val exitTransition =
                        slideOutHorizontally(
                            targetOffsetX = {
                                -(it * BACKGROUND_SHIFT_FACTOR).toInt()
                            },
                            animationSpec = tween(ANIM_DURATION),
                        ) +
                            veilOut(
                                animationSpec = tween(ANIM_DURATION),
                            )

                    enterTransition togetherWith exitTransition
                },
                popTransitionSpec = {
                    val enterTransition =
                        slideInHorizontally(
                            initialOffsetX = {
                                -(it * BACKGROUND_SHIFT_FACTOR).toInt()
                            },
                            animationSpec = tween(ANIM_DURATION),
                        ) +
                            unveilIn(
                                animationSpec = tween(ANIM_DURATION),
                            )

                    val exitTransition =
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(ANIM_DURATION),
                        )

                    enterTransition togetherWith exitTransition
                },
                predictivePopTransitionSpec = {
                    val enterTransition =
                        slideInHorizontally(
                            initialOffsetX = { fullWidth ->
                                (fullWidth * -PARALLAX_OFFSET_FACTOR).toInt()
                            },
                            animationSpec = tween(ANIM_DURATION),
                        ) + unveilIn(animationSpec = tween(ANIM_DURATION))

                    // Define the exit transition for the outgoing screen
                    val exitTransition =
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(ANIM_DURATION),
                        )

                    enterTransition togetherWith exitTransition
                },
            )
        }
    }
}

private const val ANIM_DURATION = 500
private const val PARALLAX_OFFSET_FACTOR = 0.25F
private const val BACKGROUND_SHIFT_FACTOR = 0.1F
