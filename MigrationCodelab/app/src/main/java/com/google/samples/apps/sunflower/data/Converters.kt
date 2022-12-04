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
     * Converts its [Calendar] parameter [calendar] to a [Long] by using the [Calendar.getTimeInMillis]
     * (kotlin `timeInMillis` property) to fetch the time of the [Calendar] as UTC milliseconds from
     * the epoch. The `@TypeConverter` annotation Marks the method as a type converter for Room to use
     * to store [Calendar] values in a database.
     *
     * @param calendar a [Calendar] instance
     * @return the time of the [Calendar] parameter [calendar] as UTC milliseconds from the epoch.
     */
    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    /**
     * Converts its [Long] parameter [value] (a time in UTC milliseconds from the epoch) to a
     * [Calendar] instance and returns it. The `@TypeConverter` annotation Marks the method as a
     * type converter for Room to use to recreate [Calendar] values from the [Long] values used to
     * store them in a database. We call the [Calendar.getInstance] method to create a [Calendar]
     * based on the current time in the default time zone with the default locale, use the [apply]
     * extension function on it to call its [Calendar.setTimeInMillis] method (kotlin `timeInMillis`
     * property) to set its UTC milliseconds from the epoch time to our [Long] parameter [value],
     * then return the [Calendar] instance.
     *
     * @param value a [Long] time in UTC milliseconds from the epoch that we should convert to a
     * [Calendar] instance.
     * @return a [Calendar] instance whose time in UTC milliseconds from the epoch is our [Long]
     * parameter [value].
     */
    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}
