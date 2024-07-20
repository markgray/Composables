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

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * Contains information and methods for keeping track of the days that the user has selected on the
 * calendar.
 *
 * @param selectedStartDate the [LocalDate] of the start of the day range.
 * @param selectedEndDate the [LocalDate] of the end of the day range.
 * @param animateDirection the [AnimationDirection] that should be used when animating the extension
 * of the selection range. One of [AnimationDirection.FORWARDS] or [AnimationDirection.BACKWARDS].
 */
data class CalendarUiState(
    val selectedStartDate: LocalDate? = null,
    val selectedEndDate: LocalDate? = null,
    val animateDirection: AnimationDirection? = null
) {

    /**
     * Returns the number of days between [selectedStartDate] and [selectedEndDate].
     */
    val numberSelectedDays: Float
        get() {
            if (selectedStartDate == null) return 0f
            if (selectedEndDate == null) return 1f
            return ChronoUnit.DAYS.between(selectedStartDate, selectedEndDate.plusDays(1)).toFloat()
        }

    /**
     * Returns `true` if the user has selected at least one day.
     */
    val hasSelectedDates: Boolean
        get() {
            return selectedStartDate != null || selectedEndDate != null
        }

    /**
     * Returns a [String] describing the selected day range. If [selectedStartDate] is `null` we
     * return an empty [String], otherwise we initialize our [String] variable `var output` to the
     * [String] returned by the [LocalDate.format] method of [selectedStartDate] for the
     * [DateTimeFormatter] constant [SHORT_DATE_FORMAT] (a [DateTimeFormatter.ofPattern] with the
     * `pattern` "MMM dd"). Then if [selectedEndDate] is not `null`, we append to `output` the
     * [String] returned by the [LocalDate.format] method of [selectedEndDate] for the formatter
     * [SHORT_DATE_FORMAT]. Finally we return `output`.
     */
    val selectedDatesFormatted: String
        get() {
            if (selectedStartDate == null) return ""
            var output: String = selectedStartDate.format(SHORT_DATE_FORMAT)
            if (selectedEndDate != null) {
                output += " - ${selectedEndDate.format(SHORT_DATE_FORMAT)}"
            }
            return output
        }

    /**
     * Called to check whether the date range defined by the [LocalDate] parameters [start] to [end]
     * overlaps with the date range defined by [selectedStartDate] to [selectedEndDate].
     *  - if [hasSelectedDates] returns `false` we return `false`
     *  - if [selectedStartDate] is `null` and [selectedEndDate] is `null` we return `false`.
     *  - if [selectedStartDate] is equal to [start] or [selectedEndDate] is equal to [end] we
     *  return `true`
     *  - if [selectedEndDate] is `null` we return `true` if [selectedStartDate] is not before [start]
     *  and [selectedStartDate] is not after [end].
     *  - we return `true` if [end] is not before [selectedStartDate] and [start] is not after
     *  [selectedEndDate].
     *
     * @param start the [LocalDate] of the start of the date range whose overlap with the date range
     * defined by [selectedStartDate] to [selectedEndDate] we are checking.
     * @param end the [LocalDate] of the end of the date range whose overlap with the date range
     * defined by [selectedStartDate] to [selectedEndDate] we are checking.
     * @return `true` if the date range defined by the [LocalDate] parameters [start] to [end]
     * overlaps with the date range defined by [selectedStartDate] to [selectedEndDate].
     */
    fun hasSelectedPeriodOverlap(start: LocalDate, end: LocalDate): Boolean {
        if (!hasSelectedDates) return false
        if (selectedStartDate == null && selectedEndDate == null) return false
        if (selectedStartDate == start || selectedStartDate == end) return true
        if (selectedEndDate == null) {
            return !selectedStartDate!!.isBefore(start) && !selectedStartDate.isAfter(end)
        }
        return !end.isBefore(selectedStartDate) && !start.isAfter(selectedEndDate)
    }

    /**
     * Checks whether its [LocalDate] parameter [date] is in the date range defined by
     * [selectedStartDate] to [selectedEndDate].
     *  - if [selectedStartDate] is `null` we return `false`
     *  - if [selectedStartDate] is equal to [date] we return `true`
     *  - if [selectedEndDate] is `null` we return `false`
     *  - if [date] is before [selectedStartDate] or [date] is after [selectedEndDate] we
     *  return `false
     *  - otherwise we return `true`.
     *
     * @param date the [LocalDate] of the date whose presence in the date range defined by
     * [selectedStartDate] to [selectedEndDate] we are checking for.
     * @return `true` is our [LocalDate] parameter [date] is in the date range defined by
     * [selectedStartDate] to [selectedEndDate].
     */
    fun isDateInSelectedPeriod(date: LocalDate): Boolean {
        if (selectedStartDate == null) return false
        if (selectedStartDate == date) return true
        if (selectedEndDate == null) return false
        if (date.isBefore(selectedStartDate) ||
            date.isAfter(selectedEndDate)
        ) return false
        return true
    }

    /**
     * Returns how many days in the week starting at [LocalDate] parameter [currentWeekStartDate]
     * that are included in the the date range defined by [selectedStartDate] to [selectedEndDate].
     * We start by initializing our [Int] variable `var countSelected` to 0, and initializing our
     * [LocalDate] variable `var currentDate` to our [LocalDate] parameter [currentWeekStartDate].
     * Then we loop over `i` from 0 until [CalendarState.DAYS_IN_WEEK]:
     *  - if our [isDateInSelectedPeriod] method returns `true` for [LocalDate] variable `currentDate`
     *  and the [LocalDate.getMonth] of `currentDate` (kotlin `LocalDate.month`) is equal to the
     *  [YearMonth.getMonth] of our [YearMonth] parameter [month] we increment `countSelected`
     *  - we add one day to `currentDate` and loop back for the next day in the week.
     *
     * Finally we return `countSelected` to the caller
     *
     * @param currentWeekStartDate the [LocalDate] of the first day of the week whose number of days
     * in the date range defined by [selectedStartDate] to [selectedEndDate] we are to count.
     * @param month the [YearMonth] of the week whose number of days in the date range defined by
     * [selectedStartDate] to [selectedEndDate] we are to count.
     * @return the number of days in the week starting at [LocalDate] parameter [currentWeekStartDate]
     * that are included in the the date range defined by [selectedStartDate] to [selectedEndDate].
     */
    fun getNumberSelectedDaysInWeek(currentWeekStartDate: LocalDate, month: YearMonth): Int {
        var countSelected = 0
        var currentDate: LocalDate = currentWeekStartDate
        for (i in 0 until CalendarState.DAYS_IN_WEEK) {
            if (isDateInSelectedPeriod(currentDate) && currentDate.month == month.month) {
                countSelected++
            }
            currentDate = currentDate.plusDays(1)
        }
        return countSelected
    }

    /**
     * Returns the number of selected days from the start or end of the week, depending on direction.
     * We branch on the value of our [AnimationDirection] property [animateDirection]:
     *  - [animateDirection] is `null` or [animateDirection] is [AnimationDirection.FORWARDS]:
     *  We initialize our [LocalDate] variable `var startDate` to our [LocalDate] parameter
     *  [currentWeekStartDate], and our [Int] variable `var startOffset` to 0. Then we loop over `i`
     *  from 0 until [CalendarState.DAYS_IN_WEEK] and if our [isDateInSelectedPeriod] method returns
     *  `false` for [LocalDate] variable `startDate` or the [LocalDate.getMonth] of `startDate`
     *  (kotlin `LocalDate.month`) is not equal to [YearMonth.getMonth] of our [YearMonth] parameter
     *  [yearMonth] we increment `startOffset` (otherwise we have entered the date range of this
     *  [CalendarUiState] so we break out of the `for` loop). Otherwise we add 1 day to `startDate`
     *  and loop back for the next day. Once done with the loop we return `startOffset` to the caller.
     *
     *  - [animateDirection] is [AnimationDirection.BACKWARDS]:
     *  We initialize our [LocalDate] variable `var startDate` to our [LocalDate] parameter
     *  [currentWeekStartDate] plus 6 days, and our [Int] variable `var startOffset` to 0. Then we
     *  loop over `i` from 0 until [CalendarState.DAYS_IN_WEEK] and if our [LocalDate] variable
     *  `startDate` is not in our selected date range, or the [LocalDate.getMonth] of `startDate`
     *  (kotlin `LocalDate.month`) is not equal to [YearMonth.getMonth] of our [YearMonth] parameter
     *  [yearMonth] we increment `startOffset` (otherwise we have entered the date range of this
     *  [CalendarUiState] so we break out of the `for` loop). Otherwise we subtract 1 day from
     *  `startDate` and loop back for the next day. Once done with the loop we return 7 minus
     *  `startOffset` to the caller.
     */
    fun selectedStartOffset(currentWeekStartDate: LocalDate, yearMonth: YearMonth): Int {
        return if (animateDirection == null || animateDirection.isForwards()) {
            var startDate: LocalDate = currentWeekStartDate
            var startOffset = 0
            for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                if (!isDateInSelectedPeriod(startDate) || startDate.month != yearMonth.month) {
                    startOffset++
                } else {
                    break
                }
                startDate = startDate.plusDays(1)
            }
            startOffset
        } else {
            var startDate: LocalDate = currentWeekStartDate.plusDays(6)
            var startOffset = 0

            for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                if (!isDateInSelectedPeriod(startDate) || startDate.month != yearMonth.month) {
                    startOffset++
                } else {
                    break
                }
                startDate = startDate.minusDays(1)
            }
            7 - startOffset
        }
    }

    /**
     * Returns `true` if both [LocalDate] parameter [beginningWeek] and the last day of the week before
     * [beginningWeek] are included in the selected date range of this [CalendarUiState].
     *  - If our [LocalDate] parameter [beginningWeek] is `null` we return `false`
     *  - if the [Int] value of the [YearMonth.getMonth] of our [YearMonth] parameter [month] is not
     *  equal to the [Int] value of the [LocalDate.getMonth] of our [LocalDate] parameter [beginningWeek]
     *  we return `false`.
     *  - Otherwise we initialize our [Boolean] variable `val beginningWeekSelected` to `true` if
     *  [beginningWeek] is included in the selected date range of this [CalendarUiState], then we
     *  initialize our [LocalDate] variable `val lastDayPreviousWeek` to [beginningWeek] minus 1 day.
     *  Finally we we return `true` if `lastDayPreviousWeek` is included in the selected date range
     *  of this [CalendarUiState] and our [Boolean] variable `beginningWeekSelected` is `true`.
     *
     * @param beginningWeek the [LocalDate] of the day we are interested in.
     * @param month the [YearMonth] of the day we are interested in.
     * @return `true` if our [LocalDate] parameter [beginningWeek] and the day before it are both
     * included in the selected date range of this [CalendarUiState].
     */
    fun isLeftHighlighted(beginningWeek: LocalDate?, month: YearMonth): Boolean {
        return if (beginningWeek != null) {
            if (month.month.value != beginningWeek.month.value) {
                false
            } else {
                val beginningWeekSelected: Boolean = isDateInSelectedPeriod(beginningWeek)
                val lastDayPreviousWeek: LocalDate = beginningWeek.minusDays(1)
                isDateInSelectedPeriod(lastDayPreviousWeek) && beginningWeekSelected
            }
        } else {
            false
        }
    }

    /**
     * Returns `true` if the last day of the week starting at [LocalDate] parameter [beginningWeek]
     * and the first day the week after that week are included in the selected date range of this
     * [CalendarUiState]. We begin by initializing our [LocalDate] variable `val lastDayOfTheWeek`
     * to our [LocalDate] parameter [beginningWeek] plus 6 days. Then:
     *  - If `lastDayOfTheWeek` is `null` we return `false`
     *  - if the [Int] value of the [YearMonth.getMonth] of our [YearMonth] parameter [month] is not
     *  equal to the [Int] value of the [LocalDate.getMonth] of our [LocalDate] variable
     *  `lastDayOfTheWeek` we return `false`
     *  - Otherwise we initialize our [Boolean] variable `val lastDayOfTheWeekSelected` to `true` if
     *  `lastDayOfTheWeek` is included in the selected date range of this [CalendarUiState], then we
     *  initialize our [LocalDate] variable `val firstDayNextWeek` to `lastDayOfTheWeek` plus 1 day.
     *  Finally we we return `true` if `firstDayNextWeek` is included in the selected date range
     *  of this [CalendarUiState] and our [Boolean] variable `lastDayOfTheWeekSelected` is `true`.
     *
     * @param beginningWeek the [LocalDate] of the first day of the week we interested in.
     * @param month the [YearMonth] of the week we are interested in.
     * @return `true` if the last day of the week beginning on [LocalDate] parameter [beginningWeek]
     * and the first day of the next week are both included in the selected date range of this
     * [CalendarUiState].
     */
    fun isRightHighlighted(
        beginningWeek: LocalDate?,
        month: YearMonth
    ): Boolean {
        val lastDayOfTheWeek = beginningWeek?.plusDays(6)
        return if (lastDayOfTheWeek != null) {
            if (month.month.value != lastDayOfTheWeek.month.value) {
                false
            } else {
                val lastDayOfTheWeekSelected = isDateInSelectedPeriod(lastDayOfTheWeek)
                val firstDayNextWeek = lastDayOfTheWeek.plusDays(1)
                isDateInSelectedPeriod(firstDayNextWeek) && lastDayOfTheWeekSelected
            }
        } else {
            false
        }
    }

    /**
     * Returns 0 if the [selectedEndDate] or the [selectedStartDate] is in the week starting at
     * [LocalDate] parameter [currentWeekStartDate], otherwise it returns the number of days until
     * [selectedEndDate] and the end of the week beginning on [currentWeekStartDate] when
     * [animateDirection] is [AnimationDirection.BACKWARDS], or the number of days since
     * [selectedStartDate] and [currentWeekStartDate] when [animateDirection] is
     * [AnimationDirection.FORWARDS].
     */
    fun dayDelay(currentWeekStartDate: LocalDate): Int {
        if (selectedStartDate == null && selectedEndDate == null) return 0
        // if selected week contains start date, don't have any delay
        val endWeek = currentWeekStartDate.plusDays(6)
        return if (animateDirection != null && animateDirection.isBackwards()) {
            if (selectedEndDate?.isBefore(currentWeekStartDate) == true ||
                selectedEndDate?.isAfter(endWeek) == true
            ) {
                // selected end date is not in current week - return actual days calc difference
                abs(ChronoUnit.DAYS.between(endWeek, selectedEndDate)).toInt()
            } else {
                0
            }
        } else {
            if (selectedStartDate?.isBefore(currentWeekStartDate) == true ||
                selectedStartDate?.isAfter(endWeek) == true
            ) {
                // selected start date is not in current week
                abs(ChronoUnit.DAYS.between(currentWeekStartDate, selectedStartDate)).toInt()
            } else {
                0
            }
        }
    }

    fun monthOverlapSelectionDelay(
        currentWeekStartDate: LocalDate,
        week: Week
    ): Int {
        return if (animateDirection?.isBackwards() == true) {
            val endWeek = currentWeekStartDate.plusDays(6)
            val isStartInADifferentMonth = endWeek.month != week.yearMonth.month
            if (isStartInADifferentMonth) {
                var currentDate = endWeek
                var offset = 0
                for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                    if (currentDate.month.value != week.yearMonth.month.value &&
                        isDateInSelectedPeriod(currentDate)
                    ) {
                        offset++
                    }
                    currentDate = currentDate.minusDays(1)
                }
                offset
            } else {
                0
            }
        } else {
            val isStartInADifferentMonth = currentWeekStartDate.month != week.yearMonth.month
            return if (isStartInADifferentMonth) {
                var currentDate = currentWeekStartDate
                var offset = 0
                for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                    if (currentDate.month.value != week.yearMonth.month.value &&
                        isDateInSelectedPeriod(currentDate)
                    ) {
                        offset++
                    }
                    currentDate = currentDate.plusDays(1)
                }
                offset
            } else {
                0
            }
        }
    }

    fun setDates(newFrom: LocalDate?, newTo: LocalDate?): CalendarUiState {
        return if (newTo == null) {
            copy(selectedStartDate = newFrom)
        } else {
            copy(selectedStartDate = newFrom, selectedEndDate = newTo)
        }
    }

    companion object {
        private val SHORT_DATE_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMM dd")
    }
}
