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
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.data.PlantRepository
import com.google.samples.apps.sunflower.utilities.InjectorUtils

/**
 * Factory for creating a [PlantListViewModel] with a constructor that takes a [PlantRepository].
 * Called by [InjectorUtils.providePlantListViewModelFactory] which is called by [PlantListFragment].
 *
 * @property repository the apps singleton [PlantRepository]
 */
class PlantListViewModelFactory(
    private val repository: PlantRepository
) : ViewModelProvider.Factory {

    /**
     * Returns a [PlantListViewModel] that will use the factories [PlantRepository] field
     * [repository] as its  [PlantRepository].
     *
     * @param modelClass the [Class] of the type of [ViewModel] that we are to create.
     * @param extras additional information for this creation request
     * @return a [PlantListViewModel] that will use the factories [PlantRepository] field
     * [repository] as its  [PlantRepository].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        if (modelClass.isAssignableFrom(PlantListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // It is checked by the if statement
            return PlantListViewModel(repository, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
