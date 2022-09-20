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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * This Composable widget uses [CraneUserInput] to display its [String] parameter [text] in the
 * [Text] of [CraneUserInput], its [String] parameter [caption] in the `caption` of the
 * [CraneBaseUserInput] of [CraneUserInput], and the drawable whose resource ID is [vectorImageId]
 * in the [Icon] of the [CraneBaseUserInput] of [CraneUserInput]. It is used twice as arguments to
 * the `CraneSearch` Composable that is used by `EatSearchContent` (see file home/HomeFeatures.kt),
 * and once by the `CraneSearch` Composable that is used by `SleepSearchContent` (also in the file
 * home/HomeFeatures.kt). These three usages only use our [caption] and [vectorImageId] parameter
 * with the values: "Select Time" [R.drawable.ic_time], "Select Location" [R.drawable.ic_restaurant]
 * and "Select Location" [R.drawable.ic_hotel] respectively.
 *
 * @param text the [String] we should use as the `text` argument of [CraneUserInput].
 * @param caption the [String] we should use as the `caption` argument of [CraneUserInput].
 * @param vectorImageId the drawable resource ID we should use as the `vectorImageId` argument of
 * [CraneUserInput].
 */
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

/**
 * A Composable widget which uses [CraneBaseUserInput] to host a [Text] as its `content` which will
 * display our [String] parameter [text] using [tint] as the [Color] for the [TextStyle] `body1`
 * of [MaterialTheme.typography] (which is the ttf whose resource ID is [R.font.raleway_semibold],
 * whose `fontWeight` is `FontWeight.W600`, and `fontSize` is 16.sp). It is used by the [SimpleUserInput]
 * Composable (see above), the `DatesUserInput` Composable, the `FromDestination` Composable and the
 * `PeopleUserInput` Composable (see the file home/SearchUserInput.kt for the last three).
 *
 * @param text the [String] that should be displayed in our [Text].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and or
 * behavior. None of our callers pass one so the empty, default, or starter Modifier that contains
 * no elements is used instead.
 * @param onClick a lambda that we use as the `onClick` argument of [CraneBaseUserInput]. The only
 * one of our callers to pass one is the `PeopleUserInput` Composable (see the file
 * home/SearchUserInput.kt) and it is a lambda which increments the number of people traveling.
 * @param caption the [String] we use as the `caption` argument of [CraneBaseUserInput].
 * @param vectorImageId the resource ID we use as the `vectorImageId` argument of [CraneBaseUserInput]
 * @param tint the [Color] we use as the `tint` argument of [CraneBaseUserInput] and as the [Color]
 * of the [text] displayed in our [Text].
 */
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

/**
 * This is a Preview of our [CraneBaseUserInput] Composable created using the arguments:
 *  - `tintIcon` is a lambda returning `true` which causes the [Icon] created from the argument
 *  `vectorImageId` to be tinted using the default `LocalContentColor.current`.
 *  - `vectorImageId` is the drawable whose resource ID is [R.drawable.ic_plane]
 *  - `caption` is the [String] "Caption"
 *  - `showCaption` is a lambda returning `true`
 *  - `content` Composable lambda argument is a [Text] displaying the [String] "text" using the
 *  `body1` [TextStyle] of [MaterialTheme.typography] which is the ttf whose resource ID is
 *  [R.font.raleway_semibold], whose `fontWeight` is `FontWeight.W600`, and `fontSize` is 16.sp
 *
 * The [CraneBaseUserInput] Composable is wrapped in a [Surface] (for some reason) which is in turn
 * wrapped in our [CraneTheme] custom [MaterialTheme].
 */
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
