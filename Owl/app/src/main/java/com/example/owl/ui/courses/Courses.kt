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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.owl.R
import com.example.owl.model.Course
import com.example.owl.model.Topic
import com.example.owl.model.courses
import com.example.owl.model.topics
import com.example.owl.ui.MainDestinations
import kotlinx.coroutines.CoroutineScope

/**
 * Defines the navigation graph for the courses feature.
 *
 * This function sets up three composable destinations:
 * - Featured Courses: Displays a list of featured courses. If onboarding is not complete,
 *   it navigates to the onboarding screen.
 * - My Courses: Displays a list of courses the user is enrolled in.
 * - Search Courses: Allows users to search for courses.
 *
 * **First**: We use the [NavGraphBuilder.composable] function to add a composable destination for
 * the `route` [CourseTabs.route] of [CourseTabs.FEATURED]. In the [AnimatedContentScope] `content`
 * composable lambda argument we accept the [NavBackStackEntry] passed the lambda in variable `from`
 * and first compose a [LaunchedEffect] whose `key1` argument is the value of our [State] wrapped
 * [Boolean] parameter [onboardingComplete] and in the [CoroutineScope] block of the [LaunchedEffect]
 * if the `value` of [onboardingComplete] is `false` we navigate to the
 * [MainDestinations.ONBOARDING_ROUTE]. Then if the `value` of [onboardingComplete] is `true` we
 * compose a [FeaturedCourses] composable whose arguments are:
 *  - `courses`: The global [List] of [Course] property [courses].
 *  - `selectCourse`: A lambda which accepts the [Long] passed the lambda in variable `id` and then
 *  calls our [onCourseSelected] lambda parmeter with `id` and the [NavBackStackEntry] variable
 *  `from`.
 *  - `modifier`: Our [Modifier] parameter [modifier].
 *
 * **Second**: We use the [NavGraphBuilder.composable] function to add a composable destination for
 * the `route` [CourseTabs.route] of [CourseTabs.MY_COURSES]. In the [AnimatedContentScope] `content`
 * composable lambda argument we accept the [NavBackStackEntry] passed the lambda in variable `from`
 * and then compose a [MyCourses] composable whose arguments are:
 *  - `courses`: The global [List] of [Course] property [courses].
 *  - `selectCourse`: A lambda which accepts the [Long] passed the lambda in variable `id` and then
 *  calls our [onCourseSelected] lambda parmeter with `id` and the [NavBackStackEntry] variable
 *  `from`.
 *  - `modifier`: Our [Modifier] parameter [modifier].
 *
 * **Third**: We use the [NavGraphBuilder.composable] function to add a composable destination for
 * the `route` [CourseTabs.route] of [CourseTabs.SEARCH]. In the [AnimatedContentScope] `content`
 * composable lambda argument we accept the [NavBackStackEntry] passed the lambda in variable `it`
 * (and ignore it) and then compose a [SearchCourses] composable whose arguments are:
 *  - `topics`: The global [List] of [Topic] property [topics].
 *  - `modifier`: Our [Modifier] parameter [modifier].
 *
 * @param onCourseSelected A lambda function that is invoked when a course is selected.
 * It receives the course ID and the NavBackStackEntry as parameters.
 * @param onboardingComplete A [State] of [Boolean] that indicates whether the onboarding process
 * has been completed (`true`) or not (`false`)..
 * @param navController The [NavHostController] used for navigation.
 * @param modifier A [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us a [Modifier.padding] that adds the [PaddingValues] that the
 * [Scaffold] we are composed into passes its `content` composable lambda argument.
 */
fun NavGraphBuilder.courses(
    onCourseSelected: (Long, NavBackStackEntry) -> Unit,
    onboardingComplete: State<Boolean>, // https://issuetracker.google.com/174783110
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    composable(route = CourseTabs.FEATURED.route) { from: NavBackStackEntry ->
        // Show onboarding instead if not shown yet.
        LaunchedEffect(key1 = onboardingComplete) {
            if (!onboardingComplete.value) {
                navController.navigate(route = MainDestinations.ONBOARDING_ROUTE)
            }
        }
        if (onboardingComplete.value) { // Avoid glitch when showing onboarding
            FeaturedCourses(
                courses = courses,
                selectCourse = { id: Long -> onCourseSelected(id, from) },
                modifier = modifier
            )
        }
    }
    composable(route = CourseTabs.MY_COURSES.route) { from: NavBackStackEntry ->
        MyCourses(
            courses = courses,
            selectCourse = { id: Long -> onCourseSelected(id, from) },
            modifier = modifier
        )
    }
    composable(route = CourseTabs.SEARCH.route) {
        SearchCourses(topics = topics, modifier = modifier)
    }
}

/**
 * Display a simple TopAppBar with the Owl logo and a Profile icon.
 *
 * Our root composable is a [TopAppBar] whose `elevation` argument is `0.dp`, and whose `modifier`
 * argument is a [Modifier.height] whose `height` argument is `80.dp`. In its [RowScope] `content`
 * composable lambda argument we compose:
 *
 * **First**: An [Image] whose arguments are:
 *  - `modifier`: A [Modifier.padding] that adds `16.dp` padding to `all` sides of the [Image]
 *  chained to a [RowScope.align] whose `alignment` argument is [Alignment.CenterVertically].
 *  - `painter`: The [Painter] that [painterResource] creates from the drawable with resource ID
 *  `R.drawable.ic_lockup_white`.
 *  - `contentDescription`: is `null`
 *
 * **Second**: An [IconButton] whose `modifier` argument is a [RowScope.align] whose `alignment`
 * argument is [Alignment.CenterVertically], and whose `onClick` argument is a do-nothing lambda.
 * In its `content` composable lambda argument we compose an [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Filled.AccountCircle]
 *  - `contentDescription`: is the [String] with resource ID `R.string.label_profile` ("Profile")
 */
@Composable
fun CoursesAppBar() {
    TopAppBar(
        elevation = 0.dp,
        modifier = Modifier.height(height = 80.dp)
    ) {
        Image(
            modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_lockup_white),
            contentDescription = null
        )
        IconButton(
            modifier = Modifier.align(alignment = Alignment.CenterVertically),
            onClick = { /* todo */ }
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = stringResource(id = R.string.label_profile)
            )
        }
    }
}

/**
 * An enum class that defines the tabs available in the Courses screen.
 *
 * Each tab has a title, icon, and route associated with it.
 *  - `MY_COURSES`: Represents the "My Courses" tab, displaying courses the user is "enrolled" in.
 *  - `FEATURED`: Represents the "Featured" tab, displaying featured courses.
 *  - `SEARCH`: Represents the "Search" tab, allowing users to search for courses.
 *
 * @param title The string resource ID for the title of the tab.
 * @param icon The drawable resource ID for the icon of the tab.
 * @param route The navigation route associated with the tab.
 */
enum class CourseTabs(
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int,
    val route: String
) {
    MY_COURSES(R.string.my_courses, R.drawable.ic_grain, CoursesDestinations.MY_COURSES_ROUTE),
    FEATURED(R.string.featured, R.drawable.ic_featured, CoursesDestinations.FEATURED_ROUTE),
    SEARCH(R.string.search, R.drawable.ic_search, CoursesDestinations.SEARCH_COURSES_ROUTE)
}

/**
 * Destinations used in [CourseTabs].
 */
private object CoursesDestinations {
    const val FEATURED_ROUTE = "courses/featured"
    const val MY_COURSES_ROUTE = "courses/my"
    const val SEARCH_COURSES_ROUTE = "courses/search"
}
