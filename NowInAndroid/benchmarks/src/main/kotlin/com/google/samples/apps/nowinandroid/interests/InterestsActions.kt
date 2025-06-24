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

package com.google.samples.apps.nowinandroid.interests

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.google.samples.apps.nowinandroid.flingElementDownUp
import com.google.samples.apps.nowinandroid.waitForObjectOnTopAppBar

/**
 * Navigates to the Interests screen and waits for it to load.
 *
 * First we use the [UiDevice.findObject] method to find the [UiObject2] with the text "Interests"
 * and click on it, we wait for the [UiDevice] to be idle then we wait for the top app bar to be
 * visible on screen displaying the text "Interests". Finally we wait for the "loadingWheel" to
 * disappear.
 */
fun MacrobenchmarkScope.goToInterestsScreen() {
    device.findObject(By.text("Interests")).click()
    device.waitForIdle()
    // Wait until interests are shown on screen
    waitForObjectOnTopAppBar(By.text("Interests"))

    // Wait until content is loaded by checking if interests are loaded
    device.wait(Until.gone(By.res("loadingWheel")), 5_000)
}

/**
 * Scrolls the interests topics list down and up.
 *
 * We start by using the [UiDevice.wait] method to wait for the object with the resource name
 * "interests:topics" to appear on screen. Then we initialize our [UiObject2] variable `topicsList`
 * to the [UiObject2] that the [UiDevice.findObject] method returns that matches the resource
 * name criteria of the [String] "interests:topics". Finally we call the [UiDevice.flingElementDownUp]
 * method of our [UiDevice] to fling the [UiObject2] variable `topicsList` down and up.
 */
fun MacrobenchmarkScope.interestsScrollTopicsDownUp() {
    device.wait(Until.hasObject(By.res("interests:topics")), 5_000)
    val topicsList = device.findObject(By.res("interests:topics"))
    device.flingElementDownUp(topicsList)
}

/**
 * Waits for the topics to be displayed on screen.
 *
 * We wait for the [UiDevice] to find the [UiObject2] that contains the text "Accessibility", waiting
 * for at most 30 seconds.
 */
fun MacrobenchmarkScope.interestsWaitForTopics() {
    device.wait(Until.hasObject(By.text("Accessibility")), 30_000)
}

/**
 * Finds the first topic item that is checkable and clicks it.
 *
 * We initialize our [UiObject2] variable `topicsList` to the [UiObject2] that the [UiDevice.findObject]
 * method finds with the resource id "interests:topics" and then we initialize our [UiObject2]
 * variable `checkable` to the [UiObject2] that the [UiObject2.findObject] method finds that is a
 * checkable item in `topicsList`. We then click on the `checkable` item and wait for the [UiDevice]
 * to be idle.
 */
fun MacrobenchmarkScope.interestsToggleBookmarked() {
    val topicsList = device.findObject(By.res("interests:topics"))
    val checkable = topicsList.findObject(By.checkable(true))
    checkable.click()
    device.waitForIdle()
}
