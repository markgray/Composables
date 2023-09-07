package com.codelabs.basicstatecodelab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * This Composable holds the `count` [MutableState] hoisted from [StatelessCounter] which it
 * remembers by [rememberSaveable]. It then calls [StatelessCounter] with `count` as its `count`
 * argument, a lambda which increments `count` as its `onIncrement` argument, and our [Modifier]
 * parameter [modifier] as its `modifier` argument.
 *
 * @param modifier a [Modifier] instance which our caller can use to modify our behavior and
 * appearance. [WellnessScreen] does not pass a value so the empty, default, or starter [Modifier]
 * that contains no elements is used, which we pass to [StatelessCounter] as its `modifier` argument.
 */
@Composable
fun StatefulCounter(modifier: Modifier = Modifier) {
    var count by rememberSaveable { mutableIntStateOf(0) }
    StatelessCounter(count = count, onIncrement = { count++ }, modifier = modifier)
}

/**
 * This Composable uses a [Column] to hold a [Text] which will be displayed only when our [count]
 * parameter is greater than 0 (it shows the text "You've had $count glasses."), and a [Button]
 * labeled "Add one" (using a [Text]) whose `onClick` argument calls our [onIncrement] parameter.
 * The `modifier` argument of the [Column] adds a [Modifier.padding] of 16.dp to our [modifier]
 * parameter, and the `modifier` argument of the [Button] is a [Modifier.padding] whose `top`
 * padding is 8.dp. The [Button] is only enabled while our [count] parameter is less than 10.
 *
 * @param count the state variable which has been hoisted to [StatefulCounter], when it is greater
 * than 0 its value is displayed as part of the text of our [Text], and when it reaches 10 it
 * disables our [Button] Composable.
 * @param onIncrement a lambda to call when our [Button] is clicked.
 * @param modifier a [Modifier] instance which our caller can use to modify our appearance and
 * behavior. When you trace up the call chain this turns out to be just the empty, default, or
 * starter [Modifier] that contains no elements that is specified by the default value of the
 * `modifier` parameter of [StatefulCounter].
 */
@Composable
fun StatelessCounter(
    count: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        if (count > 0) {
            Text("You've had $count glasses.")
        }
        Button(
            onClick = onIncrement,
            modifier = Modifier.padding(top = 8.dp),
            enabled = count < 10
        ) {
            Text("Add one")
        }
    }
}
