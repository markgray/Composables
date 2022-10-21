/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.interests

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.R
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.data.interests.TopicsMap
import com.example.jetnews.ui.components.InsetAwareTopAppBar
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.JetnewsNavGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Stateful InterestsScreen that handles the interaction with the repository. First we use the
 * [rememberCoroutineScope] method to create and remember a [CoroutineScope] which we use to
 * initialize our variable `val coroutineScope`. We initialize our [Set] of [TopicSelection]
 * variable `val selectedTopics` by using the [Flow.collectAsState] method with an initial value
 * of an empty set on the [Flow] returned by the [InterestsRepository.observeTopicsSelected] method
 * of our [interestsRepository] parameter. We initialize our variable `val onTopicSelect` to a
 * lambda which takes a [TopicSelection] as its parameter then uses the [CoroutineScope.launch]
 * method of our `coroutineScope` variable to launch a new coroutine whose lambda block calls the
 * [InterestsRepository.toggleTopicSelection] method of our [interestsRepository] parameter with
 * the [TopicSelection] passed it. Finally we call the stateless interest screen with the arguments:
 *  - `topics` the [Map] of [String] to [List] of [String] field [InterestsRepository.topics] of our
 *  [interestsRepository] parameter.
 *  - `selectedTopics` our [Set] of [TopicSelection] variable `selectedTopics` (which is wrapped in
 *  a [State] so that any change to it will cause a recomposition of every [State.value] usage).
 *  - `onTopicSelect` our `onTopicSelect` variable.
 *  - `openDrawer` our [openDrawer] parameter
 *  - `modifier` our [modifier] parameter
 *  - `scaffoldState` our [scaffoldState] parameter
 *
 * @param interestsRepository data source for this screen
 * @param openDrawer (event) request opening the app drawer
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller, the [JetnewsNavGraph] Composable, does not pass one so the empty, default,
 * or starter [Modifier] that contains no elements is used.
 * @param scaffoldState (state) state for screen [Scaffold]
 */
@Composable
fun InterestsScreen(
    interestsRepository: InterestsRepository,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    // Returns a [CoroutineScope] that is scoped to the lifecycle of [InterestsScreen]. When this
    // screen is removed from composition, the scope will be cancelled.
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    // collectAsState will read a [Flow] in Compose
    val selectedTopics: Set<TopicSelection> by interestsRepository
        .observeTopicsSelected()
        .collectAsState(setOf())
    val onTopicSelect: (TopicSelection) -> Unit = { topic: TopicSelection ->
        coroutineScope.launch { interestsRepository.toggleTopicSelection(topic) }
    }
    InterestsScreen(
        topics = interestsRepository.topics,
        selectedTopics = selectedTopics,
        onTopicSelect = onTopicSelect,
        openDrawer = openDrawer,
        modifier = modifier,
        scaffoldState = scaffoldState
    )
}

/**
 * Stateless interest screen displays the topics the user can subscribe to. Our root Composable is a
 * [Scaffold] whose `scaffoldState` argument is our [ScaffoldState] parameter [scaffoldState], and
 * whose `topBar` argument is an [InsetAwareTopAppBar]. The `title` argument of the [InsetAwareTopAppBar]
 * is a lambda which composes a [Text] displaying the `text` "Interests", the `navigationIcon`openDrawer
 * argument is an [IconButton] whose `onClick` argument is our [openDrawer] parameter, and whose
 * `content` is an [Icon] whose `painter` draws the drawable with resource ID [R.drawable.ic_jetnews_logo]
 * (which is a "greater than" symbol followed by an underline). The `content` of the [Scaffold] is a
 * [LazyColumn] whose `modifier` argument adds a [Modifier.padding] of the [PaddingValues] passed to
 * the `content` of the [Scaffold] in the `padding` variable to the [modifier] parameter of
 * [InterestsScreen]. The `content` of the [LazyColumn] is provided by a [forEach] loop over the
 * entries in our [TopicsMap] parameter [topics] with the [String] variable `section` holding the
 * map key, and the [List] of [String] variable `topicsList` holding the value. The loop creates
 * an `item` which holds a [Text] Composable whose `text` displays the current `section` value,
 * and an [items] created from the current `topicsList` [List] of [String] each `topic` in that
 * [List] is then used as the `itemTitle` of a [TopicItem] Composable. The `selected` argument of
 * the [TopicItem] is `true` if our [Set] of [TopicSelection] parameter [selectedTopics] contains
 * a [TopicSelection] constructed from `section` and `topic`, and its `onToggle` argument is a call
 * to our [onTopicSelect] parameter with a [TopicSelection] constructed from `section` and `topic`.
 * The [items] is followed by a [TopicDivider] (our full-width divider for topics)
 *
 * @param topics (state) topics to display, mapped by section
 * @param selectedTopics (state) currently selected topics
 * @param onTopicSelect (event) request a topic selection be changed
 * @param openDrawer (event) request opening the app drawer
 * @param scaffoldState (state) the state for the screen's [Scaffold]
 * @param modifier a [Modifier] instance our caller can use to modify our appearance and/or behavior.
 * Our caller (the Stateful InterestsScreen) passes us its own `modifier` parameter which is the
 * empty, default, or starter [Modifier] that contains no elements since its caller did not pass one
 * to it.
 */
@Composable
fun InterestsScreen(
    topics: TopicsMap,
    selectedTopics: Set<TopicSelection>,
    onTopicSelect: (TopicSelection) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            InsetAwareTopAppBar(
                title = { Text(text = "Interests") },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_jetnews_logo),
                            contentDescription = stringResource(id = R.string.cd_open_navigation_drawer)
                        )
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        LazyColumn(
            modifier = modifier.padding(paddingValues = padding)
        ) {
            topics.forEach { (section: String, topicsList: List<String>) ->
                item {
                    Text(
                        text = section,
                        modifier = Modifier.padding(all = 16.dp),
                        style = MaterialTheme.typography.subtitle1
                    )
                }
                items(topicsList) { topic: String ->
                    TopicItem(
                        itemTitle = topic,
                        selected = selectedTopics
                            .contains(TopicSelection(section = section, topic = topic)),
                        onToggle = {
                            onTopicSelect(TopicSelection(section = section, topic = topic))
                        }
                    )
                    TopicDivider()
                }
            }
        }
    }
}

/**
 * Display a full-width topic item. First we initialize our [Painter] variable `val imagePainter`
 * to an instance which will draw the drawable with resource ID [R.drawable.placeholder_1_1] (a
 * triangle intersecting a circle), initialize our [String] variable `val stateNotSubscribed` to
 * the string with resource ID [R.string.state_not_subscribed] ("not subscribed"), and our [String]
 * variable `val stateSubscribed` to the string with resource ID [R.string.state_subscribed]
 * ("subscribed"). Our root Composable is a [Row] whose `modifier` argument is a [Modifier.semantics]
 * whose `properties` argument is a lambda which sets the [SemanticsPropertyReceiver.stateDescription]
 * of its receiver to `stateSubscribed` if `selected` is `true` or to `stateNotSubscribed` if it is
 * `false`. A [Modifier.toggleable] (configures component to make it toggleable via input and
 * accessibility events) is appended to that [Modifier] whose `value` is our [Boolean] parameter
 * [selected], whose `onValueChange` argument (callback to be invoked when toggleable is clicked) is
 * a lambda calling our [onToggle] lambda parameter, and whose `role` argument (the type of user
 * interface element) is [Role.Checkbox] (element is a Checkbox which is a component that represents
 * two states checked or unchecked). And then a [Modifier.padding] is appended which sets the
 * `horizontal` padding along the left and right edges of the content to 16.dp, and the `vertical`
 * padding along the top and bottom edges to 8.dp
 *
 * The `content` of the [Row] is an [Image], a [Text], a [Spacer], and a [Checkbox].
 *
 * @param itemTitle (state) topic title
 * @param selected (state) is topic currently selected
 * @param onToggle (event) toggle selection for topic
 */
@Composable
private fun TopicItem(itemTitle: String, selected: Boolean, onToggle: () -> Unit) {
    val imagePainter: Painter = painterResource(id = R.drawable.placeholder_1_1)
    val stateNotSubscribed: String = stringResource(id = R.string.state_not_subscribed)
    val stateSubscribed: String = stringResource(id = R.string.state_subscribed)
    Row(
        modifier = Modifier
            .semantics {
                stateDescription = if (selected) {
                    stateSubscribed
                } else {
                    stateNotSubscribed
                }
            }
            .toggleable(
                value = selected,
                onValueChange = { onToggle() },
                role = Role.Checkbox
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .size(width = 56.dp, height = 56.dp)
                .clip(shape = RoundedCornerShape(size = 4.dp))
        )
        Text(
            text = itemTitle,
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .padding(all = 16.dp),
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(Modifier.weight(weight = 1f))
        Checkbox(
            checked = selected,
            onCheckedChange = null,
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}

/**
 * Full-width divider for topics
 */
@Composable
private fun TopicDivider() {
    Divider(
        modifier = Modifier.padding(start = 90.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
    )
}

/**
 * These Previews show what the [JetnewsTheme] custom [MaterialTheme] wrapped [InterestsScreen]
 * looks like for different device configurations.
 */
@Preview("Interests screen", "Interests")
@Preview("Interests screen (dark)", "Interests", uiMode = UI_MODE_NIGHT_YES)
@Preview("Interests screen (big font)", "Interests", fontScale = 1.5f)
@Preview("Interests screen (large screen)", "Interests", device = Devices.PIXEL_C)
@Composable
fun PreviewInterestsScreen() {
    JetnewsTheme {
        InterestsScreen(
            interestsRepository = InterestsRepository(),
            openDrawer = {}
        )
    }
}
