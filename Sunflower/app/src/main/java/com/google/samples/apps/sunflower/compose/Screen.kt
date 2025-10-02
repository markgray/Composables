/*
 * Copyright 2023 Google LLC
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

@file:Suppress("unused")

package com.google.samples.apps.sunflower.compose

import androidx.compose.foundation.pager.HorizontalPager
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * A sealed class that represents the different screens in the app.
 *
 * Each screen is defined as a data object or a class that extends this sealed class.
 * It contains the route string and any navigation arguments required for the screen.
 *
 * @property route The route string used for navigation.
 * @property navArguments A list of [NamedNavArgument]s that this screen requires.
 */
sealed class Screen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    /**
     * Represents the home screen of the app, which is composed of a [HorizontalPager]
     * with two tabs: "My Garden" and "Plant List".
     */
    data object Home : Screen(route = "home")

    /**
     * Represents the plant detail screen.
     *
     * This screen displays detailed information about a specific plant.
     * It requires a `plantId` [navArgument] argument to identify which plant to display.
     */
    data object PlantDetail : Screen(
        route = "plantDetail/{plantId}",
        navArguments = listOf(navArgument("plantId") {
            type = NavType.StringType
        })
    ) {
        /**
         * Creates a route to the plant detail screen with the given [plantId].
         *
         * @param plantId The ID of the plant to display.
         * @return The complete route string for the plant detail screen for the given [plantId].
         */
        fun createRoute(plantId: String): String = "plantDetail/${plantId}"
    }

    /**
     * Represents the gallery screen, which shows a list of photos for a specific plant.
     *
     * It requires a `plantName` [navArgument] argument to identify which plant to display.
     */
    data object Gallery : Screen(
        route = "gallery/{plantName}",
        navArguments = listOf(navArgument("plantName") {
            type = NavType.StringType
        })
    ) {
        /**
         * Creates a route to the gallery screen with the given [plantName].
         *
         * @param plantName The name of the plant whose gallery is to be displayed.
         * @return The complete route string for the gallery screen for the given [plantName].
         */
        fun createRoute(plantName: String): String = "gallery/${plantName}"

    }
}