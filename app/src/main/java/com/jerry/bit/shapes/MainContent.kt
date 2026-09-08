package com.jerry.bit.shapes

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.unveilIn
import androidx.compose.animation.veilOut
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.minus
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jerry.bit.shapes.navigation.Navigator
import com.jerry.bit.shapes.ui.boxes.BoxesMain
import com.jerry.bit.shapes.ui.boxes.BoxesNavKey
import com.jerry.bit.shapes.ui.common.FloatButtonProperties
import com.jerry.bit.shapes.ui.common.LocalFloatingActionBarButton
import com.jerry.bit.shapes.ui.common.unboundClickable
import com.jerry.bit.shapes.ui.create.CreateMain
import com.jerry.bit.shapes.ui.create.CreateNavKey
import com.jerry.bit.shapes.ui.home.HomeMain
import com.jerry.bit.shapes.ui.home.HomeNavKey
import com.jerry.bit.shapes.ui.howto.HowToMain
import com.jerry.bit.shapes.ui.howto.HowToNavKey
import com.jerry.bit.shapes.ui.layers.LayersEditMain
import com.jerry.bit.shapes.ui.layers.LayersEditNavKey
import org.koin.compose.koinInject

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
                            .graphicsLayer {
                                scaleX = animateSize
                                scaleY = animateSize
                            },
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
                entryProvider = { key ->
                    when (key) {
                        is HomeNavKey ->
                            NavEntry(key) {
                                HomeMain(navigator = navigator)
                            }

                        is BoxesNavKey ->
                            NavEntry(key) {
                                BoxesMain(
                                    projectId = key.projectId,
                                    projectName = key.projectName,
                                    navigator = navigator,
                                )
                            }

                        is LayersEditNavKey ->
                            NavEntry(key) {
                                LayersEditMain(
                                    projectId = key.projectId,
                                    navigator = navigator,
                                )
                            }

                        is CreateNavKey ->
                            NavEntry(key) {
                                CreateMain(
                                    projectId = key.projectId,
                                    navigator = navigator,
                                )
                            }

                        is HowToNavKey ->
                            NavEntry(key) {
                                HowToMain(navigator = navigator)
                            }

                        else -> error("Unknown route: $key")
                    }
                },
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

@Composable
fun Toolbar(
    scrollBehavior: TopAppBarScrollBehavior,
    showBackArrow: Boolean,
    getTitle: () -> String,
    actions: () -> @Composable RowScope.() -> Unit = { {} },
) {
    val navigator = koinInject<Navigator>()
    val topAppBarElementColor = MaterialTheme.colorScheme.onPrimary
    val appBarContainerColor = MaterialTheme.colorScheme.primary
    TopAppBar(
        actions = actions(),
        windowInsets =
            WindowInsets.statusBars.add(
                WindowInsets.navigationBars.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
                ),
            ),
        navigationIcon = {
            if (showBackArrow) {
                Icon(
                    modifier =
                        Modifier
                            .padding(8.dp)
                            .unboundClickable {
                                navigator.popBackstack()
                            }.padding(8.dp),
                    painter = painterResource(R.drawable.ic_arrow_back_24),
                    contentDescription = stringResource(R.string.back),
                )
            }
        },
        title = {
            Text(
                modifier = Modifier.animateContentSize(),
                text = getTitle(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = appBarContainerColor,
                scrolledContainerColor = appBarContainerColor,
                navigationIconContentColor = topAppBarElementColor,
                titleContentColor = topAppBarElementColor,
                actionIconContentColor = topAppBarElementColor,
            ),
        scrollBehavior = scrollBehavior,
    )
}

private const val ANIM_DURATION = 500
private const val PARALLAX_OFFSET_FACTOR = 0.25F
private const val BACKGROUND_SHIFT_FACTOR = 0.1F
private const val FAB_ANIM_DURATION = 200
private const val FAB_HIDDEN_SCALE = 0.4F
