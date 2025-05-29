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

package com.example.owl.ui.courses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import com.example.owl.R
import com.example.owl.model.Course
import com.example.owl.model.courses
import com.example.owl.ui.common.OutlinedAvatar
import com.example.owl.ui.theme.BlueTheme
import com.example.owl.ui.theme.OwlTheme
import com.example.owl.ui.utils.NetworkImage
import java.util.Locale
import kotlin.math.ceil

/**
 * Display a list of courses. This is the destination for the [CourseTabs.FEATURED] route.
 *
 * Our root composable is a [Column] whose `modifier` argument chains to our [Modifier] parmaeter
 * [modifier] a [Modifier.verticalScroll] to allow it to scroll, and then chains to that a
 * [Modifier.statusBarsPadding] to add padding to accommodate the status bars insets. In the
 * [ColumnScope] `content` composable lambda argument we first compose a [CoursesAppBar], then we
 * compose a [StaggeredVerticalGrid] whose `maxColumnWidth` argument is 220.dp and whose `modifier`
 * argument is a [Modifier.padding] that adds `4.dp` to `all` sides. In the `content` composable
 * lambda argument we use the [Iterable.forEach] method of our [List] of [Course] parameter [courses]
 * to loop through each [Course] capturing the [Course] passed the lambda in variable `course`, the
 * compose a [FeaturedCourse] whose `course` argument is `course`, and whose `selectCourse` argument
 * is our lambda parameter [selectCourse].
 *
 * @param courses (state) the list of courses to display
 * @param selectCourse (event) request navigation to Course Details
 * @param modifier A [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior.
 */
@Composable
fun FeaturedCourses(
    courses: List<Course>,
    selectCourse: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(state = rememberScrollState())
            .statusBarsPadding()
    ) {
        CoursesAppBar()
        StaggeredVerticalGrid(
            maxColumnWidth = 220.dp,
            modifier = Modifier.padding(all = 4.dp)
        ) {
            courses.forEach { course: Course ->
                FeaturedCourse(course = course, selectCourse = selectCourse)
            }
        }
    }
}

/**
 * This composable displays a single [Course].
 * TODO: Continue here.
 *
 * @param course (state) the [Course] to display
 * @param selectCourse (event) request navigation to Course Details
 * @param modifier A [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior.
 */
@Composable
fun FeaturedCourse(
    course: Course,
    selectCourse: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(all = 4.dp),
        color = MaterialTheme.colors.surface,
        elevation = OwlTheme.elevations.card,
        shape = MaterialTheme.shapes.medium
    ) {
        val featuredString = stringResource(id = R.string.featured)
        ConstraintLayout(
            modifier = Modifier
                .clickable(
                    onClick = { selectCourse(course.id) }
                )
                .semantics {
                    contentDescription = featuredString
                }
        ) {
            val (image, avatar, subject, name, steps, icon) = createRefs()
            NetworkImage(
                url = course.thumbUrl,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(ratio = 4f / 3f)
                    .constrainAs(ref = image) {
                        centerHorizontallyTo(other = parent)
                        top.linkTo(anchor = parent.top)
                    }
            )
            val outlineColor: Color = LocalElevationOverlay.current?.apply(
                color = MaterialTheme.colors.surface,
                elevation = OwlTheme.elevations.card
            ) ?: MaterialTheme.colors.surface
            OutlinedAvatar(
                url = course.instructor,
                outlineColor = outlineColor,
                modifier = Modifier
                    .size(size = 38.dp)
                    .constrainAs(ref = avatar) {
                        centerHorizontallyTo(other = parent)
                        centerAround(anchor = image.bottom)
                    }
            )
            Text(
                text = course.subject.uppercase(Locale.getDefault()),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.overline,
                modifier = Modifier
                    .padding(all = 16.dp)
                    .constrainAs(ref = subject) {
                        centerHorizontallyTo(other = parent)
                        top.linkTo(anchor = avatar.bottom)
                    }
            )
            Text(
                text = course.name,
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .constrainAs(ref = name) {
                        centerHorizontallyTo(other = parent)
                        top.linkTo(anchor = subject.bottom)
                    }
            )
            val center: ConstraintLayoutBaseScope.VerticalAnchor =
                createGuidelineFromStart(fraction = 0.5f)
            Icon(
                imageVector = Icons.Rounded.OndemandVideo,
                tint = MaterialTheme.colors.primary,
                contentDescription = null,
                modifier = Modifier
                    .size(size = 16.dp)
                    .constrainAs(ref = icon) {
                        end.linkTo(anchor = center)
                        centerVerticallyTo(other = steps)
                    }
            )
            Text(
                text = course.steps.toString(),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier
                    .padding(
                        start = 4.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
                    .constrainAs(ref = steps) {
                        start.linkTo(anchor = center)
                        top.linkTo(anchor = name.bottom)
                    }
            )
        }
    }
}

@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    maxColumnWidth: Dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables: List<Measurable>, constraints: Constraints ->
        check(value = constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columns: Int = ceil(constraints.maxWidth / maxColumnWidth.toPx()).toInt()
        val columnWidth: Int = constraints.maxWidth / columns
        val itemConstraints: Constraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(columns) // track each column's height
        val placeables: List<Placeable> = measurables.map { measurable: Measurable ->
            val column: Int = shortestColumn(colHeights = colHeights)
            val placeable: Placeable = measurable.measure(constraints = itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height: Int =
            colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
                ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(columns)
            placeables.forEach { placeable: Placeable ->
                val column: Int = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index: Int, height: Int ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}

/**
 * Preview of [FeaturedCourse]
 */
@Preview(name = "Featured Course")
@Composable
private fun FeaturedCoursePreview() {
    BlueTheme {
        FeaturedCourse(
            course = courses.first(),
            selectCourse = { }
        )
    }
}

/**
 * Preview of [FeaturedCourses] in Portrait mode
 */
@Preview(name = "Featured Courses Portrait")
@Composable
private fun FeaturedCoursesPreview() {
    BlueTheme {
        FeaturedCourses(
            courses = courses,
            selectCourse = { }
        )
    }
}

/**
 * Preview of [FeaturedCourses] in Dark Theme in Portrait mode
 */
@Preview(name = "Featured Courses Dark")
@Composable
private fun FeaturedCoursesPreviewDark() {
    BlueTheme(darkTheme = true) {
        FeaturedCourses(
            courses = courses,
            selectCourse = { }
        )
    }
}

/**
 * Preview of [FeaturedCourses] in Landscape mode
 */
@Preview(
    name = "Featured Courses Landscape",
    widthDp = 640,
    heightDp = 360
)
@Composable
private fun FeaturedCoursesPreviewLandscape() {
    BlueTheme {
        FeaturedCourses(
            courses = courses,
            selectCourse = { }
        )
    }
}
