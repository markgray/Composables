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

package com.google.samples.apps.nowinandroid.core.testing.util

import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.TimeZone

/**
 * A testable implementation of [TimeZoneMonitor].
 *
 * It allows consuming and emitting values to [currentTimeZone].
 */
class TestTimeZoneMonitor : TimeZoneMonitor {

    /**
     * The current time zone that should be used by the app. This is a [MutableStateFlow] that can
     * be updated by tests to simulate changes in the time zone.
     */
    private val timeZoneFlow: MutableStateFlow<TimeZone> = MutableStateFlow(value = defaultTimeZone)

    /**
     * Public read-only [Flow] of [TimeZone] that emits the [MutableStateFlow] of [TimeZone] property
     * [timeZoneFlow].
     */
    override val currentTimeZone: Flow<TimeZone> = timeZoneFlow

    /**
     * A test-only API to set the current time zone from tests.
     *
     * @param zoneId The desired [TimeZone].
     */
    fun setTimeZone(zoneId: TimeZone) {
        timeZoneFlow.value = zoneId
    }

    companion object {
        /**
         * The default time zone to use in tests. This is Europe/Warsaw because it is a fixed
         * offset time zone that is not affected by daylight saving time.
         */
        val defaultTimeZone: TimeZone = TimeZone.of(zoneId = "Europe/Warsaw")
    }
}
