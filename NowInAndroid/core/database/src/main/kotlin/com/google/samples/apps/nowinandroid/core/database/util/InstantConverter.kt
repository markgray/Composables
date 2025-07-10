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

package com.google.samples.apps.nowinandroid.core.database.util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

/**
 * Room [TypeConverter] for [Instant] to [Long] and [Long] to [Instant]
 */
internal class InstantConverter {
    /**
     * Converts a [Long] value (representing milliseconds since epoch) to an [Instant] object.
     * Returns null if the input value is `null`. The [TypeConverter] annotation indicates that
     * this function should be used by Room when converting from a [Long] to an [Instant].
     *
     * @param value The [Long] value to convert.
     * @return The corresponding [Instant] object, or `null` if the input was `null`.
     */
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    /**
     * Converts an [Instant] object to its [Long] value representation (milliseconds since epoch).
     * Returns `null` if the input Instant is `null`. The [TypeConverter] annotation indicates
     * that this function should be used by Room when converting from an [Instant] to a [Long].
     *
     * @param instant The [Instant] object to convert.
     * @return The corresponding [Long] value, or `null` if the input was `null`.
     */
    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}
