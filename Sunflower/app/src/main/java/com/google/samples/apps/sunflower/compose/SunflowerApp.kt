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

package com.google.samples.apps.sunflower.compose

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.compose.gallery.GalleryScreen
import com.google.samples.apps.sunflower.compose.home.HomeScreen
import com.google.samples.apps.sunflower.compose.plantdetail.PlantDetailsScreen
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.UnsplashPhoto
import com.google.samples.apps.sunflower.data.UnsplashUser

/**
 * Main entry point for the Sunflower app. Sets up the navigation controller and
 * defines the navigation graph.
 *
 * We start by initializing and remembering our [NavHostController] variable `navController` using
 * the [rememberNavController] method. Then we compose a [SunFlowerNavHost] with its `navController`
 * argument our [NavHostController] variable `navController`.
 *
 */
@Composable
fun SunflowerApp() {
    val navController: NavHostController = rememberNavController()
    SunFlowerNavHost(
        navController = navController
    )
}

/**
 * Sets up the navigation host for the Sunflower app.
 *
 * This composable function defines the navigation graph, mapping routes to their
 * corresponding screens. It handles navigation between the home screen, plant details screen,
 * and the photo gallery.
 *
 * We start by initializing our [Activity] variable `activity` to the current [LocalActivity]. Then
 * we compose a [NavHost] whose `navController` argument is our [NavHostController] variable
 * `navController`, and whose `startDestination` argument is the route for the home screen, the
 * [Screen.route] of the [Screen.Home]. In the [NavGraphBuilder] `builder` lambda argument we:
 *
 * **First** We use [NavGraphBuilder.composable] to add a destination for the `route` argument the
 * [Screen.route] of [Screen.Home] ("home"). In its [AnimatedContentScope] `content` composable
 * lambda argument we compose a [HomeScreen] whose `onPlantClick` argument is a lambda that accepts
 * the [Plant] passed the lambda in variable `plant` and calls the [NavHostController.navigate]
 * method of [NavHostController] variable `navController` with its `route` argument the value
 * returned by the [Screen.PlantDetail.createRoute] method of [Screen.PlantDetail] when called with
 * the [Plant.plantId] of [Plant] variable `plant`.
 *
 * **Second** We use [NavGraphBuilder.composable] to add a destination for the `route` argument the
 * [Screen.route] of [Screen.PlantDetail] with its `navArguments` argument the value of the
 * [Screen.navArguments] property of [Screen.PlantDetail]. In its [AnimatedContentScope] `content`
 * composable lambda argument we compose a [PlantDetailsScreen] whose arguments are:
 *  - `onBackClick`: A lambda that calls the [NavHostController.navigateUp] method of
 *  [NavHostController] variable `navController`.
 *  - `onShareClick`: A lambda that accepts the [String] passed the lambda in variable `plantName`
 *  and calls the [createShareIntent] method with its `activity` argument our [Activity] variable
 *  `activity` and its `plantName` argument [String] variable `plantName`.
 *  - `onGalleryClick`: A lambda that accepts the [Plant] passed the lambda in variable `plant`
 *  and calls the [NavHostController.navigate] method of [NavHostController] variable `navController`
 *  with its `route` argument the value returned by the [Screen.Gallery.createRoute] method of
 *  [Screen.Gallery] for the `plantName` argument the [Plant.name] of [Plant] variable `plant`.
 *
 * **Third** We use [NavGraphBuilder.composable] to add a destination for the `route` argument the
 * [Screen.route] of [Screen.Gallery] with its `navArguments` argument the value of the [Screen.navArguments]
 * property of [Screen.Gallery]. In its [AnimatedContentScope] `content` composable lambda argument we
 * compose a [GalleryScreen] whose arguments are:
 *  - `onPhotoClick`: A lambda that accepts the [UnsplashPhoto] passed the lambda in variable
 *  `photo` then initializes its [Uri] variable `uri` to the [UnsplashUser.attributionUrl] of the
 *  [UnsplashPhoto.user] of [UnsplashPhoto] variable `photo`. Then it initializes its [Intent]
 *  variable `intent` to an [Intent] whose `action` property is [Intent.ACTION_VIEW] and its `data`
 *  property is the [Uri] variable `uri`. Finally it calls the [Activity.startActivity] method
 *  of [Activity] variable `activity` with its `intent` argument [Intent] variable `intent`.
 *  - `onUpClick`: A lambda that calls the [NavHostController.navigateUp] method of
 *  [NavHostController] variable `navController`.
 *
 * @param navController The [NavHostController] that manages app navigation.
 */
@Composable
fun SunFlowerNavHost(
    navController: NavHostController
) {
    val activity: Activity = (LocalActivity.current as Activity)
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onPlantClick = { plant: Plant ->
                    navController.navigate(
                        route = Screen.PlantDetail.createRoute(
                            plantId = plant.plantId
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.PlantDetail.route,
            arguments = Screen.PlantDetail.navArguments
        ) {
            PlantDetailsScreen(
                onBackClick = { navController.navigateUp() },
                onShareClick = { plantName: String ->
                    createShareIntent(activity = activity, plantName = plantName)
                },
                onGalleryClick = { plant: Plant ->
                    navController.navigate(
                        route = Screen.Gallery.createRoute(
                            plantName = plant.name
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.Gallery.route,
            arguments = Screen.Gallery.navArguments
        ) {
            GalleryScreen(
                onPhotoClick = { photo: UnsplashPhoto ->
                    val uri: Uri = photo.user.attributionUrl.toUri()
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    activity.startActivity(intent)
                },
                onUpClick = {
                    navController.navigateUp()
                })
        }
    }
}

/**
 * Helper function for calling a share functionality, it creates a share intent and starts the share
 * activity. It should be used when user presses a share button/menu item.
 *
 * This function constructs an intent to share a piece of text. The text is formatted
 * using a string resource and includes the name of the plant. It then starts an
 * activity to present the user with a chooser for sharing the content.
 *
 * We start by initializing our [String] variable `shareText` to the [String] formatted using the
 * format string with resource ID `R.string.share_text_plant` and the [String] parameter `plantName`
 * as the `formatArgs`. Then we initialize our [Intent] variable `shareIntent` to the [Intent]
 * created by a [ShareCompat.IntentBuilder] whose `context` argument is our [Activity] parameter
 * [activity], to which we chain an [ShareCompat.IntentBuilder.setText] method with its `text`
 * argument our [String] variable `shareText`, to which we chain an [ShareCompat.IntentBuilder.setType]
 * method with its `mimeType` "text/plain", to which we chain a
 * [ShareCompat.IntentBuilder.createChooserIntent] to create an Intent that will launch the standard
 * Android activity chooser, allowing the user to pick what activity/app on the system should handle
 * the share, and finally we chain a [Intent.addFlags] method adding the flags
 * [Intent.FLAG_ACTIVITY_NEW_DOCUMENT] and [Intent.FLAG_ACTIVITY_MULTIPLE_TASK].
 *
 * We then call the [Activity.startActivity] method of [Activity] parameter [activity] with its
 * `intent` argument our [Intent] variable `shareIntent`.
 *
 * @param activity The [Activity] context used to create and start the intent.
 * @param plantName The name of the plant to be included in the shared text.
 */
private fun createShareIntent(activity: Activity, plantName: String) {
    val shareText: String = activity.getString(R.string.share_text_plant, plantName)
    val shareIntent: Intent = ShareCompat.IntentBuilder(activity)
        .setText(shareText)
        .setType("text/plain")
        .createChooserIntent()
        .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    activity.startActivity(shareIntent)
}