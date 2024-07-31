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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.calendar.model.Month
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import java.time.YearMonth

/**
 * This Composable is used to display the month name and year at the top of each month in the
 * calendar. Our root Composable is a [Row] whose `modifier` argument chains a
 * [Modifier.clearAndSetSemantics] to our [Modifier] parameter [modifier] to clear the semantics of
 * all the descendant nodes. The [RowScope] Composable `content` lambda holds:
 *  - a [Text] whose `modifier` argument is a [RowScope.weight] of 1.0f which will cause it to take
 *  up all the incoming size constraint after its sibling has been measured and placed, whose `text`
 *  argument is our [String] parameter [month], and whose [TextStyle] `style` argument is the
 *  [Typography.h6] of our [CraneTheme] custom [MaterialTheme.typography].
 *  - a [Text] whose `modifier` argument is a [RowScope.align] with an `alignment` argument of
 *  [Alignment.CenterVertically] to center it vertically, whose `text` argument is our [String]
 *  parameter [year], and whose [TextStyle] `style` argument is the [Typography.caption] of our
 *  [CraneTheme] custom [MaterialTheme.typography].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller (the private extension function `LazyListScope.itemsCalendarMonth` in the
 * file `Calendar.kt`) calls us with a [Modifier.padding] that adds 32.dp to our `start`, 32.dp to
 * our `end`, and 32.dp to our `top`.
 * @param month the [String] name of the month. Our caller calls us with the `name` of the
 * [YearMonth.getMonth] of the [Month.yearMonth] of the [Month] it is working on.
 * @param year the [String] version of the year. Our caller calls us with the [String] value of the
 * [YearMonth.getYear] of the [Month.yearMonth] of the [Month] it is working on.
 */
@Composable
internal fun MonthHeader(modifier: Modifier = Modifier, month: String, year: String) {
    Row(modifier = modifier.clearAndSetSemantics { }) {
        Text(
            modifier = Modifier.weight(1f),
            text = month,
            style = MaterialTheme.typography.h6
        )
        Text(
            modifier = Modifier.align(alignment = Alignment.CenterVertically),
            text = year,
            style = MaterialTheme.typography.caption
        )
    }
}
