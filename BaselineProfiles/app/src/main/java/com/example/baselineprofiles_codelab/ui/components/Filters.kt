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
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
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
 *  - `horizontalArrangement`: [Arrangement.spacedBy], which creates a space of 8.dp between
 *  the items.
 *  - `contentPadding`: [PaddingValues], which adds 12.dp of padding to the start and end of
 *  the content.
 *  - `modifier`: [Modifier.heightIn], which sets the height of the row to be at least 56.dp.
 *
 * In the [LazyListScope] `content` composable lambda argument we first compose a [LazyListScope.item]
 * in whose [LazyItemScope] `content` composable lambda argument we compose an [IconButton] whose
 * `onClick` argument is our [onShowFilters] lambda parameter. In the `content` composable lambda
 * argument of the [IconButton] we compose an [Icon] whose arguments are:
 *  - `imageVector`: [Icons.Rounded.FilterList], which is an icon representing the additional filter
 *  selection UI.
 *  - `tint`: the [Color] of the icon. We use [JetsnackColors.brand] of our custom
 *  [JetsnackTheme.colors].
 *  - `contentDescription`: a string resource describing the icon, is the [String] with resource ID
 *  `R.string.label_filters` ("Filters").
 *  - `modifier`: a [Modifier.diagonalGradientBorder], which draws a diagonal gradient border using
 *  the [JetsnackColors.interactiveSecondary] color as its `colors` argument, and [CircleShape] as
 *  its `shape` argument.
 *
 * Below that we compose a [LazyListScope.items] composable whose `items` argument is our
 * [List] of [Filter] parameter [filters]. In the [LazyItemScope] `itemContent` composable lambda
 * argument we accept each [Filter] passed the lambda in the variable `filter` and compose a
 * [FilterChip] whose `filter` argument is the `filter` variable, and whose `shape` argument is the
 * [Shapes.small] of our custom [MaterialTheme.shapes].
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

/**
 * A customizable filter chip composable.
 *
 * This composable displays a chip that represents a filter, allowing the user to toggle
 * its enabled/disabled state. The chip's appearance (background color, text color, border)
 * changes based on whether it's selected or not.
 *
 * We start by using destructuring to initialize a [Boolean] variable `selected` and our lambda
 * taking a [Boolean] variable `setSelected` from the [Filter.enabled] property of our [Filter]
 * parameter [filter] (it is a [MutableState] wrapped [Boolean]). We initialize our animated [State]
 * wrapped [Color] variable `backgroundColor` to be [JetsnackColors.brandSecondary] if `selected` is
 * `true`, or [JetsnackColors.uiBackground] if `selected` is `false` (to do this we use the
 * [animateColorAsState] composable method to animate the color change).
 *
 * We then initialize our [Modifier] variable `border` to be a [Modifier.fadeInDiagonalGradientBorder]
 * whose `showBorder` argument is the opposite of `selected`, `colors` argument is the
 * [JetsnackColors.interactiveSecondary] of our custom [JetsnackTheme.colors], and `shape` argument
 * is our [Shape] parameter [shape].
 *
 * We then initialize our [State] wrapped [Color] variable `textColor` to be [Color.Black] if
 * `selected` is `true`, or [JetsnackColors.textSecondary] if `selected` is `false` (to do this
 * we use the [animateColorAsState] composable method to animate the color change).
 *
 * Then our root composable is a [JetsnackSurface] whose arguments are:
 *  - `modifier`: chained to our [Modifier] parameter [modifier] is a [Modifier.height], which sets
 *  the `height` of the surface to 28.dp.
 *  - `color`: our animated [State] wrapped [Color] variable `backgroundColor` (which is animated
 *  between [JetsnackColors.brandSecondary] and [JetsnackColors.uiBackground] based on the value
 *  of `selected`).
 *  - `contentColor`: our animated [State] wrapped [Color] variable `textColor` (which is animated
 *  between [Color.Black] and [JetsnackColors.textSecondary] based on the value of `selected`).
 *  - `shape`: our [Shape] parameter [shape].
 *  - `elevation`: 2.dp.
 *
 * In the `content` composable lambda argument of the [JetsnackSurface] we initialize and remember
 * our [MutableInteractionSource] variable `interactionSource` to a new instance, and initialize our
 * [State] wrapped [Boolean] variable `pressed` using the [InteractionSource.collectIsPressedAsState]
 * method of `interactionSource`. Next we initialize our [Modifier] variable `backgroundPressed` to
 * be a [Modifier.offsetGradientBackground] whose `colors` argument is the
 * [JetsnackColors.interactiveSecondary] of our custom [JetsnackTheme.colors], `width` argument is
 * 200.dp, and `offset` argument is 0.dp if `pressed` is `true`, or to a [Modifier.background] whose
 * `color` argument is [Color.Transparent] if `pressed` is `false`. Then we compose a [Box] whose
 * `modifier` argument is a [Modifier.toggleable] whose `value` argument is `selected`, `onValueChange`
 * argument is `setSelected`, whose `interactionSource` argument is `interactionSource`, and whose
 * `indication` argument is `null`, and chained to that using [Modifier.then] is the `backgroundPressed`
 * [Modifier] variable, followed by our `border` [Modifier] variable.
 *
 * In the [BoxScope] `content` composable lambda argument of the [Box] we compose a [Text] whose
 * arguments are:
 *  - `text`: the [Filter.name] property of our [Filter] parameter [filter].
 *  - `style`: the [Typography.caption] textstyle of our custom [MaterialTheme.typography].
 *  - `maxLines`: 1.
 *  - `modifier`: a [Modifier.padding] whose `horizontal` argument is 20.dp, and whose `vertical`
 *  argument is 6.dp.
 *
 * @param filter The [Filter] object representing the filter's data and state.
 * @param modifier Modifier to be applied to the filter chip.
 * @param shape The shape of the filter chip. Defaults to the [Shapes.small] of our custom
 * [MaterialTheme.shapes].
 */
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
