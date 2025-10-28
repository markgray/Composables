package androidx.demo.composedemos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import androidx.demo.composedemos.ui.theme.ComposeDemosTheme
import java.util.*

/**
 * The main activity for the Compose Demos application.
 *
 * This activity serves as the entry point of the app and sets up the main UI content
 * using Jetpack Compose. It enables edge-to-edge display and renders the [Login]
 * composable as its primary view within the app's theme.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides a
     * Bundle containing the activity's previously frozen state, if there was one.
     *
     * This implementation sets up the main content view using Jetpack Compose. It enables
     * edge-to-edge display, applies the [ComposeDemosTheme], and displays the [Login]
     * composable within a `Surface` that fills the entire screen.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in
     * `onSaveInstanceState(Bundle)`. Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDemosTheme {
                // A surface container using the 'background' color from the theme
                Box(modifier = Modifier.safeDrawingPadding()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        Login()
                    }
                }
            }
        }
    }
}

/**
 * A Composable function that provides a preview of the [Login] screen.
 *
 * This function is annotated with `@Preview`, making it visible in the Android Studio design pane.
 * It wraps the [Login] composable within the [ComposeDemosTheme] to ensure that the preview
 * accurately reflects the app's visual styling. The `showBackground = true` parameter adds a
 * default background to the preview, making the UI components more visible.
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeDemosTheme {
        Login()
    }
}

/**
 * A Composable function demonstrating a login screen animation using [MotionLayout].
 *
 * This screen consists of two [BasicTextField] composables for "Name" and "Password"
 * within a [MotionLayout]. The [MotionLayout]'s state is defined by a JSON-based
 * [MotionScene] which describes the start and end `ConstraintSets` for the fields.
 *
 * An animation between these two states is triggered by a "Run" button. When clicked,
 * the `animateToEnd` state toggles, which drives the `progress` of the `MotionLayout`
 * via `animateFloatAsState`. This creates a smooth 6-second transition where the
 * text fields move from the bottom of the layout to the top.
 *
 * The [MotionScene] also includes `KeyFrames` to modify the "name" field's scale
 * during the transition, creating a more dynamic visual effect. Debug flags are enabled
 * to show paths and progress for development purposes.
 */
@OptIn(ExperimentalMotionApi::class)
@Composable
fun  Login() {
    var animateToEnd: Boolean by remember { mutableStateOf(value = false) }

    val progress: Float by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f,
        animationSpec = tween(durationMillis = 6000),
        label = ""
    )
    Column {
        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 400.dp)
                .background(color = Color.White),
            motionScene = MotionScene("""{
                ConstraintSets: {
                  start: {                
                    name: {
                      width: 'spread',
                      start: ['parent', 'start', 56],
                       end: ['parent', 'end', 56],
                      bottom: ['parent', 'bottom', 56]
                    },
                   password: {
                      width: {value:'spread', max:100},
                      hBias: 0,
                      //rotationZ: 390,
                       start: ['name', 'start', 0],
                        end: ['parent', 'end', 56],
                      bottom: ['parent', 'bottom', 16]
                    }
                  },
                  end: {
                    name: {
                      width: 'spread',                   
                      //rotationZ: 390,
                      end: ['parent', 'end', 56],
                      top: ['parent', 'top', 56]
                    },
                  password: {                     
                      //rotationZ: 390,
                      end: ['parent', 'end', 16],
                      top: ['parent', 'top', 16]
                    }
                  }
                },
                Transitions: {
                  default: {
                    from: 'start',
                    to: 'end',
                    pathMotionArc: 'startHorizontal',
                    KeyFrames: {
                      KeyAttributes: [
                        {
                          target: ['name'],
                          frames: [25, 50],
                          scaleX: 3,
                          scaleY: .3
                        }
                      ]
                    }
                  }
                }
            }"""),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL),
            progress = progress) {
            var name: TextFieldValue by remember {
                mutableStateOf(value = TextFieldValue(text = "Name "))
            }
            var password: TextFieldValue by remember {
                mutableStateOf(value = TextFieldValue(text = "Password"))
            }

             BasicTextField(modifier = Modifier
                 .layoutId(layoutId = "name")
                 .background(color = Color.Gray),
                 value = name,
                 onValueChange = { name = it }
             )

            BasicTextField(modifier = Modifier
                .layoutId(layoutId = "password")
                .background(color = Color.Gray),
                value = password,
                onValueChange = {  password = it  }
            )

        }

        Button(onClick = { animateToEnd = !animateToEnd }) {
            Text(text = "Run")
        }
    }
}
