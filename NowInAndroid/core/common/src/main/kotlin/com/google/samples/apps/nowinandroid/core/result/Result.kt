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

package com.google.samples.apps.nowinandroid.core.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T> the type of the value held by this Result
 */
sealed interface Result<out T> {
    /**
     * Represents a successful outcome with the associated [data].
     *
     * @param T The type of the data.
     * @property data The successful data.
     */
    data class Success<T>(val data: T) : Result<T>

    /**
     * Represents a failed outcome with the associated [exception].
     *
     * @property exception The [Throwable] that caused the failure.
     */
    data class Error(val exception: Throwable) : Result<Nothing>

    /**
     * Represents a loading state.
     */
    data object Loading : Result<Nothing>
}

/**
 * Converts a [Flow] of [T] to a [Flow] of [Result]s.
 *
 * This function wraps the emissions of the original Flow in [Result.Success] objects.
 * It prepends a [Result.Loading] emission before the Flow starts collecting.
 * If the Flow encounters an error, it emits a [Result.Error] with the corresponding [Throwable].
 *
 * @receiver The source Flow to convert.
 * @return A Flow of [Result] objects, representing the loading, success, or error states
 * of the original Flow's emissions.
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = map<T, Result<T>> { Result.Success(it) }
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(it)) }
