/*
 * Copyright 2022 Google LLC
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

package com.google.samples.apps.sunflower.macrobenchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a baseline profile for the Sunflower app.
 *
 * This class triggers a baseline profile generation by executing a predefined user journey.
 * The generated profile is used to improve app startup and runtime performance.
 *
 * The user journey simulated is:
 *  1. Start the app and wait for it to load.
 *  2. Navigate to the "Plant list" screen.
 *  3. Select the first plant in the list to open its detail screen.
 *
 * This journey is defined in the [startPlantListPlantDetail] test method.
 *
 * To generate the profile, run this test on a rooted, userdebug, or eng build of Android P (API 28)
 * or higher.
 *
 * Example command:
 * `./gradlew :app:generateBaselineProfile`
 */
@RunWith(value = AndroidJUnit4::class)
class BaselineProfileGenerator {

    /**
     * The [BaselineProfileRule] is a JUnit rule that generates a baseline profile for the app.
     *
     * This rule is responsible for:
     *  - Starting and stopping the app.
     *  - Collecting performance data during the user journey defined in the test method.
     *  - Generating the baseline profile file (`baseline-prof.txt`) in the `app/src/main/` directory.
     *
     * The @get:[Rule] annotation is necessary for JUnit to recognize this as a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     * The @[RequiresApi] annotation indicates that this rule and the associated tests
     * require Android P (API 28) or higher to run, as baseline profiles are only supported
     * on these versions.
     */
    @RequiresApi(value = Build.VERSION_CODES.P)
    @get:Rule
    val rule: BaselineProfileRule = BaselineProfileRule()

    /**
     * Simulates a user journey through the app to generate a baseline profile.
     * The journey starts from the home screen, navigates to the plant list, and then
     * opens the detail screen for the first plant in the list. This flow is critical
     * for capturing performance metrics related to app startup and core navigation paths.
     */
    @RequiresApi(value = Build.VERSION_CODES.P)
    @Test
    fun startPlantListPlantDetail() {
        rule.collect(PACKAGE_NAME) {
            // start the app flow
            pressHome()
            startActivityAndWait()

            // go to plant list flow
            val plantListTab: UiObject2 =
                device.findObject(By.descContains("Plant list"))
            plantListTab.click()
            device.waitForIdle()
            // sleep for animations to settle
            Thread.sleep(500)

            // go to plant detail flow
            val plantList: UiObject2 =
                device.findObject(
                    By.res(
                        packageName,
                        "plant_list"
                    )
                )
            val listItem: UiObject2 = plantList.children[0]
            listItem.click()
            device.wait(
                Until.gone(By.res(packageName, "plant_list")),
                5_000
            )
        }
    }
}
