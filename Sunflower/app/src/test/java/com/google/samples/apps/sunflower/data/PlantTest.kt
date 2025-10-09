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

@file:Suppress("RedundantValueArgument")

package com.google.samples.apps.sunflower.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Calendar.DAY_OF_YEAR

/**
 * Unit tests for the [Plant] data class.
 */
internal class PlantTest {

    /**
     * The [Plant] object used for testing.
     * It is initialized in the [setUp] function before each test.
     */
    private lateinit var plant: Plant

    /**
     * This function is executed before each test. It initializes the [plant] property
     * with a new [Plant] object.
     */
    @Before
    fun setUp() {
        plant = Plant(
            plantId = "1",
            name = "Tomato",
            description = "A red vegetable",
            growZoneNumber = 1,
            wateringInterval = 2,
            imageUrl = ""
        )
    }

    /**
     * Tests that the [Plant] data class has the correct default values.
     * When a [Plant] is created without specifying a `wateringInterval` or `imageUrl`,
     * it should default to a watering interval of 7 days and an empty string for the image URL.
     */
    @Test
    fun test_default_values() {
        val defaultPlant = Plant(
            plantId = "2",
            name = "Apple",
            description = "Description",
            growZoneNumber = 1
        )
        assertEquals(
            7,
            defaultPlant.wateringInterval
        )
        assertEquals(
            "",
            defaultPlant.imageUrl
        )
    }

    /**
     * Tests the [Plant.shouldBeWatered] function.
     *
     * This test verifies the logic for determining if a plant needs to be watered based on its
     * `wateringInterval` and the `lastWateringDate`.
     *
     * The test scenarios include:
     *  - Watering today: The plant should not need watering.
     *  - Watering yesterday: The plant should not need watering (as the interval is 2 days).
     *  - Watering two days ago: The plant should not need watering (as the interval is 2 days,
     *  so it's due tomorrow).
     *  - Watering three days ago: The plant should need watering as it has exceeded the watering
     *  interval.
     */
    @Test
    fun test_shouldBeWatered() {
        Calendar.getInstance().let { now: Calendar ->
            // Generate lastWateringDate from being as copy of now.
            val lastWateringDate = Calendar.getInstance()

            // Test for lastWateringDate is today.
            lastWateringDate.time = now.time
            assertFalse(
                plant.shouldBeWatered(
                    since = now,
                    lastWateringDate = lastWateringDate.apply { add(DAY_OF_YEAR, -0) }
                )
            )

            // Test for lastWateringDate is yesterday.
            lastWateringDate.time = now.time
            assertFalse(
                plant.shouldBeWatered(
                    since = now,
                    lastWateringDate = lastWateringDate.apply { add(DAY_OF_YEAR, -1) }
                )
            )

            // Test for lastWateringDate is the day before yesterday.
            lastWateringDate.time = now.time
            assertFalse(
                plant.shouldBeWatered(
                    since = now,
                    lastWateringDate = lastWateringDate.apply { add(DAY_OF_YEAR, -2) }
                )
            )

            // Test for lastWateringDate is some days ago, three days ago, four days ago etc.
            lastWateringDate.time = now.time
            assertTrue(
                plant.shouldBeWatered(
                    since = now,
                    lastWateringDate = lastWateringDate.apply { add(DAY_OF_YEAR, -3) }
                )
            )
        }
    }

    /**
     * Tests that the [toString] method of the [Plant] data class returns the plant's name.
     * The [setUp] method creates a [Plant] with the name "Tomato" @[Before] this @[Test] is
     * run and this @[Test] just asserts that calling `toString()` on this object returns the
     * string "Tomato".
     */
    @Test
    fun test_toString() {
        assertEquals("Tomato", plant.toString())
    }
}
