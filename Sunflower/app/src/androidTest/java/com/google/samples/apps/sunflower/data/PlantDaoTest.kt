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

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device to test the [PlantDao].
 *
 * This class uses an in-memory database and the [InstantTaskExecutorRule] to ensure that
 * Room database operations and LiveData updates are executed synchronously and on the same thread,
 * simplifying testing.
 *
 * The @[RunWith] annotation causes JUnit to invoke the [AndroidJUnit4] class to run the tests in
 * this class instead of the runner built into JUnit.
 */
@RunWith(AndroidJUnit4::class)
class PlantDaoTest {
    /**
     * The Room database for this test.
     */
    private lateinit var database: AppDatabase

    /**
     * The Data Access Object for the [Plant] class.
     */
    private lateinit var plantDao: PlantDao

    /**
     * A [Plant] object used for testing purposes.
     * It has a `plantId` of "1", a `name` of "A", and a `growZoneNumber` of 1.
     */
    private val plantA = Plant(
        plantId = "1",
        name = "A",
        description = "",
        growZoneNumber = 1,
        wateringInterval = 1,
        imageUrl = ""
    )

    /**
     * A [Plant] object used for testing purposes.
     * It has a `plantId` of "2", a `name` of "B", and a `growZoneNumber` of 1.
     */
    private val plantB = Plant(
        plantId = "2",
        name = "B",
        description = "",
        growZoneNumber = 1,
        wateringInterval = 1,
        imageUrl = ""
    )

    /**
     * A [Plant] object used for testing purposes.
     * It has a `plantId` of "3", a `name` of "C", and a `growZoneNumber` of 2.
     */
    private val plantC = Plant(
        plantId = "3",
        name = "C",
        description = "",
        growZoneNumber = 2,
        wateringInterval = 2,
        imageUrl = ""
    )

    /**
     * A JUnit rule that swaps the background executor used by the Architecture Components with a
     * different one which executes each task synchronously. This is necessary for testing LiveData
     * and other components that rely on asynchronous operations.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Creates and initializes an in-memory database and the [PlantDao] for testing.
     *
     * This method is annotated with @[Before], so it's executed before each test.
     * It sets up an in-memory version of the [AppDatabase] to ensure that tests are isolated
     * and don't affect the actual application database. It also pre-populates the database
     * with a set of test [Plant] objects.
     */
    @Before
    fun createDb(): Unit = runBlocking {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        database =
            Room.inMemoryDatabaseBuilder(context = context, klass = AppDatabase::class.java).build()
        plantDao = database.plantDao()

        // Insert plants in non-alphabetical order to test that results are sorted by name
        plantDao.upsertAll(plants = listOf(plantB, plantC, plantA))
    }

    /**
     * Closes the database @[After] each @[Test].
     * This is an important step to release the resources used by the in-memory database
     * and to ensure that each test runs in a clean state.
     */
    @After
    fun closeDb() {
        database.close()
    }

    /**
     * Tests that [PlantDao.getPlants] retrieves all [Plant] entities from the database and
     * that they are sorted alphabetically by name.
     *
     * This test function uses `runBlocking` to execute the coroutine-based DAO method
     * synchronously. It first retrieves the list of plants as a `Flow` and takes the
     * first emission. It then asserts that the list contains all three pre-populated plants
     * and verifies that they are in the correct alphabetical order (A, B, C),
     * confirming the sorting logic of the query.
     */
    @Test
    fun testGetPlants(): Unit = runBlocking {
        val plantList: List<Plant> = plantDao.getPlants().first()
        assertThat(plantList.size, equalTo(3))

        // Ensure plant list is sorted by name
        assertThat(plantList[0], equalTo(plantA))
        assertThat(plantList[1], equalTo(plantB))
        assertThat(plantList[2], equalTo(plantC))
    }

    /**
     * Tests that [PlantDao.getPlantsWithGrowZoneNumber] returns the correct list of plants
     * for a given grow zone number.
     *
     * This test verifies three scenarios:
     *  1. A grow zone number that matches multiple plants (`growZoneNumber` = 1)
     *  returns all matching plants.
     *  2. A grow zone number that matches a single plant (`growZoneNumber` = 2)
     *  returns a list with that one plant.
     *  3. A grow zone number that does not match any plants (`growZoneNumber` = 3)
     *  returns an empty list.
     *
     * It also confirms that the returned list of plants is correctly sorted by name in ascending order.
     */
    @Test
    fun testGetPlantsWithGrowZoneNumber(): Unit = runBlocking {
        val plantList = plantDao.getPlantsWithGrowZoneNumber(growZoneNumber = 1).first()
        assertThat(
            plantList.size,
            equalTo(2)
        )
        assertThat(
            plantDao.getPlantsWithGrowZoneNumber(growZoneNumber = 2).first().size,
            equalTo(1)
        )
        assertThat(
            plantDao.getPlantsWithGrowZoneNumber(growZoneNumber = 3).first().size,
            equalTo(0)
        )

        // Ensure plant list is sorted by name
        assertThat(plantList[0], equalTo(plantA))
        assertThat(plantList[1], equalTo(plantB))
    }

    /**
     * Tests that [PlantDao.getPlant] retrieves the correct [Plant] from the database
     * based on its `plantId`.
     *
     * This test function calls `getPlant` with the ID of a known plant (`plantA`) and asserts
     * that the returned `Flow` emits the correct `Plant` object. It uses `runBlocking` to
     * synchronously execute the test logic and `first()` to get the initial value from the `Flow`.
     */
    @Test
    fun testGetPlant(): Unit = runBlocking {
        assertThat(
            plantDao.getPlant(plantId = plantA.plantId).first(),
            equalTo(plantA)
        )
    }
}
