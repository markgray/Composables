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

package com.example.compose.rally.ui.accounts

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.compose.NavHost
import com.example.compose.rally.Accounts
import com.example.compose.rally.R
import com.example.compose.rally.RallyNavHost
import com.example.compose.rally.SingleAccount
import com.example.compose.rally.data.Account
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.components.AccountRow
import com.example.compose.rally.ui.components.StatementBody

/**
 * The Accounts screen. This is the Composable that is used for the [Accounts.route] `route` of
 * the [NavHost] used in [RallyNavHost]. We initialize and remember our variable `val amountsTotal`
 * by using the [map] extension function to loop through all of the [Account] instances in the [List]
 * of [Account] field [UserData.accounts] to produce of [List] of the [Account.balance] properties
 * of all of the [Account] instances and feed that [List] to the [sum] extension function to produce
 * a sum of the [List], which is then used as the `amountsTotal` argument of [StatementBody]. Then
 * we compose a [StatementBody] with its arguments:
 *  - `modifier` is a [Modifier.semantics] whose `contentDescription` argument is "Accounts Screen"
 *  (this is used to find the screen in our `rallyNavHost_clickAllAccount_navigatesToAccounts` test
 *  as well as by the accessibility framework).
 *  - `items` is the [List] of [Account] field [UserData.accounts].
 *  - `amounts` is a lambda which returns the [Account.balance] property of the [Account] passed it.
 *  - `colors` is a lambda which returns the [Account.color] property of the [Account] passed it.
 *  - `amountsTotal` is our `amountsTotal` variable.
 *  - `circleLabel` is the [String] whose resource ID is [R.string.total] ("Total").
 *  - `rows` is a a lambda which destructures its [Account] parameter into its [Account.name],
 *  [Account.number], [Account.balance], and [Account.color] properties and feeds them as the
 *  arguments of the same name to the [AccountRow] Composable. The `modifier` argument of the
 *  [AccountRow] is a [Modifier.clickable] which will call our [onAccountClick] parameter with the
 *  [Account.name] of the [Account].
 *
 * @param onAccountClick a lambda that we use as the `onClick` argument of the [Modifier.clickable]
 * that we use as the `modifier` argument of each of the [AccountRow]'s that we have [StatementBody]
 * compose for us. We are passed a lambda by [RallyNavHost] which calls the extension function
 * `NavHostController.navigateToSingleAccount` (see file `RallyActivity.kt`) with the [Account.name]
 * string passed it as the `route` argument which will cause the app to navigate to the route
 * "${SingleAccount.route}/$accountType" which displays the [SingleAccountScreen] for that
 * `accountType`.
 */
@Composable
fun AccountsScreen(
    onAccountClick: (String) -> Unit = {},
) {
    val amountsTotal: Float = remember { UserData.accounts.map { account -> account.balance }.sum() }
    StatementBody(
        modifier = Modifier.semantics { contentDescription = "Accounts Screen" },
        items = UserData.accounts,
        amounts = { account: Account -> account.balance },
        colors = { account: Account -> account.color },
        amountsTotal = amountsTotal,
        circleLabel = stringResource(R.string.total),
        rows = { (name: String, number: Int, balance: Float, color: Color) ->
            AccountRow(
                modifier = Modifier.clickable {
                    onAccountClick(name)
                },
                name = name,
                number = number,
                amount = balance,
                color = color
            )
        }
    )
}

/**
 * Detail screen for a single account. This is the destination of the route [SingleAccount.route]
 * with the `accountType` [String] argument appended to that route separated by a "/". We start by
 * initializing and remembering our [Account] variable `val account` using the [UserData.getAccount]
 * method on our [String] parameter [accountType] ([accountType] is used as the `key1` argument of
 * [remember] so that `account` will be recalculated if [accountType] changes value).
 *
 * @param accountType the [Account.name] of the [Account] we are to display in our [SingleAccountScreen].
 */
@Composable
fun SingleAccountScreen(
    accountType: String? = UserData.accounts.first().name
) {
    val account: Account = remember(accountType) { UserData.getAccount(accountName = accountType) }
    StatementBody(
        items = listOf(account),
        colors = { account.color },
        amounts = { account.balance },
        amountsTotal = account.balance,
        circleLabel = account.name,
    ) { (name: String, number: Int, balance: Float, color: Color) ->
        AccountRow(
            name = name,
            number = number,
            amount = balance,
            color = color
        )
    }
}
