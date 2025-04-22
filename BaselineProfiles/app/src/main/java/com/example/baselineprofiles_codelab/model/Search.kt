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

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * A fake repo for searching.
 */
object SearchRepo {
    /**
     * Retrieves the list of available search category collections.
     *
     * This function returns a list of [SearchCategoryCollection] objects, representing
     * the different categories that can be used for searching. Each
     * [SearchCategoryCollection] contains information about a specific
     * category, such as its name, ID, and potentially other related data.
     *
     * @return A [List] of [SearchCategoryCollection] objects. Returns an empty list if no
     * categories are available.
     */
    fun getCategories(): List<SearchCategoryCollection> = searchCategoryCollections

    /**
     * Retrieves a list of search suggestion groups.
     *
     * This function returns the currently available search suggestions,
     * categorized into groups. Each group represents a different type
     * of suggestion, such as recent searches, popular searches, or
     * suggestions based on the current query.
     *
     * @return A list of [SearchSuggestionGroup] objects, each containing a group of related search
     * suggestions. The list may be empty if no suggestions are currently available.
     */
    fun getSuggestions(): List<SearchSuggestionGroup> = searchSuggestions

    /**
     * Searches for snacks whose names contain the given query string.
     *
     * This function performs a case-insensitive search within the list of available snacks.
     * It simulates an I/O delay using `delay` and executes the filtering operation on the
     * `Dispatchers.Default` coroutine dispatcher to avoid blocking the main thread.
     *
     * @param query The string to search for within snack names.
     * @return A list of [Snack] objects whose names contain the query string, or an empty list if
     * no matches are found.
     */
    suspend fun search(query: String): List<Snack> = withContext(Dispatchers.Default) {
        delay(timeMillis = 200L) // simulate an I/O delay
        snacks.filter { snack: Snack -> snack.name.contains(other = query, ignoreCase = true) }
    }
}

/**
 * Represents a collection of search categories.
 *
 * This data class encapsulates a group of related [SearchCategory] objects,
 * providing a structure to organize and manage them. Each collection has a unique
 * identifier, a descriptive name, and a list of its member categories.
 *
 * @property id A unique identifier for this search category collection. This is typically a
 * database primary key or a unique generated ID.
 * @property name A user-friendly name describing this collection of search categories.
 * Examples include "Popular Categories," "Electronics," or "Trending Searches."
 * @property categories A list of [SearchCategory] objects that belong to this collection.
 * This list defines the specific search categories within this group.
 *
 * @see SearchCategory
 */
@Immutable
data class SearchCategoryCollection(
    val id: Long,
    val name: String,
    val categories: List<SearchCategory>
)

/**
 * Represents a category used for searching.
 *
 * This data class encapsulates the information needed to display and identify a search category.
 * It includes the category's name and an associated image URL.
 *
 * @property name The name of the search category. This is typically a user-friendly string
 * used for display purposes. Must not be empty.
 * @property imageUrl The URL pointing to an image associated with this search category.
 * This image can be used as a visual representation of the category. Must not be empty.
 *
 * @[Immutable] Indicates that instances of this class are immutable, meaning their
 * properties cannot be changed after creation. This is beneficial for performance
 * and predictability, especially within UI frameworks like Compose.
 */
@Immutable
data class SearchCategory(
    val name: String,
    val imageUrl: String
)

/**
 * Represents a group of search suggestions.
 *
 * This data class encapsulates a collection of suggestions that are logically grouped together,
 * typically for display in a search suggestion UI. Each group has a unique identifier, a
 * user-friendly name, and a list of suggested search terms.
 *
 * @property id A unique identifier for this suggestion group. This ID should be unique across
 * all suggestion groups.
 * @property name The display name of the suggestion group. This name is typically shown in the UI
 * to indicate the category or type of the suggestions within the group (e.g., "Recent Searches",
 * "Popular Products", "Trending Topics").
 * @property suggestions A list of suggested search terms belonging to this group. Each element
 * in this list is a String representing a single suggestion. The order of the suggestions may
 * be significant, representing relevance or priority.
 *
 * @[Immutable] Indicates that this class is immutable. All fields are read-only and cannot be
 * modified after the object is created. This is crucial for data classes that are used in
 * reactive UI frameworks like Jetpack Compose.
 */
@Immutable
data class SearchSuggestionGroup(
    val id: Long,
    val name: String,
    val suggestions: List<String>
)

/*
 * Static data
 */

/**
 * A list of [SearchCategoryCollection] representing different groupings of search categories.
 *
 * This list defines the collections that will be used to display and organize
 * search categories in the user interface. Each [SearchCategoryCollection] contains
 * a unique ID, a name that describes the collection, and a list of [SearchCategory]
 * objects within that collection.
 *
 * Each [SearchCategory] represents a specific search term that the user can select.
 * The `name` field of a [SearchCategory] is displayed to the user and the `imageUrl`
 * field is used for visual representation of the category.
 *
 * Currently, there are two predefined collections:
 *
 * 1. **Categories:** This collection groups products into broad food categories like
 * "Chips & crackers", "Fruit snacks", etc.
 * 2. **Lifestyles:** This collection groups products based on dietary lifestyles like
 * "Organic", "Gluten Free", "Vegan", etc.
 *
 * This data structure enables a flexible and organized way to present and manage different search
 * categories within the application. Adding more collections or categories is as simple as adding
 * new elements to the [searchCategoryCollections] list.
 */
private val searchCategoryCollections = listOf(
    SearchCategoryCollection(
        id = 0L,
        name = "Categories",
        categories = listOf(
            SearchCategory(
                name = "Chips & crackers",
                imageUrl = "https://source.unsplash.com/UsSdMZ78Q3E"
            ),
            SearchCategory(
                name = "Fruit snacks",
                imageUrl = "https://source.unsplash.com/SfP1PtM9Qa8"
            ),
            SearchCategory(
                name = "Desserts",
                imageUrl = "https://source.unsplash.com/_jk8KIyN_uA"
            ),
            SearchCategory(
                name = "Nuts ",
                imageUrl = "https://source.unsplash.com/UsSdMZ78Q3E"
            )
        )
    ),
    SearchCategoryCollection(
        id = 1L,
        name = "Lifestyles",
        categories = listOf(
            SearchCategory(
                name = "Organic",
                imageUrl = "https://source.unsplash.com/7meCnGCJ5Ms"
            ),
            SearchCategory(
                name = "Gluten Free",
                imageUrl = "https://source.unsplash.com/m741tj4Cz7M"
            ),
            SearchCategory(
                name = "Paleo",
                imageUrl = "https://source.unsplash.com/dt5-8tThZKg"
            ),
            SearchCategory(
                name = "Vegan",
                imageUrl = "https://source.unsplash.com/ReXxkS1m1H0"
            ),
            SearchCategory(
                name = "Vegitarian",
                imageUrl = "https://source.unsplash.com/IGfIGP5ONV0"
            ),
            SearchCategory(
                name = "Whole30",
                imageUrl = "https://source.unsplash.com/9MzCd76xLGk"
            )
        )
    )
)

/**
 * A list of search suggestion groups, each containing a set of suggested search terms.
 *
 * This list is used to populate the search suggestion UI, providing users with
 * quick access to common or recent searches.
 *
 * The list is composed of [SearchSuggestionGroup] objects, each representing a logical grouping of
 * suggestions. Each group has a unique ID, a name (e.g., "Recent searches" or "Popular searches"),
 * and a list of string suggestions.
 */
private val searchSuggestions = listOf(
    SearchSuggestionGroup(
        id = 0L,
        name = "Recent searches",
        suggestions = listOf(
            "Cheese",
            "Apple Sauce"
        )
    ),
    SearchSuggestionGroup(
        id = 1L,
        name = "Popular searches",
        suggestions = listOf(
            "Organic",
            "Gluten Free",
            "Paleo",
            "Vegan",
            "Vegitarian",
            "Whole30"
        )
    )
)
