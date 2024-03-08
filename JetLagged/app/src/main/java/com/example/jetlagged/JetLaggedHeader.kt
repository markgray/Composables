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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetlagged.ui.theme.HeadingStyle
import com.example.jetlagged.ui.theme.SmallHeadingStyle
import com.example.jetlagged.ui.theme.TitleBarStyle

/**
 * This sits at the top of the [JetLaggedScreen] Composable and holds an [IconButton] which when
 * clicked calls its [onDrawerClicked] lambda parameter, and a [Text] that displays the [String]
 * with resource ID [R.string.jetlagged_app_heading] ("JetLagged"). Our root composable is a [Box]
 * whose `modifier` argument chains a [Modifier.height] to our [Modifier] parameter [modifier] that
 * sets its height to 150.dp. The `content` of the [Box] is a [Row] whose `modifier` argument is a
 * [Modifier.windowInsetsPadding] which adds padding so that its content doesn't enter the `insets`
 * space [WindowInsets.Companion.systemBars] (the space occupied by the `systemBars` at the top of
 * the device). The `content` of the [Row] is an [IconButton] whose `onClick` argument is our lambda
 * parameter [onDrawerClicked], and whose `content` is an [Icon] whose `imageVector` argument is the
 * [ImageVector] drawn by [Icons.Filled.Menu] (three horizontal bars), and whose `contentDescription`
 * argument is the [String] with resource ID [R.string.not_implemented] ("Not implemented yet"). This
 * is followed by a [Text] whose `text` argument is the [String] with resource ID
 * [R.string.jetlagged_app_heading] ("JetLagged"), whose `modifier` argument is a [Modifier.fillMaxWidth]
 * that causes it to occupy its entire incoming width contraint, with a [Modifier.padding] that adds
 * 8.dp padding to its `top`. The [TextStyle] for its `style` argument is our [TitleBarStyle] (the
 * downloadable [GoogleFont] "Lato" with a `fontSize` of 22.sp and a [FontWeight] of 700) and its
 * `textAlign` argument is [TextAlign.Start] to align the text to the leading edge of the container.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [JetLaggedScreen] passes us a [Modifier.fillMaxWidth] that causes us to
 * occupy our entire incoming width constraint.
 * @param onDrawerClicked a lambda we should call when our [IconButton] is clicked. Our caller
 * [JetLaggedScreen] passes us its own `onDrawerClicked` lambda parameter which is the `onDrawerClicked`
 * lambda parameter of the [ScreenContents] Composable, which is a reference to the `toggleDrawerState`
 * method of the [HomeScreenDrawer] Composable that calls [ScreenContents] ("opens" the navigation
 * drawer).
 */
@Preview
@Composable
fun JetLaggedHeader(
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {}
) {
    Box(
        modifier = modifier.height(height = 150.dp)
    ) {
        Row(modifier = Modifier.windowInsetsPadding(insets = WindowInsets.systemBars)) {
            IconButton(
                onClick = onDrawerClicked,
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(id = R.string.not_implemented)
                )
            }

            Text(
                text = stringResource(id = R.string.jetlagged_app_heading),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                style = TitleBarStyle,
                textAlign = TextAlign.Start
            )
        }
    }
}

/**
 * Displays the values of the "AVG. TIME IN BED" ("8h2min") and the "AVG. SLEEP TIME" ("7h15min").
 * This is composed in the same [Column] as the [JetLaggedHeader] Composable with a [Spacer] whose
 * height is 32.dp between them in the [JetLaggedScreen] Composable. That [Column] uses as its
 * `modifier` argument a [Modifier.yellowBackground] which draws an animated "Yellow" sea wave
 * background for the contents of the [Column]. Our root Composable is a [Row] whose `modifier`
 * argument chains a [Modifier.fillMaxWidth] to our [Modifier] parameter [modifier] to have it
 * occupy its entire incoming width constraint, and its `horizontalArrangement` argument is
 * [Arrangement.SpaceBetween] which causes the [Row] to place its children such that they are spaced
 * evenly across the main axis, without free space before the first child or after the last child.
 * The `content` of the [Row] is a [Column] whose `content` is a [Text] whose `text` is the [String]
 * with resource ID [R.string.average_time_in_bed_heading] ("AVG. TIME IN BED") with a [TextStyle]
 * `style` argument of [SmallHeadingStyle] (the downloadable [GoogleFont] "Lato" with a `fontSize`
 * of 16.sp and a [FontWeight] of 600) and below this is another [Text] whose `text` is the [String]
 * with resource ID [R.string.placeholder_text_ave_time] ("8h2min") with a [TextStyle] `style`
 * argument of [HeadingStyle] (the downloadable [GoogleFont] "Lato" with a `fontSize`of 24.sp and a
 * [FontWeight] of 600). Following this [Column] in the [Row] is a [Spacer] whose `modifier` argument
 * is a [Modifier.width] that sets its width to 16.dp. The [Spacer] is followed by another [Column]
 * whose `content` is a [Text] whose `text` is the [String] with resource ID [R.string.average_sleep_time_heading]
 * ("AVG. SLEEP TIME") with a [TextStyle] `style` argument of [SmallHeadingStyle] and below this is
 * another [Text] whose `text` is the [String] with resource ID [R.string.placeholder_text_ave_time_2]
 * ("7h15min") with a [TextStyle] `style` argument of [HeadingStyle]. Below the [Row] is a [Spacer]
 * whose `modifier` argument is a [Modifier.height] that sets the height of the [Spacer] to 32.dp.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [JetLaggedScreen] passes us a [Modifier.padding] that adds 16.dp to our
 * `start` and our `end`.
 */
@Preview
@Composable
fun JetLaggedSleepSummary(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.average_time_in_bed_heading),
                style = SmallHeadingStyle
            )
            Text(
                text = stringResource(id = R.string.placeholder_text_ave_time),
                style = HeadingStyle
            )
        }
        Spacer(modifier = Modifier.width(width = 16.dp))
        Column {
            Text(
                text = stringResource(id = R.string.average_sleep_time_heading),
                style = SmallHeadingStyle
            )
            Text(
                text = stringResource(id = R.string.placeholder_text_ave_time_2),
                style = HeadingStyle,
            )
        }
    }
    Spacer(modifier = Modifier.height(height = 32.dp))
}
