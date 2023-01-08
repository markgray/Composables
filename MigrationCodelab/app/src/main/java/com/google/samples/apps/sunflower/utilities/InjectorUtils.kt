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

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.samples.apps.sunflower.GardenFragment
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.plantdetail.PlantDetailFragment
import com.google.samples.apps.sunflower.data.AppDatabase
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantRepository
import com.google.samples.apps.sunflower.viewmodels.GardenPlantingListViewModel
import com.google.samples.apps.sunflower.viewmodels.GardenPlantingListViewModelFactory
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModelFactory
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    /**
     * Returns the singleton instance of [PlantRepository] used by the app. Used by the methods
     * [providePlantDetailViewModelFactory] and [providePlantListViewModelFactory] to construct our
     * [PlantDetailViewModelFactory] and [PlantListViewModelFactory] respectively.
     *
     * @param context the [Context] that our caller is running under.
     * @return our app's singleton [PlantRepository]
     */
    private fun getPlantRepository(context: Context): PlantRepository {
        return PlantRepository.getInstance(
            plantDao = AppDatabase.getInstance(context.applicationContext).plantDao()
        )
    }

    /**
     * Returns the singleton instance of [GardenPlantingRepository] used by the app. Used by the
     * method [provideGardenPlantingListViewModelFactory] to construct our [GardenPlantingListViewModelFactory]
     * and [providePlantDetailViewModelFactory] to construct our [PlantDetailViewModelFactory].
     *
     * @param context the [Context] that our caller is running under.
     * @return our app's singleton [GardenPlantingRepository]
     */
    private fun getGardenPlantingRepository(context: Context): GardenPlantingRepository {
        return GardenPlantingRepository.getInstance(
            gardenPlantingDao = AppDatabase.getInstance(context.applicationContext).gardenPlantingDao()
        )
    }

    /**
     * Constructs and returns a new instance of [GardenPlantingListViewModelFactory]. Used by
     * [GardenFragment] to retrieve (or create if necessary) its [GardenPlantingListViewModel].
     *
     * @param context the [Context] the fragment is currently associated with.
     * @return a new instance of [GardenPlantingListViewModelFactory].
     */
    fun provideGardenPlantingListViewModelFactory(
        context: Context
    ): GardenPlantingListViewModelFactory {
        return GardenPlantingListViewModelFactory(repository = getGardenPlantingRepository(context))
    }

    /**
     * Constructs and returns a new instance of [PlantListViewModelFactory]. Used by
     * [PlantListFragment] to retrieve (or create if necessary) its [PlantListViewModel].
     *
     * @param fragment the [Fragment] that we are being called from.
     * @return a new instance of [PlantListViewModelFactory].
     */
    fun providePlantListViewModelFactory(fragment: Fragment): PlantListViewModelFactory {
        return PlantListViewModelFactory(
            repository = getPlantRepository(context = fragment.requireContext()),
            owner = fragment
        )
    }

    /**
     * Constructs and returns a new instance of [PlantDetailViewModelFactory]. Used by
     * [PlantDetailFragment] to retrieve (or create if necessary) its [PlantDetailViewModel].
     *
     * @param context the [FragmentActivity] the fragment is currently associated with.
     * @param plantId the [Plant.plantId] of the [Plant] that the [PlantDetailViewModel] is being
     * used for (ie. the [Plant] that [PlantDetailFragment] is supposed to display).
     * @return a new instance of [PlantDetailViewModelFactory] constructed to create a
     * [PlantDetailViewModel] that can interact with the [Plant] in the [PlantRepository] whose
     * [Plant.plantId] property is equal to our [plantId] parameter.
     */
    fun providePlantDetailViewModelFactory(
        context: Context,
        plantId: String
    ): PlantDetailViewModelFactory {
        return PlantDetailViewModelFactory(
            plantRepository = getPlantRepository(context),
            gardenPlantingRepository = getGardenPlantingRepository(context),
            plantId = plantId
        )
    }
}
