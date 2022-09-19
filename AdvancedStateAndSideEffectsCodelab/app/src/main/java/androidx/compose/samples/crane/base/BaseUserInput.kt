/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.samples.crane.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.samples.crane.ui.captionTextStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SimpleUserInput(
    text: String? = null,
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null
) {
    CraneUserInput(
        caption = if (text == null) caption else null,
        text = text ?: "",
        vectorImageId = vectorImageId
    )
}

@Composable
fun CraneUserInput(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null,
    tint: Color = LocalContentColor.current
) {
    CraneBaseUserInput(
        modifier = modifier,
        onClick = onClick,
        caption = caption,
        vectorImageId = vectorImageId,
        tintIcon = { text.isNotEmpty() },
        tint = tint
    ) {
        Text(text = text, style = MaterialTheme.typography.body1.copy(color = tint))
    }
}

/**
 * This Composable serves as a building block for other Composables to use to create widgets for
 * user input. It will display in a [Row] the drawable whose resource ID is its [vectorImageId]
 * parameter at the far left in an [Icon] (if it is not `null`) tinted using the [Color] parameter
 * [tint] (if the [tintIcon] lambda returns `true`). Next will be a [Text] displaying the [String]
 * in parameter [caption] (if [caption] is not `null` and the [showCaption] lambda returns `true`)
 * and at the far right will be a [Row] (using a `modifier` argument of `Modifier.weight` from the
 * `RowScope` interface in [Row] of 1f so it takes up all the space left over when the other widgets
 * in its parent's [Row] are measured) and this last [Row] contains the Composable lambda parameter
 * [content]. When this Composable is used by [CraneUserInput] the [content] lambda is a [Text] that
 * displays the `text` it was called with, and when used by [CraneEditableUserInput] it is a
 * [BasicTextField] that will enable users to edit text. The root Composable of this widget is a
 * [Surface] whose `onClick` argument is set to our [onClick] parameter, whose background `color`
 * argument is the `primaryVariant` [Color] of [MaterialTheme.colors] (which is `crane_purple_700`
 * aka `Color(0xFF720D5D)` in our [CraneTheme] custom [MaterialTheme]).
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance or
 * behavior. In our case none of our callers pass any so the empty, default, or starter [Modifier]
 * that contains no elements is used.
 * @param onClick a lambda which will be called when our root [Surface] is clicked, it defaults to
 * a do nothing lambda for all of our usages except for the use of [CraneUserInput] by the
 * `PeopleUserInput` widget (see the file home/SearchUserInput.kt) where it increments the number of
 * persons travelling.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CraneBaseUserInput(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null,
    showCaption: () -> Boolean = { true },
    tintIcon: () -> Boolean,
    tint: Color = LocalContentColor.current,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = MaterialTheme.colors.primaryVariant
    ) {
        Row(Modifier.padding(all = 12.dp)) {
            if (vectorImageId != null) {
                Icon(
                    modifier = Modifier.size(24.dp, 24.dp),
                    painter = painterResource(id = vectorImageId),
                    tint = if (tintIcon()) tint else Color(0x80FFFFFF),
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
            }
            if (caption != null && showCaption()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = caption,
                    style = (captionTextStyle).copy(color = tint)
                )
                Spacer(Modifier.width(8.dp))
            }
            Row(
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun PreviewInput() {
    CraneTheme {
        Surface {
            CraneBaseUserInput(
                tintIcon = { true },
                vectorImageId = R.drawable.ic_plane,
                caption = "Caption",
                showCaption = { true }
            ) {
                Text(text = "text", style = MaterialTheme.typography.body1)
            }
        }
    }
}
