package com.jerry.bit.shapes.ui.howto

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jerry.bit.shapes.R
import com.jerry.bit.shapes.navigation.Navigator
import com.jerry.bit.shapes.ui.common.DefaultContainer

@Composable
fun HowToMain(
    navigator: Navigator,
) {
    DefaultContainer(
        title = stringResource(R.string.how_to),
        showBackArrow = true,
    ) {
    }
}
