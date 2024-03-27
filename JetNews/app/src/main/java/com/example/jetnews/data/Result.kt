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

package com.example.jetnews.data

/**
 * A generic class that holds a value or an exception
 */
sealed class Result<out R> {
    /**
     * TODO: Add kdoc
     *
     * @param data TODO: Add kdoc
     */
    data class Success<out T>(val data: T) : Result<T>()
    /**
     * TODO: Add kdoc
     *
     * @param exception TODO: Add kdoc
     */
    data class Error(val exception: Exception) : Result<Nothing>()
}

/**
 * TODO: Add kdoc
 */
fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}
