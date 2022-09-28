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
    val destinations: List<ExploreModel> = destinationsLocalDataSource.craneDestinations
    val hotels: List<ExploreModel> = destinationsLocalDataSource.craneHotels
    val restaurants: List<ExploreModel> = destinationsLocalDataSource.craneRestaurants

    fun getDestination(cityName: String): ExploreModel? {
        return destinationsLocalDataSource.craneDestinations.firstOrNull {
            it.city.name == cityName
        }
    }
}
