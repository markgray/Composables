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
 * TODO: Add kdoc
 */
class MainActivity : ComponentActivity() {
    private val composeKey = "USE_COMPOSE"

    private var cmap = listOf(
        get("FlowKeyPad") { FlowPad() },
        get("Calendar") { CalendarList() },


        )

    /**
     * TODO: Add kdoc
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

        val com = ComposeView(this)
        setContentView(com)
        com.setContent {
            Box(modifier = Modifier.safeDrawingPadding()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF0E7FC)
                ) {
                    if (cfunc != null) {
                        Log.v("MAIN", " running $cfunc")
                        cfunc.Run()
                    } else {
                        ComposableMenu(map = cmap) { act -> launch(act) }
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
            .padding(10.dp)
    ) {
        for (i in 0..(map.size-1)/2) {
            val cFunc1 = map[i*2]
            val cFunc2 = if ((i*2+1 < map.size)) map[i*2+1] else null
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { act(cFunc1) }) {
                    Text(cFunc1.toString(), modifier = Modifier.padding(2.dp))
                }
                if (cFunc2 != null) {
                    Button(onClick = { act(cFunc2) }) {
                        val s = cFunc2.toString().substring(cFunc2.toString().indexOf(' ')+1)
                        Text(s, modifier = Modifier.padding(2.dp))
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
