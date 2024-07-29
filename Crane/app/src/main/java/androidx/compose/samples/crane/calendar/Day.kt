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

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.calendar.model.CalendarUiState
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import java.time.LocalDate
import java.time.YearMonth

/**
 * Used by the [DaysOfWeek] Composable to display the first letter of the each day of the week at
 * the top of each month. Our root composable is a [DayContainer] whose `content` argument is a
 * [Text] whose `modifier` argument is a [Modifier.fillMaxSize] that causes it to occupy its entire
 * incoming size constraints. with a [Modifier.wrapContentHeight] chained to that whose `align`
 * argument is a [Alignment.CenterVertically] causes [Text] to center its `text` vertically, whose
 * `textAlign` argument is [TextAlign.Center] to align the text in the center of the container, whose
 * `text` argument is our [String] parameter [day], and whose [TextStyle] `style` argument is the
 * a copy of the [Typography.caption] of our [CraneTheme] custom [MaterialTheme.typography] with its
 * `color` overridden by a copy of [Color.White] with an `alpha` of 0.6f
 *
 * @param day the first letter of the day of the week we are to represent (ie "M", "T", "W", "T",
 * "F", "S", or "S").
 */
@Composable
internal fun DayOfWeekHeading(day: String) {
    DayContainer {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            text = day,
            style = MaterialTheme.typography.caption.copy(color = Color.White.copy(alpha = 0.6f))
        )
    }
}

/**
 * This is used by [DayOfWeekHeading] to display the first letter of each day of the week, and by
 * [Day] to display a numbered day in the Calendar. We start by initializing our [String] variable
 * `val stateDescriptionLabel` to the [String] with resource ID [R.string.state_descr_selected]
 * ("Selected") if our [Boolean] parameter [selected] is `true` or to the [String] with resource ID
 * [R.string.state_descr_not_selected] ("Not selected") if it is `false`. Our root Composable is a
 * [Box] whose `modifier` argument is a [Modifier.size] that sets its `width` and `height` to
 * [CELL_SIZE] (48.dp), with a [Modifier.pointerInput] chained to that that uses the `block`
 * [PointerInputScope] lambda to call the [PointerInputScope.detectTapGestures] method where it
 * calls our [onClick] lambda parameter in the lambda that it uses as the `onTap` argument of the
 * [PointerInputScope.detectTapGestures]. It uses [Modifier.then] to add a [Modifier] based on the
 * value of our [Boolean] parameter [onClickEnabled]:
 *  - `true` it adds a [Modifier.semantics] whose `properties` lambda argument is a lambda that
 *  sets the [SemanticsPropertyReceiver.stateDescription] property to our [String] variable
 *  `stateDescriptionLabel`, and calls the [SemanticsPropertyReceiver.onClick] method with its
 *  `label` argument our [String] parameter [onClickLabel], and its `action` argument `null`.
 *  - `false` it adds a [Modifier.clearAndSetSemantics] to clear the semantics of all the descendant
 *  nodes.
 *
 * At the tail end of the [Modifier] chain is a [Modifier.background] that sets its background `color`
 * to our [Color] parameter [backgroundColor]. The `content` of the [Box] is just our Composable
 * lambda parameter [content].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [DayOfWeekHeading] passes us none so the default empty [Modifier] is used, but [Day]
 * passes us a [Modifier.semantics] whose `text` is an [AnnotatedString] describing the day of the
 * month that we represent.
 * @param selected if `true` we are in the selected dates range of the [CalendarUiState].
 * @param onClick a lambda that we should call when we are clicked.
 * @param onClickEnabled this is always `true` so it always adds some [Modifier.semantics] to the
 * `modifier` argument of our [Box].
 * @param backgroundColor this is always [Color.Transparent], and is used to specify the background
 * [Color] of our [Box].
 * @param onClickLabel used as the `label` argument of a [SemanticsPropertyReceiver.onClick] that
 * is used in a [Modifier.semantics] of our [Box]. [DayOfWeekHeading] passes us none, but [Day]
 * passes us the [String] with resource ID [R.string.click_label_select] ("select").
 * @param content the Composable lambda that we should use as the `content` Composable lambda of our
 * [Box]. Both of our callers pass us a [Text] configured for their purposes.
 */
@Composable
private fun DayContainer(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = { },
    onClickEnabled: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    onClickLabel: String? = null,
    content: @Composable () -> Unit
) {
    val stateDescriptionLabel: String = stringResource(
        if (selected) R.string.state_descr_selected else R.string.state_descr_not_selected
    )
    Box(
        modifier = modifier
            .size(width = CELL_SIZE, height = CELL_SIZE)
            .pointerInput(Any()) {
                detectTapGestures {
                    onClick()
                }
            }
            .then(
                if (onClickEnabled) {
                    modifier.semantics {
                        stateDescription = stateDescriptionLabel
                        onClick(label = onClickLabel, action = null)
                    }
                } else {
                    modifier.clearAndSetSemantics { }
                }
            )
            .background(color = backgroundColor)
    ) {
        content()
    }
}

/**
 * Called by [androidx.compose.samples.crane.calendar.Week] to render a single day of the month.
 *
 * @param day the [LocalDate] of the day of the month we are to render.
 * @param calendarState the current [CalendarUiState] that we can use to determine if our [day] is
 * in the selected date range of our calendar.
 * @param onDayClicked a lambda that we should call with the [LocalDate] of [day] when the
 * [DayContainer] we compose for it is clicked.
 * @param month the [YearMonth] containing [LocalDate] parameter [day].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [androidx.compose.samples.crane.calendar.Week] does not pass us one so the empty,
 * default, or starter [Modifier] that contains no elements is used.
 */
@Composable
internal fun Day(
    day: LocalDate,
    calendarState: CalendarUiState,
    onDayClicked: (LocalDate) -> Unit,
    month: YearMonth,
    modifier: Modifier = Modifier
) {
    val selected: Boolean = calendarState.isDateInSelectedPeriod(day)
    DayContainer(
        modifier = modifier.semantics {
            text = AnnotatedString(
                "${month.month.name.lowercase().capitalize(Locale.current)} " +
                    "${day.dayOfMonth} ${month.year}"
            )
            dayStatusProperty = selected
        },
        selected = selected,
        onClick = { onDayClicked(day) },
        onClickLabel = stringResource(id = R.string.click_label_select)
    ) {

        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                // Parent will handle semantics
                .clearAndSetSemantics {},
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.body1.copy(color = Color.White)
        )
    }
}

/**
 * TODO: Add kdoc
 */
val DayStatusKey: SemanticsPropertyKey<Boolean> =
    SemanticsPropertyKey("DayStatusKey")

/**
 * TODO: Add kdoc
 */
var SemanticsPropertyReceiver.dayStatusProperty: Boolean by DayStatusKey
