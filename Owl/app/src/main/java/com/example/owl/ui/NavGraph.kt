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

package com.example.owl.ui

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.savedstate.SavedState
import com.example.owl.ui.MainDestinations.COURSE_DETAIL_ID_KEY
import com.example.owl.ui.course.CourseDetails
import com.example.owl.ui.courses.CourseTabs
import com.example.owl.ui.courses.courses
import com.example.owl.ui.onboarding.Onboarding

/**
 * The routes for the Destinations used in the ([OwlApp]).
 */
object MainDestinations {
    const val ONBOARDING_ROUTE: String = "onboarding"
    const val COURSES_ROUTE: String = "courses"
    const val COURSE_DETAIL_ROUTE: String = "course"
    const val COURSE_DETAIL_ID_KEY: String = "courseId"
}

/**
 * Provides the navigation graph for the app.
 *
 * We start by initializing and remembering our [MutableState] of [Boolean] variable
 * `onboardingComplete` to the inverse of our [Boolean] parameter [showOnboardingInitially] (with
 * the `key1` argument to [remember] our [Boolean] variable [showOnboardingInitially]). Then we
 * initialize and remember [MainActions] variable `actions` with our [NavHostController] parameter
 * [navController] as the `navController` argument (with the `key1` argument to [remember] our
 * [NavHostController] parameter [navController]).
 *
 * Then our root composable is a [NavHost] with its `navController` argument our [NavHostController]
 * parameter [navController], and its `startDestination` argument our [String] parameter
 * [startDestination]. In its [NavGraphBuilder] `builder` composable lambda argument we:
 *
 * **First**: we use the [NavGraphBuilder.composable] method to add a destination with the `route`
 * [MainDestinations.ONBOARDING_ROUTE]. In its [AnimatedContentScope] `content` composable lambda
 * argument we first call the [BackHandler] method to add an [OnBackPressedCallback] lambda that
 * calls our [finishActivity] lambda parameter to finish the activity (Intercepts back in Onboarding
 * to make it finish the activity). Then we compose an [Onboarding] composable with its
 * `onboardingComplete` argument a lambda that sets our [MutableState] variable `onboardingComplete`
 * to `true` (Sets the flag so that onboarding is not shown next time) then calls the
 * [MainActions.onboardingComplete] method of [MainActions] variable `actions`.
 *
 * **Second**: we use the [NavGraphBuilder.navigation] method to add a nested [NavGraph] with its
 * `route` argument [MainDestinations.COURSES_ROUTE] and its `startDestination` argument the
 * [CourseTabs.route] of [CourseTabs.FEATURED]. In its [NavGraphBuilder] `builder` composable lambda
 * argument we compose a [NavGraphBuilder.courses] (Defines the navigation graph for the courses
 * feature) whose arguments are:
 *  - `onCourseSelected`: is the [MainActions.openCourse] method of [MainActions] variable `actions`.
 *  - `onboardingComplete`: is our [MutableState] wrapped [Boolean] variable `onboardingComplete`.
 *  - `navController`: is our [NavHostController] parameter [navController].
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *
 * **Third**: we use the [NavGraphBuilder.composable] method to add a destination for the `route`
 * [MainDestinations.COURSE_DETAIL_ROUTE] with its `arguments` argument a [List] of
 * [navArgument] whose `name` argument is [COURSE_DETAIL_ID_KEY] and its `type` argument is
 * [NavType.LongType]. In its [AnimatedContentScope] `content` composable lambda argument we
 * accept the [NavBackStackEntry] passed the lambda in variable `backStackEntry` and then
 * initialize our [SavedState] variable `arguments` with the [NavBackStackEntry.arguments] of
 * `backStackEntry` (throwing [IllegalArgumentException] if it is `null`). Then we initialize our
 * [Long] variable `currentCourseId` with the [Long] stored under the `key` [COURSE_DETAIL_ID_KEY]
 * in `arguments`. Then we compose a [CourseDetails] composable whose arguments are:
 *  - `courseId`: is our [Long] variable `currentCourseId`.
 *  - `selectCourse`: is a lambda that accepts the [Long] passed the lambda in variable `newCourseId
 *  and calls the [MainActions.relatedCourse] method of [MainActions] variable `actions` with
 *  `newCourseId` and `backStackEntry` as its arguments.
 *  - `upPress`: is a lambda that calls the [MainActions.upPress] method of [MainActions] variable
 *  `actions` with `backStackEntry` as its argument.
 *
 * @param modifier [Modifier] to apply to the [NavHost].
 * @param finishActivity Callback to finish the activity.
 * @param navController [NavHostController] to manage navigation.
 * @param startDestination The starting destination of the graph.
 * @param showOnboardingInitially Whether to show the onboarding screen initially.
 */
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    finishActivity: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.COURSES_ROUTE,
    showOnboardingInitially: Boolean = true
) {
    // Onboarding could be read from shared preferences.
    val onboardingComplete: MutableState<Boolean> = remember(key1 = showOnboardingInitially) {
        mutableStateOf(!showOnboardingInitially)
    }

    val actions: MainActions = remember(key1 = navController) { MainActions(navController = navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = MainDestinations.ONBOARDING_ROUTE) {
            // Intercept back in Onboarding: make it finish the activity
            BackHandler {
                finishActivity()
            }

            Onboarding(
                onboardingComplete = {
                    // Set the flag so that onboarding is not shown next time.
                    onboardingComplete.value = true
                    actions.onboardingComplete()
                }
            )
        }
        navigation(
            route = MainDestinations.COURSES_ROUTE,
            startDestination = CourseTabs.FEATURED.route
        ) {
            courses(
                onCourseSelected = actions.openCourse,
                onboardingComplete = onboardingComplete,
                navController = navController,
                modifier = modifier
            )
        }
        composable(
            route = "${MainDestinations.COURSE_DETAIL_ROUTE}/{$COURSE_DETAIL_ID_KEY}",
            arguments = listOf(
                navArgument(name = COURSE_DETAIL_ID_KEY) { type = NavType.LongType }
            )
        ) { backStackEntry: NavBackStackEntry ->
            val arguments: SavedState = requireNotNull(backStackEntry.arguments)
            val currentCourseId: Long = arguments.getLong(COURSE_DETAIL_ID_KEY)
            CourseDetails(
                courseId = currentCourseId,
                selectCourse = { newCourseId: Long ->
                    actions.relatedCourse(newCourseId, backStackEntry)
                },
                upPress = { actions.upPress(backStackEntry) }
            )
        }
    }
}

/**
 * Models the navigation actions in the app.
 *
 * It contains several lambda properties which are used to navigate to different destinations in
 * the app.
 *
 * @param navController The [NavHostController] that this class will use to navigate.
 */
class MainActions(navController: NavHostController) {
    /**
     * Pops the back stack when the onboarding is complete.
     */
    val onboardingComplete: () -> Unit = {
        navController.popBackStack()
    }

    /**
     * Navigate to the course detail screen for the given `newCourseId`.( Used from COURSES_ROUTE)
     *
     * In order to discard duplicated navigation events, we check that the lifecycle of the
     * [NavBackStackEntry] `from` is [Lifecycle.State.RESUMED] using our extension function
     * [NavBackStackEntry.lifecycleIsResumed] before calling the [NavHostController.navigate]
     * method of our [NavHostController] field [navController] to navigate to the route
     * [MainDestinations.COURSE_DETAIL_ROUTE] with `newCourseId` as its argument.
     *
     * param `newCourseId` the ID of the course that is to be displayed.
     *
     * param `from` the [NavBackStackEntry] that is the source of this navigation.
     */
    val openCourse: (Long, NavBackStackEntry) -> Unit =
        { newCourseId: Long, from: NavBackStackEntry ->
            // In order to discard duplicated navigation events, we check the Lifecycle
            if (from.lifecycleIsResumed()) {
                navController.navigate(route = "${MainDestinations.COURSE_DETAIL_ROUTE}/$newCourseId")
            }
        }

    /**
     * Navigates to the [CourseDetails] screen for the course whose ID is its `newCourseId` [Long]
     * parameter. (Used from COURSE_DETAIL_ROUTE)
     *
     * This is used by the [CourseDetails] screen to navigate to a related course. Before navigating
     * we check if the [Lifecycle.State] of our `from` [NavBackStackEntry] parameter is
     * [Lifecycle.State.RESUMED] and only navigate if it is in order to discard duplicated
     * navigation events.
     *
     * param `newCourseId` the course ID of the course that we are to navigate to.
     * 
     * param `from` the [NavBackStackEntry] that we are navigating from.
     */
    val relatedCourse: (Long, NavBackStackEntry) -> Unit =
        { newCourseId: Long, from: NavBackStackEntry ->
            // In order to discard duplicated navigation events, we check the Lifecycle
            if (from.lifecycleIsResumed()) {
                navController.navigate(route = "${MainDestinations.COURSE_DETAIL_ROUTE}/$newCourseId")
            }
        }

    /**
     * This lambda is used by the `upPress` argument of the [CourseDetails] screen to navigate "Up"
     * in the back stack. (Used from COURSE_DETAIL_ROUTE)
     *
     * We check whether the [Lifecycle.State] of our `from` [NavBackStackEntry]
     * parameter is [Lifecycle.State.RESUMED] and only call the [NavHostController.navigateUp]
     * method of `navController` if it is, in order to discard duplicated navigation events.
     *
     * param `from` the [NavBackStackEntry] that we are navigating from.
     */
    val upPress: (from: NavBackStackEntry) -> Unit = { from: NavBackStackEntry ->
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigateUp()
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
