package com.jerry.bit.shapes.ui.howto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jerry.bit.shapes.R
import com.jerry.bit.shapes.navigation.Navigator
import com.jerry.bit.shapes.ui.common.DefaultContainer

@Composable
fun HowToMain(navigator: Navigator) {
    DefaultContainer(
        title = stringResource(R.string.how_to),
        showBackArrow = true,
    ) {
        val basicDrawingItems = listOf(
            Triple(R.drawable.ic_colorize_24, stringResource(R.string.guide_basic_drawing_1_label), stringResource(R.string.guide_basic_drawing_1_desc)),
            Triple(R.drawable.ic_colorize_24, stringResource(R.string.guide_basic_drawing_2_label), stringResource(R.string.guide_basic_drawing_2_desc)),
            Triple(R.drawable.ic_zoom_out_map_24, stringResource(R.string.guide_basic_drawing_3_label), stringResource(R.string.guide_basic_drawing_3_desc)),
        )
        val toolIconBarItems = listOf(
            Triple(R.drawable.ic_colorize_24, stringResource(R.string.guide_tool_icon_bar_1_label), stringResource(R.string.guide_tool_icon_bar_1_desc)),
            Triple(R.drawable.ic_edit_24, stringResource(R.string.guide_tool_icon_bar_2_label), stringResource(R.string.guide_tool_icon_bar_2_desc)),
            Triple(R.drawable.ic_select_all_24, stringResource(R.string.guide_tool_icon_bar_3_label), stringResource(R.string.guide_tool_icon_bar_3_desc)),
            Triple(R.drawable.ic_auto_renew, stringResource(R.string.guide_tool_icon_bar_4_label), stringResource(R.string.guide_tool_icon_bar_4_desc)),
            Triple(R.drawable.ic_zoom_out_map_24, stringResource(R.string.guide_tool_icon_bar_5_label), stringResource(R.string.guide_tool_icon_bar_5_desc)),
        )
        val drawerMenuOptionsItems = listOf(
            Triple(R.drawable.ic_format_color_fill_24, stringResource(R.string.guide_drawer_menu_1_label), stringResource(R.string.guide_drawer_menu_1_desc)),
            Triple(R.drawable.ic_colorize_24, stringResource(R.string.guide_drawer_menu_2_label), stringResource(R.string.guide_drawer_menu_2_desc)),
            Triple(R.drawable.ic_eraser_on_24, stringResource(R.string.guide_drawer_menu_3_label), stringResource(R.string.guide_drawer_menu_3_desc)),
            Triple(R.drawable.ic_select_all_24, stringResource(R.string.guide_drawer_menu_4_label), stringResource(R.string.guide_drawer_menu_4_desc)),
            Triple(R.drawable.ic_grid_on_24, stringResource(R.string.guide_drawer_menu_5_label), stringResource(R.string.guide_drawer_menu_5_desc)),
            Triple(R.drawable.ic_opacity_on_24, stringResource(R.string.guide_drawer_menu_6_label), stringResource(R.string.guide_drawer_menu_6_desc)),
            Triple(R.drawable.ic_edit_24, stringResource(R.string.guide_drawer_menu_7_label), stringResource(R.string.guide_drawer_menu_7_desc)),
            Triple(R.drawable.ic_save_24, stringResource(R.string.guide_drawer_menu_8_label), stringResource(R.string.guide_drawer_menu_8_desc)),
            Triple(R.drawable.ic_image_24, stringResource(R.string.guide_drawer_menu_9_label), stringResource(R.string.guide_drawer_menu_9_desc)),
            Triple(R.drawable.ic_share_24, stringResource(R.string.guide_drawer_menu_10_label), stringResource(R.string.guide_drawer_menu_10_desc)),
            Triple(R.drawable.ic_upload_file_24, stringResource(R.string.guide_drawer_menu_11_label), stringResource(R.string.guide_drawer_menu_11_desc)),
            Triple(R.drawable.ic_auto_renew, stringResource(R.string.guide_drawer_menu_12_label), stringResource(R.string.guide_drawer_menu_12_desc)),
        )
        val layerOptionsItems = listOf(
            Triple(R.drawable.ic_edit_24, stringResource(R.string.guide_layer_options_1_label), stringResource(R.string.guide_layer_options_1_desc)),
            Triple(R.drawable.ic_visibility_off_24, stringResource(R.string.guide_layer_options_2_label), stringResource(R.string.guide_layer_options_2_desc)),
            Triple(R.drawable.ic_layers_24, stringResource(R.string.guide_layer_options_3_label), stringResource(R.string.guide_layer_options_3_desc)),
        )
        val layerEditScreenItems = listOf(
            Triple(R.drawable.ic_edit_24, stringResource(R.string.guide_layer_edit_1_label), stringResource(R.string.guide_layer_edit_1_desc)),
            Triple(R.drawable.ic_layers_24, stringResource(R.string.guide_layer_edit_2_label), stringResource(R.string.guide_layer_edit_2_desc)),
            Triple(R.drawable.ic_arrow_upward_24, stringResource(R.string.guide_layer_edit_3_label), stringResource(R.string.guide_layer_edit_3_desc)),
            Triple(R.drawable.ic_edit_24, stringResource(R.string.guide_layer_edit_4_label), stringResource(R.string.guide_layer_edit_4_desc)),
            Triple(R.drawable.ic_eraser_on_24, stringResource(R.string.guide_layer_edit_5_label), stringResource(R.string.guide_layer_edit_5_desc)),
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = WindowInsets.navigationBars.asPaddingValues().plus(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GuideSection(
                    title = stringResource(R.string.guide_basic_drawing),
                    items = basicDrawingItems,
                )
            }
            item {
                GuideSection(
                    title = stringResource(R.string.guide_tool_icon_bar),
                    items = toolIconBarItems,
                )
            }
            item {
                GuideSection(
                    title = stringResource(R.string.guide_drawer_menu_options),
                    items = drawerMenuOptionsItems,
                )
            }
            item {
                GuideSection(
                    title = stringResource(R.string.guide_layer_options_draw_order),
                    items = layerOptionsItems,
                )
            }
            item {
                GuideSection(
                    title = stringResource(R.string.guide_layer_edit_screen),
                    items = layerEditScreenItems,
                )
            }
        }
    }
}

@Composable
private fun GuideSection(
    title: String,
    items: List<Triple<Int, String, String>>,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items.forEach { (iconRes, label, description) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "$label:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1F),
                        )
                    }
                }
            }
        }
    }
}
