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

package com.example.compose.rally.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose.rally.data.Account
import com.example.compose.rally.data.Bill
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.bills.BillsBody

/**
 * Generic component used by the accounts and bills screens to show a chart and a list of items. Our
 * root Composable is a [Column] whose `modifier` argument is a [Modifier.verticalScroll] whose `state`
 * argument is the [ScrollState] constructed and remembered by [rememberScrollState] (this [Modifier]
 * modifies the element to allow it to scroll vertically when the height of the content is bigger than
 * max constraints allow). The `content` of the [Column]
 *
 * @param items either a [List] of [Account] objects when we are used by [AccountsBody] or a [List]
 * of [Bill] objects when we are used by [BillsBody]
 * @param colors a lambda which tales an [Account] or [Bill] instance and returns a [Color]. This
 * will return the [Account.color] property of an [Account] when we are used by [AccountsBody] or
 * the [Bill.color] property of a [Bill] when we are used by [BillsBody].
 * @param amounts a lambda which tales an [Account] or [Bill] instance and returns a [Float]. This
 * will return the [Account.balance] property of an [Account] when we are used by [AccountsBody] or
 * the [Bill.amount] property of a [Bill] when we are used by [BillsBody].
 * @param amountsTotal the sum of all of the [Account.balance] or [Bill.amount] properties in the
 * [List] of [Account] or [List] of [Bill] that is our [items] parameter.
 * @param circleLabel this is the label that is displayed beneath our [AnimatedCircle], it is the
 * [String] "Total" when we are used by [AccountsBody] or the [String] "Due" when we are used by
 * [BillsBody].
 * @param rows a lambda emitting a Composable displaying the [Account] or [Bill] that we pass it as
 * the argument to the lambda that we take from our [List] of [Account] or [Bill] parameter [items].
 * The Composable will be an [AccountRow] when we are used by [AccountsBody] or a [BillRow] when
 * we are used by [BillsBody].
 */
@Composable
fun <T> StatementBody(
    items: List<T>,
    colors: (T) -> Color,
    amounts: (T) -> Float,
    amountsTotal: Float,
    circleLabel: String,
    rows: @Composable (T) -> Unit
) {
    Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
        Box(Modifier.padding(all = 16.dp)) {
            val accountsProportion: List<Float> = items.extractProportions { amounts(it) }
            val circleColors: List<Color> = items.map { colors(it) }
            AnimatedCircle(
                proportions = accountsProportion,
                colors = circleColors,
                modifier = Modifier
                    .height(height = 300.dp)
                    .align(alignment = Alignment.Center)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.align(alignment = Alignment.Center)) {
                Text(
                    text = circleLabel,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )
                Text(
                    text = formatAmount(amount = amountsTotal),
                    style = MaterialTheme.typography.h2,
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )
            }
        }
        Spacer(modifier = Modifier.height(height = 10.dp))
        Card {
            Column(modifier = Modifier.padding(all = 12.dp)) {
                items.forEach { accountOrBill ->
                    rows(accountOrBill)
                }
            }
        }
    }
}
