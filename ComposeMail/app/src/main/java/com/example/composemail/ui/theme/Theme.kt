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

package com.example.composemail.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composemail.ui.home.TopToolbar

/**
 * A Material [lightColors] pallet for the light theme of the app.
 */
private val LightColorPalette = lightColors(
    primary = Color(color = 0xFF6200EE),
    primaryVariant = Color(color = 0xFF3700B3),
    secondary = Color(color = 0xFF03DAC5),
    onSecondary = Color.White
)

/**
 * A Material [darkColors] pallet for the dark theme of the app.
 */
private val DarkColorPalette = darkColors(
    primary = Color(color = 0xFFBB86FC),
    primaryVariant = Color(color = 0xFF3700B3),
    secondary = Color(color = 0xFF03DAC5),
    onSecondary = Color.White
)

/**
 * Defines colors for when items are selected.
 *
 * This is not part of the Material [Colors] system, but is used by custom components in this
 * app, for example to indicate that a mail item in a list has been selected.
 */
object Selection {
    val backgroundColor: Color = Color(color = 0xFFADB7C5)
}

/**
 * The main theme of our app.
 *
 * This is a wrapper around [MaterialTheme] using the custom color palettes, typography, and shapes
 * defined for this app.
 *
 * It defaults to either the light or dark theme based on the system's settings.
 *
 * @param darkTheme whether the theme should be in dark mode or not. Defaults to the result of
 * [isSystemInDarkTheme].
 * @param content the content to be displayed inside the theme.
 */
@Composable
fun ComposeMailTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

/**
 * A preview of different components in the app, such as buttons, cards, and toolbars.
 *
 * This preview is useful to see how the components look in both light and dark mode, and how they
 * are affected by the app's theme.
 *
 * It displays:
 *  - A card with buttons on a default surface.
 *  - A card with buttons on a secondary-colored surface.
 *  - The top toolbar in its default state (no items selected).
 *  - The top toolbar in its selection state (one or more items selected).
 */
@Preview(name = "Components-Light", group = "components")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    name = "Components-Night", group = "components"
)
@Composable
private fun ComponentsPreview() {
    ComposeMailTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card {
                Column {
                    Text(text = "Default Surface")
                    Button(onClick = { }) {
                        Text(text = "Button")
                    }
                    OutlinedButton(onClick = { }) {
                        Text(text = "Outlined")
                    }
                    TextButton(onClick = { }) {
                        Text(text = "Text Button")
                    }
                }
            }
            Card(
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary
            ) {
                Column {
                    Text(text = "Secondary Surface")
                    Button(onClick = { }) {
                        Text(text = "Button")
                    }
                    OutlinedButton(onClick = { }) {
                        Text(text = "Outlined")
                    }
                    TextButton(onClick = { }) {
                        Text(text = "Text Button")
                    }
                }
            }
            Card {
                TopToolbar(
                    modifier = Modifier,
                    selectionCountProvider = { 0 }
                ) { /* Do nothing*/ }
            }
            Card {
                TopToolbar(
                    modifier = Modifier.fillMaxWidth(),
                    selectionCountProvider = { 1 }
                ) { /* Do nothing*/ }
            }
        }
    }
}

/**
 * A preview of the different text styles used in the app.
 *
 * This preview displays various typography styles defined in the app's theme,
 * such as headers (H1 to H5) and body text. It is useful for visually verifying
 * how text appears in both light and dark modes, ensuring readability and
 * consistency with the Material Design guidelines.
 */
@Preview(name = "Text-Light", group = "text")
@Preview(
    name = "Text-Night", group = "text",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun TextPreview() {
    ComposeMailTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
        ) {
            ThemedText(
                text = "Header H1",
                style = MaterialTheme.typography.h1
            )
            ThemedText(
                text = "Header H2",
                style = MaterialTheme.typography.h2
            )
            ThemedText(
                text = "Header H3",
                style = MaterialTheme.typography.h3
            )
            ThemedText(
                text = "Header H4",
                style = MaterialTheme.typography.h4
            )
            ThemedText(
                text = "Header H5",
                style = MaterialTheme.typography.h5
            )
            ThemedText(
                text = "Body 1",
                style = MaterialTheme.typography.body1
            )
            ThemedText(
                text = "Body 2",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

/**
 * A preview that displays a grid of `DumbThemedButton` Composables.
 *
 * This preview is useful for visualizing how buttons look with various
 * background and content colors defined in the app's `MaterialTheme`. It showcases
 * buttons styled with primary, secondary, background, surface, error, and variant
 * colors, helping to ensure color combinations are legible and aesthetically
 * pleasing in both light and dark themes.
 *
 * The buttons are arranged in a responsive grid that adapts to the available width.
 */
@Preview(name = "Button-Light", group = "button")
@Preview(
    name = "Button-Night", group = "button",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun ButtonPreview() {
    ComposeMailTheme {
        val buttonSpecs = listOf(
            MyButtonSpec(
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                text = "Background"
            ),
            MyButtonSpec(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                text = "Primary"
            ),
            MyButtonSpec(
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary,
                text = "Secondary"
            ),
            MyButtonSpec(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                text = "Surface"
            ),
            MyButtonSpec(
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = MaterialTheme.colors.onPrimary,
                text = "PrimaryV"
            ),
            MyButtonSpec(
                backgroundColor = MaterialTheme.colors.secondaryVariant,
                contentColor = MaterialTheme.colors.onSecondary,
                text = "SecondaryV"
            ),
            MyButtonSpec(
                backgroundColor = MaterialTheme.colors.error,
                contentColor = MaterialTheme.colors.onError,
                text = "Error"
            ),
        )

        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            buttonSpecs.chunked(3).forEach { specs ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    specs.forEach { buttonSpec ->
                        DumbThemedButton(buttonSpec = buttonSpec)
                    }
                }
            }
        }
    }
}

/**
 * A wrapper around [Text] that ensures the text color is appropriate for the current theme.
 *
 * This composable applies the `onSurface` color from the `MaterialTheme.colors` to the
 * [Text] component, making it readable on the default surface background of both light and
 * dark themes.
 * TODO: Continue here.
 * @param modifier The [Modifier] to be applied to this layout node.
 * @param style The text style to be applied to the text. Defaults to [TextStyle.Default].
 * @param text The text to be displayed.
 */
@Composable
private fun ThemedText(
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    text: String,
) {
    Text(
        text = text,
        style = style,
        modifier = modifier,
        color = MaterialTheme.colors.onSurface
    )
}

private data class MyButtonSpec(
    val backgroundColor: Color,
    val contentColor: Color,
    val text: String = "Default"
)

@Composable
private fun DumbThemedButton(
    modifier: Modifier = Modifier,
    buttonSpec: MyButtonSpec
) {
    Button(
        modifier = modifier,
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = buttonSpec.backgroundColor,
            contentColor = buttonSpec.contentColor
        )
    ) {
        Text(text = buttonSpec.text)
    }
}