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

package com.google.samples.apps.nowinandroid.foryou

import android.graphics.Rect
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren
import com.google.samples.apps.nowinandroid.flingElementDownUp
import com.google.samples.apps.nowinandroid.waitAndFindObject
import com.google.samples.apps.nowinandroid.waitForObjectOnTopAppBar
import org.junit.Assert.fail

/**
 * Waits for content to be loaded in the For You screen.
 * It waits for the "loadingWheel" to disappear and then for the "forYou:topicSelection" to appear
 * saving a reference to the [UiObject2] in variable `obj`. Finally it waits for `obj` to have
 * children. This is important because sometimes the loading wheel disappears before the content
 * is fully loaded.
 */
fun MacrobenchmarkScope.forYouWaitForContent() {
    // Wait until content is loaded by checking if topics are loaded
    device.wait(Until.gone(By.res("loadingWheel")), 5_000)
    // Sometimes, the loading wheel is gone, but the content is not loaded yet
    // So we'll wait here for topics to be sure
    val obj: UiObject2 = device.waitAndFindObject(By.res("forYou:topicSelection"), 10_000)
    // Timeout here is quite big, because sometimes data loading takes a long time!
    obj.wait(untilHasChildren(), 60_000)
}

/**
 * Selects some topics, which will show the feed content for them.
 *
 * We start by initializing our [UiObject2] variable `topics` using the [UiDevice.findObject] method
 * of our [UiDevice] to the first object whose resource name matches the [String]
 * "forYou:topicSelection". We initialize our [Int] variable `horizontalMargin` to `10` times the
 * [Rect.width] of the [Rect] returned by the  [UiObject2.getVisibleBounds] method of `topic` the
 * quantity divided by `100`. We call the [UiObject2.setGestureMargins] method of `topics` to set
 * its gesture margins to `left`: `horizontalMargin`, `top`: `0`, `right`: `horizontalMargin`, and
 * `bottom`: `0`. We initialize our [Int] variables `var index` and `var visited` to `0`, then loop
 * while `visited` is less then `3`:
 *  - if the [UiObject2.getChildCount] of `topics` is equal to `0` we call [fail] with the [String]
 *  ("No topics found, can't generate profile for ForYou page.")
 *  - we initialize our [UiObject2] variable `topic` to the [UiObject2.getChildCount] at index
 *  [Int] variable `index` modulo the [UiObject2.getChildCount].
 *  - we initialize our [UiObject2] variable `topicCheckIcon` by using the [UiObject2.findObject]
 *  method of `topic` to find a [UiObject2] that is "checkable".
 *  - if `topicCheckIcon` is equal to `null` we increment `index` and loop around for the next
 *  child in `topics` (icon may not be visible if it's outside of the screen boundaries)
 *  - We then use a `when` statement to branch on:
 *  - if the `topicCheckIcon` is not "checked" we click on `topic` and call [UiDevice.waitForIdle]
 *  to wait for the [UiDevice] to be idle
 *  - if `recheckTopicsIfChecked` is `true` we loop twice clicking on `topic` then calling
 *  [UiDevice.waitForIdle] (topic was checked already and we want to recheck it, so just do it twice)
 *  - `else` the Topic is checked, but we don't recheck it so we do nothing.
 *
 * We then increment both `index` and `visited` and loop around for the next `topic`.
 *
 * @param recheckTopicsIfChecked if `true` and the Topic chosen to be checked is already checked,
 * check it again anyway.
 */
fun MacrobenchmarkScope.forYouSelectTopics(recheckTopicsIfChecked: Boolean = false) {
    val topics: UiObject2 = device.findObject(By.res("forYou:topicSelection"))

    // Set gesture margin from sides not to trigger system gesture navigation
    val horizontalMargin: Int = 10 * topics.visibleBounds.width() / 100
    topics.setGestureMargins(horizontalMargin, 0, horizontalMargin, 0)

    // Select some topics to show some feed content
    var index = 0
    var visited = 0

    while (visited < 3) {
        if (topics.childCount == 0) {
            fail("No topics found, can't generate profile for ForYou page.")
        }
        // Selecting some topics, which will populate items in the feed.
        val topic: UiObject2 = topics.children[index % topics.childCount]
        // Find the checkable element to figure out whether it's checked or not
        val topicCheckIcon: UiObject2? = topic.findObject(By.checkable(true))
        // Topic icon may not be visible if it's out of the screen boundaries
        // If that's the case, let's try another index
        if (topicCheckIcon == null) {
            index++
            continue
        }

        when {
            // Topic wasn't checked, so just do that
            !topicCheckIcon.isChecked -> {
                topic.click()
                device.waitForIdle()
            }

            // Topic was checked already and we want to recheck it, so just do it twice
            recheckTopicsIfChecked -> {
                repeat(2) {
                    topic.click()
                    device.waitForIdle()
                }
            }

            else -> {
                // Topic is checked, but we don't recheck it
            }
        }

        index++
        visited++
    }
}

/**
 * Scrolls the "For You" feed down and up to measure scrolling performance. We initialize our
 * [UiObject2] variable `feedList` to the [UiObject2] that the [UiDevice.findObject] method returns
 * that matches the resource name criteria of the [String] "forYou:feed". Then we call the
 * [UiDevice.flingElementDownUp] method of our [UiDevice] to fling the [UiObject2] variable
 * `feedList` down and up.
 */
fun MacrobenchmarkScope.forYouScrollFeedDownUp() {
    val feedList: UiObject2 = device.findObject(By.res("forYou:feed"))
    device.flingElementDownUp(element = feedList)
}

/**
 * Changes the theme of the app to dark theme if [isDark] is `true` or light theme if [isDark] is
 * `false`. We use a `when` statement to branch on the value of [isDark];
 *  - if `true`, we search our [UiDevice] for a [UiObject2] with the text "Dark" and click on it.
 *  - if `false`, we search our [UiDevice] for a [UiObject2] with the text "Light" and click on it.
 *
 * Then we wait for the [UiDevice] to be idle then search for a [UiObject2] with the text "OK" and
 * click on it. Finally, we wait for the top app bar to be visible on screen displaying the text
 * "Now in Android".
 */
fun MacrobenchmarkScope.setAppTheme(isDark: Boolean) {
    when (isDark) {
        true -> device.findObject(By.text("Dark")).click()
        false -> device.findObject(By.text("Light")).click()
    }
    device.waitForIdle()
    device.findObject(By.text("OK")).click()

    // Wait until the top app bar is visible on screen
    waitForObjectOnTopAppBar(By.text("Now in Android"))
}
