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

package com.example.compose.rally.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.compose.rally.data.Account
import com.example.compose.rally.data.Bill

/**
 * Generic component used by the accounts and bills screens to show a chart and a list of items.
 *
 * Our root composable is a [Column] whose `modifier` argument is a [Modifier.verticalScroll] whose
 * `state` argument is a [rememberScrollState] (used to scroll the [Column]). In the [ColumnScope]
 * `content` composable lambda argument of the [Column] we have:
 *
 * **First**: a [Box] whose `modifier` argument is a [Modifier.padding] that adds `16.dp` padding to
 * all sides of the [Box]. In the [BoxScope] `content` composable lambda argument of the [Box] we
 * initialize our [List] of [Float] variable `val accountsProportion` to the value returned by our
 * [List.extractProportions] extension function when it is applied to our [List] of [T] parameter
 * `accountsOrBills` with a lambda that returns the [Float] that our [amounts] lambda parameter
 * returns for the [T] that it is called with. We initialize our [List] of [Color] variable
 * `val circleColors` to the value returned by the [Iterable.map] extension function when it is
 * applied to our [List] of [T] parameter `accountsOrBills` with a lambda that returns the [Color]
 * that our [colors] lambda parameter returns for the [T] that it is called with. We then compose an
 * [AnimatedCircle] into the [Box] with its `proportions` argument our [List] of [Float] variable
 * `accountsProportion`, its `colors` argument our [List] of [Color] variable `circleColors`, and
 * its `modifier` argument a [Modifier.height] of 300.dp, chained to a [BoxScope.align] whose
 * `alignment` argument is [Alignment.Center], and at the end of the chain is a [Modifier.fillMaxWidth].
 * Composed on top of the [AnimatedCircle] is a [Column] whose `modifier` argument is a [BoxScope.align]
 * whose `alignment` argument is [Alignment.Center], and in the [ColumnScope] `content` composable
 * we compose:
 *
 * A [Text] whose `text` argument is our [String] parameter [circleLabel], whose [TextStyle] `style`
 * argument is the [Typography.body1] of our custom [MaterialTheme.typography], and whose `modifier`
 * argument is a [ColumnScope.align] whose `alignment` argument is [Alignment.CenterHorizontally].
 *
 * A [Text] whose `text` argument is the value returned by our [formatAmount] method when it is
 * applied to the [Float] parameter [amountsTotal], whose [TextStyle] `style` argument is the
 * [Typography.h2] of our custom [MaterialTheme.typography], and whose `modifier` argument is a
 * [ColumnScope.align] whose `alignment` argument is [Alignment.CenterHorizontally].
 *
 * **Second**: Below the [Box] in the outer [Column] is a [Spacer] whose `modifier` argument is a
 * [Modifier.height] whose `height` argument is `10.dp`.
 *
 * **Third**: Below the [Spacer] in the outer [Column] is a [Card] in whose `content` composable
 * lambda argument we comopose a [Column] whose `modifier` argument is a [Modifier.padding] that adds
 * `12.dp` padding to all sides of the [Column]. In the [ColumnScope] `content` composable lambda
 * argument of the [Column] we use the [Iterable.forEach] method of our [List] of [T] parameter
 * `accountsOrBills` capturing the [T] passed the lambda in variable `item` and using that variable
 * as the argument to our [rows] composable lambda parameter to fill the [Column] with [rows].
 *
 * @param accountsOrBills The list of items to display.
 * @param colors A lambda that returns the color for a given item.
 * @param amounts A lambda that returns the amount for a given item.
 * @param amountsTotal The total amount of all items.
 * @param circleLabel The label to display in the center of the circle chart.
 * @param rows A composable that displays a row for a given item.
 * @param T The type of the items in the [List] of [T] parameter [accountsOrBills], either an
 * [Account] or a [Bill] depending on who calls us.
 */
@Composable
fun <T> StatementBody(
    accountsOrBills: List<T>,
    colors: (T) -> Color,
    amounts: (T) -> Float,
    amountsTotal: Float,
    circleLabel: String,
    rows: @Composable (T) -> Unit
) {
    Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
        Box(modifier = Modifier.padding(all = 16.dp)) {
            val accountsProportion: List<Float> = accountsOrBills.extractProportions { amounts(it) }
            val circleColors: List<Color> = accountsOrBills.map { colors(it) }
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
                accountsOrBills.forEach { item: T ->
                    rows(item)
                }
            }
        }
    }
}
