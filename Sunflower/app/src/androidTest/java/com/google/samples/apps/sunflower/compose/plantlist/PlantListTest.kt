/*
 * Copyright 2023 Google LLC
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

package com.google.samples.apps.sunflower.compose.plantlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.samples.apps.sunflower.compose.plantdetail.plantForTesting
import com.google.samples.apps.sunflower.data.Plant
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A test class for the [PlantListScreen] Composable.
 *
 * This class utilizes a [ComposeContentTestRule] created by [createComposeRule] to test the UI
 * components of the plant list screen. It verifies that the plant items are displayed correctly.
 *
 * The @[RunWith] annotation causes JUnit to invoke the [AndroidJUnit4] class to run the tests in
 * this class instead of the runner built into JUnit.
 */
@RunWith(AndroidJUnit4::class)
class PlantListTest {
    /**
     * A rule for testing Compose UIs.
     *
     * This rule provides the ability to set Compose content and interact with it for testing purposes.
     * It is used here to test the [PlantListScreen] Composable.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    /**
     * Tests that a plant item is shown in the plant list.
     *
     * This test sets the content to [PlantListScreen] with a list containing a single test plant.
     * It then verifies that a node with the text "Apple" (the name of the test plant) is displayed
     * on the screen.
     */
    @Test
    fun plantList_itemShown() {
        startPlantList()
        composeTestRule.onNodeWithText(text = "Apple").assertIsDisplayed()
    }

    /**
     * A helper function to set the content of the Compose test rule to the [PlantListScreen].
     *
     * This function is used to initialize the UI for testing, displaying a list with a single
     * test plant. It allows for an optional click handler to be provided for testing interactions.
     *
     * @param onPlantClick A lambda function to be invoked when a plant item is clicked. Defaults
     * to an empty lambda.
     */
    private fun startPlantList(onPlantClick: (Plant) -> Unit = {}) {
        composeTestRule.setContent {
            PlantListScreen(plants = listOf(plantForTesting()), onPlantClick = onPlantClick)
        }
    }
}