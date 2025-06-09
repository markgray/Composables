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

package com.example.compose.rally.ui.bills

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.compose.rally.R
import com.example.compose.rally.data.Bill
import com.example.compose.rally.ui.components.BillRow
import com.example.compose.rally.ui.components.StatementBody

/**
 * The Bills screen. Our root composable is a [StatementBody] whose arguments are:
 *  - `accountsOrBills`: Our [List] of [Bill] parameter [bills].
 *  - `amounts`: a lambda that accepts the [Bill] passed the lambda in variable `bill` and
 *  returns the [Bill.amount] of `bill`.
 *  - `colors`: a lambda that accepts the [Bill] passed the lambda in variable `bill` and
 *  returns the [Bill.color] of `bill`.
 *  - `amountsTotal`: it uses the [Iterable.map] function to loop through our [List] of [Bill]
 *  parameter [bills] and in its `transform` lambda argument accepts each [Bill] in variable
 *  `bill` producing a [List] of [Float] which is fed to the [Iterable.sum] function which returns
 *  the sum of the [Float]s in the [List].
 *  - `circleLabel`: is the [String] with resource ID `R.string.due` ("Due")
 *  - `rows`: a lambda that accepts the [Bill] passed the lambda in variable `bill` and composes
 *  a [BillRow] whose `name` argument is the [Bill.name] of `bill`, whose `due` argument is the
 *  [Bill.due] of `bill`, whose `amount` argument is the [Bill.amount] of `bill`, and whose
 *  and whose `color` argument is the [Bill.color] of `bill`
 *
 * @param bills The list of [Bill]s to display.
 */
@Composable
fun BillsBody(bills: List<Bill>) {
    StatementBody(
        accountsOrBills = bills,
        amounts = { bill: Bill -> bill.amount },
        colors = { bill: Bill -> bill.color },
        amountsTotal = bills.map { bill: Bill -> bill.amount }.sum(),
        circleLabel = stringResource(id = R.string.due),
        rows = { bill: Bill ->
            BillRow(
                name = bill.name,
                due = bill.due,
                amount = bill.amount,
                color = bill.color
            )
        }
    )
}
