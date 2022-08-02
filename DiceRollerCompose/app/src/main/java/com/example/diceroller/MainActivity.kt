package com.example.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diceroller.ui.theme.DiceRollerTheme

/**
 * This is the main activity of our diceroller app.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call [setContent] to Compose the given composable into our activity. The content will
     * become the root view of the given activity. This composable consists of our [DiceRollerTheme]
     * app theme wrapping our [DiceRollerApp] composable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so we ignore it.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerTheme {
                DiceRollerApp()
            }
        }
    }
}

/**
 * The main screen of our app, it contains a [DiceWithButtonAndImage] composable called with a
 * [Modifier] configured with `fillMaxSize   to have the content fill the `Constraints.maxWidth`
 * and `Constraints.maxHeight` of its incoming measurement constraints. and configured with
 * `wrapContentSize` to center its contents.
 */
@Preview
@Composable
fun DiceRollerApp() {
    DiceWithButtonAndImage(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}

/**
 * The `Composable` holding our entire UI, it consists of a [Column] holding the [Image] corresponding
 * to the most recent rolling of the dice, and a [Button] which when clicked rolls the dice again.
 *
 * @param modifier the [Modifier] to be used by our [Column].
 */
@Composable
fun DiceWithButtonAndImage(modifier: Modifier = Modifier) {
    var result by remember { mutableStateOf(1) }
    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = result.toString()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { result = (1..6).random() }) {
            Text(text = stringResource(id = R.string.roll))
        }
    }

}