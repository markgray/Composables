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
 * We initialize our [List] of [Placeable] variable `val barPlaceables` using the [List.map] method
 * of `dayLabelMeasurables` to loop through all of the [Measurable] in [List] of [Measurable] variable
 * `barMeasureables` and perform the following steps on the [Measurable] variable `measurable` passed
 * the map's `transform` lambda to create each [Placeable] variable `val barPlaceable` that is added
 * to the [List]:
 *  - initialize our [TimeGraphParentData] variable `val barParentData` to the [Measurable.parentData]
 *  of the [Measurable] passed the lambda.
 *  - initialize our [Int] variable `val barWidth` to the [TimeGraphParentData.duration] of
 *  `barParentData` times the [Placeable.width] of `hoursHeaderPlaceable` all rounded to [Int]
 *  - initialize our [Placeable] variable `val barPlaceable` using the [Measurable.measure] method
 *  of the [Measurable] passed the lambda with its `constraints` argument a copy of the [Constraints]
 *  passed the [MeasureScope] block in variable `constraints` with the `minWidth` and the `maxWidth`
 *  properties both overridden by our `barWidth` variable.
 *  - add the [Placeable.height] of our [Placeable] variable `barPlaceable` to our `totalHeight`
 *  variable.
 *  - return the [Placeable] variable `barPlaceable` to have it added to the [List] of [Placeable]
 *  being formed by the [List.map] method of [List] of [Measurable] variable `barMeasureables`.
 *
 * Next we initialize our [Int] variable `val totalWidth` to the [Placeable.width] of the [List.first]
 * of [List] of [Placeable] variable `dayLabelPlaceables` plus the [Placeable.width] of [Placeable]
 * variable `hoursHeaderPlaceable`.
 *
 * Finally we call [layout] with its `width` argument our `totalWidth` variable and its `height`
 * argument our `totalHeight` variable. Then in its [Placeable.PlacementScope] block we:
 *  - initialize our [Int] variable `val xPosition` to the [Placeable.width] of the [List.first] of
 *  [List] of [Placeable] variable `dayLabelPlaceables`.
 *  - initialize our [Int] variable `val yPosition` to the [Placeable.height] of [Placeable] variable
 *  `hoursHeaderPlaceable`.
 *  - call the [Placeable.PlacementScope.place] method of [Placeable] variable `hoursHeaderPlaceable`
 *  to place it at `x` coordinate `xPosition` and `y` coordinate 0.
 *
 * Then still in the [Placeable.PlacementScope] block we use the [List.forEachIndexed] method of
 * [List] of [Placeable] variable `barPlaceables` to loop through all the [Placeable]'s in it
 * assigning the index to [Int] variable `index` and the [Placeable] to [Placeable] variable
 * `barPlaceable` performing the following:
 *  - initialize our [TimeGraphParentData] variable `val barParentData` to the [Placeable.parentData]
 *  of the [Placeable] passed the `action` lambda of the [List.forEachIndexed]
 *  - initialize our [Int] variable `val barOffset` to the [TimeGraphParentData.offset] of the
 *  `barParentData` variable time the [Placeable.width] of [Placeable] variable `hoursHeaderPlaceable`
 *  all rounded to [Int].
 *  - call the [Placeable.PlacementScope.place] method of [Placeable] variable `barPlaceable` to
 *  place it at `x` coordinate `xPosition` plus `barOffset` and `y` coordinate `yPosition`
 *  - initialize our [Placeable] variable `val dayLabelPlaceable` to the [Placeable] at index `index`
 *  in our [List] of [Placeable] variable `dayLabelPlaceables`
 *  - call the [Placeable.PlacementScope.place] method of [Placeable] variable `dayLabelPlaceable` to
 *  place it at `x` coordinate 0 and `y` coordinate `yPosition`
 *  - add the [Placeable.height] of [Placeable] variable `barPlaceable` and loop around for the next
 *  `index` and [Placeable].
 *
 * @param hoursHeader a lambda returning a header bar Composable created by [HoursHeader] which has
 * a [Text] for each [Int] hour covered by the [SleepDayData] that is in [SleepGraphData].
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
                constraints = constraints.copy(
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
 * Defines a scope that allows the use of the [Modifier.timeGraphBar] on a Composable in that scope.
 */
@LayoutScopeMarker
@Immutable
object TimeGraphScope {
    /**
     * Produces a [TimeGraphParentData] instance from the [SleepDayData] derived values passed it in
     * its parameters. We start by initializing our [LocalTime] variable `val earliestTime` to the
     * [LocalTime.of] the `hour` of the [List.first] of [List] of [Int] parameter [hours] and the
     * `minute` of 0. We initialize our [Float] variable `val durationInHours` to the value of
     * minutes returned by the [ChronoUnit.MINUTES.between] method for [LocalDateTime] parameters
     * [start] and [end] divided by 60f. We initialize our [Float] variable
     * `val durationFromEarliestToStartInHours` to the value of the minutes returned by the
     * [ChronoUnit.MINUTES.between] method for [LocalDateTime] our [LocalDateTime] variable
     * `earliestTime` and the [LocalDateTime.toLocalTime] method of [LocalDateTime] parameter
     * [start] divided by 60f. We initialize our [Float] variable `val offsetInHours` to
     * `durationFromEarliestToStartInHours` plus 0.5f (we add extra half of an hour as hour label
     * text is visually centered in its slot). We then concatenate a [TimeGraphParentData] modifier
     * whose `duration` argument is our [Float] variable `durationInHours` divided by the [List.size]
     * of our [List] of [Int] parameter [hours] and whose `offset` argument is our [Float] variable
     * `offsetInHours` divided by the [List.size] of our [List] of [Int] parameter [hours] to our
     * receiver [Modifier] and return it (the [layout] method called by [TimeGraph] can then retrieve
     * the [TimeGraphParentData] for the [Measurable] produced for each of the [SleepBar]'s it places
     * using the [Measurable.parentData] method of the [Measurable]).
     *
     * @param start the [LocalDateTime] of the [SleepDayData.firstSleepStart] property of the
     * [SleepDayData] that the [SleepBar] is being composed for.
     * @param end the [LocalDateTime] of the [SleepDayData.lastSleepEnd] property of the
     * [SleepDayData] that the [SleepBar] is being composed for.
     * @param hours the [List] of [Int] hours that is covered by all of the [SleepDayData] in the
     * dataset.
     */
    @Stable
    fun Modifier.timeGraphBar(
        start: LocalDateTime,
        end: LocalDateTime,
        hours: List<Int>,
    ): Modifier {
        val earliestTime: LocalTime = LocalTime.of(hours.first(), 0)
        val durationInHours: Float = ChronoUnit.MINUTES.between(start, end) / 60f
        val durationFromEarliestToStartInHours: Float =
            ChronoUnit.MINUTES.between(earliestTime, start.toLocalTime()) / 60f
        // we add extra half of an hour as hour label text is visually centered in its slot
        val offsetInHours: Float = durationFromEarliestToStartInHours + 0.5f
        return then(
            TimeGraphParentData(
                duration = durationInHours / hours.size,
                offset = offsetInHours / hours.size
            )
        )
    }
}

/**
 * A [Modifier] that provides data to the parent Layout. This can be read from within the [Layout]
 * during measurement and positioning, via [Measurable.parentData]. The parent data is commonly used
 * to inform the parent how the child [Layout] should be measured and positioned.
 */
class TimeGraphParentData(
    /**
     * This is the duration in hours of the [SleepDayData] divided by the [List.size] of the [List]
     * of [Int] of the hours covered by the dataset that the [SleepBar] was composed for. It is used
     * to calculate the width of the [Placeable] created from the [Measurable] created from the
     * [SleepBar] by multiplying it by the [Placeable.width] of the [Placeable] created from the
     * [Measurable] that was created for the [HoursHeader] displayed at the top of [TimeGraph].
     */
    val duration: Float,
    /**
     * This is the [Float] hours offset from the first hour displayed by the [HoursHeader] of the
     * [SleepDayData.firstSleepStart] of the [SleepDayData] that the [SleepBar] was composed for
     * divided by the [List.size] of the [List] of [Int] of the hours covered by the dataset. It is
     * used to calculate the `x` coordinate that is used when the [Placeable.PlacementScope.place]
     * method of the [Placeable] created from the [Measurable] created from the [SleepBar] is called
     * to place the Composable.
     */
    val offset: Float,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): TimeGraphParentData = this@TimeGraphParentData
}
