/*
 * Copyright 2018 Google LLC
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

package com.google.samples.apps.sunflower.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.SEPTEMBER
import java.util.Calendar.YEAR

/**
 * Unit tests for the [Converters] class. This class tests the conversion between
 * [Calendar] objects and [Long] timestamps to ensure data integrity when storing
 * and retrieving dates from the database.
 */
internal class ConvertersTest {

    /**
     * [Calendar] object used for testing the [Converters] class.
     *
     * This calendar is set to September 4, 1998, which is a significant date for Google.
     * It is used as a consistent reference point for a known date to verify that the
     * `calendarToDatestamp` and `datestampToCalendar` conversion functions work correctly.
     */
    private val cal = Calendar.getInstance().apply {
        set(YEAR, 1998)
        set(MONTH, SEPTEMBER)
        set(DAY_OF_MONTH, 4)
    }

    /**
     * Test for [Converters.calendarToDatestamp].
     */
    @Test
    fun calendarToDatestamp() {
        assertEquals(
            cal.timeInMillis,
            Converters().calendarToDatestamp(cal)
        )
    }

    /**
     * Test for [Converters.datestampToCalendar].
     */
    @Test
    fun datestampToCalendar() {
        assertEquals(
            Converters().datestampToCalendar(cal.timeInMillis),
            cal
        )
    }
}
