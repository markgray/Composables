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

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.google.samples.apps.nowinandroid.PACKAGE_NAME
import com.google.samples.apps.nowinandroid.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile for app startup. This profile also enables using
 * [Dex Layout Optimizations](https://developer.android.com/topic/performance/baselineprofiles/dex-layout-optimizations)
 * via the `includeInStartupProfile` parameter.
 */
class StartupBaselineProfile {
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
     * Generates a baseline profile for the app startup.
     * The `includeInStartupProfile` parameter is set to `true` to also enable
     * [Dex Layout Optimizations](https://developer.android.com/topic/performance/baselineprofiles/dex-layout-optimizations).
     *
     * The `profileBlock` argument defines the actions to be performed when generating the profile.
     * In this case, it launches the default activity and allows notifications.
     */
    @Test
    fun generate(): Unit = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = true,
        profileBlock = MacrobenchmarkScope::startActivityAndAllowNotifications,
    )
}
