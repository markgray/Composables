/*
 * Copyright 2020 Google LLC
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

package com.google.samples.apps.sunflower

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

/**
 * A test class for the [GardenActivity] that exercises the user interface and functionality
 * of the garden screen. It uses Hilt for dependency injection and Compose Test Rule for UI testing.
 * This class verifies a key user flow: navigating from the garden to the plant list.
 *
 * The @[HiltAndroidTest] annotation is used to have Hilt inject dependencies into the test class.
 */
@HiltAndroidTest
class GardenActivityTest {

    /**
     * A rule from the Hilt testing library that manages the state of the Hilt components and
     * injects dependencies into the test class.
     */
    private val hiltRule = HiltAndroidRule(this)

    /**
     * A rule for testing Compose UIs with an Android `Activity`.
     *
     * This rule launches the [GardenActivity] before each test and provides access to the activity
     * and the Compose test framework. It's used to interact with and verify the UI components
     * within the [GardenActivity].
     */
    private val composeTestRule = createAndroidComposeRule<GardenActivity>()

    /**
     * A rule that combines other rules into a chain.
     *
     * This is necessary to control the order of execution of rules. In this case, [hiltRule]
     * needs to be executed before [composeTestRule] to ensure that Hilt's dependency injection
     * is set up before the activity and its Compose content are launched.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(hiltRule)
        .around(composeTestRule)

    /**
     * Initializes the test environment before each test.
     *
     * This method sets up a test-specific configuration for [WorkManager] to ensure that
     * background tasks can be tested synchronously and reliably. It uses a [SynchronousExecutor]
     * to execute [WorkManager] tasks immediately on the same thread, which simplifies testing
     * by removing the complexities of asynchronous operations.
     *
     * The @[Before] annotation ensures this setup logic is run before each test case in this class.
     */
    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    /**
     * Tests that clicking the "Add Plant" button navigates to the plant list screen.
     */
    @Test
    fun clickAddPlant_OpensPlantList() {
        // Given that no Plants are added to the user's garden

        // When the "Add Plant" button is clicked
        with(receiver = composeTestRule.onNodeWithText(text = "Add plant")) {
            assertExists()
            assertIsDisplayed()
            performClick()
        }

        composeTestRule.waitForIdle()

        // Then the pager should change to the Plant List page
        with(receiver = composeTestRule.onNodeWithTag(testTag = "plant_list")) {
            assertExists()
        }
    }
}