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
import android.app.Application
import android.view.View
import android.view.Window
import androidx.compose.material.DrawerState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.theme.JetnewsTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
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
 *
 * Next we initialize and remember our [NavHostController] variable `val navController`, initialize
 * and remember our [CoroutineScope] variable `val coroutineScope`, and initialize and remember our
 * [ScaffoldState] variable `val scaffoldState`.
 *
 * We initialize our [NavBackStackEntry] variable `val navBackStackEntry` to the [State] wrapped
 * [NavBackStackEntry] that is returned by the [NavHostController.currentBackStackEntryAsState]
 * method of `navController`, and we initialize our [String] variable `val currentRoute` to the
 * the value returned for the [NavDestination.route] property of the [NavBackStackEntry.destination]
 * property of `navBackStackEntry` (which is the unique route of the destination associated with the
 * entry), defaulting to [MainDestinations.HOME_ROUTE] if the result is `null`.
 *
 * Finally we call our root [Scaffold] Composable with its `scaffoldState` argument our [ScaffoldState]
 * variable `scaffoldState`, and its `drawerContent` argument an [AppDrawer] whose `currentRoute`
 * argument is our `currentRoute` [String] variable, whose `navigateToHome` argument is a lambda
 * which calls the [NavHostController.navigate] method of `navController` to navigate to the
 * [MainDestinations.HOME_ROUTE] route in the current [NavGraph]. Its `navigateToInterests` argument
 * is a lambda which calls the [NavHostController.navigate] method of `navController` to navigate to
 * the [MainDestinations.INTERESTS_ROUTE] route in the current [NavGraph]. And its `closeDrawer`
 * argument is a lambda which uses the [CoroutineScope.launch] method of `coroutineScope` to launch
 * a new coroutine without blocking the current thread which calls the [DrawerState.close] method
 * of the [ScaffoldState.drawerState] of `scaffoldState` to close the drawer with animation and
 * suspend until it is fully closed or the animation has been cancelled.
 *
 * The `content` of the [Scaffold] is the [JetnewsNavGraph] Composable with its `appContainer`
 * argument our [AppContainer] parameter [appContainer], its `navController` argument our
 * [NavHostController] variable `navController`, and its `scaffoldState` argument our [ScaffoldState]
 * variable `scaffoldState`.
 *
 * @param appContainer the [AppContainer] which contains the [Application]'s dependencies:
 * [AppContainer.postsRepository] contains a reference to our [PostsRepository], and the field
 * [AppContainer.interestsRepository] contains a reference to our [InterestsRepository].
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

        val navController: NavHostController = rememberNavController()
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        // This top level scaffold contains the app drawer, which needs to be accessible
        // from multiple screens. An event to open the drawer is passed down to each
        // screen that needs it.
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
        val currentRoute: String = navBackStackEntry?.destination?.route
            ?: MainDestinations.HOME_ROUTE
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
