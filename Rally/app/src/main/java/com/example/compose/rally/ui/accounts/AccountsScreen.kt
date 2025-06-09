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

package com.example.compose.rally.ui.accounts

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.compose.rally.R
import com.example.compose.rally.data.Account
import com.example.compose.rally.ui.components.AccountRow
import com.example.compose.rally.ui.components.StatementBody

/**
 * The Accounts screen. Our root composable is a [StatementBody] whose arguments are:
 *  - `accountsOrBills`: The [List] of [Account]s to display.
 *  - `amounts`: a lambda that accepts the [Account] passed the lambda in variable `account` and
 *  returns the [Account.balance] of `account`.
 *  - `colors`: a lambda that accepts the [Account] passed the lambda in variable `account` and
 *  returns the [Account.color] of `account`.
 *  - `amountsTotal`: it uses the [Iterable.map] function to loop through our [List] of [Account]
 *  parameter [accounts] and in its `transform` lambda argument accepts each [Account] in variable
 *  `account` producing a [List] of [Float] which is fed to the [Iterable.sum] function which returns
 *  the sum of the [Float]s in the [List].
 *  - `circleLabel`: is the [String] with resource ID [R.string.total] ("Total").
 *  - `rows`: a lambda that accepts the [Account] passed the lambda in variable `account` and composes
 *  an [AccountRow] whose `name` argument is the [Account.name] of `account`, whose `number` argument
 *  is the [Account.number] of `account`, whose `amount` argument is the [Account.balance] of `account`,
 *  and whose `color` argument is the [Account.color] of `account`.
 *
 * @param accounts The [List] of [Account] to display.
 */
@Composable
fun AccountsBody(accounts: List<Account>) {
    StatementBody(
        accountsOrBills = accounts,
        amounts = { account: Account -> account.balance },
        colors = { account: Account -> account.color },
        amountsTotal = accounts.map { account: Account -> account.balance }.sum(),
        circleLabel = stringResource(id = R.string.total),
        rows = { account: Account ->
            AccountRow(
                name = account.name,
                number = account.number,
                amount = account.balance,
                color = account.color
            )
        }
    )
}
