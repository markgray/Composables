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

@file:Suppress("unused")

package com.google.samples.apps.sunflower.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the Repository used to handle [GardenPlanting] data that is stored in the
 * "garden_plantings" table of the database. Repository classes are responsible for
 * the following tasks:
 *  - Exposing data to the rest of the app.
 *  - Centralizing changes to the data.
 *  - Resolving conflicts between multiple data sources.
 *  - Abstracting sources of data from the rest of the app.
 *  - Containing business logic.
 *
 * The @[Singleton] annotation Identifies a type that the Hilt only instantiates once, the @[Inject]
 * annotation identifies our constructor as an injectable constructor to Hilt and Hilt generates a
 * `GardenPlantingRepository_Factory` java class from this file which it will use when a
 * [GardenPlantingRepository] class injection is requested.
 *
 * @property gardenPlantingDao the [GardenPlantingDao] DAO for the [GardenPlanting] table, injected
 * by Hilt.
 */
@Singleton
class GardenPlantingRepository @Inject constructor(
    private val gardenPlantingDao: GardenPlantingDao
) {

    /**
     * Creates a new instance of [GardenPlanting] for the [Plant] whose [Plant.plantId] is the same
     * as our [String] parameter [plantId] to initialize its variable `val gardenPlanting`, then
     * calls the [GardenPlantingDao.insertGardenPlanting] method of our [gardenPlantingDao] property
     * to have it insert the [GardenPlanting] into the  "garden_plantings" table of the database.
     *
     * @param plantId the [Plant.plantId] field of the [Plant] that is to be "planted" as a
     * [GardenPlanting] in the "garden_plantings" table of the database.
     */
    suspend fun createGardenPlanting(plantId: String) {
        val gardenPlanting = GardenPlanting(plantId)
        gardenPlantingDao.insertGardenPlanting(gardenPlanting)
    }

    /**
     * Removes its [GardenPlanting] parameter [gardenPlanting] from the "garden_plantings"
     * table of the database by calling the [GardenPlantingDao.deleteGardenPlanting] method
     * of our [gardenPlantingDao] property with [gardenPlanting] as the argument.
     *
     * @param gardenPlanting the [GardenPlanting] to remove from the "garden_plantings" table of the
     * database.
     */
    suspend fun removeGardenPlanting(gardenPlanting: GardenPlanting) {
        gardenPlantingDao.deleteGardenPlanting(gardenPlanting)
    }

    /**
     * Returns a [Flow] of [Boolean] that is `true` if and only if one of the rows of the
     * "garden_plantings" table has a "plant_id" column containing the value of our [String]
     * parameter [plantId]. It does this by returning the value returned by the
     * [GardenPlantingDao.isPlanted] method of our [gardenPlantingDao] property with our [plantId]
     * parameter as its argument.
     *
     * @param plantId the [Plant.plantId] field of the [Plant] that we are to look for in the
     * "garden_plantings" table of the database.
     */
    fun isPlanted(plantId: String): Flow<Boolean> =
        gardenPlantingDao.isPlanted(plantId)

    /**
     * Returns the [Flow] of [List] of [PlantAndGardenPlantings] created by ROOM from both
     * the "garden_plantings" table and the "plants" of the database by using the "plant_id" column
     * of the row in the "garden_plantings" table to fetch the corresponding [Plant] row in the
     * "plants" table. This is all done automatically by the [GardenPlantingDao.getPlantedGardens]
     * method of our [gardenPlantingDao] property.
     *
     * @return the [Flow] of [List] of [PlantAndGardenPlantings] created by ROOM from both
     * the "garden_plantings" table and the "plants" of the database by using the "plant_id" column
     * of the row in the "garden_plantings" table to fetch the corresponding [Plant] row in the
     * "plants" table.
     */
    fun getPlantedGardens(): Flow<List<PlantAndGardenPlantings>> =
        gardenPlantingDao.getPlantedGardens()

    companion object {

        /**
         * For Singleton instantiation our [getInstance] method caches the [GardenPlantingRepository]
         * it creates the first time it runs here, and thereafter returns the same instance.
         */
        @Volatile
        private var instance: GardenPlantingRepository? = null

        /**
         * Called to fetch a reference to our singleton [GardenPlantingRepository]. If our [instance]
         * field is not `null` we return it, otherwise in a block [synchronized] on `this` we construct
         * a new instance of [GardenPlantingRepository] using our [GardenPlantingDao] parameter
         * [gardenPlantingDao] as its `gardenPlantingDao` argument and return it after using the
         * [also] extension method to cache the new instance in our [instance] field.
         *
         * @param gardenPlantingDao the [GardenPlantingDao] that we should use to access the
         * "garden_plantings" table.
         * @return our singleton instance of [GardenPlantingRepository], with a new one created and
         * cached if we have not been called before.
         */
        fun getInstance(gardenPlantingDao: GardenPlantingDao): GardenPlantingRepository =
            instance ?: synchronized(this) {
                instance ?: GardenPlantingRepository(gardenPlantingDao).also { instance = it }
            }
    }
}
