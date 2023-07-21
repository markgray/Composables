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

import androidx.compose.runtime.mutableStateOf

/**
 * TODO: Add kdoc
 *
 * @param id TODO: Add kdoc
 * @param onSelected TODO: Add kdoc
 */
class MailItemState(val id: Int, private val onSelected: (id: Int, isSelected: Boolean) -> Unit) {
    private val _isSelected = mutableStateOf(false)
    /**
     * TODO: Add kdoc
     */
    val isSelected: Boolean
        get() = _isSelected.value

    /**
     * TODO: Add kdoc
     *
     * @param isSelected TODO: Add kdoc
     */
    fun setSelected(isSelected: Boolean) {
        onSelected(id, isSelected)
        _isSelected.value = isSelected
    }
}