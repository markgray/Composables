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

package com.example.compose.rally.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.rally.R
import com.example.compose.rally.RallyScreen
import com.example.compose.rally.data.Account
import com.example.compose.rally.data.Bill
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.components.AccountRow
import com.example.compose.rally.ui.components.BillRow
import com.example.compose.rally.ui.components.RallyAlertDialog
import com.example.compose.rally.ui.components.RallyDivider
import com.example.compose.rally.ui.components.formatAmount
import java.util.Locale

/**
 * The main content of the Overview screen.
 *
 * Our root composable is a [Column] whose `modifier` argument is a [Modifier.padding] that adds
 * `16.dp` padding to all sides, chained to a [Modifier.verticalScroll] whose `state` argument is
 * a new instance of [rememberScrollState]. In its [ColumnScope] `content` composable lambda argument
 * we compose:
 *
 * **First** an [AlertCard]
 *
 * **Second** a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` argument
 * is [RallyDefaultPadding] (`16.dp`).
 *
 * **Third** an [AccountsCard] whose `onScreenChange` argument is our [onScreenChange] lambda
 * paramter.
 *
 * **Fourth** a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height` argument
 * is [RallyDefaultPadding] (`16.dp`).
 *
 * **Fifth** a [BillsCard] whose `onScreenChange` argument is our [onScreenChange] lambda
 * parameter.
 *
 * @param onScreenChange (event) request navigation to a new Rally screen
 */
@Composable
fun OverviewBody(onScreenChange: (RallyScreen) -> Unit = {}) {
    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        AlertCard()
        Spacer(modifier = Modifier.height(height = RallyDefaultPadding))
        AccountsCard(onScreenChange = onScreenChange)
        Spacer(modifier = Modifier.height(height = RallyDefaultPadding))
        BillsCard(onScreenChange = onScreenChange)
    }
}

/**
 * The Alerts card within the Rally Overview screen.
 *
 * We start by initializing and remembering our [MutableState] wrapped [Boolean] variable
 * `var showDialog` to `false`. We initialize our [String] constant `val alertMessage` to the
 * "Heads up, you've used up 90% of your Shopping budget for this month."
 *
 * If our [Boolean] variable `showDialog` is `true` we compose a [RallyAlertDialog] whose arguments
 * are:
 *  - `onDismiss`: is a lambda that sets our [MutableState] wrapped [Boolean] variable `showDialog`
 *  to `false`.
 *  - `bodyText`: is our [String] constant `alertMessage`.
 *  - `buttonText`: is the [String] "Dismiss" converted to uppercase using [Locale.getDefault]
 *  as the [Locale].
 *
 * In any case we compose a [Card] whose `content` composable lambda argument is a [Column] in
 * whose [ColumnScope] `content` composable lambda argument we compose:
 *
 * **First** an [AlertHeader] whose `onClickSeeAll` argument is a lambda that sets our [MutableState]
 * wrapped [Boolean] variable `showDialog` to `true`.
 *
 * **Second** a [RallyDivider] whose `modifier` argument is a [Modifier.padding] that adds
 * [RallyDefaultPadding] (`12.dp`) to the `start` and `end` of the [RallyDivider].
 *
 * **Third** an [AlertItem] whose `message` argument is our [String] constant `alertMessage`.
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
 * This composable function creates the header for the "Alerts" card.
 *
 * Our root composable is a [Row] whose `modifier` argument is a [Modifier.padding] that adds
 * [RallyDefaultPadding] to `all` sides, chained to a [Modifier.fillMaxWidth]. In its [RowScope]
 * `content` composable lambda argument we compose:
 *
 * **First** a [Text] whose arguments are:
 *  - `text`: is the [String] "Alerts".
 *  - `style`: is the [Typography.subtitle2] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [RowScope.align] that aligns the [Text] to the [Alignment.CenterVertically].
 *
 * **Second** a [TextButton] whose arguments are:
 *  - `onClick`: is our [onClickSeeAll] lambda parameter.
 *  - `contentPadding`: is a new instance of [PaddingValues] that adds `0.dp` to `all` sides.
 *  - `modifier`: is a [RowScope.align] that aligns the [TextButton] to the [Alignment.CenterVertically].
 *
 * In the [RowScope] `content` composable lambda argument of the [TextButton] we compose a [Text]
 * whose arguments are:
 *  - `text`: is the [String] "SEE ALL".
 *  - `style`: is the [Typography.button] of our custom [MaterialTheme.typography].
 *
 * @param onClickSeeAll A lambda function that will be executed when the "SEE ALL" button is clicked.
 */
@Composable
private fun AlertHeader(onClickSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(all = RallyDefaultPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Alerts",
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        TextButton(
            onClick = onClickSeeAll,
            contentPadding = PaddingValues(all = 0.dp),
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        ) {
            Text(
                text = "SEE ALL",
                style = MaterialTheme.typography.button,
            )
        }
    }
}

/**
 * This composable function creates an item for the "Alerts" card.
 *
 * Our root composable is a [Row] whose `modifier` argument is a [Modifier.padding] that adds
 * [RallyDefaultPadding] to `all` sides, chained to a [Modifier.semantics] whose
 * [SemanticsPropertyReceiver] `properties` argument is a lambda that sets the `mergeDescendants`
 * to `true` to regard the whole [Row] as one semantics node. In the [RowScope] `content` composable
 * lambda argument of the [Row] we compose:
 *
 * **First** a [Text] whose arguments are:
 *  - `style`: is the [Typography.body2] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [RowScope.align] whose `alignment` argument is [Alignment.CenterVertically],
 *  chained to a [RowScope.weight] whose `weight` argument is `1f`.
 *  - `text`: is our [String] parameter [message].
 *
 * **Second** an [IconButton] whose arguments are:
 *  - `onClick`: is a lambda that does nothing.
 *  - `modifier`: is a [RowScope.align] whose `alignment` argument is [Alignment.Top], chained to
 *  [Modifier.clearAndSetSemantics] that clears any semantics applied to the [IconButton].
 *
 * In the `content` composable lambda argument of the [IconButton] we compose an [Icon] whose
 * arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.AutoMirrored.Filled.Sort].
 *  - `contentDescription`: is `null`.
 *
 * @param message The message [String] to display in our [Text] composable.
 */
@Suppress("SameParameterValue")
@Composable
private fun AlertItem(message: String) {
    Row(
        modifier = Modifier
            .padding(all = RallyDefaultPadding)
            // Regard the whole row as one semantics node. This way each row will receive focus as
            // a whole and the focus bounds will be around the whole row content. The semantics
            // properties of the descendants will be merged. If we'd use clearAndSetSemantics instead,
            // we'd have to define the semantics properties explicitly.
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(weight = 1f),
            text = message
        )
        IconButton(
            onClick = {},
            modifier = Modifier
                .align(alignment = Alignment.Top)
                .clearAndSetSemantics {}
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = null)
        }
    }
}

/**
 * Base structure for cards in the Overview screen. Generic composable function that displays a card
 * with a title, an amount, a list of items, and a "SEE ALL" button.
 *
 * Our root composable is a [Card] whose `content` composable lambda argument constains a [Column]
 * in whose [ColumnScope] `content` composable lambda argument we compose:
 *
 * **First:** an inner [Column] whose `modifier` argument is a [Modifier.padding] that adds
 * [RallyDefaultPadding] to `all` sides, and in its [ColumnScope] `content` composable lambda
 * argument we first compose a [Text] whose `text` argument is our [String] parameter [title],
 * and whose `style` argument is the [Typography.subtitle2] of our custom [MaterialTheme.typography].
 * We then initialize our [String] variable `val amountText` to the "$" character followed by the
 * [String] returned by the [formatAmount] method whose `amount` argument is our [Float] parameter
 * [amount]. We then compose a [Text] whose `text` argument is our [String] variable `amountText`,
 * and whose `style` argument is the [Typography.h2] of our custom [MaterialTheme.typography].
 *
 * **Second:** an [OverViewDivider] whose `data` argument is our [List] parameter [data],
 * whose `values` argument is our lambda parameter [values], and whose `colors` argument is our
 * lambda parameter [colors].
 *
 * **Third:** an inner [Column] whose `modifier` argument is a [Modifier.padding] that adds
 * `16.dp` padding to the `start`, `4.dp` padding to the `top`, and `8.dp` padding to the `end`.
 * In the [ColumnScope] `content` composable lambda argument we first loop over the first [SHOWN_ITEMS]
 * in our [List] of [T] parameter [data] using the [Iterable.forEach] method capturing the current
 * [T] in variable `it` then compose a [row] of that [T]. At the bottom we then compose a
 * [SeeAllButton] whose `onClick` argument is our [onClickSeeAll] lambda parameter.
 *
 * @param title The title to display in the card.
 * @param amount The amount to display in the card.
 * @param onClickSeeAll A lambda function that will be executed when the "SEE ALL" button is clicked.
 * @param values A lambda function that takes a single [T] parameter and returns a [Float].
 * @param colors A lambda function that takes a single [T] parameter and returns a [Color].
 * @param data The [List] of [T] items to display in the card.
 * @param row A lambda function that takes a single [T] parameter and returns a [Composable].
 * @param T The type of the items in the [List] either [Account]s or [Bill]s.
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
            Column(modifier = Modifier.padding(all = RallyDefaultPadding)) {
                Text(text = title, style = MaterialTheme.typography.subtitle2)
                val amountText: String = "$" + formatAmount(
                    amount = amount
                )
                Text(text = amountText, style = MaterialTheme.typography.h2)
            }
            OverViewDivider(data = data, values = values, colors = colors)
            Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 8.dp)) {
                data.take(SHOWN_ITEMS).forEach { row(it) }
                SeeAllButton(onClick = onClickSeeAll)
            }
        }
    }
}

/**
 * This composable function creates a row of colored [Spacer] views that are used as a divider
 * in the [OverviewScreenCard] composable.
 *
 * Our root composable is a [Row] whose `modifier` argument is a [Modifier.fillMaxWidth].
 * In its [RowScope] `content` composable lambda argument we loop over our [List] of [T] parameter
 * [data] using the [Iterable.forEach] method capturing the current [T] in variable `accountOrBill`
 * then we compose a [Spacer] whose `modifier` argument is a [RowScope.weight] whose `weight` argument
 * is the [Float] returned by our [values] lambda parameter for our [T] variable `accountOrBill`,
 * chained to a [Modifier.height] whose `height` argument is `1.dp`, chained to a [Modifier.background]
 * whose `color` argument is the [Color] returned by our [colors] lambda parameter for our
 * [T] variable `accountOrBill`.
 *
 * @param data The [List] of [T] objects that we are to iterate over to produce the colored
 * [Spacer] views.
 * @param values A lambda that takes an object of type [T] and returns the [Float] that should be
 * used for the `weight` of the [Spacer] that displays information about that object.
 * @param colors A lambda that takes an object of type [T] and returns the [Color] that should be
 * used for the background `color` of the [Spacer] that displays information about that object.
 */
@Composable
private fun <T> OverViewDivider(
    data: List<T>,
    values: (T) -> Float,
    colors: (T) -> Color
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        data.forEach { accountOrBill: T ->
            Spacer(
                modifier = Modifier
                    .weight(weight = values(accountOrBill))
                    .height(height = 1.dp)
                    .background(color = colors(accountOrBill))
            )
        }
    }
}

/**
 * The Accounts card within the Rally Overview screen. This composable function creates a card that
 * displays information about the user's accounts.
 *
 * We start by initializing our [Float] variable `val amount` to the sum of the [Float] values
 * in the [Account.balance] property of all of the [Account]s in [UserData.accounts]. Then we
 * compose an [OverviewScreenCard] whose arguments are:
 *  - `title`: is the [String] whose resource ID is `R.string.accounts` ("Accounts").
 *  - `amount`: is our [Float] variable `amount`.
 *  - `onClickSeeAll`: is a lambda that calls our [onScreenChange] lambda parameter with the
 *  [RallyScreen.Accounts] screen as the destination.
 *  - `data`: is the [List] of [Account]s in [UserData.accounts].
 *  - `colors`: is a lambda that takes an [Account] and returns its [Account.color] property.
 *  - `values`: is a lambda that takes an [Account] and returns its [Account.balance] property.
 *
 * In the `row` lambda argument of the [OverviewScreenCard] we accept the [Account] passed the lambda
 * in variable `account` then we compose an [AccountRow] whose arguments are:
 *  - `name`: is the [String] returned by the [Account.name] property of our [Account] variable
 *  `account`.
 *  - `number`: is the [String] returned by the [Account.number] property of our [Account] variable
 *  `account`.
 *  - `amount`: is the [Float] returned by the [Account.balance] property of our [Account] variable
 *  `account`.
 *  - `color`: is the [Color] returned by the [Account.color] property of our [Account] variable
 *
 *
 * @param onScreenChange A lambda function that will be executed when the "SEE ALL" button is
 * clicked, it is called with the [RallyScreen] that we want to navigate to.
 */
@Composable
fun AccountsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount: Float = UserData.accounts.map { account: Account -> account.balance }.sum()
    OverviewScreenCard(
        title = stringResource(id = R.string.accounts),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(RallyScreen.Accounts)
        },
        data = UserData.accounts,
        colors = { it.color },
        values = { it.balance }
    ) { account: Account ->
        AccountRow(
            name = account.name,
            number = account.number,
            amount = account.balance,
            color = account.color
        )
    }
}

/**
 * The Bills card within the Rally Overview screen.
 *
 * We start by initializing our [Float] variable `val amount` to the sum of the [Float] values
 * in the [Bill.amount] property of all of the [Bill]s in [UserData.bills]. Then we compose an
 * [OverviewScreenCard] whose arguments are:
 *  - `title`: is the [String] whose resource ID is `R.string.bills` ("Bills").
 *  - `amount`: is our [Float] variable `amount`.
 *  - `onClickSeeAll`: is a lambda that calls our [onScreenChange] lambda parameter with the
 *  [RallyScreen.Bills] screen as the destination.
 *  - `data`: is the [List] of [Bill]s in [UserData.bills].
 *  - `colors`: is a lambda that takes a [Bill] and returns its [Bill.color] property.
 *  - `values`: is a lambda that takes a [Bill] and returns its [Bill.amount] property.
 *
 * In the `row` composable lambda argument of the [OverviewScreenCard] we accept the [Bill] passed
 * the lambda in variable `bill` then we compose a [BillRow] whose arguments are:
 *  - `name`: is the [String] returned by the [Bill.name] property of our [Bill] variable `bill`.
 *  - `due`: is the [String] returned by the [Bill.due] property of our [Bill] variable `bill`.
 *  - `amount`: is the [Float] returned by the [Bill.amount] property of our [Bill] variable `bill`.
 *  - `color`: is the [Color] returned by the [Bill.color] property of our [Bill] variable `bill`.
 *
 * @param onScreenChange A lambda function that will be executed when the "SEE ALL" button is
 * clicked, it is called with the [RallyScreen] that we want to navigate to.
 */
@Composable
fun BillsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount: Float = UserData.bills.map { bill: Bill -> bill.amount }.sum()
    OverviewScreenCard(
        title = stringResource(id = R.string.bills),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(RallyScreen.Bills)
        },
        data = UserData.bills,
        colors = { it.color },
        values = { it.amount }
    ) { bill: Bill ->
        BillRow(
            name = bill.name,
            due = bill.due,
            amount = bill.amount,
            color = bill.color
        )
    }
}

/**
 * Displays a button that the user can click to see all the items in the list.
 *
 * Our root composable is a [TextButton] whose `onClick` argument is our [onClick] lambda parameter,
 * and whose `modifier` argument is a [Modifier.height] whose `height` argument is `44.dp`, chained
 * to a [Modifier.fillMaxWidth].
 *
 * In the [RowScope] `content` composable lambda argument of the [TextButton] we compose a [Text]
 * whose `text` argument is the [String] whose resource ID is `R.string.see_all` ("See All").
 *
 * @param onClick A lambda function that will be executed when the button is clicked.
 */
@Composable
private fun SeeAllButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .height(height = 44.dp)
            .fillMaxWidth()
    ) {
        Text(text = stringResource(id = R.string.see_all))
    }
}

/**
 * Default padding used by the Rally app.
 */
val RallyDefaultPadding: Dp = 12.dp

/**
 * Used by [OverviewScreenCard] to limit the number of items displayed from its list of items.
 */
private const val SHOWN_ITEMS = 3
