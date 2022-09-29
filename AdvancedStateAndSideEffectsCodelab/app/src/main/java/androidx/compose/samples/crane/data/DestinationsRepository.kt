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

package androidx.compose.samples.crane.data

import androidx.compose.samples.crane.base.Result
import androidx.compose.samples.crane.details.DetailsActivity
import androidx.compose.samples.crane.details.DetailsViewModel
import androidx.compose.samples.crane.details.KEY_ARG_DETAILS_CITY_NAME
import androidx.compose.samples.crane.details.launchDetailsActivity
import androidx.compose.samples.crane.home.CraneScreen
import androidx.compose.samples.crane.home.MainViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * A Repository allows the app to use different Data Sources, but in our case we only use the static
 * fake data supplied by the Singleton [DestinationsLocalDataSource]. The `Inject` annotation
 * identifies our constructor as a injectable constructor to Hilt and Hilt generates a
 * [DestinationsRepository_Factory] java class from this file which it will use when a
 * [DestinationsRepository] class injection is requested.
 *
 * @param destinationsLocalDataSource the Singleton instance of [DestinationsLocalDataSource] that
 * will be injected by Hilt
 */
class DestinationsRepository @Inject constructor(
    private val destinationsLocalDataSource: DestinationsLocalDataSource
) {
    /**
     * This [List] of [ExploreModel] objects is used by [MainViewModel] in its
     * [MainViewModel.toDestinationChanged] method, and its [MainViewModel.updatePeople] method to
     * update its [MainViewModel.suggestedDestinations] property of [StateFlow] of [List] of
     * [ExploreModel], which is collected as "state" for the variable `suggestedDestinations` in
     * `CraneHomeContent` (file home/CraneHome.kt) which is used when the [CraneScreen.Fly] tab of
     * `CraneHomeContent` is selected (file home/CraneHome.kt)
     */
    val destinations: List<ExploreModel> = destinationsLocalDataSource.craneDestinations

    /**
     * This [List] of [ExploreModel] is used by [MainViewModel] for its [MainViewModel.hotels]
     * property, which is used when the [CraneScreen.Sleep] tab of `CraneHomeContent` is selected
     * (file home/CraneHome.kt)
     */
    val hotels: List<ExploreModel> = destinationsLocalDataSource.craneHotels

    /**
     * This [List] of [ExploreModel] is used by [MainViewModel] for its [MainViewModel.restaurants]
     * property, which is used when the [CraneScreen.Eat] tab of `CraneHomeContent` is selected
     * (file home/CraneHome.kt)
     */
    val restaurants: List<ExploreModel> = destinationsLocalDataSource.craneRestaurants

    /**
     * This is called by [DetailsViewModel] to get the [ExploreModel] whose [City.name] matches the
     * [String] passed as an extra under the key [KEY_ARG_DETAILS_CITY_NAME] when the
     * [launchDetailsActivity] method launches the [DetailsActivity]. The [ExploreModel] it finds
     * is returned as a [Result.Success] of [ExploreModel] by the [DetailsViewModel.cityDetails]
     * property (or if we return `null` it returns a [Result.Error] of [IllegalArgumentException]).
     * [DetailsViewModel.cityDetails] is read by the `DetailsScreen` Composable (file
     * details/DetailsActivity.kt).
     *
     * @param cityName the [City.name] we are to search the [DestinationsLocalDataSource.craneDestinations]
     * [List] of [ExploreModel] for.
     * @return the first [ExploreModel] whose [City.name] matches our [String] parameter [cityName]
     * or `null` if none is found.
     */
    fun getDestination(cityName: String): ExploreModel? {
        return destinationsLocalDataSource.craneDestinations.firstOrNull {
            it.city.name == cityName
        }
    }
}
