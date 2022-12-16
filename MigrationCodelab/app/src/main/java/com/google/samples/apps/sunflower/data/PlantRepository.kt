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
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.plantdetail.PlantDetailFragment
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel

/**
 * This is the Repository used to handle [Plant] that is stored in the "plants" table of the
 * database. Repository classes are responsible for the following tasks:
 *  - Exposing data to the rest of the app.
 *  - Centralizing changes to the data.
 *  - Resolving conflicts between multiple data sources.
 *  - Abstracting sources of data from the rest of the app.
 *  - Containing business logic.
 *
 * It is used by the [PlantDetailViewModel] that [PlantDetailFragment] uses, and by the
 * [PlantListViewModel] that [PlantListFragment] uses.
 */
class PlantRepository private constructor(
    /**
     * The [PlantDao] used to access the "plant" table in the database.
     */
    private val plantDao: PlantDao
) {

    /**
     * TODO: Add kdoc
     */
    fun getPlants(): LiveData<List<Plant>> = plantDao.getPlants()

    /**
     * TODO: Add kdoc
     */
    fun getPlant(plantId: String): LiveData<Plant> = plantDao.getPlant(plantId)

    /**
     * TODO: Add kdoc
     */
    fun getPlantsWithGrowZoneNumber(growZoneNumber: Int): LiveData<List<Plant>> =
        plantDao.getPlantsWithGrowZoneNumber(growZoneNumber)

    companion object {

        /**
         * For Singleton instantiation our [getInstance] method caches the [PlantRepository]
         * it creates the first time it runs here, and thereafter returns the same instance.
         */
        @Volatile
        private var instance: PlantRepository? = null

        /**
         * Called to fetch a reference to our singleton [PlantRepository]. If our [instance] field
         * is not `null` we return it, otherwise in a block [synchronized] on `this` we construct
         * a new instance of [PlantRepository] using our [PlantDao] parameter [plantDao] as its
         * `plantDao` argument and return it after using the [also] extension method to cache the
         * new instance in our [instance] field.
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
