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

package com.google.samples.apps.sunflower.compose.garden

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import com.google.samples.apps.sunflower.utilities.testPlantAndGardenPlanting
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A test class for the [GardenScreen] composable.
 *
 * This class uses a [ComposeContentTestRule] created by [createComposeRule] to test the UI
 * components of the garden screen under different conditions, such as when the garden is
 * empty and when it contains plants.
 *
 * The @[RunWith] annotation causes JUnit to invoke the [AndroidJUnit4] class to run the tests in
 * this class instead of the runner built into JUnit.
 */
@RunWith(AndroidJUnit4::class)
class GardenTest {

    /**
     * The Compose rule used for testing.
     * This rule allows for testing of Compose UIs by providing the ability to set content,
     * find nodes, and perform actions and assertions on them.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    /**
     * Tests that the "Add plant" text is displayed when the garden is empty.
     *
     * This test case initializes the [GardenScreen] with an empty list of plantings.
     * It then asserts that the UI element with the text "Add plant" is visible,
     * which is the expected behavior for an empty garden state.
     */
    @Test
    fun garden_emptyGarden() {
        startGarden(gardenPlantings = emptyList())
        composeTestRule.onNodeWithText(text = "Add plant").assertIsDisplayed()
    }

    /**
     * Tests that the garden screen displays plants when the garden is not empty.
     *
     * This test initializes the [GardenScreen] with a predefined list containing one plant.
     * It then verifies that the "Add plant" message is not displayed, and asserts that the
     * name of the test plant is visible on the screen, confirming that the garden correctly
     * populates with provided data.
     */
    @Test
    fun garden_notEmptyGarden() {
        startGarden(gardenPlantings = listOf(testPlantAndGardenPlanting))
        composeTestRule.onNodeWithText(text = "Add plant").assertDoesNotExist()
        composeTestRule.onNodeWithText(text = testPlantAndGardenPlanting.plant.name)
            .assertIsDisplayed()
    }

    /**
     * A helper function to set the content of the Compose test rule to the [GardenScreen] composable.
     *
     * This function simplifies the setup for each test by encapsulating the boilerplate code
     * required to display the [GardenScreen] with a specific list of garden plantings.
     *
     * @param gardenPlantings A list of [PlantAndGardenPlantings] to be displayed in the garden.
     * This can be an empty list to test the empty garden state, or a list containing plants
     * to test the populated garden state.
     */
    private fun startGarden(gardenPlantings: List<PlantAndGardenPlantings>) {
        composeTestRule.setContent {
            GardenScreen(gardenPlants = gardenPlantings)
        }
    }
}