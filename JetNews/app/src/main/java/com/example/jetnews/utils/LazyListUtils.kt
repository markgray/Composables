/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf

/**
 * Convenience extension property of [LazyListState uses [derivedStateOf] to create a [State] wrapped
 * [Boolean] based on the calculation [LazyListState.firstVisibleItemIndex] is greater than 0, or
 * [LazyListState.firstVisibleItemScrollOffset] is greater than 0 and returns the [State.value] of
 * that to the caller. It might be useful if the app only wanted to display a "go to top" button if
 * the [LazyListState] is scrolled (but this app does not use it).
 */
@Suppress("unused")
val LazyListState.isScrolled: Boolean
    get() = derivedStateOf { firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0 }.value
