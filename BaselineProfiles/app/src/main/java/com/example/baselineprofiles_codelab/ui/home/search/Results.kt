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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.Filter
import com.example.baselineprofiles_codelab.model.Snack
import com.example.baselineprofiles_codelab.model.snacks
import com.example.baselineprofiles_codelab.ui.components.FilterBar
import com.example.baselineprofiles_codelab.ui.components.JetsnackButton
import com.example.baselineprofiles_codelab.ui.components.JetsnackDivider
import com.example.baselineprofiles_codelab.ui.components.JetsnackSurface
import com.example.baselineprofiles_codelab.ui.components.SnackImage
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
 * TODO: Continue here
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
                Modifier.constrainAs(ref = divider) {
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
            painterResource(id = R.drawable.empty_state_search),
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
