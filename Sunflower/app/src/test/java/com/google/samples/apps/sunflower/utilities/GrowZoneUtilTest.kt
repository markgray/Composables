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

package com.google.samples.apps.sunflower.utilities

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the [getZoneForLatitude] function.
 */
internal class GrowZoneUtilTest {

    /**
     * Test for [getZoneForLatitude] with various latitude values.
     * This test covers positive latitudes from the equator (0.0) up to the pole (90.0),
     * checking that the correct grow zones are returned at the boundaries.
     */
    @Test
    fun getZoneForLatitude() {
        assertEquals(13, getZoneForLatitude(0.0))
        assertEquals(13, getZoneForLatitude(7.0))
        assertEquals(12, getZoneForLatitude(7.1))
        assertEquals(1, getZoneForLatitude(84.1))
        assertEquals(1, getZoneForLatitude(90.0))
    }

    /**
     * Test for [getZoneForLatitude] with negative latitude values.
     * This test covers negative latitudes from near the equator (-7.0) down to the South Pole (-90.0),
     * checking that the correct grow zones are returned at the boundaries. The zones should be
     * symmetrical to their positive latitude counterparts.
     */
    @Test
    fun getZoneForLatitude_negativeLatitudes() {
        assertEquals(13, getZoneForLatitude(-7.0))
        assertEquals(12, getZoneForLatitude(-7.1))
        assertEquals(1, getZoneForLatitude(-84.1))
        assertEquals(1, getZoneForLatitude(-90.0))
    }

    /**
     * Bugfix test for [Issue 8](https://github.com/android/sunflower/issues/8) to ensure
     * that the grow zone calculation is correct for specific latitude values that were
     * previously reported as incorrect. This test verifies the fix by checking a set of
     * known latitude-to-zone mappings.
     *
     * @see getZoneForLatitude
     */
    @Test
    fun getZoneForLatitude_GitHub_issue8() {
        assertEquals(9, getZoneForLatitude(35.0))
        assertEquals(8, getZoneForLatitude(42.0))
        assertEquals(7, getZoneForLatitude(49.0))
        assertEquals(6, getZoneForLatitude(56.0))
        assertEquals(5, getZoneForLatitude(63.0))
        assertEquals(4, getZoneForLatitude(70.0))
    }
}
