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

package com.example.baselineprofiles_codelab.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a filter that can be applied to a data set.
 *
 * This class encapsulates a filter's name, enabled state, and an optional icon.
 * Filters can be toggled on or off, and their state is tracked using a [MutableState].
 *
 * @property name The name of the filter. This is a user-friendly identifier for the filter.
 * @property enabled The initial enabled state of the filter. Defaults to `false`.
 * @property icon An optional [ImageVector] representing an icon for the filter.
 * @constructor Creates a new [Filter] instance.
 */
@Stable
class Filter(
    val name: String,
    enabled: Boolean = false,
    val icon: ImageVector? = null
) {
    /**
     * Indicates whether this component is enabled and can respond to user interactions.
     *
     * When set to `true`, the component is interactive and can receive events like clicks,
     * focus, etc. When set to `false`, the component is disabled and does not respond to user
     * input. Disabled components are typically displayed in a way that visually indicates their
     * inactive state (e.g., grayed out). It is just a [MutableState] wrapped copy of our parameter
     * `enabled`.
     */
    val enabled: MutableState<Boolean> = mutableStateOf(enabled)
}

/**
 * A list of predefined filters that can be applied to food items.
 *
 * These filters represent common dietary preferences and flavor profiles.
 * Each filter is represented by a [Filter] object, which contains a name describing the filter.
 *
 * The available filters are:
 *  - Organic: Food produced using organic farming methods.
 *  - Gluten-free: Food that does not contain gluten.
 *  - Dairy-free: Food that does not contain dairy products.
 *  - Sweet: Food with a sweet flavor profile.
 *  - Savory: Food with a savory flavor profile.
 *
 * This list can be used to categorize and filter food items based on user preferences or
 * requirements.
 */
val filters: List<Filter> = listOf(
    Filter(name = "Organic"),
    Filter(name = "Gluten-free"),
    Filter(name = "Dairy-free"),
    Filter(name = "Sweet"),
    Filter(name = "Savory")
)

/**
 * Represents a list of price filters for filtering search results.
 * Each filter corresponds to a price range, denoted by the number of dollar signs.
 *
 * For example:
 *   - `$` represents the lowest price range.
 *   - `$$` represents a mid-low price range.
 *   - `$$$` represents a mid-high price range.
 *   - `$$$$` represents the highest price range.
 */
val priceFilters: List<Filter> = listOf(
    Filter(name = "$"),
    Filter(name = "$$"),
    Filter(name = "$$$"),
    Filter(name = "$$$$")
)

/**
 *  A list of available sort filters for a collection of items.
 *  Each filter defines a way to order the items, including its name and icon.
 *  The first filter in the list is considered the default sort order.
 *
 *  The current filters are:
 *   - "Android's favorite (default)":  Represents a default, potentially custom ordering.
 *   - "Rating": Sorts items based on their rating or popularity.
 *   - "Alphabetical": Orders items in alphabetical order.
 */
val sortFilters: List<Filter> = listOf(
    Filter(name = "Android's favorite (default)", icon = Icons.Filled.Android),
    Filter(name = "Rating", icon = Icons.Filled.Star),
    Filter(name = "Alphabetical", icon = Icons.Filled.SortByAlpha)
)

/**
 * A list of predefined category filters.
 *
 * These filters represent the different categories of snacks or food items
 * that can be used to refine or narrow down search results or product listings.
 * Each filter is represented by a [Filter] object, which primarily contains the
 * display name of the category.
 *
 * Example categories include:
 *  - "Chips & crackers"
 *  - "Fruit snacks"
 *  - "Desserts"
 *  - "Nuts"
 */
val categoryFilters: List<Filter> = listOf(
    Filter(name = "Chips & crackers"),
    Filter(name = "Fruit snacks"),
    Filter(name = "Desserts"),
    Filter(name = "Nuts")
)

/**
 * A list of predefined lifestyle filters that can be applied to food or recipe searches.
 *
 * These filters represent dietary preferences or restrictions and can be used to narrow down
 * search results to only include items that match the selected lifestyle.
 *
 * The available lifestyle filters are:
 *  - **Organic:**  Indicates products grown or produced without synthetic pesticides or fertilizers.
 *  - **Gluten-free:** Indicates products that do not contain gluten, suitable for people with
 *  celiac disease or gluten sensitivity.
 *  - **Dairy-free:** Indicates products that do not contain dairy or dairy derivatives, suitable
 *  for people with lactose intolerance or dairy allergies.
 *  - **Sweet:**  Indicates food or recipes with a predominantly sweet flavor profile.
 *  - **Savory:** Indicates food or recipes with a predominantly salty, umami, or herb-driven
 *  flavor profile.
 */
val lifeStyleFilters: List<Filter> = listOf(
    Filter(name = "Organic"),
    Filter(name = "Gluten-free"),
    Filter(name = "Dairy-free"),
    Filter(name = "Sweet"),
    Filter(name = "Savory")
)

/**
 * The default sorting option to apply.
 *
 * This property holds the name of the default sorting filter, which is determined
 * by the name of the first filter in the `sortFilters` list.
 *
 * If `sortFilters` is empty, accessing this property will likely result in an
 * `IndexOutOfBoundsException`. Therefore, ensure that `sortFilters` has at least
 * one element before using this property.
 */
var sortDefault: String = sortFilters[0].name
