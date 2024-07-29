/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.samples.crane.details

import android.content.Intent
import androidx.compose.samples.crane.base.Result
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.DestinationsRepository
import androidx.compose.samples.crane.home.MainActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * The `HiltViewModel` annotation identifies a [ViewModel] for construction injection. The [ViewModel]
 * annotated with HiltViewModel will be available for creation by the [HiltViewModelFactory] and can
 * be retrieved by default in an `Activity` or `Fragment` annotated with `AndroidEntryPoint`. The
 * `HiltViewModel` containing a constructor annotated with `Inject` will have its dependencies defined
 * in the constructor parameters injected by Dagger's Hilt. The generated java class
 * `DetailsViewModel_Factory.java` is used to provide instances of [DetailsViewModel]. This is the
 * [ViewModel] used by the [DetailsScreen] of [DetailsActivity] which it uses to retrieve the
 * [City] that corresponds to the `cityName` of the item clicked in [MainActivity]'s UI.
 *
 * @param destinationsRepository the [DestinationsRepository] singleton injected by Hilt that we
 * should use in order to search its [List] of [City] using its method
 * [DestinationsRepository.getDestination] for the [City] whose [City.name] matches the one passed
 * as an extra in the [Intent] that launched [DetailsActivity].
 * @param savedStateHandle the [SavedStateHandle] injected by Hilt, it is automatically populated
 * by Hilt with the arguments passed in the [Intent] that started our activity, using the same key
 * that [launchDetailsActivity] used when it stored the `cityName` of the item clicked as an extra.
 */
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val destinationsRepository: DestinationsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * The [SavedStateHandle] is automatically populated by Hilt with the arguments passed in the
     * [Intent] that started our activity, using the same key that [launchDetailsActivity] used when
     * it stored the `cityName` of the item clicked as an extra.
     */
    private val cityName = savedStateHandle.get<String>(KEY_ARG_DETAILS_CITY_NAME)!!

    /**
     * This property uses the [DestinationsRepository.getDestination] method of our field
     * [destinationsRepository] to search for the [City] whose [City.name] matches the [String]
     * passed as an extra under the key [KEY_ARG_DETAILS_CITY_NAME] in the [Intent] that launched
     * [DetailsActivity]. If it succeeds it returns a [Result.Success] containing that [City], and
     * if it fails it returns an [Result.Error] of [IllegalArgumentException]: "City doesn't exist".
     */
    val cityDetails: Result<City>
        get() {
            val destination: City? = destinationsRepository.getDestination(cityName)
            return if (destination != null) {
                Result.Success(data = destination)
            } else {
                Result.Error(exception = IllegalArgumentException("City doesn't exist"))
            }
        }
}
