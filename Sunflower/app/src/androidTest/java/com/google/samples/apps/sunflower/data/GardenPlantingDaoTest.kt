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

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.platform.app.InstrumentationRegistry
import com.google.samples.apps.sunflower.utilities.testCalendar
import com.google.samples.apps.sunflower.utilities.testGardenPlanting
import com.google.samples.apps.sunflower.utilities.testPlant
import com.google.samples.apps.sunflower.utilities.testPlants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented tests for [GardenPlantingDao].
 */
class GardenPlantingDaoTest {
    /**
     * The in-memory database used for testing.
     */
    private lateinit var database: AppDatabase

    /**
     * The [GardenPlantingDao] under test.
     */
    private lateinit var gardenPlantingDao: GardenPlantingDao

    /**
     * The row Id os the [Plant] used for testing.
     */
    private var testGardenPlantingId: Long = 0

    /**
     * A JUnit rule that swaps the background executor used by the Architecture Components with a
     * different one which executes each task synchronously.
     *
     * This is necessary for testing LiveData, as it ensures that any background operations
     * are completed before the test continues.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Creates an in-memory database and initializes it with test data.
     * This method is executed before each test, ensuring a clean state.
     *
     * The @[Before] annotation annotates methods which must be executed before
     * every @[Test] annotated method.
     */
    @Before
    fun createDb(): Unit = runBlocking {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        database =
            Room.inMemoryDatabaseBuilder(context = context, klass = AppDatabase::class.java).build()
        gardenPlantingDao = database.gardenPlantingDao()

        database.plantDao().upsertAll(plants = testPlants)
        testGardenPlantingId =
            gardenPlantingDao.insertGardenPlanting(gardenPlanting = testGardenPlanting)
    }

    /**
     * Closes the in-memory database after each test.
     *
     * Annotating a method with @[After] causes that method to be run after every @[Test] annotated
     * method. All @[After] methods are guaranteed to run even if a @[Before] or @[Test] method
     * throws an exception. The @[After] methods declared in superclasses will be run after those of
     * the current class, unless they are overridden in the current class.
     */
    @After
    fun closeDb() {
        database.close()
    }

    /**
     * Tests that [GardenPlantingDao.getGardenPlantings] returns all garden plantings from the
     * database. It inserts a second [GardenPlanting] and then asserts that the size of the
     * returned list is 2, confirming that both the initial and the newly added plantings are
     * retrieved.
     */
    @Test
    fun testGetGardenPlantings(): Unit = runBlocking {
        val gardenPlanting2 = GardenPlanting(
            plantId = testPlants[1].plantId,
            plantDate = testCalendar,
            lastWateringDate = testCalendar
        ).also { planting: GardenPlanting -> planting.gardenPlantingId = 2 }
        gardenPlantingDao.insertGardenPlanting(gardenPlanting = gardenPlanting2)
        assertThat(gardenPlantingDao.getGardenPlantings().first().size, equalTo(2))
    }

    /**
     * Tests the deletion of a [GardenPlanting] from the database.
     * It first adds a second planting, asserts that the total count is 2,
     * then deletes the second planting and asserts that the total count is back to 1.
     */
    @Test
    fun testDeleteGardenPlanting(): Unit = runBlocking {
        val gardenPlanting2 = GardenPlanting(
            plantId = testPlants[1].plantId,
            plantDate = testCalendar,
            lastWateringDate = testCalendar
        ).also { planting: GardenPlanting -> planting.gardenPlantingId = 2 }
        gardenPlantingDao.insertGardenPlanting(gardenPlanting = gardenPlanting2)
        assertThat(gardenPlantingDao.getGardenPlantings().first().size, equalTo(2))
        gardenPlantingDao.deleteGardenPlanting(gardenPlanting = gardenPlanting2)
        assertThat(gardenPlantingDao.getGardenPlantings().first().size, equalTo(1))
    }

    /**
     * Tests that [GardenPlantingDao.isPlanted] returns `true` when a plant is in the garden.
     * It uses the `testPlant` which is known to be planted by the @[Before] setup method.
     * It then asserts that the result of `isPlanted` for this plant's ID is `true`.
     */
    @Test
    fun testGetGardenPlantingForPlant(): Unit = runBlocking {
        assertTrue(gardenPlantingDao.isPlanted(plantId = testPlant.plantId).first())
    }

    /**
     * Tests that [GardenPlantingDao.isPlanted] returns `false` for a plant that has not been planted.
     * It queries for a plant that was inserted into the `plants` table but not into the
     * `garden_plantings` table and asserts that the result is `false`.
     */
    @Test
    fun testGetGardenPlantingForPlant_notFound(): Unit = runBlocking {
        assertFalse(gardenPlantingDao.isPlanted(plantId = testPlants[2].plantId).first())
    }

    /**
     * Tests that [GardenPlantingDao.getPlantedGardens] returns a list of [PlantAndGardenPlantings],
     * which correctly links [Plant]s with their corresponding [GardenPlanting]s.
     * This test verifies that only plants which have been added to the garden are returned,
     * and that the returned object contains the correct plant and its associated garden planting details.
     */
    @Test
    fun testGetPlantAndGardenPlantings(): Unit = runBlocking {
        val plantAndGardenPlantings: List<PlantAndGardenPlantings> =
            gardenPlantingDao.getPlantedGardens().first()
        assertThat(plantAndGardenPlantings.size, equalTo(1))

        /**
         * Only the [testPlant] has been planted, and thus has an associated [GardenPlanting]
         */
        assertThat(plantAndGardenPlantings[0].plant, equalTo(testPlant))
        assertThat(plantAndGardenPlantings[0].gardenPlantings.size, equalTo(1))
        assertThat(plantAndGardenPlantings[0].gardenPlantings[0], equalTo(testGardenPlanting))
    }
}
