package com.jerry.shapes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.unveilIn
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.minus
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jerry.shapes.navigation.Navigator
import com.jerry.shapes.ui.boxes.BoxesMain
import com.jerry.shapes.ui.boxes.BoxesNavKey
import com.jerry.shapes.ui.common.FloatButtonProperties
import com.jerry.shapes.ui.common.LocalAppBarActions
import com.jerry.shapes.ui.common.LocalAppBarHeight
import com.jerry.shapes.ui.common.LocalAppBarTitle
import com.jerry.shapes.ui.common.LocalFloatingActionBarButton
import com.jerry.shapes.ui.common.unboundClickable
import com.jerry.shapes.ui.create.CreateMain
import com.jerry.shapes.ui.create.CreateNavKey
import com.jerry.shapes.ui.home.HomeMain
import com.jerry.shapes.ui.home.HomeNavKey
import com.jerry.shapes.ui.layers.LayersEditMain
import com.jerry.shapes.ui.layers.LayersEditNavKey
import org.koin.compose.koinInject

@Composable
fun MainContent(onBackPressed: () -> Unit) {
    var fab by remember { mutableStateOf<FloatButtonProperties?>(null) }
    var title by remember { mutableStateOf<Pair<String?, Boolean>>(null to false) }
    var actions by remember { mutableStateOf<(@Composable RowScope.() -> Unit)?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navigator = koinInject<Navigator>()
    navigator.Init()

    CompositionLocalProvider(
        LocalAppBarTitle provides { title = it },
        LocalFloatingActionBarButton provides { fab = it },
        LocalAppBarActions provides { actions = it },
        LocalAppBarHeight provides rememberUpdatedState(scrollBehavior.state.heightOffset),
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                val showBackArrow by remember {
                    derivedStateOf { !navigator.isAtRoot }
                }
                Toolbar(
                    scrollBehavior = scrollBehavior,
                    showBackArrow = { showBackArrow },
                    onBack = onBackPressed,
                    getTitle = { title.first.orEmpty() },
                    actions = { actions ?: {} },
                )
            },
            floatingActionButton = {
                val derived by remember { derivedStateOf { fab != null } }
                AnimatedVisibility(
                    visible = derived,
                    enter = fadeIn() + expandIn { IntSize(width = 1, height = 1) },
                ) {
                    FloatingActionButton(
                        onClick = fab?.onClick ?: {},
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_24),
                            contentDescription = null,
                        )
                    }
                }
            },
        ) { innerPadding ->
            NavDisplay(
                modifier =
                    Modifier
                        .padding(
                            innerPadding.minus(
                                WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal).asPaddingValues(),
                            ),
                        ),
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

                        else -> error("Unknown route: $key")
                    }
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
    showBackArrow: () -> Boolean,
    onBack: () -> Unit,
    getTitle: () -> String,
    actions: () -> @Composable RowScope.() -> Unit = { {} },
) {
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
            AnimatedVisibility(
                visible = showBackArrow(),
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut(),
            ) {
                Icon(
                    modifier =
                        Modifier
                            .padding(8.dp)
                            .unboundClickable {
                                onBack()
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

private const val ANIM_DURATION = 700
private const val PARALLAX_OFFSET_FACTOR = 0.25F
