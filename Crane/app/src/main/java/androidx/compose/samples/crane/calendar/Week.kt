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

package androidx.compose.samples.crane.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.calendar.model.CalendarUiState
import androidx.compose.samples.crane.calendar.model.Week
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * The Composable displays the first letter of each day of the week in a header at the top of each
 * month.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller (the private extension function `LazyListScope.itemsCalendarMonth` in the
 * file `Calendar.kt`) calls us with a [Modifier.fillMaxWidth] with a [Modifier.wrapContentWidth]
 * whose `align` is [Alignment.CenterHorizontally] chained to that (expanding width and centering
 * horizontally).
 */
@Composable
internal fun DaysOfWeek(modifier: Modifier = Modifier) {
    Row(modifier = modifier.clearAndSetSemantics { }) {
        for (day: DayOfWeek in DayOfWeek.values()) {
            DayOfWeekHeading(day = day.name.take(1))
        }
    }
}

@Composable
internal fun Week(
    calendarUiState: CalendarUiState,
    week: Week,
    onDayClicked: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val beginningWeek: LocalDate = week.yearMonth.atDay(1).plusWeeks(week.number.toLong())
    var currentDay: LocalDate = beginningWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    Box {
        Row(modifier = modifier) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = CELL_SIZE)
            )
            for (i in 0..6) {
                if (currentDay.month == week.yearMonth.month) {
                    Day(
                        calendarState = calendarUiState,
                        day = currentDay,
                        onDayClicked = onDayClicked,
                        month = week.yearMonth
                    )
                } else {
                    Box(modifier = Modifier.size(CELL_SIZE))
                }
                currentDay = currentDay.plusDays(1)
            }
            Spacer(
                Modifier
                    .weight(1f)
                    .heightIn(max = CELL_SIZE)
            )
        }
    }
}

internal val CELL_SIZE = 48.dp
