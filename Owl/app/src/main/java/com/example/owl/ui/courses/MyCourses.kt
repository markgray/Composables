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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.owl.model.Course
import com.example.owl.model.courses
import com.example.owl.ui.common.CourseListItem
import com.example.owl.ui.theme.BlueTheme

/**
 * Displays a list of courses.
 *
 * Our root composable is a [LazyColumn] whose `modifier` argument is our [Modifier] parameter
 * [modifier]. In the [LazyListScope] `content` composable lambda argument of the [LazyColumn] we
 * compose:
 *
 * **First**: We compose a [LazyListScope.item] whose [LazyItemScope] `content` argument is a [Spacer]
 * whose `modifier` argument is a [Modifier.windowInsetsTopHeight] whose `insets` argument is
 * [WindowInsets.Companion.statusBars] (adds space to avoid overlap with the status bar).
 *
 * **Second**: We compose a [LazyListScope.item] whose [LazyItemScope] `content` argument is a
 * [CoursesAppBar].
 *
 * **Third**: We compose a [LazyListScope.itemsIndexed] whose `items` argument is our [List] of
 * [Course] parameter [courses] and whose `key` argument is a lambda that accepts the current
 * [Course] passed the lambda in variable `course` and returns the [Course.id] of `course`. In
 * the [LazyItemScope] `content` composable lambda argument of the [LazyListScope.itemsIndexed] we
 * accept the index of the current [Course] passed the lambda in variable `index` and the [Course]
 * in variable `course`, then compose a [MyCourse] whose arguments are:
 *  - `course`: is [Course] variable `course`.
 *  - `index`: is [Int] variable `index`.
 *  - `selectCourse`: is our lambda parameter [selectCourse].
 *
 * @param courses The list of courses to display.
 * @param selectCourse A function to be called when a course is selected.
 * @param modifier A [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us a [Modifier.padding] whose [PaddingValues] traces back to the
 * [PaddingValues] that the [Scaffold] that houses us passed its `content` argument.
 */
@Composable
fun MyCourses(
    courses: List<Course>,
    selectCourse: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            Spacer(modifier = Modifier.windowInsetsTopHeight(insets = WindowInsets.statusBars))
        }
        item {
            CoursesAppBar()
        }
        itemsIndexed(
            items = courses,
            key = { _, course: Course -> course.id }
        ) { index: Int, course: Course ->
            MyCourse(
                course = course,
                index = index,
                selectCourse = selectCourse
            )
        }
    }
}

/**
 * Displays a single course in the list of courses. The course item is staggered based on its
 * index in the list, creating a visually interesting layout.
 *
 * Our root composable is a [Row] whose `modifier` argument is a [Modifier.padding] that adds `8.dp`
 * to the `bottom`. In the [RowScope] `content` composable lambda argument we start by initializing
 * our [Dp] variable `stagger` to `72.dp` if the [Int] parameter [index] is even, or `16.dp` if it
 * is odd. We then compose a [Spacer] whose `modifier` argument is a [Modifier.width] whose `width`
 * argument is `stagger`. We then compose a [CourseListItem] whose arguments are:
 *  - `course`: is our [Course] parameter [course].
 *  - `onClick`: is a lambda that calls our lambda parameter [selectCourse] with the [Course.id] of
 *  our [Course] parameter [course].
 *  - `shape`: is a [RoundedCornerShape] whose `topStart` argument is `24.dp`.
 *  - `modifier`: is a [Modifier.height] whose `height` argument is `96.dp`.
 *
 * @param course The [Course] to display.
 * @param index The index of the course in the list. This is used to stagger the course item.
 * @param selectCourse A function to be called when the course is selected.
 */
@Composable
fun MyCourse(
    course: Course,
    index: Int,
    selectCourse: (Long) -> Unit
) {
    Row(modifier = Modifier.padding(bottom = 8.dp)) {
        val stagger: Dp = if (index % 2 == 0) 72.dp else 16.dp
        Spacer(modifier = Modifier.width(width = stagger))
        CourseListItem(
            course = course,
            onClick = { selectCourse(course.id) },
            shape = RoundedCornerShape(topStart = 24.dp),
            modifier = Modifier.height(height = 96.dp)
        )
    }
}

/**
 * Preview of [MyCourses].
 */
@Preview(name = "My Courses")
@Composable
private fun MyCoursesPreview() {
    BlueTheme {
        MyCourses(
            courses = courses,
            selectCourse = { }
        )
    }
}
