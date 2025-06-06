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

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.owl.R
import com.example.owl.model.courses
import com.example.owl.ui.fakes.installTestImageLoader
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the navigation flows in the app are correct.
 */
class NavigationTest {

    /**
     * Using an empty activity to have control of the content that is set.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity> =
        createAndroidComposeRule<ComponentActivity>()

    /**
     * Helper function to start the app with a specific nav graph.
     *
     * This allows a test to quickly start the app in the screen under test.
     *
     * @param startDestination The destination to start the app in.
     */
    private fun startActivity(startDestination: String? = null) {
        installTestImageLoader()
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalOnBackPressedDispatcherOwner provides composeTestRule.activity
            ) {
                if (startDestination == null) {
                    NavGraph()
                } else {
                    NavGraph(
                        startDestination = startDestination,
                        showOnboardingInitially = false
                    )
                }
            }
        }
    }

    /**
     * When the app is opened, the first screen should be the onboarding screen.
     */
    @Test
    fun firstScreenIsOnboarding() {
        // When the app is open
        startActivity()
        // The first screen should be the onboarding screen.
        // Assert that the FAB label for the onboarding screen exists:
        composeTestRule.onNodeWithContentDescription(getOnboardingFabLabel()).assertExists()
    }

    /**
     * When the app is in the Onboarding screen, and the user clicks on the FAB,
     * the Courses screen is shown.
     */
    @Test
    fun onboardingToCourses() {
        // Given the app in the onboarding screen
        startActivity()

        // Navigate to the next screen by clicking on the FAB
        val fabLabel = getOnboardingFabLabel()
        composeTestRule.onNodeWithContentDescription(fabLabel).performClick()

        // The first course should be shown
        composeTestRule.onNodeWithText(
            text = courses.first().name,
            substring = true
        ).assertExists()
    }

    /**
     * When the app is in the Courses screen, and the user clicks on a course,
     * the Course Details screen is shown with the correct course.
     */
    @Test
    fun coursesToDetail() {
        // Given the app in the courses screen
        startActivity(MainDestinations.COURSES_ROUTE)

        // Navigate to the first course
        composeTestRule.onNode(
            hasContentDescription(getFeaturedCourseLabel()).and(
                hasText(
                    text = courses.first().name,
                    substring = true
                )
            )
        ).performClick()

        // Assert navigated to the course details
        composeTestRule.onNodeWithText(
            text = getCourseDesc().take(15),
            substring = true
        ).assertExists()
    }

    /**
     * Test that navigating to a course detail screen and then pressing back returns to the
     * courses screen.
     */
    @Test
    fun coursesToDetailAndBack() {
        coursesToDetail()
        composeTestRule.runOnUiThread {
            composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        }

        // The first course should be shown
        composeTestRule.onNodeWithText(
            text = courses.first().name,
            substring = true
        ).assertExists()
    }

    /**
     * Helper function to get the string for the onboarding FAB.
     */
    private fun getOnboardingFabLabel(): String {
        return composeTestRule.activity.resources.getString(R.string.label_continue_to_courses)
    }

    /**
     * Helper function to get the string for the featured course label.
     */
    private fun getFeaturedCourseLabel(): String {
        return composeTestRule.activity.resources.getString(R.string.featured)
    }

    /**
     * Helper function to get the string for the course description.
     */
    private fun getCourseDesc(): String {
        return composeTestRule.activity.resources.getString(R.string.course_desc)
    }
}
