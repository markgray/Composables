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
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.example.baselineprofiles_codelab.model.Filter
import com.example.baselineprofiles_codelab.model.SnackCollection
import com.example.baselineprofiles_codelab.model.SnackRepo
import com.example.baselineprofiles_codelab.ui.components.FilterBar
import com.example.baselineprofiles_codelab.ui.components.JetsnackDivider
import com.example.baselineprofiles_codelab.ui.components.JetsnackSurface
import com.example.baselineprofiles_codelab.ui.components.SnackCollection
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import kotlinx.coroutines.delay

/**
 * Displays the main feed of snack collections.
 *
 * This composable Simulates loading data asynchronously and displays it in a list. It also provides
 * filter options.
 *
 * TODO: Continue Here.
 *
 * @param onSnackClick A lambda that is invoked when a snack item is clicked. It receives the snack's ID as a parameter.
 * @param modifier Modifier for styling and layout of the feed.
 */
@Composable
fun Feed(
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Simulate loading data asynchronously.
    // In real world application, you shouldn't have this kind of logic in your UI code,
    // but you should move it to appropriate layer.
    var snackCollections: List<SnackCollection> by remember { mutableStateOf(listOf<SnackCollection>()) }
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
                itemsIndexed(snackCollections) {
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
