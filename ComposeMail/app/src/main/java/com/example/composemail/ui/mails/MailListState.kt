/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.example.composemail.ui.mails

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import java.util.Map

/**
 * A state holder for a list of mails.
 *
 * This class is responsible for managing the selection state of a list of mail items. It holds
 * the state for each individual item and provides information about the overall selection, such as
 * the count of selected items and their IDs.
 *
 * It is designed to be used with a [LazyColumn] or similar component, where [stateFor] can be
 * called to retrieve or create a state object for each item by its ID.
 */
class MailListState {
    /**
     * A map of [MailItemState]s, keyed by the ID of the mail item.
     *
     * This allows us to persist the state of each mail item (e.g., whether it is selected)
     * even when it is scrolled out of view and recycled by a [LazyColumn].
     */
    private val conversationStatesById: MutableMap<Int, MailItemState> = mutableMapOf()

    /**
     * A mutable set that tracks the IDs of all selected mail items.
     *
     * This set is updated via the lambda passed to [MailItemState] when an item's selection state
     * changes. It is used to calculate [selectedCount] and provide the list of [selectedIDs].
     */
    private val selectedTracker: MutableSet<Int> = mutableSetOf()

    /**
     * The number of selected mails, as a [MutableIntState].
     *
     * This state is updated whenever a mail item's selection state changes, and is used to drive
     * recomposition in Composables that observe it. It is exposed externally as an immutable [Int]
     * through the [selectedCount] property.
     */
    private val _selectedCount: MutableIntState = mutableIntStateOf(value = 0)

    /**
     * A collection of the IDs of all currently selected mail items.
     *
     * This provides a read-only view of the selected item IDs, derived from the internal
     * [selectedTracker].
     */
    val selectedIDs: Collection<Int>
        get() = selectedTracker.toList()

    /**
     * The number of currently selected mail items.
     *
     * This property provides a read-only count of the selected items and is derived from an
     * internal [MutableIntState], ensuring that Composables observing this value will be
     * recomposed when the selection changes.
     */
    val selectedCount: Int
        get() = _selectedCount.intValue

    /**
     * Unselects all currently selected mail items.
     *
     * This function iterates through all known [MailItemState] objects and sets their
     * selection status to `false`. This effectively clears the current selection and updates
     * the [selectedCount] to zero.
     */
    fun unselectAll() {
        conversationStatesById.values.forEach { it.setSelected(isSelected = false) }
    }

    /**
     * Retrieves or creates a [MailItemState] for a mail item with the given [id].
     *
     * This function ensures that the state for each mail item is preserved, even when it is
     * scrolled out of view and recycled by a [LazyColumn]. If a [MailItemState] for the
     * specified [id] already exists, it is returned. Otherwise, a new [MailItemState] is
     * created, stored, and then returned.
     *
     * Repeated calls for the same [id] will return the same instance of [MailItemState].
     * If the provided [id] is `null`, a default state for an ID of `-1` is used.
     *
     * We start by initializing our [Int] variable `nextId` to our [Int] parameter [id] if it is
     * not `null` or to `-1` if it is `null`. Then we call the [Map.computeIfAbsent] method of
     * [conversationStatesById] with the `key` argument `nextId` and the `mappingFunction` lambda
     * argument that will be called if the key is not present in the map a lambda that accepts the
     * [Int] passed the lambda in variable `id` and the [Boolean] in variable `isSelected`. Then
     * if `isSelected` is `true` it calls the [MutableSet.add] method of [selectedTracker] with its
     * `element` argument `id` and if `isSelected` is `false` it calls the [MutableSet.remove]
     * method of [selectedTracker] with its `element` argument `id`. In either case it then sets the
     * value of [selectedCount] to the size of [selectedTracker].
     *
     * @param id The unique identifier of the mail item. Can be `null`.
     * @return The [MailItemState] for the corresponding mail item.
     */
    fun stateFor(id: Int?): MailItemState {
        val nextId = id ?: -1
        return conversationStatesById.computeIfAbsent(nextId) {
            MailItemState(id = nextId) { id: Int, isSelected: Boolean ->
                // Track which items are selected
                if (isSelected) {
                    selectedTracker.add(element = id)
                } else {
                    selectedTracker.remove(element = id)
                }
                _selectedCount.intValue = selectedTracker.size
            }
        }
    }
}