package com.example.compose.rally

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.compose.rally.data.Account
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.accounts.SingleAccountScreen
import com.example.compose.rally.ui.bills.BillsScreen
import com.example.compose.rally.ui.overview.OverviewScreen

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
 * `builder` argument a lambda begins by calling the [NavOptionsBuilder.popUpTo] method to Pop up to
 * the start destination of the graph to avoid building up a large stack of destinations, the lambda
 * of the `popUpTo` call sets `saveState` to `true` to have the back stack and the state of all
 * destinations between the current destination and the [NavOptionsBuilder.popUpTo] ID should be
 * saved for later restoration, then the `Builder` sets the [NavOptionsBuilder.launchSingleTop]
 * option to `true` (Whether this navigation action should launch as single-top i.e., there will be
 * at most one copy of a given destination on the top of the back stack), and sets `restoreState` to
 * `true` (Restores state when reselecting a previously selected item).
 *
 * @param route the [String] route for the destination we are to navigate to.
 */
fun NavHostController.navigateSingleTopTo(route: String): Unit =
    this.navigate(route = route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

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
