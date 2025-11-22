package androidx.demo.motiondemos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.DebugFlags
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.demo.motiondemos.ui.theme.MotionDemosTheme

/**
 * To run this demo you need to use "Edit Configurations -> Launch -> Specified Activity.
 */
class MainActivityStart : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to edge
     * display, then we call  our super's implementation of `onCreate`. Next we call [setContent] to
     * have it Compose the Composable we pass it as its `content` argument into our activity as our
     * root view. That Composable is wrapped in our [MotionDemosTheme] custom [MaterialTheme] and
     * consists of a [Box] whose `modifier` argument is a [Modifier.safeDrawingPadding] to add
     * padding to accommodate the safe drawing insets (insets that include areas where content may
     * be covered by other drawn content. This includes all system bars, display cutout, and soft
     * keyboard). Inside the [Box] is a [Surface] whose `modifier` argument is a
     * [Modifier.fillMaxSize] to have it occupy its entire incoming [Constraints], and whose `color`
     * argument is the [Colors.background] color of [MaterialTheme.colors] which sets the background
     * color of the [Surface] to the default [Color.White] since [MotionDemosTheme] does not override
     * it. The `content` of the [Surface] is our [CycleScale] [Composable].
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MotionDemosTheme {
                // A surface container using the 'background' color from the theme
                Box(modifier = Modifier.safeDrawingPadding()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        CycleScale()
                    }
                }
            }
        }
    }
}

/**
 * This Composable consists of a [MotionLayout] whose [MotionScene] is defined by a JSON string.
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "motion8")
@Composable
fun CycleScale() {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(6000), label = ""
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .background(Color.White),
            motionScene = MotionScene("""{
                   Debug: {
                  name: 'Cycle30'
                },
                ConstraintSets: {
                  start: {
                    cover: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    },
                 run: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start', 64],
                      bottom: ['parent', 'bottom', 64],
                         end: ['parent', 'end', 64],
                      top: ['parent', 'top', 64]
                    },
                  edge: {
                     width: 'spread',
                      height: 14,
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                         end: ['parent', 'end', 16],
                        alpha: 0,
                    },
                    },
                  end: {
                    cover: {
                      width: 'spread',
                      height: 'spread',
                      rotationX: -90,
                      pivotX: 0.5,
                      pivotY: 0,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16],
                      start: ['parent', 'start', 16],
                      bottom: ['parent', 'bottom', 16],
                    },
                     run: {
                      width: 'spread',
                      height: 'spread',
                      start: ['parent', 'start', 64],
                      bottom: ['parent', 'bottom', 64],
                         end: ['parent', 'end', 64],
                      top: ['parent', 'top', 64]
                    },
                  edge: {
                       width: '50%',
                      height: 14,
                      start: ['parent', 'start', 16],
                    
                         end: ['parent', 'end', 16],
                          top: ['parent', 'top', 16],
                          alpha: 0,
                  }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                  onSwipe: {
                  mode: 'spring',
                direction: 'up',
                   anchor: 'edge',
                side: 'top',
                springBoundary: 'down',
                springStiffness: 800,
                springDamping: 32
                  },
                  
                  }
                }
            }"""),
            debugFlags = DebugFlags.None,
            progress = progress) {
            Button(modifier = Modifier
                .layoutId("run"),
                onClick = { /* Start Engine */ },
                shape = RoundedCornerShape(40)
            ) {
                Text(text = "Start\nEngine")

            }
            Box(modifier = Modifier
                .layoutId("cover")
                .clip(
                    RoundedCornerShape(
                        bottomEnd = 32.dp, bottomStart = 32.dp
                    )
                )
                .background(Color.Red))

            Box(modifier = Modifier
                .layoutId("edge")
                .background(Color.Green))

        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}
