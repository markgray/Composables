/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat

/**
 * This app calculates the tip to give depending on the "Cost of Service", the "Tip (%)", and a
 * "Round up tip?" [Switch].
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. This is where most initialization should go: calling
     * [setContent] to Compose the given composable into our given activity. The content will become
     * the root view of the given activity. This is roughly equivalent to calling
     * [ComponentActivity.setContentView] with a ComposeView.
     *
     * First we call our super's implementation of `onCreate` then we call [setContent] to Compose a
     * composable consisting of our [TipTimeTheme] custom [MaterialTheme] wrapping a [Surface] which
     * uses [Modifier.fillMaxSize] as its modifier to have it totally fill the screen with the
     * [TipTimeScreen] composable which is its `content` (Material surface is the central metaphor
     * in material design. Each surface exists at a given elevation, which influences how that piece
     * of surface visually relates to other surfaces and how that surface casts shadows).
     *
     * See the code for [TipTimeScreen] to understand what the resulting UI looks like.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this [Bundle] contains the data it most recently supplied in [onSaveInstanceState],
     * as well as recomposition triggering values persisted by [rememberSaveable]. Note: Otherwise
     * it is `null`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TipTimeScreen()
                }
            }
        }
    }
}

/**
 * This is the main screen of our app. We remember three values between re-compositions:
 *  - `var amountInput` is the [String] that the user has so far entered into the "Cost of Service"
 *  [EditNumberField] and is the value of the purchase for which the tip needs to be calculated.
 *  - `var tipInput` is the [String] that the user has so far entered into the "Tip (%)"
 *  [EditNumberField] and is the tip percentage to use for our calculation.
 *  - `var roundUp` is a [Boolean] flag which indicates that we should round the tip up to the
 *  nearest dollar. It is updated by the position of the [Switch] in our [RoundTheTipRow] and the
 *  [Text] in the [RoundTheTipRow] displays the text "Round up tip?".
 *
 * On every re-composition forced by a change to one of the `remember`'ed variables we calculate
 * three values:
 *  - `val amount` is the [Double] value converted from the `amountInput` variable (or 0.0 if that
 *  is `null`.
 *  - `val tipPercent` is the [Double] value converted from the `tipInput` variable (or 0.0 if that
 *  is `null`.
 *  - `val tip` is the [String] that our [calculateTip] method creates when called with `amount`,
 *  `tipPercent`, and `roundUp`. This is the [String] that is displayed in the "Tip Amount: %s"
 *  [Text], where `tip` is substituted for the "%s" in the format [String].
 *
 * We next initialize our [FocusManager] variable `val focusManager` to the CompositionLocal that
 * can be used to control focus within Compose. We will use this to move the focus from the
 * "Cost of Service" [EditNumberField] down to the "Tip (%)" [EditNumberField] when the user clicks
 * the "Next" key of the IME keyboard and to clear the focus when the user clicks the "Done" key of
 * the "Tip (%)" [EditNumberField] IME keyboard.
 *
 * The UI uses a [Column] layout whose `modifier` is a [Modifier.padding] of 32.dp, and whose
 * `verticalArrangement` uses a [Arrangement.spacedBy] of 8.dp to place its children with 8.dp of
 * space between them. The top widget in the column is a [Text] displaying the [String] "Calculate
 * Tip" whose `fontSize` is 24.sp, and whose `modifier` is a `Modifier.align` which is used to have
 * the text centered horizontally. This is followed by a 16.dp high [Spacer], and then the
 * [EditNumberField] labelled "Cost of Service" which the user is to use to enter the price of the
 * purchase that the tip is to be calculated for. Below this is another [EditNumberField] labelled
 * "Tip (%)" where the user enters the percnt tip they want to leave. Below this is a [RoundTheTipRow]
 * which contains a [Switch] labeled "Round up tip?" that allows the user to flip the switch so that
 * that tip is rounded up to the nearest dollar. At the bottom of the [Column] is a [Text] that
 * displays the formatted string "Tip Amount: %s", where the current value of `tip` is substituted
 * for the "%s" in the format.
 */
@Composable
fun TipTimeScreen() {
    var amountInput: String by rememberSaveable { mutableStateOf("") }
    var tipInput: String by rememberSaveable { mutableStateOf("") }
    var roundUp: Boolean by remember { mutableStateOf(false) }

    val amount: Double = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent: Double = tipInput.toDoubleOrNull() ?: 0.0
    val tip: String = calculateTip(amount, tipPercent, roundUp)

    val focusManager: FocusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        EditNumberField(
            label = R.string.cost_of_service,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            value = amountInput,
            onValueChanged = { amountInput = it }
        )
        EditNumberField(
            label = R.string.how_was_the_service,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            value = tipInput,
            onValueChanged = { tipInput = it }
        )
        RoundTheTipRow(roundUp = roundUp, onRoundUpChanged = { roundUp = it })
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.tip_amount, tip),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * This is basically just a front end for a [TextField] which simplifies our use of the [TextField]
 * in our UI.
 */
@Composable
fun EditNumberField(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

@Composable
fun RoundTheTipRow(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.round_up_tip))
        Switch(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            checked = roundUp,
            onCheckedChange = onRoundUpChanged,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color.DarkGray
            )
        )
    }
}

/**
 * Calculates the tip based on the user input and formats the tip amount
 * according to the local currency for its display onscreen.
 *
 * Example would be "$10.00".
 *
 * The VisibleForTesting annotation Denotes that the class, method or field has its visibility
 * relaxed, so that it is more widely visible than otherwise necessary to make code testable.
 * You can optionally specify what the visibility should have been if not for testing; this allows
 * tools to catch unintended access from within production code.
 *
 * @param amount the amount of the purchase
 * @param tipPercent the tip percent you want to tip
 * @param roundUp a flag which when `true` will round the tip up to the nearest dollar.
 */
@VisibleForTesting
fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundUp: Boolean): String {
    var tip = tipPercent / 100 * amount
    if (roundUp)
        tip = kotlin.math.ceil(tip)
    return NumberFormat.getCurrencyInstance().format(tip)
}

/**
 * This is just the `Preview` of the [TipTimeScreen].
 */
@Preview
@Composable
fun TipTimeScreenPreview() {
    TipTimeTheme {
        TipTimeScreen()
    }
}
