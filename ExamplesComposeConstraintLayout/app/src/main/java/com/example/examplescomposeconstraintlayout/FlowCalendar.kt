@file:Suppress("UnusedImport")

package com.example.examplescomposeconstraintlayout

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Wrap
import java.text.SimpleDateFormat
import java.util.*

/**
 * A Composable that demonstrates a vertically scrolling list of calendars.
 * It uses a [LazyColumn] to efficiently display a large number of months (1000 in this case).
 * Each item in the list is a [DynamicCalendar] composable, representing a single month.
 * The `monthOffset` parameter is passed to each [DynamicCalendar] to determine which
 * month to display relative to the current month.
 */
@Preview(group = "scroll")
@Composable
fun CalendarList() {
    LazyColumn {
        items(count = 1000) { monthOffset: Int ->
            Box(
                modifier = Modifier
                    .padding(all = 3.dp)
                    .background(color = Color(0xFFA2A2E0))
            ) {
                DynamicCalendar(montOffset = monthOffset)
            }
        }
    }
}

/**
 * A Composable that displays a single calendar month.
 * It uses [ConstraintLayout] with a [ConstraintSetScope.createFlow] helper to arrange the days of
 * the week and the dates in a grid. The calendar is "dynamic" in the sense that it can display any
 * month relative to the current one, specified by the [Int] parameter [montOffset].
 *
 * The layout consists of:
 *  - A title displaying the month and year (e.g., "August 2023").
 *  - A row of single-letter abbreviations for the days of the week (S, M, T, W, T, F, S).
 *  - A grid of dates for the specified month.
 *
 * @param montOffset The number of months to offset from the current month. `0` represents the
 * current month, `1` represents next month, `-1` represents last month, and so on. Defaults to `0`.
 */
@Preview(group = "scroll")
@Composable
fun DynamicCalendar(montOffset: Int = 0) {
    val days: ArrayList<String> = ArrayList(listOf("S", "M", "T", "W", "T", "F", "S"))
    val cal: Calendar = Calendar.getInstance()
    val t: Long = cal.timeInMillis

    for (pos in 0..41) {
        cal.timeInMillis = t
        cal.add(Calendar.MONTH, montOffset)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val offset: Int = cal.get(Calendar.DAY_OF_WEEK) - 1
        val lastDay: Int = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (offset > pos || pos - offset >= lastDay) {
            days.add("")
        } else {
            days.add((pos - offset + 1).toString())
        }
    }
    cal.timeInMillis = t
    @SuppressLint("SimpleDateFormat")
    val fd = SimpleDateFormat("LLLL yyyy")

    cal.add(Calendar.MONTH, montOffset)
    val calDate: String = fd.format(cal.time)
    val refId: Array<String> = days.mapIndexed { index: Int, s: String ->
        "id" + index + "_$s"
    }.toTypedArray()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        ConstraintLayout(
            ConstraintSet {
                val date: ConstrainedLayoutReference = createRefFor(id = "date")
                val keys: Array<ConstrainedLayoutReference> = refId.map { id: String ->
                    createRefFor(id = id)
                }.toTypedArray()
                val flow: ConstrainedLayoutReference = createFlow(
                    elements = keys,
                    maxElement = 7,
                    wrapMode = Wrap.Aligned,
                    verticalGap = 8.dp,
                    horizontalGap = 8.dp
                )
                constrain(ref = flow) {
                    top.linkTo(anchor = date.bottom, margin = 18.dp)
                    bottom.linkTo(anchor = parent.bottom)
                    centerHorizontallyTo(other = parent)
                }
                constrain(ref = date) {
                    centerHorizontallyTo(other = parent)
                    top.linkTo(anchor = parent.top, margin = 6.dp)
                }
            }
        ) {
            Text(text = calDate, fontSize = 22.sp, modifier = Modifier.layoutId(layoutId = "date"))
            refId.forEachIndexed { index, id ->
                Text(text = days[index], modifier = Modifier.layoutId(layoutId = id))
            }
        }
    }
}
