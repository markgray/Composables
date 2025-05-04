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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.Filter
import com.example.baselineprofiles_codelab.model.SnackRepo
import com.example.baselineprofiles_codelab.ui.components.FilterChip
import com.example.baselineprofiles_codelab.ui.components.JetsnackScaffold
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors

/**
 * A composable function that displays a filter screen within a dialog.
 *
 * This screen allows users to filter and sort a list of snacks based on various criteria.
 * It includes options for sorting, filtering by price, category, and lifestyle, and setting
 * a maximum calorie limit.
 *
 * We start by initializing and remembering our [MutableState] wrapped [String] variable `sortState`
 * to the value returned by [SnackRepo.getSortDefault]. Then we initialize and remember our
 * [MutableFloatState] wrapped [Float] variable `maxCalories` with an initial value of `0f`.
 * We initialize our [String] variable `defaultFilter` with the value returned by
 * [SnackRepo.getSortDefault].
 *
 * Our root composable is a [Dialog] whose `onDismissRequest` argument is our lambda parameter
 * `onDismiss`. In its `content` composable lambda argument we initialize and remember our
 * [List] of [Filter] variable `priceFilters` with the value returned by [SnackRepo.getPriceFilters],
 * initialize and remember our [List] of [Filter] variable `categoryFilters` with the value
 * returned by [SnackRepo.getCategoryFilters], and initialize and remember our [List] of
 * [Filter] variable `lifeStyleFilters` with the value returned by [SnackRepo.getLifeStyleFilters].
 *
 * Then we compose a [JetsnackScaffold] with its `topBar` argument a [TopAppBar] whose arguments are:
 *  - `navigationIcon` a [IconButton] whose `onClick` argument is a lambda parameter `onDismiss`
 *  - `title` a [Text] whose `text` argument is a string resource with id `R.string.label_filters``
 *  whose `modifier` argument is a [Modifier.fillMaxWidth], whose `textAlign` argument is
 *  [TextAlign.Center], and whose [TextStyle] `style` argument is the [Typography.h6] of our custom
 *  [MaterialTheme.typography].
 *  - `actions` a lambda in which we initialize our [Boolean] variable `resetEnabled` to `true` if
 *  our [String] variable `sortState` is not equal to our [String] variable `defaultFilter`. Then we
 *  compose a [IconButton] whose `onClick` argument is an empty lambda and whose `enabled` argument
 *  our [Boolean] variable `resetEnabled`. In its `content` lambda argument we initialize our [Float]
 *  variable `alpha` to `ContentAlpha.high` if our [Boolean] variable `resetEnabled` is `true` or
 *  `ContentAlpha.disabled` if our [Boolean] variable `resetEnabled` is `false`. Then we compose a
 *  [CompositionLocalProvider] that provides `alpha` as the [LocalContentAlpha] to its `content`
 *  composable lambda argument. In its `content` composable lambda argument we compose a [Text]
 *  whose `text` argument is a string resource with id `R.string.reset` ("Reset") and whose
 *  [TextStyle] `style` argument is the [Typography.body2] of our custom [MaterialTheme.typography].
 *  - `backgroundColor` is the [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors].
 *
 * In the `content` composable lambda taking a [PaddingValues] argument of the [JetsnackScaffold] we
 * compose a [Column] whose `modifier` argument is a [Modifier.fillMaxSize], with a
 * [Modifier.verticalScroll] chained to that, and a [Modifier.padding] that adds `24.dp` to each
 * `horizontal` side and `16.dp` to each `vertical` side. In its [ColumnScope] `content` lambda
 * argument we compose:
 * 
 * **First** A [SortFiltersSection] whose arguments are:
 *  - `sortState`: Our [MutableState] wrapped [String] variable `sortState`.
 *  - `onFilterChange`: A lambda that accepts the [Filter] parameter in variable `filter` and
 *  then sets our [MutableState] wrapped [String] variable `sortState` to the [Filter.name]
 *  of the [Filter] variable `filter`.
 *
 * **Second** A [FilterChipSection] whose arguments are:
 *  - `title`: A string resource with id `R.string.price` ("Price").
 *  - `filters`: Our [List] of [Filter] variable `priceFilters`.
 *
 * **Third** A [FilterChipSection] whose arguments are:
 *  - `title`: A string resource with id `R.string.category` ("Category").
 *  - `filters`: Our [List] of [Filter] variable `categoryFilters`.
 *
 * **Fourth** A [MaxCalories] whose arguments are:
 *  - `sliderPosition`: Our [MutableFloatState] wrapped [Float] variable `maxCalories`.
 *  - `onValueChanged`: A lambda that accepts the [Float] parameter in variable `newValue` and
 *  then sets our [MutableFloatState] wrapped [Float] variable `maxCalories` to the [Float]
 *  variable `newValue`.
 *
 * **Fifth** A [FilterChipSection] whose arguments are:
 *  - `title`: A string resource with id `R.string.lifestyle` ("Lifestyle").
 *  - `filters`: Our [List] of [Filter] variable `lifeStyleFilters`.
 *
 * @param onDismiss A lambda function to be invoked when the dialog is dismissed.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FilterScreen(
    onDismiss: () -> Unit
) {
    var sortState: String by remember { mutableStateOf(SnackRepo.getSortDefault()) }
    var maxCalories: Float by remember { mutableFloatStateOf(0f) }
    val defaultFilter: String = SnackRepo.getSortDefault()

    Dialog(onDismissRequest = onDismiss) {

        val priceFilters: List<Filter> = remember { SnackRepo.getPriceFilters() }
        val categoryFilters: List<Filter> = remember { SnackRepo.getCategoryFilters() }
        val lifeStyleFilters: List<Filter> = remember { SnackRepo.getLifeStyleFilters() }
        JetsnackScaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(id = R.string.close)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(id = R.string.label_filters),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h6
                        )
                    },
                    actions = {
                        var resetEnabled: Boolean = sortState != defaultFilter
                        IconButton(
                            onClick = { /* TODO: Open search */ },
                            enabled = resetEnabled
                        ) {
                            val alpha: Float = if (resetEnabled) {
                                ContentAlpha.high
                            } else {
                                ContentAlpha.disabled
                            }
                            CompositionLocalProvider(LocalContentAlpha provides alpha) {
                                Text(
                                    text = stringResource(id = R.string.reset),
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    },
                    backgroundColor = JetsnackTheme.colors.uiBackground
                )
            }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                SortFiltersSection(
                    sortState = sortState,
                    onFilterChange = { filter: Filter ->
                        sortState = filter.name
                    }
                )
                FilterChipSection(
                    title = stringResource(id = R.string.price),
                    filters = priceFilters
                )
                FilterChipSection(
                    title = stringResource(id = R.string.category),
                    filters = categoryFilters
                )

                MaxCalories(
                    sliderPosition = maxCalories,
                    onValueChanged = { newValue: Float ->
                        maxCalories = newValue
                    }
                )
                FilterChipSection(
                    title = stringResource(id = R.string.lifestyle),
                    filters = lifeStyleFilters
                )
            }
        }
    }
}

/**
 * Displays a section of filter chips with a title.
 *
 * This composable arranges a list of [Filter] objects as interactive chips within a [FlowRow].
 * Each chip is displayed with its associated label and can be interacted with. The section
 * is also given a title to help organize the filtering options.
 *
 * TODO: Continue here.
 *
 * @param title The title of the filter section. This will be displayed above the filter chips.
 * @param filters A list of [Filter] objects to be displayed as chips. Each [Filter] should
 * contain the necessary data to display the chip.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipSection(title: String, filters: List<Filter>) {
    FilterTitle(text = title)
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 16.dp)
            .padding(horizontal = 4.dp)
    ) {
        filters.forEach { filter: Filter ->
            FilterChip(
                filter = filter,
                modifier = Modifier.padding(end = 4.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun SortFiltersSection(sortState: String, onFilterChange: (Filter) -> Unit) {
    FilterTitle(text = stringResource(id = R.string.sort))
    Column(Modifier.padding(bottom = 24.dp)) {
        SortFilters(
            sortState = sortState,
            onChanged = onFilterChange
        )
    }
}

@Composable
fun SortFilters(
    sortFilters: List<Filter> = SnackRepo.getSortFilters(),
    sortState: String,
    onChanged: (Filter) -> Unit
) {

    sortFilters.forEach { filter: Filter ->
        SortOption(
            text = filter.name,
            icon = filter.icon,
            selected = sortState == filter.name,
            onClickOption = {
                onChanged(filter)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MaxCalories(sliderPosition: Float, onValueChanged: (Float) -> Unit) {
    FlowRow {
        FilterTitle(text = stringResource(id = R.string.max_calories))
        Text(
            text = stringResource(id = R.string.per_serving),
            style = MaterialTheme.typography.body2,
            color = JetsnackTheme.colors.brand,
            modifier = Modifier.padding(top = 5.dp, start = 10.dp)
        )
    }
    Slider(
        value = sliderPosition,
        onValueChange = { newValue: Float ->
            onValueChanged(newValue)
        },
        valueRange = 0f..300f,
        steps = 5,
        modifier = Modifier
            .fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = JetsnackTheme.colors.brand,
            activeTrackColor = JetsnackTheme.colors.brand
        )
    )
}

@Composable
fun FilterTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        color = JetsnackTheme.colors.brand,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
@Composable
fun SortOption(
    text: String,
    icon: ImageVector?,
    onClickOption: () -> Unit,
    selected: Boolean
) {
    Row(
        modifier = Modifier
            .padding(top = 14.dp)
            .selectable(selected = selected) { onClickOption() }
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(weight = 1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                tint = JetsnackTheme.colors.brand
            )
        }
    }
}

/**
 * Preview of [FilterScreen].
 */
@Preview("filter screen")
@Composable
fun FilterScreenPreview() {
    FilterScreen(onDismiss = {})
}
