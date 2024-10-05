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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetlagged.TimeGraphScope.timeGraphBar
import com.example.jetlagged.ui.theme.SmallHeadingStyle
import com.example.jetlagged.ui.theme.Yellow
import com.example.jetlagged.ui.theme.YellowVariant
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

/**
 * This is used as the screen for the [Screen.Home] selected screen (the only [Screen] with non-empty
 * content). Our root Composable is a [Column] whose `modifier` argument chains a [Modifier.background]
 * to our [Modifier] parameter [modifier] that sets its background color to [Color.White], with a
 * [Modifier.fillMaxSize] chained to that which makes it occupy its entire incoming size constraint,
 * and a [Modifier.verticalScroll] chained to that so that the [Column] can be scrolled vertically.
 * The `content` of the [Column] is:
 *  - a [Column] whose `modifier` argument is [Modifier.yellowBackground] (animates its background
 *  colors), and whose `content` is a [JetLaggedHeader] whose `onDrawerClicked` argument is our
 *  [onDrawerClicked] lambda parameter and whose `modifier` argument is a [Modifier.fillMaxWidth]
 *  that causes it to occupy its entire incoming size constraint. This is followed by a [Spacer]
 *  whose `modifier` argument is a [Modifier.height] of 32.dp, with a [JetLaggedSleepSummary] at
 *  the bottom of the [Column] is a [JetLaggedSleepSummary] whose `modifier` argument is a
 *  [Modifier.padding] that adds 16.dp to its `start` and 16.dp to its `end`.
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] of 16.dp
 *  - We initialize and remember our [MutableState] wrapped [SleepTab] variable `var selectedTab`
 *  to a [SleepTab.Week]
 *  - a [JetLaggedHeaderTabs] whose `onTabSelected` argument is a lambda that sets `selectedTab`
 *  to the [SleepTab] it is called with, and whose `selectedTab` argument is our [MutableState]
 *  wrapped [SleepTab] variable `selectedTab`
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] of 16.dp
 *  - We initialize and remember our [MutableState] wrapped [SleepGraphData] variable `var sleepState`
 *  to [sleepData] (our fake dataset).
 *  - a [JetLaggedTimeGraph] whose `sleepGraphData` argument is our [MutableState] wrapped
 *  [SleepGraphData] variable `sleepState`.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [ScreenContents] passes us an empty, default, or starter Modifier that
 * contains no elements.
 * @param onDrawerClicked a lambda which can be called when the user wants to open the "navigation
 * drawer". We pass it to [JetLaggedHeader] as its `onDrawerClicked` argument and it passes it to
 * the [IconButton] at the beginning of its [Row] as its `onClick` argument. Our caller [ScreenContents]
 * passes us its own `onDrawerClicked` lambda parameter, which is a reference to the `toggleDrawerState`
 * method of the [HomeScreenDrawer] that calls it.
 */
@Preview(showBackground = true)
@Preview(device = "spec:width=673dp,height=841dp", showBackground = true)
@Composable
fun JetLaggedScreen(
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(color = Color.White)
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        Column(modifier = Modifier.yellowBackground()) {
            JetLaggedHeader(
                onDrawerClicked = onDrawerClicked,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(height = 32.dp))
            JetLaggedSleepSummary(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        }

        Spacer(modifier = Modifier.height(height = 16.dp))

        var selectedTab: SleepTab by remember { mutableStateOf(value = SleepTab.Week) }
        JetLaggedHeaderTabs(
            onTabSelected = { selectedTab = it },
            selectedTab = selectedTab,
        )

        Spacer(modifier = Modifier.height(height = 16.dp))
        val sleepState: SleepGraphData by remember {
            mutableStateOf(value = sleepData)
        }
        JetLaggedTimeGraph(sleepGraphData = sleepState)
    }
}

/**
 * This Composable displays its [SleepGraphData] parameter [sleepGraphData] in a [TimeGraph]. We
 * start by initializing and remembering our [ScrollState] variable `val scrollState`, and initializing
 * our [List] of [Int] variable `val hours` to the [List] of hours inclusive between the
 * [SleepGraphData.earliestStartHour] of [sleepGraphData] and 23 and the  [List] of hours inclusive
 * between 0 and the [SleepGraphData.latestEndHour] of [sleepGraphData] (includes all hours that are
 * covered in the [List] of [SleepDayData] field [SleepGraphData.sleepDayData] of [sleepGraphData]).
 * Then our root Composable is a [TimeGraph] whose `modifier` argument is a [Modifier.horizontalScroll]
 * whose `state` argument is our [ScrollState] variable `scrollState` (makes the [TimeGraph] scrollable
 * in the horizontal direction) with a [Modifier.wrapContentSize] chained to that which allows the
 * content to measure at its desired size without regard for the incoming measurement minimum width
 * or minimum height constraints. Its `dayItemsCount` argument is the size of the [List] of [SleepDayData]
 * field [SleepGraphData.sleepDayData] of [sleepGraphData]. Its `hoursHeader` argument is a lambda
 * which composes a [HoursHeader] whose `hours` argument is our [List] of [Int] variable `hours`. Its
 * `dayLabel` argument is a lambda that receives its [Int] argument as `index`, initializes its
 * [SleepDayData] variable `val data` to the [SleepDayData] at index `index` in the [List] of
 * [SleepDayData] field [SleepGraphData.sleepDayData] of [sleepGraphData], then composes a
 * [DayLabel] whose `dayOfWeek` argment is the [LocalDateTime.getDayOfWeek] (kotlin `dayOfWeek`
 * property) of the [SleepDayData.startDate] property of `data`. Its `bar` argument is a
 * lambda that receives its [Int] argument as `index`, initializes its [SleepDayData] variable
 * `val data` to the [SleepDayData] at index `index` in the [List] of [SleepDayData] field
 * [SleepGraphData.sleepDayData] of [sleepGraphData], then composes a [SleepBar] whose arguments
 * are:
 *  - `sleepData` our [SleepDayData] variable `data`
 *  - `modifier` a [Modifier.padding] that adds 8.dp of padding the the bottom edge, to which is
 *  chained a [TimeGraphScope.timeGraphBar] whose `start` argument is the [SleepDayData.firstSleepStart]
 *  property of `data`, whose `start` argument is the [SleepDayData.lastSleepEnd] property of `data`,,
 *  and whose `hours` argument is our [List] of [Int] variable `hours`.
 *
 * @param sleepGraphData the [SleepGraphData] fake dataset we are to display in a [TimeGraph].
 */
@Composable
private fun JetLaggedTimeGraph(sleepGraphData: SleepGraphData) {
    val scrollState: ScrollState = rememberScrollState()

    val hours: List<Int> = (sleepGraphData.earliestStartHour..23) +
        (0..sleepGraphData.latestEndHour)

    TimeGraph(
        modifier = Modifier
            .horizontalScroll(state = scrollState)
            .wrapContentSize(),
        dayItemsCount = sleepGraphData.sleepDayData.size,
        hoursHeader = {
            HoursHeader(hours = hours)
        },
        dayLabel = { index: Int ->
            val data: SleepDayData = sleepGraphData.sleepDayData[index]
            DayLabel(dayOfWeek = data.startDate.dayOfWeek)
        },
        bar = { index: Int ->
            val data: SleepDayData = sleepGraphData.sleepDayData[index]
            // We have access to Modifier.timeGraphBar() as we are now in TimeGraphScope
            SleepBar(
                sleepData = data,
                modifier = Modifier.padding(bottom = 8.dp)
                    .timeGraphBar(
                        start = data.firstSleepStart,
                        end = data.lastSleepEnd,
                        hours = hours,
                    )
            )
        }
    )
}

/**
 * Composes a [Text] displaying the [DayOfWeek.getDisplayName] value of its [DayOfWeek] parameter
 * [dayOfWeek] ("Mon" through "Sun"). The lambda used as the `dayLabel` argument of the [TimeGraph]
 * used by the [JetLaggedTimeGraph] calls [DayLabel] with the [SleepDayData.startDate] of the
 * [SleepDayData] whose index is the [Int] passed the lambda in the [SleepGraphData.sleepDayData]
 * list of our fake dataset. [TimeGraph] calls its `dayLabel` lambda with the indexes of every
 * entry in [SleepGraphData.sleepDayData], collecting the Composables returned in a list which it
 * places using [Layout] at the beginning of each row of [SleepDayData] that it displays. Our root
 * Composable is a [Text] whose `text` argument is the [DayOfWeek.getDisplayName] value of our
 * [DayOfWeek] parameter [dayOfWeek] when it is called with the [TextStyle.SHORT] as its `style`
 * argument (short text, typically an abbreviation, day-of-week "Monday" will output "Mon") and
 * [Locale.getDefault] as its `locale` argument (current value of the default locale). Its `modifier`
 * argument is [Modifier.height] setting its height to 24.dp, with a [Modifier.padding] chained to
 * that that sets the padding at the start of the [Text] to 8.dp, and the padding at the end to 24.dp.
 * Its `style` [TextStyle] argument is [SmallHeadingStyle], which is the "Lato" downloadable
 * [GoogleFont] with a `fontSize` of 16.dp, and a [FontWeight] of 400. Its `textAlign` argument is
 * [TextAlign.Center] which aligns the text in the center of the container.
 *
 * @param dayOfWeek the [DayOfWeek] whose [DayOfWeek.getDisplayName] we are to display in a [Text].
 */
@Composable
fun DayLabel(dayOfWeek: DayOfWeek) {
    Text(
        text = dayOfWeek.getDisplayName(
            /* style = */ TextStyle.SHORT,
            /* locale = */ Locale.getDefault()
        ),
        modifier = Modifier
            .height(height = 24.dp)
            .padding(start = 8.dp, end = 24.dp),
        style = SmallHeadingStyle,
        textAlign = TextAlign.Center
    )
}

/**
 * Composes a [Text] for every [Int] in its [List] of [Int] parameter [hours]. [JetLaggedTimeGraph]
 * passes a call to [HoursHeader] in the lambda that it uses as the `hoursHeader` argument of its
 * [TimeGraph] with the [hours] argument a [List] of all of the hours that are used in our dummy
 * [SleepGraphData] dataset (the hours between [SleepGraphData.earliestStartHour] and
 * [SleepGraphData.latestEndHour]). [TimeGraph] places the Composable created by its `hoursHeader`
 * lambda parameter as the first "Header" line in the [Layout] it creates. Our root Comosable is a
 * [Row] whose `modifier` argument is a [Modifier.padding] that adds 16.dp to the bottom edge, with
 * a [Modifier.drawBehind] whose `onDraw` [DrawScope] lambda initializes its [Brush] variable
 * `val brush` to a [Brush.linearGradient] created from the colors [YellowVariant] and [Yellow],
 * then calls the [DrawScope.drawRoundRect] method to use `brush` as the [Brush] to draw a rounded
 * rectangle behind the [Row] whose `cornerRadius` is a [CornerRadius] with both the x and y radius
 * of 10.dp (converted to pixels). In the `content` lambda argument of the [Row] we loop over every
 * [Int] `hour` in our [List] of [Int] parameter [hours] composing a [Text] whose `text` argument is
 * the [String] value of the current `hour`, whose `textAlign` argument is [TextAlign.Center] to align
 * the `text` in the center of the [Text], whose `modifier` argument is a [Modifier.width] that sets
 * its width to 50.dp with a [Modifier.padding] that adds 4.dp to the top and bottom edges. Its
 * `style` [TextStyle] argument is [SmallHeadingStyle], which is the "Lato" downloadable [GoogleFont]
 * with a `fontSize` of 16.dp, and a [FontWeight] of 400.
 *
 * @param hours a [List] of [Int] whose [String] values we are to display in [Text]'s arranged in a
 * [Row]. The [List] of [Int] passed us is a [List] of all of the hours that are used in our dummy
 * [SleepGraphData] dataset (the hours between [SleepGraphData.earliestStartHour] and
 * [SleepGraphData.latestEndHour]).
 */
@Composable
fun HoursHeader(hours: List<Int>) {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .drawBehind {
                val brush: Brush = Brush.linearGradient(listOf(YellowVariant, Yellow))
                drawRoundRect(
                    brush = brush,
                    cornerRadius = CornerRadius(x = 10.dp.toPx(), y = 10.dp.toPx()),
                )
            }
    ) {
        hours.forEach { hour: Int ->
            Text(
                text = "$hour",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(width = 50.dp)
                    .padding(vertical = 4.dp),
                style = SmallHeadingStyle
            )
        }
    }
}
