package com.example.aboutmecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aboutmecompose.ui.theme.AboutMeComposeTheme
import com.example.aboutmecompose.ui.theme.Shapes
import com.example.aboutmecompose.ui.theme.Typography

/**
 * This is a Compose implementation of the Solution code for the Adding user Interactivity codelab:
 * `Android Kotlin Fundamentals 02.2: Add user interactivity.`It demonstrates:
 *  * Getting user input with an [OutlinedTextField].
 *  * Click handler for a [Button] to retrieve text from an [OutlinedTextField] and set it in a [Text].
 *  * Setting a click handler on a [Text].
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
        super.onCreate(savedInstanceState)
        setContent {
            AboutMeComposeTheme {
                AboutMeApp()
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
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun NameNicknameButtonAndFishtail(modifier: Modifier = Modifier) {
    /**
     * The current string that the user has entered in the "What is your Nickname?" [OutlinedTextField]
     */
    var nickNameEntry by rememberSaveable {
        mutableStateOf("")
    }

    /**
     * The current string to be displayed in the nickname [Text] when the user clicks the "Done"
     * [Button].
     */
    var nickNameSaved by rememberSaveable {
        mutableStateOf("")
    }

    /**
     * The visisility toggle for the "DONE" [Button].
     */
    var showDoneButton by rememberSaveable {
        mutableStateOf(true)
    }

    /**
     * The visisility toggle for the "What is your Nickname?" [OutlinedTextField]
     */
    var showEnterNickNameTextField by rememberSaveable {
        mutableStateOf(true)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.name),
            fontSize = 20.sp
        )
        HideOrShow(showEnterNickNameTextField) {
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
        HideOrShow(showDoneButton) {
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
        Box(modifier = Modifier
            .height(40.dp)
            .width(40.dp)) {
            Image(
                modifier =Modifier.fillMaxSize(1f),
                painter = painterResource(id = android.R.drawable.btn_star_big_on),
                contentDescription = stringResource(id = R.string.yellow_star)
            )
        }
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()),
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