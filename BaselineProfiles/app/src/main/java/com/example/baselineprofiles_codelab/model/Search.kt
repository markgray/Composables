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

@Immutable
data class SearchCategoryCollection(
    val id: Long,
    val name: String,
    val categories: List<SearchCategory>
)

@Immutable
data class SearchCategory(
    val name: String,
    val imageUrl: String
)

@Immutable
data class SearchSuggestionGroup(
    val id: Long,
    val name: String,
    val suggestions: List<String>
)

/**
 * Static data
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
