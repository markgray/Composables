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
 * A SnackCollection is an immutable data class that holds information about a group of snacks. It
 * contains a unique identifier, a name for the collection, a list of snacks within the collection,
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
 *Represents the type of collection.
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
    @Suppress("UNUSED_PARAMETER", "RedundantSuppression")
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

/**
 * A curated collection of tasty snacks hand-picked for Android users.
 * This collection features a selection of the first 13 snacks from the [snacks] list,
 * representing a highlight reel of delightful treats. Its arguments are:
 *  - `id`: A unique identifier for this collection, which is `1L`.
 *  - `name`: The name of this collection, which is "Android's picks".
 *  - `type`: The type of this collection, which is [CollectionType.Highlight].
 *  - `snacks`: The list of snacks included in this collection, derived from the
 *  first 13 elements of the [snacks] list.
 */
private val tastyTreats = SnackCollection(
    id = 1L,
    name = "Android's picks",
    type = CollectionType.Highlight,
    snacks = snacks.subList(0, 13)
)

/**
 * A [SnackCollection] representing snacks that are popular on the Jetsnack platform.
 *
 * This collection includes a curated subset of snacks deemed popular, as determined by
 * factors like user preference, sales trends, or editorial selection. Its arguments are:
 *  - `id`: A unique identifier for this collection, which is `2L`.
 *  - `name`: The name of this collection, which is "Popular on Jetsnack".
 *  - `snacks`: The list of snacks included in this collection, derived from the
 *  sublist of the [snacks] list starting from index 14 and ending at index 18.
 */
private val popular = SnackCollection(
    id = 2L,
    name = "Popular on Jetsnack",
    snacks = snacks.subList(14, 19)
)

/**
 * A predefined list of "tasty treats" specifically curated as Work From Home (WFH) favorites.
 * This list is a copy of the `tastyTreats` list, but with a modified ID and name to represent
 * its distinct purpose. Its arguments are:
 *  - `id`: A unique identifier for this collection, which is `3L`.
 *  - `name`: Changed to "WFH favourites" to clearly indicate its status.
 */
private val wfhFavs = tastyTreats.copy(
    id = 3L,
    name = "WFH favourites"
)

/**
 * Represents a newly added item, derived from the 'popular' item with a modified ID and name.
 * This item is intended to represent a recently added entry in the list.
 *
 * - `id`: Set to 4L to uniquely identify this newly added item.
 * - `name`: Changed to "Newly Added" to clearly indicate its status.
 *
 * It's created by copying the 'popular' item and altering the necessary fields.
 */
private val newlyAdded = popular.copy(
    id = 4L,
    name = "Newly Added"
)

/**
 * A special `TastyTreat` available exclusively through the Jetsnack app.
 *
 * This treat is unique and not available through any other channels. Its arguments are:
 *  - `id`: A unique identifier for this treat, which is `5L`.
 *  - `name`: The name of this treat, which is "Only on Jetsnack".
 *
 * It's created by copying the 'tastyTreats' item and altering the necessary fields.
 */
private val exclusive = tastyTreats.copy(
    id = 5L,
    name = "Only on Jetsnack"
)

/**
 * Represents a list of items that customers frequently purchase alongside the items
 * they are currently viewing or have in their cart. This is often used to suggest
 * complementary products or popular pairings. Its arguments are:
 *  - `id`: A unique identifier for this collection, which is `6L`.
 *  - `name`: The name of this collection, which is "Customers also bought".
 *
 * It's created by copying the 'tastyTreats' item and altering the necessary fields.
 */
private val also = tastyTreats.copy(
    id = 6L,
    name = "Customers also bought"
)

/**
 * A `TastyTreat` object representing a suggested item "inspired" by the user's shopping cart.
 * This treat is intended to be a recommendation, possibly based on items
 * already present in the cart or general purchase history. It has a specific ID (7L)
 * and a name that indicates its purpose as a cart-related suggestion. Its arguments are:
 *  - `id`: A unique identifier for this treat, which is `7L`.
 *  - `name`: The name of this treat, which is "Inspired by your cart".
 *
 *  It uses the base `tastyTreats` as a template and overrides the id and name properties.
 */
private val inspiredByCart = tastyTreats.copy(
    id = 7L,
    name = "Inspired by your cart"
)

/**
 * A list of different snack collections to be displayed.
 *
 * Each element in the list represents a distinct collection of snacks,
 * such as "Tasty Treats", "Popular", "WFH Favs", "Newly Added", and "Exclusive".
 * The order of the collections in this list determines their order of display.
 *
 * The individual collections are defined as separate variables (e.g., [tastyTreats],
 * [popular], etc.) and are assumed to be lists or other data structures containing the
 * actual snack data.
 */
private val snackCollections = listOf(
    tastyTreats,
    popular,
    wfhFavs,
    newlyAdded,
    exclusive
)

/**
 * A list of related items.
 *
 * This property contains a list of lists, where each inner list represents
 * a different category of related items. For example, it might include
 * items that are similar to the current item ("also"), and items that are
 * generally well-liked ("popular").
 *
 * The order of the inner lists represents the order in which these categories
 * should be presented to the user.
 *
 * The current implementation contains the following categories:
 * - `also`: Items that are similar or related in some other way.
 * - `popular`: Items that are generally popular.
 */
private val related = listOf(
    also,
    popular
)

/**
 * The current shopping cart containing a list of [OrderLine] items.
 *
 * This cart represents the items a customer has selected for purchase. Each [OrderLine] in the
 * cart includes a [Snack] as its [OrderLine.snack] and the desired [OrderLine.count]
 *
 * In this example, the cart contains:
 * - 2 units of 'Gingerbread'
 * - 3 units of 'Ice Cream Sandwich'
 * - 1 unit of 'KitKat'
 */
private val cart = listOf(
    OrderLine(snack = snacks[4], count = 2),
    OrderLine(snack = snacks[6], count = 3),
    OrderLine(snack = snacks[8], count = 1)
)

/**
 * Represents a single line item within an order, specifying the snack and the quantity.
 *
 * @property snack The [Snack] that is part of this order line.
 * @property count The quantity of the specified [snack] in this order line. Must be a
 * non-negative integer.
 */
@Immutable
data class OrderLine(
    val snack: Snack,
    val count: Int
)
