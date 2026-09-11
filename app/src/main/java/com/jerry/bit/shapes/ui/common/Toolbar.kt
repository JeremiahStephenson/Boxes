package com.jerry.bit.shapes.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jerry.bit.shapes.R
import com.jerry.bit.shapes.navigation.Navigator
import org.koin.compose.koinInject

@Composable
fun Toolbar(
    scrollBehavior: TopAppBarScrollBehavior,
    showBackArrow: Boolean,
    getTitle: () -> String,
    actions: () -> @Composable RowScope.() -> Unit = { {} },
) {
    val navigator = koinInject<Navigator>()
    val topAppBarElementColor = MaterialTheme.colorScheme.onPrimaryContainer
    val appBarContainerColor = MaterialTheme.colorScheme.primaryContainer
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
