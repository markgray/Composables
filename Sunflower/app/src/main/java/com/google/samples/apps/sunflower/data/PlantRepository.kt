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

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository module for handling data operations. Collecting from the Flows in [PlantDao] is
 * main-safe.  Room supports Coroutines and moves the query execution off of the main thread.
 *
 * The @[Singleton] annotation Identifies a type that Hilt only instantiates once, the @[Inject]
 * annotation identifies our constructor as an injectable constructor to Hilt and Hilt generates a
 * `PlantRepository_Factory` java class from this file which it will use when a
 * [PlantRepository] class injection is requested.
 *
 * @property plantDao the [PlantDao] DAO for the [Plant] table, injected by Hilt.
 */
@Singleton
class PlantRepository @Inject constructor(private val plantDao: PlantDao) {

    /**
     * Retrieves a [Flow] of [List] of all [Plant]s from the database.
     * The list is sorted alphabetically by the plant's name.
     *
     * @return A [Flow] emitting a [List] of all [Plant]s.
     */
    fun getPlants(): Flow<List<Plant>> = plantDao.getPlants()

    /**
     * Retrieves a plant from the database based on its unique [plantId] as a [Flow] of [Plant].
     *
     * @param plantId The ID of the plant to retrieve.
     * @return A [Flow] emitting the [Plant] object.
     */
    fun getPlant(plantId: String): Flow<Plant> = plantDao.getPlant(plantId)

    /**
     * Retrieves a [Flow] of a [List] of [Plant]s that have a specific [growZoneNumber].
     * The list is sorted alphabetically by the plant's name.
     *
     * @param growZoneNumber The grow zone number to filter by.
     * @return A [Flow] emitting a [List] of [Plant]s matching the grow zone number.
     */
    fun getPlantsWithGrowZoneNumber(growZoneNumber: Int): Flow<List<Plant>> =
        plantDao.getPlantsWithGrowZoneNumber(growZoneNumber)

    companion object {

        /**
         * For Singleton instantiation our [getInstance] method caches the [PlantRepository]
         * it creates the first time it runs here, and thereafter returns the same instance.
         */
        @Volatile
        private var instance: PlantRepository? = null

        /**
         * Called to fetch a reference to our singleton [PlantRepository]. If our [instance]
         * field is not `null` we return it, otherwise in a block [synchronized] on `this` we
         * construct a new instance of [PlantRepository] using our [PlantDao] parameter
         * [plantDao] as its `plantDao` argument and return it after using the
         * [also] extension method to cache the new instance in our [instance] field.
         *
         * @param plantDao the [PlantDao] that we should use to access the "plants" table.
         * @return our singleton instance of [PlantRepository], with a new one created and
         * cached if we have not been called before.
         */
        fun getInstance(plantDao: PlantDao): PlantRepository =
            instance ?: synchronized(this) {
                instance ?: PlantRepository(plantDao).also { instance = it }
            }
    }
}
