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

package com.example.baselineprofiles_codelab.ui.home.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.model.SearchCategory
import com.example.baselineprofiles_codelab.model.SearchCategoryCollection
import com.example.baselineprofiles_codelab.ui.components.SnackImage
import com.example.baselineprofiles_codelab.ui.components.VerticalGrid
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import kotlin.math.max

/**
 * Displays a list of search category collections in a scrollable vertical list.
 *
 * This composable uses a [LazyColumn] to efficiently display a potentially large list of
 * [SearchCategoryCollection] items. Each collection is rendered using a [SearchCategoryCollection]
 * composable. After the [LazyColumn] a [Spacer] of `8dp` height is added.
 *
 * @param categories A list of [SearchCategoryCollection] objects to be displayed. Each object
 * represents a collection of search categories.
 *
 * @see SearchCategoryCollection
 * @see LazyColumn
 * @see itemsIndexed
 */
@Composable
fun SearchCategories(
    categories: List<SearchCategoryCollection>
) {
    LazyColumn {
        itemsIndexed(items = categories) { index, collection ->
            SearchCategoryCollection(collection = collection, index = index)
        }
    }
    Spacer(modifier = Modifier.height(height = 8.dp))
}

/**
 * Displays a collection of search categories within a vertical grid.
 *
 * This composable function renders a header with the collection name,
 * followed by a grid of [SearchCategory] items. Each collection alternates
 * between two predefined gradients for visual distinction.
 *
 * Our root Composable is a [Column] whose `modifier` argument is our [Modifier] parameter `modifier`.
 * In its [ColumnScope] `content` composable lambda argument we compose:
 *
 * **First** a [Text] whose arguments are:
 *  - `text`: is the collection name -- [SearchCategoryCollection.name]
 *  - `style`: [TextStyle] is the [Typography.h6] of our custom [MaterialTheme.typography]
 *  - `color`: is the [JetsnackColors.textPrimary] of our custom [JetsnackTheme.colors].
 *  - `modifier`: is a [Modifier.heightIn] of 56.dp, with a [Modifier.padding] of 16.dp
 *  chained to it, and a [Modifier.wrapContentHeight] chained to that.
 *
 * **Second** a [VerticalGrid] whose `modifier` argument is a [Modifier.padding] that adds 16.dp
 * to each horizontal side of the content. In its `content` composable lambda argument we first
 * initialize our [List] of [Color] variable `gradient` with the appropriate gradient based on
 * whether our [Int] parameter [index] is even or odd, if it is even we use the
 * [JetsnackColors.gradient2_2] of our custom [JetsnackTheme.colors], if it is odd we use
 * [JetsnackColors.gradient2_3]. Then we use the [Iterable.forEach] method of the
 * [SearchCategoryCollection.categories] of our [SearchCategoryCollection] parameter [collection]
 * to iterate through the [SearchCategory] items capturing the current [SearchCategory] in our
 * variable `category` and composing a [SearchCategory] whose argruments are:
 *  - `category`: is the current [SearchCategory] in variable `category`
 *  - `gradient`: is the [List] of [Color] variable `gradient`
 *  - `modifier`: is a [Modifier.padding] that adds 8.dp to all sides of the content.
 *
 * **Third** a [Spacer] of `4.dp` height is added at the bottom of the [Column].
 *
 * @param collection The [SearchCategoryCollection] containing the name and a list of
 * [SearchCategory] items.
 * @param index The index of the collection within a potential list of collections.
 * Used to determine the gradient.
 * @param modifier [Modifier] to be applied to the root Column layout.
 */
@Composable
private fun SearchCategoryCollection(
    collection: SearchCategoryCollection,
    index: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = collection.name,
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .wrapContentHeight()
        )
        VerticalGrid(modifier = Modifier.padding(horizontal = 16.dp)) {
            val gradient: List<Color> = when (index % 2) {
                0 -> JetsnackTheme.colors.gradient2_2
                else -> JetsnackTheme.colors.gradient2_3
            }
            collection.categories.forEach { category: SearchCategory ->
                SearchCategory(
                    category = category,
                    gradient = gradient,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(height = 4.dp))
    }
}

/**
 * Minimum height for a [SnackImage] in our [SearchCategory] composable is `134.dp`.
 */
private val MinImageSize = 134.dp

/**
 * Shape of our [SearchCategory] composable is a [RoundedCornerShape] whose `size` is `10.dp`
 */
private val CategoryShape = RoundedCornerShape(size = 10.dp)

/**
 * Proportion of width used to determine the width of the [Text] in our [SearchCategory] composable
 * us `0.55f`
 */
private const val CategoryTextProportion = 0.55f

/**
 * Displays a single search category. Our root Composable is a [Layout] whose `modifier` argument
 * chains to our [Modifier] parameter [modifier] a [Modifier.aspectRatio] whose `ratio` argument is
 * 1.45f, with a [Modifier.shadow] chained to that whose `elevation` argument is `3.dp` and whose
 * `shape` argument is [CategoryShape], with a [Modifier.clip] chained to that whose `shape` argument
 * is [CategoryShape], with a [Modifier.background] chained to that whose `brush` argument is a
 *  [Brush.horizontalGradient] whose `colors` argument is our [List] of [Color] parameter [gradient],
 *  with a [Modifier.clickable] chained to that whose `onClick` lambda argument is a do-nothing lambda.
 *  In its `content` Composable lambda argument we have:
 *   - a [Text] whose `text` argument is the [SearchCategory.name] of our [SearchCategory] parameter
 *   [category], whose [TextStyle] `style` argument is the [Typography.subtitle1] of our custom
 *   [MaterialTheme.typography], whose [Color] `color` argument is the [JetsnackColors.textSecondary]
 *   of our custom [JetsnackTheme.colors], and whose [Modifier] `modifier` argument is a
 *   [Modifier.padding] that adds 4.dp to all sides, with a [Modifier.padding] that adds 8.dp to the
 *   `start` chained to that.
 *   - a [SnackImage] whose `imageRes` argument is the resource ID [SearchCategory.imageUrl] of our
 *   [SearchCategory] parameter [category], whose `content` description argument is `null`, and whose
 *   `modifier` argument is a [Modifier.fillMaxSize] that allows it to measure at its desired size.
 *
 * The arguments passed to the [MeasureScope] lambda argument of the [Layout] Composable are a
 * [List] of [Measurable] passed in the variable `measurables` and a [Constraints] passed in the
 * variable `constraints`. In that lambda we initialize our [Int] variable `val textWidth` to the
 * `maxWidth` of our [Constraints] times [CategoryTextProportion]. We initialize our [Placeable]
 * variable `val textPlaceable` by using the [Measurable.measure] method of the 0 index [Measurable]
 * in the `measurables` list to measure the [Text] using our [Int] variable `textWidth` as the
 * [Constraints.fixedWidth] of the [Placeable]. We initialize our [Int] variable `imageSize` to the
 * [max] of [MinImageSize] and the [Constraints.maxHeight] of the [Constraints] variable `constraints`.
 * We initialize our [Placeable] variable `val imagePlaceable` by using the [Measurable.measure]
 * method of the index `1` [Measurable] in the [List] of [Measurable] variable `measurables` to
 * measure the [SnackImage] using our [Int] variable `imageSize` as the [Constraints.fixed] size of
 * the [Placeable] produced. We then call the [MeasureScope.layout] method with its `width` argument
 * set to the [Constraints.maxWidth] of the [Constraints] variable `constraints`, and its `height`
 * argument set to the [Constraints.minHeight] of the [Constraints] variable `constraints`. In the
 * `placementBlock` lambda argument we call:
 *  - the [Placeable.PlacementScope.placeRelative] extension method of [Placeable] variable
 *  `textPlaceable` with its `x` argument set to 0, and its `y` argument set to the
 *  [Constraints.maxHeight] of the [Constraints] variable `constraints` minus the [Placeable.height]
 *  of the [Placeable] variable `textPlaceable` all divided by 2 (to center it vertically).
 *  - the [Placeable.PlacementScope.placeRelative] extension method of [Placeable] variable
 *  `imagePlaceable` with its `x` argument our [Int] variable `textWidth`, and its `y` argument set
 *  to the [Constraints.maxHeight] minus the [Placeable.height] of `imagePlaceable` all divided by 2
 *  (to center it vertically).
 *
 * @param category The [SearchCategory] to display.
 * @param gradient The [List] of [Color] to use as the gradient for the background.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behaviour. Our caller [SearchCategoryCollection] passes us a [Modifier.padding] that adds 8.dp to
 * all sides.
 */
@Composable
private fun SearchCategory(
    category: SearchCategory,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Layout(
        modifier = modifier
            .aspectRatio(ratio = 1.45f)
            .shadow(elevation = 3.dp, shape = CategoryShape)
            .clip(shape = CategoryShape)
            .background(brush = Brush.horizontalGradient(colors = gradient))
            .clickable { /* todo */ },
        content = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier
                    .padding(all = 4.dp)
                    .padding(start = 8.dp)
            )
            SnackImage(
                imageUrl = category.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    ) { measurables: List<Measurable>, constraints: Constraints ->
        // Text given a set proportion of width (which is determined by the aspect ratio)
        val textWidth: Int = (constraints.maxWidth * CategoryTextProportion).toInt()
        val textPlaceable: Placeable = measurables[0].measure(
            constraints = Constraints.fixedWidth(width = textWidth)
        )

        // Image is sized to the larger of height of item, or a minimum value
        // i.e. may appear larger than item (but clipped to the item bounds)
        val imageSize: Int = max(MinImageSize.roundToPx(), constraints.maxHeight)
        val imagePlaceable: Placeable = measurables[1].measure(
            constraints = Constraints.fixed(width = imageSize, height = imageSize)
        )
        layout(
            width = constraints.maxWidth,
            height = constraints.minHeight
        ) {
            textPlaceable.placeRelative(
                x = 0,
                y = (constraints.maxHeight - textPlaceable.height) / 2 // centered
            )
            imagePlaceable.placeRelative(
                // image is placed to end of text i.e. will overflow to the end (but be clipped)
                x = textWidth,
                y = (constraints.maxHeight - imagePlaceable.height) / 2 // centered
            )
        }
    }
}

/**
 * Three previews of the [SearchCategory] composable:
 *  - "default" preview with light theme
 *  - "dark theme" preview with dark theme
 *  - "large font" preview with font scale set to 2.0
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SearchCategoryPreview() {
    JetsnackTheme {
        SearchCategory(
            category = SearchCategory(
                name = "Desserts",
                imageUrl = ""
            ),
            gradient = JetsnackTheme.colors.gradient3_2
        )
    }
}
