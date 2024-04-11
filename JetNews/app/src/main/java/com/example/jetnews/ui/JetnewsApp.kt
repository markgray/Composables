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

package com.example.jetnews.ui

import android.app.Application
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.components.AppNavRail
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.ui.interests.InterestsRoute
import com.example.jetnews.ui.theme.JetnewsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This is the main Composable of the entire app. Wrapped in our [JetnewsTheme] custom [MaterialTheme]
 * our root Composable is a [ModalNavigationDrawer]. We start by initializing and remembering our
 * [NavHostController] variable `val navController` to the instance created and remembered by the
 * [rememberNavController] method. Then we initialize and remember our  [JetnewsNavigationActions]
 * variable `val navigationActions` to a new instance whose `navController` argument is our
 * [NavHostController] variable `navController` (it will call its [NavHostController.navigate]
 * method to navigate to [JetnewsDestinations.HOME_ROUTE] which displays [HomeRoute] if its
 * [JetnewsNavigationActions.navigateToHome] method is called or to [JetnewsDestinations.INTERESTS_ROUTE]
 * which displays [InterestsRoute] if its [JetnewsNavigationActions.navigateToInterests] method is
 * called). Next we initialize and remember our [CoroutineScope] variable `val coroutineScope` to the
 * new instance returned by [rememberCoroutineScope]. Then we initialize our [State] wrapped
 * [NavBackStackEntry] variable `val navBackStackEntry` to the value returned by the
 * [NavController.currentBackStackEntryAsState] method of `navController` (a [NavBackStackEntry] is
 * a representation of an entry in the back stack of a [NavController]. The `Lifecycle`, `ViewModelStore`,
 * and `SavedStateRegistry` provided via this object are valid for the lifetime of this destination
 * on the back stack: when this destination is popped off the back stack, the lifecycle will be
 * destroyed, state will no longer be saved, and ViewModels will be cleared). Next we initialize our
 * [String] variable `val currentRoute` to the [NavDestination.route] of `navBackStackEntry` defaulting
 * to [JetnewsDestinations.HOME_ROUTE] ("home") if it is `null`. We then initialize our [Boolean]
 * variable to `true` if our [WindowWidthSizeClass] parameter [widthSizeClass] is equal to
 * [WindowWidthSizeClass.Expanded] (represents the majority of tablets in landscape and large unfolded
 * inner displays in landscape). Next we initialize and remember our [DrawerState] variable
 * `val sizeAwareDrawerState` to the value returned by our [rememberSizeAwareDrawerState] method when
 * its `isExpandedScreen` argument is our [Boolean] variable `isExpandedScreen` (this returns a
 * a [DrawerState] whose `initialValue` is [DrawerValue.Closed] locking it closed if `isExpandedScreen`
 * if `isExpandedScreen` is `true` or a remembered [State] wrapped [DrawerState] whose `initialValue`
 * is [DrawerValue.Closed] if it is `false`.
 *
 * The arguments of our root [ModalNavigationDrawer] Composable are:
 *  - `drawerContent` is a lambda composing an [AppDrawer] Composable whose `currentRoute` argument
 *  is our [String] variable `currentRoute`, whose `navigateToHome` argument is the
 *  [JetnewsNavigationActions.navigateToHome] method of our `navigationActions` variable, whose
 *  `navigateToInterests` argument is the [JetnewsNavigationActions.navigateToInterests] method of
 *  our `navigationActions` variable, whose `closeDrawer` argument is openDrawer
 *  [CoroutineScope.launch] method of our `coroutineScope` variable to launch a new coroutine which
 *  calls the [DrawerState.close] method of our `sizeAwareDrawerState` to close the
 *  [ModalNavigationDrawer].
 *  - `drawerState` our [DrawerState] variable `sizeAwareDrawerState`.
 *  - `gesturesEnabled` is `true` if our [Boolean] variable `isExpandedScreen` is `false` (Only
 *  enable opening the drawer via gestures if the screen is not expanded).
 *
 * The `content` of the [ModalNavigationDrawer] is a [Row] which if `isExpandedScreen` is `true`
 * hold an [AppNavRail] whose `currentRoute` argument is our [String] variable `currentRoute`, whose
 * `navigateToHome` argument is the [JetnewsNavigationActions.navigateToHome] method of our
 * `navigationActions` variable, and whose `navigateToInterests` argument is the
 * [JetnewsNavigationActions.navigateToInterests] method of our navigationActions variable (providing
 * essentially the same functionality provided as the [AppDrawer] used as the `drawerContent` for
 * the [ModalNavigationDrawer] on phones but in an always visible form on tablets).
 *
 * Next in the [Row] whether `isExpandedScreen` is `true` or not is a [JetnewsNavGraph] whose
 * `appContainer` argument is our [AppContainer] parameter [appContainer], whose `isExpandedScreen`
 * argument is our `isExpandedScreen` [Boolean] variable, whose `navController` argument is our
 * [NavHostController] variable `navController`, and whose `openDrawer` argument is a lambda which
 * calls the [CoroutineScope.launch] method of our `coroutineScope` variable to launch a new coroutine
 * which calls the [DrawerState.open] method of our `sizeAwareDrawerState` to open the
 * [ModalNavigationDrawer].
 *
 * @param appContainer this is the [AppContainer] which is created in our [JetnewsApplication] custom
 * [Application] (it contains a reference to our singleton [PostsRepository] in [AppContainer.postsRepository]
 * and a reference to our singleton [InterestsRepository] in [AppContainer.interestsRepository]).
 * @param widthSizeClass the [WindowWidthSizeClass] of the device we are running on. It is calculated
 * by a call to the [calculateWindowSizeClass] in the `onCreate` override of [MainActivity].
 */
@Composable
fun JetnewsApp(
    appContainer: AppContainer,
    widthSizeClass: WindowWidthSizeClass,
) {
    JetnewsTheme {
        val navController: NavHostController = rememberNavController()
        val navigationActions: JetnewsNavigationActions = remember(key1 = navController) {
            JetnewsNavigationActions(navController = navController)
        }

        val coroutineScope: CoroutineScope = rememberCoroutineScope()

        val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
        val currentRoute: String =
            navBackStackEntry?.destination?.route ?: JetnewsDestinations.HOME_ROUTE

        val isExpandedScreen: Boolean = widthSizeClass == WindowWidthSizeClass.Expanded
        val sizeAwareDrawerState: DrawerState =
            rememberSizeAwareDrawerState(isExpandedScreen = isExpandedScreen)

        ModalNavigationDrawer(
            drawerContent = {
                AppDrawer(
                    currentRoute = currentRoute,
                    navigateToHome = navigationActions.navigateToHome,
                    navigateToInterests = navigationActions.navigateToInterests,
                    closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } }
                )
            },
            drawerState = sizeAwareDrawerState,
            // Only enable opening the drawer via gestures if the screen is not expanded
            gesturesEnabled = !isExpandedScreen
        ) {
            Row {
                if (isExpandedScreen) {
                    AppNavRail(
                        currentRoute = currentRoute,
                        navigateToHome = navigationActions.navigateToHome,
                        navigateToInterests = navigationActions.navigateToInterests,
                    )
                }
                JetnewsNavGraph(
                    appContainer = appContainer,
                    isExpandedScreen = isExpandedScreen,
                    navController = navController,
                    openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                )
            }
        }
    }
}

/**
 * Determine the drawer state to pass to the modal drawer.
 */
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // If we want to allow showing the drawer, we use a real, remembered drawer
        // state defined above
        drawerState
    } else {
        // If we don't want to allow the drawer to be shown, we provide a drawer state
        // that is locked closed. This is intentionally not remembered, because we
        // don't want to keep track of any changes and always keep it closed
        DrawerState(initialValue = DrawerValue.Closed)
    }
}
