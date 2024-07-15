/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.samples.crane.base

import androidx.compose.samples.crane.base.Result.Error
import androidx.compose.samples.crane.base.Result.Success
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.details.DetailsViewModel

/**
 * A generic class that holds a value. It has two subclasses: [Success] which holds a result value
 * in its [Success.data] field, and [Error] which holds an [Exception] in its [Error.exception]
 * field. This is used by [DetailsViewModel] as the return type of its [DetailsViewModel.cityDetails]
 * property, and it returns a [Result.Success] of the [City] that it retrieves from the
 * [DestinationsRepository] if the [DestinationsRepository.getDestination] method finds the `cityName`
 * or a [Result.Error] of an [IllegalArgumentException] ("City doesn't exist") if the method returns
 * `null`.
 *
 * @param <T> the type of [Result] that will be returned.
 */
sealed class Result<out R> {
    /**
     * This subclass of [Result] returns results of a successful query in its [data] field
     *
     * @param data data to be returned to the caller, in our case a [City]
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * This subclass of [Result] returns an [Exception] in its [exception] field.
     *
     * @param exception an [Exception] explaining an error that occurred.
     */
    data class Error(val exception: Exception) : Result<Nothing>()
}
