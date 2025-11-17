package com.example.examplecomposegrid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp

/**
 * The main activity for the application, serving as a hub for demonstrating various
 * Jetpack Compose layouts.
 *
 * This activity can display one of two main UIs:
 *  1. A menu of available layout demos ([ComposableMenu]).
 *  2. A specific layout demo.
 *
 * When launched without an intent extra, it shows the menu. Tapping a button in the
 * menu relaunches the activity with an intent extra, specifying which demo to display.
 */
class MainActivity : ComponentActivity() {
    /**
     * Key for the intent extra to specify which composable function to display.
     * This is used to re-launch the [MainActivity] to show a specific example
     * selected from the main menu.
     */
    private val composeKey = "USE_COMPOSE"

    /**
     * A map that associates a string name with a Composable function.
     * This list is used to build the main menu of available demos and to
     * launch a specific demo via an [Intent] extra.
     * Each entry is created using the [get] helper function.
     */
    private var cmap = listOf(
        get(name = "GridDslKeypad") { GridDslKeypad() },
        get(name = "GridDslMediumCalculator") { GridDslMediumCalculator() },
        get(name = "GridDslMediumRow") { GridDslMediumRow() },
        get(name = "GridDslMediumColumn") { GridDslMediumColumn() },
        get(name = "GridDslMediumNested") { GridDslMediumNested() },
        get(name = "GridDslColumnInRow") { GridDslColumnInRow() },
        get(name = "GridJsonKeypad") { GridJsonKeypad() },
        get(name = "GridJsonMediumCalculator") { GridJsonMediumCalculator() },
        get(name = "GridJsonRow") { GridJsonRow() },
        get(name = "GridJsonColumn") { GridJsonColumn() },
        get(name = "GridJsonNested") { GridJsonNested() },
        get(name = "GridJsonColumnInRow") { GridJsonColumnInRow() },
        get(name = "MotionGridDslDemo") { MotionGridDslDemo() },
        get(name = "MotionDslDemo2") { MotionDslDemo2() },
        get(name = "MotionGridDemo") { MotionGridDemo() },
        get(name = "MotionGridDemo2") { MotionGridDemo2() },
        get(name = "RowDslDemo") { RowDslDemo() },
        get(name = "RowWeightsDslDemo") { RowWeightsDslDemo() },
        get(name = "ColumnDslDemo") { ColumnDslDemo() },
        get(name = "ColumnWeightsJsonDemo") { ColumnWeightsJsonDemo() },
        get(name = "RowJsonDemo") { RowJsonDemo() },
        get(name = "RowWeightsJsonDemo") { RowWeightsJsonDemo() },
        get(name = "ColumnJsonDemo") { ColumnJsonDemo() },
        get(name = "ColumnWeightsJsonDemo") { ColumnWeightsJsonDemo() }
    )

    /**
     * Called when the activity is first created. This function sets up the user interface.
     *
     * It checks if an [Intent] extra stored under the key [composeKey] is provided.
     * If a specific composable function name is passed, this function finds the corresponding
     * [ComposeFunc] in the `cmap` list and displays it.
     *
     * If no specific composable is provided in the intent, it displays a menu ([ComposableMenu])
     * of all available example composables. Clicking an item in the menu relaunches this
     * activity with an intent extra to display that specific composable.
     *
     * The UI is built using Jetpack Compose within a [ComposeView].
     *
     * @param savedInstanceState We do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val extra = intent.extras
        var cfunc: ComposeFunc? = null
        if (extra != null) {
            val composeName = extra.getString(composeKey)
            for (composeFunc in cmap) {
                if (composeFunc.toString() == composeName) {
                    cfunc = composeFunc
                    break
                }
            }
        }
        val com = ComposeView(context = this)
        setContentView(view = com)
        com.setContent {
            Box(modifier = Modifier.safeDrawingPadding()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(color = 0xFFF0E7FC)
                ) {
                    if (cfunc != null) {
                        Log.v("MAIN", " running $cfunc")
                        cfunc.Run()
                    } else {
                        ComposableMenu(map = cmap) { act -> launch(toRun = act) }
                    }
                }
            }

        }
    }

    /**
     * A Composable that displays a two-column menu of buttons.
     * Each button corresponds to a [ComposeFunc] from the [List] of [ComposeFunc] parameter [map].
     * Clicking a button calls our lambda parameter [act] with the corresponding [ComposeFunc].
     *
     * @param map A list of [ComposeFunc] objects to be displayed as buttons in the menu.
     * @param act A lambda function that is invoked when a button is clicked. It receives the
     * [ComposeFunc] associated with the clicked button.
     */
    @Composable
    fun ComposableMenu(map: List<ComposeFunc>, act: (act: ComposeFunc) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
        ) {
            for (i in 0..(map.size - 1) / 2) {
                val cFunc1: ComposeFunc = map[i * 2]
                val cFunc2: ComposeFunc? = if ((i * 2 + 1 < map.size)) map[i * 2 + 1] else null
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { act(cFunc1) }) {
                        Text(text = cFunc1.toString(), modifier = Modifier.padding(all = 2.dp))
                    }
                    if (cFunc2 != null) {
                        Button(onClick = { act(cFunc2) }) {
                            val s: String =
                                cFunc2.toString().substring(cFunc2.toString().indexOf(' ') + 1)
                            Text(text = s, modifier = Modifier.padding(all = 2.dp))
                        }
                    }
                }
            }

        }
    }

    /**
     * Starts a new instance of [MainActivity] to display a specific Composable.
     *
     * This function creates an Intent to launch the [MainActivity] again. It passes the string
     * representation of the provided [ComposeFunc] as an extra in the intent stored under the
     * key [composeKey].
     *
     * The receiving [MainActivity] will then use this extra to determine which
     * Composable function to display, instead of showing the main menu.
     *
     * @param toRun The [ComposeFunc] to be executed in the new activity instance.
     */
    private fun launch(toRun: ComposeFunc) {
        Log.v("MAIN", " launch $toRun")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(composeKey, toRun.toString())
        startActivity(intent)
    }
}

/**
 * A factory function to create an instance of the [ComposeFunc] interface.
 *
 * This function is a convenient way to bundle a Composable function with a display name.
 * It returns an anonymous object that implements [ComposeFunc]. The `toString()` method
 * of this object is overridden to return the [String] parameter [name], which is used for display
 * purposes in the UI (e.g., button labels) and for identification in [Intent]'s.
 *
 * @param name The string identifier for the composable function. This is used as its display name
 * in the menu.
 * @param cRun A lambda containing the Composable function to be executed.
 * @return An object implementing the [ComposeFunc] interface, which encapsulates the
 * given name and composable lambda.
 */
fun get(name: String, cRun: @Composable () -> Unit): ComposeFunc {
    return object : ComposeFunc {
        /**
         * A Composable function that executes the encapsulated UI logic.
         * When this function is called within a Composable context, it will render the
         * UI defined in the `cRun` lambda passed to the `get` function.
         */
        @Composable
        override fun Run() {
            cRun()
        }

        /**
         * Returns the string representation of this composable function, which is its unique name.
         * This name is used for display in the UI (e.g., on buttons) and as a key for
         * launching the specific composable via an [Intent] extra.
         *
         * @return The [name] of the composable function.
         */
        override fun toString(): String {
            return name
        }
    }
}

/**
 * An interface for encapsulating a named Composable function.
 *
 * This serves as a common type for different UI demonstration screens, allowing them
 * to be listed in a menu and launched dynamically. Implementations of this interface
 * provide a specific Composable function to be rendered and a unique name to identify it.
 */
interface ComposeFunc {
    /**
     * A Composable function that encapsulates the UI to be displayed.
     * When called, this function will render the specific layout or component
     * defined by the implementation of this interface.
     */
    @Composable
    fun Run()

    /**
     * Provides a string representation of the composable function, typically its unique name.
     *
     * This name is used for display purposes in the UI (e.g., on menu buttons) and as a
     * key for identifying which composable to launch via an [Intent] extra.
     *
     * @return The name of the composable function.
     */
    override fun toString(): String
}
