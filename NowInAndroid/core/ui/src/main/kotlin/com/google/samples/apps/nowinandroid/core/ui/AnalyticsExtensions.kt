/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectScope
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent.Param
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent.ParamKeys
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent.Types
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.analytics.LocalAnalyticsHelper

/**
 * Logs a screen view event.
 *
 * @param screenName The name of the screen.
 */
fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        event = AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(key = ParamKeys.SCREEN_NAME, value = screenName),
            ),
        ),
    )
}

/**
 * Logs a news resource opened event.
 *
 * @param newsResourceId The ID of the news resource that was opened.
 */
fun AnalyticsHelper.logNewsResourceOpened(newsResourceId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "news_resource_opened",
            extras = listOf(
                Param(key = "opened_news_resource", value = newsResourceId),
            ),
        ),
    )
}

/**
 * A side-effect which records a screen view event. We launch a [DisposableEffect] keyed on [Unit]
 * and in its [DisposableEffectScope] `effect` block we call the [AnalyticsHelper.logScreenView]
 * method of our [AnalyticsHelper] parameter [analyticsHelper] with its `screenName` argument
 * ouf [String] parameter [screenName], then call [onDispose] with an empty lambda as its
 * `onDisposeEffect` lambda argument.
 *
 * @param screenName The name of the screen to record.
 * @param analyticsHelper The analytics helper to use to log the event.
 */
@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
): Unit = DisposableEffect(key1 = Unit) {
    analyticsHelper.logScreenView(screenName = screenName)
    onDispose {}
}
