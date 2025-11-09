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

package com.example.composemail.ui.mails

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * A state holder for a single mail item.
 *
 * @param id The unique id of the mail item.
 * @param onSelected A callback invoked when the selection state of this item changes.
 */
class MailItemState(val id: Int, private val onSelected: (id: Int, isSelected: Boolean) -> Unit) {
    /**
     * Private mutable state for whether this item is selected.
     */
    private val _isSelected: MutableState<Boolean> = mutableStateOf(value = false)

    /**
     * Whether this mail item is currently selected. Public read-only access to our private
     * [MutableState] wrapped property [_isSelected].
     */
    val isSelected: Boolean
        get() = _isSelected.value

    /**
     * Updates the selection state of this mail item.
     *
     * This function will also invoke the [onSelected] callback with the new state.
     *
     * @param isSelected Whether the item is selected.
     */
    fun setSelected(isSelected: Boolean) {
        onSelected(id, isSelected)
        _isSelected.value = isSelected
    }
}