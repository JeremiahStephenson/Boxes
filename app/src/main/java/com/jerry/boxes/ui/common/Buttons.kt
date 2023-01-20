package com.jerry.boxes.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jerry.boxes.ui.boxes.ColorAndShape
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconMenuButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    color: ColorAndShape? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    contentDescription: String,
    allowTooltip: Boolean = true,
    @DrawableRes drawableRes: Int
) {
    val builder = rememberBalloonBuilder {
        setArrowPosition(0.5F)
        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setPadding(12)
        setCornerRadius(8F)
        setBalloonAnimation(BalloonAnimation.ELASTIC)
    }
    Balloon(
        modifier = Modifier,
        builder = builder,
        balloonContent = {
            Text(text = contentDescription)
        }
    ) { balloonWindow ->
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .run {
                    when (enabled) {
                        true -> combinedClickable(
                            onClick = { onClick() },
                            onLongClick = {
                                if (allowTooltip) {
                                    balloonWindow.showAlignTop()
                                }
                            }
                        )
                        else -> this
                    }
                }
                .padding(padding)
                .then(modifier),
            painter = painterResource(drawableRes),
            tint = (color?.color ?: LocalContentColor.current).run {
                copy(
                    alpha = when (enabled) {
                        true -> this.alpha
                        else -> 0.3F
                    }
                )
            },
            contentDescription = contentDescription
        )
    }
}

@Composable
fun IconSelectableMenuButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isSelected: () -> Boolean,
    isEnabled: () -> Boolean = { true },
    contentDescription: String,
    @DrawableRes drawableResOn: Int,
    @DrawableRes drawableResOff: Int? = null
) {
    val builder = rememberBalloonBuilder {
        setArrowSize(10)
        setArrowPosition(0.5F)
        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setPadding(12)
        setCornerRadius(8F)
        setBalloonAnimation(BalloonAnimation.ELASTIC)
    }
    Balloon(
        modifier = Modifier,
        builder = builder,
        balloonContent = {
            Text(text = contentDescription)
        }
    ) { balloonWindow ->
        Icon(
            modifier = modifier
                .run {
                    when (isEnabled()) {
                        true -> unboundClickable(
                            onClick = { onClick() },
                            onLongClick = {
                                balloonWindow.showAlignTop()
                            }
                        )
                        else -> this
                    }
                }
                .padding(4.dp)
                .clip(CircleShape)
                .run {
                    when (drawableResOff) {
                        null -> when (isSelected()) {
                            true -> background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4F))
                            else -> this
                        }
                        else -> this
                    }
                }
                .padding(12.dp),
            painter = when (drawableResOff) {
                null -> painterResource(drawableResOn)
                else -> painterResource(
                    when (isSelected()) {
                        true -> drawableResOff
                        else -> drawableResOn
                    }
                )
            },
            contentDescription = contentDescription,
            tint = when (isEnabled()) {
                true -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4F)
            }
        )
    }
}
