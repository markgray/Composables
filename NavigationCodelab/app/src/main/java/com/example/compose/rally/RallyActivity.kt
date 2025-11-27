/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.compose.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.rally.ui.components.RallyTab
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to
     * edge display, then we call our super's implementation of `onCreate`. Next we call the
     * [setContent] method to have it compose a [Box] whose `modifier` argument is a
     * [Modifier.safeDrawingPadding] to add padding to accommodate the safe drawing insets.
     * In its [BoxScope] `content` composable lambda argument we compose our [RallyApp] Composable
     * into the activity.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.safeDrawingPadding()) {
                RallyApp()
            }
        }
    }
}

/**
 * This is the main screen of our app. First we initialize and remember our [NavHostController]
 * variable `val navController` using the [rememberNavController] method, then we initialize our
 * [NavBackStackEntry] variable `val currentBackStack` by using the method
 * [NavHostController.currentBackStackEntryAsState] of `navController` (it gets the current
 * navigation back stack entry as a [MutableState]. When `navController` changes the back stack
 * due to a [NavHostController.navigate] or [NavHostController.popBackStack] this will trigger a
 * recompose and return the top entry on the back stack). We initialize our [NavDestination]
 * variable `val currentDestination` to the [NavBackStackEntry.destination] of `currentBackStack`,
 * and initialize our [RallyDestination] variable `val currentScreen` by searching the [List] of
 * [RallyDestination] field [rallyTabRowScreens] for a [RallyDestination] whose [RallyDestination.route]
 * is equal to the [NavDestination.route] of `currentDestination`, defaulting to [Overview]'s
 * [RallyDestination] if none is found.
 *
 * Our root Composable is a [Scaffold] whose `topBar` argument is a [RallyTabRow] constructed to
 * use [rallyTabRowScreens] as its `allScreens` argument, with its `onTabSelected` argument a
 * lambda which calls the [navigateSingleTopTo] extension method of `navController`, and the
 * `currentScreen` argument is our `currentScreen` variable (the currently selected [RallyTab]
 * in the [RallyTabRow], each [RallyTab] compares this value with the [RallyDestination] it is
 * associated with and modifies its appearance if they are equal). The `content` of the [Scaffold]
 * is a [RallyNavHost] whose `navController` argument is our [NavHostController] variable
 * `navController` and whose `modifier` argument is a [Modifier.padding] that uses the
 * [PaddingValues] passed the `content` lambda to pad the [RallyNavHost] on all sides with that
 * value.
 */
@Composable
fun RallyApp() {
    RallyTheme {
        val navController: NavHostController = rememberNavController()
        val currentBackStack: NavBackStackEntry? by navController.currentBackStackEntryAsState()
        // Fetch your currentDestination:
        val currentDestination: NavDestination? = currentBackStack?.destination
        // Change the variable to this and use Overview as a backup screen if this returns null
        val currentScreen: RallyDestination =
            rallyTabRowScreens.find { it.route == currentDestination?.route } ?: Overview
        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = rallyTabRowScreens,
                    // Pass the callback like this,
                    // defining the navigation action when a tab is selected:
                    onTabSelected = { newScreen: RallyDestination ->
                        navController.navigateSingleTopTo(route = newScreen.route)
                    },
                    currentScreen = currentScreen,
                )
            }
        ) { innerPadding: PaddingValues ->
            RallyNavHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues = innerPadding)
            )
        }
    }
}
