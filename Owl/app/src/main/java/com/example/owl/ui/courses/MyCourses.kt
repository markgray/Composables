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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
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
 * TODO: Continue here.
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
    LazyColumn(modifier) {
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
