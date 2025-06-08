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

/**
 * Generic component used by the accounts and bills screens to show a chart and a list of items.
 * TODO: Continue here.
 *
 * @param accountsOrBills The list of items to display.
 * @param colors A lambda that returns the color for a given item.
 * @param amounts A lambda that returns the amount for a given item.
 * @param amountsTotal The total amount of all items.
 * @param circleLabel The label to display in the center of the circle chart.
 * @param rows A composable that displays a row for a given item.
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
