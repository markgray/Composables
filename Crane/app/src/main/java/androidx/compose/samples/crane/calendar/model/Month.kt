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

import androidx.compose.samples.crane.calendar.Calendar
import java.time.YearMonth

/**
 * The data class that we use to hold information needed to render each of the 24 months that our
 * [Calendar] Composable draws.
 *
 * @param yearMonth the [YearMonth] of this [Month]
 * @param weeks the [List] of [Week] of the weeks in this [Month].
 */
data class Month(
    val yearMonth: YearMonth,
    val weeks: List<Week>
)
