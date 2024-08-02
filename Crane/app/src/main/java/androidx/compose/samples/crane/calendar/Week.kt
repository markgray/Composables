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
import androidx.compose.foundation.layout.RowScope
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
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

/**
 * The Composable displays the first letter of each day of the week in a header at the top of each
 * month. Our root Composable is a [Row] whose `modifier` argument chains a
 * [Modifier.clearAndSetSemantics] to our [Modifier] parameter [modifier] to clear the semantics of
 * all of its descendant nodes. In the [RowScope] `content` lambda we loop through all of the
 * [DayOfWeek] in the [DayOfWeek.entries] composing a [DayOfWeekHeading] whose `day` argument is the
 * first letter of the [DayOfWeek.name] of the current [DayOfWeek].
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
        for (day: DayOfWeek in DayOfWeek.entries) {
            DayOfWeekHeading(day = day.name.take(1))
        }
    }
}

/**
 * This Composable is used by the private `LazyListScope.itemsCalendarMonth` extension function in
 * the file `Calendar.kt` to display the 7 days in its [Week] parameter [week] in a [Day] Composable
 * and if the day falls in the previous or following month it will compose an empty [Box] instead.
 * We start by initializing our [LocalDate] variable `val beginningWeek` to the first day of the
 * month that our [Week] parameter [week] is in plus the number of weeks of the [Week.number] of
 * [week]. We then initialize our [LocalDate] variable `var currentDay` to the [DayOfWeek.MONDAY]
 * of the week containing `beginningWeek`.
 *
 * Our root Composable is a [Box], and in the `content` composable lambda of the [Box] we have a
 * [Row] whose `modifier` argument is our [Modifier] parameter [modifier]. In the [RowScope] `content`
 * lambda argument of the [Row] we have:
 *  - a [Spacer] whose `modifier` argument is a [RowScope.weight] whose `weight` is 1f, (which causes
 *  it to split all the remaining incoming horizontal constraint with the similarly weighted [Spacer]
 *  at the end of the [Row] after the unweighted siblings have been measured and placed) with a
 *  [Modifier.heightIn] whose `max` value is [CELL_SIZE] (48.dp) chained to that (causes the maximum
 *  height of the [Spacer] to be limited to 48.dp).
 *  - We loop over `var i` in 0 until 6 branching on whether the [LocalDate.getMonth] of `currentDay`
 *  is equal to the [YearMonth.getMonth] of the [Week.yearMonth] of [Week] parameter [week]:
 *  * they are in the same month so we compose a [Day] whose `calendarState` argument is our
 *  [CalendarUiState] parameter [calendarUiState], whose `day` argument is our [LocalDate] variable
 *  `currentDay`, whose `onDayClicked` argument is our lambda parameter [onDayClicked], and whose
 *  `month` argument is the [Week.yearMonth] of our [Week] parameter [week].
 *  * they are NOT in the same month so we compose a [Box] whose `modifier` argument is a
 *  [Modifier.size] that sets its size to [CELL_SIZE] (48.dp)
 *  * in either case we add 1 day to [LocalDate] variable `currentDay` and loop around.
 *  - a [Spacer] whose `modifier` argument is a [RowScope.weight] whose `weight` is 1f, (which causes
 *  it to split all the remaining incoming horizontal constraint with the similarly weighted [Spacer]
 *  at the beginning of the [Row] after the unweighted siblings have been measured and placed) with a
 *  [Modifier.heightIn] whose `max` value is [CELL_SIZE] (48.dp) chained to that (causes the maximum
 *  height of the [Spacer] to be limited to 48.dp).
 *
 * @param calendarUiState the current [CalendarUiState] that we can query for the selected state of
 * the [Day] being composed. We just pass it to the [Day] composable as its `calendarState` argument.
 * @param week the [Week] whose days we are to display in [Day] composables.
 * @param onDayClicked a lambda that should be called when the user clicks on a [Day] with the
 * [LocalDate] that the [Day] represents.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller, the private `LazyListScope.itemsCalendarMonth` extension function in the
 * file `Calendar.kt` calls us with a [Modifier.fillMaxWidth] with a [Modifier.wrapContentWidth]
 * chained to that to have us fill our incoming width constraint and center our content horizontally.
 */
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
                    .weight(weight = 1f)
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
                    Box(modifier = Modifier.size(size = CELL_SIZE))
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

/**
 * The size of a [Day] in our calendar.
 */
internal val CELL_SIZE = 48.dp
