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
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.rally.data.Account
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.accounts.SingleAccountScreen
import com.example.compose.rally.ui.bills.BillsScreen
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.components.RallyTab
import com.example.compose.rally.ui.overview.OverviewScreen
import com.example.compose.rally.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call the [setContent] method to have it compose [RallyApp] into the activity. [RallyApp]
     * will become the root view of the activity.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
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

/**
 * This is the [NavHost] that is used as the `content` of the [Scaffold] of [RallyApp] and which is
 * used to navigate between the [OverviewScreen] (for the `route` [Overview.route]), [AccountsScreen]
 * (for the `route` [Accounts.route]), [BillsScreen] (for the `route` [Bills.route]), or the
 * [SingleAccountScreen] (for the `route` [SingleAccount.routeWithArgs]). Our root Composable is a
 * [NavHost] whose `navController` argument is our [NavHostController] parameter [navController],
 * whose `startDestination` argument is [Overview.route], and whose `modifier` argument is our
 * [Modifier] parameter [modifier]. The `builder` argument of the [NavHost] is a [NavGraphBuilder]
 * that uses the [NavGraphBuilder.composable] method to add [NavDestination]'s to the [NavGraph]
 * for the routes [Overview.route] (composes an [OverviewScreen]), [Accounts.route] (composes an
 * [AccountsScreen]), [Bills.route] (composes a [BillsScreen]), and [SingleAccount.routeWithArgs]
 * (composes a [SingleAccountScreen]).
 *
 * @param navController the [NavHostController] to be used by our [NavHost].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [RallyApp] passes us a [Modifier.padding] that adds the [PaddingValues] passed to the
 * `content` of the [Scaffold] we are composed in to the all sides of our Composable.
 */
@Composable
fun RallyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Overview.route,
        modifier = modifier
    ) {
        composable(route = Overview.route) {
            OverviewScreen(
                onClickSeeAllAccounts = {
                    navController.navigateSingleTopTo(route = Accounts.route)
                },
                onClickSeeAllBills = {
                    navController.navigateSingleTopTo(route = Bills.route)
                },
                onAccountClick = { accountType ->
                    navController.navigateToSingleAccount(accountType = accountType)
                }
            )
        }
        composable(route = Accounts.route) {
            AccountsScreen(
                onAccountClick = { accountType ->
                    navController.navigateToSingleAccount(accountType = accountType)
                }
            )
        }
        composable(route = Bills.route) {
            BillsScreen()
        }
        composable(
            route = SingleAccount.routeWithArgs,
            arguments = SingleAccount.arguments,
            deepLinks = SingleAccount.deepLinks
        ) { navBackStackEntry: NavBackStackEntry ->
            val accountType =
                navBackStackEntry.arguments?.getString(SingleAccount.accountTypeArg)
            SingleAccountScreen(accountType = accountType)
        }
    }
}

/**
 * This extension function calls the [NavHostController.navigate] method of its [NavHostController]
 * receiver with its `route` argument our [String] parameter [route], and its [NavOptionsBuilder]
 * `builder` argument a lambda which sets the [NavOptionsBuilder.launchSingleTop] option to `true`
 * (Whether this navigation action should launch as single-top i.e., there will be at most one copy
 * of a given destination on the top of the back stack).
 *
 * @param route the [String] route for the destination we are to navigate to.
 */
fun NavHostController.navigateSingleTopTo(route: String): Unit =
    this.navigate(route = route) { launchSingleTop = true }

/**
 * This extension function calls the [NavHostController.navigateToSingleAccount] extension method of
 * its [NavHostController] receiver with its `route` argument our [String] parameter [accountType]
 * appended to the [String] route [SingleAccount.route] separated by a "/" character.
 *
 * @param accountType the [Account.name] of the [Account] we are to have [SingleAccountScreen]
 * display.
 */
fun NavHostController.navigateToSingleAccount(accountType: String) {
    this.navigateSingleTopTo(route = "${SingleAccount.route}/$accountType")
}
