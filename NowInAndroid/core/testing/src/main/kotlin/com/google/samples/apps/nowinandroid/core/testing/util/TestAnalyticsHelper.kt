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

package com.google.samples.apps.nowinandroid.core.testing.util

import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper

/**
 * An test implementation of [AnalyticsHelper] that records events in a list, makes them available
 * for inspection and assertions.
 */
class TestAnalyticsHelper : AnalyticsHelper {

    /**
     * The list of [AnalyticsEvent] recorded by this helper.
     */
    private val events: MutableList<AnalyticsEvent> = mutableListOf()

    /**
     * Records an [AnalyticsEvent] in our [MutableList] of [AnalyticsEvent] property [events].
     *
     * @param event the [AnalyticsEvent] to record.
     */
    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    /**
     * Asserts that a particular [AnalyticsEvent] has been logged.
     *
     * @param event The [AnalyticsEvent] to search for.
     * @return `true` if the event was logged, `false` otherwise.
     */
    fun hasLogged(event: AnalyticsEvent): Boolean = event in events
}
