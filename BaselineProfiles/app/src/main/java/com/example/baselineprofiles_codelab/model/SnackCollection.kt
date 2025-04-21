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

/**
 * Represents a collection of snacks.
 *
 * A SnackCollection is an immutable data class that holds information about a group of snacks.
 * It contains a unique identifier, a name for the collection, a list of snacks within the collection,
 * and a type that describes the nature of the collection.
 *
 * @property id The unique identifier for this snack collection.
 * @property name The name of this snack collection.
 * @property snacks The list of snacks contained within this collection. This list is immutable,
 * ensuring that the collection's snack list cannot be modified after creation.
 * @property type The type of this collection. Defaults to [CollectionType.Normal]. This property
 * indicates whether the collection should be displayed in a normal or highlighted style.
 * @constructor Creates a new SnackCollection with the given parameters.
 */
@Immutable
data class SnackCollection(
    val id: Long,
    val name: String,
    val snacks: List<Snack>,
    val type: CollectionType = CollectionType.Normal
)

/**
 * @brief Represents the type of collection.
 *
 * This enum class defines the different types of collections that can be used.
 * It can be used to distinguish between regular collections and highlighted
 * collections, which might have different visual or functional properties.
 */
enum class CollectionType { Normal, Highlight }

/**
 * `SnackRepo` is a singleton object that acts as a repository for snack-related data.
 * It provides access to various snack collections, individual snacks, related snacks,
 * filters (e.g., price, category, lifestyle), and shopping cart information.
 *
 * This object serves as a mock data source, simulating interactions with a real
 * database or API.  In a production environment, this would likely be replaced
 * by a class that interacts with a persistent data store.
 */
object SnackRepo {
    /**
     * Retrieves a list of snack collections.
     *
     * This function returns a list containing all available snack collections.
     * Each snack collection is represented by a [SnackCollection] object,
     * which may contain multiple snacks.
     *
     * @return A list of [SnackCollection] objects.
     */
    fun getSnacks(): List<SnackCollection> = snackCollections

    /**
     * Retrieves a Snack object from the list of snacks based on the provided snack ID.
     *
     * @param snackId The unique identifier of the snack to retrieve.
     * @return The Snack object with the matching ID.
     */
    fun getSnack(snackId: Long): Snack = snacks.find { it.id == snackId }!!

    /**
     * Retrieves a list of related SnackCollections.
     *
     * This function currently returns a hardcoded list of SnackCollections
     * and does not use the provided `snackId` parameter.  In a real-world
     * implementation, this function would likely query a database or API
     * to find SnackCollections related to the given `snackId`.
     *
     * The `snackId` parameter is currently unused, as indicated by the
     * `@Suppress` annotations. This is a placeholder implementation.
     *
     * @param snackId The ID of the snack for which to find related SnackCollections.
     * This parameter is currently unused in this implementation.
     * @return A list of [SnackCollection] objects that are considered related.
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    fun getRelated(snackId: Long): List<SnackCollection> = related

    /**
     * Retrieves a collection of snacks that are suggested or inspired by the user's current cart
     * contents.
     *
     * This function returns a [SnackCollection] representing a curated list of snacks that might
     * be of interest to the user based on the items they currently have in their shopping cart.
     * This could include:
     *  - **Complementary Snacks:** Items that often go well with items already in the cart.
     *  - **Similar Snacks:** Snacks that are of the same type or flavor profile as items in
     *  the cart.
     *  - **New Releases:** New snacks that the user might enjoy based on their existing cart.
     *  - **Promotional Offers:** Snacks that are on sale or have special offers related to the
     *  cart contents.
     *
     * The specific logic used to determine the "inspired" snacks is internal to the system and
     * may change over time to improve the quality of suggestions.
     *
     * @return A [SnackCollection] containing snacks inspired by the user's cart.
     */
    fun getInspiredByCart(): SnackCollection = inspiredByCart

    /**
     * Returns the current list of filters.
     *
     * This function provides a read-only view of the filters that are currently applied.
     * Modifications to the returned list will not affect the internal filter list.
     *
     * @return A List of [Filter] objects representing the currently active filters.
     */
    fun getFilters(): List<Filter> = filters

    /**
     * Retrieves the list of available price filters.
     *
     * This function returns a predefined list of `Filter` objects, each representing
     * a specific price range that can be used to filter products or services.
     *
     * @return A list of `Filter` objects representing different price ranges.
     * Returns an empty list if no price filters are defined.
     */
    fun getPriceFilters(): List<Filter> = priceFilters

    /**
     * Retrieves the current contents of the shopping cart.
     *
     * @return A list of [OrderLine] objects representing the items in the cart
     * Returns an empty list if the cart is empty.
     */
    fun getCart(): List<OrderLine> = cart

    /**
     * Retrieves the list of available sort filters.
     *
     * This function returns a list of [Filter] objects that can be used to
     * sort data. The specific filters available are determined by the
     * [sortFilters] property, which is initialized with the available
     * sorting options.
     *
     * @return A List of [Filter] objects representing the available sort filters.
     */
    fun getSortFilters(): List<Filter> = sortFilters

    /**
     * Retrieves the list of category filters.
     *
     * This function returns a list of `Filter` objects that represent the available
     * filters for categories. These filters can be used to narrow down search
     * results or to display specific subsets of category data.
     *
     * @return A List of [Filter] objects representing the category filters.
     * Returns an empty list if no category filters are defined.
     */
    fun getCategoryFilters(): List<Filter> = categoryFilters

    /**
     * Returns the default sorting order.
     *
     * This function retrieves the globally defined default sorting order,
     * which is used when no specific sorting criteria are provided.
     *
     * @return The default sorting order as a String.
     */
    fun getSortDefault(): String = sortDefault

    /**
     * Retrieves a list of lifestyle filters.
     *
     * This function returns a predefined list of filters that represent different
     * aspects of a user's lifestyle. These filters can be used to categorize or
     * filter data based on lifestyle preferences.
     *
     * @return A list of [Filter] objects representing lifestyle filters.
     * Returns an empty list if no lifestyle filters are defined.
     */
    fun getLifeStyleFilters(): List<Filter> = lifeStyleFilters
}

/*
 * Static data
 */

private val tastyTreats = SnackCollection(
    id = 1L,
    name = "Android's picks",
    type = CollectionType.Highlight,
    snacks = snacks.subList(0, 13)
)

private val popular = SnackCollection(
    id = 2L,
    name = "Popular on Jetsnack",
    snacks = snacks.subList(14, 19)
)

private val wfhFavs = tastyTreats.copy(
    id = 3L,
    name = "WFH favourites"
)

private val newlyAdded = popular.copy(
    id = 4L,
    name = "Newly Added"
)

private val exclusive = tastyTreats.copy(
    id = 5L,
    name = "Only on Jetsnack"
)

private val also = tastyTreats.copy(
    id = 6L,
    name = "Customers also bought"
)

private val inspiredByCart = tastyTreats.copy(
    id = 7L,
    name = "Inspired by your cart"
)

private val snackCollections = listOf(
    tastyTreats,
    popular,
    wfhFavs,
    newlyAdded,
    exclusive
)

private val related = listOf(
    also,
    popular
)

private val cart = listOf(
    OrderLine(snacks[4], 2),
    OrderLine(snacks[6], 3),
    OrderLine(snacks[8], 1)
)

@Immutable
data class OrderLine(
    val snack: Snack,
    val count: Int
)
