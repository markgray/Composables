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

package com.example.compose.rally.data

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Text
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.components.AccountRow
import com.example.compose.rally.ui.components.AnimatedCircle
import com.example.compose.rally.ui.components.BillRow
import com.example.compose.rally.ui.components.StatementBody

/* Hard-coded data for the Rally sample. */

/**
 * This is the data class used to hold the fake data for each of the four `accounts` that are used
 * in the [UserData.accounts] list of [Account] instances which are then used by [AccountsBody] as
 * the data for each of the [AccountRow] Composables in the [StatementBody] of the [AccountsBody]
 * of the Accounts screen.
 *
 * @param name the name of the account, used as the `name` argument of the [AccountRow] Composable,
 * which is then used as the `title` argument of the `BaseRow` Composable which it displays as the
 * `text` argument of a [Text].
 * @param number the account "number", used as the `number` argument of the [AccountRow] Composable,
 * which is then formatted, and used as the `subtitle` argument of the `BaseRow` Composable which it
 * displays in a [Text].
 * @param balance the amount of money in the [Account].
 * @param color the [Color] that should be used for this [Account] by the [AnimatedCircle] used by
 * the `StatementBody`, as well as the `color` argument of the `AccountIndicator` which is just a
 * 4.dp by 36.dp [Spacer] which uses `color` as its background color.
 */
@Immutable
data class Account(
    val name: String,
    val number: Int,
    val balance: Float,
    val color: Color
)

/**
 * @param name the name of the [Bill], used as the `name` argument of the [BillRow] Composable,
 * which is then used as the `title` argument of the `BaseRow` Composable which it displays as the
 * `text` argument of a [Text].
 * @param due the due date of the [Bill] which is then formatted, and used as the `subtitle` argument
 * of the `BaseRow` Composable which it displays as the `text` argument of a [Text].
 * @param amount the amount of the [Bill] which is used as the `amount` argument of the [BillRow]
 * Composable which it then uses as the `amount` argument of the `BaseRow` Composable which it formats
 * and then displays as the `text` argument of a [Text].
 * @param color the [Color] that should be used for this [Bill] by the [AnimatedCircle] used by
 * the `StatementBody`, as well as the `color` argument of the `AccountIndicator` which is just a
 * 4.dp by 36.dp [Spacer] which uses `color` as its background color.
 */
@Immutable
data class Bill(
    val name: String,
    val due: String,
    val amount: Float,
    val color: Color
)

/**
 * The "actual" fake data used by our app.
 */
object UserData {
    /**
     * The [List] of [Account] instances used by our app.
     */
    val accounts: List<Account> = listOf(
        Account(
            "Checking",
            1234,
            2215.13f,
            Color(0xFF004940)
        ),
        Account(
            "Home Savings",
            5678,
            8676.88f,
            Color(0xFF005D57)
        ),
        Account(
            "Car Savings",
            9012,
            987.48f,
            Color(0xFF04B97F)
        ),
        Account(
            "Vacation",
            3456,
            253f,
            Color(0xFF37EFBA)
        )
    )

    /**
     * The [List] of [Bill] instances used by our app.
     */
    val bills: List<Bill> = listOf(
        Bill(
            "RedPay Credit",
            "Jan 29",
            45.36f,
            Color(0xFFFFDC78)
        ),
        Bill(
            "Rent",
            "Feb 9",
            1200f,
            Color(0xFFFF6951)
        ),
        Bill(
            "TabFine Credit",
            "Feb 22",
            87.33f,
            Color(0xFFFFD7D0)
        ),
        Bill(
            "ABC Loans",
            "Feb 29",
            400f,
            Color(0xFFFFAC12)
        ),
        Bill(
            "ABC Loans 2",
            "Feb 29",
            77.4f,
            Color(0xFFFFAC12)
        )
    )
}
