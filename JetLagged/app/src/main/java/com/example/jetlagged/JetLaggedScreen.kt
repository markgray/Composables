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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetlagged.ui.theme.SmallHeadingStyle
import com.example.jetlagged.ui.theme.Yellow
import com.example.jetlagged.ui.theme.YellowVariant
import java.time.DayOfWeek
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
@Preview(device = Devices.FOLDABLE, showBackground = true)
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
 *
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
        dayLabel = { index ->
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
 *
 */
@Composable
fun DayLabel(dayOfWeek: DayOfWeek) {
    Text(
        text = dayOfWeek.getDisplayName(
            TextStyle.SHORT, Locale.getDefault()
        ),
        modifier = Modifier
            .height(height = 24.dp)
            .padding(start = 8.dp, end = 24.dp),
        style = SmallHeadingStyle,
        textAlign = TextAlign.Center
    )
}

/**
 *
 */
@Composable
fun HoursHeader(hours: List<Int>) {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .drawBehind {
                val brush = Brush.linearGradient(listOf(YellowVariant, Yellow))
                drawRoundRect(
                    brush,
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                )
            }
    ) {
        hours.forEach {
            Text(
                text = "$it",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(width = 50.dp)
                    .padding(vertical = 4.dp),
                style = SmallHeadingStyle
            )
        }
    }
}
