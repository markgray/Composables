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

package com.codelab.basics

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.basics.ui.BasicsCodelabTheme
import com.codelab.basics.ui.LightBlue
import com.codelab.basics.ui.Navy

/**
 * This is the main activity of the "Jetpack Compose Basics codelab":
 *
 *     https://developer.android.com/codelabs/jetpack-compose-basics
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge]
     * to enable edge to edge display, then we call our super's implementation of `onCreate`.
     * Next we call [setContent] to have it Compose its Composable lambda argument into our activity.
     * That lambda consists of our [MyApp] Composable wrapped by our [BasicsCodelabTheme] custom
     * [MaterialTheme].
     *
     * The [MyApp] composable is wrapped in a [Box] whose `modifier` argument is a
     * [Modifier.safeDrawingPadding] to add padding to accommodate the safe drawing insets
     * as kludge to adjust to the use of [enableEdgeToEdge].
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use here. It is
     * used by [rememberSaveable] though to remember the value of the `var shouldShowOnboarding`
     * [Boolean] flag used by [MyApp].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BasicsCodelabTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    MyApp()
                }
            }
        }
    }
}

/**
 * This Composable exists to choose between the [OnboardingScreen] Composable or the [Greetings]
 * Composable based on the `true` or `false` value of the [Boolean] `var shouldShowOnboarding`. It
 * delegates using [rememberSaveable] to a [mutableStateOf] which starts out as `true`. If it is
 * `true` the [OnboardingScreen] Composable is called with its `onContinueClicked` argument set to
 * a lambda which sets `shouldShowOnboarding` to `false`. This lambda is executed when the user
 * clicks the [Button] labeled "Continue" in [OnboardingScreen]. If `shouldShowOnboarding` is `false`
 * our [Greetings] Composable is called which displays a [LazyColumn] holding 1,000 [Greeting]
 * objects whose `name` parameter is the position in the list. Each [Greeting] object holds a
 * [Card] (Cards contain content and actions about a single subject) with a [CardContent] Composable
 * that can be expanded or collapsed when its [IconButton] is clicked.
 */
@Composable
private fun MyApp() {
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

    if (shouldShowOnboarding) {
        OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
    } else {
        Greetings()
    }
}

/**
 * This Composable displays a "Welcome to the Basics Codelab!" [Text] and a [Button] labeled
 * "Continue" which executes our `onContinueClicked` parameter when it is clicked. In our case that
 * lambda sets the `shouldShowOnboarding` [mutableStateOf] to `false` which causes our caller [MyApp]
 * to be recomposed and since the `shouldShowOnboarding` is now `false` the [Greetings] Composable
 * will be displayed instead of [OnboardingScreen].
 *
 * @param onContinueClicked a lambda to be executed when our "Continue" [Button] is clicked.
 */
@Composable
private fun OnboardingScreen(onContinueClicked: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("Continue")
            }
        }
    }
}

/**
 * This Composable fills a [LazyColumn] with [Greeting] objects whose `name` is taken from its [List]
 * of [String] parameter [names]. The default value of [names] is a list of [String] whose values
 * are the [String] value of the position of the [String] in the [List] (from "0" to "999").
 *
 * @param names the [List] of [String] to use for the `name` of each of the [Greeting] items in our
 * [LazyColumn].
 */
@Composable
private fun Greetings(names: List<String> = List(1000) { "$it" }) {
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = names) { name: String ->
            Greeting(name = name)
        }
    }
}

/**
 * Used as the [items] that fill the [LazyColumn] in the [Greetings] Composable. It consists of a
 * [Card] whose `backgroundColor` is `MaterialTheme.colors.primary` ([LightBlue] for `LightColorPalette`
 * or [Navy] for `DarkColorPalette`), whose `modifier` is a [Modifier.padding] of 4.dp `vertical` and
 * 8.dp `horizontal`, and whose `content` Composable lambda is a [CardContent] whose `name` is our
 * parameter [name].
 *
 * @param name a [String] to be used as the `name` parameter of the [CardContent] which is held by
 * our [Card] Composable. In our case this is the [String] value of an [Int] from 0 to 999.
 */
@Composable
private fun Greeting(name: String) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        CardContent(name)
    }
}

/**
 * This is the `content` of the [Card] used by our [Greeting] Composable for each of the [items] in
 * the [LazyColumn] of the [Greetings] Composable. It [remember]'s the [mutableStateOf] of a single
 * [Boolean] variable `var expanded` which when `true` displays an additional [Text] in the [Column]
 * contained as the first Composable in the [Row] of our layout (when `false` it is skipped, it starts
 * out `false` and is toggled when the [IconButton] at the end of the [Row] is clicked). The layout
 * consists of a [Row] whose `modifier` is a [Modifier.padding] of 12.dp, with a daisy chained
 * [Modifier.animateContentSize] that makes the [Row] animate its size when its children change size.
 * The `content` of the [Row] has a [Column] which holds three [Text] Composables, the first one
 * displaying the `text` "Hello", and the second displaying our [name] parameter as its `text`. The
 * third `text` is only displayed if `expanded` is `true` and consists of a multi-line bit of
 * gibberish. At the end of the [Row] is an [IconButton] whose `onClick` toggles the value of
 * `expanded`. It [Icon] `content` will display the [ImageVector] drawn by [Filled.ExpandLess] if
 * `expanded` is `true` or [Filled.ExpandMore] if it is `false` with the `contentDescription`
 * "Show less" if `true` or "Show more" if `false`.
 *
 * @param name the [String] we should display in the middle [Text] of our [Column].
 */
@Composable
private fun CardContent(name: String) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(text = "Hello, ")
            Text(
                text = name,
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (expanded) {
                Text(
                    text = ("Composem ipsum color sit lazy, " +
                        "padding theme elit, sed do bouncy. ").repeat(4),
                )
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Filled.ExpandLess else Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }

            )
        }
    }
}

/**
 * This Preview Composable generates two previews of our [BasicsCodelabTheme] wrapped [Greetings]
 * Composable, one labeled "DefaultPreviewDark" is done using `uiMode` set to [UI_MODE_NIGHT_YES]
 * which will use the `DarkColorPalette` defined in [BasicsCodelabTheme] and one labeled
 * [DefaultPreview] which will use the `LightColorPalette` of [BasicsCodelabTheme].
 */
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    BasicsCodelabTheme {
        Greetings()
    }
}

/**
 * This is a Preview of our [BasicsCodelabTheme] wrapped [OnboardingScreen] Composable.
 */
@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    BasicsCodelabTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}
