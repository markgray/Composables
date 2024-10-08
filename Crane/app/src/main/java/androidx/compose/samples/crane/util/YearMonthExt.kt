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

package androidx.compose.samples.crane.util

import androidx.compose.samples.crane.calendar.CALENDAR_STARTS_ON
import java.time.YearMonth
import java.time.temporal.WeekFields

/**
 * Extension function that returns the number of weeks in its [YearMonth] receiver. The default
 * value for its [WeekFields] parameter [weekFields] is [CALENDAR_STARTS_ON] which specifies the
 * The ISO-8601 definition, where a week starts on Monday and the first week has a minimum of 4 days.
 * The returned value includes both the partial first and last weeks of the [YearMonth].
 */
fun YearMonth.getNumberWeeks(weekFields: WeekFields = CALENDAR_STARTS_ON): Int {
    val firstWeekNumber: Int = this.atDay(1)[weekFields.weekOfMonth()]
    val lastWeekNumber: Int = this.atEndOfMonth()[weekFields.weekOfMonth()]
    return lastWeekNumber - firstWeekNumber + 1 // Both weeks inclusive
}
