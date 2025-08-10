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

@file:Suppress("RedundantSuppression")

package androidx.compose.samples.crane.calendar.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.samples.crane.home.MainViewModel
import androidx.compose.samples.crane.util.getNumberWeeks
import java.time.LocalDate
import java.time.Period
import java.time.YearMonth

/**
 * Contains information and methods needed to model a calendar.
 */
class CalendarState {

    /**
     * Our [MutableState] wrapped instance of [CalendarUiState]. It represents the actual current
     * "selected days state" of the calendar being displayed, and contains methods to query and
     * manipulate them.
     */
    val calendarUiState: MutableState<CalendarUiState> = mutableStateOf(CalendarUiState())

    /**
     * The [List] of [Month] that we are concerned with. It is initialized to start at the [Month]
     * containing the first day of the current year, and contains [Month] instances for two years
     * ending in The [Month] for December.
     */
    val listMonths: List<Month>

    /**
     * Defaulting to starting at 1/01 of current year.
     */
    private val calendarStartDate: LocalDate = LocalDate.now()
        .withMonth(1).withDayOfMonth(1)

    /**
     * Defaulting to 2 years from current date.
     */
    private val calendarEndDate: LocalDate = LocalDate.now().plusYears(2)
        .withMonth(12).withDayOfMonth(31)

    /**
     * Used to determine the number of months between [calendarStartDate] and [calendarEndDate]
     * when building our [List] of [Month] field [listMonths] in our `init` block.
     */
    private val periodBetweenCalendarStartEnd: Period = Period.between(
        calendarStartDate,
        calendarEndDate
    )

    init {
        val tempListMonths = mutableListOf<Month>()
        var startYearMonth = YearMonth.from(calendarStartDate)
        for (numberMonth in 0..periodBetweenCalendarStartEnd.toTotalMonths()) {
            val numberWeeks = startYearMonth.getNumberWeeks()
            val listWeekItems = mutableListOf<Week>()
            for (week in 0 until numberWeeks) {
                listWeekItems.add(
                    Week(
                        number = week,
                        yearMonth = startYearMonth
                    )
                )
            }
            val month = Month(yearMonth = startYearMonth, weeks = listWeekItems)
            tempListMonths.add(month)
            startYearMonth = startYearMonth.plusMonths(1)
        }
        listMonths = tempListMonths.toList()
    }

    /**
     * Called by [MainViewModel.onDaySelected] when the user clicks on one of the days in the
     * calendar. We set the `value` of our [MutableState] wrapped [CalendarUiState] field
     * [calendarUiState] to the value returned by our [updateSelectedDay] when called with its
     * `newDate` argument our [LocalDate] parameter [newDate].
     *
     * @param newDate the [LocalDate] of the day that was clicked.
     */
    fun setSelectedDay(newDate: LocalDate) {
        calendarUiState.value = updateSelectedDay(newDate = newDate)
    }

    /**
     * Returns an instance of [CalendarUiState] that represents the new "selected days state" of the
     * calendar that results when the day representing [LocalDate] parameter [newDate] is clicked.
     * We start by initializing our [CalendarUiState] variable `val currentState` to the value of
     * our [MutableState] wrapped [CalendarUiState] field [calendarUiState]. We initialize our
     * [LocalDate] variable `val selectedStartDate` to the value of the [CalendarUiState.selectedStartDate]
     * property of `currentState`, and [LocalDate] variable `val selectedEndDate` to the value of the
     * [CalendarUiState.selectedEndDate] property of `currentState`. Then we use a `when` switch to
     * return different [CalendarUiState] based on:
     *  - Both `selectedStartDate` and `selectedEndDate` are `null` (no selected date range exists)
     *  we return the [CalendarUiState] that the [CalendarUiState.setDates] method of `currentState`
     *  returns when called with its `newFrom` argument our [LocalDate] parameter [newDate], and its
     *  `newTo` argument `null` (only the day just clicked is "selected").
     *  - Both `selectedStartDate` and `selectedEndDate` are non-`null` (a selected date range already
     *  exists). We initialize our [AnimationDirection] variable `val animationDirection` to
     *  [AnimationDirection.BACKWARDS] if our [LocalDate] parameter [newDate] is before `selectedStartDate`
     *  or to [AnimationDirection.FORWARDS] if it is after or equal to `selectedStartDate`. Then we
     *  set the [MutableState.value] of our [MutableState] wrapped [CalendarUiState] field
     *  [calendarUiState] to a copy of `currentState` with its [CalendarUiState.selectedStartDate]
     *  propery set to `null`, its [CalendarUiState.selectedEndDate] property set to `null` and its
     *  [CalendarUiState.animateDirection] set to `animationDirection`. Then we call ourselves again
     *  with the same [LocalDate] argument [newDate] (resets the selected date range which causes
     *  the second call to return a [CalendarUiState] with just the [CalendarUiState.selectedStartDate]
     *  set to [newDate].
     *  - `selectedStartDate` is `null` and `selectedEndDate` is non-`null`. I do not see how this
     *  can happen (and until I do, I won't waste time commenting it).
     *  - `else` is reached when `selectedStartDate` is non-`null` and `selectedEndDate` is `null`.
     *  If [LocalDate] parameter [newDate] is before `selectedStartDate` we copy [CalendarUiState]
     *  variable `currentState` with its [CalendarUiState.animateDirection] set to
     *  [AnimationDirection.BACKWARDS], then return the [CalendarUiState] that results when we call
     *  the [CalendarUiState.setDates] method on that copy with the `newFrom` argument our [LocalDate]
     *  parameter [newDate] and the `newTo` argument `selectedStartDate`. Else if [newDate] is after
     *  `selectedStartDate` we copy [CalendarUiState] variable `currentState` with its
     *  [CalendarUiState.animateDirection] set to [AnimationDirection.FORWARDS], then return the
     *  [CalendarUiState] that results when we call the [CalendarUiState.setDates] method on that
     *  copy with the `newFrom` argument our `selectedStartDate` and the `newTo` argument [LocalDate]
     *  parameter [newDate]. Else [newDate] is equal to `selectedStartDate` so we just return an
     *  unmodified [CalendarUiState] variable `currentState`.
     *
     * @param newDate the [LocalDate] of the day in the calendar that was clicked.
     * @return a new [CalendarUiState] which reflects the new selected days caused by the click of
     * the day whose [LocalDate] is our [LocalDate] parameter [newDate].
     */
    private fun updateSelectedDay(newDate: LocalDate): CalendarUiState {
        val currentState: CalendarUiState = calendarUiState.value
        val selectedStartDate: LocalDate? = currentState.selectedStartDate
        val selectedEndDate: LocalDate? = currentState.selectedEndDate

        return when {
            // No existing start or end date
            selectedStartDate == null && selectedEndDate == null -> {
                currentState.setDates(newFrom = newDate, newTo = null)
            }

            // Both start date and end date are not null. Reset both to null and call our selves again.
            selectedStartDate != null && selectedEndDate != null -> {
                val animationDirection: AnimationDirection =
                    if (newDate.isBefore(selectedStartDate)) {
                        AnimationDirection.BACKWARDS
                    } else {
                        AnimationDirection.FORWARDS
                    }
                @Suppress("RedundantValueArgument")
                this.calendarUiState.value = currentState.copy(
                    selectedStartDate = null,
                    selectedEndDate = null,
                    animateDirection = animationDirection
                )
                updateSelectedDay(newDate = newDate)
            }

            // Start date is null but end date is not (How can this happen?)
            selectedStartDate == null -> {
                if (newDate.isBefore(selectedEndDate)) {
                    currentState.copy(animateDirection = AnimationDirection.BACKWARDS)
                        .setDates(newFrom = newDate, newTo = selectedEndDate)
                } else if (newDate.isAfter(selectedEndDate)) {
                    currentState.copy(animateDirection = AnimationDirection.FORWARDS)
                        .setDates(newFrom = selectedEndDate, newTo = newDate)
                } else {
                    currentState
                }
            }

            // Start date is not null
            else -> {
                if (newDate.isBefore(selectedStartDate)) {
                    currentState.copy(animateDirection = AnimationDirection.BACKWARDS)
                        .setDates(newFrom = newDate, newTo = selectedStartDate)
                } else if (newDate.isAfter(selectedStartDate)) {
                    currentState.copy(animateDirection = AnimationDirection.FORWARDS)
                        .setDates(newFrom = selectedStartDate, newTo = newDate)
                } else {
                    // newDate is the same as start date, do nothing.
                    currentState
                }
            }
        }
    }

    companion object {
        /**
         * Constant for number of days in a week.
         */
        const val DAYS_IN_WEEK: Int = 7
    }
}
