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

package com.example.baselineprofiles_codelab.ui.home

import android.content.res.Configuration
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.example.baselineprofiles_codelab.model.Filter
import com.example.baselineprofiles_codelab.model.Snack
import com.example.baselineprofiles_codelab.model.SnackCollection
import com.example.baselineprofiles_codelab.model.SnackRepo
import com.example.baselineprofiles_codelab.ui.components.FilterBar
import com.example.baselineprofiles_codelab.ui.components.JetsnackDivider
import com.example.baselineprofiles_codelab.ui.components.JetsnackSurface
import com.example.baselineprofiles_codelab.ui.components.SnackCollection
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

/**
 * Displays the main feed of snack collections.
 *
 * This composable Simulates loading data asynchronously and displays it in a list. It also provides
 * filter options.
 *
 * We start by initializing and remembering our [List] of [SnackCollection] variable `snackCollections`
 * to a new instance. Then we compose a [LaunchedEffect] whose `key1` argument is [Unit] (to guarantee
 * that it only runs once). In its [CoroutineScope] `block` lambda argument we use [trace] to name
 * this `sectionName` "Snacks loading" and in its `block` lambda argument we delay for 300ms, then we
 * set `snackCollections` to the [List] of [SnackCollection] returned by [SnackRepo.getSnacks] (we do
 * this to simulate asynchronous data loading).
 *
 * Next we initialize and remember our [List] of [Filter] variable `filters` to the [List] of [Filter]
 * returned by [SnackRepo.getFilters].
 *
 * Our root composable is the stateless override of this [Feed] composable whose arguments are:
 *  - `snackCollections`: Our [List] of [SnackCollection] variable `snackCollections`.
 *  - `filters`: Our [List] of [Filter] variable `filters`.
 *  - `onSnackClick`: Our lambda parameter [onSnackClick]
 *  - `modifier`: Our [Modifier] parameter [modifier]
 *
 * @param onSnackClick A lambda that is invoked when a snack item is clicked. It receives the
 * [Snack.id] as its argument.
 * @param modifier [Modifier] for styling and layout of the feed.
 */
@Composable
fun Feed(
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Simulate loading data asynchronously.
    // In real world application, you shouldn't have this kind of logic in your UI code,
    // but you should move it to appropriate layer.
    var snackCollections: List<SnackCollection> by remember { mutableStateOf(listOf()) }
    LaunchedEffect(key1 = Unit) {
        trace(sectionName = "Snacks loading") {
            delay(timeMillis = 300)
            snackCollections = SnackRepo.getSnacks()
        }
    }

    val filters: List<Filter> = remember { SnackRepo.getFilters() }
    Feed(
        snackCollections = snackCollections,
        filters = filters,
        onSnackClick = onSnackClick,
        modifier = modifier
    )
}

/**
 * Stateless override of [Feed] which is called by the Stateful override. Displays the main feed
 * screen, showing a list of snack collections and filters.
 *
 * Our root composable is a [JetsnackSurface] whose `modifier` argument chains to our [Modifier]
 * parameter [modifier] a [Modifier.fillMaxSize]. In its `content` lambda argument we compose a
 * [Box] in whose [BoxScope] `content` lambda argument we compose:
 *
 * **First** A [SnackCollection] whose arguments are:
 *  - `snackCollections`: Our [List] of [SnackCollection] parameter [snackCollections].
 *  - `filters`: Our [List] of [Filter] parameter [filters].
 *  - `onSnackClick`: Our lambda parameter [onSnackClick]
 *
 * **Second** A [DestinationBar] is then displayed on top of the [SnackCollectionList].
 *
 * @param snackCollections The list of [SnackCollection] to display. Each collection
 * represents a group of snacks (e.g., "Favorites," "Trending").
 * @param filters The list of [Filter] to display for filtering snacks.
 * These filters allow users to narrow down the displayed snack collections.
 * @param onSnackClick A callback function invoked when a snack is clicked.
 * It receives the [Snack.id] of the clicked snack as its argument.
 * @param modifier [Modifier] to apply to the feed surface.
 */
@Composable
private fun Feed(
    snackCollections: List<SnackCollection>,
    filters: List<Filter>,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Box {
            SnackCollectionList(
                snackCollections = snackCollections,
                filters = filters,
                onSnackClick = onSnackClick
            )
            DestinationBar()
        }
    }
}

/**
 * Displays a list of [SnackCollection]s.
 *
 * We start by initializing and `rememberSaveable` our [Boolean] variable `filtersVisible` to `false`.
 * Then our root composable is a [Box] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In its [BoxScope] `content` lambda argument we compose a [LazyColumn] whose `modifier` argument
 * is a [Modifier.testTag] with a value of "snack_list". In the [LazyListScope] `content` lambda
 * argument of the [LazyColumn] we compose:
 *
 * **First** A [LazyListScope.item] whose [LazyItemScope] `content` lambda argument contains a
 * [Spacer] whose `modifier` argument is a [Modifier.windowInsetsTopHeight] with a value of
 * [WindowInsets.Companion.statusBars] (to avoid the status bar) plus a [WindowInsets] that adds an
 * additional top padding of `56.dp` (the height of the [DestinationBar]). Then we compose a
 * [FilterBar] whose arguments are:
 *  - `filters`: Our [List] of [Filter] parameter [filters].
 *  - `onShowFilters`: A lambda that sets our [Boolean] variable `filtersVisible` to `true`.
 *
 * **Second** If our [List] of [SnackCollection] parameter [snackCollections] is empty we compose
 * a [LazyListScope.item] whose [LazyItemScope] `content` lambda argument contains a
 * [Box] whose `modifier` argument is a [LazyItemScope.fillParentMaxWidth] with a
 * [LazyItemScope.fillParentMaxHeight] whose `fraction` is `0.75f`, and whose `contentAlignment`
 * argument is [Alignment.Center]. In its [BoxScope] `content` lambda argument we compose a
 * [CircularProgressIndicator] whose [Color] `color` argument is the [JetsnackColors.brand] of our
 * custom [JetsnackTheme.colors]
 *
 * If our [List] of [SnackCollection] parameter [snackCollections] is not empty we compose a
 * [LazyListScope.itemsIndexed] whose `items` argument is our [List] of [SnackCollection] parameter
 * [snackCollections]. In its [LazyItemScope] `itemContent` lambda argument we capture the [Int]
 * passed the lambda in variable `index` and the [SnackCollection] passed the lambda in variable
 * `snackCollection`. Then if `index` is greater than `0` we compose a [JetsnackDivider] whose
 * `thickness` argument is `2.dp`. In any case we then compose a [SnackCollection] whose
 * arguments are:
 *  - `snackCollection`: Our [SnackCollection] variable `snackCollection`.
 *  - `onSnackClick`: Our lambda parameter [onSnackClick]
 *  - `index`: Our [Int] variable `index`.
 *  - `modifier`: Our [Modifier.testTag] with a `tag` of "snack_collection".
 *
 * **Third** We then compose an [AnimatedVisibility] to appear on top of the [Box] when our [Boolean]
 * variable `filtersVisible` is `true`. Its `visible` argument is our [Boolean] variable
 * `filtersVisible`, its `enter` argument is a [slideInVertically] plus a [expandVertically]
 * whose `expandFrom` argument is [Alignment.Top] plus a [fadeIn] whose `initialAlpha` argument is
 * `0.3f`, and its `exit` argument is a [slideOutVertically] plus a [shrinkVertically] plus a
 * [fadeOut]. In its [AnimatedVisibilityScope] `content` lambda argument we compose a [FilterScreen]
 * whose `onDismiss` argument is a lambda that sets our [Boolean] variable `filtersVisible` to
 * `false`.
 *
 * **Fourth** We then compose a [ReportDrawnWhen] whose `predicate` argument is a lambda that returns
 * `true` if our [List] of [SnackCollection] parameter [snackCollections] is not empty. This adds
 * the `predicate` as a condition for when the UI is usable by user (`Activity.reportFullyDrawn`
 * will not be called before this condition is met).
 *
 * @param snackCollections The list of [SnackCollection] to display.
 * @param filters The list of [Filter] to display for filtering snacks.
 * These filters allow users to narrow down the displayed snack collections.
 * @param onSnackClick A callback function invoked when a snack is clicked.
 * It receives the [Snack.id] of the clicked snack as its argument.
 * @param modifier [Modifier] to apply our root composable.
 */
@Composable
private fun SnackCollectionList(
    snackCollections: List<SnackCollection>,
    filters: List<Filter>,
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var filtersVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.testTag(tag = "snack_list"),
        ) {
            item {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(
                        WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                    )
                )
                FilterBar(filters = filters, onShowFilters = { filtersVisible = true })
            }

            if (snackCollections.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .fillParentMaxHeight(fraction = 0.75f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = JetsnackTheme.colors.brand)
                    }
                }
            } else {
                itemsIndexed(items = snackCollections) {
                    index: Int,
                    snackCollection: SnackCollection ->
                    if (index > 0) {
                        JetsnackDivider(thickness = 2.dp)
                    }

                    SnackCollection(
                        snackCollection = snackCollection,
                        onSnackClick = onSnackClick,
                        index = index,
                        modifier = Modifier.testTag(tag = "snack_collection")
                    )
                }
            }
        }
    }
    AnimatedVisibility(
        visible = filtersVisible,
        enter = slideInVertically() + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        FilterScreen(
            onDismiss = { filtersVisible = false }
        )
    }

    // Reports when the UI is usable by user
    ReportDrawnWhen { snackCollections.isNotEmpty() }
}

/**
 * Three previews of the [Feed] with different configurations:
 *  - "default": light theme
 *  - "dark theme": dark theme
 *  - "large font": large font size
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun HomePreview() {
    JetsnackTheme {
        Feed(
            onSnackClick = { },
            snackCollections = SnackRepo.getSnacks(),
            filters = SnackRepo.getFilters()
        )
    }
}
