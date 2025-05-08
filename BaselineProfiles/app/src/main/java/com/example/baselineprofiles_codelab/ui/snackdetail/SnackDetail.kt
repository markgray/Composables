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

package com.example.baselineprofiles_codelab.ui.snackdetail

import android.content.res.Configuration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.Snack
import com.example.baselineprofiles_codelab.model.SnackCollection
import com.example.baselineprofiles_codelab.model.SnackRepo
import com.example.baselineprofiles_codelab.ui.components.JetsnackButton
import com.example.baselineprofiles_codelab.ui.components.JetsnackDivider
import com.example.baselineprofiles_codelab.ui.components.JetsnackSurface
import com.example.baselineprofiles_codelab.ui.components.QuantitySelector
import com.example.baselineprofiles_codelab.ui.components.SnackCollection
import com.example.baselineprofiles_codelab.ui.components.SnackImage
import com.example.baselineprofiles_codelab.ui.home.JetsnackBottomBar
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import com.example.baselineprofiles_codelab.ui.theme.Neutral8
import com.example.baselineprofiles_codelab.ui.utils.formatPrice
import com.example.baselineprofiles_codelab.ui.utils.mirroringBackIcon
import kotlin.math.max
import kotlin.math.min

/**
 * The height of the [CartBottomBar] (the [JetsnackBottomBar] uses `BottomNavHeight` which is equal
 * as it needs to be for the shared transition to work properly). (`56.dp`)
 */
private val BottomBarHeight = 56.dp

/**
 * The height of the [Title] Composable. (`128.dp`)
 */
private val TitleHeight = 128.dp

/**
 * The height of a [Spacer] in the [Body] Composable that compensates for part of the Gradient in
 * [Spacer] at the top of the [Header] (I think?) (`180.dp`)
 */
private val GradientScroll = 180.dp

/**
 * The height of a [Spacer] in the [Body] Composable that compensates for the [Image] (`115.dp`)
 */
private val ImageOverlap = 115.dp

/**
 * The minimum offset from the top of the screen of the [Title] Composable when the
 * [CollapsingImageLayout] is collapsed (I think?) (`56.dp`)
 */
private val MinTitleOffset = 56.dp

/**
 * The minimum offset from the top of the screen of the [Image] Composable when the
 * [CollapsingImageLayout] is collapsed (I think?) (`12.dp`)
 */
private val MinImageOffset = 12.dp

/**
 * The maximum offset from the top of the screen of the [Title] Composable when the
 * [CollapsingImageLayout] is expanded (`351.dp`)
 */
private val MaxTitleOffset = ImageOverlap + MinTitleOffset + GradientScroll

/**
 * The maximum size of the [Image] Composable when the [CollapsingImageLayout] is expanded. (`351.dp`)
 */
private val ExpandedImageSize = 300.dp

/**
 * The minimum size of the [Image] Composable when the [CollapsingImageLayout] is collapsed. (`150.dp`)
 */
private val CollapsedImageSize = 150.dp

/**
 * The [Modifier.padding] for the `horizontal` edges of the many of our composables. (`24.dp`)
 */
private val HzPadding = Modifier.padding(horizontal = 24.dp)

/**
 * Displays the detailed view of a specific snack.
 *
 * This composable fetches and displays information about a snack, including its
 * image, title, and related snacks. It also provides navigation back to the
 * previous screen and a bottom bar for adding the snack to the cart.
 *
 * We start by initializing and remembering (using our [Long] parameter [snackId] as the `key1` of
 * the [remember] function) our [Snack] variable `snack` to the [Snack] that the [SnackRepo.getSnack]
 * method returns for our [Long] parameter [snackId]. Then we initialize and remember our [List] of
 * [SnackCollection] variable `related` to the value returned by the [SnackRepo.getRelated] method
 * for our [Long] parameter [snackId] (again using [snackId] as the `key1` of the [remember]
 * function.
 *
 * Our root composable is a [Box] whose `modifier` argument is [Modifier.fillMaxSize]. In its
 * [BoxScope] `content` composable lambda argument we:
 *
 * **First** initialize and remember our [ScrollState] variable `scroll` to the value returned by
 * the [rememberScrollState] function for an `initial` value of `0`.
 *
 * **Second** We compose a [Header] Composable.
 *
 * **Third** We compose a [Body] Composable with its `related` argument our [List] of
 * [SnackCollection] variable `related` and its `scroll` argument our [ScrollState] variable
 * `scroll`.
 *
 * **Fourth** We compose a [Title] Composable with its `snack` argument our [Snack] variable
 * `snack` and its `scrollProvider` argument a lambda function that returns the current `value` of
 * our [ScrollState] variable `scroll`.
 *
 * **Fifth** We compose an [Image] Composable with its `imageUrl` argument the value of the
 * [Snack.imageUrl] property of our [Snack] variable `snack` and its `scrollProvider` argument a
 * lambda function that returns the current `value` of our [ScrollState] variable `scroll`.
 *
 * **Sixth** We compose a [Up] Composable with its `upPress` argument our lambda parameter [upPress].
 *
 * **Seventh** We compose a [CartBottomBar] Composable whose `modifier` argument is [BoxScope.align]
 * with its `alignment` argument [Alignment.BottomCenter].
 *
 * @param snackId The unique identifier of the snack to display.
 * @param upPress A callback function invoked when the "up" navigation button is pressed.
 */
@Composable
fun SnackDetail(
    snackId: Long,
    upPress: () -> Unit
) {
    val snack: Snack = remember(key1 = snackId) { SnackRepo.getSnack(snackId = snackId) }
    val related: List<SnackCollection> =
        remember(key1 = snackId) { SnackRepo.getRelated(snackId = snackId) }

    Box(modifier = Modifier.fillMaxSize()) {
        val scroll: ScrollState = rememberScrollState(initial = 0)
        Header()
        Body(related = related, scroll = scroll)
        Title(snack = snack) { scroll.value }
        Image(imageUrl = snack.imageUrl) { scroll.value }
        Up(upPress = upPress)
        CartBottomBar(modifier = Modifier.align(alignment = Alignment.BottomCenter))
    }
}

/**
 * Displays a header area with a horizontal gradient background.
 *
 * This composable is used to create a visually distinct header section
 * at the top of the [SnackDetail] screen. It fills the width of the screen
 * and has a fixed height of 280.dp. The background uses a horizontal gradient
 * defined by the [JetsnackColors.tornado1] of our custom [JetsnackTheme.colors].
 *
 * Our root composable is a [Spacer] whose `modifier` argument is a [Modifier.height] of `280.dp`
 * with a [Modifier.fillMaxWidth] chained to it, and a [Modifier.background] chained to that with
 * its `brush` argument is a [Brush.horizontalGradient] with its `colors` argument the [List] of
 * [Color] in [JetsnackColors.tornado1] of our custom [JetsnackTheme.colors].
 */
@Composable
private fun Header() {
    Spacer(
        modifier = Modifier
            .height(height = 280.dp)
            .fillMaxWidth()
            .background(brush = Brush.horizontalGradient(colors = JetsnackTheme.colors.tornado1))
    )
}

/**
 * Displays an "Up" navigation button.
 *
 * This composable displays a circular button with a back arrow icon, that can be used to navigate
 * back to the previous screen. It positions itself in the top-left corner of the screen, applying
 * status bar padding and a subtle background color for better visibility.
 *
 * Our root composable is a [IconButton] whose `onClick` argument is our lambda parameter [upPress],
 * whose `modifier` argument is a [Modifier.statusBarsPadding] to add padding to accommodate the
 * status bars insets, chained to a [Modifier.padding] that adds `16.dp` to each horizontal side and
 * `10.dp` to each vertical side, a [Modifier.size] of `36.dp` chained to that and a
 * [Modifier.background] chained to that with its `shape` argument set to a [CircleShape] and its
 * `color` argument set to a copy of [Neutral8] with its `alpha` argument set to `0.32f`.
 *
 * In the `content` composable lambda argument of the [IconButton] we compose an [Icon] whose
 * arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [mirroringBackIcon]
 *  - `tint`: [Color] is the [JetsnackColors.iconInteractive] of our custom [JetsnackTheme.colors]
 *  - `contentDescription`: is the [String] with resource ID `R.string.label_back` ("Back").
 *
 * @param upPress A callback function invoked when the button is pressed, used for handling the
 * navigation back action.
 */
@Composable
private fun Up(upPress: () -> Unit) {
    IconButton(
        onClick = upPress,
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .size(size = 36.dp)
            .background(
                color = Neutral8.copy(alpha = 0.32f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = mirroringBackIcon(),
            tint = JetsnackTheme.colors.iconInteractive,
            contentDescription = stringResource(id = R.string.label_back)
        )
    }
}

/**
 * This Composable is responsible for displaying all the information "known" about the [Snack] that
 * the [SnackDetail] is displaying (this information is the same for all [Snack]'s).
 *
 * Our root composable is a [Column] in whose [ColumnScope] `content` lambda argument we compose
 * a [Spacer] whose `modifier` argument is a [Modifier.fillMaxWidth], chained to a
 * [Modifier.statusBarsPadding] that adds padding to accommodate the status bars insets, chained to
 * a [Modifier.height] whose `height` is [MinTitleOffset] (`56.dp`), followed by another [Column]
 * whose `modifier` argument is a [Modifier.verticalScroll] whose `state` argument is our
 * [ScrollState] parameter [scroll].
 *
 * In the inner [Column] `content` lambda argument we compose a [Spacer] whose `modifier` argument
 * is a [Modifier.height] whose `height` is [GradientScroll] (`180.dp`), followed by a
 * [JetsnackSurface] whose `modifier` argument is a [Modifier.fillMaxWidth]. Inside the
 * [JetsnackSurface] we compose yet another [Column] in whose [ColumnScope] `content` lambda argument
 * we compose:
 *
 * **First** a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` is
 * [ImageOverlap] (`115.dp`), followed by a [Spacer] whose `modifier` argument is a [Modifier.height]
 * whose `height` is [TitleHeight] (`128.dp`), followed by a [Spacer] whose `modifier` argument is
 * a [Modifier.height] whose `height` is `16.dp`.
 *
 * **Second** a [Text] whose arguments are:
 *  - `text`: is the [String] with resource ID `R.string.detail_header` ("Details")
 *  - `style`: [TextStyle] is the [Typography.overline] of our custom [MaterialTheme.typography].
 *  - `color`: [Color] is the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors].
 *  - `modifier`: is our [Modifier] constant [HzPadding] (a [Modifier.padding] that adds `24.dp` to
 *  each horizontal side).
 *
 * **Third** a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` is `16.dp`.
 *
 * **Fourth** we initialize and remember our [MutableState] wrapped [Boolean] variable `seeMore` to
 * an initial value of `true`.
 *
 * **Fifth** we compose a [Text] whose arguments are:
 *  - `text`: is the [String] with resource ID `R.string.detail_placeholder` (nonsense)
 *  - `style`: [TextStyle] is the [Typography.body1] of our custom [MaterialTheme.typography].
 *  - `color`: [Color] is the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors].
 *  - `maxLines`: if our [MutableState] wrapped [Boolean] variable `seeMore` is `true` then its is
 *  an [Int] equal to `5`, otherwise its value is [Int.MAX_VALUE].
 *  - `overflow`: [TextOverflow] is [TextOverflow.Ellipsis].
 *  - `modifier`: is our [Modifier] constant [HzPadding] (a [Modifier.padding] that adds `24.dp` to
 *  each horizontal side).
 *
 * **Sixth** we initialize our [String] variable `textButton` to the [String] with resource ID
 * `R.string.see_more` ("See more") if our [MutableState] wrapped [Boolean] variable `seeMore`
 * is `true`, otherwise its value is [String] with resource ID `R.string.see_less` ("See less").
 *
 * **Seventh** we compose a [Text] whose arguments are:
 *  - `text`: is our [String] variable `textButton`.
 *  - `style`: [TextStyle] is the [Typography.button] of our custom [MaterialTheme.typography].
 *  - `textAlign`: [TextAlign] is [TextAlign.Center].
 *  - `color`: [Color] is the [JetsnackColors.textLink] of our custom [JetsnackTheme.colors].
 *  - `modifier`: is a [Modifier.heightIn] whose `min` argument `20.dp`, chained to a
 *  [Modifier.fillMaxWidth], chained to a [Modifier.padding] whose `top` argument is `15.dp`,
 *  and at the end of the chain is a [Modifier.clickable] whose `onClick` argument is a lambda
 *  that sets our [MutableState] wrapped [Boolean] variable `seeMore` to its opposite value.
 *
 * **Eighth** we compose a [Spacer] whose `modifier` argument is a [Modifier.height] whose
 * `height` is `40.dp`.
 *
 * **Ninth** we compose a [Text] whose arguments are:
 *  - `text`: is the [String] with resource ID `R.string.ingredients` ("Ingredients")
 *  - `style`: [TextStyle] is the [Typography.overline] of our custom [MaterialTheme.typography].
 *  - `color`: [Color] is the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors].
 *  - `modifier`: is our [Modifier] constant [HzPadding] (a [Modifier.padding] that adds `24.dp` to
 *  each horizontal side).
 *
 * **Tenth** we compose a [Spacer] whose `modifier` argument is a [Modifier.height] whose
 * `height` is `4.dp`.
 *
 * **Eleventh** we compose a [Text] whose arguments are:
 *  - `text`: is the [String] with resource ID `R.string.ingredients_list` (a bunch of ingredients).
 *  - `style`: [TextStyle] is the [Typography.body1] of our custom [MaterialTheme.typography].
 *  - `color`: [Color] is the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors].
 *  - `modifier`: is our [Modifier] constant [HzPadding] (a [Modifier.padding] that adds `24.dp` to
 *  each horizontal side).
 *
 * **Twelfth** we compose a [Spacer] whose `modifier` argument is a [Modifier.height] whose
 * `height` is `16.dp`, followed by a [JetsnackDivider].
 *
 * **Thirteenth** we use the [Iterable.forEach] method of our [List] of [SnackCollection] parameter
 * [related] to loop through each [SnackCollection] in the [List] and in the `action` lambda argument
 * we capture the [SnackCollection] passed the lambda in variable `snackCollection` and compose a
 * [key] whose `key` argument is the [SnackCollection.id] of the [SnackCollection] variable
 * `snackCollection` (this is utility composable that is used to "group" or "key" the contents of
 * its `block` lambda argument). Inside the [key] `block` composable lambda argument we compose a
 * [SnackCollection] whose arguments are:
 *  - `snackCollection`: is the [SnackCollection] variable `snackCollection`.
 *  - `onSnackClick`: is a do-nothing lambda.
 *  - `highlight`: is `false`.
 *
 * **Fourteenth** we compose a [Spacer] whose `modifier` argument is a [Modifier.padding] that adds
 * [BottomBarHeight] padding to the `bottom`, with a [Modifier.navigationBarsPadding] chained to that
 * which adds padding to accommodate the navigation bars insets, chained to a [Modifier.height]
 * whose `height` is `8.dp`.
 *
 * @param related The list of related [SnackCollection] objects to display.
 * @param scroll The [ScrollState] used to manage the scrolling of the content.
 */
@Composable
private fun Body(
    related: List<SnackCollection>,
    scroll: ScrollState
) {
    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(height = MinTitleOffset)
        )
        Column(
            modifier = Modifier.verticalScroll(state = scroll)
        ) {
            Spacer(modifier = Modifier.height(height = GradientScroll))
            JetsnackSurface(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Spacer(modifier = Modifier.height(height = ImageOverlap))
                    Spacer(modifier = Modifier.height(height = TitleHeight))

                    Spacer(modifier = Modifier.height(height = 16.dp))
                    Text(
                        text = stringResource(id = R.string.detail_header),
                        style = MaterialTheme.typography.overline,
                        color = JetsnackTheme.colors.textHelp,
                        modifier = HzPadding
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    var seeMore: Boolean by remember { mutableStateOf(true) }
                    Text(
                        text = stringResource(id = R.string.detail_placeholder),
                        style = MaterialTheme.typography.body1,
                        color = JetsnackTheme.colors.textHelp,
                        maxLines = if (seeMore) 5 else Int.MAX_VALUE,
                        overflow = TextOverflow.Ellipsis,
                        modifier = HzPadding
                    )
                    val textButton: String = if (seeMore) {
                        stringResource(id = R.string.see_more)
                    } else {
                        stringResource(id = R.string.see_less)
                    }
                    Text(
                        text = textButton,
                        style = MaterialTheme.typography.button,
                        textAlign = TextAlign.Center,
                        color = JetsnackTheme.colors.textLink,
                        modifier = Modifier
                            .heightIn(min = 20.dp)
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                            .clickable {
                                seeMore = !seeMore
                            }
                    )
                    Spacer(modifier = Modifier.height(height = 40.dp))
                    Text(
                        text = stringResource(id = R.string.ingredients),
                        style = MaterialTheme.typography.overline,
                        color = JetsnackTheme.colors.textHelp,
                        modifier = HzPadding
                    )
                    Spacer(modifier = Modifier.height(height = 4.dp))
                    Text(
                        text = stringResource(id = R.string.ingredients_list),
                        style = MaterialTheme.typography.body1,
                        color = JetsnackTheme.colors.textHelp,
                        modifier = HzPadding
                    )

                    Spacer(modifier = Modifier.height(height = 16.dp))
                    JetsnackDivider()

                    related.forEach { snackCollection: SnackCollection ->
                        key(snackCollection.id) {
                            SnackCollection(
                                snackCollection = snackCollection,
                                onSnackClick = { },
                                highlight = false
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .padding(bottom = BottomBarHeight)
                            .navigationBarsPadding()
                            .height(height = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Displays the title area of a snack detail screen.
 *
 * This composable shows the snack's name, tagline, and price. It is designed to stick to the top
 * of the screen while scrolling, collapsing as the user scrolls down. The position of this
 * composable is dynamically calculated based on the scroll state provided by [scrollProvider].
 * It also provides padding for the status bar and uses the custom [JetsnackTheme] for styling.
 *
 * TODO: Continue here.
 *
 * @param snack The [Snack] object to display.
 * @param scrollProvider A lambda function that returns the current scroll state of the screen.
 */
@Composable
private fun Title(snack: Snack, scrollProvider: () -> Int) {
    val maxOffset: Float = with(LocalDensity.current) { MaxTitleOffset.toPx() }
    val minOffset: Float = with(LocalDensity.current) { MinTitleOffset.toPx() }

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .heightIn(min = TitleHeight)
            .statusBarsPadding()
            .offset {
                val scroll: Int = scrollProvider()
                val offset: Float = (maxOffset - scroll).coerceAtLeast(minOffset)
                IntOffset(x = 0, y = offset.toInt())
            }
            .background(color = JetsnackTheme.colors.uiBackground)
    ) {
        Spacer(modifier = Modifier.height(height = 16.dp))
        Text(
            text = snack.name,
            style = MaterialTheme.typography.h4,
            color = JetsnackTheme.colors.textSecondary,
            modifier = HzPadding
        )
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.subtitle2,
            fontSize = 20.sp,
            color = JetsnackTheme.colors.textHelp,
            modifier = HzPadding
        )
        Spacer(modifier = Modifier.height(height = 4.dp))
        Text(
            text = formatPrice(price = snack.price),
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.textPrimary,
            modifier = HzPadding
        )

        Spacer(modifier = Modifier.height(height = 8.dp))
        JetsnackDivider()
    }
}

@Composable
private fun Image(
    imageUrl: String,
    scrollProvider: () -> Int
) {
    val collapseRange: Float = with(LocalDensity.current) { (MaxTitleOffset - MinTitleOffset).toPx() }
    val collapseFractionProvider: () -> Float = {
        (scrollProvider() / collapseRange).coerceIn(0f, 1f)
    }

    CollapsingImageLayout(
        collapseFractionProvider = collapseFractionProvider,
        modifier = HzPadding.then(other = Modifier.statusBarsPadding())
    ) {
        SnackImage(
            imageUrl = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CollapsingImageLayout(
    collapseFractionProvider: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        check(value = measurables.size == 1)

        val collapseFraction: Float = collapseFractionProvider()

        val imageMaxSize: Int = min(ExpandedImageSize.roundToPx(), constraints.maxWidth)
        val imageMinSize: Int = max(CollapsedImageSize.roundToPx(), constraints.minWidth)
        val imageWidth: Int = lerp(imageMaxSize, imageMinSize, collapseFraction)
        val imagePlaceable: Placeable = measurables[0]
            .measure(constraints = Constraints.fixed(width = imageWidth, height = imageWidth))

        val imageY: Int = lerp(MinTitleOffset, MinImageOffset, collapseFraction).roundToPx()
        val imageX: Int = lerp(
            (constraints.maxWidth - imageWidth) / 2, // centered when expanded
            constraints.maxWidth - imageWidth, // right aligned when collapsed
            collapseFraction
        )
        layout(
            width = constraints.maxWidth,
            height = imageY + imageWidth
        ) {
            imagePlaceable.placeRelative(x = imageX, y = imageY)
        }
    }
}

@Composable
private fun CartBottomBar(modifier: Modifier = Modifier) {
    val (count: Int, updateCount: (Int) -> Unit) = remember { mutableIntStateOf(1) }
    JetsnackSurface(modifier = modifier) {
        Column {
            JetsnackDivider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .navigationBarsPadding()
                    .then(other = HzPadding)
                    .heightIn(min = BottomBarHeight)
            ) {
                QuantitySelector(
                    count = count,
                    decreaseItemCount = { if (count > 0) updateCount(count - 1) },
                    increaseItemCount = { updateCount(count + 1) }
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
                JetsnackButton(
                    onClick = { /* todo */ },
                    modifier = Modifier.weight(weight = 1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.add_to_cart),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Three Previews of our [SnackDetail] Composable using different configurations:
 *  - "default" - light theme
 *  - "dark theme" - dark theme
 *  - "large font" - large font size
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SnackDetailPreview() {
    JetsnackTheme {
        SnackDetail(
            snackId = 1L,
            upPress = { }
        )
    }
}
