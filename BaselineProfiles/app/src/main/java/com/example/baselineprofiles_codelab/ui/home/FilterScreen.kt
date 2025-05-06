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
import androidx.compose.ui.graphics.Color
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
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
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
 *  - `navigationIcon` a [IconButton] whose `onClick` argument is our lambda parameter `onDismiss`,
 *  and whose `content` composable lambda argument composes an [Icon] whose `imageVector` arugment
 *  is the [ImageVector] drawn by [Icons.Filled.Close], and whose `contentDescription` argument is
 *  the [String] with resource ID `R.string.close` ("Close").
 *  - `title` a [Text] whose `text` argument is a string resource with id `R.string.label_filters``
 *  ("Filters") whose `modifier` argument is a [Modifier.fillMaxWidth], whose `textAlign` argument
 *  is [TextAlign.Center], and whose [TextStyle] `style` argument is the [Typography.h6] of our
 *  custom [MaterialTheme.typography].
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
 * We start by composing a [FilterTitle] whose `text` argument is our [String] parameter [title].
 * Then we comopose a [FlowRow] whose `horizontalArrangement` argument is [Arrangement.Center],
 * and whose `modifier` argument is a [Modifier.fillMaxWidth], with a [Modifier.padding] chained to
 * that that adds `12.dp` to the `top` and `16.dp` to the `bottom`, and at the end of the chain
 * is a [Modifier.padding] that adds `4.dp` to each `horizontal` side. In its [FlowRowScope]
 * `content` lambda we use the [Iterable.forEach] method of our [List] of [Filter] parameter
 * [filters] to loop over each [Filter] and in its the `action` lambda argument we capture the
 * [Filter] passed the lambda in variable `filter` and compose a [FilterChip] whose arguments are:
 *  - `filter`: Our [Filter] variable `filter`.
 *  - `modifier`: A [Modifier.padding] that adds `4.dp` to the `end` and `8.dp` to the `bottom` of
 *  the [FilterChip].
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

/**
 * A composable function that displays a section for sorting filters.
 *
 * This section includes a title indicating it's for sorting and a set of
 * sort filter options. It uses a [SortFilters] composable to handle the
 * actual filter selection.
 *
 * We start by composing a [FilterTitle] whose `text` argument is the [String] with resource ID
 * `R.string.sort` ("Sort"). Then we compose a [Column] whose `modifier` argument is a
 * [Modifier.padding] that adds `24.dp` to the `bottom` of the [Column]. In its [ColumnScope]
 * `content` lambda argument we compose a [SortFilters] whose arguments are:
 *  - `sortState`: Our [String] parameter [sortState].
 *  - `onChanged`: Our lambda that accepts a [Filter] object parameter [onFilterChange].
 *
 * @param sortState A string representing the currently selected sort option.
 * This is used to highlight the currently active filter.
 * @param onFilterChange A lambda function that's invoked when the user selects a new sort option.
 * It receives a [Filter] object representing the chosen sort filter.
 */
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

/**
 * Displays a list of sort filter options as selectable buttons.
 *
 * This composable takes a list of [Filter] objects, the current sort state, and a callback function.
 * It iterates through the provided filters and displays each one as a [SortOption] button.
 * The [SortOption] will be marked as selected if its name matches the current [sortState].
 * When a user clicks on a [SortOption], the [onChanged] lambda parameter is called with the
 * [Filter] that the [SortOption] represents.
 *
 * We use the [Iterable.forEach] method of our [List] of [Filter] parameter [sortFilters] to
 * loop over each [Filter] and in its the `action` lambda argument we capture the [Filter]
 * passed the lambda in variable `filter` and compose a [SortOption] whose arguments are:
 *  - `text`: The [Filter.name] of the [Filter] variable `filter`.
 *  - `icon`: The [Filter.icon] of the [Filter] variable `filter`.
 *  - `selected`: `true` if the [Filter.name] of the [Filter] variable `filter` matches
 *  our [Boolean] parameter [sortState].
 *  - `onClickOption`: A lambda that calls our lambda parameter [onChanged] with the [Filter]
 *  variable `filter`.
 *
 * @param sortFilters The [List] of [Filter] objects representing the available sort options.
 * Defaults to the sort filters obtained from [SnackRepo.getSortFilters].
 * @param sortState The name of the currently selected sort filter (e.g., "Price: Low to High").
 * Used to determine which [SortOption] should be marked as selected.
 * @param onChanged A callback function that is called when a `SortOption` is clicked.
 * It receives the selected [Filter] object as its argument.
 */
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

/**
 * A composable function that displays a slider to select the maximum calorie limit.
 *
 * This function renders a title "Max Calories", a subtext "per serving", and a slider
 * that allows the user to select a maximum calorie value between 0 and 300. The slider's
 * position represents the currently selected maximum calorie limit.
 *
 * First we compose a [FlowRow] in whose [FlowRowScope] `content` lambda argument we compose
 * a [FilterTitle] whose `text` argument is the [String] with resource ID `R.string.max_calories`
 * ("Max Calories"). Then we compose a [Text] whose `text` argument is the [String] with
 * resource ID `R.string.per_serving` ("per serving") and whose [TextStyle] `style` argument
 * is the [Typography.body2] of our custom [MaterialTheme.typography], whose [Color] `color`
 * argument is the [JetsnackColors.brand] of our custom [JetsnackTheme.colors], and whose
 * `modifier` argument is a [Modifier.padding] that adds `5.dp` to the `top` and `10.dp` to
 * the `start`.
 *
 * Next we compose a [Slider] whose arguments are:
 *  - `value`: The current position of the slider, representing the selected maximum calorie
 *  value is our [Float] parameter [sliderPosition].
 *  - `onValueChange`: A lambda that accepts the [Float] passed the lambda in variable `newValue`
 *  and calls our lambda parameter [onValueChanged] with the [Float] variable `newValue`.
 *  - `valueRange`: The range of values the slider can be in, from 0f to 300f.
 *  - `steps`: The number of steps between the `valueRange.start` and `valueRange.endInclusive`
 *  is `5`.
 *  - `modifier`: Is a [Modifier.fillMaxWidth].
 *  - `colors`: A [SliderDefaults.colors] whose `thumbColor` is the [JetsnackColors.brand] of
 *  our custom [JetsnackTheme.colors], and whose `activeTrackColor` is the [JetsnackColors.brand]
 *  of our custom [JetsnackTheme.colors].
 *
 * @param sliderPosition The current position of the slider, representing the selected maximum
 * calorie value. This value should be between 0f and 300f.
 * @param onValueChanged A callback function that is invoked when the slider's value changes.
 * It is called with the new slider position as a [Float].
 */
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

/**
 * Displays a title text used in filter sections.
 *
 * This composable function displays a given text as a title, styled according to the Jetsnack
 * theme's brand color and h6 typography. It's designed to be used as a header for sections 
 * containing filter options or similar content. It also includes a bottom padding for visual
 * separation from its surrounding content.
 *
 * Our root composable is a [Text] whose `text` argument is our [String] parameter [text], whose
 * [TextStyle] `style` argument is the [Typography.h6] of our custom [MaterialTheme.typography],
 * whose [Color] `color` argument is the [JetsnackColors.brand] of our custom [JetsnackTheme.colors],
 * and whose `modifier` argument is a [Modifier.padding] that adds `bottom` padding of `8.dp`.
 *
 * @param text The title text to be displayed.
 */
@Composable
fun FilterTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        color = JetsnackTheme.colors.brand,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
/**
 * A composable function representing a sort option in a list.
 *
 * This composable displays a row with a text label, an optional icon, and a checkmark if selected.
 * It is used to present a single sorting option to the user, allowing them to select it.
 *
 * Our root composable is a [Row] whose `modifier` argument is a [Modifier.padding] that adds
 * `14.dp` to the `top` of the [Row], with a [Modifier.selectable] chained to thqt whose `selected`
 * argument is our [Boolean] pararmeter [selected] and whose `onClick` lambda argument is a lambda
 * that calls our lambda parameter [onClickOption]. In its [RowScope] `content` lambda argument we
 * compose:
 *
 * **First** If our [ImageVector] parameter [icon] is not `null`, we compose an [Icon] whose
 * `imageVector` argument is our [ImageVector] variable `icon`, and whose `contentDescription`
 * argument is `null`.
 *
 * **Second** We compose a [Text] whose arguments are:
 *  - `text`: Our [String] parameter [text].
 *  - `style`: [TextStyle] is the [Typography.subtitle1] of our custom [MaterialTheme.typography].
 *  - `modifier`: A [Modifier.padding] that adds `start` padding of `10.dp` to the [Text] with a
 *  [RowScope.weight] whose `weight` is `1f` chained to that.
 *
 * **Third** If our [Boolean] parameter [selected] is `true`, we compose an [Icon] whose arguments
 * are:
 *  - `imageVector`: The [ImageVector] drawn by [Icons.Filled.Done]
 *  - `contentDescription`: `null`
 *  - `tint`: [Color] is the [JetsnackColors.brand] of our custom [JetsnackTheme.colors].
 *
 * @param text The text label to display for the sort option.
 * @param icon An optional [ImageVector] to display next to the text. If null, no icon is shown.
 * @param onClickOption A lambda function to be called when the sort option is clicked.
 * @param selected A [Boolean] indicating whether the sort option is currently selected.
 */
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
