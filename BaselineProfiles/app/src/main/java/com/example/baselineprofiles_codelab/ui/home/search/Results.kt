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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.Filter
import com.example.baselineprofiles_codelab.model.Snack
import com.example.baselineprofiles_codelab.model.snacks
import com.example.baselineprofiles_codelab.ui.components.FilterBar
import com.example.baselineprofiles_codelab.ui.components.JetsnackButton
import com.example.baselineprofiles_codelab.ui.components.JetsnackDivider
import com.example.baselineprofiles_codelab.ui.components.JetsnackSurface
import com.example.baselineprofiles_codelab.ui.components.SnackImage
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import com.example.baselineprofiles_codelab.ui.utils.formatPrice

/**
 * Displays the search results for snacks based on the given [searchResults] and [filters].
 *
 * This composable function displays a list of search results, including:
 *  - A [FilterBar] to show the active filters.
 *  - A text indicating the number of search results found.
 *  - A [LazyColumn] to display the list of [Snack] items, each rendered by the
 *  [SearchResult] composable.
 *  - Each snack Item has a click Listener implemented via [onSnackClick]
 *  - Dividers between snack items to improve readability.
 *
 * Our root composable is a [Column], and in its [ColumnScope] `content` composable lambda argument
 * we compose:
 *
 * **First** A [FilterBar], whose `filter` argument is our [List] of [Filter] parameter [filters],
 * and whose `onShowFilters` argument is a do-nothing lambda.
 *
 * **Second** A [Text] composable, whose arguments are:
 *  - `text`: is the [String] formatted using the format [String] whose resource ID is
 *  `R.string.search_count` from the [List.size] of our [List] of [Snack] parameter [searchResults]
 *  - `style`: [TextStyle] is the [Typography.h6] of our custom [MaterialTheme.typography].
 *  - `color`: [Color] is the [JetsnackColors.textPrimary] of our custom [JetsnackTheme.colors].
 *  - `modifier`: is a [Modifier.padding] that adds `24.dp` to each `horizontal` size, and `4.dp` to
 *  each `vertical` size.
 *
 * **Third** A [LazyColumn] in whose [LazyListScope] `content` composable lambda argument we use
 * the [LazyListScope.itemsIndexed] to iterate over our [List] of [Snack] parameter [searchResults],
 * and in its [LazyItemScope] `itemContent` Composable lambda argument we capture the [Int] passed
 * the lamba in variable `index` and the [Snack] passed in variable `snack` then compose a
 * [SearchResult] whose arguments are:
 *  - `snack`: is the [Snack] variable `snack`
 *  - `onSnackClick`: isour [onSnackClick] taking [Long] parameter [onSnackClick]
 *  - `showDivider`: is `true` if `index` not equal to `0`
 *
 * @param searchResults The list of [Snack] objects representing the search results.
 * @param filters The list of [Filter] objects applied to the search results.
 * @param onSnackClick A callback function that is invoked when a snack item is clicked.
 * It receives the [Snack.id] of the clicked snack as its argument.
 */
@Composable
fun SearchResults(
    searchResults: List<Snack>,
    filters: List<Filter>,
    onSnackClick: (Long) -> Unit
) {
    Column {
        FilterBar(filters = filters, onShowFilters = {})
        Text(
            text = stringResource(R.string.search_count, searchResults.size),
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        LazyColumn {
            itemsIndexed(items = searchResults) { index: Int, snack: Snack ->
                SearchResult(snack = snack, onSnackClick = onSnackClick, showDivider = index != 0)
            }
        }
    }
}

/**
 * Displays a single snack item in a search result list.
 *
 * This composable shows the snack's image, name, tagline, and price,
 * along with an "Add" button to add it to the cart. It also optionally
 * includes a divider at the top of the item.
 *
 * Our root composable is a [ConstraintLayout] whose `modifier` argument is a [Modifier.fillMaxWidth]
 * with a [Modifier.clickable] chained to that whose `onClick` argument is a lambda that calls our
 * [onSnackClick] lambda parameter with the [Snack.id] of our [Snack] parameter [snack], and with a
 * [Modifier.padding] that adds `24.dp` to each `horizontal` side. In its [ConstraintLayoutScope]
 * `content` composable lambda argument we first initalize our [ConstrainedLayoutReference] variables
 * `divider`, `image`, `name`, `tag`, `priceSpacer`, `price`, and `add` to the instances returned
 * by the [ConstraintLayoutScope.createRefs] method of our [ConstraintLayout] composable, then call
 * the [ConstraintLayoutScope.createVerticalChain] method to create a vertical chain between the
 * references `name`, `tag`, `priceSpacer`, and `price` with a `chainStyle` of [ChainStyle.Packed].
 *
 * If our [Boolean] parameter [showDivider] is `true`, we compose a [JetsnackDivider] composable
 * with its `modifier` argument a [ConstraintLayoutScope.constrainAs] that sets its `ref` to
 * `divider`, and in its [ConstrainScope] `constrainBlock` lambda argument we use the method
 * [ConstrainScope.linkTo] to link its `start` to the parent `start`, its `end` to the parent `end`,
 * and then use `top.linkTo` to link its `top` to the parent `top`.
 *
 * We then compose a [SnackImage] composable with the arguments:
 *  - `imageUrl`: is the [Snack.imageUrl] of our [Snack] parameter [snack]
 *  - `contentDescription`: is `null`
 *  - `modifier`: is a [Modifier.size] that sets the size to `100.dp`, with a
 *  [ConstraintLayoutScope.constrainAs] chained to that that sets its `ref` to `image`, and in its
 *  [ConstrainScope] `constrainBlock` lambda argument we use the method [ConstrainScope.linkTo] to
 *  link its `top` to the parent `top`, with a `topMargin` to `16.dp`, its `bottom` to the parent
 *  `bottom`, with a `bottomMargin` to `16.dp`, then use `start.linkTo` to link its `start` to the
 *  parent `start`.
 *
 * We then compose a [Text] composable with the arguments:
 *  - `text`: is the [Snack.name] of our [Snack] parameter [snack]
 *  - `style`: [TextStyle] is the [Typography.subtitle1] of our custom [MaterialTheme.typography]
 *  - `color`: [Color] is the [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors]
 *  - `modifier`: is a [ConstraintLayoutScope.constrainAs] that sets its `ref` to `name`, and in its
 *  [ConstrainScope] `constrainBlock` lambda argument we use the method [ConstrainScope.linkTo] to
 *  link its `start` to the `image` `end`, with a `startMargin` to `16.dp`, its `end` to the `add`
 *  `start`, with a `endMargin` to `16.dp`, and its `bias` is `0f`.
 *
 * We then compose a [Text] composable with the arguments:
 *  - `text`: is the [Snack.tagline] of our [Snack] parameter [snack]
 *  - `style`: [TextStyle] is the [Typography.body1] of our custom [MaterialTheme.typography]
 *  - `color`: [Color] is the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors]
 *  - `modifier`: is a [ConstraintLayoutScope.constrainAs] that sets its `ref` to `tag`, and in its
 *  [ConstrainScope] `constrainBlock` lambda argument we use the method [ConstrainScope.linkTo] to
 *  link its `start` to the `image` `end`, with a `startMargin` to `16.dp`, its `end` to the `add`
 *  `start`, with a `endMargin` to `16.dp`, and its `bias` is `0f`.
 *
 * We then compose a [Spacer] composable whose `modifier` argument is a [Modifier.height] that
 * sets its `height` to `8.dp`, with a [ConstraintLayoutScope.constrainAs] chained to that
 * that sets its `ref` to `priceSpacer`, and in its [ConstrainScope] `constrainBlock` lambda
 * argument we use the method [ConstrainScope.linkTo] to link its `top` to the `tag` `bottom`,
 * and its `bottom` to the `price` `top`.
 *
 * We then compose a [Text] composable with the arguments:
 *  - `text`: is the formatted price of our [Snack] parameter [snack] that the [formatPrice]
 *  creates from the [Snack.price] of our [Snack] parameter [snack]
 *  - `style`: [TextStyle] is the [Typography.subtitle1] of our custom [MaterialTheme.typography]
 *  - `color`: [Color] is the [JetsnackColors.textPrimary] of our custom [JetsnackTheme.colors]
 *  - `modifier`: is a [ConstraintLayoutScope.constrainAs] that sets its `ref` to `price`, and in
 *  its [ConstrainScope] `constrainBlock` lambda argument we use the method [ConstrainScope.linkTo]
 *  to link its `start` to the `image` `end`, with a `startMargin` of `16.dp`, its `end` to the
 *  `add` `start`, with a `endMargin` of `16.dp`, and its `bias` is `0f`.
 *
 * We then compose a [JetsnackButton] composable with the arguments:
 *  - `onClick`: is a lambda that does nothing
 *  - `shape`: is a [CircleShape]
 *  - `contentPadding`: is a [PaddingValues] that adds `0.dp` to `all` sides.
 *  - `modifier`: is a [Modifier.size] that sets the size to `36.dp`, with a
 *  [ConstraintLayoutScope.constrainAs] chained to that that sets its `ref` to `add`, and in its
 *  [ConstrainScope] `constrainBlock` lambda argument we use the method [ConstrainScope.linkTo] to
 *  link its `top` to the parent `top`, its `bottom` to the parent `bottom`, and then use `end.linkTo`
 *  to link its `end` to the parent `end`.
 *  - In its [RowScope] `content` composable lambda argument we compose an [Icon] whose `imageVector`
 *  argument is the [ImageVector] drawn by [Icons.Outlined.Add] and whose `contentDescription`
 *  argument is the [String] with resource ID `Icons.Outlined.Add` ("Add to cart").
 *
 * @param snack The [Snack] data to display.
 * @param onSnackClick Callback to be invoked when the snack item is clicked.
 * It is provided the [Snack.id] of the clicked snack as its argument.
 * @param showDivider [Boolean] indicating whether to show a divider above the snack item.
 * @param modifier [Modifier] for styling and positioning the search result item.
 */
@Composable
private fun SearchResult(
    snack: Snack,
    onSnackClick: (Long) -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack.id) }
            .padding(horizontal = 24.dp)
    ) {
        val (divider: ConstrainedLayoutReference,
            image: ConstrainedLayoutReference,
            name: ConstrainedLayoutReference,
            tag: ConstrainedLayoutReference,
            priceSpacer: ConstrainedLayoutReference,
            price: ConstrainedLayoutReference,
            add: ConstrainedLayoutReference) = createRefs()
        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
        if (showDivider) {
            JetsnackDivider(
                modifier = Modifier.constrainAs(ref = divider) {
                    linkTo(start = parent.start, end = parent.end)
                    top.linkTo(anchor = parent.top)
                }
            )
        }
        SnackImage(
            imageUrl = snack.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(size = 100.dp)
                .constrainAs(ref = image) {
                    linkTo(
                        top = parent.top,
                        topMargin = 16.dp,
                        bottom = parent.bottom,
                        bottomMargin = 16.dp
                    )
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
                    end = add.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.body1,
            color = JetsnackTheme.colors.textHelp,
            modifier = Modifier.constrainAs(ref = tag) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = add.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Spacer(
            modifier = Modifier
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
                    startMargin = 16.dp,
                    end = add.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        JetsnackButton(
            onClick = { /* todo */ },
            shape = CircleShape,
            contentPadding = PaddingValues(all = 0.dp),
            modifier = Modifier
                .size(size = 36.dp)
                .constrainAs(ref = add) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    end.linkTo(anchor = parent.end)
                }
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = stringResource(id = R.string.label_add)
            )
        }
    }
}

/**
 * Displays a "no results" message when a search query yields no matches.
 *
 * This composable shows an image, a message indicating that no results were found for the given
 * query, and a suggestion to try a different search.
 *
 * Our root composable is a [Column] whose arguments are:
 *  - `horizontalAlignment`: is [Alignment.CenterHorizontally]
 *  - `modifier`: is a [Modifier.fillMaxSize] that fills the entire available space in the parent,
 *  with a [Modifier.wrapContentSize] chained to that, and with a [Modifier.padding] that adds
 *  `24.dp` to each `all` sides.
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we compose:
 *
 * **First** A [Image] whose arguments are:
 *  - `painter`: is a [painterResource] that loads the drawable with resource ID
 *  `R.drawable.empty_state_search`
 *  - `contentDescription`: is `null`
 *
 * **Second** A [Spacer] whose `modifier` argument is a [Modifier.height] that sets its `height`
 * to `24.dp`.
 *
 * **Third** A [Text] whose arguments are:
 *  - `text`: is the [String] formatted using the format [String] whose resource ID is
 *  `R.string.search_no_matches` from the given [query] ("No matches for “%1s”")
 *  - `style`: [TextStyle] is the [Typography.subtitle1] of our custom [MaterialTheme.typography]
 *  - `textAlign`: is [TextAlign.Center]
 *  - `modifier`: is a [Modifier.fillMaxWidth].
 *
 * **Fourth** A [Spacer] whose `modifier` argument is a [Modifier.height] that sets its `height`
 * to `16.dp`.
 *
 * **Fifth** A [Text] whose arguments are:
 *  - `text`: is the [String] with resource ID `R.string.search_no_matches_retry`
 *  ("Try broadening your search")
 *  - `style`: [TextStyle] is the [Typography.body2] of our custom [MaterialTheme.typography]
 *  - `textAlign`: is [TextAlign.Center]
 *  - `modifier`: is a [Modifier.fillMaxWidth]
 *
 * @param query The search query that resulted in no matches. This will be displayed in the message.
 * @param modifier [Modifier] to be applied to the layout.
 */
@Composable
fun NoResults(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(all = 24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_state_search),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(height = 24.dp))
        Text(
            text = stringResource(R.string.search_no_matches, query),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        Text(
            text = stringResource(id = R.string.search_no_matches_retry),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Three previews of a [SearchResult] are provided here to show different device settings:
 *  - "default": Light mode
 *  - "dark theme": Dark mode
 *  - "large font": Large font size
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SearchResultPreview() {
    JetsnackTheme {
        JetsnackSurface {
            SearchResult(
                snack = snacks[0],
                onSnackClick = { },
                showDivider = false
            )
        }
    }
}
