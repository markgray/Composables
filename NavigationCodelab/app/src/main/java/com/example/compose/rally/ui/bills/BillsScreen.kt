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

package com.example.compose.rally.ui.bills

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.navigation.compose.NavHost
import com.example.compose.rally.Bills
import com.example.compose.rally.R
import com.example.compose.rally.RallyNavHost
import com.example.compose.rally.data.Bill
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.components.BillRow
import com.example.compose.rally.ui.components.StatementBody

/**
 * The Bills screen. This is the Composable that is used for the [Bills.route] `route` of the
 * [NavHost] used in [RallyNavHost]. Our root Composable is a [StatementBody] whose `modifier`
 * argument is a [Modifier.clearAndSetSemantics] which sets the `contentDescription` to the
 * [String] "Bills" for screen-readers and testing, its `items` argument is our [List] of [Bill]
 * parameter [bills] (which is the default remember'ed pointer to [UserData.bills] since we are
 * not passed it by [RallyNavHost]), its `amounts` argument is a lambda which returns the
 * [Bill.amount] of the [Bill] passed it, its `colors` argument is a lambda which returns the
 * [Bill.color] of the [Bill] passed it, its `amountsTotal` argument is calculated by using the
 * [map] extension function to extract the [Bill.amount] of all the [Bill] objects in [bills]
 * into a list of [Float] and feeding that list to the [sum] extension function to have it sum them
 * and return the total, its `circleLabel` argument is the [String] with resource ID [R.string.due]
 * ("Due") and its `rows` lambda destructures the [Bill] passed it into `name`: [String], `due`:
 * [String], `amount`: [Float], and `color`: [Color] then composes a [BillRow] whose `name` argument
 * is the [Bill.name], whose `due` argument is the [Bill.due], whose `amount` argument is the
 * [Bill.amount], and whose `color` argument is the [Bill.color] of the [Bill] passed the lambda.
 *
 * @param bills the [List] of [Bill] that our [BillsScreen] is to have [StatementBody] display.
 * [RallyNavHost] does not pass us any so the default remember'ed pointer to [UserData.bills]
 * is used instead.
 */
@Composable
fun BillsScreen(
    bills: List<Bill> = remember { UserData.bills }
) {
    StatementBody(
        modifier = Modifier.clearAndSetSemantics { contentDescription = "Bills" },
        accountsOrBills = bills,
        amounts = { bill: Bill -> bill.amount },
        colors = { bill: Bill -> bill.color },
        amountsTotal = bills.map { bill: Bill -> bill.amount }.sum(),
        circleLabel = stringResource(R.string.due),
        rows = { (name: String, due: String, amount: Float, color: Color) ->
            BillRow(name = name, due = due, amount = amount, color = color)
        }
    )
}
