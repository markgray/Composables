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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.calendar.model.AnimationDirection
import androidx.compose.samples.crane.calendar.model.CalendarState
import androidx.compose.samples.crane.calendar.model.CalendarUiState
import androidx.compose.samples.crane.calendar.model.Week
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/**
 * Draws the RoundedRect red background of the calendar date selection. Animates the selection based
 * on the direction of selection change. [CalendarUiState] stores the `selectedStartDate` and
 * `selectedEndDate` of the selection, as well as the [AnimationDirection]. These dates are then
 * used to determine a few things: The start offset of the rounded rect drawing, the delay when the
 * pill animation for the week should start and the size of the rounded rect that should be drawn.
 *
 * We start by initializing our [Float] variable `val widthPerDayPx` to the current [LocalDensity]
 * number of pixels in our [Dp] parameter [widthPerDay], and initializing our [Float] variable
 * `val cornerRadiusPx` to the current [LocalDensity] number of pixels in 24.dp. Then our root
 * Composable is a [Canvas] whose `modifier` argument chains a [Modifier.fillMaxWidth] to our
 * [Modifier] parameter [modifier] to have it take up its entire incoming width constraint, and in
 * its `onDraw` argument we have a lambda which:
 *  - uses decomposition to initialize its [Offset] variable `offset` and its [Float] variable `size`
 *  from the the [Pair] of [Offset] and [Float] returned by the [getOffsetAndSize] method when called
 *  with its `width` argument the [Size.width] of the current drawing environment, with its `state`
 *  argument our [CalendarUiState] parameter [state], with its `currentWeekStart` argument our
 *  [LocalDate] parameter [currentWeekStart], with its `week` argument our [Week] parameter [week],
 *  with its `widthPerDayPx` our [Float] variable `widthPerDayPx`, with its `cornerRadiusPx` our
 *  [Float] variable `cornerRadiusPx` and with its `selectedPercentage` variable the [Float] returned
 *  by our lambda parameter [selectedPercentageTotalProvider].
 *  - it then initializes its [Float] variable `val translationX` to minus `size` if the
 *  [CalendarUiState.animateDirection] is [AnimationDirection.BACKWARDS], or to 0f if it is not.
 *  - it uses the [DrawScope.translate] method to translate `left` by `translationX` then draws a
 *  [DrawScope.drawRoundRect] whose `color` argument is our [Color] parameter [pillColor], whose
 *  `topLeft` argument is our [Offset] variable `offset`, whose `size` argument is a [Size] whose
 *  `width` is our [Float] variable `size` and whose `height` is our [Float] variable `widthPerDayPx`,
 *  and the `cornerRadius` argument is a [CornerRadius] whose `x` argument is our [Float] variable
 *  `cornerRadiusPx`.
 *
 * @param week the [Week] in the calendar whose [WeekSelectionPill] we are drawing. Our caller the
 * private `LazyListScope.itemsCalendarMonth` extension function in the file `Calendar.kt` has
 * already verified that our [Week] parameter [week] contains days which are in the selected date
 * of our [CalendarUiState] parameter [state] before bothering to call us.
 * @param currentWeekStart this is the [LocalDate] of the Monday of the [Week] parameter [week] even
 * if it is in the previous month.
 * @param state this is the [CalendarUiState] which contains the [CalendarUiState.selectedStartDate],
 * [CalendarUiState.selectedEndDate], and [CalendarUiState.animateDirection] which define the current
 * selected date range that dictates where and when we should draw our RoundedRect red background of
 * the calendar date selection.
 * @param selectedPercentageTotalProvider provides a [Float] which animates from 0f to 1f to represent
 * the progress of the animation of the extension of the selected date range red rounded rectangle.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller the private `LazyListScope.itemsCalendarMonth` extension function in the file
 * `Calendar.kt` does not pass us one so the empty, default, or starter [Modifier] that contains no
 * elements is used.
 * @param widthPerDay the [Dp] width of a day when it is render in our calendar. Our caller, the
 * private `LazyListScope.itemsCalendarMonth` extension function in the file `Calendar.kt` passes us
 * [CELL_SIZE] (48.dp)
 * @param pillColor the [Color] we are to draw our selected date range RoundedRect. Defaults to the
 * [Colors.secondary] of our [CraneTheme] custom [MaterialTheme.colors].
 *
 */
@Composable
fun WeekSelectionPill(
    week: Week,
    currentWeekStart: LocalDate,
    state: CalendarUiState,
    selectedPercentageTotalProvider: () -> Float,
    modifier: Modifier = Modifier,
    widthPerDay: Dp = 48.dp,
    pillColor: Color = MaterialTheme.colors.secondary
) {
    val widthPerDayPx: Float = with(LocalDensity.current) { widthPerDay.toPx() }
    val cornerRadiusPx: Float = with(LocalDensity.current) { 24.dp.toPx() }

    Canvas(
        modifier = modifier.fillMaxWidth(),
        onDraw = {
            val (offset: Offset, size: Float) = getOffsetAndSize(
                width = this.size.width,
                state = state,
                currentWeekStart = currentWeekStart,
                week = week,
                widthPerDayPx = widthPerDayPx,
                cornerRadiusPx = cornerRadiusPx,
                selectedPercentage = selectedPercentageTotalProvider()
            )

            val translationX: Float = if (state.animateDirection?.isBackwards() == true) -size else 0f

            translate(left = translationX) {
                drawRoundRect(
                    color = pillColor,
                    topLeft = offset,
                    size = Size(width = size, height = widthPerDayPx),
                    cornerRadius = CornerRadius(x = cornerRadiusPx)
                )
            }
        }
    )
}

/**
 * Calculates the animated Offset and Size of the red selection pill based on the [CalendarUiState]
 * and the Week SelectionState, based on the overall [selectedPercentage]. We start by initializing
 * our [Int] variable `val numberDaysSelected` to the value returned by the
 * [CalendarUiState.getNumberSelectedDaysInWeek] method of [CalendarUiState] parameter [state] for
 * the week starting on [LocalDate] parameter [currentWeekStart] of the [Week.yearMonth] of [Week]
 * parameter [week] (this is the number of days in the week that are included in the selected date
 * range defined by [CalendarUiState.selectedStartDate] to [CalendarUiState.selectedEndDate]). We
 * then initialize our [Int] variable `val monthOverlapDelay` to the value returned by the
 * [CalendarUiState.monthOverlapSelectionDelay] method of [CalendarUiState] parameter [state] for
 * the week starting on [LocalDate] parameter [currentWeekStart] of the [Week.yearMonth] of [Week]
 * parameter [week] (this is the number of days of the [Week] that are in the month before the first
 * day of the week or the month after the last day of the week). We initialize our [Int] variable
 * `val dayDelay` to the value returned by the [CalendarUiState.dayDelay] method of [CalendarUiState]
 * parameter [state] for our [LocalDate] parameter [currentWeekStart] (this is the number of days
 * between [CalendarUiState.selectedStartDate] and the first day of the week when the
 * [CalendarUiState.animateDirection] is [AnimationDirection.FORWARDS] or between
 * [CalendarUiState.selectedEndDate] and the end day of the week when the
 * [CalendarUiState.animateDirection] is [AnimationDirection.FORWARDS], or 0 if either the
 * [CalendarUiState.selectedStartDate] or [CalendarUiState.selectedEndDate] occurs in the week). We
 * initialize our [Float] variable `val edgePadding` to one half of the quantity [width] minus
 * [widthPerDayPx] times [CalendarState.DAYS_IN_WEEK]. We initialize our [Float] variable
 * `val percentagePerDay` to 1 divided by the value of the [CalendarUiState.numberSelectedDays]
 * property. We initialize our [Float] variable `val startPercentage` to `percentagePerDay` times
 * the quantity `dayDelay` plus `monthOverlapDelay`
 *
 * @param width the [Size.width] of the current drawing environment.
 * @param state the current [CalendarUiState] containing information about the selected date range.
 * @param currentWeekStart the [LocalDate] of the Monday of the [Week] parameter [week] even
 * if it is in the previous month.
 * @param week the [Week] in the calendar whose [WeekSelectionPill] is being drawn.
 * @param widthPerDayPx the width of a day in pixels.
 * @param cornerRadiusPx the radius of the [CornerRadius] used by the [DrawScope.drawRoundRect].
 * @param selectedPercentage the animated [Float] that represents the current progress of the
 * animated extension of the date range "pill".
 */
private fun getOffsetAndSize(
    width: Float,
    state: CalendarUiState,
    currentWeekStart: LocalDate,
    week: Week,
    widthPerDayPx: Float,
    cornerRadiusPx: Float,
    selectedPercentage: Float
): Pair<Offset, Float> {
    val numberDaysSelected: Int = state.getNumberSelectedDaysInWeek(currentWeekStart, week.yearMonth)
    val monthOverlapDelay: Int = state.monthOverlapSelectionDelay(currentWeekStart, week)
    val dayDelay: Int = state.dayDelay(currentWeekStart)
    val edgePadding: Float = (width - widthPerDayPx * CalendarState.DAYS_IN_WEEK) / 2

    val percentagePerDay: Float = 1f / state.numberSelectedDays
    val startPercentage: Float = (dayDelay + monthOverlapDelay) * percentagePerDay
    val endPercentage: Float = startPercentage + numberDaysSelected * percentagePerDay

    val scaledPercentage: Float = if (selectedPercentage >= endPercentage) {
        1f
    } else if (selectedPercentage < startPercentage) {
        0f
    } else {
        // Scale the overall percentage between the start selection of days to the end selection for
        // the current week. eg: if this week has 3 days before it selected, we only want to
        // start this animation after 3 * percentagePerDay and end it at the number of selected days
        // in the week - so we normalize the percentage between the startPercentage + endPercentage
        // to a range between at min 0f and 1f.
        normalize(
            x = selectedPercentage,
            inMin = startPercentage,
            inMax = endPercentage
        )
    }

    val scaledSelectedNumberDays: Float = scaledPercentage * numberDaysSelected

    val sideSize: Float = edgePadding + cornerRadiusPx

    val leftSize: Float =
        if (state.isLeftHighlighted(currentWeekStart, week.yearMonth)) sideSize else 0f
    val rightSize: Float =
        if (state.isRightHighlighted(currentWeekStart, week.yearMonth)) sideSize else 0f

    var totalSize: Float = (scaledSelectedNumberDays * widthPerDayPx) +
        (leftSize + rightSize) * scaledPercentage
    if (dayDelay + monthOverlapDelay == 0 && numberDaysSelected >= 1) {
        totalSize = totalSize.coerceAtLeast(widthPerDayPx)
    }

    val startOffset: Float =
        state.selectedStartOffset(currentWeekStart, week.yearMonth) * widthPerDayPx

    val offset: Offset =
        if (state.animateDirection?.isBackwards() == true) {
            Offset(x = startOffset + edgePadding + rightSize, y = 0f)
        } else {
            Offset(x = startOffset + edgePadding - leftSize, y = 0f)
        }

    return offset to totalSize
}

internal const val DURATION_MILLIS_PER_DAY = 150

private fun normalize(
    x: Float,
    inMin: Float,
    inMax: Float,
    outMin: Float = 0f,
    outMax: Float = 1f
): Float {
    val outRange: Float = outMax - outMin
    val inRange: Float = inMax - inMin
    return (x - inMin) * outRange / inRange + outMin
}
