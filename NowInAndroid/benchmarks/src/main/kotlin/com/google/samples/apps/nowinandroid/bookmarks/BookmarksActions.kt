/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.bookmarks

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import com.google.samples.apps.nowinandroid.waitForObjectOnTopAppBar

/**
 * Navigates to the "Saved" screen (Bookmarks) from the bottom navigation bar.
 * It waits until the screen is idle and the "Saved" title is visible in the top app bar.
 *
 * We start by initializing our [BySelector] variable `savedSelector` to a new [BySelector] setting
 * its text value criteria to the [String] "Saved". We initialize our [UiObject2] variable `savedButton`
 * to the first object to match the [BySelector] variable `savedSelector` selector criteria using the
 * [UiDevice.findObject] method (or null if no matching objects are found). Next, we click on the
 * [UiObject2] variable `savedButton` using the [UiObject2.click] method, and then wait for the
 * device to be idle using the [UiDevice.waitForIdle] method. Finally, we call the
 * [waitForObjectOnTopAppBar] method with the [BySelector] variable `savedSelector` to wait until
 * the "Saved" title is visible in the top app bar.
 *
 */
fun MacrobenchmarkScope.goToBookmarksScreen() {
    val savedSelector: BySelector = By.text("Saved")
    val savedButton: UiObject2 = device.findObject(savedSelector)
    savedButton.click()
    device.waitForIdle()
    // Wait until saved title are shown on screen
    waitForObjectOnTopAppBar(savedSelector)
}
