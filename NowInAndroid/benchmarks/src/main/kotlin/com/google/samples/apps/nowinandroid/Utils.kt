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

package com.google.samples.apps.nowinandroid

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.google.samples.apps.nowinandroid.benchmarks.BuildConfig
import java.io.ByteArrayOutputStream

/**
 * Convenience parameter to use proper package name with regards to build type and build flavor.
 *
 * We use the new instance of [StringBuilder] that [buildString] creates to call its
 * [StringBuilder.append] method to add the [String] "com.google.samples.apps.nowinandroid",
 * and the [String] property [BuildConfig.APP_FLAVOR_SUFFIX] to the [String] that is returned
 * by [buildString].
 */
val PACKAGE_NAME: String = buildString {
    append("com.google.samples.apps.nowinandroid")
    append(BuildConfig.APP_FLAVOR_SUFFIX)
}

/**
 * Extension function on [UiDevice] that flings the given [element] down and then up.
 *
 * This is useful for testing scrolling behavior and ensuring that content is properly recycled.
 *
 * First we call the [UiObject2.setGestureMargin] method of [UiObject2] parameter [element] to set
 * the margin of the gesture to the width of the screen divided by 5. This prevents the gesture from
 * triggering system navigation. Then we call its [UiObject2.fling] method with its `direction`
 * argument [Direction.DOWN] to fling the [UiObject2] down, the we call the [UiDevice.waitForIdle]
 * method to wait for the device to be idle, and finally we call the [UiObject2.fling] method with
 * its `direction` argument [Direction.UP] to fling the [UiObject2] up.
 *
 * @param element The [UiObject2] to fling.
 */
fun UiDevice.flingElementDownUp(element: UiObject2) {
    // Set some margin from the sides to prevent triggering system navigation
    element.setGestureMargin(displayWidth / 5)

    element.fling(Direction.DOWN)
    waitForIdle()
    element.fling(Direction.UP)
}

/**
 * Waits until an object with [selector] if visible on screen and returns the object.
 * If the element is not available in [timeout], throws [AssertionError]
 *
 * @param selector The [BySelector] to find the [UiObject2]
 * @param timeout The timeout in milliseconds to wait for the [UiObject2] to be visible on screen.
 * @return The [UiObject2] that was found.
 */
fun UiDevice.waitAndFindObject(selector: BySelector, timeout: Long): UiObject2 {
    if (!wait(Until.hasObject(selector), timeout)) {
        throw AssertionError("Element not found on screen in ${timeout}ms (selector=$selector)")
    }

    return findObject(selector)
}

/**
 * Helper to dump window hierarchy into a string.
 */
@Suppress("unused")
fun UiDevice.dumpWindowHierarchy(): String {
    val buffer = ByteArrayOutputStream()
    dumpWindowHierarchy(buffer)
    return buffer.toString()
}
