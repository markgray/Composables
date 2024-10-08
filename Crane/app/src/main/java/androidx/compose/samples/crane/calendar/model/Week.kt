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

package androidx.compose.samples.crane.calendar.model

import java.time.YearMonth

/**
 * Used to model a single week.
 *
 * @param number the week number of this [Week] in its [YearMonth] (0 to whatever)
 * @param yearMonth the [YearMonth] that this [Week] is in.
 */
data class Week(
    val number: Int,
    val yearMonth: YearMonth
)
