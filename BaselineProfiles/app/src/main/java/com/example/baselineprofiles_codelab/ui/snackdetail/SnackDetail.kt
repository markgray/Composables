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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
 * TODO: Continue here.
 *
 * This composable fetches and displays information about a snack, including its
 * image, title, and related snacks. It also provides navigation back to the
 * previous screen and a bottom bar for adding the snack to the cart.
 *
 * @param snackId The unique identifier of the snack to display.
 * @param upPress A callback function invoked when the "up" navigation button is pressed.
 */
@Composable
fun SnackDetail(
    snackId: Long,
    upPress: () -> Unit
) {
    val snack: Snack = remember(key1 = snackId) { SnackRepo.getSnack(snackId) }
    val related: List<SnackCollection> =
        remember(key1 = snackId) { SnackRepo.getRelated(snackId = snackId) }

    Box(modifier = Modifier.fillMaxSize()) {
        val scroll: ScrollState = rememberScrollState(0)
        Header()
        Body(related = related, scroll = scroll)
        Title(snack = snack) { scroll.value }
        Image(imageUrl = snack.imageUrl) { scroll.value }
        Up(upPress = upPress)
        CartBottomBar(modifier = Modifier.align(alignment = Alignment.BottomCenter))
    }
}

@Composable
private fun Header() {
    Spacer(
        modifier = Modifier
            .height(height = 280.dp)
            .fillMaxWidth()
            .background(brush = Brush.horizontalGradient(colors = JetsnackTheme.colors.tornado1))
    )
}

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
