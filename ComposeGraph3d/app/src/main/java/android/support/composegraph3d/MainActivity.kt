@file:Suppress("UNUSED_VARIABLE")

package android.support.composegraph3d

import android.os.Bundle
import android.support.composegraph3d.lib.Graph
import android.support.composegraph3d.ui.theme.ComposeGraph3dTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.isActive

/**
 * TODO: Add kdoc
 */
class MainActivity : ComponentActivity() {
    /**
     * TODO: Add kdoc
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeGraph3dTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
fun Greeting(name: String) {
    val w = 600
    val h = 600
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        Text(text = "Hello $name!")
        Graph3D(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.White))
        Text(text = "Hello $name!")
    }
}


/**
 * TODO: Add kdoc
 */
@Composable
fun Graph3D(modifier: Modifier) {
    val graph = remember { Graph() }
    val time = remember { mutableLongStateOf(System.nanoTime()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameNanos {
                time.longValue = System.nanoTime()
                graph.getImageForTime(time.longValue)
            }
        }
    }

    Canvas(modifier = modifier
        .onPlaced {
            graph.setSize(it.size.width, it.size.width)
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    graph.dragStart(it)
                },
                onDragEnd = {
                    graph.dragStopped()
                },
                onDragCancel = {
                    graph.dragStopped()
                },
                onDrag = { change, dragAmount ->
                    graph.drag(change, dragAmount)
                }
            )
        }
    ) {
        time.longValue // Cute: reads cause recomposition when `time` changes value in LaunchedEffect
        scale(scale = 2.0f, pivot = Offset(0f, 0f)) {
            drawImage(graph.bitmap)
        }
    }

}

/**
 * TODO: Add kdoc
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeGraph3dTheme {
        Greeting("Android")
    }
}