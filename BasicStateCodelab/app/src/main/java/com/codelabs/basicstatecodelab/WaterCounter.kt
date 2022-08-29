package com.codelabs.basicstatecodelab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    var count by rememberSaveable { mutableStateOf(0) }
    StatelessCounter(count = count, onIncrement = { count++ }, modifier = modifier)
}

@Composable
fun StatelessCounter(count: Int, onIncrement: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        if (count > 0) {
            Text("You've had $count glasses.")
        }
        Button(onClick = onIncrement, Modifier.padding(top = 8.dp), enabled = count < 10) {
            Text("Add one")
        }
    }
}
