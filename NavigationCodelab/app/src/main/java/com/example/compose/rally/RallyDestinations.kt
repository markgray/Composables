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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.accounts.SingleAccountScreen
import com.example.compose.rally.ui.bills.BillsScreen
import com.example.compose.rally.ui.components.RallyTab
import com.example.compose.rally.ui.overview.OverviewScreen

/**
 * Contract for information needed on every Rally navigation destination.
 */
interface RallyDestination {
    /**
     * This is the [ImageVector] that should be displayed by the [RallyTab] associated with the
     * [RallyDestination].
     */
    val icon: ImageVector

    /**
     * This is the route for the destination of the [RallyDestination].
     */
    val route: String
}

/*
 * Rally app navigation destinations
 */

/**
 * This is the [RallyDestination] for navigating to the [OverviewScreen] Composable.
 */
object Overview : RallyDestination {
    override val icon: ImageVector = Icons.Filled.PieChart
    override val route: String = "overview"
}

/**
 * This is the [RallyDestination] for navigating to the [AccountsScreen] Composable.
 */
object Accounts : RallyDestination {
    override val icon: ImageVector = Icons.Filled.AttachMoney
    override val route: String = "accounts"
}

/**
 * This is the [RallyDestination] for navigating to the [BillsScreen] Composable.
 */
object Bills : RallyDestination {
    override val icon: ImageVector = Icons.Filled.MoneyOff
    override val route: String = "bills"
}

/**
 * This is the [RallyDestination] for navigating to the [SingleAccountScreen] Composable.
 */
object SingleAccount : RallyDestination {
    // Added for simplicity, this icon will not in fact be used, as SingleAccount isn't
    // part of the RallyTabRow selection
    override val icon: ImageVector = Icons.Filled.Money
    override val route: String = "single_account"

    /**
     * This is the key under which the `accountType` is stored in the arguments for navigating to
     * the [SingleAccountScreen] Composable.
     */
    const val accountTypeArg: String = "account_type"

    /**
     * This is the `route` for the [NavDestination] of [SingleAccount.routeWithArgs], and specifies
     * that the key of the argument of the `route` will be stored under the [accountTypeArg] key in
     * the arguments passed in the [NavBackStackEntry.arguments] passed to [SingleAccountScreen].
     */
    val routeWithArgs: String = "${route}/{${accountTypeArg}}"

    /**
     * TODO: Add kdoc
     */
    val arguments: List<NamedNavArgument> = listOf(
        navArgument(accountTypeArg) { type = NavType.StringType }
    )

    /**
     * TODO: Add kdoc
     */
    val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "rally://$route/{$accountTypeArg}" }
    )

}

/**
 * Screens to be displayed in the top RallyTabRow
 */
val rallyTabRowScreens: List<RallyDestination> = listOf(Overview, Accounts, Bills)
