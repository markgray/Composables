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
import androidx.compose.material.Colors
import androidx.compose.material.ElevationOverlay
import androidx.compose.material.Icon
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.example.owl.R
import com.example.owl.model.Course
import com.example.owl.model.courses
import com.example.owl.ui.common.OutlinedAvatar
import com.example.owl.ui.theme.BlueTheme
import com.example.owl.ui.theme.Elevations
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
 * lambda argument of the [StaggeredVerticalGrid] we use the [Iterable.forEach] method of our [List]
 * of [Course] parameter [courses] to loop through each [Course] capturing the [Course] passed the
 * lambda in variable `course`, then compose a [FeaturedCourse] whose `course` argument is `course`,
 * and whose `selectCourse` argument is our lambda parameter [selectCourse].
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
 *
 * Our root composable is a [Surface] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.padding] that adds `4.dp` to `all` sides, whose `color` argument is
 * the [Colors.surface] of our custom [MaterialTheme.colors], whose `elevation` argument is the
 * [Elevations.card] of our custom [OwlTheme.elevations], and whose `shape` argument is the
 * [Shapes.medium] of our custom [MaterialTheme.shapes].
 *
 * In the `content` composable lambda argument of the [Surface] we first initialize our [String]
 * variable `featuredString` to the [String] with resource ID `R.string.featured` ("Featured") and
 * then we compose a [ConstraintLayout] whose `modifier` is a [Modifier.clickable] whose `onClick`
 * lambda argument is a lambda that calls our lambda parameter [selectCourse] with the [Course.id]
 * of our [Course] parameter [course], chained to a [Modifier.semantics] whose `properties`
 * [SemanticsPropertyReceiver] lambda argument is a lambda that sets the [contentDescription] to
 * our [String] variable `featuredString`.
 *
 * In the [ConstraintLayoutScope] `content` composable lambda argument of the [ConstraintLayout] we
 * first initialize using a destructuring declaration our [ConstrainedLayoutReference] variables
 * `image`, `avatar`, `subject`, `name`, `steps`, and `icon` to values returned by the
 * [ConstraintLayoutScope.createRefs] method. We then compose:
 *
 * **First**: We compose a [NetworkImage] whose arguments are:
 *  - `url`: is the [Course.thumbUrl] of our [Course] parameter [course].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.aspectRatio] whose `ratio` argument is `4f / 3f`, chained to a
 *  [ConstraintLayoutScope.constrainAs] whose `ref` argument is our [ConstrainedLayoutReference]
 *  variable `image`, and in its [ConstrainScope] `constrainBlock` lambda argument we use the
 *  [ConstrainScope.centerHorizontallyTo] method to center the [NetworkImage] horizontally to the
 *  `parent`, and then we use the [ConstrainScope.linkTo] method to link its `top` edge to the `top`
 *  edge of the `parent`.
 *
 * We initialize our [Color] variable `outlineColor` using the [ElevationOverlay.apply] method of
 * the `current` [LocalElevationOverlay] to create the background color for the `color` argument
 * [Colors.surface] of our custom [MaterialTheme.colors] and the `elevation` argument
 * [Elevations.card] of our custom [OwlTheme.elevations] (defaulting to the [Colors.surface]
 * of our custom [MaterialTheme.colors] if this is `null`).
 *
 * **Second**: We compose an [OutlinedAvatar] whose arguments are:
 *  - `url`: is the [Course.instructor] of our [Course] parameter [course].
 *  - `outlineColor`: is our [Color] variable `outlineColor`.
 *  - `modifier`: is a [Modifier.size] whose `size` argument is `38.dp`, chained to a
 *  [ConstraintLayoutScope.constrainAs] whose `ref` argument is our [ConstrainedLayoutReference]
 *  variable `avatar`, and in its [ConstrainScope] `constrainBlock` lambda argument we use the
 *  [ConstrainScope.centerHorizontallyTo] method to center the [OutlinedAvatar] horizontally to
 *  `parent`, and then we use the [ConstrainScope.centerAround] method to center the [OutlinedAvatar]
 *  to the `bottom` of the [NetworkImage].
 *
 * **Third**: We compose a [Text] whose arguments are:
 *  - `text`: is the [Course.subject] of our [Course] parameter [course], converted to uppercase.
 *  - `color`: is the [Colors.primary] of our custom [MaterialTheme.colors].
 *  - `style`: is the [Typography.overline] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` to all sides, chained to a
 *  [ConstraintLayoutScope.constrainAs] whose `ref` argument is our [ConstrainedLayoutReference]
 *  variable `subject`, and in its [ConstrainScope] `constrainBlock` lambda argument we use the
 *  [ConstrainScope.centerHorizontallyTo] method to center the [Text] horizontally to `parent`,
 *  and then we use the [ConstrainScope.linkTo] method to link its `top` edge to the `bottom` edge
 *  of the [ConstrainedLayoutReference] variable `avatar`.
 *
 * **Fourth**: We compose a [Text] whose arguments are:
 *  - `text`: is the [Course.name] of our [Course] parameter [course].
 *  - `style`: is the [Typography.subtitle1] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.Center].
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` to the `horizontal` sides, chained to a
 *  [ConstraintLayoutScope.constrainAs] whose `ref` argument is our [ConstrainedLayoutReference]
 *  variable `name`, and in its [ConstrainScope] `constrainBlock` lambda argument we use the
 *  [ConstrainScope.centerHorizontallyTo] method to center the [Text] horizontally to `parent`,
 *  and then we use the [ConstrainScope.linkTo] method to link its `top` edge to the `bottom` edge
 *  of the [ConstrainedLayoutReference] variable `subject`.
 *
 * We initialize our [ConstraintLayoutBaseScope.VerticalAnchor] variable `center` to the value
 * returned by the [ConstraintLayoutScope.createGuidelineFromStart] method for the `fraction`
 * argument of `0.5f`.
 *
 * **Fifth**: We compose an [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Rounded.OndemandVideo].
 *  - `tint`: is the [Colors.primary] of our custom [MaterialTheme.colors].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.size] whose `size` argument is `16.dp`, chained to a
 *  [ConstraintLayoutScope.constrainAs] whose `ref` argument is our [ConstrainedLayoutReference]
 *  variable `icon`, and in its [ConstrainScope] `constrainBlock` lambda argument we use the
 *  [ConstrainScope.linkTo] method to link its `end` edge to [ConstraintLayoutBaseScope.VerticalAnchor]
 *  variable `center`, and then we use the [ConstrainScope.centerVerticallyTo] method to center
 *  the [Icon] vertically to [ConstrainedLayoutReference] variable `steps`.
 *
 * **Sixth**: We compose a [Text] whose arguments are:
 *  - `text`: is the [Course.steps] of our [Course] parameter [course].
 *  - `color`: is the [Colors.primary] of our custom [MaterialTheme.colors].
 *  - `style`: is the [Typography.subtitle2] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] that adds `4.dp` to the `start`, `16.dp` to the `top`, and
 *  `16.dp` to the `bottom` sides, chained to a [ConstraintLayoutScope.constrainAs] whose `ref`
 *  argument is our [ConstrainedLayoutReference] variable `steps`, and in its [ConstrainScope]
 *  `constrainBlock` lambda argument we use the [ConstrainScope.linkTo] method to link its `start`
 *  edge to [ConstraintLayoutBaseScope.VerticalAnchor] variable `center`, and then we use the
 *  [ConstrainScope.linkTo] method to link its `top` edge to the `bottom` edge of the
 *  [ConstrainedLayoutReference] variable `name`.
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
        val featuredString: String = stringResource(id = R.string.featured)
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

/**
 * A composable that places its children in a staggered grid.
 *
 * Our root composable is a [Layout] whose `content` argument is our lambda parameter [content],
 * and whose `modifier` argument is our [Modifier] parmaeter [modifier]. In its [MeasureScope]
 * `measurePolicy` lambda argument we accept the [List] of [Measurable] passed the lambda in
 * variable `measurables` and the [Constraints] passed the lambda in variable `constraints`.
 *
 * We then [check] that the [Constraints.hasBoundedWidth] of our [Constraints] variable `constraints`
 * is `true`, and throw an [IllegalStateException] if it is not. We initialize our [Int] variable
 * `columns` to the ceiling of the [Constraints.maxWidth] of our [Constraints] variable `constraints`
 * divided by the pixel value of our [Dp] parameter [maxColumnWidth].
 *
 * We initialize our [Int] variable `columnWidth` to the [Constraints.maxWidth] of our
 * [Constraints] variable `constraints` divided by our [Int] variable `columns`.
 *
 * We initialize our [Constraints] variable `itemConstraints` to a copy of the [Constraints] variable
 * `constraints` with its [Constraints.maxWidth] set to our [Int] variable `columnWidth`.
 *
 * We initialize our [IntArray] variable `colHeights` to an [IntArray] whose size is our [Int]
 * variable `columns`.
 *
 * We initialize our [List] of [Placeable] variable `placeables` to the result of using the
 * [Iterable.map] method of our [List] of [Measurable] variable `measurables` to loop through
 * the [Measurable]s capturing the [Measurable] passed the `transform` lambda in variable
 * `measurable` then initializing our [Int] variable `column` to the value returned by the
 * [shortestColumn] method of our [IntArray] variable `colHeights`, then initializing our
 * [Placeable] variable `placeable` to the result of calling the [Measurable.measure] method
 * of our [Measurable] variable `measurable` with the [Constraints] variable `itemConstraints`,
 * and then setting our [IntArray] variable `colHeights` at our [Int] variable `column` to the
 * [Placeable.height] of our [Int] variable `placeable`. Finally, we return [Placeable] variable
 * `placeable` to be added to our [List] of [Placeable] variable `placeables` and loop around for
 * the next [Measurable].
 *
 * We initialize our [Int] variable `height` to the [IntArray.maxOrNull] of [IntArray] variable
 * `colHeights` coerced to be between the [Constraints.minHeight] and [Constraints.maxHeight] of
 * our [Constraints] variable `constraints`, and defaulting to the [Constraints.minHeight] if
 * that is `null`.
 *
 * Finally we call the [MeasureScope.layout] method with its `width` argument the [Constraints.maxWidth]
 * of our [Constraints] variable `constraints`, and its `height` argument our [Int] variable `height`
 * In its [Placeable.PlacementScope] `placementBlock` lambda argument we initialize our [IntArray]
 * variable `colY` to an [IntArray] whose size is our [Int] variable `columns`. We then use the
 * [Iterable.forEach] method of our [List] of [Placeable] variable `placeables` to loop through
 * the [Placeable]s capturing the [Placeable] passed the `action` lambda in variable `placeable`,
 * then initialize our [Int] variable `column` to the value returned by the [shortestColumn] method
 * when its `colHeights` argument is our [IntArray] variable `colY`, then call the
 * [Placeable.PlacementScope.place] method of our [Placeable] variable `placeable` with its `x`
 * argument the [Int] variable `columnWidth` times `column`, and its `y` argument the value of the
 * [Int] in [IntArray] variable `colY` indexed by our [Int] variable `column`. Finally we increment
 * the [Int] in [IntArray] variable `colY` at our [Int] variable `column` by the [Placeable.height]
 * of our [Placeable] variable `placeable`, and loop around for the next [Placeable].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [FeaturedCourses] passes us a [Modifier.padding] that adds `4.dp` to `all`
 * sides.
 * @param maxColumnWidth the maximum width of each column.
 * @param content (slot) the composable content of our grid.
 */
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
                val column: Int = shortestColumn(colHeights = colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

/**
 * Returns the index of the column with the shortest height in its [IntArray] parameter [colHeights].
 *
 * We initialize our [Int] variable `minHeight` to [Int.MAX_VALUE] (2147483647), and our [Int]
 * variable `column` to 0. We then loop through our [IntArray] parameter [colHeights] using the
 * [IntArray.forEachIndexed] method which calls its `action` lambda argument for each element
 * providing the `index` of that element and the `height` (element value) at that index. In the
 * lambda, if `height` is less than `minHeight` we set `minHeight` to `height` and `column` to
 * `index`.
 *
 * When done with all of the elements in [colHeights] we return `column`.
 *
 * @param colHeights an [IntArray] containing the current height of each of our columns.
 * @return the index of the column with the shortest height.
 */
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
