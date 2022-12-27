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

package com.example.compose.rally.ui.accounts

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.compose.rally.R
import com.example.compose.rally.data.Account
import com.example.compose.rally.ui.components.AccountRow
import com.example.compose.rally.ui.components.StatementBody

/**
 * The Accounts screen. We just convert the field definitions of the [Account] data class into the
 * the type of arguments that the [StatementBody] Composable expects as its arguments and then call
 * [StatementBody] with them:
 *  - `items` [StatementBody] expects a [List] of `T` so we just pass it our [List] of [Account]
 *  parameter [accounts] (our `T` for this and all other arguments is an [Account]).
 *  - `amounts` [StatementBody] expects a lambda that takes a `T` and returns a [Float] so we
 *  pass it a lambda that takes an [Account] and returns the [Account.balance] property of that
 *  [Account]
 *  - `colors` [StatementBody] expects a lambda that takes a `T` and returns a [Color] so we
 *  pass it a lambda that takes an [Account] and returns the [Account.color] property of that
 *  [Account]
 *  - `amountsTotal` [StatementBody] expects a [Float] (derived from all of the [Account] instances)
 *  so we use the [map] extension function to loop through all of the [Account] instances in our
 *  [List] of [Account] parameter [accounts] to produce of [List] of the [Account.balance] properties
 *  of all of the [Account] instances and feed that [List] to the [sum] extension function to produce
 *  a sum of the [List] which is then used as the `amountsTotal` argument.
 *  - `row` [StatementBody] expects a lambda that takes a `T` and emits a Composable, so we pass it
 *  a lambda which destructures its [Account] into its [Account.name], [Account.number], [Account.balance],
 *  and [Account.color] properties and feeds them as the arguments of the same name to the [AccountRow]
 *  Composable.
 *
 * @param accounts the [List] of [Account] instances that we are to display.
 */
@Composable
fun AccountsBody(accounts: List<Account>) {
    StatementBody(
        items = accounts,
        amounts = { account: Account -> account.balance },
        colors = { account: Account -> account.color },
        amountsTotal = accounts.map { account: Account -> account.balance }.sum(),
        circleLabel = stringResource(R.string.total),
        rows = { (name: String, number: Int, balance: Float, color: Color): Account ->
            AccountRow(
                name = name,
                number = number,
                amount = balance,
                color = color
            )
        }
    )
}
