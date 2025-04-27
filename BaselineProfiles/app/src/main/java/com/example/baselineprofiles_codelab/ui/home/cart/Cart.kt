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

package com.example.baselineprofiles_codelab.ui.home.cart

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.OrderLine
import com.example.baselineprofiles_codelab.model.SnackCollection
import com.example.baselineprofiles_codelab.model.SnackRepo
import com.example.baselineprofiles_codelab.ui.components.JetsnackButton
import com.example.baselineprofiles_codelab.ui.components.JetsnackDivider
import com.example.baselineprofiles_codelab.ui.components.JetsnackSurface
import com.example.baselineprofiles_codelab.ui.components.QuantitySelector
import com.example.baselineprofiles_codelab.ui.components.SnackCollection
import com.example.baselineprofiles_codelab.ui.components.SnackImage
import com.example.baselineprofiles_codelab.ui.home.DestinationBar
import com.example.baselineprofiles_codelab.ui.theme.AlphaNearOpaque
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import com.example.baselineprofiles_codelab.ui.utils.formatPrice

/**
 * TODO: Continue here
 */
@Composable
fun Cart(
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(factory = CartViewModel.provideFactory())
) {
    val orderLines: List<OrderLine> by viewModel.orderLines.collectAsState()
    val inspiredByCart: SnackCollection = remember { SnackRepo.getInspiredByCart() }
    Cart(
        orderLines = orderLines,
        removeSnack = viewModel::removeSnack,
        increaseItemCount = viewModel::increaseSnackCount,
        decreaseItemCount = viewModel::decreaseSnackCount,
        inspiredByCart = inspiredByCart,
        onSnackClick = onSnackClick,
        modifier = modifier
    )
}

@Composable
fun Cart(
    orderLines: List<OrderLine>,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Box {
            CartContent(
                orderLines = orderLines,
                removeSnack = removeSnack,
                increaseItemCount = increaseItemCount,
                decreaseItemCount = decreaseItemCount,
                inspiredByCart = inspiredByCart,
                onSnackClick = onSnackClick,
                modifier = Modifier.align(alignment = Alignment.TopCenter)
            )
            DestinationBar(modifier = Modifier.align(alignment = Alignment.TopCenter))
            CheckoutBar(modifier = Modifier.align(alignment = Alignment.BottomCenter))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CartContent(
    orderLines: List<OrderLine>,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    inspiredByCart: SnackCollection,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources: Resources = LocalContext.current.resources
    val snackCountFormattedString: String = remember(orderLines.size, resources) {
        resources.getQuantityString(
            R.plurals.cart_order_count,
            orderLines.size, orderLines.size
        )
    }
    LazyColumn(modifier = modifier) {
        item {
            Spacer(
                Modifier.windowInsetsTopHeight(
                    insets = WindowInsets.statusBars.add(insets = WindowInsets(top = 56.dp))
                )
            )
            Text(
                text = stringResource(R.string.cart_order_header, snackCountFormattedString),
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 24.dp, vertical = 4.dp)
                    .wrapContentHeight()
            )
        }
        items(items = orderLines) { orderLine: OrderLine ->
            SwipeDismissItem(
                background = { offsetX: Dp ->
                    /*Background color changes from light gray to red when the
                    swipe to delete with exceeds 160.dp*/
                    val backgroundColor: Color = if (offsetX < (-160).dp) {
                        JetsnackTheme.colors.error
                    } else {
                        JetsnackTheme.colors.uiFloated
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(color = backgroundColor),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Set 4.dp padding only if offset is bigger than 160.dp
                        val padding: Dp by animateDpAsState(
                            if (offsetX > (-160).dp) 4.dp else 0.dp
                        )
                        Box(
                            Modifier
                                .width(width = offsetX * -1)
                                .padding(all = padding)
                        ) {
                            // Height equals to width removing padding
                            val height: Dp = (offsetX + 8.dp) * -1
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = height)
                                    .align(alignment = Alignment.Center),
                                shape = CircleShape,
                                color = JetsnackTheme.colors.error
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Icon must be visible while in this width range
                                    if (offsetX < (-40).dp && offsetX > (-152).dp) {
                                        // Icon alpha decreases as it is about to disappear
                                        val iconAlpha: Float by animateFloatAsState(
                                            if (offsetX < (-120).dp) 0.5f else 1f
                                        )

                                        Icon(
                                            imageVector = Icons.Filled.DeleteForever,
                                            modifier = Modifier
                                                .size(size = 16.dp)
                                                .graphicsLayer(alpha = iconAlpha),
                                            tint = JetsnackTheme.colors.uiBackground,
                                            contentDescription = null,
                                        )
                                    }
                                    /*Text opacity increases as the text is supposed to appear in
                                    the screen*/
                                    val textAlpha: Float by animateFloatAsState(
                                        if (offsetX > (-144).dp) 0.5f else 1f
                                    )
                                    if (offsetX < (-120).dp) {
                                        Text(
                                            text = stringResource(id = R.string.remove_item),
                                            style = MaterialTheme.typography.subtitle1,
                                            color = JetsnackTheme.colors.uiBackground,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .graphicsLayer(
                                                    alpha = textAlpha
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
            ) {
                CartItem(
                    orderLine = orderLine,
                    removeSnack = removeSnack,
                    increaseItemCount = increaseItemCount,
                    decreaseItemCount = decreaseItemCount,
                    onSnackClick = onSnackClick
                )
            }
        }
        item {
            SummaryItem(
                subtotal = orderLines.sumOf { it.snack.price * it.count },
                shippingCosts = 369
            )
        }
        item {
            SnackCollection(
                snackCollection = inspiredByCart,
                onSnackClick = onSnackClick,
                highlight = false
            )
            Spacer(modifier = Modifier.height(height = 56.dp))
        }
    }
}

@Composable
fun CartItem(
    orderLine: OrderLine,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val snack = orderLine.snack
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id) }
            .background(color = JetsnackTheme.colors.uiBackground)
            .padding(horizontal = 24.dp)

    ) {
        val (divider, image, name, tag, priceSpacer, price, remove, quantity) = createRefs()
        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
        SnackImage(
            imageUrl = snack.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(size = 100.dp)
                .constrainAs(ref = image) {
                    top.linkTo(anchor = parent.top, margin = 16.dp)
                    bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
                    start.linkTo(anchor = parent.start)
                }
        )
        Text(
            text = snack.name,
            style = MaterialTheme.typography.subtitle1,
            color = JetsnackTheme.colors.textSecondary,
            modifier = Modifier.constrainAs(ref = name) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = remove.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        IconButton(
            onClick = { removeSnack(snack.id) },
            modifier = Modifier
                .constrainAs(ref = remove) {
                    top.linkTo(anchor = parent.top)
                    end.linkTo(anchor = parent.end)
                }
                .padding(top = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                tint = JetsnackTheme.colors.iconSecondary,
                contentDescription = stringResource(id = R.string.label_remove)
            )
        }
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.body1,
            color = JetsnackTheme.colors.textHelp,
            modifier = Modifier.constrainAs(ref = tag) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = parent.end,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Spacer(
            Modifier
                .height(height = 8.dp)
                .constrainAs(ref = priceSpacer) {
                    linkTo(top = tag.bottom, bottom = price.top)
                }
        )
        Text(
            text = formatPrice(price = snack.price),
            style = MaterialTheme.typography.subtitle1,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.constrainAs(ref = price) {
                linkTo(
                    start = image.end,
                    end = quantity.start,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        QuantitySelector(
            count = orderLine.count,
            decreaseItemCount = { decreaseItemCount(snack.id) },
            increaseItemCount = { increaseItemCount(snack.id) },
            modifier = Modifier.constrainAs(ref = quantity) {
                baseline.linkTo(anchor = price.baseline)
                end.linkTo(anchor = parent.end)
            }
        )
        JetsnackDivider(
            Modifier.constrainAs(ref = divider) {
                linkTo(start = parent.start, end = parent.end)
                top.linkTo(anchor = parent.bottom)
            }
        )
    }
}

@Composable
fun SummaryItem(
    subtotal: Long,
    shippingCosts: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = stringResource(id = R.string.cart_summary_header),
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.brand,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .heightIn(min = 56.dp)
                .wrapContentHeight()
        )
        Row(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = stringResource(id = R.string.cart_subtotal_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(weight = 1f)
                    .wrapContentWidth(align = Alignment.Start)
                    .alignBy(alignmentLine = LastBaseline)
            )
            Text(
                text = formatPrice(price = subtotal),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.alignBy(alignmentLine = LastBaseline)
            )
        }
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(id = R.string.cart_shipping_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(weight = 1f)
                    .wrapContentWidth(align = Alignment.Start)
                    .alignBy(alignmentLine = LastBaseline)
            )
            Text(
                text = formatPrice(price = shippingCosts),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.alignBy(alignmentLine = LastBaseline)
            )
        }
        Spacer(modifier = Modifier.height(height = 8.dp))
        JetsnackDivider()
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(id = R.string.cart_total_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(end = 16.dp)
                    .wrapContentWidth(align = Alignment.End)
                    .alignBy(alignmentLine = LastBaseline)
            )
            Text(
                text = formatPrice(price = subtotal + shippingCosts),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.alignBy(alignmentLine = LastBaseline)
            )
        }
        JetsnackDivider()
    }
}

@Composable
private fun CheckoutBar(modifier: Modifier = Modifier) {
    Column(
        modifier.background(
            color = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque)
        )
    ) {

        JetsnackDivider()
        Row {
            Spacer(Modifier.weight(weight = 1f))
            JetsnackButton(
                onClick = { /* todo */ },
                shape = RectangleShape,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .weight(weight = 1f)
            ) {
                Text(
                    text = stringResource(id = R.string.cart_checkout),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun CartPreview() {
    JetsnackTheme {
        Cart(
            orderLines = SnackRepo.getCart(),
            removeSnack = {},
            increaseItemCount = {},
            decreaseItemCount = {},
            inspiredByCart = SnackRepo.getInspiredByCart(),
            onSnackClick = {}
        )
    }
}
