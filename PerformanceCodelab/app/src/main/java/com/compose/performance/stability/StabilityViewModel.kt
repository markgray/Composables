/*
 * Copyright 2024 The Android Open Source Project
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
package com.compose.performance.stability

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

/**
 * TASK Codelab task: make this class Stable DONE
 * Example of an item which has been made stable.
 *
 * This data class holds information about a specific stable item, including its unique identifier,
 * type, name, checked state, and creation timestamp. It is designed to be immutable, ensuring that
 * once an instance is created, its state cannot be modified.
 *
 * @property id The unique identifier of the stability item.
 * @property type The type of the stability item, represented by a [StabilityItemType] enum.
 * @property name The name or description of the stability item.
 * @property checked A boolean indicating whether the stability item is currently checked.
 * @property created The timestamp indicating when the stability item was created.
 */
@Immutable
data class StabilityItem(
    val id: Int,
    val type: StabilityItemType,
    val name: String,
    val checked: Boolean,
    val created: LocalDateTime
)

/**
 * [StabilityViewModel] manages the state and logic related to a list of [StabilityItem]s.
 * It demonstrates the behavior of unstable parameters in Compose UI by showcasing the
 * effects of using `neverEqualPolicy` with a constantly changing [LocalDate].
 *
 * This ViewModel exposes a list of [StabilityItem]s through the [items] StateFlow and
 * provides functionalities to add, remove, and check items within the list.
 *
 * @property latestDateChange A [LocalDate] that changes with each emission from the [items]
 * StateFlow. It utilizes `neverEqualPolicy` to force recomposition whenever it's accessed,
 * demonstrating how unstable parameters trigger recomposition in Compose.
 * @property items A [StateFlow] that emits a list of [StabilityItem]s. Each emission involves
 * simulating the creation of new instances of `StabilityItem` through the `simulateNewInstances`
 * function, to potentially trigger recomposition. It also updates `latestDateChange` with the
 * current date on each emission. It utilizes [SharingStarted.WhileSubscribed] to start sharing
 * when the first subscriber appears, stopping 5 seconds after the last subscriber disappears.
 */
class StabilityViewModel : ViewModel() {

    /**
     * The unique identifier for the next [StabilityItem] to be added, incremented with each addition
     * and stored in the [StabilityItem.id] property.
     */
    private var incrementingKey = 0

    /**
     * The latest date that a change occurred. We're using neverEqualPolicy to showcase what the UI
     * logic does for unstable parameters with each time a different instance.
     *
     * This property holds the most recent date when any relevant modification or update took place.
     * It is represented as a [LocalDate] object, offering date-only information (without time).
     *
     * The property utilizes Compose's `mutableStateOf` to ensure recomposition when the date
     * changes. It also uses `neverEqualPolicy` to trigger recomposition whenever the value is
     * updated, even if the new value is equal to the previous one.
     *
     * The setter of this property is private, meaning it can only be modified within the same
     * class, ensuring controlled updates to the latest date. External classes can observe the
     * changes but cannot directly modify the date.
     *
     * @see LocalDate
     * @see mutableStateOf
     * @see neverEqualPolicy
     */
    var latestDateChange: LocalDate by mutableStateOf<LocalDate>(
        value = LocalDate.now(),
        policy = neverEqualPolicy()
    )
        private set

    /**
     * A private [MutableStateFlow] that holds the list of [StabilityItem]s.
     *
     * This flow is used internally to manage the state of the item list.
     * External classes should observe the [items] property instead, which provides
     * read-only access to the list. Modifications to the list should be done
     * through appropriate methods that update this internal flow.
     *
     * Initialized with an empty list.
     */
    private val _items = MutableStateFlow<List<StabilityItem>>(emptyList())

    /**
     * A public [StateFlow] that provides read-only access to the list of [StabilityItem]s in
     * our [_items] flow. It uses the `map` operator to simulate the creation of new instances
     * and updates `latestDateChange` with the current date on each emission. It then uses
     * the [Flow.stateIn] operator to convert the flow into a [StateFlow] with a its `scope`
     * set to the [viewModelScope], `started` set to [SharingStarted.WhileSubscribed] with a
     * stop timeout of 5 seconds, and an initial value of an empty list.
     */
    val items: StateFlow<List<StabilityItem>> = _items
        .map { simulateNewInstances(it) }
        .onEach { latestDateChange = LocalDate.now() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = emptyList()
        )

    /**
     * Initializes the ViewModel with some sample data.
     */
    init {
        repeat(3) {
            addItem()
        }
    }

    /**
     * Adds a new [StabilityItem] to the [MutableStateFlow] wrapped [List] of [StabilityItem] in our
     * [_items] property.
     *
     * This function creates a new [StabilityItem] with the following properties:
     * - `id`: An auto-incrementing integer, ensuring each item has a unique ID.
     * - `type`: A [StabilityItemType] determined by the current `incrementingKey` value. It cycles
     * through the available types based on the remainder of `incrementingKey` divided by 3.
     * - `name`: A randomly selected name from the `sampleData` list.
     * - `checked`: Initialized to `false`.
     * - `created`: The current date and time when the item is created.
     *
     * The newly created [StabilityItem] is then appended to the internal list of items ([_items]).
     *
     * @see StabilityItem
     * @see StabilityItemType
     * @see LocalDateTime
     */
    fun addItem() {
        _items.value += StabilityItem(
            id = incrementingKey++,
            type = StabilityItemType.fromId(incrementingKey),
            name = sampleData.random(stableRandom),
            checked = false,
            created = LocalDateTime.now()
        )
    }

    /**
     * Removes a [StabilityItem] from the [List] of [StabilityItem] property [_items] based on
     * its ID.
     *
     * This function filters the current list of items, excluding any item whose [StabilityItem.id]
     * property matches the [Int] parameter [id]. The updated list, minus the removed item, is
     * then set as the new value of the underlying [MutableStateFlow] (our property [_items]).
     *
     * @param id The ID of the item to be removed. If no item with the given ID exists, the list
     * remains unchanged.
     */
    fun removeItem(id: Int) {
        _items.value = _items.value.filterNot { it.id == id }
    }

    /**
     * Updates the checked status of a specific item in the list.
     *
     * This function searches for an item whose [StabilityItem.id] is equal to the [Int] parameter
     * [id] in our [List] of [StabilityItem] objects. If an item with the matching `id` is found,
     * its [StabilityItem.checked] property is updated to the [Boolean] parameter [checked].
     * The updated list is then emitted through our [MutableStateFlow] wrapped [List] of
     * [StabilityItem] property [_items].
     *
     * If no item with the specified [id] is found, the function does nothing and returns early.
     *
     * @param id The unique identifier of the item to update.
     * @param checked The new checked status to apply to the item.
     */
    fun checkItem(id: Int, checked: Boolean) {
        val items: MutableList<StabilityItem> = _items.value.toMutableList()
        val index: Int = items.indexOfFirst { it.id == id }
        if (index < 0) return
        items[index] = items[index].copy(checked = checked)
        _items.value = items
    }
}

/**
 * Represents the type of stability item.
 *
 * Stability items are categorized into two types:
 * - [REFERENCE]: Indicates that the item's stability is based on its reference identity.
 * Changes to the object's internal state won't affect its stability as long as it's the
 * same instance.
 * - [EQUALITY]: Indicates that the item's stability is based on its equality with other items.
 * If the item's content changes, even if it's the same instance, its stability may change.
 */
enum class StabilityItemType {
    REFERENCE,
    EQUALITY;

    companion object {
        /**
         * Determines the [StabilityItemType] based on the provided ID.
         *
         * If the ID is even, it returns [REFERENCE]; otherwise, it returns [EQUALITY].
         *
         * @param id The integer ID to evaluate.
         * @return The [StabilityItemType], either [REFERENCE] or [EQUALITY].
         */
        fun fromId(id: Int): StabilityItemType = if (id % 2 == 0) REFERENCE else EQUALITY
    }
}

/**
 * Simulates the creation of new instances for [StabilityItem]'s, specifically for items of type
 * [StabilityItemType.REFERENCE].
 *
 * This function iterates through a list of [StabilityItem]s. For each item, it checks its type.
 * If the item's type is [StabilityItemType.REFERENCE], it creates a copy of the item, effectively
 * simulating a new instance being created. This is because REFERENCE type items should be treated
 * as new instances each time they are encountered.  If the item's type is not REFERENCE,
 * the original item is returned without modification.
 *
 * This is useful in scenarios where we want to treat REFERENCE types as if they were
 * freshly created objects, ensuring that any subsequent operations don't accidentally
 * modify shared state or rely on cached values from a previous use of the "same" item.
 *
 * @param items The list of [StabilityItem]s to process.
 * @return A new list of [StabilityItem]s where REFERENCE type items have been replaced
 * with copies, and all other items remain unchanged.
 */
private fun simulateNewInstances(items: List<StabilityItem>): List<StabilityItem> =
    items.map { stabilityItem: StabilityItem ->
        if (stabilityItem.type == StabilityItemType.REFERENCE) {
            // For the reference types, we recreate the class to be always a new instance
            stabilityItem.copy()
        } else {
            stabilityItem
        }
    }

/**
 * A list of sample words generated from Lorem Ipsum.
 *
 * This property contains a list of strings, where each string represents a
 * single word extracted from a 500-word Lorem Ipsum text. The text is first
 * generated using the `LoremIpsum` utility, then split into individual words
 * based on spaces.  Finally, common punctuation marks like periods, commas, and
 * newline characters are trimmed from the start and end of each word.
 *
 * This data is intended for use as sample data in various contexts, such as
 * populating UI elements with dummy text, or as test data for string manipulation
 * functions.
 *
 * The size of this list will be determined by the number of words in the generated
 * 500-word Lorem Ipsum string, and will typically be around 500.
 */
private val sampleData: List<String> = LoremIpsum(words = 500)
    .values.first()
    .split(" ")
    .map { it.trim('.', ',', '\n') }

/**
 * A stable random number generator initialized with a fixed seed (0).
 *
 * This instance is designed to produce the same sequence of pseudo-random numbers
 * each time the application runs, ensuring reproducibility in scenarios where
 * deterministic behavior is desired.  This is useful for:
 *
 * - **Testing:** When you need to verify the behavior of code that relies on
 *   randomness, a stable seed allows you to repeatedly generate the same inputs.
 * - **Debugging:**  If an issue arises that is related to random number generation,
 *   using a fixed seed can help isolate and reproduce the problem.
 * - **Consistent results:**  In simulations or data processing pipelines where you
 *   need to obtain the exact same results on different runs or machines.
 *
 * Note that while this generator produces consistent results, it should not be used
 * in security-sensitive contexts where unpredictable randomness is crucial.
 * In those scenarios, use `Random.Default` or `SecureRandom` instead.
 */
private val stableRandom = Random(seed = 0)
