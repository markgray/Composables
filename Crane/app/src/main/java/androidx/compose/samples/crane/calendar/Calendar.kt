/*
 * Copyright 2020 The Android Open Source Project
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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.samples.crane.calendar.model.CalendarState
import androidx.compose.samples.crane.calendar.model.CalendarUiState
import androidx.compose.samples.crane.calendar.model.Month
import androidx.compose.samples.crane.home.MainViewModel
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

/**
 * This is the Composable that is responsible for producing and managing the [LazyColumn] that holds
 * all of the [LazyListScope.itemsCalendarMonth] created from the [List] of [Month] field
 * [CalendarState.listMonths] of our [CalendarState] parameter [calendarState]. It also has a
 * [LaunchedEffect] which animates the extension of the red background that is used to indicate the
 * selected date range whenever the [CalendarUiState.numberSelectedDays] property of the
 * [CalendarState.calendarUiState] of [calendarState] changes value. We start by initializing our
 * [CalendarUiState] variable `val calendarUiState` to the value of the [CalendarState.calendarUiState]
 * property of our [CalendarState] parameter [calendarState]. We then initialize our [Int] variable
 * `val numberSelectedDays` to the [CalendarUiState.numberSelectedDays] property of `calendarUiState`.
 * We initialize and remember our [Animatable] of [Float] to an [Animatable] whose `initialValue`
 * is 0f.
 *
 * We then start a [LaunchedEffect] keyed on `numberSelectedDays` so that it will run whenever
 * `numberSelectedDays` changes.
 *
 * @param calendarState the [CalendarState] holding all of the information needed to render a 24
 * month calendar.
 * @param onDayClicked a lambda to be called with the [LocalDate] of the day whenever the user
 * clicks one of the days in our calendar. It traces up the hierarchy to [CalendarScreen] which
 * passes `CalendarContent` a lambda for its `onDayClicked` argument that calls the
 * [MainViewModel.onDaySelected] method with the [LocalDate] passed the lambda.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 * @param contentPadding the [PaddingValues] that [Scaffold] passes its `content` Composable lambda
 * argument.
 */
@Composable
fun Calendar(
    calendarState: CalendarState,
    onDayClicked: (date: LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val calendarUiState: CalendarUiState = calendarState.calendarUiState.value
    val numberSelectedDays: Int = calendarUiState.numberSelectedDays.toInt()

    val selectedAnimationPercentage: Animatable<Float, AnimationVector1D> =
        remember(key1 = numberSelectedDays) { Animatable(initialValue = 0f) }
    // Start a Launch Effect when the number of selected days change.
    // using .animateTo() we animate the percentage selection from 0f - 1f
    LaunchedEffect(numberSelectedDays) {
        if (calendarUiState.hasSelectedDates) {

            val animationSpec: TweenSpec<Float> = tween(
                durationMillis =
                (numberSelectedDays.coerceAtLeast(0) * DURATION_MILLIS_PER_DAY)
                    .coerceAtMost(2000),
                easing = EaseOutQuart
            )
            selectedAnimationPercentage.animateTo(
                targetValue = 1f,
                animationSpec = animationSpec
            )
        }
    }

    LazyColumn(
        modifier = modifier.consumeWindowInsets(contentPadding),
        contentPadding = contentPadding
    ) {
        calendarState.listMonths.forEach { month ->
            itemsCalendarMonth(
                calendarUiState,
                onDayClicked,
                { selectedAnimationPercentage.value },
                month
            )
        }

        item(key = "bottomSpacer") {
            Spacer(
                modifier = Modifier.windowInsetsBottomHeight(
                    WindowInsets.navigationBars
                )
            )
        }
    }
}

private fun LazyListScope.itemsCalendarMonth(
    calendarUiState: CalendarUiState,
    onDayClicked: (LocalDate) -> Unit,
    selectedPercentageProvider: () -> Float,
    month: Month
) {
    item(month.yearMonth.month.name + month.yearMonth.year + "header") {
        MonthHeader(
            modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 32.dp),
            month = month.yearMonth.month.name,
            year = month.yearMonth.year.toString()
        )
    }

    // Expanding width and centering horizontally
    val contentModifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
    item(month.yearMonth.month.name + month.yearMonth.year + "daysOfWeek") {
        DaysOfWeek(modifier = contentModifier)
    }

    // A custom key needs to be given to these items so that they can be found in tests that
    // need scrolling. The format of the key is ${year/month/weekNumber}. Thus,
    // the key for the fourth week of December 2020 is "2020/12/4"
    itemsIndexed(month.weeks, key = { index, _ ->
        month.yearMonth.year.toString() +
            "/" +
            month.yearMonth.month.value +
            "/" +
            (index + 1).toString()
    }) { _, week ->
        val beginningWeek = week.yearMonth.atDay(1).plusWeeks(week.number.toLong())
        val currentDay = beginningWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        if (calendarUiState.hasSelectedPeriodOverlap(
                currentDay,
                currentDay.plusDays(6)
            )
        ) {
            WeekSelectionPill(
                state = calendarUiState,
                currentWeekStart = currentDay,
                widthPerDay = CELL_SIZE,
                week = week,
                selectedPercentageTotalProvider = selectedPercentageProvider
            )
        }
        Week(
            calendarUiState = calendarUiState,
            modifier = contentModifier,
            week = week,
            onDayClicked = onDayClicked
        )
        Spacer(Modifier.height(8.dp))
    }
}

/**
 * The ISO-8601 definition, where a week starts on Monday and the first week has a minimum of 4 days.
 * The ISO-8601 standard defines a calendar system based on weeks. It uses the week-based-year and
 * week-of-week-based-year concepts to split up the passage of days instead of the standard year,
 * month. day. Note that the first week may start in the previous calendar year. Note also that the
 * first few days of a calendar year may be in the week-based-year corresponding to the previous
 * calendar year
 */
internal val CALENDAR_STARTS_ON = WeekFields.ISO

/**
 * TODO: Add kdoc
 */
@Preview
@Composable
fun DayPreview() {
    CraneTheme {
        Calendar(CalendarState(), onDayClicked = { })
    }
}
