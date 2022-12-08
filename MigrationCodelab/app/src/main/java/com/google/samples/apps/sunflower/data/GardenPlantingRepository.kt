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

import androidx.lifecycle.LiveData
import com.google.samples.apps.sunflower.plantdetail.PlantDetailFragment
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel
import com.google.samples.apps.sunflower.GardenFragment
import com.google.samples.apps.sunflower.viewmodels.GardenPlantingListViewModel

/**
 * This is the Repository used to handle [GardenPlanting] data that is stored in the "garden_plantings"
 * table of the database. Repository classes are responsible for the following tasks:
 *  - Exposing data to the rest of the app.
 *  - Centralizing changes to the data.
 *  - Resolving conflicts between multiple data sources.
 *  - Abstracting sources of data from the rest of the app.
 *  - Containing business logic.
 *
 * It is used by the [PlantDetailViewModel] that [PlantDetailFragment] uses and by the
 * [GardenPlantingListViewModel] that [GardenFragment] uses.
 */
class GardenPlantingRepository private constructor(
    /**
     * The [GardenPlantingDao] used to access the "garden_plantings" table in the database.
     */
    private val gardenPlantingDao: GardenPlantingDao
) {

    /**
     * Creates a new instance of [GardenPlanting] for the [Plant] whose [Plant.plantId] is the same
     * as our [String] parameter [plantId] to initialize its variable `val gardenPlanting`, then
     * calls the [GardenPlantingDao.insertGardenPlanting] method of our [gardenPlantingDao] field
     * to have it insert the [GardenPlanting] in the  "garden_plantings" table of the database.
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
     * of our [gardenPlantingDao] field with [gardenPlanting] as the argument.
     *
     * @param gardenPlanting the [GardenPlanting] to remove from the "garden_plantings" table of the
     * database.
     */
    @Suppress("unused")
    suspend fun removeGardenPlanting(gardenPlanting: GardenPlanting) {
        gardenPlantingDao.deleteGardenPlanting(gardenPlanting)
    }

    /**
     * Returns a [LiveData] wrapped [Boolean] that is true if and only if one of the rows of the
     * "garden_plantings" table has a "plant_id" column containing the value of our [String]
     * parameter [plantId]. It does this by returning the value returned by the
     * [GardenPlantingDao.isPlanted] method of our [gardenPlantingDao] field with our [plantId]
     * parameter as its argument.
     *
     * @param plantId the [Plant.plantId] field of the [Plant] that we are to look for in the
     * "garden_plantings" table of the database.
     */
    fun isPlanted(plantId: String): LiveData<Boolean> =
        gardenPlantingDao.isPlanted(plantId)

    /**
     * TODO: Add kdoc
     */
    fun getPlantedGardens(): LiveData<List<PlantAndGardenPlantings>> = gardenPlantingDao.getPlantedGardens()

    companion object {

        /**
         * For Singleton instantiation our [getInstance] method caches the [GardenPlantingRepository]
         * it creates the first time it runs here, and thereafter returns the same instance.
         */
        @Volatile
        private var instance: GardenPlantingRepository? = null

        /**
         * TODO: Add kdoc
         */
        fun getInstance(gardenPlantingDao: GardenPlantingDao): GardenPlantingRepository =
            instance ?: synchronized(this) {
                instance ?: GardenPlantingRepository(gardenPlantingDao).also { instance = it }
            }
    }
}
