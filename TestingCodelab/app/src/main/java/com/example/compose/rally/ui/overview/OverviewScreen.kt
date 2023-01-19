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

package com.example.compose.rally.ui.overview

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.rally.R
import com.example.compose.rally.RallyApp
import com.example.compose.rally.RallyScreen
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.bills.BillsBody
import com.example.compose.rally.ui.components.AccountRow
import com.example.compose.rally.ui.components.BillRow
import com.example.compose.rally.ui.components.RallyAlertDialog
import com.example.compose.rally.ui.components.RallyDivider
import com.example.compose.rally.ui.components.formatAmount
import com.example.compose.rally.ui.components.RallyTopAppBar
import com.example.compose.rally.ui.theme.RallyTheme
import java.util.Locale

/**
 * This Composable is composed when the [RallyScreen.Overview] screen `body` lambda argument is
 * executed. That lambda is executed by the [RallyScreen.Content] method when the `RallyTab` in the
 * [RallyTopAppBar] for the [RallyScreen.Overview] gets selected by the user. It occupies the [Box]
 * that is the `content` of the [Scaffold] of the [RallyApp] and is swapped for the [AccountsBody]
 * or the [BillsBody] Composables when their associated `RallyTab` is selected. Our root Composable
 * is a [Column] whose `modifier` argument is a [Modifier.padding] that sets the padding on all sides
 * to 16.dp, with a [Modifier.verticalScroll] chained to that whose `state` argument is a new instance
 * of [ScrollState] constructed and remembered by [rememberScrollState] (modifies the [Column] to
 * allow it to scroll vertically when the height of its content is bigger than max constraints allow).
 *
 * The `content` of the [Column] is an [AlertCard] followed by a [RallyDefaultPadding] (12.dp) high
 * [Spacer], followed by an [AccountsCard] whose `onScreenChange` argument is our [onScreenChange]
 * parameter, followed by a [RallyDefaultPadding] (12.dp) high [Spacer], followed by an [BillsCard]
 * whose `onScreenChange` argument is our [onScreenChange] parameter.
 *
 * @param onScreenChange a lambda which when called with a [RallyScreen] enum value will launch the
 * associated Composable.
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
        Spacer(Modifier.height(height = RallyDefaultPadding))
        BillsCard(onScreenChange = onScreenChange)
    }
}

/**
 * The Alerts card within the Rally Overview screen. We start by initializing and remembering our
 * [Boolean] variable `var showDialog`, and initializing our [String] variable `val alertMessage`
 * to the string "Heads up, you've used up 90% of your Shopping budget for this month." Then if
 * `showDialog` is `true` we compose a [RallyAlertDialog] whose `onDismiss` argument is a lambda
 * that sets `showDialog` to `false`, whose `bodyText` argument is our `alertMessage` [String]
 * variable, and whose `buttonText` argument is the uppercase version of the [String] "Dismiss".
 *
 * Next we initialize and remember our [InfiniteTransition] variable `val infiniteElevationAnimation`
 * then use it to initialize our animated [Dp] variable `val animatedElevation` using its
 * [InfiniteTransition.animateValue] with an `initialValue` argument of 1.dp, a `targetValue`
 * argument of 8.dp, a `typeConverter` argument of `Dp.VectorConverter` (type converter that converts
 * a [Dp] to a [AnimationVector1D], and vice versa), and an `animationSpec` argument that is the
 * [InfiniteRepeatableSpec] returned by the [infiniteRepeatable] method for a 500ms duration [tween]
 * used as its `animation` argument, and a [RepeatMode.Reverse] for its `repeatMode` argument.
 *
 * Our root Composable is then a [Card] whose `elevation` argument is our animated [Dp] variable
 * `animatedElevation`. The `content` of the [Card] is a [Column] which holds an [AlertHeader] whose
 * `onClickSeeAll` lambda argument sets `showDialog` to `true` (this launching [RallyAlertDialog]),
 * followed by a [RallyDivider] whose `modifier` argument is a [Modifier.padding] that adds
 * [RallyDefaultPadding] to the `start` and `end` of the [RallyDivider], and an [AlertItem] whose
 * `message` argument is our `alertMessage` variable.
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

    val infiniteElevationAnimation: InfiniteTransition = rememberInfiniteTransition()
    val animatedElevation: Dp by infiniteElevationAnimation.animateValue(
        initialValue = 1.dp,
        targetValue = 8.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    Card(elevation = animatedElevation) {

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

/**
 * Preview for our [OverviewBody] Composable wrapped in our [RallyTheme] custom [MaterialTheme].
 */
@Preview
@Composable
fun AlertCardPreview() {
    RallyTheme {
        OverviewBody()
    }
}

/**
 * This Composable is the header of our [AlertCard], and consists only of a [Text] displaying the
 * `text` "Alerts" and a [TextButton] labeled "SEE ALL" which when clicked executes the [onClickSeeAll]
 * lambda parameter of [AlertHeader]. Our root Composable is a [Row] whose `modifier` argument is a
 * [Modifier.padding] that sets the padding on all sides of the [Row] to [RallyDefaultPadding] (12.dp),
 * with a [Modifier.fillMaxWidth] chained to it that causes the [Row] to occupy the entire incoming
 * width constraints. The `content` of the [Row] is a [Text] diplaying the `text` "Alerts" using the
 * `subtitle2` [TextStyle] of the custom [Typography] of [MaterialTheme.typography], defined by our
 * [RallyTheme] custom [MaterialTheme] to be the [FontWeight.Normal] font of the `RobotoCondensed`
 * [FontFamily] (the [Font] with resource ID [R.font.robotocondensed_regular]) with a `fontSize` of
 * 14.sp, and `letterSpacing` of `0.1.em` and the `modifier` argument of the [Text] is a [RowScope]
 * `Modifier.align` whose `alignment` argument [Alignment.CenterVertically] centers the [Text]'s
 * `text` vertically about its center line. The [Text] is followed by a [TextButton] whose `onClick`
 * argument is our [onClickSeeAll] parameter, whose `contentPadding` argument is a [PaddingValues]
 * of 0.dp, and whose `modifier` argument of the [TextButton] is a [RowScope] `Modifier.align` whose
 * `alignment` argument [Alignment.CenterVertically] centers the [TextButton]'s vertically about the
 * center line of the [Row], and the `content` of the [TextButton] is a [Text] displaying its label
 * "SEE ALL" using as its `style` the `button` [TextStyle] of the custom [Typography] of
 * [MaterialTheme.typography], defined by our [RallyTheme] custom [MaterialTheme] to be the
 * [FontWeight.Bold] font of the `RobotoCondensed` [FontFamily] (the [Font] with resource ID
 * [R.font.robotocondensed_bold]) with a `fontSize` of 14.sp, `lineHeight` of 16.sp and
 * `letterSpacing` of `0.2.em`.
 *
 * @param onClickSeeAll a lambda that is to be executed when the "SEE ALL" [TextButton] is clicked.
 * Our [AlertCard] caller passes us a lambda which sets its `showDialog` [MutableState] wrapped
 * [Boolean] variable to `true`, which causes the composition of a [RallyAlertDialog] that displays
 * the `bodyText` "Heads up, you've used up 90% of your Shopping budget for this month."
 */
@Composable
fun AlertHeader(onClickSeeAll: () -> Unit) {
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
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = "SEE ALL",
                style = MaterialTheme.typography.button,
            )
        }
    }
}

/**
 * TODO: Add kdoc
 */
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
            Icon(imageVector = Icons.Filled.Sort, contentDescription = null)
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
            Column(Modifier.padding(RallyDefaultPadding)) {
                Text(text = title, style = MaterialTheme.typography.subtitle2)
                val amountText = "$" + formatAmount(
                    amount
                )
                Text(text = amountText, style = MaterialTheme.typography.h2)
            }
            OverViewDivider(data, values, colors)
            Column(Modifier.padding(start = 16.dp, top = 4.dp, end = 8.dp)) {
                data.take(SHOWN_ITEMS).forEach { row(it) }
                SeeAllButton(onClick = onClickSeeAll)
            }
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
private fun <T> OverViewDivider(
    data: List<T>,
    values: (T) -> Float,
    colors: (T) -> Color
) {
    Row(Modifier.fillMaxWidth()) {
        data.forEach { item: T ->
            Spacer(
                modifier = Modifier
                    .weight(values(item))
                    .height(1.dp)
                    .background(colors(item))
            )
        }
    }
}

/**
 * The Accounts card within the Rally Overview screen.
 */
@Composable
fun AccountsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount = UserData.accounts.map { account -> account.balance }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.accounts),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(RallyScreen.Accounts)
        },
        data = UserData.accounts,
        colors = { it.color },
        values = { it.balance }
    ) { (name, number, balance, color) ->
        AccountRow(
            name = name,
            number = number,
            amount = balance,
            color = color
        )
    }
}

/**
 * The Bills card within the Rally Overview screen.
 */
@Composable
fun BillsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount = UserData.bills.map { bill -> bill.amount }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.bills),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(RallyScreen.Bills)
        },
        data = UserData.bills,
        colors = { it.color },
        values = { it.amount }
    ) { (name, due, amount1, color) ->
        BillRow(
            name = name,
            due = due,
            amount = amount1,
            color = color
        )
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
private fun SeeAllButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .height(44.dp)
            .fillMaxWidth()
    ) {
        Text(stringResource(R.string.see_all))
    }
}

/**
 * TODO: Add kdoc
 */
val RallyDefaultPadding: Dp = 12.dp

/**
 * TODO: Add kdoc
 */
private const val SHOWN_ITEMS = 3
