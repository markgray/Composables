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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.rally.R
import com.example.compose.rally.data.Account
import com.example.compose.rally.data.Bill
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.bills.BillsBody
import com.example.compose.rally.ui.overview.AccountsCard
import com.example.compose.rally.ui.overview.BillsCard
import com.example.compose.rally.ui.theme.RallyTheme
import java.text.DecimalFormat

/**
 * A row representing the basic information of an [Account]. We just convert our parameters into the
 * type of arguments that the [BaseRow] Composable expects as its arguments and then call [BaseRow]
 * with them:
 *  - `color` argument is just our [Color] parameter [color]
 *  - `title` argument is just our [String] parameter [name]
 *  - `subtitle` argument is created by concatenating the [String] whose resource ID is
 *  [R.string.account_redacted] and the [String] returned by the [DecimalFormat.format] method of
 *  our [AccountDecimalFormat] field when passed our [Int] parameter [number].
 *  - `amount` argument is just our [Float] parameter [amount]
 *  - `negative` argument is `false` (if it were `true` [BaseRow] would prepend a minus sign to its
 *  display of [amount], which it does when called by [BillRow].
 *
 * @param name The title of this [AccountRow], it comes from the [Account.name] property of the
 * [Account] whose information we are displaying.
 * @param number the account number, it comes from the [Account.number] property of the [Account]
 * whose information we are displaying.
 * @param amount the amount of money in the [Account], it comes from the [Account.balance] property
 * of the [Account] whose information we are displaying.
 * @param color the [Color] that [BaseRow] should have its [AccountIndicator] Composable use to
 * differentiate this [Account] from the others, it comes from the [Account.color] property of the
 * [Account] whose information we are displaying.
 */
@Composable
fun AccountRow(name: String, number: Int, amount: Float, color: Color) {
    BaseRow(
        color = color,
        title = name,
        subtitle = stringResource(R.string.account_redacted) + AccountDecimalFormat.format(number),
        amount = amount,
        negative = false
    )
}

/**
 * A row representing the basic information of a [Bill]. We just convert our parameters into the
 * type of arguments that the [BaseRow] Composable expects as its arguments and then call [BaseRow]
 * with them:
 *  - `color` argument is just our [Color] parameter [color]
 *  - `title` argument is just our [String] parameter [name]
 *  - `subtitle` argument is created by concatenating the [String] "Due" with our [String] parameter
 *  [due]
 *  - `amount` argument is just our [Float] parameter [amount]
 *  - `negative` argument is `true` which causes [BaseRow] to prepend a minus sign to its display of
 *  [amount].
 *
 * @param name The title of this [BillRow], it comes from the [Bill.name] property of the [Bill]
 * whose information we are displaying.
 * @param due The due date of the [Bill], it comes from the [Bill.due] property of the [Bill] whose
 * information we are displaying.
 * @param amount the amount of the [Bill], it comes from the [Bill.amount] property of the [Bill]
 * whose information we are displaying.
 * @param color the [Color] that [BaseRow] should have its [AccountIndicator] Composable use to
 * differentiate this [Bill] from the others, it comes from the [Bill.color] property of the [Bill]
 * whose information we are displaying.
 */
@Composable
fun BillRow(name: String, due: String, amount: Float, color: Color) {
    BaseRow(
        color = color,
        title = name,
        subtitle = "Due $due",
        amount = amount,
        negative = true
    )
}

/**
 * This Composable is used by [AccountRow] to display the information the [AccountsBody] Composable
 * and the [AccountsCard] Composable wants [AccountRow] to display about an [Account] and by [BillRow]
 * to display the information the [BillsBody] Composable and the [BillsCard] Composable wants [BillRow]
 * to display about a [Bill]. We start by initializing our [String] variable `val dollarSign` to the
 * [String] "–$ " if our [Boolean] parameter [negative] is `true` or to "$ " if it is `false` (the
 * [AccountRow] Composable calls us with `false` and the [BillRow] Composable calls us with `true`).
 * Then we initialize our [String] variable `val formattedAmount` to the value returned by our
 * [formatAmount] method when passed our [Float] parameter [amount].
 *
 * Our `content` consists of a [Row] followed by a [RallyDivider] which separates this [BaseRow] from
 * Composables that follow it on the screen ([RallyDivider] is just a [Divider] whose `thickness` is
 * 1.dp). The arguments of the [Row] are:
 *  - `modifier` is a [Modifier.height] that sets the preferred height of the content to be exactly
 *  68.dp with a [Modifier.clearAndSetSemantics] chained to it that sets the content description of
 *  the semantics node to a [String] constructed from our parameters [title], and [subtitle], and our
 *  variables `dollarSign` and `formattedAmount`.
 *  - `verticalAlignment` is [Alignment.CenterVertically] to have the [Row]'s children center their
 *  content about the center line of the [Row].
 *
 * Inside the `content` lambda of the [Row] we first initialize our [Typography] variable `val typography`
 * to the [MaterialTheme.typography] of our [RallyTheme] custom [MaterialTheme]. Then we compose a
 * [AccountIndicator] into our [Row] with its `color` argument our [Color] parameter [color], and its
 * `modifier` argument an empty, default, or starter [Modifier] that contains no elements. The
 * [AccountIndicator] Composable is just a 4.dp wide by 36.dp high [Spacer] whose background [Color]
 * is the `color` parameter of [AccountIndicator]. Next in our [Row] is a [Spacer] whose `modifier`
 * argument is a [Modifier.width] of 12.dp and this is followed by a [Column] which contains a [Text]
 * displaying our [title] parameter using the [TextStyle] specified for [Typography.body1] by our
 * [RallyTheme] custom [MaterialTheme] (`fontWeight` = [FontWeight.Normal], `fontSize` = 16.sp, and
 * `letterSpacing` = 0.1.em with the default `RobotoCondensed` [FontFamily] using the  [Font] whose
 * resource ID is [R.font.robotocondensed_regular]. Next in the [Column] is a [CompositionLocalProvider]
 * providing [ContentAlpha.medium] as the [LocalContentAlpha] to a [Text] that is wraps which displays
 * our [String] parameter [subtitle] using the [TextStyle] specified for [Typography.subtitle1] by our
 * [RallyTheme] custom [MaterialTheme] (`fontWeight` = [FontWeight.Light], `fontSize` = 14.sp,
 * `lineHeight` = 20.sp, `letterSpacing` = 3.sp with the default `RobotoCondensed` [FontFamily] using
 * the [Font] whose resource ID is [R.font.robotocondensed_light]. After the [Column] in the [Row]
 * is a [Spacer] whose `modifier` argument is a [RowScope] `Modifier.weight` of 1f which will cause
 * it to use all of the space that remains in the [Row]'s incoming horizontal constraints after its
 * siblings have been measured and placed. We follow the [Spacer] with an inner [Row] that is used
 * so that it can have the `horizontalArrangement` argument of [Arrangement.SpaceBetween] which will
 * place children such that they are spaced evenly across the main axis, without free space before
 * the first child or after the last child, and the `content` of this [Row] is a [Text] that displays
 * our `dollarSign` variable using the [TextStyle] specified for [Typography.h6] by our [RallyTheme]
 * custom [MaterialTheme] (`fontWeight` = [FontWeight.Normal], `fontSize` = 18.sp, `lineHeight` = 20.sp,
 * `fontFamily` = `EczarFontFamily`, `letterSpacing` = 3.sp using the [Font] whose resource ID is
 * [R.font.eczar_regular]), this is followed in the inner [Row] by a [Text] that displays our
 * `formattedAmount` variable using the same [TextStyle] with both [Text] having as their `modifier`
 * argument a [RowScope] `Modifier.align` of [Alignment.CenterVertically] which centers their `content`
 * about the centerline of the [Row]. This is followed in the outer [Row] by another [Spacer] that
 * is 16.dp wide, and at the end of the [Row] is a [CompositionLocalProvider] providing
 * [ContentAlpha.medium] as the [LocalContentAlpha] to an [Icon] that displays the [ImageVector]
 * that is drawn by [Icons.Filled.ChevronRight], with its `contentDescription` argument `null`, and
 * its `modifier` argument a [Modifier.padding] that adds 12.dp padding to the end of the [Icon],
 * with a [Modifier.size] chained to that which sets the size of the [Icon] to 24.dp
 *
 * Beneath the [Row] described above is a [RallyDivider] which is just a 1.dp `thickness` [Divider]
 * that visually separates this [BaseRow] from any Composables that follow it.
 *
 * @param color the [Color] to use for our [AccountIndicator], this is the [Account.color] property
 * of the [Account] we are displaying when we are called by [AccountRow], or the [Bill.color] property
 * of the [Bill] we are displaying when we are called by [BillRow].
 * @param title
 */
@Composable
private fun BaseRow(
    color: Color,
    title: String,
    subtitle: String,
    amount: Float,
    negative: Boolean
) {
    val dollarSign: String = if (negative) "–$ " else "$ "
    val formattedAmount: String = formatAmount(amount = amount)
    Row(
        modifier = Modifier
            .height(height = 68.dp)
            .clearAndSetSemantics {
                contentDescription =
                    "$title account ending in ${subtitle.takeLast(4)}, current balance $dollarSign$formattedAmount"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typography: Typography = MaterialTheme.typography
        AccountIndicator(
            color = color,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier) {
            Text(text = title, style = typography.body1)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = subtitle, style = typography.subtitle1)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dollarSign,
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = formattedAmount,
                style = typography.h6,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.width(16.dp))

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
        }
    }
    RallyDivider()
}

/**
 * A vertical colored line that is used in a [BaseRow] to differentiate accounts.
 */
@Composable
private fun AccountIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(modifier.size(4.dp, 36.dp).background(color = color))
}

/**
 * TODO: Add kdoc
 */
@Composable
fun RallyDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colors.background, thickness = 1.dp, modifier = modifier)
}

/**
 * TODO: Add kdoc
 */
fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}

private val AccountDecimalFormat = DecimalFormat("####")
private val AmountDecimalFormat = DecimalFormat("#,###.##")

/**
 * Used with accounts and bills to create the animated circle.
 */
fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}
