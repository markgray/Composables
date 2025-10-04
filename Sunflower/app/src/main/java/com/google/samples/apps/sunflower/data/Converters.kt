/*
 * Copyright 2018 Google LLC
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

package com.google.samples.apps.sunflower.data

import androidx.room.TypeConverter
import java.util.Calendar

/**
 * Type converters to allow Room to reference complex data types.
 */
class Converters {
    /**
     * Converts its [Calendar] parameter [calendar] to a [Long] value, the number of milliseconds
     * since January 1, 1970, 00:00:00 GMT that is stored in the [Calendar.timeInMillis] property
     * of [Calendar] parameter [calendar]. The @[TypeConverter] annotation marks the method as a
     * type converter for Room.
     *
     * @param calendar the [Calendar] value to convert.
     * @return the [Long] value of the [Calendar] parameter [calendar], its [Calendar.timeInMillis]
     * property.
     */
    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    /**
     * Converts its [Long] parameter [value] to a [Calendar] instance. The @[TypeConverter]
     * annotation marks the method as a type converter for Room.
     *
     * @param value the [Long] value to convert.
     * @return a [Calendar] instance whose [Calendar.timeInMillis] property is set to the [Long]
     * parameter [value].
     */
    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}
