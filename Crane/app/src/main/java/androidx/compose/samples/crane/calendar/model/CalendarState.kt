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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
     * state of the calendar being displayed.
     */
    val calendarUiState = mutableStateOf(CalendarUiState())

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
            val month = Month(startYearMonth, listWeekItems)
            tempListMonths.add(month)
            startYearMonth = startYearMonth.plusMonths(1)
        }
        listMonths = tempListMonths.toList()
    }

    /**
     * TODO: Add kdoc
     */
    fun setSelectedDay(newDate: LocalDate) {
        calendarUiState.value = updateSelectedDay(newDate)
    }

    private fun updateSelectedDay(newDate: LocalDate): CalendarUiState {
        val currentState = calendarUiState.value
        val selectedStartDate = currentState.selectedStartDate
        val selectedEndDate = currentState.selectedEndDate

        return when {
            selectedStartDate == null && selectedEndDate == null -> {
                currentState.setDates(newDate, null)
            }

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

            selectedStartDate == null -> {
                if (newDate.isBefore(selectedEndDate)) {
                    currentState.copy(animateDirection = AnimationDirection.BACKWARDS)
                        .setDates(newDate, selectedEndDate)
                } else if (newDate.isAfter(selectedEndDate)) {
                    currentState.copy(animateDirection = AnimationDirection.FORWARDS)
                        .setDates(selectedEndDate, newDate)
                } else {
                    currentState
                }
            }

            else -> {
                if (newDate.isBefore(selectedStartDate)) {
                    currentState.copy(animateDirection = AnimationDirection.BACKWARDS)
                        .setDates(newDate, selectedStartDate)
                } else if (newDate.isAfter(selectedStartDate)) {
                    currentState.copy(animateDirection = AnimationDirection.FORWARDS)
                        .setDates(selectedStartDate, newDate)
                } else {
                    currentState
                }
            }
        }
    }

    companion object {
        /**
         * TODO: Add kdoc
         */
        const val DAYS_IN_WEEK: Int = 7
    }
}
