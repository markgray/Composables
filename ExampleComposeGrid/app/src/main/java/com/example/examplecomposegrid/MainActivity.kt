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
 * TODO: Add kdoc
 */
class MainActivity : ComponentActivity() {
    private val composeKey = "USE_COMPOSE"

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
     * TODO: Add kdoc
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
     * TODO: Add kdoc
     */
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
