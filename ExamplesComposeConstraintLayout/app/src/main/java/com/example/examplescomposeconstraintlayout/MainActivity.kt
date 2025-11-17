package com.example.examplescomposeconstraintlayout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp

/**
 * Main activity for the application.
 *
 * This activity serves as a hub for displaying various Jetpack Compose examples.
 * When launched, it checks the intent for a specific composable to display.
 * If a composable name is provided via an intent extra (using [composeKey] as its key),
 * it renders that specific composable. Otherwise, it displays a menu
 * ([ComposableMenu]) of available examples.
 *
 * Each example is represented by a [ComposeFunc] object and can be launched
 * by selecting it from the menu, which restarts this activity with the
 * appropriate intent extra.
 *
 * @see ComposableMenu
 * @see ComposeFunc
 */
class MainActivity : ComponentActivity() {
    /**
     * The key used to retrieve the name of the composable to display from the intent extras.
     */
    private val composeKey: String = "USE_COMPOSE"

    /**
     * List of available composable functions.
     */
    private var cmap: List<ComposeFunc> = listOf(
        get(name = "FlowKeyPad") { FlowPad() },
        get(name = "Calendar") { CalendarList() }
    )

    /**
     * Initializes the activity.
     *
     * This method sets up the UI for the activity. It first checks the intent's extras
     * for an extra stored under the key [composeKey].
     *  - If a valid composable name is found, it finds the corresponding [ComposeFunc]
     *  from the [cmap] list and renders that specific composable.
     *  - If no composable name is provided in the intent, it displays the [ComposableMenu],
     *  which allows the user to select an example to view.
     *
     * The entire UI is built using Jetpack Compose within a [ComposeView]. Edge-to-edge
     * display is enabled for a modern, immersive layout.
     *
     * @param savedInstanceState  We do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val extra: Bundle? = intent.extras
        var cfunc: ComposeFunc? = null
        if (extra != null) {
            val composeName: String? = extra.getString(composeKey)
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
                        ComposableMenu(map = cmap) { act: ComposeFunc -> launch(toRun = act) }
                    }
                }
            }
        }
    }

    /**
     * Launches the MainActivity again to display a specific composable function.
     *
     * This method creates a new [Intent] to start the [MainActivity]. It adds the name of the
     * desired composable function, obtained by using the [ComposeFunc.toString] method of its
     * [ComposeFunc] parameter [toRun] as an extra to the intent under the key [composeKey].
     *
     * When the activity restarts, its `onCreate` method will read this extra and
     * display the corresponding composable instead of the main menu.
     *
     * @param toRun The [ComposeFunc] object representing the composable to be displayed.
     */
    private fun launch(toRun: ComposeFunc) {
        Log.v("MAIN", " launch $toRun")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(composeKey, toRun.toString())
        startActivity(intent)
    }
}

/**
 * A composable that displays a menu of buttons for launching different composable examples.
 *
 * This function arranges the provided list of composable functions into a two-column layout.
 * Each item in the list is represented by a button. When a button is clicked, the `act`
 * lambda is invoked with the corresponding [ComposeFunc] object, allowing the caller to
 * handle the navigation to the selected example.
 *
 * The layout consists of a [Column] of [Row]s. Each [Row] contains up to two buttons,
 * ensuring a responsive grid-like appearance. If there is an odd number of items, the
 * last row will only contain a single button.
 *
 * @param map A list of [ComposeFunc] objects, each representing a selectable composable example.
 * @param act A lambda function that is executed when a button is clicked. It receives the
 * [ComposeFunc] associated with the clicked button as its parameter.
 */
@Composable
fun ComposableMenu(
    map: List<ComposeFunc>,
    act: (act: ComposeFunc) -> Unit
) {
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
                    Text(
                        text = cFunc1.toString(),
                        modifier = Modifier.padding(2.dp)
                    )
                }
                if (cFunc2 != null) {
                    Button(onClick = { act(cFunc2) })
                    {
                        val s: String = cFunc2
                            .toString()
                            .substring(startIndex = cFunc2.toString().indexOf(char = ' ') + 1)
                        Text(text = s, modifier = Modifier.padding(all = 2.dp))
                    }
                }
            }
        }
    }
}

/**
 * A factory function that creates an instance of [ComposeFunc].
 *
 * This function is used to encapsulate a composable function along with a name.
 * It returns an anonymous object that implements the [ComposeFunc] interface.
 * The [String] parameter [name] is used for the `toString()` representation, which is
 * useful for identification and display in menus. The [cRun] lambda parameter contains
 * the actual composable UI content to be rendered when [ComposeFunc.Run] is called.
 *
 * @param name The string identifier for the composable function. This is used in the `toString()`
 * method and for lookup purposes.
 * @param cRun A lambda expression containing the `@Composable` function to be executed.
 * @return An object implementing the [ComposeFunc] interface.
 */
fun get(name: String, cRun: @Composable () -> Unit): ComposeFunc {
    return object : ComposeFunc {
        /**
         * Executes the composable function provided to the `get` factory.
         *
         * This method is the entry point for rendering the UI associated with this
         * [ComposeFunc] instance. It simply invokes the `cRun` lambda that was
         * captured when the object was created.
         */
        @Composable
        override fun Run() {
            cRun()
        }

        /**
         * Returns the name of the composable function.
         *
         * This name is used for identification, such as in intent extras and for display in the UI
         * (e.g., on buttons).
         *
         * @return The string name assigned to this composable function.
         */
        override fun toString(): String {
            return name
        }
    }
}

/**
 * Represents a self-contained, runnable composable screen.
 *
 * This interface is a wrapper for a composable function, allowing it to be treated
 * as an object that can be passed around, stored in a list, and identified by a name.
 * It's used to dynamically select and display different composable examples within the app.
 *
 * Implementations of this interface should define:
 *  1. The composable UI content to be displayed, within the [Run] method.
 *  2. A unique string identifier, returned by the [toString] method, used for lookup
 *  and display purposes (e.g., in a menu).
 *
 * @see get for a factory function to easily create instances of this interface.
 */
interface ComposeFunc {
    /**
     * A composable function that renders the UI content associated with this instance.
     *
     * Implementations of this method should define the Jetpack Compose UI
     * that will be displayed when this [ComposeFunc] is selected and executed.
     */
    @Composable
    fun Run()

    /**
     * Returns a string representation of the composable function, typically its name.
     *
     * This is used for identification purposes, such as in intent extras or for display
     * in UI elements like buttons in a menu.
     *
     * @return A [String] that uniquely identifies the composable function.
     */
    override fun toString(): String
}
