package com.example.android.composetutorial

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.composetutorial.ui.theme.ComposeTutorialTheme

/**
 * This is the result of completing the "Jetpack Compose Tutorial" that is to be found at:
 *
 *     https://developer.android.com/jetpack/compose/tutorial
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`.
     * Then we call [setContent] to have it compose the lambda we pass as its `content` argument into
     * our activity. That lambda consists of our [ComposeTutorialTheme] custom [MaterialTheme] wrapping
     * our [Conversation] Composable which is called with the [List] of [Message] that is to be found
     * in [SampleData.conversationSample]. It will display each [Message] in the [List] using a
     * [MessageCard] in a [LazyColumn].
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use this.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorialTheme {
                Conversation(SampleData.conversationSample)
            }
        }
    }
}

/**
 * The data class we use to hold a [Message].
 *
 * @param author a [String] identifying the author of the [Message].
 * @param body the contents of the message.
 */
data class Message(val author: String, val body: String)

/**
 * This Composable displays a single [Message], and toggles the number of lines of the [Message.body]
 * displayed between one line and the entire string when it is clicked. The layout consists of a [Row]
 * whose `modifier` uses [Modifier.padding] with its `all` parameter specified as 8.dp to add 8.dp
 * padding to all sides of the [Row]. The `contents` of the [Row] consists of an [Image] displaying
 * the jpg with resource ID [R.drawable.shakespeare_droeshout_1623] whose `modifier` uses
 * [Modifier.size] to set its size to 40.dp by 40.dp, [Modifier.clip] to clip it to a [CircleShape],
 * and [Modifier.border] to add a border of 1.5.dp of the `secondaryVariant` color of
 * [MaterialTheme.colors] (a dark teal). This is followed by a [Spacer] of 8.dp, and a [Column] with
 * a [Modifier.clickable] `modifier` argument whose lambda toggles the value of the remembered
 * [Boolean] variable `isExpanded` when it is clicked. This [Column] holds a [Text] which displays
 * the [Message.author] field or our [msg] parameter followed by a [Spacer] of 4.dp and a [Surface]
 * which holds a [Text] which displays the [Message.body] field or our [msg] parameter. The `color`
 * of the [Surface] is `surfaceColor` which animates between the `primary` color and the `surface`
 * color of [MaterialTheme.colors] depending on the value of `isExpanded`, and the `modifier` of the
 * [Surface] uses [Modifier.animateContentSize] to change the [Surface] size gradually when its child
 * [Text] Composable changes size. The [Text] sets the value of its `maxLines` parameter to 1 if
 * `isExpanded` is `false` (displays only the first line of the [Message.body] of [msg]) or to
 * [Int.MAX_VALUE] if it is `true` (displays the entire [Message.body] of [msg]).
 *
 * @param msg the [Message] we are to display.
 */
@Composable
fun MessageCard(msg: Message) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.shakespeare_droeshout_1623),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colors.secondaryVariant, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // We keep track if the message is expanded or not in this variable
        var isExpanded by remember { mutableStateOf(false) }
        // surfaceColor will be updated gradually from one color to the other
        val surfaceColor: Color by animateColorAsState(
            if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        )

        // We toggle the isExpanded variable when we click on this Column
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = msg.author,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp,
                // surfaceColor color will be changing gradually from primary to surface
                color = surfaceColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    // If the message is expanded, we display all its content
                    // otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

/**
 * This Composable displays the fake data found in its [List] of [Message] argument [messages] in a
 * [LazyColumn] using [MessageCard] objects for each [Message].
 *
 * @param messages the [List] of [Message] objects we should display in a [LazyColumn].
 */
@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

/**
 * This is just a [ComposeTutorialTheme] wrapped preview of our [Conversation] Composable using the
 * sample data found in[SampleData.conversationSample],
 */
@Preview
@Composable
fun PreviewConversation() {
    ComposeTutorialTheme {
        Conversation(SampleData.conversationSample)
    }
}

/**
 * This is two previews of a [ComposeTutorialTheme] wrapped [MessageCard] Composable of a hard coded
 * [Message]. The preview named "Light Mode" uses the default `LightColorPalette` [lightColors] colors
 * of our [ComposeTutorialTheme] while the preview named "Dark Mode" preview sets `uiMode` to
 * [Configuration.UI_MODE_NIGHT_YES] and this causes the `DarkColorPalette` [darkColors] colors of
 * our [ComposeTutorialTheme] to be used.
 */
@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {
    ComposeTutorialTheme {
        MessageCard(
            msg = Message("Colleague", "Take a look at Jetpack Compose, it's great!")
        )
    }
}
