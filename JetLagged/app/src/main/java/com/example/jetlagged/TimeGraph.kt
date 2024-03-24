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

package com.example.jetlagged

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

/**
 * This is called by [JetLaggedTimeGraph] to layout the final graph of the [SleepGraphData] using
 * the Composables it creates using its lambda parameters to provide Composables for the various
 * components of the graph. We start by using our [dayLabel] parameter to create a [List] containing
 * [dayItemsCount] Composables to initialize our variable `val dayLabels` with each Composable in the
 * [List] displaying the [LocalDateTime.getDayOfWeek] (kotlin `dayOfWeek` property) for the
 * [SleepDayData.startDate] of the corresponding [SleepDayData] in the [SleepGraphData.sleepDayData]
 * list. We then initialize our [List] of Composables variable `val bars` by using our [bar] lambda
 * parameter to create a [SleepBar] for each of the [SleepDayData] in the [SleepGraphData.sleepDayData]
 * list.
 *
 * Our root Composable is a [Layout] whose `contents` argument is a [listOf] our [hoursHeader]
 * Composable parameter, our `dayLabels` [List] of Composables variable, and our `bars` [List] of
 * Composables variable. The `modifier` argument of the [Layout] chains a [Modifier.padding] to our
 * [modifier] parameter which sets the `bottom` padding to 32.dp.
 *
 * In the [MeasureScope] block of the [Layout] we use destructuring to split the [List] of [List]
 * of [Measurable]'s passed the block into the [List] of [Measurable] variables `hoursHeaderMeasurables`
 * (created from `hoursHeader` Composable), `dayLabelMeasurables` (created from `dayLabels` [List] of
 * Composables), and `barMeasureables` (created from `bars` [List] of Composables), and accept the
 * [Constraints] passed the block as variable `constraints`.
 *
 * We perform a [require] sanity check that "hoursHeader should only emit one composable", then
 * initialize our [Placeable] variable `val hoursHeaderPlaceable` to the value returned by the
 * [Measurable.measure] method of the [List.first] of `hoursHeaderMeasurables` for [Constraints]
 * variable `constraints`.
 *
 * We initialize our [List] of [Placeable] variable `val dayLabelPlaceables` using the [List.map]
 * method of `dayLabelMeasurables` to loop through all of the [Measurable] in [List] of [Measurable]
 * `dayLabelMeasurables` calling their [Measurable.measure] method to produce a [Placeable] for that
 * [Measurable] given [Constraints] variable `constraints`.
 *
 * We initialize our [Int] variable `var totalHeight` to the [Placeable.height] of `hoursHeaderPlaceable`.
 *
 * We initialize our [List] of [Placeable] variable
 *
 * @param hoursHeader a lambda returning a header bar Composable created by [HoursHeader] which has
 * a [Text] for each [Int] hour included by the [SleepDayData] that is in [SleepGraphData].
 * @param dayItemsCount the number of [SleepDayData] that are to have sleep bars drawn for them.
 * @param dayLabel a lambda which produces a [DayLabel] Composable displaying the value returned by
 * [LocalDateTime.getDayOfWeek] (kotlin `dayOfWeek` property) for the [SleepDayData.startDate] of
 * the [SleepDayData] at the [Int] `index` passed the lambda in the [List] of [SleepDayData] field
 * [SleepGraphData.sleepDayData].
 * @param bar a lambda which produces a [SleepBar] Composable displaying the [SleepDayData] at the
 * [Int] `index` passed the lambda in the [List] of [SleepDayData] field [SleepGraphData.sleepDayData].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [JetLaggedTimeGraph] passes us a [Modifier.horizontalScroll] (allows us to
 * scroll horizontally when width of our content is bigger than max constraints allow) with a
 * [Modifier.wrapContentSize] chained to that (allows us to measure at our desired size without
 * regard for the incoming measurement minimum width or minimum height constraints) .
 */
@Composable
fun TimeGraph(
    hoursHeader: @Composable () -> Unit,
    dayItemsCount: Int,
    dayLabel: @Composable (index: Int) -> Unit,
    bar: @Composable TimeGraphScope.(index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dayLabels = @Composable { repeat(dayItemsCount) { dayLabel(it) } }
    val bars = @Composable { repeat(dayItemsCount) { TimeGraphScope.bar(it) } }
    Layout(
        contents = listOf(hoursHeader, dayLabels, bars),
        modifier = modifier.padding(bottom = 32.dp)
    ) {
        (
            hoursHeaderMeasurables: List<Measurable>,
            dayLabelMeasurables: List<Measurable>,
            barMeasureables: List<Measurable>
        ),
        constraints: Constraints,
        ->
        require(hoursHeaderMeasurables.size == 1) {
            "hoursHeader should only emit one composable"
        }
        val hoursHeaderPlaceable: Placeable = hoursHeaderMeasurables.first().measure(constraints)

        val dayLabelPlaceables: List<Placeable> = dayLabelMeasurables.map { measurable ->
            val placeable: Placeable = measurable.measure(constraints)
            placeable
        }

        var totalHeight: Int = hoursHeaderPlaceable.height

        val barPlaceables: List<Placeable> = barMeasureables.map { measurable: Measurable ->
            val barParentData = measurable.parentData as TimeGraphParentData
            val barWidth: Int = (barParentData.duration * hoursHeaderPlaceable.width).roundToInt()

            val barPlaceable: Placeable = measurable.measure(
                constraints.copy(
                    minWidth = barWidth,
                    maxWidth = barWidth
                )
            )
            totalHeight += barPlaceable.height
            barPlaceable
        }

        val totalWidth: Int = dayLabelPlaceables.first().width + hoursHeaderPlaceable.width

        layout(width = totalWidth, height = totalHeight) {
            val xPosition: Int = dayLabelPlaceables.first().width
            var yPosition: Int = hoursHeaderPlaceable.height

            hoursHeaderPlaceable.place(x = xPosition, y = 0)

            barPlaceables.forEachIndexed { index: Int, barPlaceable: Placeable ->
                val barParentData = barPlaceable.parentData as TimeGraphParentData
                val barOffset: Int = (barParentData.offset * hoursHeaderPlaceable.width).roundToInt()

                barPlaceable.place(x = xPosition + barOffset, y = yPosition)
                // the label depend on the size of the bar content - so should use the same y
                val dayLabelPlaceable: Placeable = dayLabelPlaceables[index]
                dayLabelPlaceable.place(x = 0, y = yPosition)

                yPosition += barPlaceable.height
            }
        }
    }
}

/**
 *
 */
@LayoutScopeMarker
@Immutable
object TimeGraphScope {
    /**
     *
     */
    @Stable
    fun Modifier.timeGraphBar(
        start: LocalDateTime,
        end: LocalDateTime,
        hours: List<Int>,
    ): Modifier {
        val earliestTime = LocalTime.of(hours.first(), 0)
        val durationInHours = ChronoUnit.MINUTES.between(start, end) / 60f
        val durationFromEarliestToStartInHours =
            ChronoUnit.MINUTES.between(earliestTime, start.toLocalTime()) / 60f
        // we add extra half of an hour as hour label text is visually centered in its slot
        val offsetInHours = durationFromEarliestToStartInHours + 0.5f
        return then(
            TimeGraphParentData(
                duration = durationInHours / hours.size,
                offset = offsetInHours / hours.size
            )
        )
    }
}

/**
 *
 */
class TimeGraphParentData(
    /**
     *
     */
    val duration: Float,
    /**
     *
     */
    val offset: Float,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): TimeGraphParentData = this@TimeGraphParentData
}
