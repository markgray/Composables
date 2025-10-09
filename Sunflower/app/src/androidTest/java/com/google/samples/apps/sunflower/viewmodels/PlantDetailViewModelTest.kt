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

package com.google.samples.apps.sunflower.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.samples.apps.sunflower.MainCoroutineRule
import com.google.samples.apps.sunflower.data.AppDatabase
import com.google.samples.apps.sunflower.data.GardenPlanting
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantRepository
import com.google.samples.apps.sunflower.runBlockingTest
import com.google.samples.apps.sunflower.utilities.testPlant
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestWatcher
import javax.inject.Inject

/**
 * Unit tests for [PlantDetailViewModel].
 *
 * The @[HiltAndroidTest] annotation is used to have Hilt inject dependencies into the test class.
 */
@HiltAndroidTest
class PlantDetailViewModelTest {

    /**
     * The in-memory database used for testing.
     */
    private lateinit var appDatabase: AppDatabase

    /**
     * The [PlantDetailViewModel] under test.
     */
    private lateinit var viewModel: PlantDetailViewModel

    /**
     * A JUnit rule that provides a [HiltAndroidRule] to inject dependencies into the test class.
     */
    private val hiltRule = HiltAndroidRule(this)

    /**
     * A JUnit rule that swaps the background executor used by the Architecture Components with a
     * different one which executes each task synchronously.
     */
    private val instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * A custom [TestWatcher] JUnit rule that sets up and tears down a test coroutine dispatcher.
     */
    private val coroutineRule = MainCoroutineRule()

    /**
     * A JUnit rule that combines multiple rules into a single rule.
     *
     * This is useful for ensuring that the rules are applied in the correct order. The [RuleChain]
     * executes rules in a specific order: outer rules are executed before inner rules.
     *
     * In this case, the order is:
     *  1. `hiltRule`: Manages the components' state and is used to perform injection on the test class.
     *  2. `instantTaskExecutorRule`: Swaps the background executor used by Architecture Components with a
     *  synchronous one.
     *  3. `coroutineRule`: Sets the main coroutines dispatcher to a test dispatcher.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    val rule: RuleChain = RuleChain
        .outerRule(hiltRule)
        .around(instantTaskExecutorRule)
        .around(coroutineRule)

    /**
     * The repository for accessing [Plant] data.
     * This is injected by Hilt.
     */
    @Inject
    lateinit var plantRepository: PlantRepository

    /**
     * The repository for accessing [GardenPlanting] data.
     * This is injected by Hilt.
     */
    @Inject
    lateinit var gardenPlantingRepository: GardenPlantingRepository

    /**
     * Sets up the test environment before each test.
     *
     * This function is annotated with @[Before], so it's executed before each test method.
     * It performs the following steps:
     *  1. Injects dependencies using Hilt.
     *  2. Creates an in-memory version of the [AppDatabase] for testing purposes.
     *  3. Initializes a [SavedStateHandle] with a test plant ID, simulating navigation arguments.
     *  4. Instantiates the [PlantDetailViewModel] with the prepared dependencies and [SavedStateHandle].
     */
    @Before
    fun setUp() {
        hiltRule.inject()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        val savedStateHandle: SavedStateHandle = SavedStateHandle().apply {
            set("plantId", testPlant.plantId)
        }
        viewModel =
            PlantDetailViewModel(savedStateHandle, plantRepository, gardenPlantingRepository)
    }

    /**
     * Closes the in-memory database after each test.
     */
    @After
    fun tearDown() {
        appDatabase.close()
    }

    /**
     * Tests that the `isPlanted` LiveData is `false` by default when the plant is not in the garden.
     *
     * This test verifies the initial state of the ViewModel. It creates a [PlantDetailViewModel]
     * for a plant that has not been added to the garden. It then observes the `isPlanted` Flow
     * and asserts that its first emitted value is `false`. This ensures that the ViewModel correctly
     * reflects the plant's initial (not planted) state.
     */
    @Test
    @Throws(InterruptedException::class)
    fun testDefaultValues(): TestResult = coroutineRule.runBlockingTest {
        assertFalse(viewModel.isPlanted.first())
    }
}