/*
 * Copyright 2023 Google LLC
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

package com.google.samples.apps.sunflower.test

import org.hamcrest.Description
import org.hamcrest.Factory
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

/**
 * Custom Hamcrest matcher to assert that two [Calendar] objects represent the same date.
 *
 * This matcher only compares the year, month, and day-of-month fields, ignoring time-of-day
 * components. This is useful for testing date-based logic, such as in `GardenPlanting` entries.
 *
 * @property expected The expected [Calendar] date to match against.
 */
internal class CalendarMatcher(
    private val expected: Calendar
) : TypeSafeDiagnosingMatcher<Calendar>() {
    /**
     * Formatter used to display dates in a consistent format (dd.MM.yyyy) for mismatch descriptions.
     */
    private val formatter = SimpleDateFormat("dd.MM.yyyy")

    /**
     * Appends a description of the expected date to the provided [Description] object.
     *
     * This method is part of the Hamcrest matching framework. It's used to generate
     * a clear and readable description of what the matcher was expecting when an
     * assertion fails. The date is formatted as "dd.MM.yyyy".
     *
     * @param description The [Description] to which the text is appended.
     */
    override fun describeTo(description: Description?) {
        description?.appendText(formatter.format(expected.time))
    }

    /**
     * Checks if the [Calendar] parameter [actual] object represents the same date as the [expected]
     * property.
     *
     * This method is the core logic of the matcher. It performs the following checks:
     *  1. Handles the case where the [actual] calendar is `null`.
     *  2. Compares the [YEAR], [MONTH], and [DAY_OF_MONTH] fields of the [actual] and [expected]
     *  calendars.
     *  3. If the dates do not match, it appends a formatted description of the `actual` date
     *  to the `mismatchDescription` for clear test failure reporting.
     *
     * @param actual The [Calendar] object to be evaluated. Can be `null`.
     * @param mismatchDescription A [Description] object to which a description of the mismatch
     * is appended.
     * @return `true` if the year, month, and day of the `actual` calendar match the `expected`
     * calendar; `false` otherwise.
     */
    override fun matchesSafely(actual: Calendar?, mismatchDescription: Description?): Boolean {
        if (actual == null) {
            mismatchDescription?.appendText("was null")
            return false
        }
        if (actual.get(YEAR) == expected.get(YEAR) &&
            actual.get(MONTH) == expected.get(MONTH) &&
            actual.get(DAY_OF_MONTH) == expected.get(DAY_OF_MONTH)
        ) return true

        mismatchDescription?.appendText("was ")
            ?.appendText(formatter.format(actual.time))
        return false
    }

    companion object {
        /**
         * Creates a matcher for [Calendar]s that only matches when year, month and day of
         * actual calendar are equal to year, month and day of expected calendar.
         *
         * For example:
         * <code>assertThat(someDate, hasSameDateWith(Calendar.getInstance()))</code>
         *
         * @param expected calendar that has expected year, month and day [Calendar]
         */
        @Factory
        fun equalTo(expected: Calendar): Matcher<Calendar> = CalendarMatcher(expected)
    }
}
