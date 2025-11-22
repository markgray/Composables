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

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baselineprofiles_codelab.model.CollectionType
import com.example.baselineprofiles_codelab.model.Snack
import com.example.baselineprofiles_codelab.model.SnackCollection
import com.example.baselineprofiles_codelab.model.snacks
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import com.example.baselineprofiles_codelab.ui.utils.mirroringIcon

/**
 * The width of the highlight card in the UI.
 * This value determines the horizontal space allocated to each highlight card.
 * It's set to 170 density-independent pixels (dp) by default.
 */
private val HighlightCardWidth: Dp = 170.dp

/**
 * The default padding applied to highlight cards.
 *
 * This padding is used to provide visual breathing room around content
 * within a highlight card, ensuring that the content doesn't touch the
 * card's edges and improving readability and aesthetics.
 *
 * The value is set to 16.dp, which is a commonly used padding size in
 * Material Design and other design systems. You can adjust this value
 * if needed for specific layout requirements.
 */
private val HighlightCardPadding: Dp = 16.dp

/**
 * The width of the gradient effect used for highlighting the cards.
 * The Cards show a gradient which spans 3 cards and scrolls with parallax.
 *
 * This value is dynamically calculated based on the current screen density,
 * the width of a single highlight card, and the padding around it.
 * It's designed to create a gradient that smoothly transitions across roughly three card widths.
 *
 * @see HighlightCardWidth
 * @see HighlightCardPadding
 */
private val gradientWidth: Float
    @Composable
    get() = with(LocalDensity.current) {
        (3 * (HighlightCardWidth + HighlightCardPadding).toPx())
    }

/**
 * Displays its [SnackCollection] parameter [snackCollection] and all its [SnackCollection.snacks].
 *
 * Our root Composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In its [ColumnScope] `content` composable lambda argument we first display a [Row] whose
 * `verticalAlignment` argument is [Alignment.CenterVertically], and whose `modifier` argument is a
 * [Modifier.heightIn] whose `min` is `56.dp`, with a [Modifier.padding] whose `start` is `24.dp`.
 * In the [RowScope] `content` composable lambda argument of the [Row] we compose two elements:
 *
 * A [Text] whose arguments are:
 *  - `text` the [SnackCollection.name] of our [SnackCollection] parameter [snackCollection]
 *  - `style` is the [Typography.h6] of our custom [MaterialTheme.typography]
 *  - `color` is the [JetsnackColors.brand] of our custom [JetsnackTheme.colors]
 *  - `maxLines` is 1
 *  - `overflow` is [TextOverflow.Ellipsis]
 *  - `modifier` is a [RowScope.weight] whose `weight` is set to 1f, with a [Modifier.wrapContentWidth]
 *  whose `align` is set to [Alignment.Start] chained to that
 *
 * An [IconButton] whose `onClick` lambda argument is a lambda that does nothing, and whose `modifier`
 * argument is a [RowScope.align] whose `alignment` is set to [Alignment.CenterVertically]. In its
 * `content` composable lambda argument we display an [Icon] whose arguments are:
 *  - `imageVector` is the [ImageVector] returned by [mirroringIcon] when is `ltrIcon` argument is
 *  [Icons.AutoMirrored.Outlined.ArrowForward], and its `rtlIcon` argument is
 *  [Icons.AutoMirrored.Outlined.ArrowBack].
 *  - `tint` is the [JetsnackColors.brand] of our custom [JetsnackTheme.colors]
 *  - `contentDescription` is `null`
 *
 * Next in the [Column] if our [Boolean] parameter [highlight] is `true` and the
 * [SnackCollection.type] of our [SnackCollection] parameter [snackCollection] is equal to
 * [CollectionType.Highlight] we compose a [HighlightedSnacks] whose arguments are:
 *  - `index` is our [Int] parameter [index]
 *  - `snacks` is the [List] of [Snack]s in the [SnackCollection.snacks] property of our
 *  [SnackCollection] parameter [snackCollection].
 *  - `onSnackClick` is our lambda parameter [onSnackClick]
 *
 * Otherwise we compose a [Snacks] whose arguments are:
 *  - `snacks` is the [List] of [Snack]s in the [SnackCollection.snacks] property of our
 *  [SnackCollection] parameter [snackCollection].
 *  - `onSnackClick` is our lambda parameter [onSnackClick]
 *
 * @param snackCollection the [SnackCollection] whose [List] of [Snack] field [SnackCollection.snacks]
 * we should display.
 * @param onSnackClick the lambda that each of the [JetsnackCard] displaying a [Snack] from our
 * [snackCollection] should call with the [Snack.id] of the clicked [Snack].
 * @param modifier the [Modifier] to be applied to this layout node.
 * @param index the position of this [SnackCollection] in a [List] of [SnackCollection] that are
 * being displayed. We use it to select the [List] of [Color] to use as the gradient of any
 * [HighlightSnackItem] from our [SnackCollection] parameter [snackCollection] that we display.
 * @param highlight if `true` and the [SnackCollection.type] of our [SnackCollection] parameter
 * [snackCollection] is [CollectionType.Highlight], we use a [HighlightedSnacks] to display the
 * [List] of [Snack] in [SnackCollection.snacks] otherwise we use a [Snacks].
 */
@Composable
fun SnackCollection(
    snackCollection: SnackCollection,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0,
    highlight: Boolean = true
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(start = 24.dp)
        ) {
            Text(
                text = snackCollection.name,
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 1f)
                    .wrapContentWidth(align = Alignment.Start)
            )
            IconButton(
                onClick = { /* Forward? */ },
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = mirroringIcon(
                        ltrIcon = Icons.AutoMirrored.Outlined.ArrowForward,
                        rtlIcon = Icons.AutoMirrored.Outlined.ArrowBack
                    ),
                    tint = JetsnackTheme.colors.brand,
                    contentDescription = null
                )
            }
        }
        if (highlight && snackCollection.type == CollectionType.Highlight) {
            HighlightedSnacks(
                index = index,
                snacks = snackCollection.snacks,
                onSnackClick = onSnackClick
            )
        } else {
            Snacks(snacks = snackCollection.snacks, onSnackClick = onSnackClick)
        }
    }
}

/**
 * Displays a horizontal list of highlighted snacks using a [LazyRow].
 *
 * This composable takes a list of [Snack] items and displays them in a horizontally scrolling
 * row. Each snack is displayed using the [HighlightSnackItem] composable, and a gradient effect
 * is applied to visually highlight the items. The gradient alternates colors based on the index
 * of the [HighlightedSnacks] composable to create visual distinction.
 *
 * We start by initializing and remembering our [ScrollState] variable `scroll` with a new instance
 * whose initial value is `0`. We initialize our [List] of [Color] variable `gradient` with
 * [JetsnackColors.gradient6_1] if [index] is even or with [JetsnackColors.gradient6_2] if it is
 * odd. We initialize our [Float] variable `gradientWidth` to the pixel value for the current
 * [LocalDensity] of six times the quantity [HighlightCardWidth] plus [HighlightCardPadding].
 *
 * Our root Composable is a [LazyRow] whose `modifier` argument is our [Modifier] parameter [modifier],
 * whose `horizontalArrangement` argument is a [Arrangement.spacedBy] whose `space` argument is 16.dp,
 * and whose `contentPadding` argument is a [PaddingValues] whose `start` and `end` arguments are both
 * `24.dp`. In its [LazyListScope] `content` composable lambda argument we use the
 * [LazyListScope.itemsIndexed] method to iterate over our [List] of [Snack] parameter [snacks]
 * and in its [LazyItemScope] `itemContent` composable lambda argument we capture in [Int] passed
 * the lambda in our `index` variable and the [Snack] passed the lambda in our `snack` variable,
 * then compose for every [Snack] in our [List] a [HighlightSnackItem] whose arguments are:
 *  - `snack` is our [Snack] variable `snack`
 *  - `onSnackClick` is our lambda parameter [onSnackClick]
 *  - `index` is our [Int] variable `index`
 *  - `gradient` is oue [List] of [Color] variable `gradient`
 *  - `gradientWidth` is our [Float] variable `gradientWidth`
 *  - `scroll` is the [ScrollState.value] of our [ScrollState] variable `scroll`
 *
 * @param index The index of this [HighlightedSnacks] within its parent list. This is used to
 * alternate the gradient colors.
 * @param snacks The list of [Snack] items to display.
 * @param onSnackClick A lambda function to be called when a snack is clicked. It receives the
 * [Snack.id] of the clicked snack.
 * @param modifier [Modifier] for styling and layout customization.
 */
@Composable
private fun HighlightedSnacks(
    index: Int,
    snacks: List<Snack>,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * A [ScrollState] that will be used to control and observe the scrolling position of the
     * [LazyRow] displaying our [List] of [Snack] parameter [snacks].
     */
    val scroll: ScrollState = rememberScrollState(initial = 0)

    /**
     * [List] of [Color] used as the gradient of the [HighlightSnackItem] composed from our [snacks],
     * it alternates between [JetsnackColors.gradient6_1] of our custom [JetsnackTheme.colors] for
     * even indices and [JetsnackColors.gradient6_2] of our custom [JetsnackTheme.colors] for
     * odd indices.
     */
    val gradient: List<Color> = when ((index / 2) % 2) {
        0 -> JetsnackTheme.colors.gradient6_1
        else -> JetsnackTheme.colors.gradient6_2
    }

    /**
     * The width of the gradient effect used for highlighting the cards.
     * The Cards show a gradient which spans 3 cards and scrolls with parallax.
     */
    val gradientWidth: Float = with(LocalDensity.current) {
        (6 * (HighlightCardWidth + HighlightCardPadding).toPx())
    }
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
    ) {
        itemsIndexed(items = snacks) { index: Int, snack: Snack ->
            HighlightSnackItem(
                snack = snack,
                onSnackClick = onSnackClick,
                index = index,
                gradient = gradient,
                gradientWidth = gradientWidth,
                scroll = scroll.value
            )
        }
    }
}

/**
 * Displays a horizontal row of snack items.
 *
 * This composable function renders a horizontally scrolling list of [Snack] items.
 * Each snack item is clickable, triggering the `onSnackClick` callback with the
 * ID of the clicked snack.
 *
 * The root Composable is a [LazyRow] whose `modifier` argument is our [Modifier] parameter [modifier],
 * whose `contentPadding` argument is a [PaddingValues] whose `start` and `end` arguments are both
 * `12.dp`. In its [LazyListScope] `content` composable lambda argument we use the
 * [LazyListScope.items] method to iterate over our [List] of [Snack] parameter [snacks] and in
 * its [LazyItemScope] `itemContent` composable lambda argument we capture the [Snack] passed
 * the lambda in our `snack` variable, then compose for every [Snack] in our [List] of [Snack] a
 * [SnackItem] whose arguments are:
 *  - `snack` is our [Snack] variable `snack`
 *  - `onSnackClick` is our lambda parameter [onSnackClick]
 *
 * @param snacks The list of [Snack] objects to display.
 * @param onSnackClick A lambda function that is invoked when a snack item is clicked.
 * It receives the [Snack.id] of the clicked snack as a parameter.
 * @param modifier [Modifier] for styling and layout customization of the LazyRow.
 */
@Composable
private fun Snacks(
    snacks: List<Snack>,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
    ) {
        items(items = snacks) { snack: Snack ->
            SnackItem(snack = snack, onSnackClick = onSnackClick)
        }
    }
}

/**
 * Displays a single snack item in a card format.
 *
 * Our root Composable is a [JetsnackCard] whose `shape` argument is the [Shapes.medium] of our custom
 * [MaterialTheme.shapes], and whose `modifier` argument chains to our [Modifier] parameter [modifier]
 * a [Modifier.testTag] whose `tag` argument is `"snack_item"`, followed by a [Modifier.padding]
 * that adds `4.dp` to the start, `4.dp` to the `end`, and `8.dp` to the `bottom` of the content.
 * In the composable `content` lambda argument we compose a [Column] whose `horizontalAlignment`
 * argument is [Alignment.CenterHorizontally], and whose `modifier` argument is a [Modifier.clickable]
 * whose `onClick` argument is a lambda that calls our lambda parameter [onSnackClick] with the
 * [Snack.id] of the clicked [Snack], with a [Modifier.padding] that adds `8.dp` to all sides of the
 * content chained to that. In the [ColumnScope] `content` composable lambda argument we compose two
 * composables:
 *
 * A [SnackImage] whose arguments are:
 *  - `imageUrl` is the [Snack.imageUrl] property of our [Snack] parameter [snack]
 *  - `elevation` is `4.dp`.
 *  - `contentDescription` is `null`
 *  - `modifier` is a [Modifier.size] whose `size` is `120.dp`
 *
 * A [Text] whose arguments are:
 *  - `text` is the [Snack.name] property of our [Snack] parameter [snack]
 *  - `style` is the [Typography.subtitle1] of our custom [MaterialTheme.typography]
 *  - `color` is the [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors]
 *  - `modifier` is a [Modifier.padding] whose `top` is `8.dp`
 *
 * @param snack The [Snack] object containing the details of the snack.
 * @param onSnackClick Callback invoked when the snack item is clicked. It is provided the [Snack.id]
 * of the clicked snack as its parameter.
 * @param modifier [Modifier] to apply to the snack item container.
 */
@Composable
fun SnackItem(
    snack: Snack,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .testTag(tag = "snack_item")
            .padding(
                start = 4.dp,
                end = 4.dp,
                bottom = 8.dp
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(onClick = { onSnackClick(snack.id) })
                .padding(all = 8.dp)
        ) {
            SnackImage(
                imageUrl = snack.imageUrl,
                elevation = 4.dp,
                contentDescription = null,
                modifier = Modifier.size(size = 120.dp)
            )
            Text(
                text = snack.name,
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * Displays a snack item in a highlighted card format.
 *
 * This composable renders a single snack item within a card, showcasing the snack's image,
 * name, and tagline. It also applies a gradient background that shifts based on the scroll
 * position, creating a dynamic visual effect. The card is clickable, allowing users to
 * navigate to the snack's detail page.
 *
 * We start by initializing our [Float] variable `left` with the pixel value for the current
 * [LocalDensity] of the quantity [HighlightCardWidth] plus [HighlightCardPadding].
 *
 * Our root Composable is a [JetsnackCard] whose `modifier` argument is our [Modifier] parameter
 * [modifier] chained to a [Modifier.testTag] whose `tag` argument is `"snack_item"`, followed by a
 * [Modifier.size] whose `width` is `170.dp` and `height` is `250.dp`, and a [Modifier.padding] that
 * adds `16.dp` to the `bottom`.
 *
 * In the composable `content` lambda argument of the [JetsnackCard] we compose a [Column] whose
 * `modifier` argument is a [Modifier.clickable] whose `onClick` argument is a lambda that calls
 * our lambda parameter [onSnackClick] with the [Snack.id] of the clicked [Snack], with a
 * [Modifier.fillMaxSize] chained to that. In the [ColumnScope] `content` composable lambda
 * argument we compose the following composables:
 *
 * A [Box] whose `modifier` argument is a [Modifier.height] whose `height` is `160.dp`, with a
 * [Modifier.fillMaxWidth] chained to that. In its [BoxScope] `content` composable lambda we
 * initialize our [Float] variable `gradientOffset` with the pixel value for the current
 * [LocalDensity] of the quantity `left` minus the scroll position divided by 3 (this will be the
 * horizontal offset of the gradient). We then compose a [Box] to be the background with its
 * `modifier` argument a [Modifier.height] whose `height` is `100.dp`, with a [Modifier.fillMaxWidth]
 * chained to that, and a [Modifier.offsetGradientBackground] whose `colors` argument is our
 * [List] of [Color] parameter [gradient], whose `width` argument is our [Float] variable
 * `gradientWidth`, and whose `offset` argument is our [Float] variable `gradientOffset`. On top
 * of this we compose a [SnackImage] whose arguments are:
 *  - `imageUrl` is the [Snack.imageUrl] property of our [Snack] parameter [snack]
 *  - `contentDescription` is `null`
 *  - `modifier` is a [Modifier.size] whose `size` is `120.dp` with a [BoxScope.align] whose
 *  `alignment` is [Alignment.BottomCenter]
 *
 * Next in the [Column] we compose a [Spacer] whose `modifier` argument is a [Modifier.height] whose
 * `height` is `8.dp`.
 *
 * Next in the [Column] we compose a [Text] whose arguments are:
 *  - `text` is the [Snack.name] property of our [Snack] parameter [snack]
 *  - `maxLines` is `1`
 *  - `overflow` is [TextOverflow.Ellipsis]
 *  - `style` is the [Typography.h6] of our custom [MaterialTheme.typography]
 *  - `color` is the [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors]
 *  - `modifier` is a [Modifier.padding] whose `horizontal` argument is `16.dp`.
 *
 * Next in the [Column] we compose a [Spacer] whose `modifier` argument is a [Modifier.height]
 * whose `height` is `4.dp`.
 *
 * Finally in the [Column] we compose a [Text] whose arguments are:
 *  - `text` is the [Snack.tagline] property of our [Snack] parameter [snack]
 *  - `style` is the [Typography.body1] of our custom [MaterialTheme.typography]
 *  - `color` is the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors]
 *  - `modifier` is a [Modifier.padding] that adds `16.dp` to each `horizontal` side.
 *
 * @param snack The [Snack] data to display.
 * @param onSnackClick Callback invoked when the snack item is clicked. It is provided the [Snack.id]
 * of the clicked [Snack] as its parameter.
 * @param index The index of the snack item within a list, used for positioning calculations.
 * @param gradient The list of colors defining the gradient background.
 * @param gradientWidth The width of the gradient effect.
 * @param scroll The current scroll position of the list.
 * @param modifier [Modifier] to apply to the card container.
 */
@Composable
private fun HighlightSnackItem(
    snack: Snack,
    onSnackClick: (Long) -> Unit,
    index: Int,
    gradient: List<Color>,
    gradientWidth: Float,
    scroll: Int,
    modifier: Modifier = Modifier
) {
    val left: Float = index * with(LocalDensity.current) {
        (HighlightCardWidth + HighlightCardPadding).toPx()
    }
    JetsnackCard(
        modifier = modifier
            .testTag(tag = "snack_item")
            .size(
                width = 170.dp,
                height = 250.dp
            )
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = { onSnackClick(snack.id) })
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .height(height = 160.dp)
                    .fillMaxWidth()
            ) {
                /**
                 * The horizontal offset of the gradient for the current scroll position in pixels.
                 */
                val gradientOffset: Float = left - (scroll / 3f)
                Box(
                    modifier = Modifier
                        .height(height = 100.dp)
                        .fillMaxWidth()
                        .offsetGradientBackground(
                            colors = gradient,
                            width = gradientWidth,
                            offset = gradientOffset
                        )
                )
                SnackImage(
                    imageUrl = snack.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = 120.dp)
                        .align(alignment = Alignment.BottomCenter)
                )
            }
            Spacer(modifier = Modifier.height(height = 8.dp))
            Text(
                text = snack.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(height = 4.dp))
            Text(
                text = snack.tagline,
                style = MaterialTheme.typography.body1,
                color = JetsnackTheme.colors.textHelp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * A composable function that displays an image from a given URL within a circular surface.
 *
 * The image is loaded asynchronously using Coil's AsyncImage, and it's displayed within
 * a [JetsnackSurface] which provides a background with a specific color, elevation, and shape.
 *
 * Our root Composable is a [JetsnackSurface] whose `color` argument is [Color.LightGray], whose
 * `elevation` argument is our [Dp] parameter [elevation], whose `shape` argument is
 * [CircleShape], and whose `modifier` argument is our [Modifier] parameter [modifier].
 *
 * In the `content` composable lambda argument we compose an [AsyncImage] whose `model` argument
 * is built using an [ImageRequest.Builder] whose `context` argument is the current [LocalContext].
 * To this [ImageRequest.Builder] we chain a [ImageRequest.Builder.data] whose `data` argument
 * is our [String] parameter [imageUrl]. We then chain a [ImageRequest.Builder.crossfade] with
 * its `enable` argument set to `true`, and finally chain a [ImageRequest.Builder.build] to
 * create the [ImageRequest]. The `contentDescription` argument is our [String] parameter
 * [contentDescription]. The `modifier` argument is a [Modifier.fillMaxSize], and its `contentScale`
 * argument is [ContentScale.Crop].
 *
 * @param imageUrl The URL of the image to be displayed.
 * @param contentDescription A description of the image for accessibility purposes.
 * Should be `null` if the image is decorative.
 * @param modifier [Modifier] to be applied to the [JetsnackSurface] containing the image.
 * @param elevation The elevation of the `JetsnackSurface`, controlling the shadow effect.
 * Defaults to `0.dp`, meaning no shadow.
 */
@Composable
fun SnackImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp
) {
    JetsnackSurface(
        color = Color.LightGray,
        elevation = elevation,
        shape = CircleShape,
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(data = imageUrl)
                .crossfade(enable = true)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

/**
 * Three previews of [HighlightSnackItem]:
 *  - "default" default arguments
 *  - "dark theme" [UI_MODE_NIGHT_YES]
 *  - "large font" fontScale = 2f
 */
@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun SnackCardPreview() {
    JetsnackTheme {
        val snack = snacks.first()
        HighlightSnackItem(
            snack = snack,
            onSnackClick = { },
            index = 0,
            gradient = JetsnackTheme.colors.gradient6_1,
            gradientWidth = gradientWidth,
            scroll = 0
        )
    }
}
