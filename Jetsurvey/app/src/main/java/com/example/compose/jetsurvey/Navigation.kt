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

package com.example.compose.jetsurvey

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.jetsurvey.Destinations.SIGN_IN_ROUTE
import com.example.compose.jetsurvey.Destinations.SIGN_UP_ROUTE
import com.example.compose.jetsurvey.Destinations.SURVEY_RESULTS_ROUTE
import com.example.compose.jetsurvey.Destinations.SURVEY_ROUTE
import com.example.compose.jetsurvey.Destinations.WELCOME_ROUTE
import com.example.compose.jetsurvey.signinsignup.SignInRoute
import com.example.compose.jetsurvey.signinsignup.SignUpRoute
import com.example.compose.jetsurvey.signinsignup.WelcomeRoute
import com.example.compose.jetsurvey.survey.SurveyResultScreen
import com.example.compose.jetsurvey.survey.SurveyRoute

/**
 * Destinations used as the routes for the Jetsurvey App.
 */
object Destinations {
    /**
     * Route for the [WelcomeRoute] welcome screen.
     */
    const val WELCOME_ROUTE: String = "welcome"

    /**
     * Route for the [SignInRoute] sign in screen.
     *
     * This route expects an email address to be passed as an argument.
     * The email address can be pre-filled from the welcome screen.
     */
    const val SIGN_IN_ROUTE: String = "signin/{email}"

    /**
     * Route for the [SignUpRoute] sign up screen.
     *
     * This route expects an email address to be passed as an argument.
     * The email address can be pre-filled in the welcome screen.
     */
    const val SIGN_UP_ROUTE: String = "signup/{email}"

    /**
     * Route for the [SurveyRoute] survey screen.
     */
    const val SURVEY_ROUTE: String = "survey"

    /**
     * Route for the [SurveyResultScreen] survey results screen.
     */
    const val SURVEY_RESULTS_ROUTE: String = "surveyresults"
}

/**
 * Provides the navigation graph for the Jetsurvey app.
 *
 * This composable function defines the navigation flow of the app, including the different screens
 * and how they are connected. It uses a [NavHostController] to manage the navigation state and a
 * [NavHost] to display the appropriate screen based on the current route.
 *
 * Our root composable is a [NavHost] whose `navController` argument is our [NavHostController]
 * parameter [navController], and whose `startDestination` argument is [WELCOME_ROUTE]. In the
 * [NavGraphBuilder] `builder` lambda argument we use the [NavGraphBuilder.composable] method to
 * add five routes for destinations in our navigation graph:
 *
 * [WELCOME_ROUTE] -> we compose a [WelcomeRoute] screen whose arguments are:
 *  - `onNavigateToSignIn`: a lambda that accepts the [String] passed the lambda in variable `email`
 *  then calls the [NavHostController.navigate] method to navigate to the [SIGN_IN_ROUTE] route
 *  using the `route` formed by concatenating the [String] "signin/" and the [String] `email`.
 *  - `onNavigateToSignUp`: a lambda that accepts the [String] passed the lambda in variable
 *  `startingEmail` then calls the [NavHostController.navigate] method to navigate to the
 *  [SIGN_UP_ROUTE] route using the `route` formed by concatenating the [String] "signup/" and
 *  the [String] `startingEmail`.
 *  - `onSignInAsGuest`: a lambda that calls the [NavHostController.navigate] method to navigate
 *  to the [SURVEY_ROUTE] route.
 *
 * [SIGN_IN_ROUTE] -> we start by initializing our [String] variable `startingEmail` to the [String]
 * stored in the [NavBackStackEntry.arguments] of the [NavBackStackEntry] that triggered this
 * composable under the key "email". We then compose a [SignInRoute] screen whose arguments are:
 *  - `email`: the [String] variable `startingEmail`.
 *  - `onSignInSubmitted`: a lambda that calls the [NavHostController.navigate] method to navigate
 *  to the [SURVEY_ROUTE] route.
 *  - `onSignInAsGuest`: a lambda that calls the [NavHostController.navigate] method to navigate
 *  to the [SURVEY_ROUTE] route.
 *  - `onNavUp`: a lambda that calls the [NavHostController.navigateUp] method to navigate up
 *  the navigation stack.
 *
 * [SIGN_UP_ROUTE] -> we start by initializing our [String] variable `startingEmail` to the [String]
 * stored in the [NavBackStackEntry.arguments] of the [NavBackStackEntry] that triggered this
 * composable under the key "email". We then compose a [SignUpRoute] screen whose arguments are:
 *  - `email`: the [String] variable `startingEmail`.
 *  - `onSignUpSubmitted`: a lambda that calls the [NavHostController.navigate] method to navigate
 *  to the [SURVEY_ROUTE] route.
 *  - `onSignInAsGuest`: a lambda that calls the [NavHostController.navigate] method to navigate
 *  to the [SURVEY_ROUTE] route.
 *  - `onNavUp`: a lambda that calls the [NavHostController.navigateUp] method to navigate up
 *  the navigation stack.
 *
 * [SURVEY_ROUTE] -> we compose a [SurveyRoute] screen whose arguments are:
 *  - `onSurveyComplete`: a lambda that calls the [NavHostController.navigate] method to navigate
 *  to the [SURVEY_RESULTS_ROUTE] route.
 *  - `onNavUp`: a lambda that calls the [NavHostController.navigateUp] method to navigate up
 *  the navigation stack.
 *
 * [SURVEY_RESULTS_ROUTE] -> we compose a [SurveyResultScreen] screen whose arguments are:
 *  - `onDonePressed`: a lambda that calls the [NavHostController.popBackStack] method to navigate
 *  back to the [WELCOME_ROUTE] route.
 *
 * @param navController The [NavHostController] to use for navigation. Defaults to a new
 * [NavHostController] created by [rememberNavController].
 */
@Composable
fun JetsurveyNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = WELCOME_ROUTE,
    ) {
        composable(route = WELCOME_ROUTE) {
            WelcomeRoute(
                onNavigateToSignIn = { email: String ->
                    navController.navigate(route = "signin/$email")
                },
                onNavigateToSignUp = { startingEmail: String ->
                    navController.navigate(route = "signup/$startingEmail")
                },
                onSignInAsGuest = {
                    navController.navigate(route = SURVEY_ROUTE)
                },
            )
        }

        composable(route = SIGN_IN_ROUTE) { navBack: NavBackStackEntry ->
            val startingEmail: String? = navBack.arguments?.getString("email")
            SignInRoute(
                email = startingEmail,
                onSignInSubmitted = {
                    navController.navigate(route = SURVEY_ROUTE)
                },
                onSignInAsGuest = {
                    navController.navigate(route = SURVEY_ROUTE)
                },
                onNavUp = navController::navigateUp,
            )
        }

        composable(route = SIGN_UP_ROUTE) { navBack: NavBackStackEntry ->
            val startingEmail: String? = navBack.arguments?.getString("email")
            SignUpRoute(
                email = startingEmail,
                onSignUpSubmitted = {
                    navController.navigate(route = SURVEY_ROUTE)
                },
                onSignInAsGuest = {
                    navController.navigate(route = SURVEY_ROUTE)
                },
                onNavUp = navController::navigateUp,
            )
        }

        composable(route = SURVEY_ROUTE) {
            SurveyRoute(
                onSurveyComplete = {
                    navController.navigate(route = SURVEY_RESULTS_ROUTE)
                },
                onNavUp = navController::navigateUp,
            )
        }

        composable(route = SURVEY_RESULTS_ROUTE) {
            SurveyResultScreen {
                navController.popBackStack(route = WELCOME_ROUTE, inclusive = false)
            }
        }
    }
}
