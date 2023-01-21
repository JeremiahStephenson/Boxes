package com.jerry.boxes.ui.common

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.jerry.boxes.R

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProjectImage(
    imageRequest: ImageRequest,
    modifier: Modifier = Modifier,
    crossFade: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(),
    contentScale: ContentScale = ContentScale.Crop,
    loadingContent: @Composable () -> Unit = { ProjectImageLoading() },
    errorContent: @Composable () -> Unit = { ProjectImageError() }
) {
    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = null,
        modifier = modifier
            .padding(contentPadding),
        contentScale = contentScale
    ) {
        val state = painter.state
        val transition = updateTransition(state, label = imageRequest.data.toString())
        val tween: TweenSpec<Float> = when {
            crossFade &&
                (transition.currentState is AsyncImagePainter.State.Loading && transition.targetState is AsyncImagePainter.State.Success) ||
                (transition.currentState is AsyncImagePainter.State.Loading && transition.targetState is AsyncImagePainter.State.Error) -> {
                tween()
            }
            else -> tween(0)
        }
        Crossfade(
            targetState = state,
            animationSpec = tween
        ) { painterState ->
            when (painterState) {
                is AsyncImagePainter.State.Loading -> loadingContent()
                is AsyncImagePainter.State.Error -> errorContent()
                else -> SubcomposeAsyncImageContent(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ProjectImageLoading(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    color: Color = MaterialTheme.colorScheme.secondary,
    size: Dp = 36.dp
) {
    NonLoadedContainer(
        modifier = modifier,
        backgroundColor = backgroundColor
    ) {
        CircularProgressIndicator(
            color = color,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun ProjectImageError(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    color: Color = MaterialTheme.colorScheme.secondary
) {
    NonLoadedContainer(
        modifier = modifier,
        backgroundColor = backgroundColor
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_warning_24),
            tint = color,
            contentDescription = stringResource(R.string.error)
        )
    }
}

@Composable
private fun NonLoadedContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProjectImage(
    imagePath: String?,
    modifier: Modifier = Modifier,
    memoryKey: String? = null,
    crossFade: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(),
    contentScale: ContentScale = ContentScale.Crop,
    loadingContent: @Composable () -> Unit = { ProjectImageLoading() },
    errorContent: @Composable () -> Unit = { ProjectImageError() }
) {
    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .diskCacheKey(memoryKey)
        .data(imagePath)
        .crossfade(true)
        .build()

    ProjectImage(
        imageRequest = request,
        modifier = modifier,
        crossFade = crossFade,
        contentPadding = contentPadding,
        contentScale = contentScale,
        loadingContent = loadingContent,
        errorContent = errorContent
    )
}
