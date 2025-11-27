package com.example.colormyviewscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colormyviewscompose.ui.theme.ColorMyViewsComposeTheme

/**
 * This is the main activity of our `ColorMyViewCompose`. It is a Compose version of the "Constraint
 * Layout using Layout Editor codelab" of the android-kotlin-fundamentals-apps course. (It uses
 * [Column] and [Row] instead of Compose `ConstraintLayout` because I was too lazy to learn how to
 * use Compose `ConstraintLayout` when I wrote it. I have since learned how to use Compose
 * `ConstraintLayout` so I am tempted to rewrite it when I have nothing better to do.)
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge]
     * to enable edge to edge display, then we call our super's
     * implementation of `onCreate`. Next we call [setContent] to Compose the composable which
     * consists of our custom material theme [ColorMyViewsComposeTheme] wrapped composable
     * [ColorMyViewApp] into our activity. The content will become the root view of our activity.
     * This is roughly equivalent to calling [ComponentActivity.setContentView] with a [ComposeView].
     * The [ColorMyViewsComposeTheme] composable is our custom [MaterialTheme] which defines the
     * default color palette, [Typography], and [Shapes] to be used by the [ColorMyViewApp]
     * composable.
     *
     * The [ColorMyViewApp] composable is wrapped in a [Box] whose `modifier` argument is a
     * [Modifier.safeDrawingPadding] to add padding to accommodate the safe drawing insets
     * as a kludge to adjust to the use of [enableEdgeToEdge].
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ColorMyViewsComposeTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    ColorMyViewApp()
                }
            }
        }
    }
}

/**
 * This Composable exists only to provide some flexibility in case I needed to hoist some variables
 * when I was writing the app. It also decreases the amount of indentation. Its content composes
 * [ColumnAndRowLayout] into the UI, passing it a [Modifier] which uses [Modifier.fillMaxSize] to
 * have [ColumnAndRowLayout] occupy the entire space allowed it, and [Modifier.wrapContentSize] to
 * have it align its children to the top center of its space.
 */
@Preview(showBackground = true)
@Composable
fun ColorMyViewApp() {
    ColumnAndRowLayout(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    )
}

/**
 * This is the actual main layout of our app. We [remember] five variables to hold the [Color]'s of
 * the background of the five [Text] widgets in our UI (all start out as [Color.White]):
 *  - `var boxOneColor` holds the [Color] of the "Box One" [Text]. It is the top box in the [Column]
 *  and occupies the entire width of the [Column]. When the box is clicked it turns its color to
 *  [Color.DarkGray].
 *  - `var boxTwoColor` holds the [Color] of the "Box Two" [Text]. It is the left most box in the
 *  second row of the layout, and is 130.dp by 130dp (it shares this [Row] with a [Column] that
 *  holds "Box Three", "Box Four" and "Box Five"). When the box is clicked it turns its color to
 *  [Color.DarkGray].
 *  - `var boxThreeColor` holds the [Color] of the "Box Three" [Text]. It is the top box in the
 *  [Column] of three boxes that shares the [Row] with "Box Two". When the box is clicked it changes
 *  its color to [Color.Blue], and when the [Button] labeled "RED" is clicked its color is changed
 *  to [Color.Red].
 *  - `var boxFourColor` holds the [Color] of the "Box Four" [Text]. It is the middle box in the
 *  [Column] of three boxes that shares the [Row] with "Box Two". When the box is clicked it changes
 *  its color to [Color.Magenta], and when the [Button] labeled "YELLOW" is clicked its color is
 *  changed to [Color.Yellow].
 *  - `var boxFiveColor` holds the [Color] of the "Box Five" [Text]. It is the bottom box in the
 *  [Column] of three boxes that shares the [Row] with "Box Two". When the box is clicked it changes
 *  its color to [Color.Blue], and when the [Button] labeled "GREEN" is clicked its color is
 *  changed to [Color.Green].
 *
 * @param modifier a [Modifier] for us to use to modify the looks and behavior of our contents.
 * The instance passed us by [ColorMyViewApp] uses [Modifier.fillMaxSize] to have us occupy the
 * entire space allowed us, and [Modifier.wrapContentSize] to have us align our children to the
 * top center of our space.
 */
@Composable
fun ColumnAndRowLayout(modifier: Modifier = Modifier) {
    var boxOneColor by remember {
        mutableStateOf(Color.White)
    }
    var boxTwoColor by remember {
        mutableStateOf(Color.White)
    }
    var boxThreeColor by remember {
        mutableStateOf(Color.White)
    }
    var boxFourColor by remember {
        mutableStateOf(Color.White)
    }
    var boxFiveColor by remember {
        mutableStateOf(Color.White)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.box_one),
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(boxOneColor)
                .padding(16.dp)
                .clickable {
                    boxOneColor = Color.DarkGray
                }
        )
        Row {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.box_two),
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(130.dp)
                    .height(130.dp)
                    .background(boxTwoColor)
                    .clickable {
                        boxTwoColor = Color.Gray
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.box_three),
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(boxThreeColor)
                        .clickable {
                            boxThreeColor = Color.Blue
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.box_four),
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(boxFourColor)
                        .clickable {
                            boxFourColor = Color.Magenta
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.box_five),
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(boxFiveColor)
                        .clickable {
                            boxFiveColor = Color.Blue
                        }
                )
            }
        }
        Row {
            Text(
                text = stringResource(id = R.string.how_to_play),
                fontSize = 24.sp
            )
            Text(
                text = stringResource(id = R.string.tap_the_boxes_and_buttons),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1.0f, fill = true))
        Row {
            Button(onClick = { boxThreeColor = Color.Red }) {
                Text(
                    text = stringResource(id = R.string.button_red)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { boxFourColor = Color.Yellow }) {
                Text(
                    text = stringResource(id = R.string.button_yellow)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { boxFiveColor = Color.Green }) {
                Text(
                    text = stringResource(id = R.string.button_green)
                )
            }
        }
    }
}