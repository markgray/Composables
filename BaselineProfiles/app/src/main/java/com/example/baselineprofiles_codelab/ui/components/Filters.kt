/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.baselineprofiles_codelab.ui.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.Filter
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * A horizontal bar displaying a list of filter chips and a "Filters" [IconButton].
 *
 * This composable displays a row of [FilterChip] components, each representing a
 * filter that can be applied. It also includes a button that, when clicked,
 * triggers the [onShowFilters] callback to display a filter selection UI that shows
 * a list of additional available filters.
 *
 * Our root composable is a [LazyRow] whose arguments are:
 *  - `verticalAlignment`: [Alignment.CenterVertically], which aligns the items vertically.
 *  - `horizontalArrangement`: [Arrangement.spacedBy], which creates a space of 8.dp between the items.
 *  - `contentPadding`: [PaddingValues], which adds 12.dp of padding to the start and end of the content.
 *  - `modifier`: [Modifier.heightIn], which sets the height of the row to be at least 56.dp.
 *
 * @param filters The list of [Filter] objects to display as chips.
 * @param onShowFilters Callback function triggered when the "Filters" [IconButton] is clicked.
 * This callback opens a filter selection UI.
 */
@Composable
fun FilterBar(
    filters: List<Filter>,
    onShowFilters: () -> Unit
) {

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
        modifier = Modifier.heightIn(min = 56.dp)
    ) {
        item {
            IconButton(onClick = onShowFilters) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    tint = JetsnackTheme.colors.brand,
                    contentDescription = stringResource(id = R.string.label_filters),
                    modifier = Modifier.diagonalGradientBorder(
                        colors = JetsnackTheme.colors.interactiveSecondary,
                        shape = CircleShape
                    )
                )
            }
        }
        items(items = filters) { filter: Filter ->
            FilterChip(filter = filter, shape = MaterialTheme.shapes.small)
        }
    }
}

@Composable
fun FilterChip(
    filter: Filter,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small
) {
    val (selected: Boolean, setSelected: (Boolean) -> Unit) = filter.enabled
    val backgroundColor: Color by animateColorAsState(
        if (selected) JetsnackTheme.colors.brandSecondary else JetsnackTheme.colors.uiBackground
    )
    val border: Modifier = Modifier.fadeInDiagonalGradientBorder(
        showBorder = !selected,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = shape
    )
    val textColor: Color by animateColorAsState(
        if (selected) Color.Black else JetsnackTheme.colors.textSecondary
    )

    JetsnackSurface(
        modifier = modifier.height(height = 28.dp),
        color = backgroundColor,
        contentColor = textColor,
        shape = shape,
        elevation = 2.dp
    ) {
        val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

        val pressed: Boolean by interactionSource.collectIsPressedAsState()
        @Suppress("RedundantValueArgument")
        val backgroundPressed: Modifier =
            if (pressed) {
                Modifier.offsetGradientBackground(
                    colors = JetsnackTheme.colors.interactiveSecondary,
                    width = 200f,
                    offset = 0f
                )
            } else {
                Modifier.background(color = Color.Transparent)
            }
        Box(
            modifier = Modifier
                .toggleable(
                    value = selected,
                    onValueChange = setSelected,
                    interactionSource = interactionSource,
                    indication = null
                )
                .then(other = backgroundPressed)
                .then(other = border),
        ) {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 6.dp
                )
            )
        }
    }
}

/**
 * Three previews of a disabled [FilterChip] with different configurations.
 */
@Suppress("RedundantValueArgument")
@Preview(name = "default")
@Preview(name = "dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "large font", fontScale = 2f)
@Composable
private fun FilterDisabledPreview() {
    JetsnackTheme {
        FilterChip(Filter(name = "Demo", enabled = false), Modifier.padding(4.dp))
    }
}

/**
 * Three previews of an enabled [FilterChip] with different configurations.
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun FilterEnabledPreview() {
    JetsnackTheme {
        FilterChip(Filter(name = "Demo", enabled = true))
    }
}
