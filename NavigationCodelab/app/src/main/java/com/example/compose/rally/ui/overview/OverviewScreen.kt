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

package com.example.compose.rally.ui.overview

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.compose.rally.Accounts
import com.example.compose.rally.Bills
import com.example.compose.rally.Overview
import com.example.compose.rally.RallyApp
import com.example.compose.rally.R
import com.example.compose.rally.RallyDestination
import com.example.compose.rally.data.Account
import com.example.compose.rally.data.UserData
import com.example.compose.rally.navigateSingleTopTo
import com.example.compose.rally.navigateToSingleAccount
import com.example.compose.rally.ui.accounts.SingleAccountScreen
import com.example.compose.rally.ui.components.AccountRow
import com.example.compose.rally.ui.components.BillRow
import com.example.compose.rally.ui.components.RallyAlertDialog
import com.example.compose.rally.ui.components.RallyDivider
import com.example.compose.rally.ui.components.RallyTab
import com.example.compose.rally.ui.components.formatAmount
import java.util.Locale

/**
 * This is the Composable that is navigated to for the `route` [Overview.route] and is the start
 * screen used as the `content` of the [Scaffold] of the app's [RallyApp] root Composable, as well
 * as the destination when the [RallyTab] associated with its [RallyDestination] is clicked. Our
 * root Composable ia a [Column] whose `modifier` argument is a [Modifier.padding] that adds 16.dp
 * to all sides of the [Column], to which is chained a [Modifier.verticalScroll] whose `state`
 * argument is the [ScrollState] constructed and remembered by [rememberScrollState] which Modifies
 * the [Column] to allow it to scroll vertically, and the end of the chain of [Modifier]'s is a
 * [Modifier.semantics] whose [contentDescription] is the [String] "Overview Screen" for the use
 * of the accessibility framework. The `content` of the [Column] is:
 *  - an [AlertCard] which displays the hard coded alert message: "Heads up, you've used up 90% of
 *  your Shopping budget for this month" and allows your to click on a [TextButton] labeled "SEE ALL"
 *  which will launch a [RallyAlertDialog] that displays the same message in an [AlertDialog].
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` is [RallyDefaultPadding]
 *  (12.dp).
 *  - an [AccountsCard] whose `onClickSeeAll` argument calls our [onClickSeeAllAccounts] lambda
 *  parameter, and whose `onAccountClick` argument calls our [onAccountClick] lambda parameter.
 *  - this is followed by another [RallyDefaultPadding] `height` [Spacer]
 *  - the end of the [Column] is a [BillsCard] whose `onClickSeeAll` argument is our [onClickSeeAllBills]
 *  parameter.
 *
 * @param onClickSeeAllAccounts this lambda is used as the `onClickSeeAll` argument of our [AccountsCard]
 * and in our case it is a lambda that calls the [navigateSingleTopTo] method of the [NavHostController]
 * used by the app with the `route` [Accounts.route].
 * @param onClickSeeAllBills this lambda is used as the `onClickSeeAll` argument of our [BillsCard]
 * and in our case it is a lambda that calls the [navigateSingleTopTo] method of the [NavHostController]
 * used by the app with the `route` [Bills.route].
 * @param onAccountClick this lambda is used as the `onAccountClick` argument of our [AccountsCard]
 * and in our case it is a lambda that calls the [navigateToSingleAccount] method of the
 * [NavHostController] used by the app with the `accountType` argument the [Account.name] of the
 * [Account] whose details we want the [SingleAccountScreen] to display.
 */
@Composable
fun OverviewScreen(
    onClickSeeAllAccounts: () -> Unit = {},
    onClickSeeAllBills: () -> Unit = {},
    onAccountClick: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .verticalScroll(state = rememberScrollState())
            .semantics { contentDescription = "Overview Screen" }
    ) {
        AlertCard()
        Spacer(modifier = Modifier.height(height = RallyDefaultPadding))
        AccountsCard(
            onClickSeeAll = onClickSeeAllAccounts,
            onAccountClick = onAccountClick
        )
        Spacer(modifier = Modifier.height(height = RallyDefaultPadding))
        BillsCard(
            onClickSeeAll = onClickSeeAllBills
        )
    }
}

/**
 * The Alerts card within the Rally Overview screen. We start by initializing and remembering our
 * [Boolean] variable `var showDialog`, and initializing our [String] variable `val alertMessage`
 * to the string "Heads up, you've used up 90% of your Shopping budget for this month." Then if
 * `showDialog` is `true` we compose a [RallyAlertDialog] whose `onDismiss` argument is a lambda
 * that sets `showDialog` to `false`, whose `bodyText` argument is our `alertMessage` [String]
 * variable, and whose `buttonText` argument is the uppercase version of the [String] "Dismiss".
 *
 * Then our root Composable is a [Card] whose `content` is a [Column] that holds a [AlertHeader]
 * whose `onClickSeeAll` lambda argument is a lambda that sets our `showDialog` variable to `true`.
 * This is followed in the [Column] by a [RallyDivider] whose `modifier` argument sets both the
 * `start` and `end` padding of the Composable to [RallyDefaultPadding] (12.dp), and this is
 * followed by an [AlertItem] whose `message` argument is our `alertMessage` variable.
 */
@Composable
fun AlertCard() {
    var showDialog: Boolean by remember { mutableStateOf(false) }
    val alertMessage = "Heads up, you've used up 90% of your Shopping budget for this month."

    if (showDialog) {
        RallyAlertDialog(
            onDismiss = {
                showDialog = false
            },
            bodyText = alertMessage,
            buttonText = "Dismiss".uppercase(Locale.getDefault())
        )
    }
    Card {
        Column {
            AlertHeader {
                showDialog = true
            }
            RallyDivider(
                modifier = Modifier.padding(start = RallyDefaultPadding, end = RallyDefaultPadding)
            )
            AlertItem(message = alertMessage)
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
fun AlertHeader(onClickSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(RallyDefaultPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Alerts",
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        TextButton(
            onClick = onClickSeeAll,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = "SEE ALL",
                style = MaterialTheme.typography.button,
            )
        }
    }
}

@Suppress("SameParameterValue") // Suggested change would make the Composable less reusable.
@Composable
private fun AlertItem(message: String) {
    Row(
        modifier = Modifier
            .padding(RallyDefaultPadding)
            // Regard the whole row as one semantics node. This way each row will receive focus as
            // a whole and the focus bounds will be around the whole row content. The semantics
            // properties of the descendants will be merged. If we'd use clearAndSetSemantics instead,
            // we'd have to define the semantics properties explicitly.
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f),
            text = message
        )
        IconButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.Top)
                .clearAndSetSemantics {}
        ) {
            Icon(Icons.Filled.Sort, contentDescription = null)
        }
    }
}

/**
 * Base structure for cards in the Overview screen.
 */
@Composable
private fun <T> OverviewScreenCard(
    title: String,
    amount: Float,
    onClickSeeAll: () -> Unit,
    values: (T) -> Float,
    colors: (T) -> Color,
    data: List<T>,
    row: @Composable (T) -> Unit
) {
    Card {
        Column {
            Column(Modifier.padding(RallyDefaultPadding)) {
                Text(text = title, style = MaterialTheme.typography.subtitle2)
                val amountText = "$" + formatAmount(
                    amount
                )
                Text(text = amountText, style = MaterialTheme.typography.h2)
            }
            OverViewDivider(data, values, colors)
            Column(Modifier.padding(start = 16.dp, top = 4.dp, end = 8.dp)) {
                data.take(SHOWN_ITEMS).forEach { row(it) }
                SeeAllButton(
                    modifier = Modifier.clearAndSetSemantics {
                        contentDescription = "All $title"
                    },
                    onClick = onClickSeeAll,
                )
            }
        }
    }
}

@Composable
private fun <T> OverViewDivider(
    data: List<T>,
    values: (T) -> Float,
    colors: (T) -> Color
) {
    Row(Modifier.fillMaxWidth()) {
        data.forEach { item: T ->
            Spacer(
                modifier = Modifier
                    .weight(values(item))
                    .height(1.dp)
                    .background(colors(item))
            )
        }
    }
}

/**
 * The Accounts card within the Rally Overview screen.
 */
@Composable
private fun AccountsCard(onClickSeeAll: () -> Unit, onAccountClick: (String) -> Unit) {
    val amount = UserData.accounts.map { account -> account.balance }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.accounts),
        amount = amount,
        onClickSeeAll = onClickSeeAll,
        data = UserData.accounts,
        colors = { it.color },
        values = { it.balance }
    ) { (name, number, balance, color) ->
        AccountRow(
            modifier = Modifier.clickable { onAccountClick(name) },
            name = name,
            number = number,
            amount = balance,
            color = color
        )
    }
}

/**
 * The Bills card within the Rally Overview screen.
 */
@Composable
private fun BillsCard(onClickSeeAll: () -> Unit) {
    val amount = UserData.bills.map { bill -> bill.amount }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.bills),
        amount = amount,
        onClickSeeAll = onClickSeeAll,
        data = UserData.bills,
        colors = { it.color },
        values = { it.amount }
    ) { (name, due, amount1, color) ->
        BillRow(
            name = name,
            due = due,
            amount = amount1,
            color = color
        )
    }
}

@Composable
private fun SeeAllButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .fillMaxWidth()
    ) {
        Text(stringResource(R.string.see_all))
    }
}

/**
 * The default passing used in several places.
 */
val RallyDefaultPadding: Dp = 12.dp

private const val SHOWN_ITEMS = 3
