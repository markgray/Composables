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

package com.example.owl.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.owl.R
import com.example.owl.model.Course
import com.example.owl.model.courses
import com.example.owl.ui.theme.BlueTheme
import com.example.owl.ui.theme.OwlTheme
import com.example.owl.ui.utils.NetworkImage

/**
 * Display a [Course] as a list item.
 *
 * Our root composable is a [Surface] whose `elevation` argument is our [Dp] parameter [elevation],
 * whose `shape` argument is our [Shape] parameter [shape] and whose `modifier` argument is our
 * [Modifier] parameter [modifier]. In its `content` composable lambda argument, we compose a [Row]
 * whose `modifier` argument is a [Modifier.clickable] whose `onClick` argument is our lambda
 * parameter [onClick]. In the [RowScope] `content` composable lambda argument of the [Row], we
 * first compose a [NetworkImage] whose arguments are:
 *  - `url`: is the [Course.thumbUrl] of our [Course] parameter [course].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.aspectRatio] whose `ratio` argument is `1f`.
 *
 * Next in the [Row] we compose a [Column] whose `modifier` argument is a [Modifier.padding] that
 * adds `16.dp` padding to the `start`, `16.dp` paddin gto the `top`, `16.dp` padding to the `end`,
 * and `0.dp` padding to the `bottom` of the [Column]. In the [ColumnScope] `content` composable
 * lambda argument of the [Column], we compose a [Text] whose arguments are:
 *  - `text`: is the [Course.name] of our [Course] parameter [course].
 *  - `style`: is the [TextStyle] of [Typography.subtitle1] of our custom [MaterialTheme.typography].
 *  - `maxLines`: is `2`.
 *  - `overflow`: is [TextOverflow.Ellipsis].
 *  - `modifier`: is a [ColumnScope.weight] whose `weight` argument is `1f`, chained to a
 *  [Modifier.padding] that adds `4.dp` padding to the `bottom` of the [Text].
 *
 * Below the [Text] we compose a [Row] whose `verticalAlignment` argument is
 * [Alignment.CenterVertically], and in the [RowScope] `content` composable lambda argument of the
 * [Row], we compose:
 *
 * **First**: we compose an [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Rounded.OndemandVideo].
 *  - `tint`: is the [Colors.primary] of our custom [MaterialTheme.colors].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.size] whose `size` argument is our [Dp] parameter [iconSize].
 *
 * **Second**: we compose a [Text] whose arguments are:
 *  - `text`: is the [String] formatted by [stringResource] using the format whose resource id is
 *  [R.string.course_step_steps] ("%1$d / %2$d") from the arguments [Course.step] and [Course.steps]
 *  of our [Course] parameter [course].
 *  - `color`: is the [Colors.primary] of our custom [MaterialTheme.colors].
 *  - `style`: is the [TextStyle] of [Typography.caption] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] whose that adds `8.dp` padding to the `start`, chained to
 *  a [RowScope.weight] whose `weight` argument is `1f`, chained to a [Modifier.wrapContentWidth]
 *  whose `align` argument is [Alignment.Start].
 *
 * **Third**: we compose a [NetworkImage] whose arguments are:
 *  - `url`: is the [Course.instructor] of our [Course] parameter [course].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.size] whose `size` argument is `28.dp`. chained to a [Modifier.clip]
 *  whose `shape` argument is [CircleShape].
 *
 * @param course The [Course] to display.
 * @param onClick Called when the user clicks on this course.
 * @param modifier [Modifier] to apply to this layout node.
 * @param shape The shape of the item.
 * @param elevation The elevation of the item.
 * @param titleStyle The [TextStyle] of the title.
 * @param iconSize The size of the icon.
 */
@Composable
fun CourseListItem(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    elevation: Dp = OwlTheme.elevations.card,
    titleStyle: TextStyle = MaterialTheme.typography.subtitle1,
    iconSize: Dp = 16.dp
) {
    Surface(
        elevation = elevation,
        shape = shape,
        modifier = modifier
    ) {
        Row(modifier = Modifier.clickable(onClick = onClick)) {
            NetworkImage(
                url = course.thumbUrl,
                contentDescription = null,
                modifier = Modifier.aspectRatio(ratio = 1f)
            )
            Column(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                )
            ) {
                Text(
                    text = course.name,
                    style = titleStyle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(weight = 1f)
                        .padding(bottom = 4.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.OndemandVideo,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = null,
                        modifier = Modifier.size(size = iconSize)
                    )
                    Text(
                        text = stringResource(
                            R.string.course_step_steps,
                            course.step,
                            course.steps
                        ),
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(weight = 1f)
                            .wrapContentWidth(align = Alignment.Start)
                    )
                    NetworkImage(
                        url = course.instructor,
                        contentDescription = null,
                        modifier = Modifier
                            .size(size = 28.dp)
                            .clip(shape = CircleShape)
                    )
                }
            }
        }
    }
}

/**
 * A preview of a [CourseListItem] using the light theme
 */
@Preview(name = "Course list item")
@Composable
private fun CourseListItemPreviewLight() {
    CourseListItemPreview(darkTheme = false)
}

/**
 * A preview of a [CourseListItem] using the dark theme
 */
@Preview(name = "Course list item – Dark")
@Composable
private fun CourseListItemPreviewDark() {
    CourseListItemPreview(darkTheme = true)
}

/**
 * A preview of a [CourseListItem] using either light or dark theme depending on the value of
 * its [Boolean] parameter [darkTheme]
 */
@Composable
private fun CourseListItemPreview(darkTheme: Boolean) {
    BlueTheme(darkTheme = darkTheme) {
        CourseListItem(
            course = courses.first(),
            onClick = {},
            modifier = Modifier
                .padding(end = 8.dp)
                .size(width = 288.dp, height = 80.dp),
        )
    }
}
