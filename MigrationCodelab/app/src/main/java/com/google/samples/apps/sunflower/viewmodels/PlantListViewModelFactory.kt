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

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.data.PlantRepository
import com.google.samples.apps.sunflower.utilities.InjectorUtils

/**
 * Factory for creating a [PlantListViewModel] with a constructor that takes a [PlantRepository].
 * Called by [InjectorUtils.providePlantListViewModelFactory] which is called by [PlantListFragment].
 *
 * @param repository the apps singleton [PlantRepository]
 * @param owner A scope that owns [SavedStateRegistry], in our case the [Fragment] that calls the
 * [InjectorUtils.providePlantListViewModelFactory] method: [PlantListFragment].
 * @param defaultArgs values from this [Bundle] will be used as defaults by [SavedStateHandle]
 * passed in ViewModels if there is no previously saved state or previously saved state misses
 * a value by such key, none is specified by [InjectorUtils.providePlantListViewModelFactory] so
 * it is the default value `null`.
 */
class PlantListViewModelFactory(
    private val repository: PlantRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    /**
     * Returns a [PlantListViewModel] that will use the factories [PlantRepository] field
     * [repository] as its  [PlantRepository].
     *
     * @param key a key associated with the requested [ViewModel]
     * @param modelClass the [Class] of the type of [ViewModel] that we are to create.
     * @param handle a handle to saved state associated with the requested [ViewModel]
     * @return a [PlantListViewModel] that will use the factories [PlantRepository] field
     * [repository] as its  [PlantRepository].
     */
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(PlantListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // It is checked by above if statement
            return PlantListViewModel(repository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
