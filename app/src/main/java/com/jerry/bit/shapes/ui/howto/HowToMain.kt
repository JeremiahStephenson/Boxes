package com.jerry.bit.shapes.ui.howto

import android.graphics.RectF
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerry.bit.shapes.R
import com.jerry.bit.shapes.cache.data.ColorAndShape
import com.jerry.bit.shapes.ui.common.DefaultContainer
import com.jerry.bit.shapes.ui.shapes.Shape
import com.jerry.bit.shapes.ui.theme.BoxesTheme
import com.jerry.bit.shapes.util.drawCustomShape
import kotlinx.coroutines.launch

private data class GuideItem(
    @DrawableRes val icon: Int?,
    val title: String,
    val description: String,
)

private data class GuideDestination(
    val title: String,
    val listIndex: Int,
)

@Composable
fun HowToMain() {
    DefaultContainer(title = stringResource(R.string.how_to), showBackArrow = true) {
        val drawingItems =
            listOf(
                GuideItem(
                    R.drawable.ic_brush_24,
                    stringResource(R.string.guide_basic_drawing_1_label),
                    stringResource(R.string.guide_basic_drawing_1_desc),
                ),
                GuideItem(
                    R.drawable.ic_done_24,
                    stringResource(R.string.guide_basic_drawing_2_label),
                    stringResource(R.string.guide_basic_drawing_2_desc),
                ),
                GuideItem(
                    R.drawable.ic_zoom_out_map_24,
                    stringResource(R.string.guide_basic_drawing_3_label),
                    stringResource(R.string.guide_basic_drawing_3_desc),
                ),
            )
        val toolbarItems =
            listOf(
                GuideItem(
                    R.drawable.ic_color_lens_24,
                    stringResource(R.string.guide_tool_icon_bar_1_label),
                    stringResource(R.string.guide_tool_icon_bar_1_desc),
                ),
                GuideItem(
                    null,
                    stringResource(R.string.guide_tool_icon_bar_2_label),
                    stringResource(R.string.guide_tool_icon_bar_2_desc),
                ),
                GuideItem(
                    R.drawable.ic_brush_24,
                    stringResource(R.string.guide_tool_icon_bar_3_label),
                    stringResource(R.string.guide_tool_icon_bar_3_desc),
                ),
                GuideItem(
                    R.drawable.ic_undo_24,
                    stringResource(R.string.guide_tool_icon_bar_4_label),
                    stringResource(R.string.guide_tool_icon_bar_4_desc),
                ),
                GuideItem(
                    R.drawable.ic_zoom_out_map_24,
                    stringResource(R.string.guide_tool_icon_bar_5_label),
                    stringResource(R.string.guide_tool_icon_bar_5_desc),
                ),
            )
        val toolItems =
            listOf(
                GuideItem(
                    R.drawable.ic_brush_24,
                    stringResource(R.string.guide_drawer_menu_0_label),
                    stringResource(R.string.guide_drawer_menu_0_desc),
                ),
                GuideItem(
                    R.drawable.ic_colorize_24,
                    stringResource(R.string.guide_drawer_menu_2_label),
                    stringResource(R.string.guide_drawer_menu_2_desc),
                ),
                GuideItem(
                    R.drawable.ic_format_color_fill_24,
                    stringResource(R.string.guide_drawer_menu_1_label),
                    stringResource(R.string.guide_drawer_menu_1_desc),
                ),
                GuideItem(
                    R.drawable.ic_eraser_on_24,
                    stringResource(R.string.guide_drawer_menu_3_label),
                    stringResource(R.string.guide_drawer_menu_3_desc),
                ),
                GuideItem(
                    R.drawable.ic_select_all_24,
                    stringResource(R.string.guide_drawer_menu_4_label),
                    stringResource(R.string.guide_drawer_menu_4_desc),
                ),
            )
        val displayItems =
            listOf(
                GuideItem(
                    R.drawable.ic_grid_on_24,
                    stringResource(R.string.guide_drawer_menu_5_label),
                    stringResource(R.string.guide_drawer_menu_5_desc),
                ),
                GuideItem(
                    R.drawable.ic_opacity_on_24,
                    stringResource(R.string.guide_drawer_menu_6_label),
                    stringResource(R.string.guide_drawer_menu_6_desc),
                ),
            )
        val projectItems =
            listOf(
                GuideItem(
                    R.drawable.ic_edit_24,
                    stringResource(R.string.guide_drawer_menu_7_label),
                    stringResource(R.string.guide_drawer_menu_7_desc),
                ),
                GuideItem(
                    R.drawable.ic_save_24,
                    stringResource(R.string.guide_drawer_menu_8_label),
                    stringResource(R.string.guide_drawer_menu_8_desc),
                ),
                GuideItem(
                    R.drawable.ic_image_24,
                    stringResource(R.string.guide_drawer_menu_9_label),
                    stringResource(R.string.guide_drawer_menu_9_desc),
                ),
                GuideItem(
                    R.drawable.ic_share_24,
                    stringResource(R.string.guide_drawer_menu_10_label),
                    stringResource(R.string.guide_drawer_menu_10_desc),
                ),
                GuideItem(
                    R.drawable.ic_upload_file_24,
                    stringResource(R.string.guide_drawer_menu_11_label),
                    stringResource(R.string.guide_drawer_menu_11_desc),
                ),
                GuideItem(
                    R.drawable.ic_auto_renew,
                    stringResource(R.string.guide_drawer_menu_12_label),
                    stringResource(R.string.guide_drawer_menu_12_desc),
                ),
            )
        val drawerLayerItems =
            listOf(
                GuideItem(
                    R.drawable.ic_done_24,
                    stringResource(R.string.guide_layer_options_1_label),
                    stringResource(R.string.guide_layer_options_1_desc),
                ),
                GuideItem(
                    R.drawable.ic_add_24,
                    stringResource(R.string.guide_layer_options_3_label),
                    stringResource(R.string.guide_layer_options_3_desc),
                ),
                GuideItem(
                    R.drawable.ic_visibility_off_24,
                    stringResource(R.string.guide_layer_options_2_label),
                    stringResource(R.string.guide_layer_options_2_desc),
                ),
                GuideItem(
                    R.drawable.ic_edit_24,
                    stringResource(R.string.guide_layer_edit_1_label),
                    stringResource(R.string.guide_layer_edit_1_desc),
                ),
            )
        val layerEditItems =
            listOf(
                GuideItem(
                    R.drawable.ic_opacity_on_24,
                    stringResource(R.string.guide_layer_edit_2_label),
                    stringResource(R.string.guide_layer_edit_2_desc),
                ),
                GuideItem(
                    R.drawable.ic_drag_indicator_24,
                    stringResource(R.string.guide_layer_edit_3_label),
                    stringResource(R.string.guide_layer_edit_3_desc),
                ),
                GuideItem(
                    R.drawable.ic_edit_24,
                    stringResource(R.string.guide_layer_edit_4_label),
                    stringResource(R.string.guide_layer_edit_4_desc),
                ),
                GuideItem(
                    R.drawable.ic_delete_24,
                    stringResource(R.string.guide_layer_edit_5_label),
                    stringResource(R.string.guide_layer_edit_5_desc),
                ),
            )

        val destinations =
            listOf(
                GuideDestination(stringResource(R.string.guide_basic_drawing), 2),
                GuideDestination(stringResource(R.string.guide_tool_icon_bar), 3),
                GuideDestination(stringResource(R.string.guide_drawer_tools), 4),
                GuideDestination(stringResource(R.string.guide_canvas_display), 5),
                GuideDestination(stringResource(R.string.guide_project_actions), 6),
                GuideDestination(stringResource(R.string.guide_layers), 7),
                GuideDestination(stringResource(R.string.guide_layer_edit_screen), 8),
            )
        val gridState = rememberLazyStaggeredGridState()
        val coroutineScope = rememberCoroutineScope()

        LazyVerticalStaggeredGrid(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            columns = StaggeredGridCells.Adaptive(minSize = 360.dp),
            contentPadding =
                WindowInsets.navigationBars
                    .asPaddingValues()
                    .plus(PaddingValues(16.dp)),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            state = gridState,
        ) {
            item(span = StaggeredGridItemSpan.FullLine) { GuideIntroduction() }
            item(span = StaggeredGridItemSpan.FullLine) {
                GuideTableOfContents(destinations) { destination ->
                    coroutineScope.launch {
                        gridState.animateScrollToItem(destination.listIndex)
                    }
                }
            }
            item {
                GuideSection(
                    stringResource(R.string.guide_basic_drawing),
                    stringResource(R.string.guide_basic_drawing_desc),
                    drawingItems,
                )
            }
            item {
                GuideSection(
                    stringResource(R.string.guide_tool_icon_bar),
                    stringResource(R.string.guide_tool_icon_bar_desc),
                    toolbarItems,
                )
            }
            item {
                GuideSection(
                    stringResource(R.string.guide_drawer_tools),
                    stringResource(R.string.guide_drawer_tools_desc),
                    toolItems,
                )
            }
            item {
                GuideSection(
                    stringResource(R.string.guide_canvas_display),
                    stringResource(R.string.guide_canvas_display_desc),
                    displayItems,
                )
            }
            item {
                GuideSection(
                    stringResource(R.string.guide_project_actions),
                    stringResource(R.string.guide_project_actions_desc),
                    projectItems,
                )
            }
            item {
                GuideSection(
                    stringResource(R.string.guide_layers),
                    stringResource(R.string.guide_layers_desc),
                    drawerLayerItems,
                )
            }
            item {
                GuideSection(
                    stringResource(R.string.guide_layer_edit_screen),
                    stringResource(R.string.guide_layer_edit_screen_desc),
                    layerEditItems,
                )
            }
            item(span = StaggeredGridItemSpan.FullLine) { GuideTip() }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GuideTableOfContents(
    destinations: List<GuideDestination>,
    onDestinationClick: (GuideDestination) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.guide_on_this_page),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            destinations.forEach { destination ->
                AssistChip(
                    onClick = { onDestinationClick(destination) },
                    label = { Text(destination.title) },
                )
            }
        }
    }
}

@Composable
private fun GuideIntroduction() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                stringResource(R.string.guide_welcome_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                stringResource(R.string.guide_welcome_desc),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun GuideSection(
    title: String,
    supportingText: String,
    items: List<GuideItem>,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
    ) {
        Column(modifier = Modifier.padding(vertical = 20.dp)) {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier.padding(start = 20.dp, top = 4.dp, end = 20.dp, bottom = 12.dp),
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier =
                            Modifier.padding(
                                start = 76.dp,
                                end = 20.dp,
                            ),
                    )
                }
                GuideRow(item)
            }
        }
    }
}

@Composable
private fun GuideRow(item: GuideItem) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            item.icon?.let { icon ->
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier =
                        Modifier
                            .size(22.dp)
                            .wrapContentSize(align = Alignment.Center),
                )
            } ?: ShapePreview()
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ShapePreview() {
    val color = MaterialTheme.colorScheme.onPrimaryContainer
    Box(
        modifier =
            Modifier
                .size(22.dp)
                .padding(8.dp)
                .drawWithContent({
                    drawCustomShape(
                        pos = RectF(0F, 0F, size.width, size.height),
                        color = ColorAndShape(color.value, Shape.Star),
                    )
                }),
    )
}

@Composable
private fun GuideTip() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            painterResource(R.drawable.ic_brush_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Column {
            Text(
                stringResource(R.string.guide_tip_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(R.string.guide_tip_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GuideRowPreview() {
    BoxesTheme {
        GuideRow(
            item =
                GuideItem(
                    icon = null,
                    title = "Basic Drawing",
                    description = "Tap and drag on canvas to create freeform lines and shapes.",
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GuideSectionPreview() {
    BoxesTheme {
        GuideSection(
            title = "Basic Drawing",
            supportingText = "Learn how to draw and edit shapes.",
            items =
                listOf(
                    GuideItem(
                        icon = R.drawable.ic_brush_24,
                        title = "Basic Drawing",
                        description = "Tap and drag on canvas to create freeform lines and shapes.",
                    ),
                ),
        )
    }
}
