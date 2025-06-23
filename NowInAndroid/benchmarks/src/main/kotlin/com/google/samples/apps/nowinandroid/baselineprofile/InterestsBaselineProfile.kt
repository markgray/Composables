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
import com.google.samples.apps.nowinandroid.interests.goToInterestsScreen
import com.google.samples.apps.nowinandroid.interests.interestsScrollTopicsDownUp
import com.google.samples.apps.nowinandroid.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile of the "Interests" screen
 */
class InterestsBaselineProfile {
    /**
     * This rule provides a way to interact with the app under test and write a
     * baseline profile to a file. It also comes with running the baseline profile generation
     * through a [CompilationMode][androidx.benchmark.macro.CompilationMode] that is optimized
     * for generating profiles.
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
     * Generates a baseline profile for the "Interests" screen.
     * This test will:
     *  1. Start the app and allow notifications.
     *  2. Navigate to the "Interests" screen.
     *  3. Scroll down and up through the topics on the "Interests" screen.
     */
    @Test
    fun generate(): Unit =
        baselineProfileRule.collect(PACKAGE_NAME) {
            startActivityAndAllowNotifications()

            // Navigate to interests screen
            goToInterestsScreen()
            interestsScrollTopicsDownUp()
        }
}
