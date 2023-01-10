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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.samples.apps.sunflower.data.GardenPlantingRepository

/**
 * Factory for creating a [GardenPlantingListViewModel] using a constructor for the [ViewModel] that
 * takes the apps singleton [GardenPlantingRepository].
 *
 * @param repository the apps singleton [GardenPlantingRepository].
 */
class GardenPlantingListViewModelFactory(
    private val repository: GardenPlantingRepository
) : ViewModelProvider.Factory {

    /**
     * Returns a new instance of [GardenPlantingListViewModel] constructed to use the apps singleton
     * [GardenPlantingRepository]. First we make sure that our [Class] parameter [modelClass] can
     * be hold a [GardenPlantingListViewModel] (throwing [IllegalArgumentException] is it cannot).
     * Then we return a new instance of [GardenPlantingListViewModel] constructed to use our
     * [GardenPlantingRepository] field [repository].
     *
     * @param modelClass the [Class] of the type of [ViewModel] that we are to create.
     * @return a new instance of [GardenPlantingListViewModel] constructed to use the apps singleton
     * [GardenPlantingRepository].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GardenPlantingListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // It is checked by above if statement
            return GardenPlantingListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
