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
import com.example.compose.rally.data.Account
import com.example.compose.rally.data.Bill
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
 * This Composable just displays its [String] parameter [message] in a [Text] and a [IconButton] in
 * a [Row] root Composable. The `modifier` argument of the [Row] is a [Modifier.padding] that sets
 * the padding on all sides of the [Row] to [RallyDefaultPadding] (12.dp), with a [Modifier.semantics]
 * whose `mergeDescendants` argument is `true` so that the semantic information provided by the owning
 * component and its descendants should be treated as one logical entity. The `content` of the [Row]
 * is a [Text] whose `style` argument is a `body2` [TextStyle] of [MaterialTheme.typography] (which
 * is [FontWeight.Normal] of the `RobotoCondensed` [FontFamily] (the [Font] file with resource ID
 * [R.font.robotocondensed_regular]) with `fontSize` = 14.sp, `lineHeight` = 20.sp, and `letterSpacing`
 * = 0.1.em, and the `modifier` argument of the [Text] is a [RowScope] `Modifier.weight` of 1f which
 * causes the [Text] to take all the incoming width constraint that remains after its sibling is
 * measured and placed, and the `text` argument is our [String] parameter [message]. The [IconButton]
 * at the end of the [Row] has a do-nothing lambda as its `onClick` argument, and for its `modifier`
 * argument a [RowScope] `Modifier.align` whose `alignment` argument aligns it to [Alignment.Top]
 * (the top of the [Row]), and to this [Modifier] is chained a [Modifier.clearAndSetSemantics] which
 * clears the semantics of all its descendant nodes and sets the semantics to a do-nothing lambda.
 * The `content` of the [IconButton] is an [Icon] displaying the `imageVector` [Icons.Filled.Sort]
 * (three horozontal lines, each line slightly shorter than the one above it), with a `null`
 * `contentDescription` argument.
 *
 * @param message the [String] for our [Text] to display.
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
 * Base structure for cards in the Overview screen, it is used by both [AccountsCard] and [BillsCard].
 * Our root Composable is a [Card] which holds a [Column] as its `content`. This [Column] has three
 * children:
 *  - a [Column] whose `modifier` argument is a [Modifier.padding] that adds [RallyDefaultPadding]
 *  (12.dp) to all sides and whose `content` consists of a [Text] displaying our [title] parameter
 *  using the `subtitle2` [TextStyle] of [MaterialTheme.typography] (`fontWeight` = [FontWeight.Normal],
 *  `fontSize` = 14.sp, `letterSpacing` = 0.1.em), and this is followed by a [Text] which displays the
 *  formatted `text` representation of our [amount] parameter using the `h2` [TextStyle] of
 *  [MaterialTheme.typography] (`fontWeight` = [FontWeight.SemiBold], `fontSize` = 44.sp,
 *  `letterSpacing` = 1.5.em, `fontFamily` = `EczarFontFamily` the [Font] whose resource ID is
 *  [R.font.eczar_semibold]).
 *  - an [OverViewDivider] which displays different [Color] horizontal lines in a [Row] whose length
 *  represents the relative "weight" of the [Account] or [Bill]
 *  - a [Column] whose `modifier` argument is a [Modifier.padding] that adds 16.dp to the `start`,
 *  4.dp to the `top` and 8.dp to the `end` of the [Column]. The `content` is a [row] for each of the
 *  first [SHOWN_ITEMS] (3) [Account] or [Bill] objects in our [data] parameter, followed by a
 *  [SeeAllButton] whose `onClick` argument is our [onClickSeeAll] lambda parameter.
 *
 * @param title the title text for the card, "Accounts" when called by [AccountsCard] and "Bills"
 * when called by [BillsCard].
 * @param amount the dollar amount to display, either the sum of all the [Account.balance] properties
 * when called by [AccountsCard], or the sum of all the [Bill.amount] properties when called by
 * [BillsCard].
 * @param onClickSeeAll a lambda to be used as the `onClick` argument of the [SeeAllButton].
 * [AccountsCard] passes us a lambda which calls its `onScreenChange` parameter with
 * [RallyScreen.Accounts] in order to navigate to the [AccountsBody] Composable which displays all
 * of the [Account] objects, and [BillsCard] passes us a lambda which calls its `onScreenChange`
 * parameter with [RallyScreen.Bills] in order to navigate to the [BillsBody] Composable which
 * displays all of the [Bill] objects.
 * @param values a lambda which returns a floating point value that represents the dollar amount of
 * the [Account] or [Bill] it is called with, [AccountsCard] passes us a lambda which returns the
 * [Account.balance] property of its argument, and [BillsCard] passes us a lambda which returns the
 * [Bill.amount] property of its argument. Used by [OverViewDivider] to assign a weight to the
 * [Spacer] it displays for each of the [Account] or [Bill] objects.
 * @param colors a lambda which returns a [Color] to be used to represent an the [Account] or [Bill]
 * object it is called with, [AccountsCard] passes us a lambda which returns the [Account.color]
 * property of its argument, and [BillsCard] passes us a lambda which returns the [Bill.color]
 * property of its argument.
 * @param data a [List] of [Account] objects when we are called by [AccountsCard] or a [List] of
 * [Bill] objects when we are called by [BillsCard].
 * @param row a Composable that can be used to display an individual [Account] or [Bill] from our
 * [data] parameter. [AccountsCard] passes us a lambda which calls the [AccountRow] Composable with
 * its [Account] argument, and [BillsCard] passes us a lambda which calls the [BillRow] Composable
 * with its [Bill] argument.
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
            Column(modifier = Modifier.padding(all = RallyDefaultPadding)) {
                Text(text = title, style = MaterialTheme.typography.subtitle2)
                val amountText = "$" + formatAmount(
                    amount
                )
                Text(text = amountText, style = MaterialTheme.typography.h2)
            }
            OverViewDivider(data = data, values = values, colors = colors)
            Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 8.dp)) {
                data.take(SHOWN_ITEMS).forEach { row(it) }
                SeeAllButton(onClick = onClickSeeAll)
            }
        }
    }
}

/**
 * This Composable different colored horizontal lines in a [Row] whose lengths represent the relative
 * contribution that each [Account] or [Bill] in its [List] parameter [data] makes to the total of
 * amount of the [List]. The root Composable is a [Row] whose `modifier` argument is a
 * [Modifier.fillMaxWidth] that causes the [Row] to occupy the entire incoming width constraints.
 * For the `content` of the [Row] it loops through each of the [Account] or [Bill] objects in its
 * [List] of `T` parameter [data] composing a [Spacer] whose `modifier` is a [RowScope]
 * `Modifier.weight` whose `weight argument is the value that our [values] lambda parameter returns
 * for that [Account] or [Bill]. A [Modifier.height] is chained to that which sets the height to 1.dp,
 * and at the end of the chain is a [Modifier.background] that sets the background color to the [Color]
 * that our [colors] lambda parameter returns for that [Account] or [Bill].
 *
 * @param data the [List] of [Account] or [Bill] instances that we are supposed to draw lines for.
 * @param values a lambda that returns a [Float] that represents the value of the [Account] or [Bill]
 * it is passed. When [AccountsCard] uses [OverviewScreenCard] this is a lambda that returns the
 * [Account.balance] property of the [Account], and when [BillsCard] uses [OverviewScreenCard] this
 * is a lambda that returns the [Bill.amount] property of the [Bill].
 * @param colors a lambda that returns a [Color] to represent the [Account] of [Bill] passed it as
 * its argument. When [AccountsCard] uses [OverviewScreenCard] this is a lambda that returns the
 * [Account.color] property of the [Account], and when [BillsCard] uses [OverviewScreenCard] this
 * is a lambda that returns the [Bill.color] property of the [Bill].
 */
@Composable
private fun <T> OverViewDivider(
    data: List<T>,
    values: (T) -> Float,
    colors: (T) -> Color
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        data.forEach { item: T ->
            Spacer(
                modifier = Modifier
                    .weight(weight = values(item))
                    .height(height = 1.dp)
                    .background(color = colors(item))
            )
        }
    }
}

/**
 * The Accounts card within the Rally Overview screen. We use the [map] extension function to sum
 * all of the [Account.balance] properties in the [List] of [Account] field [UserData.accounts] and
 * initialize our [Float] variable `val amount` to the result. We use [OverviewScreenCard] as our
 * root Composable with its `title` argument the [String] whose resource ID is [R.string.accounts]
 * ("Accounts"), its `amount` argument is our `amount` variable, its `onClickSeeAll` argument is a
 * lambda that calls our [onScreenChange] lambda parameter with the argument [RallyScreen.Accounts],
 * its `data` argument is the [List] of [Account] field [UserData.accounts], its `colors` argument
 * is a lambda that returns the [Account.color] of the [Account] it is called with, and its `values`
 * argument is a lambda that returns the [Account.balance] of the [Account] it is called with. The
 * `row` Composable lambda destructures the [Account] it is passed into its [Account.name],
 * [Account.number], [Account.balance], and [Account.color] properties and calls the [AccountRow]
 * Composable with the arguments `name` = `name`, `number` = `number`, `amount` = `balance`, and
 * `color` = `color` that are produced by the destructuring.
 *
 * @param onScreenChange a lambda which when called with a [RallyScreen] will transition to the
 * screen that displays for that [RallyScreen]. We pass it [RallyScreen.Accounts] in the lambda
 * we use for the `onClickSeeAll` argument of [OverviewScreenCard] and that will cause the
 * [AccountsBody] screen to be displayed.
 */
@Composable
fun AccountsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount: Float = UserData.accounts.map { account: Account -> account.balance }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.accounts),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(RallyScreen.Accounts)
        },
        data = UserData.accounts,
        colors = { it.color },
        values = { it.balance }
    ) { (name: String, number: Int, balance: Float, color: Color) ->
        AccountRow(
            name = name,
            number = number,
            amount = balance,
            color = color
        )
    }
}

/**
 * The Bills card within the Rally Overview screen. We use the [map] extension function to sum
 * all of the [Bill.amount] properties in the [List] of [Bill] field [UserData.bills] and
 * initialize our [Float] variable `val amount` to the result. We use [OverviewScreenCard] as our
 * root Composable with its `title` argument the [String] whose resource ID is [R.string.bills]
 * ("Bills"), its `amount` argument is our `amount` variable, its `onClickSeeAll` argument is a
 * lambda that calls our [onScreenChange] lambda parameter with the argument [RallyScreen.Bills],
 * its `data` argument is the [List] of [Bill] field [UserData.bills], its `colors` argument
 * is a lambda that returns the [Bill.color] of the [Bill] it is called with, and its `values`
 * argument is a lambda that returns the [Bill.amount] of the [Bill] it is called with. The
 * `row` Composable lambda destructures the [Bill] it is passed into its [Bill.name], [Bill.due],
 * [Bill.amount] (renamed `amount1`), and [Bill.color] properties and calls the [BillRow] Composable
 * with the arguments `name` = `name`, `due` = `due`, `amount` = `amount1`, and `color` = `color`.
 *
 * @param onScreenChange a lambda which when called with a [RallyScreen] will transition to the
 * screen that displays for that [RallyScreen]. We pass it [RallyScreen.Bills] in the lambda
 * we use for the `onClickSeeAll` argument of [OverviewScreenCard] and that will cause the
 * [BillsBody] screen to be displayed.
 */
@Composable
fun BillsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount = UserData.bills.map { bill: Bill -> bill.amount }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.bills),
        amount = amount,
        onClickSeeAll = {
            onScreenChange(RallyScreen.Bills)
        },
        data = UserData.bills,
        colors = { it.color },
        values = { it.amount }
    ) { (name: String, due: String, amount1: Float, color: Color) ->
        BillRow(
            name = name,
            due = due,
            amount = amount1,
            color = color
        )
    }
}

/**
 * [TextButton] used as the bottom Composable of the [OverviewScreenCard] Composable both when it is
 * used by [AccountsCard] and when it is used by [BillsCard]. Our root Composable is a [TextButton]
 * with the `onClick` argument our [onClick] lambda parameter, and the `modifier` argument a
 * [Modifier.height] of 44.dp, to which is chained a [Modifier.fillMaxWidth] that causes the button
 * to occupy the entire incoming width constraint. Its label `content` is a [Text] whose `text` is
 * the [String] with resource ID [R.string.see_all] ("SEE ALL").
 *
 * @param onClick a lambda to use as the `onClick` argument of our [TextButton]. [OverviewScreenCard]
 * passes us its `onClickSeeAll` lambda parameter which when used by [AccountsCard] is a lambda that
 * calls the [AccountsCard] parameter `onScreenChange` with [RallyScreen.Accounts] to navigate to the
 * [AccountsBody] Composable and when used by [BillsCard] is a lambda that calls the [BillsCard]
 * parameter `onScreenChange` with [RallyScreen.Bills] to navigate to the [BillsBody] Composable.
 */
@Composable
private fun SeeAllButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .height(44.dp)
            .fillMaxWidth()
    ) {
        Text(text = stringResource(id = R.string.see_all))
    }
}

/**
 * This is the default padding, and is used in several places.
 */
val RallyDefaultPadding: Dp = 12.dp

/**
 * The number of [Account] or [Bill] instances that [OverviewScreenCard] should display.
 */
private const val SHOWN_ITEMS = 3
