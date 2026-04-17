package com.jerry.shapes

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.jerry.shapes.ui.NavGraphs
import com.jerry.shapes.ui.appCurrentDestinationAsState
import com.jerry.shapes.ui.common.*
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine

@OptIn(
    ExperimentalLayoutApi::class
)
@Composable
fun MainContent(
    onBackPressed: () -> Unit
) {
    var fab by remember { mutableStateOf<FloatButtonProperties?>(null) }
    var title by remember { mutableStateOf<Pair<String?, Boolean>>(null to false) }
    var actions by remember { mutableStateOf<(@Composable RowScope.() -> Unit)?>(null) }

    // todo figure out how to fix the issue of the appbar scrolling itself
    val scrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior()
//        TopAppBarDefaults.enterAlwaysScrollBehavior(
//            state = rememberTopAppBarState(),
//            canScroll = { !title.second }
//        )

    val engine = rememberAnimatedNavHostEngine(
        rootDefaultAnimations = RootNavGraphDefaultAnimations(
            enterTransition = { fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIM_DURATION)) },
        )
    )
    val navController = engine.rememberNavController()

    val showToolbarAnimator = remember { Animatable(0F) }
    LaunchedEffect(navController.appCurrentDestinationAsState().value) {
        showToolbarAnimator.snapTo(scrollBehavior.state.heightOffset)
        showToolbarAnimator.animateTo(0F) {
            scrollBehavior.state.heightOffset = this.value
        }
    }

    CompositionLocalProvider(
        LocalAppBarTitle provides { title = it },
        LocalFloatingActionBarButton provides { fab = it },
        LocalAppBarActions provides { actions = it },
        LocalAppBarHeight provides rememberUpdatedState(scrollBehavior.state.heightOffset)
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                val showBackArrow by remember(navController.appCurrentDestinationAsState().value) {
                    derivedStateOf { navController.previousBackStackEntry != null }
                }
                Toolbar(
                    scrollBehavior = scrollBehavior,
                    showBackArrow = { showBackArrow },
                    onBack = onBackPressed,
                    getTitle = { title.first.orEmpty() },
                    actions = { actions ?: {} }
                )
            },
            floatingActionButton = {
                val derived by remember { derivedStateOf { fab != null } }
                AnimatedVisibility(
                    visible = derived,
                    enter = fadeIn() + expandIn { IntSize(width = 1, height = 1) }
                ) {
                    FloatingActionButton(
                        modifier = Modifier.systemBarsPadding(),
                        onClick = fab?.onClick ?: {}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_24),
                            contentDescription = null
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            DestinationsNavHost(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .systemBarsPadding(),
                engine = engine,
                navGraph = NavGraphs.root,
                navController = navController,
                startRoute = NavGraphs.root.startRoute
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
    actions: () -> @Composable RowScope.() -> Unit = { {} }
) {
    val topAppBarElementColor = MaterialTheme.colorScheme.onPrimary
    val appBarContainerColor = MaterialTheme.colorScheme.primary
    TopAppBar(
        actions = actions(),
        windowInsets = WindowInsets.statusBars.add(
            WindowInsets.navigationBars.only(
                WindowInsetsSides.Top + WindowInsetsSides.Horizontal
            )
        ),
        navigationIcon = {
            AnimatedVisibility(
                visible = showBackArrow(),
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .unboundClickable {
                            onBack()
                        }
                        .padding(8.dp),
                    painter = painterResource(R.drawable.ic_arrow_back_24),
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        title = {
            Text(
                modifier = Modifier.animateContentSize(),
                text = getTitle(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = appBarContainerColor,
            scrolledContainerColor = appBarContainerColor,
            navigationIconContentColor = topAppBarElementColor,
            titleContentColor = topAppBarElementColor,
            actionIconContentColor = topAppBarElementColor
        ),
        scrollBehavior = scrollBehavior
    )
}

private const val ANIM_DURATION = 300
