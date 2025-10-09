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

@file:Suppress("RedundantValueArgument")

package com.google.samples.apps.sunflower.compose.plantdetail

import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.test.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A test class for the [PlantDetails] composable.
 *
 * This class contains UI tests for the Plant Details screen, covering various states
 * such as whether a plant is planted or not, and whether the gallery is displayed
 * based on the Unsplash API key availability.
 *
 * The @[RunWith] annotation causes JUnit to invoke the [AndroidJUnit4] class to run the tests in
 * this class instead of the runner built into JUnit.
 */
@RunWith(AndroidJUnit4::class)
class PlantDetailComposeTest {

    /**
     * Rule for composing and testing UI components.
     * This rule provides a way to launch and interact with Compose UI within a test.
     * It is used to set the content of the test screen and to find and interact with UI elements.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    /**
     * Tests the Plant Details screen when the plant is not in the garden.
     * It verifies that the plant's name ("Apple") is displayed and that the
     * "Add plant" button (identified by its content description) is visible,
     * indicating that the user has the option to add the plant to their garden.
     */
    @Test
    fun plantDetails_checkIsNotPlanted() {
        startPlantDetails(isPlanted = false)
        composeTestRule.onNodeWithText(text = "Apple").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = "Add plant").assertIsDisplayed()
    }

    /**
     * Tests the Plant Details screen when the plant is already in the garden.
     * It verifies that the plant's name ("Apple") is displayed and that the
     * "Add plant" button is not visible, as the plant has already been added.
     */
    @Test
    fun plantDetails_checkIsPlanted() {
        startPlantDetails(isPlanted = true)
        composeTestRule.onNodeWithText(text = "Apple").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = "Add plant").assertDoesNotExist()
    }

    /**
     * Tests that the gallery icon is not displayed on the Plant Details screen
     * when there is no Unsplash API key. This is verified by starting the details
     * screen with `hasUnsplashKey` set to `false` and asserting that the node
     * with the content description "Gallery Icon" does not exist.
     */
    @Test
    fun plantDetails_checkGalleryNotShown() {
        startPlantDetails(isPlanted = true, hasUnsplashKey = false)
        composeTestRule.onNodeWithContentDescription(label = "Gallery Icon").assertDoesNotExist()
    }

    /**
     * Tests that the gallery icon is displayed on the Plant Details screen
     * when a valid Unsplash API key is provided. This is verified by starting the
     * details screen with `hasUnsplashKey` set to `true` and asserting that the node
     * with the content description "Gallery Icon" is displayed.
     */
    @Test
    fun plantDetails_checkGalleryIsShown() {
        startPlantDetails(isPlanted = true, hasUnsplashKey = true)
        composeTestRule.onNodeWithContentDescription(label = "Gallery Icon").assertIsDisplayed()
    }

    /**
     * A helper function to set the content of the test screen to the [PlantDetails] composable.
     *
     * This function is used to initialize the UI for testing, allowing different states of the
     * `PlantDetails` screen to be displayed and verified. It configures the screen based on whether
     * the plant is in the user's garden and whether a valid Unsplash API key is available.
     *
     * @param isPlanted A boolean indicating whether the plant is already in the user's garden.
     * This controls the visibility of the "Add plant" button.
     * @param hasUnsplashKey A boolean indicating if a valid Unsplash API key is provided.
     * This determines whether the photo gallery is displayed. Defaults to `false`.
     */
    private fun startPlantDetails(isPlanted: Boolean, hasUnsplashKey: Boolean = false) {
        composeTestRule.setContent {
            PlantDetails(
                plant = plantForTesting(),
                isPlanted = isPlanted,
                callbacks = PlantDetailsCallbacks({ }, { }, { }, { }),
                hasValidUnsplashKey = hasUnsplashKey
            )
        }
    }
}

/**
 * A factory function that creates a [Plant] object for testing purposes.
 *
 * This function is used within the test suite to generate a consistent [Plant]
 * object, representing an "Apple" plant. The object is pre-populated with
 * static data, including its ID, name, description, and other attributes,
 * which simplifies the setup for UI tests that require a [Plant] instance.
 *
 * The `imageUrl` is dynamically resolved from a local raw resource to ensure
 * it's available during testing without network dependencies.
 *
 * @return A [Plant] object with predefined data for "Apple".
 */
@Composable
internal fun plantForTesting(): Plant {
    return Plant(
        plantId = "malus-pumila",
        name = "Apple",
        description = "An apple is a sweet, edible fruit produced by an apple tree (Malus pumila). Apple trees are cultivated worldwide, and are the most widely grown species in the genus Malus. The tree originated in Central Asia, where its wild ancestor, Malus sieversii, is still found today. Apples have been grown for thousands of years in Asia and Europe, and were brought to North America by European colonists. Apples have religious and mythological significance in many cultures, including Norse, Greek and European Christian traditions.<br><br>Apple trees are large if grown from seed. Generally apple cultivars are propagated by grafting onto rootstocks, which control the size of the resulting tree. There are more than 7,500 known cultivars of apples, resulting in a range of desired characteristics. Different cultivars are bred for various tastes and uses, including cooking, eating raw and cider production. Trees and fruit are prone to a number of fungal, bacterial and pest problems, which can be controlled by a number of organic and non-organic means. In 2010, the fruit's genome was sequenced as part of research on disease control and selective breeding in apple production.<br><br>Worldwide production of apples in 2014 was 84.6 million tonnes, with China accounting for 48% of the total.<br><br>(From <a href=\\\"https://en.wikipedia.org/wiki/Apple\\\">Wikipedia</a>)",
        growZoneNumber = 3,
        wateringInterval = 30,
        imageUrl = rawUri(id = R.raw.apple).toString()
    )
}

/**
 * A composable function that returns the [Uri] of a given raw resource.
 *
 * This function is used to generate a URI for a resource located in the `res/raw`
 * directory. It constructs the URI using the `android.resource` scheme, the current
 * application's package name, and the resource ID. This is particularly useful in
 * tests for providing a local image URI when network calls are not feasible.
 *
 * @param id The raw resource ID (e.g., `R.raw.my_resource`).
 * @return The [Uri] for the raw resource.
 */
@Composable
private fun rawUri(@RawRes id: Int): Uri {
    return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${LocalContext.current.packageName}/$id"
        .toUri()
}
