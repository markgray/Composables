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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.OrderLine
import com.example.baselineprofiles_codelab.model.Snack
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
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import com.example.baselineprofiles_codelab.ui.utils.formatPrice
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.platform.LocalResources

/**
 * Stateful composable that Displays the contents of our cart, by calling its Stateless override.
 *
 * We start by initializing our [State] wrapped [List] of [OrderLine] variable `orderLines` by using
 * the [StateFlow.collectAsState] method of the [StateFlow] of [List] of [OrderLine] property
 * [CartViewModel.orderLines] of our [CartViewModel] parameter [viewModel]. Then we initialize and
 * remember our [SnackCollection] variable `inspiredByCart` to the [SnackCollection] returned by
 * the [SnackRepo.getInspiredByCart] method. Our root composable function is our stateless `Cart`
 * override whose arguments are;
 *  - `orderLines`: Our [State] wrapped [List] of [OrderLine] variable `orderLines`.
 *  - `removeSnack`: A function reference to the [CartViewModel.removeSnack] method.
 *  - `increaseItemCount`: A function reference to the [CartViewModel.increaseSnackCount] method.
 *  - `decreaseItemCount`: A function reference to the [CartViewModel.decreaseSnackCount] method.
 *  - `inspiredByCart`: Our [SnackCollection] variable `inspiredByCart`.
 *  - `onSnackClick`: Our lambda parameter [onSnackClick].
 *  - `modifier`: Our [Modifier] parameter [modifier].
 *
 * @param onSnackClick function to be called with the [Snack.id] when a [Snack] is clicked.
 * @param modifier [Modifier] to be applied to the layout.
 * @param viewModel the [CartViewModel] for this cart.
 */
@Composable
fun Cart(
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(factory = CartViewModel.provideFactory())
) {
    /**
     * The [State] wrapped [List] of [OrderLine] that are in our cart.
     */
    val orderLines: List<OrderLine> by viewModel.orderLines.collectAsState()

    /**
     * The [SnackCollection] of snacks that are inspired by the contents of the cart.
     */
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

/**
 * Stateless composable override that displays the contents of our cart, which is called by its
 * Stateful override.
 *
 * Our root composable is a [JetsnackSurface] whose `modifier` argument chains to our [Modifier]
 * parameter [modifier] a [Modifier.fillMaxSize] to fill the entire screen. In the `content`
 * composable lambda argument of the [JetsnackSurface] we compose a [Box], and inside the [Box] we
 * compose three composables:
 *
 * A [CartContent] whose arguments are:
 *  - `orderLines`: Our [State] wrapped [List] of [OrderLine] parameter [orderLines].
 *  - `removeSnack`: Our lambda parameter [removeSnack].
 *  - `increaseItemCount`: Our lambda parameter [increaseItemCount].
 *  - `decreaseItemCount`: Our lambda parameter [decreaseItemCount].
 *  - `inspiredByCart`: Our [SnackCollection] parameter [inspiredByCart].
 *  - `onSnackClick`: Our lambda parameter [onSnackClick].
 *  - `modifier`: The [Modifier] extension function [BoxScope.align] with the its `alignment`
 *  argument set to [Alignment.TopCenter].
 *
 * A [DestinationBar] whose `modifier` is a [BoxScope.align] with the its `alignment` argument
 * set to [Alignment.TopCenter].
 *
 * A [CheckoutBar] whose `modifier` is a [BoxScope.align] with the its `alignment` argument set
 * to [Alignment.BottomCenter].
 *
 * @param orderLines the [List] of [OrderLine] that are in our cart.
 * @param removeSnack function to be called with the [Snack.id] when a [Snack] is removed from the
 * cart.
 * @param increaseItemCount function to be called with the [Snack.id] when a [Snack] count is
 * increased.
 * @param decreaseItemCount function to be called with the [Snack.id] when a [Snack] count is
 * decreased.
 * @param inspiredByCart the [SnackCollection] of snacks that are inspired by the contents of the
 * cart.
 * @param onSnackClick function to be called with the [Snack.id] when a [Snack] is clicked.
 * @param modifier [Modifier] to be applied to the layout.
 */
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

/**
 * Displays the content of the shopping cart.
 *
 * This composable function renders the list of items in the cart,
 * along with controls to remove items, increase/decrease quantity,
 * a summary of the order, and a "Inspired by your cart" section.
 *
 * We start by initializing our [Resources] variable `resources` to the `current` [LocalContext]
 * resources. Then we initialize and remember our [String] variable `snackCountFormattedString`
 * to the formatted string of the number of items in our cart created by the
 * [Resources.getQuantityString] method from the format string `R.plurals.cart_order_count` and the
 * [List.size] of our [List] of [OrderLine] parameter [orderLines].
 *
 * Our root composable function is a [LazyColumn] whose `modifier` is our [Modifier] parameter
 * [modifier]. In its [LazyListScope] `content` composable lambda argument we first compose a
 * [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda argument we
 * compose a [Spacer] whose `modifier` argument is a [Modifier.windowInsetsTopHeight] with the
 * `insets` argument set to [WindowInsets.Companion.statusBars] with a [WindowInsets.add] chained
 * to it to add an additional [WindowInsets] `top` of 56.dp. Below this in the `item` we compose a
 * [Text] whose arguments are:
 *  - `text`: The formatted string of the number of items in our cart with "Order" prepended.
 *  - `style`: The [TextStyle] of the text is the [Typography.h6] of our custom
 *  [MaterialTheme.typography].
 *  - `color`: The [Color] of the text is the [JetsnackColors.brand] of our custom
 *  [JetsnackTheme.colors].
 *  - `maxLines`: The number of lines of text to display is `1`.
 *  - `overflow`: The text overflow is [TextOverflow.Ellipsis].
 *  - `modifier`: The [Modifier] of the text is a [Modifier.heightIn] whose `min` is `56.dp`, with a
 *  [Modifier.padding] chained to that that adds `24.dp` to each `horizontal` side and `4.dp` to
 *  each `vertical` side, with a [Modifier.wrapContentHeight] chained to that.
 *
 * Next in the [LazyColumn] we compose a [LazyListScope.items] whose `items` argument is our
 * [List] of [OrderLine] parameter [orderLines] and in whose [LazyItemScope] `itemContent` composable
 * lambda argument each [OrderLine] passed the lambda is accepted in our variable `orderLine` then
 * we compose a [SwipeDismissItem] whose `background` is a lambda that accepts the [Dp] passed the
 * lambda in variable `offsetX` and then composes a complex composable that animates the dismissing
 * of the [SwipeDismissItem] (see the code as i am too lazy to describe it). In the `content`
 * composable lambda argument of the [SwipeDismissItem] we compose a [CartItem] whose arguments are:
 *  - `orderLine`: Our [OrderLine] variable `orderLine`.
 *  - `removeSnack`: Our lambda parameter [removeSnack].
 *  - `increaseItemCount`: Our lambda parameter [increaseItemCount].
 *  - `decreaseItemCount`: Our lambda parameter [decreaseItemCount].
 *  - `onSnackClick`: Our lambda parameter [onSnackClick].
 *
 * Next in the [LazyColumn] we compose a [LazyListScope.item] in whose [LazyItemScope] `content`
 * composable lambda argument we compose a [SummaryItem] whose arguments are:
 *  - `subtotal`: The subtotal of the [Snack.price] of all of items in our cart.
 *  - `shippingCosts`: The shipping costs of our cart is a constant of `369`.
 *
 * Finally in the [LazyColumn] we compose a [LazyListScope.item] in whose [LazyItemScope] `content`
 * composable lambda argument we compose a [SnackCollection] whose arguments are:
 *  - `snackCollection`: Our [SnackCollection] variable `inspiredByCart`.
 *  - `onSnackClick`: Our lambda parameter [onSnackClick].
 *  - `highlight`: Our boolean parameter `highlight` is set to `false`.
 *
 * Below the [SnackCollection] in the [LazyListScope.item] holding it we compose a [Spacer] whse
 * `modifier` is a [Modifier.height] whose `height` is set to `56.dp`
 *
 * @param orderLines the [List] of [OrderLine] that are in our cart.
 * @param removeSnack function to be called with the [Snack.id] when a [Snack] is removed from the
 * cart.
 * @param increaseItemCount function to be called with the [Snack.id] when a [Snack] count is
 * increased.
 * @param decreaseItemCount function to be called with the [Snack.id] when a [Snack] count is
 * decreased.
 * @param inspiredByCart the [SnackCollection] of snacks that are inspired by the contents of the
 * cart.
 * @param onSnackClick function to be called with the [Snack.id] when a [Snack] is clicked.
 * @param modifier [Modifier] to be applied to the layout.
 */
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
    /**
     * The [Resources] used to provide string resources to the app.
     */
    val resources: Resources = LocalResources.current

    /**
     * The formatted [String] of the number of items in our cart.
     */
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
                    swipe to delete `offsetX` exceeds 160.dp*/
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

/**
 * A composable function that displays a single item in the cart.
 *
 * This composable represents a row in the cart, showing details of a specific snack,
 * its quantity, and offering actions to remove or adjust the quantity of the snack.
 *
 * We start by initializing our [Snack] variable `snack` to the [OrderLine.snack] of our [OrderLine]
 * parameter [orderLine].
 *
 * Our root composable function is a [ConstraintLayout] whose `modifier` argument chains to our
 * [Modifier] parameter [modifier] a [Modifier.fillMaxWidth] to fill the width of the parent,
 * with a [Modifier.clickable] chained to that that calls our lambda parameter [onSnackClick],
 * with the [Snack.id] of our [Snack] variable `snack`, with a [Modifier.background] chained to
 * that that sets the [Color] of the background to the [JetsnackColors.uiBackground] of our custom
 * [JetsnackTheme.colors], with a [Modifier.padding] chained to that that adds `24.dp` to each
 * `horizontal` side. In the [ConstraintLayoutScope] `content` composable lambda argument we start
 * by initializing our [ConstrainedLayoutReference] variables `divider`, `image`, `name`, `tag`,
 * `priceSpacer`, `price`, `remove`, and `quantity` to new instances using the
 * [ConstraintLayoutScope.createRefs] method of the [ConstraintLayout]. Then we call the
 * [ConstraintLayoutScope.createVerticalChain] method of the [ConstraintLayout] with the `chainStyle`
 * argument set to [ChainStyle.Packed] to vertically chain the [ConstrainedLayoutReference] variables
 * `name`, `tag`, `priceSpacer`, and `price`.
 *
 * **The composables in the [ConstraintLayout]'s `content` are:**
 *
 * A [SnackImage] whose arguments are:
 *  - `imageUrl`: The [String] of the [Snack.imageUrl] of our [Snack] variable `snack`.
 *  - `contentDescription`: The `null` [String].
 *  - `modifier`: The [Modifier] of the [SnackImage] is a [Modifier.size] whose `size` is set to
 *  `100.dp`, with a [ConstraintLayoutScope.constrainAs] chained to that that sets the
 *  [ConstrainedLayoutReference] `ref` to the [ConstrainedLayoutReference] variable `image` and
 *  in its [ConstrainScope] `constrainBlock` lambda argument we link the `top` to the parent `top`
 *  with a margin of `16.dp`, the `bottom` to the parent `bottom` with a margin of `16.dp`, and
 *  the `start` to the parent `start`.
 *
 * A [Text] whose arguments are:
 *  - `text`: The [String] of the [Snack.name] of our [Snack] variable `snack`.
 *  - `style`: The [TextStyle] of the text is the [Typography.subtitle1] of our custom
 *  [MaterialTheme.typography].
 *  - `color`: The [Color] of the text is the [JetsnackColors.textSecondary] of our custom
 *  [JetsnackTheme.colors].
 *  - `modifier`: The [Modifier] of the text is a [ConstraintLayoutScope.constrainAs] that sets the
 *  [ConstrainedLayoutReference] `ref` to the [ConstrainedLayoutReference] variable `name` and
 *  in its [ConstrainScope] `constrainBlock` lambda argument uses the [ConstrainScope.linkTo] method
 *  to link the `start` to the [ConstrainedLayoutReference.end] of the [ConstrainedLayoutReference]
 *  variable `image` with a `startMargin` of `16.dp`, the `end` to [ConstrainedLayoutReference.start]
 *  of the [ConstrainedLayoutReference] variable `remove` with a `endMargin` of `16.dp`, and a
 *  `bias` of `0f`.
 *
 * An [IconButton] whose `onClick` argument is a lambda that calls our lambda parameter [removeSnack]
 * with the [Snack.id] of our [Snack] variable `snack`, whose `modifier` argument is a
 * [ConstraintLayoutScope.constrainAs] that sets the [ConstrainedLayoutReference] `ref` to the
 * [ConstrainedLayoutReference] variable `remove` and in its [ConstrainScope] `constrainBlock`
 * lambda argument we link the `top` to the parent `top` and the `end` to the parent `end`, with a
 * [Modifier.padding] chained to that that adds `12.dp` to the `top`. In the `content` composable
 * lambda argument we compose an [Icon] whose arguments are:
 *  - `imageVector`: The [ImageVector] drawn by [Icons.Filled.Close].
 *  - `tint`: The [Color] of the icon is the [JetsnackColors.iconSecondary] of our custom
 *  [JetsnackTheme.colors].
 *  - `contentDescription`: The [String] with resource ID `R.string.label_remove` ("Remove item").
 *
 * A [Text] whose arguments are:
 *  - `text`: The [Snack.tagline] of our [Snack] variable `snack`.
 *  - `style`: The [TextStyle] of the text is the [Typography.body1] of our custom
 *  [MaterialTheme.typography].
 *  - `color`: The [Color] of the text is the [JetsnackColors.textHelp] of our custom
 *  [JetsnackTheme.colors].
 *  - `modifier`: The [Modifier] of the text is a [ConstraintLayoutScope.constrainAs] that sets
 *  the [ConstrainedLayoutReference] `ref` to the [ConstrainedLayoutReference] variable `tag` and
 *  in its [ConstrainScope] `constrainBlock` lambda argument use the [ConstrainScope.linkTo] method
 *  to link the `start` to the [ConstrainedLayoutReference.end] of the [ConstrainedLayoutReference]
 *  variable `image` with a `startMargin` of `16.dp`, the `end` to [ConstrainedLayoutReference.end]
 *  of our `parent` with a `endMargin` of `16.dp`, and a `bias` of `0f`.
 *
 * A [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` is set to `8.dp`, with
 * a [ConstraintLayoutScope.constrainAs] chained to that that sets the [ConstrainedLayoutReference]
 * `ref` to the [ConstrainedLayoutReference] variable `priceSpacer` and in its [ConstrainScope]
 * `constrainBlock` lambda argument uses the [ConstrainScope.linkTo] method to link the `top` to the
 * [ConstrainedLayoutReference.bottom] of the [ConstrainedLayoutReference] variable `tag` and the
 * `bottom` to the [ConstrainedLayoutReference.top] of the [ConstrainedLayoutReference] variable
 * `price`
 *
 * A [Text] whose arguments are:
 *  - `text`: The [String] of the formatted price of our [Snack.price] of our [Snack] variable `snack`
 *  returned by the [formatPrice] method.
 *  - `style`: The [TextStyle] of the text is the [Typography.subtitle1] of our custom
 *  [MaterialTheme.typography].
 *  - `color`: The [Color] of the text is the [JetsnackColors.textPrimary] of our custom
 *  [JetsnackTheme.colors].
 *  - `modifier`: The [Modifier] of the text is a [ConstraintLayoutScope.constrainAs] that sets
 *  the [ConstrainedLayoutReference] `ref` to the [ConstrainedLayoutReference] variable `price`
 *  and in its [ConstrainScope] `constrainBlock` lambda argument uses the [ConstrainScope.linkTo]
 *  method to link the `start` to the [ConstrainedLayoutReference.end] of the [ConstrainedLayoutReference]
 *  variable `image`, the `end` to [ConstrainedLayoutReference.start] of the [ConstrainedLayoutReference]
 *  variable `quantity`, with a `startMargin` of `16.dp`, an `endMargin` of `16.dp`, and a `bias`
 *  of `0f`.
 *
 * A [QuantitySelector] whose arguments are:
 *  - `count`: The [Int] of the [OrderLine.count] of our [OrderLine] parameter [orderLine].
 *  - `decreaseItemCount`: A lambda function that calls our lambda parameter [decreaseItemCount] with
 *  the [Snack.id] of our [Snack] variable `snack`.
 *  - `increaseItemCount`: A lambda function that calls our lambda parameter [increaseItemCount] with
 *  the [Snack.id] of our [Snack] variable `snack`.
 *  - `modifier`: The [Modifier] of the [QuantitySelector] is a [ConstraintLayoutScope.constrainAs]
 *  that sets the [ConstrainedLayoutReference] `ref` to the [ConstrainedLayoutReference] variable
 *  `quantity` and in its [ConstrainScope] `constrainBlock` lambda argument we link the `baseline`
 *  to the [ConstrainedLayoutReference.baseline] of the [ConstrainedLayoutReference] variable `price`,
 *  and the `end` to the [ConstrainedLayoutReference.end] of our `parent`.
 *
 * Finally in the [ConstraintLayout] we compose a [JetsnackDivider] whose `modifier` argument is
 * a [ConstraintLayoutScope.constrainAs] that sets the [ConstrainedLayoutReference] `ref` to the
 * [ConstrainedLayoutReference] variable `divider` and in its [ConstrainScope] `constrainBlock`
 * lambda argument uses the [ConstrainScope.linkTo] method to link the `start` to the parent `start`
 * and the `end` to the parent `end`, then links the `top` to the parent `bottom`.
 *
 * @param orderLine The [OrderLine] representing the snack and its quantity in the cart.
 * @param removeSnack A lambda function to be called when the user wants to remove the snack from
 * the cart. It receives the [Snack.id] of the snack to be removed.
 * @param increaseItemCount A lambda function to be called when the user wants to increase the
 * quantity of the snack. It receives the [Snack.id] of the snack to be increased.
 * @param decreaseItemCount A lambda function to be called when the user wants to decrease the
 * quantity of the snack. It receives the [Snack.id] of the snack to be decreased.
 * @param onSnackClick A lambda function to be called when the user clicks on the snack. It
 * receives the [Snack.id] of the snack clicked.
 * @param modifier The [Modifier] to be applied to the layout.
 */
@Composable
fun CartItem(
    orderLine: OrderLine,
    removeSnack: (Long) -> Unit,
    increaseItemCount: (Long) -> Unit,
    decreaseItemCount: (Long) -> Unit,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * The [Snack] of the [OrderLine] parameter [orderLine].
     */
    val snack: Snack = orderLine.snack
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id) }
            .background(color = JetsnackTheme.colors.uiBackground)
            .padding(horizontal = 24.dp)

    ) {
        /**
         * The [ConstrainedLayoutReference]'s of the [ConstraintLayout] composable.
         */
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

/**
 * A composable function that displays the summary of the cart.
 *
 * Our root composable function is a [Column] whose `modifier` argument is our [Modifier] parameter
 * [modifier]. In the [ColumnScope] `content` composable lambda argument we compose:
 *
 * A [Text] whose arguments are:
 *  - `text`: The [String] with resource ID `R.string.cart_summary_header` ("Summary").
 *  - `style`: The [TextStyle] of the text is the [Typography.h6] of our custom
 *  [MaterialTheme.typography].
 *  - `color`: The [Color] of the text is the [JetsnackColors.brand] of our custom
 *  [JetsnackTheme.colors].
 *  - `maxLines`: The number of lines of text to display is `1`.
 *  - `overflow`: The text overflow is [TextOverflow.Ellipsis].
 *  - `modifier`: The [Modifier] of the text is a [Modifier.padding] that adds `24.dp` to each
 *  `horizontal` side, with a [Modifier.heightIn] chained to that that sets the `min` to `56.dp`,
 *  and a [Modifier.wrapContentHeight] chained to that.
 *
 * A [Row] whose `modifier` argument is a [Modifier.padding] that adds `24.dp` to each `horizontal`
 * side. In the [RowScope] `content` composable lambda argument we compose:
 *
 * **First** A [Text] whose arguments are:
 *  - `text`: The [String] with resource ID `R.string.cart_subtotal_label` ("Subtotal").
 *  - `style`: The [TextStyle] of the text is the [Typography.body1] of our custom
 *  [MaterialTheme.typography].
 *  - `modifier`: The [Modifier] of the text is a [RowScope.weight] whose `weight` is set to
 *  `1f`, with a [Modifier.wrapContentWidth] chained to that that sets the `align` to [Alignment.Start]
 *  and a [RowScope.alignBy] chained to that that sets the `alignmentLine` to [LastBaseline].
 *
 * **Second** A [Text] whose arguments are:
 *  - `text`: The formatted `price` of our [Long] parameter [subtotal] returned by the [formatPrice]
 *  method.
 *  - `style`: The [TextStyle] of the text is the [Typography.body1] of our custom
 *  [MaterialTheme.typography].
 *  - `modifier`: The [Modifier] of the text is a [RowScope.alignBy] that sets the `alignmentLine`
 *  to [LastBaseline].
 *
 * Below that [Row] in the [Column] we compose another [Row] whose `modifier` argument is a
 * [Modifier.padding] that adds `24.dp` to each `horizontal` side, and 8.dp to each `vertical` side.
 * In the [RowScope] `content` composable lambda argument we compose:
 *
 * **First** A [Text] whose arguments are:
 *  - `text`: The [String] with resource ID `R.string.cart_shipping_label` ("Shipping & Handling").
 *  - `style`: The [TextStyle] of the text is the [Typography.body1] of our custom
 *  [MaterialTheme.typography].
 *  - `modifier`: The [Modifier] of the text is a [RowScope.weight] whose `weight` is set to
 *  `1f`, with a [Modifier.wrapContentWidth] chained to that that sets the `align` to [Alignment.Start]
 *  and a [RowScope.alignBy] chained to that that sets the `alignmentLine` to [LastBaseline].
 *
 * **Second** A [Text] whose arguments are:
 *  - `text`: The formatted `price` of our [Long] parameter [shippingCosts] returned by the
 *  [formatPrice] method.
 *  - `style`: The [TextStyle] of the text is the [Typography.body1] of our custom
 *  [MaterialTheme.typography].
 *  - `modifier`: The [Modifier] of the text is a [RowScope.alignBy] that sets the `alignmentLine`
 *  to [LastBaseline].
 *
 * A [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` is set to `8.dp`.
 *
 * A [JetsnackDivider].
 *
 * A [Row] whose `modifier` argument is a [Modifier.padding] that adds `24.dp` to each `horizontal`
 * side, and 8.dp to each `vertical` side. In the [RowScope] `content` composable lambda argument
 * of the [Row] we compose:
 *
 * **First** A [Text] whose arguments are:
 *  - `text`: is the [String] with resource ID `R.string.cart_total_label` ("Total").
 *  - `style`: The [TextStyle] of the text is the [Typography.body1] of our custom
 *  [MaterialTheme.typography].
 *  - `modifier`: The [Modifier] of the text is a [RowScope.weight] whose `weight` is set to
 *  `1f`, with a [Modifier.padding] chained to that that adds `16.dp` to the `end`, with a
 *  [Modifier.wrapContentWidth] chained to that that sets the `align` to [Alignment.End], and
 *  a [RowScope.alignBy] chained to that that sets the `alignmentLine` to [LastBaseline].
 *
 * **Second** A [Text] whose arguments are:
 *  - `text`: The formatted `price` of our [Long] parameter [subtotal] plus our [Long] parameter
 *  [shippingCosts] returned by the [formatPrice] method.
 *  - `style`: The [TextStyle] of the text is the [Typography.subtitle1] of our custom
 *  [MaterialTheme.typography].
 *  - `modifier`: The [Modifier] of the text is a [RowScope.alignBy] that sets the `alignmentLine`
 *  to [LastBaseline].
 *
 * A [JetsnackDivider].
 *
 * @param subtotal The subtotal of the cart.
 * @param shippingCosts The shipping costs of the cart.
 * @param modifier The [Modifier] to be applied to the layout.
 */
@Composable
fun SummaryItem(
    subtotal: Long,
    shippingCosts: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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

/**
 * A composable function that displays the checkout bar at the bottom of the cart screen.
 * This bar includes a divider and a checkout button.
 *
 * Our root composable function is a [Column] whose `modifier` argument chains to our [Modifier]
 * parameter [modifier] a [Modifier.background] whose [Color] is the [JetsnackColors.uiBackground]
 * of our custom [JetsnackTheme.colors].
 *
 * In the [ColumnScope] `content` composable lambda argument we compose:
 *
 * A [JetsnackDivider].
 *
 * A [Row] in whose [RowScope] `content` composable lambda argument we compose:
 *
 * **First** A [Spacer] whose `modifier` argument is a [RowScope.weight] whose `weight` is set
 * to `1f`.
 *
 * **Second** A [JetsnackButton] whose arguments are:
 *  - `onClick`: A lambda function that is empty and does nothing at the moment.
 *  - `shape`: The [Shape] of the button is a [RectangleShape].
 *  - `modifier`: The [Modifier] of the button is a [Modifier.padding] that adds `12.dp` to
 *  each `horizontal` side and `8.dp` to each `vertical` side, with a [RowScope.weight] chained
 *  to that that sets the `weight` to `1f`.
 *
 * In the [RowScope] `content` composable lambda argument of the [IconButton] we compose a [Text]
 * whose arguments are:
 *  - `text`: The [String] with resource ID `R.string.cart_checkout` ("Checkout").
 *  - `modifier`: The [Modifier] of the text is a [Modifier.fillMaxWidth].
 *  - `textAlign`: The [TextAlign] of the text is [TextAlign.Left].
 *  - `maxLines` is `1`.
 *
 * @param modifier Modifier to be applied to the layout. Defaults to an empty Modifier.
 */
@Composable
private fun CheckoutBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(
            color = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque)
        )
    ) {

        JetsnackDivider()
        Row {
            Spacer(modifier = Modifier.weight(weight = 1f))
            JetsnackButton(
                onClick = { /* Checkout */ },
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

/**
 * Three preview composables for the [Cart] screen:
 *  - "default": The default preview.
 *  - "dark theme": The dark theme preview.
 *  - "large font": The large font preview.
 */
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
