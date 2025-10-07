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

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantRepository
import com.google.samples.apps.sunflower.compose.home.HomeScreen
import com.google.samples.apps.sunflower.compose.plantlist.PlantListScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for the plant list used in [HomeScreen] and in [PlantListScreen].
 *
 * The @[HiltViewModel] annotation identifies a ViewModel for construction injection.
 * The [ViewModel] annotated with [HiltViewModel] will be available for creation by the
 * `HiltViewModelFactory` and can be retrieved by using the [hiltViewModel] method. The
 * [HiltViewModel] containing a constructor annotated with @[Inject] will have its
 * dependencies defined in the constructor parameters injected by Dagger's Hilt.
 *
 * @param plantRepository The repository for fetching plant data from the database, injected by Hilt.
 * @param savedStateHandle A handle to the saved state of the ViewModel, used to retrieve and store
 * the current grow zone number from the navigation arguments.
 */
@Suppress("MemberVisibilityCanBePrivate")
@HiltViewModel
class PlantListViewModel @Inject internal constructor(
    plantRepository: PlantRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * The current grow zone number.
     *
     * It can be set to [NO_GROW_ZONE] to show all plants. The value is backed by our
     * [SavedStateHandle] property to survive process death.
     */
    private val growZone: MutableStateFlow<Int> = MutableStateFlow(
        value = savedStateHandle[GROW_ZONE_SAVED_STATE_KEY] ?: NO_GROW_ZONE
    )

    /**
     * A list of plants that is filtered by the current grow zone.
     *
     * This [LiveData] is dynamically updated by observing the [MutableStateFlow] of [Int] property
     * [growZone]. When [growZone] is set to [NO_GROW_ZONE], it fetches all plants from the repository.
     * Otherwise, it fetches plants that match the specified grow zone number.
     * The [Flow.flatMapLatest] operator ensures that whenever the [growZone] changes, the old
     * database query is cancelled and a new one is started.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val plants: LiveData<List<Plant>> = growZone.flatMapLatest { zone: Int ->
        if (zone == NO_GROW_ZONE) {
            plantRepository.getPlants()
        } else {
            plantRepository.getPlantsWithGrowZoneNumber(growZoneNumber = zone)
        }
    }.asLiveData()

    init {

        /**
         * When `growZone` changes, store the new value in `savedStateHandle`.
         *
         * There are a few ways to write this; all of these are equivalent. (This info is from
         * https://github.com/android/sunflower/pull/671#pullrequestreview-548900174)
         *
         * 1) A verbose version:
         *
         *    viewModelScope.launch {
         *        growZone.onEach { newGrowZone ->
         *            savedStateHandle.set(GROW_ZONE_SAVED_STATE_KEY, newGrowZone)
         *        }
         *    }.collect()
         *
         * 2) A simpler version of 1). Since we're calling `collect`, we can consume
         *    the elements in the `collect`'s lambda block instead of using the `onEach` operator.
         *    This is the version that's used in the live code below.
         *
         * 3) We can avoid creating a new coroutine using the `launchIn` terminal operator. In this
         *    case, `onEach` is needed because `launchIn` doesn't take a lambda to consume the new
         *    element in the Flow; it takes a `CoroutineScope` that's used to create a coroutine
         *    internally.
         *
         *    growZone.onEach { newGrowZone ->
         *        savedStateHandle.set(GROW_ZONE_SAVED_STATE_KEY, newGrowZone)
         *    }.launchIn(viewModelScope)
         */
        viewModelScope.launch {
            growZone.collect { newGrowZone: Int ->
                savedStateHandle[GROW_ZONE_SAVED_STATE_KEY] = newGrowZone
            }
        }
    }

    /**
     * Toggles the filter state for the plant list.
     *
     * If the list is currently filtered by a grow zone, this function clears the filter,
     * showing all plants. If the list is not filtered, it applies a filter for grow zone 9.
     */
    fun updateData() {
        if (isFiltered()) {
            clearGrowZoneNumber()
        } else {
            setGrowZoneNumber(9)
        }
    }

    /**
     * Sets the grow zone number to filter the plant list.
     *
     * This function updates the [MutableStateFlow] of [Int] property [growZone], which in turn
     * triggers a new database query to fetch plants matching the specified grow zone. The new value
     * is also automatically saved to the [SavedStateHandle] to persist it across process death.
     *
     * @param num The grow zone number to filter by.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun setGrowZoneNumber(num: Int) {
        growZone.value = num
    }

    /**
     * Clears the current grow zone filter.
     *
     * This function resets the grow zone filter, causing the plant list to display all plants
     * regardless of their grow zone. It achieves this by setting the grow zone number to
     * [NO_GROW_ZONE].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun clearGrowZoneNumber() {
        growZone.value = NO_GROW_ZONE
    }

    /**
     * Returns `true` if the plant list is filtered by a grow zone, `false` otherwise.
     *
     * The list is considered filtered if the current grow zone is not set to [NO_GROW_ZONE].
     *
     * @return `true` if the plant list is filtered by a grow zone, `false` otherwise.
     */
    fun isFiltered(): Boolean = growZone.value != NO_GROW_ZONE

    companion object {
        /**
         * A constant representing that no grow zone filter is applied.
         *
         * When the grow zone is set to this value, all plants are displayed.
         */
        private const val NO_GROW_ZONE = -1

        /**
         * A key used to save and restore the selected grow zone number in the [SavedStateHandle].
         * This key is used to persist the user's filter choice across process death.
         */
        private const val GROW_ZONE_SAVED_STATE_KEY = "GROW_ZONE_SAVED_STATE_KEY"
    }
}