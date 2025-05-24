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

package com.example.compose.jetsurvey.util

import java.util.Calendar

/**
 * Returns the start of today in milliseconds. We start by initializing our [Calendar] variable `cal`
 * to a new instance, initialize our [Int] variables `year`, `month`, and `date` to the current
 * [Calendar.YEAR], [Calendar.MONTH], and [Calendar.DATE]. We then call the [Calendar.clear] method
 * of `cal` to clear all fields of `cal`, then call the [Calendar.set] method to set the [Calendar.YEAR],
 * [Calendar.MONTH], and [Calendar.DATE] fields of `cal` to the current [Calendar.YEAR],
 * [Calendar.MONTH], and [Calendar.DATE]. Finally, we call the [Calendar.timeInMillis] method to
 * return the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this calendar.
 *
 * @return The start of today in milliseconds.
 */
fun getDefaultDateInMillis(): Long {
    val cal: Calendar = Calendar.getInstance()
    val year: Int = cal.get(Calendar.YEAR)
    val month: Int = cal.get(Calendar.MONTH)
    val date: Int = cal.get(Calendar.DATE)
    cal.clear()
    cal.set(year, month, date)
    return cal.timeInMillis
}
