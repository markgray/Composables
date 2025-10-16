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
     * We initialize our [ComposeView] variable `com` to a new [ComposeView] with the context of
     * `this` activity and set our content view to `com`. We call the [ComposeView.setContent]
     * method of `com` and in its `content` composable lambda argument we compose a [Box]
     * TODO: Continue here.
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

    private fun launch(toRun: ComposeFunc) {
        Log.v("MAIN", " launch $toRun")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(composeKey, toRun.toString())
        startActivity(intent)

    }
}

/**
 * TODO: Add kdoc
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
 * TODO: Add kdoc
 */
fun get(name: String, cRun: @Composable () -> Unit): ComposeFunc {
    return object : ComposeFunc {
        @Composable
        override fun Run() {
            cRun()
        }

        override fun toString(): String {
            return name
        }
    }
}

/**
 * TODO: Add kdoc
 */
interface ComposeFunc {
    /**
     * TODO: Add kdoc
     */
    @Composable
    fun Run()
    override fun toString(): String
}
