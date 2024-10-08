package com.example.aboutmecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aboutmecompose.ui.theme.AboutMeComposeTheme

/**
 * This is a Compose implementation of the Solution code for the Adding user Interactivity codelab:
 * `Android Kotlin Fundamentals 02.2: Add user interactivity.`It demonstrates:
 *  * Getting user input with an [OutlinedTextField].
 *  * Click handler for a [Button] to retrieve text from an [OutlinedTextField] and set it in a [Text].
 *  * Setting a click handler on a [Text].Shapes
 *  * Changing the visibility status of a Composable.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of [onCreate],
     * then we call [setContent] to Compose the composable which consists of our [AboutMeComposeTheme]
     * wrapped composable [AboutMeApp] into our activity. The content will become the root view of
     * our activity. This is roughly equivalent to calling [ComponentActivity.setContentView] with a
     * [ComposeView]. The [AboutMeComposeTheme] composable is our custom [MaterialTheme] which defines
     * the default color palette, [Typography], and [Shapes] to be used by the [AboutMeApp] composable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use, but it is
     * used by Compose to persist the values of several [MutableState] variables across configuration
     * changes.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.safeDrawingPadding()) {
                AboutMeComposeTheme {
                    AboutMeApp()
                }
            }
        }
    }
}

/**
 * This is the main Composable which creates the UI of the app. Its content just consists of a
 * [NameNicknameButtonAndFishtail] Composable which is called with a modifier of [Modifier.fillMaxSize]
 * which causes it to fill the entire screen. A call to [Modifier.wrapContentSize] is chained to that
 * modifier with [Alignment.Center] to have the content centered on the screen.
 */
@Preview(showBackground = true)
@Composable
fun AboutMeApp() {
    NameNicknameButtonAndFishtail(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    )
}

/**
 * This is the Composable which draws our UI. It uses four [rememberSaveable] variables to control
 * its state:
 *  - `var nickNameEntry` is a [String] that holds the content of whatever the user has entered so
 *  far in the "What is your Nickname?" [OutlinedTextField]
 *  - `var nickNameSaved` is the current string to be displayed in the nickname [Text] when the user
 *  clicks the "Done" [Button].
 *  - `var showDoneButton` is a [Boolean] flag which controls whether the "DONE" [Button] is shown.
 *  If `false` the "DONE" [Button] is not composed into the UI thanks to the [HideOrShow] Composable
 *  which wraps it.
 *  - `var showEnterNickNameTextField` is a [Boolean] flag which controls whether the "What is your
 *  Nickname?" [OutlinedTextField] is shown. If `false` it is not composed into the UI thanks to the
 *  [HideOrShow] Composable which wraps it.
 *
 * The layout uses a [Column] whose `modifier` is our `modifier` argument and whose `horizontalAlignment`
 * is [Alignment.CenterHorizontally] to have its children centered horizontally. The top widget is a
 * [Text] displaying "Alecks Haecky" (the author of the original app?), followed by a [HideOrShow]
 * whose `show` argument is `showEnterNickNameTextField` and whose `content` is a [OutlinedTextField]
 * whose `value` is `nickNameEntry` and whose `onValueChange` is a lambda which sets `nickNameEntry`
 * to the new value entered. Its label is a [Text] displaying "What is your Nickname?". Note that
 * this [OutlinedTextField] is only composed into the UI if `showEnterNickNameTextField` is `true`.
 * This is followed by a [HideOrShow] whose `show` argument is `showDoneButton` and whose `content`
 * is a [DoneButton] with an `onClick` lambda which sets `nickNameSaved` to `nickNameEntry`. The
 * content of the [HideOrShow] then includes an `if` statement which when `nickNameSaved` is not
 * the empty string sets both `showDoneButton` and `showEnterNickNameTextField` to `false`. Note that
 * this [DoneButton] is only composed into the UI if `showDoneButton` is `true`.
 *
 * Next in the [Column] is a [Text] displaying the current value of `nickNameSaved`. Its `modifier`
 * argument is a [Modifier.clickable] whose lambda sets `showDoneButton` and `showEnterNickNameTextField`
 * to `true` to allow the user the enter a new nickname, and sets `nickNameSaved` to the empty [String].
 * There follows a 40.dp by 40.dp [Box] holding an [Image] whose [Modifier.fillMaxSize] `modifier`
 * causes it to fill the [Box] with the [android.R.drawable.btn_star_big_on] (big yellow star) that
 * the `painter` [Painter] of the [Image] draws.
 *
 * The last widget of that [Column] is a [Column] which adds a [Modifier.verticalScroll] to the
 * `modifier` argument of its parent [Column] to be its `modifier` argument in order to make it
 * scrollable, and uses [Alignment.CenterHorizontally] as its `horizontalAlignment` argument to
 * center its children. The content of this [Column] consists of three [Text] widgets displaying
 * some trivia about fish.
 *
 * @param modifier a [Modifier] our parent can use to modify our looks and behavior. In our case our
 * parent uses [Modifier.fillMaxSize] to make us fill our available space and [Modifier.wrapContentSize]
 * to have use center align our content.
 */
@Composable
fun NameNicknameButtonAndFishtail(modifier: Modifier = Modifier) {
    /**
     * The current string that the user has entered in the "What is your Nickname?" [OutlinedTextField]
     */
    var nickNameEntry: String by rememberSaveable {
        mutableStateOf(value = "")
    }

    /**
     * The current string to be displayed in the nickname [Text] when the user clicks the "Done"
     * [Button].
     */
    var nickNameSaved: String by rememberSaveable {
        mutableStateOf(value = "")
    }

    /**
     * The visisility toggle for the "DONE" [Button].
     */
    var showDoneButton: Boolean by rememberSaveable {
        mutableStateOf(value = true)
    }

    /**
     * The visisility toggle for the "What is your Nickname?" [OutlinedTextField]
     */
    var showEnterNickNameTextField: Boolean by rememberSaveable {
        mutableStateOf(value = true)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.name),
            fontSize = 20.sp
        )
        HideOrShow(show = showEnterNickNameTextField) {
            OutlinedTextField(
                value = nickNameEntry,
                onValueChange = {
                    nickNameEntry = it
                },
                label = {
                    Text(stringResource(id = R.string.what_is_your_nickname))
                }
            )
        }
        HideOrShow(show = showDoneButton) {
            DoneButton(onClick = { nickNameSaved = nickNameEntry })
            if (nickNameSaved != "") {
                showDoneButton = false
                showEnterNickNameTextField = false
            }
        }
        Text(
            text = nickNameSaved,
            fontSize = 20.sp,
            modifier = Modifier.clickable {
                showDoneButton = true
                showEnterNickNameTextField = true
                nickNameSaved = ""
            }
        )
        Box(
            modifier = Modifier
                .height(height = 40.dp)
                .width(width = 40.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(fraction = 1f),
                painter = painterResource(id = android.R.drawable.btn_star_big_on),
                contentDescription = stringResource(id = R.string.yellow_star)
            )
        }
        Column(
            modifier = modifier.verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.bio),
                fontSize = 20.sp,
                modifier = modifier.padding(start = 8.dp, end = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.more_bio1),
                fontSize = 20.sp,
                modifier = modifier.padding(start = 8.dp, end = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.more_bio2),
                fontSize = 20.sp,
                modifier = modifier.padding(start = 8.dp, end = 8.dp)
            )
        }
    }
}

/**
 * The Composable used to render the "Done" [Button] in our UI.
 *
 * @param onClick the lambda to be called when the [Button] is clicked.
 */
@Composable
private fun DoneButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = stringResource(id = R.string.done))
    }
}

/**
 * If its [show] parameter is `true` this method will execute its [content] Composable lambda, if it
 * is `false` execution of the lambda will be skipped.
 *
 * @param show if `true` the [content] Composable will be executed.
 * @param content the Composable that this method should execute or skip depending on the value of
 * its [show] parameter.
 */
@Composable
private fun HideOrShow(
    show: Boolean = true,
    content: @Composable () -> Unit
) {
    if (show) {
        content()
    }

}