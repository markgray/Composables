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

import androidx.compose.runtime.Immutable

/**
 * This data class contains all the information our app needs to identify and locate a city. The
 * Immutable annotation marks this class as producing immutable instances. This is a promise by the
 * type that all publicly accessible properties and fields will not change after the instance is
 * constructed. Immutable is used by composition to enable composition optimizations that can be
 * performed based on the assumption that values read from the type will not change.
 *
 * @param name the name of the city.
 * @param country the country that the city is in.
 * @param latitude the coordinate that specifies the north–south position of a point on the surface
 * of the Earth,an angle that ranges from –90° at the south pole to 90° at the north pole, with 0°
 * at the Equator.
 * @param longitude the geographic coordinate that specifies the east–west position of a point on
 * the surface of the Earth, an angular measurement that ranges from –180° to 180°. The prime
 * meridian is the line of 0° longitude arbitrarily designated to run though the Greenwich Royal
 * Observatory in London, England.
 */
@Immutable
data class City(
    val name: String,
    val country: String,
    val latitude: String,
    val longitude: String
) {
    /**
     * The [String] to display to a user for this [City].
     */
    val nameToDisplay = "$name, $country"
}

/**
 * This data class holds a [City] instance in its [city] field that can be used to locate the city
 * that the [ExploreModel] refers to, holds a [String] in its [description] field that contains
 * different information about the city depending on which [List] the [ExploreModel] is in (the
 * [DestinationsLocalDataSource.craneDestinations] list uses the field to display information about
 * the flight time ie. "Nonstop - 5h 16m+", the [DestinationsLocalDataSource.craneHotels] list
 * contains information about how many hotels are available ie. "1286 Available Properties" and the
 * [DestinationsLocalDataSource.craneRestaurants] list contains information about how many Restaurants
 * are available ie. "1286 Restaurants"), and in its [imageUrl] field it holds a https URL for a
 * photo on the unsplash.com website that is appropriate for the [ExploreModel]. The Immutable
 * annotation marks this class as producing immutable instances. This is a promise by the type that
 * all publicly accessible properties and fields will not change after the instance is constructed.
 * Immutable is used by composition to enable composition optimizations that can be performed based
 * on the assumption that values read from the type will not change.
 *
 * @param city a [City] instance that can be used to identify and locate the city that this
 * [ExploreModel] refers to.
 * @param description information about the city that is appropriate for the type of [List] that the
 * [ExploreModel] is in.
 * @param imageUrl a https URL for a photo on the unsplash.com website that is appropriate for the
 * [ExploreModel].
 */
@Immutable
data class ExploreModel(
    val city: City,
    val description: String,
    val imageUrl: String
)
