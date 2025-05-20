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

package com.example.compose.jetsurvey.survey.question

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.QuestionWrapper
import com.example.compose.jetsurvey.survey.simpleDateFormatPattern
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.theme.slightlyDeemphasizedAlpha
import com.example.compose.jetsurvey.util.getDefaultDateInMillis
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Displays a date question. Our root composable is a [QuestionWrapper] whose arguments are:
 *  - `titleResourceId`: is our [Int] parameter [titleResourceId] which is the resource ID for the
 *  string to display for the title of the question.
 *  - `directionsResourceId`: is our [Int] parameter [directionsResourceId] which is the resource
 *  ID for the string to display for the directions of the question.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *
 * Inside of the `content` composable lambda argument of the [QuestionWrapper] we start by
 * initializing our [SimpleDateFormat] variable `dateFormat` with an instance that uses our
 * [simpleDateFormatPattern] (the constant [String] "EEE, MMM d")  as the format pattern for the
 * [Locale.getDefault] locale. We then set the [SimpleDateFormat.timeZone] to the
 * [TimeZone.getTimeZone] for the UTC time zone.
 *
 * We initialize our [String] variable `dateString` with the result of calling the
 * [SimpleDateFormat.format] method of our variable `dateFormat` with our [Long] parameter
 * [dateInMillis] if [dateInMillis] is not `null`, and if [dateInMillis] is `null`, we call
 * the [getDefaultDateInMillis] method to get the default date in milliseconds.
 *
 * Then we compose a [Button] whose arguments are:
 *  - `onClick`: is our lambda parameter [onClick].
 *  - `colors`: is the [ButtonDefaults.buttonColors] with its `containerColor` set to the
 *  [ColorScheme.surface] of our custom [MaterialTheme.colorScheme] and its `contentColor` set to
 *  a copy of the [ColorScheme.onSurface] with its alpha set to [slightlyDeemphasizedAlpha].
 *  - `shape`: is the [Shapes.small] of our custom [MaterialTheme.shapes].
 *  - `modifier`: is a [Modifier.padding] that adds `20.dp` padding to the vertical sides, chained
 *  to a [Modifier.height] of `54.dp`.
 *  - `border`: is a [BorderStroke] with its `width` set to `1.dp` and its `color` set to a copy of
 *  the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme] with its alpha set to
 *  `0.12f`.
 *
 * In the [RowScope] `content` composable lambda argument of the [Button] we compose:
 *
 * **First** a [Text] whose arguments are:
 *  - `text`: is our [String] variable `dateString`.
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [RowScope.weight] with a weight of
 *  `1.8f`.
 *
 * **Second** an [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Filled.ArrowDropDown].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [RowScope.weight] with a weight of
 *  `0.2f`.
 *
 * @param titleResourceId String resource to display for the title of the question
 * @param directionsResourceId String resource to display fir the directions of the question
 * @param dateInMillis The date to display, in milliseconds, or `null` if no date has been selected
 * @param onClick The callback to be invoked when the user clicks on the date picker
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun DateQuestion(
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    dateInMillis: Long?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        titleResourceId = titleResourceId,
        directionsResourceId = directionsResourceId,
        modifier = modifier,
    ) {
        // All times are stored in UTC, so generate the display from UTC also
        val dateFormat = SimpleDateFormat(simpleDateFormatPattern, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val dateString: String = dateFormat.format(dateInMillis ?: getDefaultDateInMillis())

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
                    .copy(alpha = slightlyDeemphasizedAlpha),
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .height(height = 54.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            ),
        ) {
            Text(
                text = dateString,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1.8f)
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 0.2f)
            )
        }
    }
}

/**
 * Two previews of the [DateQuestion] composable:
 *  - One with the light theme
 *  - One with the dark theme
 */
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DateQuestionPreview() {
    JetsurveyTheme {
        Surface {
            DateQuestion(
                titleResourceId = R.string.takeaway,
                directionsResourceId = R.string.select_date,
                dateInMillis = 1672560000000, // 2023-01-01
                onClick = {},
            )
        }
    }
}
