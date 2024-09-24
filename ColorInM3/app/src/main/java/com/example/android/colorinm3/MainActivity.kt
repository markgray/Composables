package com.example.android.colorinm3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.android.colorinm3.ui.theme.ColorInM3Theme
import com.example.android.colorinm3.ui.theme.DarkColorScheme
import com.example.android.colorinm3.ui.theme.LightColorScheme

/**
 * This app displays colors used for all of the [ColorScheme]'s used by Material3. [Button]'s in
 * the [AppBottombar] `bottomBar` of the [Scaffold] allow one to toggle between Dynamic and Static
 * [ColorScheme]'s and Dark Theme and Light Theme [ColorScheme]'s.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of [onCreate],
     * then we call [setContent] to Compose its `content` composable lambda argument into our activity.
     * In that lambda we start by initializing and [rememberSaveable]'ing [MutableState] wrapped
     * [Boolean] variable `var dynamicColor` to `true` and [MutableState] wrapped [Boolean] variable
     * `var darkTheme` to `true`. Then wrapped in our [ColorInM3Theme] custom [MaterialTheme] whose
     * `darkTheme` argument is our `darkTheme` variable and whose `dynamicColor` argument is our
     * `dynamicColor` variable we have as our root Composable a [Surface] whose `modifier` argument
     * is a [Modifier.fillMaxSize] to have it occupy its entire incoming size constraints, and whose
     * `color` argument is the [ColorScheme.background] color of [MaterialTheme.colorScheme]. The
     * `content` of the [Surface] is our [MyApp] Composable whose `dynamicColor` argument is our
     * `dynamicColor` variable, whose `darkTheme` argument is our `darkTheme` variable, whose
     * `toggleDynamic` arugment is lambda which toggles the value of our [Boolean] `dynamicColor`
     * variable, and whose `toggleDarkTheme` argument is a lambda which toggles the value of our
     * [Boolean] `darkTheme` variable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use, but it is
     * used by Compose to persist the values of several [MutableState] variables across configuration
     * changes.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            var dynamicColor: Boolean by rememberSaveable {
                mutableStateOf(value = true)
            }
            var darkTheme: Boolean by rememberSaveable {
                mutableStateOf(value = true)
            }
            ColorInM3Theme(dynamicColor = dynamicColor, darkTheme = darkTheme) {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MyApp(
                            dynamicColor = dynamicColor,
                            darkTheme = darkTheme,
                            toggleDynamic = { dynamicColor = !dynamicColor },
                            toggleDarkTheme = { darkTheme = !darkTheme }
                        )
                    }
                }
            }
        }
    }
}

/**
 * This is the main screen Composable of our app. It consists of a [Scaffold] whose `bottomBar`
 * argument is an [AppBottombar] whose `modifier` argument is our [modifier] parameter, whose
 * `dynamicColor` argument is our [dynamicColor] parameter, whose `darkTheme` argument is our
 * [darkTheme] parameter, whose `toggleDynamic` is a lambda that calls our [toggleDynamic]
 * parameter, and whose `toggleDarkTheme` argument is our [toggleDarkTheme] parameter. The content
 * of the [Scaffold] is our [Greeting] Composable whose `modifier` argument chains a [Modifier.padding]
 * to our [Modifier] parameter [modifier] that adds the [PaddingValues] that the [Scaffold] passes
 * the `content` lambda to the [Greeting] Composable.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not pass us one so the empty, default, or starter [Modifier] that
 * contains no elements is used instead.
 * @param dynamicColor a [Boolean] which if `true` indicates that our [ColorInM3Theme] custom
 * [MaterialTheme] is using currently using a dynamic [ColorScheme] (either [dynamicDarkColorScheme]
 * if [darkTheme] is `true` or [dynamicLightColorScheme] if [darkTheme] is `false`). If [dynamicColor]
 * is `false` one of the static [ColorScheme] is used (either [DarkColorScheme] if [darkTheme] is
 * `true` or [LightColorScheme] if [darkTheme] is `false`).
 * @param darkTheme a [Boolean] which if `true` indicates that our [ColorInM3Theme] custom
 * [MaterialTheme] is using currently using a dark [ColorScheme] (either [dynamicDarkColorScheme]
 * if [dynamicColor] is `true`, or [DarkColorScheme] if [dynamicColor] is `false`). If [darkTheme]
 * is `false` one of the light [ColorScheme] is used (either [dynamicLightColorScheme] if [dynamicColor]
 * is `true` or [LightColorScheme] if [dynamicColor] is `false`).
 * @param toggleDynamic a lambda that our caller passes us which when called will toggle the value
 * of our [Boolean] parameter [dynamicColor] thereby causing [ColorInM3Theme] to be recomposed as
 * well as all of the `content` that is wrapped in [ColorInM3Theme]. We pass it on to the [AppBottombar]
 * of our [Scaffold] where it is called in the `onClick` lambda of the "Static/Dynamic" [Button].
 * @param toggleDarkTheme a lambda that our caller passes us which when called will toggle the value
 * f our [Boolean] parameter [darkTheme] thereby causing [ColorInM3Theme] to be recomposed as well
 * as all of the `content` that is wrapped in [ColorInM3Theme]. We pass it on to the [AppBottombar]
 * of our [Scaffold] where it is called in the `onClick` lambda of the "Light Theme/Dark Theme"
 * [Button].
 */
@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    dynamicColor: Boolean,
    darkTheme: Boolean,
    toggleDynamic: () -> Unit,
    toggleDarkTheme: () -> Unit
) {
    Scaffold(
        bottomBar = {
            AppBottombar(
                modifier = modifier,
                dynamicColor = dynamicColor,
                darkTheme = darkTheme,
                toggleDynamic = { toggleDynamic() },
                toggleDarkTheme = { toggleDarkTheme() }
            )
        }
    ) { paddingValues: PaddingValues ->
        Greeting(
            modifier = modifier.padding(paddingValues = paddingValues)
        )
    }
}

/**
 * This Composable is used as the `bottomBar` argument of the [Scaffold] in the [MyApp] Composable.
 * Its root Composable is a [Row] whose `modifier` argument adds a [Modifier.fillMaxWidth] to our
 * [modifier] argument. The content of the [Row] consists of two [Button] Composables. Both [Button]
 * use a [RowScope] `Modifier.weight` of 1f to have them occupy an equal part of the [Row]'s incoming
 * width constraint. The `onClick` argument of the first [Button] calls our [toggleDynamic] lambda
 * parameter, and the `onClick` argument of the second [Button] calls our [toggleDarkTheme] lambda.
 * The label content of the first [Button] displays in a [Text] the string "Static" if our [Boolean]
 * parameter [dynamicColor] is `true` and  the string "Dynamic" if it is `false`. The label content
 * of the second [Button] displays in a [Text] the string "Light Theme" if our [Boolean] parameter
 * [darkTheme] is `true` and  the string "Dark Theme" if it is `false`.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us the empty, default, or starter [Modifier] that contains no
 * elements that it uses.
 * @param dynamicColor if `true` our [ColorInM3Theme] custom [MaterialTheme] is using one of the
 * dynamic [ColorScheme]'s, if `false` it is using one of the static [ColorScheme]'s.
 * @param darkTheme if `true` our [ColorInM3Theme] custom [MaterialTheme] is using one of the dark
 * [ColorScheme]'s, if `false` it is using one of the light [ColorScheme]'s.
 * @param toggleDynamic a lambda which when called toggles the value of [Boolean] parameter
 * [dynamicColor]. It is used as the `onClick` argument of the first [Button] in our [Row].
 * @param toggleDarkTheme a lambda which when called toggles the value of [Boolean] parameter
 * [darkTheme]. It is used as the `onClick` argument of the second [Button] in our [Row].
 */
@Composable
fun AppBottombar(
    modifier: Modifier = Modifier,
    dynamicColor: Boolean,
    darkTheme: Boolean,
    toggleDynamic: () -> Unit,
    toggleDarkTheme: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { toggleDynamic() },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = if (dynamicColor) "Static" else "Dynamic")
        }
        Button(
            onClick = { toggleDarkTheme() },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = if (darkTheme) "Light Theme" else "Dark Theme")
        }
    }
}

/**
 * This [Composable] displays all of the different [Color]'s available from the [ColorScheme] of our
 * [ColorInM3Theme] custom [MaterialTheme] using [Surface]'s which wrap a [Text] Composable which
 * gives the Material3 name for the [Color]. The [Surface]'s are surrounded by a [Column] whose
 * `modifier` argument adds a [Modifier.fillMaxSize] to our [modifier] parameter to have it occupy
 * its entire incoming size constraints, with a [Modifier.verticalScroll] chained to that to make it
 * scrollable. Its `verticalArrangement` argument uses a [Arrangement.spacedBy] to have its children
 * spaced by 6.dp.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. The [Scaffold] that calls us passes us the [Modifier.padding] of the [PaddingValues]
 * that it passes to its Composable `content` lambda.
 */
@Composable
fun Greeting(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(space = 6.dp)
    ) {
        val textModifier: Modifier = modifier.padding(all = 8.dp)
        Spacer(modifier = Modifier.height(height = 6.dp))
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "primary",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "primaryContainer",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "secondary",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "secondaryContainer",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "tertiary",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "tertiaryContainer",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "error",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onError
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "errorContainer",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "background",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "surface",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "surfaceVariant",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.inverseSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "inverseSurface",
                modifier = textModifier,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "inversePrimary",
                modifier = textModifier
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceTint,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "surfaceTint",
                modifier = textModifier
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "outlineVariant",
                modifier = textModifier
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.scrim,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "scrim",
                modifier = textModifier
            )
        }
    }
}
