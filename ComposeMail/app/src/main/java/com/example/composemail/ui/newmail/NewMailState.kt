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

package com.example.composemail.ui.newmail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * TODO: Add kdoc
 *
 * @param key TODO: Add kdoc
 * @param initialLayoutState TODO: Add kdoc
 */
@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun rememberNewMailState(
    key: Any = Unit,
    initialLayoutState: NewMailLayoutState
): NewMailState = remember(key1 = key) { NewMailState(initialLayoutState = initialLayoutState) }

/**
 * TODO: Add kdoc
 *
 * @param initialLayoutState TODO: Add kdoc
 */
class NewMailState(initialLayoutState: NewMailLayoutState) {
    private var _currentState = mutableStateOf(initialLayoutState)

    /**
     * TODO: Add kdoc
     */
    val currentState: NewMailLayoutState
        get() = _currentState.value

    /**
     * TODO: Add kdoc
     */
    fun setToFull() {
        _currentState.value = NewMailLayoutState.Full
    }

    /**
     * TODO: Add kdoc
     */
    fun setToMini() {
        _currentState.value = NewMailLayoutState.Mini
    }

    /**
     * TODO: Add kdoc
     */
    fun setToFab() {
        _currentState.value = NewMailLayoutState.Fab
    }
}

/**
 * TODO: Add kdoc
 */
enum class NewMailLayoutState {
    /**
     * TODO: Add kdoc
     */
    Full,
    /**
     * TODO: Add kdoc
     */
    Mini,
    /**
     * TODO: Add kdoc
     */
    Fab
    // MiniExpanded
}

// Reply, Forward, Archive, Delete