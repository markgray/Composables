package com.example.examplescomposemotionlayout

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
 * The main activity of the application, which serves as the entry point and navigation hub
 * for various Compose MotionLayout examples.
 *
 * This activity displays a menu of available examples. When an example is selected, it relaunches
 * itself with an [Intent] extra, which specifies which composable example to display. If no
 * specific example is requested via the intent, it defaults to showing the main menu.
 */
class MainActivity : ComponentActivity() {
    /**
     * Key for the [Intent] extra that specifies which composable function to display.
     * When an example is selected from the menu, the activity is relaunched with this key
     * in the [Intent]'s extras, and its value is the name of the composable to be displayed.
     */
    private val composeKey = "USE_COMPOSE"

    /**
     * A [List] of all available Compose MotionLayout examples.
     *
     * Each entry in the list is a [ComposeFunc] object, which pairs a human-readable name
     * (e.g., "CollapsingToolbar DSL") with the composable function that implements the example.
     * This list is used to build the main menu in [ComposableMenu] and to look up the
     * correct composable to display when an example is selected.
     */
    private var cmap = listOf(
        get(name = "CollapsingToolbar DSL") { ToolBarExampleDsl() },
        get(name = "CollapsingToolbar JSON") { ToolBarExample() },
        get(name = "ToolBarLazyExample DSL") { ToolBarLazyExampleDsl() },
        get(name = "ToolBarLazyExample JSON") { ToolBarLazyExample() },
        get(name = "MotionInLazyColumn Dsl") { MotionInLazyColumnDsl() },
        get(name = "MotionInLazyColumn JSON") { MotionInLazyColumn() },
        get(name = "DynamicGraph") { ManyGraphs() },
        get(name = "ReactionSelector") { ReactionSelector() },
        get(name = "MotionPager") { MotionPager() },
        get(name = "Puzzle") { Puzzle() },
        get(name = "MPuzzle") { MPuzzle() },
        get(name = "FlyIn") { M1FlyIn() },
        get(name = "DragReveal") { M2DragReveal() },
        get(name = "MultiState") { M3MultiState() },
    )

    /**
     * Called when the activity is first created.
     *
     * This function sets up the main content view of the activity. It checks if the [Intent]
     * that started the activity contains an extra with the key [composeKey].
     *  - If the extra is found, it looks up the corresponding `ComposeFunc` from the `cmap` list
     *  and renders that specific composable example. This allows the activity to display a
     *  single example when selected from the menu.
     *  - If no such extra is present (e.g., on the initial launch of the app), it displays
     *  the main menu defined by the [ComposableMenu], which lists all available examples.
     *
     * We start by calling [enableEdgeToEdge] to enable edge-to-edge display, then we call our
     * super's implementation of `onCreate`. We initialize our [Bundle] variable `extra` to the
     * [Bundle] stored in the [Intent.getExtras] of the [Intent] that launched us. We initialize
     * our [ComposeFunc] variable `cfunc` to `null`. If `extra` is not `null` we initialize our
     * [String] variable `composeName` to the value stored in `extra` with the key `composeKey`.
     * We then loop through our `cmap` list to find a matching [ComposeFunc] based on the
     * `composeName`. If a match is found, we set `cfunc` to that [ComposeFunc] and break out of
     * the loop. If no match is found, we continue to the next iteration of the loop.
     *
     * We initialize our [ComposeView] variable `com` to a new instance of [ComposeView] with the
     * context of `this` activity and set our content view to `com`. We call the
     * [ComposeView.setContent] method of `com` and in its `content` composable lambda argument we
     * compose a [Box] whose `modifier` argument is a [Modifier.safeDrawingPadding] to add padding
     * to accommodate the safe drawing insets (insets that include areas where content may be
     * covered by other drawn content. This includes all systemBars, displayCutout, and ime).
     * In the [BoxScope] `content` composable lambda argument of the [Box] we compose a [Surface]
     * whose `modifier` argument is a [Modifier.fillMaxSize] and whose `color` argument is a [Color]
     * with a hexadecimal value of `0xFFF0E7FC`. In the `content` composable lambda argument of the
     * [Surface] if our [ComposeFunc] variable `cfunc` is not `null` we log that we are running
     * `cfunc`, and call the [ComposeFunc.Run] method of `cfunc`, and if it is `null` we compose a
     * [ComposableMenu] whose `map` argument is `cmap` and in whose `act` lambda argument we accept
     * the [ComposeFunc] passed the lambda in variable `act` and call the [launch] method with that
     * [ComposeFunc] as the `toRun` argument.
     *
     * @param savedInstanceState We do not override [onSaveInstanceState] so we do not use this.
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
     * Relaunches the [MainActivity] to display a specific composable example.
     *
     * This function is called when a user selects an example from the [ComposableMenu]. It creates
     * a new [Intent] to start the [MainActivity] again. It adds the selected [ComposeFunc] example's
     * name as an extra to the intent, using [composeKey] as the key.
     *
     * When the activity restarts, the `onCreate` method will detect this extra and display the
     * corresponding composable instead of the main menu.
     *
     * @param toRun The [ComposeFunc] object representing the example to be launched. The value
     * returned by the `toString()` method of this object is used as the value for the intent extra.
     */
    private fun launch(toRun: ComposeFunc) {
        Log.v("MAIN", " launch $toRun")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(composeKey, toRun.toString())
        startActivity(intent)

    }
}

/**
 * A Composable function that displays a two-column menu of buttons.
 *
 * This function takes a list of [ComposeFunc] objects and arranges them into a grid-like
 * menu. Each item in the list becomes a button. The layout is structured as a [Column]
 * containing multiple [Row]s. Each [Row] contains up to two buttons, creating a two-column
 * effect. The buttons are spaced apart evenly within each row.
 *
 * When a button is clicked, the [act] lambda parameter is invoked with the corresponding
 * [ComposeFunc] object, allowing the caller to define the action to be taken, such as
 * launching a new screen or example.
 *
 * The text for the first button in a row displays the full name of the [ComposeFunc]. The text for
 * the second button in a row is shortened to display only the part of the name after the first
 * space, which helps in differentiating related examples (e.g., "DSL" vs. "JSON").
 *
 * Our root composable is a [Column] whose `modifier` argument is a [Modifier.fillMaxWidth] chained
 * to a [Modifier.padding] that adds 10.dp to `all` sides. In the [ColumnScope] `content` composable
 * lambda argument of the [Column] we loop over `i` between 0 and half the size of the `map` list
 * minus 1. Inside the loop we initialize [ComposeFunc] variable `cFunc1` to the entry at index
 * `i * 2` of the `map` list, and initialize [ComposeFunc] variable `cFunc2` to the entry at index
 * `i * 2 + 1` of the `map` list, if it exists otherwise `null`. Then we compose a [Row] whose
 * `modifier` argument is a [Modifier.fillMaxWidth] and whose `horizontalArrangement` argument is a
 * [Arrangement.SpaceBetween]. In the [RowScope] `content` composable lambda argument of the [Row]
 * we:
 *  1. Compose a [Button] whose `onClick` argument is a lambda that calls the `act` lambda with
 *  the value of [ComposeFunc] variable `cFunc1`. In the [RowScope] `content` composable lambda
 *  argument of the [Button] we compose a [Text] whose `text` argument is the [String] value of
 *  [ComposeFunc] variable `cFunc1` and whose `modifier` argument is a [Modifier.padding] that
 *  adds 2.dp to `all` sides
 *  2. If [ComposeFunc] variable `cFunc2` is not `null` we compose another [Button] whose `onClick`
 *  argument is a lambda that calls the `act` lambda with the value of [ComposeFunc] variable
 *  `cFunc2`. In the [RowScope] `content` composable lambda argument of the [Button] we initialize
 *  our [String] variable `s` to the [String] value of [ComposeFunc] variable `cFunc2` starting 1
 *  character after the first space character. Then we compose a [Text] whose `text` argument is
 *  `s` and whose `modifier` argument is a [Modifier.padding] that adds 2.dp to `all` sides.
 *
 * @param map A [List] of [ComposeFunc] objects, where each object represents a menu item.
 * Each item will be displayed as a button.
 * @param act A lambda function that will be executed when a button in the menu is clicked.
 * The lambda receives the [ComposeFunc] associated with the clicked button as its parameter.
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
 * A factory function that creates an instance of the [ComposeFunc] interface.
 *
 * This function simplifies the creation of runnable composable examples for the main menu.
 * It takes a human-readable [name] and a composable lambda [cRun], and wraps them in an
 * anonymous object that implements the [ComposeFunc] interface.
 *
 * The `toString()` method of the returned object is overridden to return the provided [name],
 * which is used for display in the UI (e.g., button text) and for identification in intents.
 * The `Run()` method simply invokes the [cRun] composable lambda, running the actual example.
 *
 * @param name The [String] name of the composable example. This is used as the unique identifier
 * and display text for the menu item.
 * @param cRun A composable lambda function (`@Composable () -> Unit`) that contains the UI
 * code for the example. This lambda will be executed when the example is run.
 * @return An object that implements the [ComposeFunc] interface, ready to be added to the
 * list of examples.
 */
fun get(name: String, cRun: @Composable () -> Unit): ComposeFunc {
    return object : ComposeFunc {
        /**
         * A composable function that executes and displays the UI for this specific example.
         * When this function is called, it will render the user interface defined by the
         * [cRun] composable lambda that was provided when this [ComposeFunc] instance was created
         * using the [get] factory function.
         */
        @Composable
        override fun Run() {
            cRun()
        }

        /**
         * Returns the name of the composable example.
         *
         * This name is used as a unique identifier for the example. It is displayed in the UI
         * (e.g., as button text in the main menu) and is used as an extra in the [Intent]
         * to specify which example to launch.
         *
         * @return The [String] name of the example.
         */
        override fun toString(): String {
            return name
        }
    }
}

/**
 * An interface that represents a runnable composable example.
 *
 * This interface provides a common structure for all the MotionLayout examples displayed in the
 * main menu. It allows each example to be treated as a self-contained unit with a name
 * and a composable function to execute.
 *
 * The [toString] method provides a human-readable name for the example, which is used for display
 * in the UI (e.g., button labels) and as a unique key when launching the example via an [Intent].
 *
 * The [Run] method is a composable function that contains the actual UI implementation of the
 * example.
 */
interface ComposeFunc {
    /**
     * A composable function that executes and displays the UI for the specific example.
     * When this function is called, it will render the user interface defined by the
     * composable lambda provided when the [ComposeFunc] instance was created.
     */
    @Composable
    fun Run()

    /**
     * Provides a human-readable name for the example.
     *
     * This name is used for display in the UI (e.g., as button text in the main menu)
     * and serves as a unique identifier when launching the example via an [Intent].
     * The `MainActivity` uses this string to look up and display the correct composable.
     *
     * @return A [String] representing the unique name of the composable example.
     */
    override fun toString(): String
}
