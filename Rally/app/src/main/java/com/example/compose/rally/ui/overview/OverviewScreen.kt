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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
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
 * TODO: Continue here.
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
            Column(modifier = Modifier.padding(all = RallyDefaultPadding)) {
                Text(text = title, style = MaterialTheme.typography.subtitle2)
                val amountText: String = "$" + formatAmount(
                    amount
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

@Composable
private fun <T> OverViewDivider(
    data: List<T>,
    values: (T) -> Float,
    colors: (T) -> Color
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        data.forEach { item: T ->
            Spacer(
                modifier = Modifier
                    .weight(weight = values(item))
                    .height(height = 1.dp)
                    .background(color = colors(item))
            )
        }
    }
}

/**
 * The Accounts card within the Rally Overview screen.
 */
@Composable
fun AccountsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount: Float = UserData.accounts.map { account: Account -> account.balance }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.accounts),
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

val RallyDefaultPadding: Dp = 12.dp

private const val SHOWN_ITEMS = 3
