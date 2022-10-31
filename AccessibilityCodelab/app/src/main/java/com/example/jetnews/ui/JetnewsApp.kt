/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui

import android.annotation.SuppressLint
import android.view.View
import android.view.Window
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.data.AppContainer
import com.example.jetnews.ui.theme.JetnewsTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

/**
 * This is the main Composable of the app. We start by initializing our [SystemUiController] variable
 * `val systemUiController` using the [rememberSystemUiController] metod. It "remembers" it using the
 * `current` [LocalView] and the [Window] associated with that [View] as `keys`. We then use the
 * Composable [SideEffect] function to schedule its `effect` lambda to run when the current composition
 * finishes and then every recomposition thereafter. In that lambda we call the method
 * [SystemUiController.setSystemBarsColor] of `systemUiController` to set the color of the status and
 * navigation bars [Color.Transparent], and to set its `darkIcons` property to `false` to prevent the
 * use of dark navigation bar icons.
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun JetnewsApp(
    appContainer: AppContainer
) {
    JetnewsTheme {
        val systemUiController: SystemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = false)
        }

        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        // This top level scaffold contains the app drawer, which needs to be accessible
        // from multiple screens. An event to open the drawer is passed down to each
        // screen that needs it.
        val scaffoldState = rememberScaffoldState()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: MainDestinations.HOME_ROUTE
        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                AppDrawer(
                    currentRoute = currentRoute,
                    navigateToHome = { navController.navigate(MainDestinations.HOME_ROUTE) },
                    navigateToInterests = { navController.navigate(MainDestinations.INTERESTS_ROUTE) },
                    closeDrawer = { coroutineScope.launch { scaffoldState.drawerState.close() } }
                )
            }
        ) {
            JetnewsNavGraph(
                appContainer = appContainer,
                navController = navController,
                scaffoldState = scaffoldState
            )
        }
    }
}
