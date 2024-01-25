/*
 * Copyright 2020 The Android Open Source Project
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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.compose.jetchat.conversation

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.profile.ProfileFragment
import com.example.compose.jetchat.profile.ProfileScreen
import com.example.compose.jetchat.profile.ProfileScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Entry point for a conversation screen, it is used in a call to [ComposeView.setContent] in the
 * `onCreateView` override of [ConversationFragment] to create the [View] that `onCreateView` returns
 * to its caller. We start by initializing our [String] variable `val authorMe` to the string with
 * resource ID [R.string.author_me] ("me") and our [String] variable `val timeNow` to the string with
 * resource ID [R.string.now] ("8:30 PM"). We initialize and remember our [LazyListState] variable
 * `val scrollState`, initialize and remember our [TopAppBarState] variable `val topBarState`,
 * initialize and remember our [TopAppBarScrollBehavior] variable `val scrollBehavio` to a pinned
 * [TopAppBarScrollBehavior] that tracks nested-scroll callbacks and updates its
 * [TopAppBarState.contentOffset] accordingly using `topBarState` as its `state` argument, and
 * initialize and remember our [CoroutineScope] variable `val scope`.
 *
 * Our root Composable is a [Scaffold] whose `topBar` argument is a [ChannelNameBar] whose
 * `channelName` argument is the [String] field [ConversationUiState.channelName] of our
 * [uiState] parameter, whose `channelMembers` argument is the [Int] field
 * [ConversationUiState.channelMembers] of our [uiState] parameter, whose `onNavIconPressed`
 * argument is our [onNavIconPressed] lambda parameter, and whose `scrollBehavior` argument is our
 * [TopAppBarScrollBehavior] variable `scrollBehavior`. The `contentWindowInsets` argument of the
 * [Scaffold] starts with the [ScaffoldDefaults] containing various default values for the [Scaffold]
 * component, then adds the [WindowInsets] supplied by `contentWindowInsets` (Default insets to be
 * used and consumed by the scaffold content slot), then uses the [WindowInsets.exclude] on the result
 * to exclude the `navigationBars` and `ime` [WindowInsets]. Its `modifier` argument chains to our
 * [Modifier] parameter `modifier` a [Modifier.nestedScroll] whose `connection` argument is the
 * [TopAppBarScrollBehavior.nestedScrollConnection] of our `scrollBehavior` variable (its a
 * [NestedScrollConnection] that should be attached to a [Modifier.nestedScroll] in order to keep
 * track of the scroll events).
 *
 * The `content` of the [Scaffold] is a [Column] whose `modifier` argument is a [Modifier.fillMaxSize]
 * that causes the [Column] to occupy its entire incoming size constraints, to which is chained a
 * [Modifier.padding] which adds the [PaddingValues] that the [Scaffold] passes to the `content`
 * lambda to its padding. The `content` of the [Column] is a [Messages] Composable and a [UserInput]
 * Composable.
 *
 * The [Messages] Composable uses the [List] of [Message] from the [ConversationUiState.messages] of
 * our [ConversationUiState] parameter [uiState] as its `messages` argument, its `navigateToProfile`
 * argument is our [navigateToProfile] parameter, its `modifier` argument is a `ColumnScope`
 * `Modifier.weight` whose `weight` argument is 1f causing it to use the entire incoming vertical
 * constraint after its non-weighted siblings are messured and placed, and its `scrollState` argument
 * is our [LazyListState] variable `scrollState`.
 *
 * The [UserInput] Composable uses a lambda as its `onMessageSent` argument which calls the
 * [ConversationUiState.addMessage] method of our [ConversationUiState] parameter [uiState] with a
 * new instance of [Message] to add it to the beginning of the [MutableList] of [Message] which is
 * read using the [ConversationUiState.messages] property of our [ConversationUiState] parameter
 * [uiState], its `resetScroll` argument is a lambda which uses the [CoroutineScope.launch] method
 * of our [CoroutineScope] variable `scope` to launch a new coroutine lambda without blocking the
 * current thread  which calls the [LazyListState.scrollToItem] method of our [LazyListState] variable
 * `scrollState` to scroll the [LazyColumn] in [Messages] to `index` 0, and its `modifier` argument
 * is a [Modifier.navigationBarsPadding] to add padding to accommodate the navigation bars insets,
 * with a [Modifier.imePadding] chained to it to add padding to accommodate the ime insets.
 *
 * @param uiState [ConversationUiState] that contains messages to display
 * @param navigateToProfile User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onNavIconPressed Sends an event up when the user clicks on the menu. Our caller passes us
 * a lambda which calls the [MainViewModel.openDrawer] method of our view model (which sets the
 * [MutableStateFlow] of [Boolean] private variable which is read using the public read-only
 * property [MainViewModel.drawerShouldBeOpened] to `true`, which will causes a [LaunchedEffect]
 * to be composed which calls the [DrawerState.open] method of the [DrawerState] used by
 * [JetchatDrawer] which opens the [ModalNavigationDrawer].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationContent(
    uiState: ConversationUiState,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { }
) {
    val authorMe: String = stringResource(id = R.string.author_me)
    val timeNow: String = stringResource(id = R.string.now)

    val scrollState: LazyListState = rememberLazyListState()
    val topBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = topBarState)
    val scope: CoroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ChannelNameBar(
                channelName = uiState.channelName,
                channelMembers = uiState.channelMembers,
                onNavIconPressed = onNavIconPressed,
                scrollBehavior = scrollBehavior,
            )
        },
        // Exclude ime and navigation bar padding so this can be added by the UserInput composable
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(insets = WindowInsets.navigationBars)
            .exclude(insets = WindowInsets.ime),
        modifier = modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection)
    ) { paddingValues: PaddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            Messages(
                messages = uiState.messages,
                navigateToProfile = navigateToProfile,
                modifier = Modifier.weight(weight = 1f),
                scrollState = scrollState
            )
            UserInput(
                onMessageSent = { content: String ->
                    uiState.addMessage(
                        Message(author = authorMe, content = content, timestamp = timeNow)
                    )
                },
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(index = 0)
                    }
                },
                // let this element handle the padding so that the elevation is shown behind the
                // navigation bar
                modifier = Modifier.navigationBarsPadding().imePadding()
            )
        }
    }
}

/**
 * This is the `topBar` used by the [Scaffold] in [ConversationContent]. We start by initializing and
 * remebering our [MutableState] wrapped [Boolean] variable `var functionalityNotAvailablePopupShown`
 * to `false`. Next if `functionalityNotAvailablePopupShown` is `true` we compose our Composable
 * [FunctionalityNotAvailablePopup] with its `onDismiss` argument a lambda which sets
 * `functionalityNotAvailablePopupShown` to `false` again ([FunctionalityNotAvailablePopup] is an
 * [AlertDialog] that displays the [Text] "Functionality not available" and calls its `onDismiss`
 * lambda argument when its "CLOSE" [TextButton] is clicked). Our root Composable is a [JetchatAppBar]
 * whose `modifier` argument is our [Modifier] parameter [modifier], whose `scrollBehavior` argument
 * is our [TopAppBarScrollBehavior] parameter [scrollBehavior], qnd whose `onNavIconPressed` argument
 * is our lambda parameter [onNavIconPressed]. The `title` argument of the [JetchatAppBar] is a
 * [Column] whose `horizontalAlignment` argument is [Alignment.CenterHorizontally] causing it to
 * center its children horizontally, and the `content` of the [Column] is a [Text] displaying our
 * [String] parameter [channelName], using as its [TextStyle] the [Typography.titleMedium] of our
 * [JetchatTheme] custom [MaterialTheme.typography], and this is followed by another [Text] displaying
 * the formatted [String] value of our [Int] parameter [channelMembers], using as its [TextStyle] the
 * [Typography.bodySmall] of our [JetchatTheme] custom [MaterialTheme.typography], with the `color`
 * of the text the [ColorScheme.onSurfaceVariant] of our [JetchatTheme] custom [MaterialTheme.colorScheme].
 * The `actions` of the [JetchatAppBar] are two [Icon]'s in a [Row]:
 *  - an [Icon] displaying the `imageVector` [Icons.Outlined.Search] (a stylized magnifying glass),
 *  whose `tint` is the [ColorScheme.onSurfaceVariant] of our [JetchatTheme] custom
 *  [MaterialTheme.colorScheme], and whose `modifier` argument is a [Modifier.clickable] whose lambda
 *  `onClick` argument sets `functionalityNotAvailablePopupShown` to `true` (causing the
 *  [FunctionalityNotAvailablePopup] to "pop-up".
 *
 *  - an [Icon] displaying the `imageVector` [Icons.Outlined.Info] (an "i" with a circle around it),
 *  whose `tint` is the [ColorScheme.onSurfaceVariant] of our [JetchatTheme] custom
 *  [MaterialTheme.colorScheme], and whose `modifier` argument is a [Modifier.clickable] whose lambda
 *  `onClick` argument sets `functionalityNotAvailablePopupShown` to `true` (causing the
 *  [FunctionalityNotAvailablePopup] to "pop-up".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelNameBar(
    channelName: String,
    channelMembers: Int,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { }
) {
    var functionalityNotAvailablePopupShown: Boolean by remember { mutableStateOf(value = false) }
    if (functionalityNotAvailablePopupShown) {
        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }
    JetchatAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Channel name
                Text(
                    text = channelName,
                    style = MaterialTheme.typography.titleMedium
                )
                // Number of members
                Text(
                    text = stringResource(R.string.members, channelMembers),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            // Search icon
            Icon(
                imageVector = Icons.Outlined.Search,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { functionalityNotAvailablePopupShown = true })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.search)
            )
            // Info icon
            Icon(
                imageVector = Icons.Outlined.Info,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { functionalityNotAvailablePopupShown = true })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.info)
            )
        }
    )
}

/**
 * This is used as the `tag` argument of the [Modifier.testTag] used in the [LazyColumn] in the
 * [Messages] Composable. An `androidTest` can then use `composeTestRule.onNodeWithTag` to refer to
 * this `node` as is done in the `ConversationTest.kt` test file.
 */
const val ConversationTestTag: String = "ConversationTestTag"

/**
 * This Composable is used by [ConversationContent] to display the [List] of [Message] in the
 * [ConversationUiState.messages] field of the [ConversationUiState] parameter that it is called
 * with. We start by initializing and remembering our [CoroutineScope] variable `val scope` with a
 * new instance. Then our root Composable is a [Box] whose `modifier` argument is our [Modifier]
 * parameter [modifier]. In its `content` lambda it initializes its [String] variable `val authorMe`
 * to the [String] with resource ID [R.string.author_me] ("me"), its root Composable is a [LazyColumn]
 * whose `reverseLayout` argument is `true` (reverse the direction of scrolling and layout. When true,
 * items are laid out in the reverse order and [LazyListState.firstVisibleItemIndex] == 0 means that
 * column is scrolled to the bottom). The `state` argument of the [LazyColumn] is our [LazyListState]
 * parameter [scrollState], and its `modifier` argument is a [Modifier.testTag] using our [String]
 * field [ConversationTestTag] as its `tag` argument (allows an `androidTest` to use
 * `composeTestRule.onNodeWithTag` to refer to this node), with a [Modifier.fillMaxSize] chained to
 * that to have the [LazyColumn] occupy its entire incoming size constraints. The `content` lambda
 * of the [LazyColumn] uses a `for` [Int] `var index` to loop over all of the [indices] of our [List]
 * of [Message] parameter [messages], and uses `index` to access each [Message] in order to initialize
 * its variables:
 *  - `val prevAuthor` to the [String] which is in field [Message.author] of the [Message] which is
 *  in position `index` minus 1 or `null` if there is no such [Message] (which occurs for an `index`
 *  of 0).
 *  - `val nextAuthor` to the [String] which is in field [Message.author] of the [Message] which is
 *  in position `index` plus 1 or `null` if there is no such [Message] (which occurs for an `index`
 *  for the last [Message] in [messages]).
 *  - `val content` to the [Message] which is in position `index`.
 *  - `val isFirstMessageByAuthor` to `true` iff `prevAuthor` is equal to the [Message.author] field
 *  of `content`.
 *  - `val isLastMessageByAuthor` to `true` iff `nextAuthor` is equal to the [Message.author] field
 *  of `content`.
 *
 * Then we use an `if`/`else` statement to compose into the UI an `item` for the [LazyColumn] either
 * a [DayHeader] displaying the `dayString` "20 Aug" if `index` is equal to 1 less than the [List.size]
 * of [messages], or else if `index` is equal to 2 it composes an `item` for the [LazyColumn] which
 * holds a [DayHeader] displaying the `dayString` "Today".
 *
 * Having taken care of these two dividers we add an `item` to the [LazyColumn] displaying a new
 * instance of [Message] whose `onAuthorClick` argument is a lambda which calls our [navigateToProfile]
 * lambda parameter with the `name` [String] passed to `onAuthorClick`, whose `msg` argument is our
 * [Message] variable `content`, whose `isUserMe` argument is `true` if the [Message.author] of
 * `content` is equal to `authorMe`, whose `isFirstMessageByAuthor` argument is our `isFirstMessageByAuthor`
 * variable and whose `isLastMessageByAuthor` argument is our `isLastMessageByAuthor` variable.
 *
 * Below the [LazyColumn] we initialize our [Float] variable `val jumpThreshold` to the pixel value
 * of our constant [JumpToBottomThreshold] (which is 56.dp). Then we initialize and remember our
 * [derivedStateOf] wrapped [Boolean] variable `val jumpToBottomButtonEnabled` to `true` if the
 * [LazyListState.firstVisibleItemIndex] of [scrollState] is not equal to 0 (the first visible item
 * is not the first one) or the [LazyListState.firstVisibleItemIndex] of [scrollState] is greater than
 * our `jumpThreshold` variable (the offset is greater than the threshold).
 *
 * Then we compose into our UI a [JumpToBottom] Composable whose `enabled` argument is our
 * `jumpToBottomButtonEnabled` variable, whose `onClicked` argument is a lambda which uses the
 * [CoroutineScope.launch] method of `scope` to launch a coroutine which calls the
 * [LazyListState.animateScrollToItem] of [scrollState] to scroll to `index` 0 (the bottom of the
 * [LazyColumn]), and whose `modifier` argument is a `BoxScope` `Modifier.align` whose `alignment`
 * argument is [Alignment.BottomCenter] to align it at the bottom center of the [Box].
 *
 * @param messages the [List] of [Message] we are to display. Our caller [ConversationContent] passes
 * us the [ConversationUiState.messages] field of the current [ConversationUiState].
 * @param navigateToProfile a lambda which will navigate to the `author` profile of the [String]
 * passed the lambda.
 * @param scrollState a [LazyListState] that we should use as the `state` argument of our [LazyColumn].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [ConversationContent] calls us with a `ColumnScope` `Modifier.weight` whose
 * `weight` argument is 1f causing us to use our entire incoming vertical constraint after our
 * non-weighted siblings are messured and placed.
 */
@Composable
fun Messages(
    messages: List<Message>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope: CoroutineScope = rememberCoroutineScope()
    Box(modifier = modifier) {

        val authorMe: String = stringResource(id = R.string.author_me)
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier
                .testTag(tag = ConversationTestTag)
                .fillMaxSize()
        ) {
            for (index in messages.indices) {
                val prevAuthor: String? = messages.getOrNull(index = index - 1)?.author
                val nextAuthor: String? = messages.getOrNull(index = index + 1)?.author
                val content: Message = messages[index]
                val isFirstMessageByAuthor: Boolean = prevAuthor != content.author
                val isLastMessageByAuthor: Boolean = nextAuthor != content.author

                // Hardcode day dividers for simplicity
                if (index == messages.size - 1) {
                    item {
                        DayHeader(dayString = "20 Aug")
                    }
                } else if (index == 2) {
                    item {
                        DayHeader(dayString = "Today")
                    }
                }

                item {
                    Message(
                        onAuthorClick = { name: String -> navigateToProfile(name) },
                        msg = content,
                        isUserMe = content.author == authorMe,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor
                    )
                }
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold: Float = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled: Boolean by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                    scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(index = 0)
                }
            },
            modifier = Modifier.align(alignment = Alignment.BottomCenter)
        )
    }
}

/**
 * This Composable is used to display a [Message] instance in an `item` used in the [LazyColumn] in
 * the Composable [Messages]. We start by initializing our [Color] variable `val borderColor`
 * depending on whether our [Boolean] parameter [isUserMe] is `true` or not. If `true` we initialize
 * it to the [ColorScheme.primary] color of our [JetchatTheme] custom [MaterialTheme.colorScheme],
 * if `false` we initialize it to the [ColorScheme.tertiary] color. Then we initialize our [Modifier]
 * variable `val spaceBetweenAuthors` depending on whether our [Boolean] parameter [isLastMessageByAuthor]
 * is `true` or not. If `true` we initialize we initialize it to a [Modifier.padding] that will add
 * 8.dp to the `top` of any Composable it is applied to, if `false` we initialize it a the empty,
 * default, or starter [Modifier] that contains no elements. Our root Composable is a [Row] whose
 * `modifier` argument is our [Modifier] variable `spaceBetweenAuthors`. If our [Boolean] parameter
 * [isLastMessageByAuthor] is `true` we compose an [Image] which will display the avatar for the
 * author whose resource ID is found in the [Message.authorImage] property of our [Message] parameter
 * [msg], the `modifier` argument of the [Image] is a [Modifier.clickable] whose `onClick` argument
 * is a lambda which calls our [onAuthorClick] lambda parameter with the [Message.author] property of
 * our [Message] parameter [msg], to which is chained a [Modifier.padding] that adds 16.dp to both
 * ends of the [Image], followed by a chain to a [Modifier.size] that sets its `size` to 42.dp,
 * followed by a chain to a [Modifier.border] whose `width` is 1.5dp whose `color` is our [Color]
 * variable `borderColor` and whose `shape` is a [CircleShape]. Chained after that is another
 * [Modifier.border] whose `width` is 3.dp whose `color` is the [ColorScheme.surface] color of our
 * [JetchatTheme] custom [MaterialTheme.colorScheme], and whose `shape` is a [CircleShape]. This is
 * followed by a [Modifier.clip] whose `shape` is a [CircleShape], and at the end is a [RowScope.align]
 * modifier whose `alignment` argument is a [Alignment.Top] which aligns the [Image] to the top of
 * the [Row]. The `contentScale` argument is a [ContentScale.Crop] which scales the source uniformly
 * maintaining the source's aspect ratio) so that both dimensions (width and height) of the source
 * will be equal to or larger than the corresponding dimension of the destination.
 *
 * On the other hand if our [Boolean] parameter [isLastMessageByAuthor] is `false` we just compose a
 * [Spacer] whose `modifier` argument is a [Modifier.width] which sets its width to 74.dp to occupy
 * the area occupied by the [Image] when [isLastMessageByAuthor] is `true`.
 *
 * At the end of the [Row] is an [AuthorAndTextMessage] Composable whose `msg` argument is our
 * [Message] parameter [msg], whose `isUserMe` argument is our [Boolean] parameter [isUserMe], whose
 * `isLastMessageByAuthor` argument is our [isLastMessageByAuthor] parameter, whose `authorClicked`
 * argument is our [onAuthorClick] lambda parameter, and whose `modifier` argument is a
 * [Modifier.padding] that adds 16.dp to the `end` of the [AuthorAndTextMessage], to which is chained
 * a [RowScope.weight] whose `weight` argument is 1f causing the [AuthorAndTextMessage] to take up all
 * the remaining incoming space constraints after its non-weighted siblings are measured and placed.
 *
 * @param onAuthorClick a lambda to be called with the [Message.author] property of our [Message]
 * parameter [msg] when the [Image] displaying the [Message.authorImage] in our [Row] is clicked.
 * @param msg the [Message] we are supposed to display.
 * @param isUserMe if `true` then the [Message.author] field of our [Message] parameter [msg] is
 * equal to the [String] with resource ID [R.string.author_me] ("me").
 * @param isFirstMessageByAuthor if `true` the previous author is equal to the [Message.author] field
 * of our [Message] parameter [msg].
 * @param isLastMessageByAuthor if `true` the next author is equal to the [Message.author] field of
 * our [Message] parameter [msg].
 */
@Composable
fun Message(
    onAuthorClick: (String) -> Unit,
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {
    val borderColor: Color = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val spaceBetweenAuthors: Modifier = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Row(modifier = spaceBetweenAuthors) {
        if (isLastMessageByAuthor) {
            // Avatar
            Image(
                modifier = Modifier
                    .clickable(onClick = { onAuthorClick(msg.author) })
                    .padding(horizontal = 16.dp)
                    .size(size = 42.dp)
                    .border(width = 1.5.dp, color = borderColor, shape = CircleShape)
                    .border(width = 3.dp, color = MaterialTheme.colorScheme.surface, shape = CircleShape)
                    .clip(shape = CircleShape)
                    .align(alignment = Alignment.Top),
                painter = painterResource(id = msg.authorImage),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            // Space under avatar
            Spacer(modifier = Modifier.width(width = 74.dp))
        }
        AuthorAndTextMessage(
            msg = msg,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(weight = 1f)
        )
    }
}

/**
 * This Composable displays its [Message] parameter, as well as an [AuthorNameTimestamp] it its
 * [Boolean] parameter [isLastMessageByAuthor] is `true`. It is used by the [Message] Composable.
 * The actual rendering of the [Text] in its [Message.content] property and possible [Image] whose
 * resource ID might be in its nullable [Message.image] property is delegated to the [ChatItemBubble]
 * Composable. Our root Composable is a [Column] whose `modifier` argument is our [Modifier] parameter
 * [modifier]. In the `content` of the [Column] we check if our [Boolean] parameter [isLastMessageByAuthor]
 * is `true` and if so we compose an [AuthorNameTimestamp] Composable whose `msg` argument is our
 * [Message] parameter [msg] (this displays the [Message.author], and [Message.timestamp] properties
 * of its [Message] parameter [msg] in [Text] Composables, and we only want to display this if the
 * [Message] before ours in the thread is not written by the author of this [Message]). The next
 * Composable in the [Column] is a [ChatItemBubble] whose `message` argument is our [Message] paramter
 * [msg] (the [Message] it will display), whose `isUserMe` argument is our [Boolean] parameter [isUserMe]
 * (if `true` the background [Color] used will be different), and whose `authorClicked` argument is
 * our lambda parameter [authorClicked] (it will be called with the name of the author of the [Message]
 * when the [ClickableMessage] in the [ChatItemBubble] is clicked). Then we use the value of our
 * [Boolean] parameter [isFirstMessageByAuthor] to decide how bit a [Spacer] to use at the end of our
 * [AuthorAndTextMessage], if `true` we use a [Spacer] whose height is 8.dp (Last bubble before next
 * author), and if `false` we use a [Spacer] whose height is 4.dp (space between bubbles written by
 * the same author).
 *
 * @param msg the [Message] we are to display.
 * @param isUserMe if `true` the [Message.author] is equal to the [String] with resource ID
 * [R.string.author_me] ("me"), and this effects the [Color] used to display the [Message].
 * @param isFirstMessageByAuthor if `true` the author of the [Message] before ours in the [List] of
 * [Message] is different from our [Message.author], and we will want to display a bigger [Spacer]
 * between these two [AuthorAndTextMessage]. NOTE: the `reverseLayout` argument of the [LazyColumn]
 * we are displayed in is `true` so items are laid out in reverse order so the next [Message] to be
 * displayed is actually the one before us in the [List].
 * @param isLastMessageByAuthor if `true` the author of the [Message] after ours in the [List] of
 * [Message] is different from our [Message.author], and we will want to display an [AuthorNameTimestamp].
 * NOTE: the `reverseLayout` argument of the [LazyColumn] we are displayed in is `true` so items are
 * laid out in the reverse order so the [Message] that was displayed before us is actually the one
 * after us in the [List].
 * @param authorClicked a lambda that should be called with the author name when a reference to an
 * author is clicked. The `onCreateView` override of [ConversationFragment] passes down through the
 * hierarchy a lambda which navigates to the destination [R.id.nav_profile] with the [String] passed
 * the lambda as its `args` which navigates to the [ProfileFragment], where the the author's
 * [ProfileScreenState] is displayed by a [ProfileScreen].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [Message] passes us a [Modifier.padding] which adds 16.dp to the end of our
 * [AuthorAndTextMessage], to which it chains a [RowScope.weight] whose `weight` argument is 1f
 * causing us to take up all the [Row]'s incoming space constraint after our unweighted siblings are
 * measured and placed.
 */
@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isLastMessageByAuthor) {
            AuthorNameTimestamp(msg = msg)
        }
        ChatItemBubble(message = msg, isUserMe = isUserMe, authorClicked = authorClicked)
        if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(height = 8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(height = 4.dp))
        }
    }
}

/**
 * Displays the [Message.author], and [Message.timestamp] properties of its [Message] parameter [msg]
 * in [Text] Composables.
 *
 * @param msg the [Message] containing the [Message.author], and [Message.timestamp] properties we
 * are to display.
 */
@Composable
private fun AuthorNameTimestamp(msg: Message) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = msg.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .alignBy(alignmentLine = LastBaseline)
                .paddingFrom(alignmentLine = LastBaseline, after = 8.dp) // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(width = 8.dp))
        Text(
            text = msg.timestamp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(alignmentLine = LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

/**
 *
 */
@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(height = 16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    Divider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

/**
 *
 */
@Composable
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {

    val backgroundBubbleColor: Color = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Column {
        Surface(
            color = backgroundBubbleColor,
            shape = ChatBubbleShape
        ) {
            ClickableMessage(
                message = message,
                isUserMe = isUserMe,
                authorClicked = authorClicked
            )
        }

        message.image?.let { imageResourceID: Int ->
            Spacer(modifier = Modifier.height(height = 4.dp))
            Surface(
                color = backgroundBubbleColor,
                shape = ChatBubbleShape
            ) {
                Image(
                    painter = painterResource(id = imageResourceID),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(size = 160.dp),
                    contentDescription = stringResource(id = R.string.attached_image)
                )
            }
        }
    }
}

/**
 *
 */
@Composable
fun ClickableMessage(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {
    val uriHandler: UriHandler = LocalUriHandler.current

    val styledMessage: AnnotatedString = messageFormatter(
        text = message.content,
        primary = isUserMe
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.padding(16.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation: AnnotatedString.Range<String> ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(uri = annotation.item)
                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                        else -> Unit
                    }
                }
        }
    )
}

/**
 *
 */
@Preview
@Composable
fun ConversationPreview() {
    JetchatTheme {
        ConversationContent(
            uiState = exampleUiState,
            navigateToProfile = { }
        )
    }
}

/**
 *
 */
@Preview
@Composable
fun ChannelBarPrev() {
    JetchatTheme {
        ChannelNameBar(channelName = "composers", channelMembers = 52)
    }
}

/**
 *
 */
@Preview
@Composable
fun DayHeaderPrev() {
    DayHeader("Aug 6")
}

private val JumpToBottomThreshold = 56.dp
