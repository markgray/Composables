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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import com.example.jetlagged.ui.theme.SmallHeadingStyle
import com.example.jetlagged.ui.theme.White
import com.example.jetlagged.ui.theme.Yellow

/**
 * This enum is used as the `text` of the [SleepTabText] entries displayed by the [JetLaggedHeaderTabs]
 * Composable. The [title] is the resource ID of the [String] to use as the `text`.
 */
enum class SleepTab(
    /**
     * The resource ID of the [String] to use as the `text`.
     */
    val title: Int) {
    /**
     * [SleepTab] whose [title] is the resource ID [R.string.sleep_tab_day_heading] ("Day")
     */
    Day(R.string.sleep_tab_day_heading),

    /**
     * [SleepTab] whose [title] is the resource ID [R.string.sleep_tab_week_heading] ("Week")
     */
    Week(R.string.sleep_tab_week_heading),

    /**
     * [SleepTab] whose [title] is the resource ID [R.string.sleep_tab_month_heading] ("Month")
     */
    Month(R.string.sleep_tab_month_heading),

    /**
     * [SleepTab] whose [title] is the resource ID [R.string.sleep_tab_six_months_heading] ("6M")
     */
    SixMonths(R.string.sleep_tab_six_months_heading),

    /**
     * [SleepTab] whose [title] is the resource ID [R.string.sleep_tab_one_year_heading] ("1Y")
     */
    OneYear(R.string.sleep_tab_one_year_heading)
}

/**
 * This Composable displays a [ScrollableTabRow] which holds a [SleepTabText] for each of the
 * [SleepTab.entries] of the [SleepTab] enum. The arguments passed our [ScrollableTabRow] are:
 *  - `modifier` we pass it our [Modifier] parameter [modifier]
 *  - `edgePadding` we pass it 12.dp (the padding between the starting and ending edge of the
 *  scrollable tab row, and the tabs inside the row).
 *  - `selectedTabIndex` we pass it the [SleepTab.ordinal] of our [SleepTab] parameter [selectedTab]
 *  (the index of the currently selected tab)
 *  - `containerColor` we pass it [White] (the color used for the background of the tab row)
 *  - `indicator` we pass it a lambda which composes a [Box] whose `modifier` argument is a
 *  [tabIndicatorOffset] ([Modifier] that takes up all the available width inside the [ScrollableTabRow],
 *  and then animates the offset of the indicator it is applied to, depending on its `currentTabPosition`
 *  argument, which is the [TabPosition] occupying the [SleepTab.ordinal] of our [SleepTab] parameter
 *  [selectedTab] entry in the [List] of [TabPosition] passed the lambda. To this is chained a
 *  [Modifier.fillMaxSize] which makes the [Box] occupy its entire incoming size constraints, followed
 *  by a [Modifier.padding] which adds 2.dp to each end of the [Box], and finally a [Modifier.border]
 *  whose `border` is a [BorderStroke] with a `width` of 2.dp, and `color` [Yellow], and the `shape`
 *  of the [Modifier.border] is a [RoundedCornerShape] whose `size` is 10.dp.
 *  - `divider` (the divider displayed at the bottom of the tab row) we pass it an empty "do nothing"
 *  lambda.
 *
 * The `tabs` argument of the [ScrollableTabRow] is a block which uses the [forEachIndexed] extension
 * function to loop over all of the [SleepTab.entries], setting its [Boolean] variable `val selected`
 * to `true` if the current [Int] `index` passed the `action` lambda is equal to the [SleepTab.ordinal]
 * of our [SleepTab] parameter [selectedTab] then composing a [SleepTabText] whose `sleepTab` argument is
 * the current [SleepTab] `sleepTab` passed the lambda, whose `selected` is our [Boolean] variable
 * `selected`, whose `onTabSelected` argument is our [onTabSelected] lambda parameter, and whose
 * `index` argument is the current [Int] `index` passed the `action` lambda.
 *
 * @param onTabSelected a lambda that the [SleepTabText]'s in our [ScrollableTabRow] should call
 * with the [SleepTab] that it was constructed to display. Our caller [JetLaggedScreen] passes us
 * a lambda which sets the [MutableState] wrapped [SleepTab] variable that it passes us as our
 * [selectedTab] parameter to the [SleepTab] enum passes the lambda.
 * @param selectedTab this is the current value of the "selected" [SleepTab]. Our caller
 * [JetLaggedScreen] passes us a [MutableState] wrapped [SleepTab] variable (the one that our
 * [onTabSelected] lambda parameter sets when it is called).
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [JetLaggedScreen] does not pass us any so the empty, default, or starter
 * [Modifier] that contains no elements is used instead.
 */
@Composable
fun JetLaggedHeaderTabs(
    onTabSelected: (SleepTab) -> Unit,
    selectedTab: SleepTab,
    modifier: Modifier = Modifier,
) {
    ScrollableTabRow(
        modifier = modifier,
        edgePadding = 12.dp,
        selectedTabIndex = selectedTab.ordinal,
        containerColor = White,
        indicator = { tabPositions: List<TabPosition> ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(currentTabPosition = tabPositions[selectedTab.ordinal])
                    .fillMaxSize()
                    .padding(horizontal = 2.dp)
                    .border(
                        border = BorderStroke(width = 2.dp, color = Yellow),
                        shape = RoundedCornerShape(size = 10.dp)
                    )
            )
        },
        divider = { }
    ) {
        SleepTab.entries.forEachIndexed { index: Int, sleepTab: SleepTab ->
            val selected: Boolean = index == selectedTab.ordinal
            SleepTabText(
                sleepTab = sleepTab,
                selected = selected,
                onTabSelected = onTabSelected,
                index = index
            )
        }
    }
}

/**
 * [Modifier.padding] used for the [Text] Composable used by the [Tab] of [SleepTabText], it adds
 * 6.dp to the top and bottom edges and 4.dp to the left and right edges.
 */
private val textModifier = Modifier
    .padding(vertical = 6.dp, horizontal = 4.dp)

/**
 * This Composable is used to display each of the [SleepTab] enum entries in our [ScrollableTabRow]
 * Composable. Our root Composable is a [Tab] whose `modifier` argument is a [Modifier.padding] that
 * adds 2.dp to the left and right edges of the content, with a [Modifier.clip] chained to that that
 * clips it to a [RoundedCornerShape] space whose `size` is 16.dp, its `selected` argument is our
 * [Boolean] parameter [selected], its `unselectedContentColor` argument is [Color.Black] and, its
 * `selectedContentColor` argument is [Color.Black], and its `onClick` argument is a lambda which
 * calls our [onTabSelected] lambda parameter with the [SleepTab] in the [SleepTab.entries] property
 * at index [index]. The [Tab]'s `content` is a [Text] whose `modifier` parameter is our [Modifier]
 * field [textModifier], its `text` argument is the [String] whose resource ID is the [SleepTab.title]
 * of our [SleepTab] parameter [sleepTab], and its [TextStyle] `style` argument is [SmallHeadingStyle]
 * (downloadable "Lato" [GoogleFont] with a `fontSize` of 16.sp and a [FontWeight] of 600.
 *
 * @param sleepTab the [SleepTab] that we are to display.
 * @param selected if `true` our [SleepTabText] is the currently selected [Tab] in the [ScrollableTabRow]
 * of [JetLaggedHeaderTabs].
 * @param index the index of our [SleepTab] in the [SleepTab.entries] property.
 * @param onTabSelected a lambda we should call with the [SleepTab] in the [SleepTab.entries] property
 * at index [index] when our [Tab] is clicked.
 */
@Composable
fun SleepTabText(
    sleepTab: SleepTab,
    selected: Boolean,
    index: Int,
    onTabSelected: (SleepTab) -> Unit,
) {
    Tab(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clip(shape = RoundedCornerShape(size = 16.dp)),
        selected = selected,
        unselectedContentColor = Color.Black,
        selectedContentColor = Color.Black,
        onClick = {
            onTabSelected(SleepTab.entries[index])
        }
    ) {
        Text(
            modifier = textModifier,
            text = stringResource(id = sleepTab.title),
            style = SmallHeadingStyle
        )
    }
}
