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

package com.example.compose.rally.ui.bills

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.compose.rally.R
import com.example.compose.rally.data.Bill
import com.example.compose.rally.ui.components.BillRow
import com.example.compose.rally.ui.components.StatementBody

/**
 * The Bills screen. We just convert the fields of the [Bill] data class into the type of arguments
 * that the [StatementBody] Composable expects as its arguments and then call [StatementBody] with
 * them:
 *  - `accountsOrBills` [StatementBody] expects a [List] of `T` so we just pass it our [List] of
 *  [Bill] parameter [bills] (our `T` for this and all other arguments is a [Bill]).
 *  - `amounts` [StatementBody] expects a lambda that takes a `T` and returns a [Float] so we
 *  pass it a lambda that takes a [Bill] and returns the [Bill.amount] property of that [Bill].
 *  - `colors` [StatementBody] expects a lambda that takes a `T` and returns a [Color] so we
 *  pass it a lambda that takes a [Bill] and returns the [Bill.color] of that [Bill].
 *  - `amountsTotal` [StatementBody] expects a [Float] (derived from all of the [Bill] instances)
 *  so we use the [map] extension function to loop through all of the [Bill] instances in our
 *  [List] of [Bill] parameter [bills] to produce of [List] of the [Bill.amount] properties
 *  of all of the [Bill] instances and feed that [List] to the [sum] extension function to produce
 *  a sum of the [List] which is then used as the `amountsTotal` argument.
 *  - `circleLabel` [StatementBody] expects a [String] so we pass it the [String] with resource ID
 *  [R.string.due] ("Due").
 *  - `rows` [StatementBody] expects a lambda that takes a `T` and emits a Composable, so we pass it
 *  a lambda which destructures its [Bill] parameter into its [Bill.name], [Bill.due], [Bill.amount],
 *  and [Bill.color] properties and feeds them as the arguments of the same name to the [BillRow]
 *  Composable.
 *
 * @param bills the [List] of [Bill] instances that we are to display.
 *
 */
@Composable
fun BillsBody(bills: List<Bill>) {
    StatementBody(
        accountsOrBills = bills,
        amounts = { bill: Bill -> bill.amount },
        colors = { bill: Bill -> bill.color },
        amountsTotal = bills.map { bill: Bill -> bill.amount }.sum(),
        circleLabel = stringResource(R.string.due),
        rows = { (name: String, due: String, amount: Float, color: Color): Bill ->
            BillRow(
                name = name,
                due = due,
                amount = amount,
                color = color
            )
        }
    )
}
