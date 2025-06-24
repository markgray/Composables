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

package com.google.samples.apps.nowinandroid.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.google.samples.apps.nowinandroid.PACKAGE_NAME
import com.google.samples.apps.nowinandroid.foryou.forYouScrollFeedDownUp
import com.google.samples.apps.nowinandroid.foryou.forYouSelectTopics
import com.google.samples.apps.nowinandroid.foryou.forYouWaitForContent
import com.google.samples.apps.nowinandroid.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/**
 * Generates a baseline profile of the "For You" screen.
 */
class ForYouBaselineProfile {
    /**
     * This rule provides a way to interact with the app under test and write a
     * baseline profile to a file. It also allows you to run the baseline profile generation
     * using 3 different [CompilationMode][androidx.benchmark.macro.CompilationMode] that are
     * optimized for generating profiles.
     *
     * So, this rule will do the following:
     *  1. Kill the app under test.
     *  2. Clear profile data to ensure clean state.
     *  3. Terminate the app under test.
     *  4. Build the baseline profile.
     *  5. Apply the baseline profile.
     *  6. Verify the baseline profile.
     */
    @get:Rule
    val baselineProfileRule: BaselineProfileRule = BaselineProfileRule()

    /**
     * Generates a baseline profile for the "For You" screen.
     * This function collects baseline profile data by simulating user interactions on the
     * "For You" screen.
     * It includes the following steps:
     *  1. Starts the activity and allows notifications.
     *  2. Waits for content to load on the "For You" screen.
     *  3. Selects topics on the "For You" screen.
     *  4. Scrolls the feed down and up on the "For You" screen.
     */
    @Test
    fun generate(): Unit =
        baselineProfileRule.collect(PACKAGE_NAME) {
            startActivityAndAllowNotifications()

            // Scroll the feed critical user journey
            forYouWaitForContent()
            forYouSelectTopics(true)
            forYouScrollFeedDownUp()
        }
}
