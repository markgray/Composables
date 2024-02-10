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

package com.example.compose.jetchat.conversation

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Duo
import androidx.compose.material.icons.outlined.InsertPhoto
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.R
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import androidx.compose.ui.text.TextRange as TextRange1

/**
 * These are the types of input selectors that the user may use to add special content to the message
 * that he posts. Each of them has an [InputSelectorButton] located in the [UserInputSelector]
 * Composable, but only the [EMOJI] has an implementation so far and when its [InputSelectorButton]
 * is clicked the [SelectorExpanded] Composable will compose a [EmojiSelector] into the UI.
 */
enum class InputSelector {
    /**
     * No input selector has been chosen.
     */
    NONE,

    /**
     * The "Location selector" input selector, when its [InputSelectorButton] is clicked the
     * [FunctionalityNotAvailablePanel] will be composed into the UI displaying a [Text] with the
     * `text` "Functionality currently not available"., and a [Text] with the `text` "Grab a beverage
     * and check back later!"
     */
    MAP,

    /**
     * The "Direct Message" input selector, when its [InputSelectorButton] is clicked the
     * [NotAvailablePopup] method will pop up the [FunctionalityNotAvailablePopup] which is an
     * [AlertDialog] displaying the `text` "Functionality not available".
     */
    DM,

    /**
     * The "Show Emoji selector" input selector, when its [InputSelectorButton] is clicked the
     * [EmojiSelector] Composable will be composed into the UI allowing the user to select one of
     * the emoji in the [EmojiTable] Composable to insert into their text message (the [List] of
     * [String] that contains the emojis is our field [emojis]).
     */
    EMOJI,

    /**
     * The "Start videochat" input selector, when its [InputSelectorButton] is clicked the
     * [FunctionalityNotAvailablePanel] will be composed into the UI displaying a [Text] with the
     * `text` "Functionality currently not available"., and a [Text] with the `text` "Grab a beverage
     * and check back later!"
     */
    PHONE,

    /**
     * The "Attach Photo" input selector, when its [InputSelectorButton] is clicked the
     * [FunctionalityNotAvailablePanel] will be composed into the UI displaying a [Text] with the
     * `text` "Functionality currently not available"., and a [Text] with the `text` "Grab a beverage
     * and check back later!"
     */
    PICTURE
}

/**
 * These are the two types of [ExtendedSelectorInnerButton] that are rendered at the top of our
 * [EmojiSelector]. Only the [EMOJI] is actually implemented.
 */
enum class EmojiStickerSelector {
    /**
     * The "Emojis" [ExtendedSelectorInnerButton]
     */
    EMOJI,

    /**
     * The "Stickers" [ExtendedSelectorInnerButton]. When clicked the [NotAvailablePopup] method
     * will pop up the [FunctionalityNotAvailablePopup] which is an [AlertDialog] displaying the
     * `text` "Functionality not available".
     */
    STICKER
}

/**
 * Preview of our [UserInput] Composable.
 */
@Preview
@Composable
fun UserInputPreview() {
    UserInput(onMessageSent = {})
}

/**
 * This Composable implements the custom "keyboard" at the bottom of the [ConversationContent]
 * Composable. It consists of a [Column] holding a [UserInputText] (which holds a [UserInputTextField]
 * that has a [BasicTextField] to enter text in and a [RecordButton] to simulate recording), followed
 * by a [UserInputSelector] that allows the user to select special content to add to the message and
 * a "Send" [Button] that "sends" the message. We start by initializing and [rememberSaveable]'ing
 * our [MutableState] wrapped [InputSelector] variable `var currentInputSelector` to [InputSelector.NONE].
 * Then we initialize our lambda variable `val dismissKeyboard` to a lambda which sets `currentInputSelector`
 * to [InputSelector.NONE]. Then if `currentInputSelector` is not equal to [InputSelector.NONE] we
 * call [BackHandler] with its `onBack` argument `dismissKeyboard` to add it to the
 * [OnBackPressedDispatcher] thereby intercepting back navigation if there's an [InputSelector]
 * visible and having `dismissKeyboard` close the [InputSelector]. Next we initialize and
 * [rememberSaveable] our [MutableState] wrapped [TextFieldValue] variable `var textState` to a new
 * instance using [TextFieldValue.Saver] as its `stateSaver`. And finally we initialize and [remember]
 * our [MutableState] wrapped [Boolean] variable `var textFieldFocusState` to `false` (this will be
 * used to decide if the keyboard should be shown).
 *
 * Our root Composable is a [Surface] whose `tonalElevation` argument is 2.dp (the higher elevation
 * will result in a darker background color in light theme and lighter background color in dark theme),
 * and its `contentColor` is the [ColorScheme.secondary] color of our [MaterialTheme.colorScheme]
 * (the preferred content color provided by the [Surface] to its children). The `content` of the
 * [Surface] is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier], and the
 * `content` of the [Column] is:
 *  - a [UserInputText] whose `textFieldValue` argument is our [TextFieldValue] variable `textState`
 *  (this is used as the `value` argument of a [BasicTextField] further down the hierarchy where it
 *  is the [TextFieldValue] to be shown in the [BasicTextField]), the `onTextChanged` of the
 *  [UserInputText] is a lambda which sets `textState` to the [TextFieldValue] passed the lambda, the
 *  `keyboardShown` argument is `true` only when `currentInputSelector` is [InputSelector.NONE] and
 *  `textFieldFocusState` is `true` (this only shows the keyboard if there's no input selector and
 *  the text field has focus), its `onTextFieldFocused` argument is a lambda which if the [Boolean]
 *  passed it is `true` sets `currentInputSelector` to [InputSelector.NONE] and calls our lambda
 *  parameter [resetScroll] (closes extended selector if text field receives focus) and `true` or
 *  `false` it sets `textFieldFocusState` to the [Boolean] passed the lambda, and finally the
 *  `focusState` argument of the [UserInputText] is our [MutableState] wrapped [Boolean] variable
 *  `textFieldFocusState` (the [BoxScope.UserInputTextField] extension function further down the
 *  hierarchy will display a [Text] displaying a hint "Message #composers" when this is `false` and
 *  the [TextFieldValue] we pass [UserInputText] is empty).
 *  - a [UserInputSelector] whose `onSelectorChange` argument is a lambda which sets our [MutableState]
 *  wrapped [InputSelector] variable `currentInputSelector` to the [InputSelector] passed the lambda,
 *  whose `sendMessageEnabled` argument is `true` if the [TextFieldValue.text] property of our variable
 *  `textState` is not empty and contains some non-whitespace characters, whose `onMessageSent` argument
 *  is a lambda which calls our [onMessageSent] lambda parameter with the [TextFieldValue.text] property
 *  of our [TextFieldValue] variable `textState`, sets `textState` to a new empty instance of [TextFieldValue],
 *  then it calls our lambda parameter [resetScroll] and our lambda variable `dismissKeyboard` to dismiss
 *  the keyboard. Finally the `currentInputSelector` argument is our [InputSelector] variable
 *  `currentInputSelector`,
 *  - a [SelectorExpanded] whose `onCloseRequested` argument is our lambda variable `dismissKeyboard`,
 *  whose `onTextAdded` argument is a lambda which sets our [MutableState] wrapped [TextFieldValue]
 *  variable `textState` to the result of calling its [TextFieldValue.addText] extension function with
 *  the [String] passed the lambda (thereby replacing the [TextFieldValue.selection] of `textState`
 *  with the [String]), and finally the `currentSelector` argument is our [MutableState] wrapped
 *  [InputSelector] variable `currentInputSelector`
 *
 * @param onMessageSent a lambda that should called when the user decides to "send" the message they
 * have typed. Our caller [ConversationContent] passes us a lambda which calls the
 * [ConversationUiState.addMessage] method of the [ConversationUiState] it has been passed with a
 * new [Message] constructed from the [String] that is passed the [onMessageSent] lambda.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us a [Modifier] which is tailored with `navigationBarsPadding` and
 * `imePadding` to allow us to handle the padding so that the elevation is shown behind the navigation
 * bar (whatever that means).
 * @param resetScroll a lambda we can call to "reset" the scroll of the [Messages] Composable that is
 * above us in the [ConversationContent]. [ConversationContent] calls us with a lambda which launches
 * a coroutine which calls the [LazyListState.scrollToItem] method of the [LazyListState] variable it
 * uses as the `scrollState` argument of its [Messages] Composable.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier,
    resetScroll: () -> Unit = {},
) {
    var currentInputSelector: InputSelector by rememberSaveable { mutableStateOf(value = InputSelector.NONE) }
    val dismissKeyboard: () -> Unit = { currentInputSelector = InputSelector.NONE }

    // Intercept back navigation if there's a InputSelector visible
    if (currentInputSelector != InputSelector.NONE) {
        BackHandler(onBack = dismissKeyboard)
    }

    var textState: TextFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(value = TextFieldValue())
    }

    // Used to decide if the keyboard should be shown
    var textFieldFocusState: Boolean by remember { mutableStateOf(value = false) }

    Surface(tonalElevation = 2.dp, contentColor = MaterialTheme.colorScheme.secondary) {
        Column(modifier = modifier) {
            UserInputText(
                textFieldValue = textState,
                onTextChanged = { textState = it },
                // Only show the keyboard if there's no input selector and text field has focus
                keyboardShown = currentInputSelector == InputSelector.NONE && textFieldFocusState,
                // Close extended selector if text field receives focus
                onTextFieldFocused = { focused: Boolean ->
                    if (focused) {
                        currentInputSelector = InputSelector.NONE
                        resetScroll()
                    }
                    textFieldFocusState = focused
                },
                focusState = textFieldFocusState
            )
            UserInputSelector(
                onSelectorChange = { currentInputSelector = it },
                sendMessageEnabled = textState.text.isNotBlank(),
                onMessageSent = {
                    onMessageSent(textState.text)
                    // Reset text field and close keyboard
                    textState = TextFieldValue()
                    // Move scroll to bottom
                    resetScroll()
                    dismissKeyboard()
                },
                currentInputSelector = currentInputSelector
            )
            SelectorExpanded(
                onCloseRequested = dismissKeyboard,
                onTextAdded = { textState = textState.addText(it) },
                currentSelector = currentInputSelector
            )
        }
    }
}

/**
 * Extension function of a [TextFieldValue] which replaces the [String] that currently occupies
 * the [TextRange] of the current [TextFieldValue.selection] of its receiver with our [String]
 * parameter [newString]. Note that if the [TextRange.start] is equal to the [TextRange.end] the
 * [String] is just inserted (or added to the end). We start by initializing our [String] variable
 * `val newText` to the result of calling the [String.replaceRange] method of our receiver's
 * [TextFieldValue.text] field with the `startIndex` argument the [TextRange.start] of its
 * [TextFieldValue.selection] field, with the `endIndex` argument the [TextRange.end] of its
 * [TextFieldValue.text] field, and the `replacement` argument our [String] parameter [newString].
 * Then we initialize our [TextRange] variable `val newSelection` to the immutable text range class
 * returned by [TextRange1] with the `start` argument the [String.length] of `newText`, and the `end`
 * argument the [String.length] of `newText` (an empty selection at the end of `newText`). Finally
 * we return the result of calling the [TextFieldValue.copy] of our receiver with the `text` argument
 * our [String] variable `newText`, and the `selection` argument our [TextRange] variable `newSelection`
 * (this leaves the `composition` the same as our receiver which is important since that field is owned
 * by the IME).
 *
 * @param newString the [String] to use to replace the substring occupying the current [TextRange]
 * of the [TextFieldValue.selection] of our receiver. NOTE that if the [TextRange.start] of the
 * [TextFieldValue.selection] is equal to its [TextRange.end] the [String] is just inserted at that
 * location.
 * @return a [TextFieldValue] which is a copy of our receiver with our [String] parameter [newString]
 * replacing the substring occupying the current [TextRange] of its [TextFieldValue.selection], with
 * its [TextFieldValue.selection] set to the end of the [TextFieldValue.text].
 */
private fun TextFieldValue.addText(newString: String): TextFieldValue {
    val newText: String = this.text.replaceRange(
        startIndex = this.selection.start,
        endIndex = this.selection.end,
        replacement = newString
    )
    val newSelection: TextRange = TextRange1(
        start = newText.length,
        end = newText.length
    )

    return this.copy(text = newText, selection = newSelection)
}

/**
 * This Composable renders different Composables depending on the value of its [InputSelector]
 * parameter [currentSelector] with [InputSelector.NONE] just returning without rendering anything.
 * If [currentSelector] is not [InputSelector.NONE] we initialize our [FocusRequester] variable
 * `val focusRequester` to a new instance, and use [SideEffect] to schedule its `effect` lambda to
 * run after every recomposition, and in that lambda if [currentSelector] is equal to [InputSelector.EMOJI]
 * it calls the [FocusRequester.requestFocus] method of our [FocusRequester] variable `focusRequester`.
 * Then our root Composable is a [Surface] whose `tonalElevation` argument is 8.dp, and its `content`
 * consists of a `when` which switches depending on the value of our [InputSelector] parameter
 * [currentSelector]:
 *  - [InputSelector.EMOJI] ("Emoji selector") we render an [EmojiSelector] with its `onTextAdded`
 *  argument our [onTextAdded] lambda parameter, and its `focusRequester` argument our [FocusRequester]
 *  variable `focusRequester` ([EmojiSelector] allows the user to select emoji to be inserted into the
 *  "message" they are writing).
 *  - [InputSelector.DM] ("Direct Message") we call [NotAvailablePopup] with its `onDismissed`
 *  argument our [onCloseRequested] lambda parameter and this pops up the [FunctionalityNotAvailablePopup]
 *  which is an [AlertDialog] displaying the `text` "Functionality not available".
 *  - [InputSelector.PICTURE] ("Attach Photo") we render a [FunctionalityNotAvailablePanel] which displays
 *  a [Text] with the `text` "Functionality currently not available"., and a [Text] with the `text`
 *  "Grab a beverage and check back later!"
 *  - [InputSelector.MAP] ("Location selector") we render a [FunctionalityNotAvailablePanel] which
 *  displays a [Text] with the `text` "Functionality currently not available"., and a [Text] with
 *  the `text` "Grab a beverage and check back later!"
 *  - [InputSelector.PHONE] ("Start videochat") we render a [FunctionalityNotAvailablePanel] which
 *  displays a [Text] with the text "Functionality currently not available"., and a [Text] with the
 *  text "Grab a beverage and check back later!"
 *  - `else` we throw [NotImplementedError].
 *
 * @param currentSelector the [InputSelector] that the user has selected to be displayed by clicking
 * its [InputSelectorButton] in the [UserInputSelector] Composable rendered in [UserInput].
 * @param onCloseRequested a lambda which sets [currentSelector] to [InputSelector.NONE].
 * @param onTextAdded a lambda we can call with a [String] to be inserted into the message that the
 * user is typing.
 */
@Composable
private fun SelectorExpanded(
    currentSelector: InputSelector,
    onCloseRequested: () -> Unit,
    onTextAdded: (String) -> Unit
) {
    if (currentSelector == InputSelector.NONE) return

    // Request focus to force the TextField to lose it
    val focusRequester = FocusRequester()
    // If the selector is shown, always request focus to trigger a TextField.onFocusChange.
    SideEffect {
        if (currentSelector == InputSelector.EMOJI) {
            focusRequester.requestFocus()
        }
    }

    Surface(tonalElevation = 8.dp) {
        when (currentSelector) {
            InputSelector.EMOJI -> EmojiSelector(onTextAdded = onTextAdded, focusRequester = focusRequester)
            InputSelector.DM -> NotAvailablePopup(onDismissed = onCloseRequested)
            InputSelector.PICTURE -> FunctionalityNotAvailablePanel()
            InputSelector.MAP -> FunctionalityNotAvailablePanel()
            InputSelector.PHONE -> FunctionalityNotAvailablePanel()
            else -> {
                throw NotImplementedError()
            }
        }
    }
}

/**
 * This Composable is composed into the UI when the user tries to select an [InputSelector] which
 * does not have a real implementation. It is called by the [SelectorExpanded] Composable for the
 * [InputSelector]'s [InputSelector.PICTURE], [InputSelector.MAP], and [InputSelector.PHONE]. The
 * root Composable is an [AnimatedVisibility] which animates the appearance and disappearance of its
 * `content`, as its [MutableTransitionState] wrapped [Boolean] argument `visibleState`'s `targetState`
 * changes. The `visibleState` argument we pass to [AnimatedVisibility] is a remembered
 * [MutableTransitionState] whose initial value is `false` but which uses the [apply] extension
 * function to set its `targetState` to `true` thereby starting the animation. The `enter`
 * [EnterTransition] we pass it is an [expandHorizontally] (expands the clip bounds of the appearing
 * content horizontally, from the width returned from initialWidth to the full width) plus a [fadeIn]
 * (fades in the content of the transition, from the specified starting alpha to 1f). The `exit`
 * [ExitTransition] is a [shrinkHorizontally] (animates from full width to 0, shrinking towards the
 * end of the content) plus a [fadeOut] (the content will be faded out to fully transparent).
 *
 * The `content` of the [AnimatedVisibility] is a [Column] whose `modifier` argument is a
 * [Modifier.height] that sets its `height` to 320.dp, with a [Modifier.fillMaxWidth] chained to
 * that causes it to occupy its entire incoming horizontal size constraint. It `verticalArrangement`
 * argument is [Arrangement.Center] (places children such that they are as close as possible to the
 * middle of the main axis) and its `horizontalAlignment` argument is a [Alignment.CenterHorizontally]
 * (centers its children horizontally).
 *
 * The `content` of the [Column] is two [Text] Composables, the first [Text] displays the `text`
 * whose resource ID is [R.string.not_available] ("Functionality currently not available") with its
 * `style` [TextStyle] argument the [Typography.titleMedium] of our custom [MaterialTheme.typography].
 * The second [Text] displays the `text` whose resource ID is [R.string.not_available_subtitle]
 * ("Grab a beverage and check back later!"), whose `modifier` argument is a [Modifier.paddingFrom]
 * whose `alignmentLine` is [FirstBaseline] (alignment line relative to which the padding is defined
 * is the AlignmentLine defined by the baseline of a first line), and whose `before` is 32.dp (the
 * distance between the container's top edge and the horizontal alignment line). The `style`
 * [TextStyle] argument is the [Typography.bodyMedium] of our custom [MaterialTheme.typography], and
 * the `color` of the `text` is the [ColorScheme.onSurfaceVariant] of our [MaterialTheme.colorScheme].
 */
@Composable
fun FunctionalityNotAvailablePanel() {
    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(initialState = false).apply { targetState = true } },
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .height(height = 320.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.not_available),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.not_available_subtitle),
                modifier = Modifier.paddingFrom(alignmentLine = FirstBaseline, before = 32.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * This Composable holds in its root [Row] Composable five [InputSelectorButton]'s which allow the
 * user to select which [InputSelector] the [SelectorExpanded] should be rendering, as well as a
 * "Send" [Button]. The `modifier` argument of the [Row] is a [Modifier.height] that sets its
 * `height` to 72.dp chained to our [Modifier] parameter [modifier], with a [Modifier.wrapContentHeight]
 * chained to that (allows the content of the [Row] to measure at its desired height without regard
 * for the incoming measurement minimum height constraint), with a [Modifier.padding] that set the
 * `start` padding to 16.dp, the `end` padding to 16.dp and the `bottom` padding to 16.dp. The
 * `verticalAlignment` argument of the [Row] is a [Alignment.CenterVertically] which centers its
 * children vertically. The `content` of the [Row] is:
 *  - an [InputSelectorButton] whose `selected` argument is `true` if our [InputSelector] parameter
 *  [currentInputSelector] is [InputSelector.EMOJI], whose `description` is the [String] with resource
 *  ID [R.string.emoji_selector_bt_desc] ("Show Emoji selector"), whose `icon` argument is the
 *  [ImageVector] drawn by [Icons.Outlined.Mood] (a smiley face), and whose `onClick` argument is a
 *  lambda that calls our [onSelectorChange] lambda parameter with [InputSelector.EMOJI].
 *  - an [InputSelectorButton] whose `selected` argument is `true` if our [InputSelector] parameter
 *  [currentInputSelector] is [InputSelector.DM], whose `description` is the [String] with resource
 *  ID [R.string.dm_desc] ("Direct Message"),  whose `icon` argument is the [ImageVector] drawn by
 *  [Icons.Outlined.AlternateEmail] (an "@" character), and whose `onClick` argument is a lambda that
 *  calls our [onSelectorChange] lambda parameter with [InputSelector.DM].
 *  - an [InputSelectorButton] whose `selected` argument is `true` if our [InputSelector] parameter
 *  [currentInputSelector] is [InputSelector.PICTURE], whose `description` is the [String] with
 *  resource ID [R.string.attach_photo_desc] ("Attach Photo"),  whose `icon` argument is the
 *  [ImageVector] drawn by [Icons.Outlined.InsertPhoto] (a stylized picture of a mountain range),
 *  and whose `onClick` argument is a lambda that calls our [onSelectorChange] lambda parameter with
 *  [InputSelector.PICTURE].
 *  - an [InputSelectorButton] whose `selected` argument is `true` if our [InputSelector] parameter
 *  [currentInputSelector] is [InputSelector.MAP], whose `description` is the [String] with resource
 *  ID [R.string.map_selector_desc] ("Location selector"),  whose `icon` argument is the [ImageVector]
 *  drawn by [Icons.Outlined.Place] (a location marker symbol), and whose `onClick` argument is a
 *  lambda that calls our [onSelectorChange] lambda parameter with [InputSelector.MAP].
 *  - an [InputSelectorButton] whose `selected` argument is `true` if our [InputSelector] parameter
 *  [currentInputSelector] is [InputSelector.PHONE], whose `description` is the [String] with resource
 *  ID [R.string.videochat_desc] ("Start videochat"),  whose `icon` argument is the [ImageVector]
 *  drawn by [Icons.Outlined.Duo] (a stylized movie camera), and whose `onClick` argument is a
 *  lambda that calls our [onSelectorChange] lambda parameter with [InputSelector.PHONE].
 *  - a [Spacer] whose whose `modifier` argument is a [RowScope.weight] whose `weight` argument of
 *  1f causes it to take up all available space after its unweighted siblings have been measured and
 *  placed (thus shoving the [Button] that follows it in the [Row] to the end of the [Row]).
 *
 * Next if our [Boolean] parameter [sendMessageEnabled] is `true` we initialize our [BorderStroke]
 * variable `val border` to a [BorderStroke] whose `width` is 1.dp, and whose `color` is a `copy` of
 * the [ColorScheme.onSurface] of our [MaterialTheme.colorScheme] with its `alpha` set to 0.3f, and
 * if [sendMessageEnabled] is `false` we initialize `border` to `null`. We initialize our [Color]
 * variable `val disabledContentColor` to a `copy` of the [ColorScheme.onSurface] of our
 * [MaterialTheme.colorScheme] with its `alpha` set to 0.3f, and we initialize our [ButtonColors]
 * variable `val buttonColors` to a [ButtonColors] that represents the default container and content
 * colors used in a [Button] but with the `disabledContainerColor` argument set to [Color.Transparent],
 * and the `disabledContentColor` argument set to `disabledContentColor`.
 *
 * Then at the very end of the [Row] we compose a [Button] whose `modifier` argument is a [Modifier.height]
 * that sets its `height` to 36.dp, with its `enabled` argument our [Boolean] parameter [sendMessageEnabled],
 * whose `onClick` argument is our lambda parameter [onMessageSent], whose `colors` argument is our
 * [ButtonColors] variable `buttonColors`, whose `border` argument is our [BorderStroke] variable
 * `border`, and whose `contentPadding` argument is a [PaddingValues] that adds 0.dp to all sides.
 * The `content` of the [Button] is a [Text] whose `text` is the string with resource ID [R.string.send]
 * ("Send").
 *
 * @param onSelectorChange a lambda which the [InputSelectorButton]'s can call with the [InputSelector]
 * that they are responsible for to have the UI react to the user selecting a different [InputSelector].
 * @param sendMessageEnabled if `true` our "Send" [Button] is enabled.
 * @param onMessageSent called when the user clicks the "Send" [Button].
 * @param currentInputSelector the current selected [InputSelector].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [UserInput] passes us none so the empty, default, or starter [Modifier]
 * that contains no elements is used instead.
 */
@Composable
private fun UserInputSelector(
    onSelectorChange: (InputSelector) -> Unit,
    sendMessageEnabled: Boolean,
    onMessageSent: () -> Unit,
    currentInputSelector: InputSelector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(height = 72.dp)
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.EMOJI) },
            icon = Icons.Outlined.Mood,
            selected = currentInputSelector == InputSelector.EMOJI,
            description = stringResource(id = R.string.emoji_selector_bt_desc)
        )
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.DM) },
            icon = Icons.Outlined.AlternateEmail,
            selected = currentInputSelector == InputSelector.DM,
            description = stringResource(id = R.string.dm_desc)
        )
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.PICTURE) },
            icon = Icons.Outlined.InsertPhoto,
            selected = currentInputSelector == InputSelector.PICTURE,
            description = stringResource(id = R.string.attach_photo_desc)
        )
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.MAP) },
            icon = Icons.Outlined.Place,
            selected = currentInputSelector == InputSelector.MAP,
            description = stringResource(id = R.string.map_selector_desc)
        )
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.PHONE) },
            icon = Icons.Outlined.Duo,
            selected = currentInputSelector == InputSelector.PHONE,
            description = stringResource(id = R.string.videochat_desc)
        )
        Spacer(modifier = Modifier.weight(weight = 1f))

        val border: BorderStroke? = if (!sendMessageEnabled) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        } else {
            null
        }

        val disabledContentColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

        val buttonColors: ButtonColors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Transparent,
            disabledContentColor = disabledContentColor
        )

        // Send button
        Button(
            modifier = Modifier.height(height = 36.dp),
            enabled = sendMessageEnabled,
            onClick = onMessageSent,
            colors = buttonColors,
            border = border,
            contentPadding = PaddingValues(all = 0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.send),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * This Composable is used by [UserInputSelector] to display [IconButton]'s that allow the user to
 * select one of the five [InputSelector] types that the app provides. We start by initializing our
 * [Modifier] variable `val backgroundModifier` to a [Modifier.background] if our [Boolean] parameter
 * [selected] is `true` or to an empty [Modifier] if it is `false`. The `color` argument of the
 * [Modifier.background] is the `current` [LocalContentColor], and the `shape` argument is a
 * [RoundedCornerShape] whose `size` is 14.dp.
 *
 * Then our root Composable is an [IconButton] whose `onClick` argument is our [onClick] lambda
 * parameter, and whose `modifier` argument uses the [Modifier.then] method of our [Modifier]
 * parameter [modifier] to chain our [Modifier] variable `backgroundModifier` to it. Inside the
 * `content` of the [IconButton] we initialize our [Color] variable `val tint` to the [Color]
 * returned by the [contentColorFor] method for the `backgroundColor` argument the `current`
 * [LocalContentColor] if our [Boolean] parameter [selected] is `true` or to the `current`
 * [LocalContentColor] if it is `false`. Then we compose an [Icon] Composable whose `imageVector`
 * argument is our [ImageVector] parameter [icon], whose `tint` argument is our [Color] variable
 * `tint`, whose `modifier` argument is a [Modifier.padding] that sets the padding on all its sides
 * to 8.dp, with a [Modifier.size] chained to that that sets its `size` to 56.dp, and the
 * `contentDescription` argument is our [String] parameter [description].
 *
 * @param onClick a lambda that our [IconButton] should call when the user clicks it. Each of the 5
 * uses of us by [UserInputSelector] pass us a lambda which calls its `onSelectorChange` lambda
 * parameter with the [InputSelector] we are supposed to select when the user clicks us.
 * @param icon the [ImageVector] that the [Icon] of our [IconButton] should display.
 * @param description the [String] to use as the `contentDescription` of the [Icon] of our [IconButton].
 * @param selected if `true` our [InputSelectorButton] is the currently selected one, and we need to
 * change the [Color]'s we use to draw our content to relect this fact.
 * @param modifier a [Modifier] instance that our caller can use to modify our apearance and/or
 * behavior. Our caller [UserInputSelector] does not pass us any, so the empty, default, or starter
 * [Modifier] that contains no elements is used instead.
 */
@Composable
private fun InputSelectorButton(
    onClick: () -> Unit,
    icon: ImageVector,
    description: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundModifier: Modifier = if (selected) {
        Modifier.background(
            color = LocalContentColor.current,
            shape = RoundedCornerShape(size = 14.dp)
        )
    } else {
        Modifier
    }
    IconButton(
        onClick = onClick,
        modifier = modifier.then(other = backgroundModifier)
    ) {
        val tint: Color = if (selected) {
            contentColorFor(backgroundColor = LocalContentColor.current)
        } else {
            LocalContentColor.current
        }
        Icon(
            imageVector = icon,
            tint = tint,
            modifier = Modifier
                .padding(all = 8.dp)
                .size(size = 56.dp),
            contentDescription = description
        )
    }
}

/**
 * This is just a convenience(?) function for calling our [FunctionalityNotAvailablePopup] Composable
 * (which composes an [AlertDialog] into the UI).
 *
 * @param onDismissed the lambda we should pass to [FunctionalityNotAvailablePopup] as its `onDismiss`
 * argument.
 */
@Composable
private fun NotAvailablePopup(onDismissed: () -> Unit) {
    FunctionalityNotAvailablePopup(onDismiss = onDismissed)
}

/**
 * [SemanticsPropertyKey] is the infrastructure for setting key/value pairs inside semantics blocks
 * in a type-safe way. Each key has one particular statically defined value type T. The `name` of
 * this property is "KeyboardShownKey", and that is the key used to access its [Boolean] value by
 * the [SemanticsPropertyReceiver.keyboardShownProperty]
 */
val KeyboardShownKey: SemanticsPropertyKey<Boolean> = SemanticsPropertyKey(name = "KeyboardShownKey")

/**
 * This is used as the key in the [Modifier.semantics] of the `modifier` argument used in the
 * [UserInputTextField] Composable that is used by the [UserInputText] Composable. Its value is
 * set to the [Boolean] parameter `keyboardShown` of the [UserInputText].
 */
var SemanticsPropertyReceiver.keyboardShownProperty: Boolean by KeyboardShownKey

/**
 * This Composable is used as a [Row] in the [Column] used by [UserInput]. Below it in the [Column]
 * is a [UserInputSelector], and a [SelectorExpanded]. The [Row] root Composable has a `modifier`
 * argument of [Modifier.fillMaxWidth] which causes it to occupy its entire incoming width constraint,
 * to which is chained a [Modifier.height] that sets its height to 64.dp, and its `horizontalArrangement`
 * argument is an [Arrangement.End] (places children horizontally such that they are as close as
 * possible to the end of the main axis). The `content` of the [Row] is an [AnimatedContent] that
 * automatically animates its content when its `targetState` argument changes value. Its `targetState`
 * argument is our [MutableState] wrapped [Boolean] variable `isRecordingMessage`, and its `modifier`
 * argument is a [RowScope.weight] with a `weight` of 1f which causes it to occupy the entire incoming
 * width constraint after its unweighted siblings have been measures and placed, with a
 * [Modifier.fillMaxHeight] chained to that to have its occupy the entire incoming `height` constraint.
 * The `content` lambda of the [AnimatedContent] is passed the current [Boolean] value of `isRecordingMessage`
 * in `recording`, and the `content` is a [Box] which holds a [RecordingIndicator] if `recording` is
 * `true` (its `swipeOffset` lambda argument is the [MutableFloatState.floatValue] of our variable
 * `swipeOffset`), and if `recording` is `false` the [Box] holds a [UserInputTextField] whose `textFieldValue`
 * argument is our [TextFieldValue] parameter [textFieldValue], whose `onTextChanged` argument is our
 * [onTextChanged] lambda parameter, whose `onTextFieldFocused` argument is our [onTextFieldFocused]
 * lambda parameter, whose `keyboardType` argument is our [KeyboardType] parameter [keyboardType],
 * whose `focusState` argument is our [Boolean] parameter [focusState], and whose `modifier` argument
 * is a [Modifier.semantics] which adds semantics key/value pairs to the layout node, (for use in
 * testing, and accessibility) with the [SemanticsPropertyReceiver.contentDescription] key to our
 * [String] variable `a11ylabel`, and the [keyboardShownProperty] key to our [Boolean] parameter
 * [keyboardShown]. Then at the very end of the [Row] is a [RecordButton] whose `recording` argument
 * is our [MutableState] wrapped [Boolean] variable `isRecordingMessage`, whose `swipeOffset` argument
 * is a lambda that returns the [MutableFloatState.floatValue] property of our [MutableFloatState]
 * variable `swipeOffset`, whose `onSwipeOffsetChange` argument is a lambda which sets the
 * [MutableFloatState.floatValue] property of `swipeOffset` to the [Float] `offset` passed the lambda,
 * whose `onStartRecording` argument is a lambda which sets its [Boolean] variable `val consumed` to
 * the inverse of our [MutableState] wrapped [Boolean] variable `isRecordingMessage`, sets
 * `isRecordingMessage` to `true` and returns `comsumed`, its `onFinishRecording` argument is a lambda
 * which sets our [MutableState] wrapped [Boolean] variable `isRecordingMessage` to `false`, its
 * `onCancelRecording` argument is a lambda which sets our [MutableState] wrapped [Boolean] variable
 * `isRecordingMessage` to `false`, and its `modifier` argument is a [Modifier.fillMaxHeight] that
 * causes it to occupy its entire incoming `height` constraint.
 *
 * @param keyboardType this is used as the `keyboardType` argument of the [KeyboardOptions] used by
 * the [BasicTextField] in [UserInputTextField]. Our caller [UserInput] does not pass one so our
 * default [KeyboardType.Text] is used (keyboard type used to request an IME that shows a regular
 * keyboard).
 * @param onTextChanged a lambda that can be called with the current [TextFieldValue] to update the
 * [MutableState] of [TextFieldValue] that our caller keeps track of in the variable that it uses to
 * supply our [textFieldValue] parameter.
 * @param textFieldValue the current [TextFieldValue] that the user has entered.
 * @param keyboardShown if `true` the IME keyboard is currently being shown. Our caller [UserInput]
 * passes us `true` if the current [InputSelector] is [InputSelector.NONE] and [focusState] is
 * `true`.
 * @param onTextFieldFocused a lambda we can call to set the [MutableState] wrapped [Boolean] variable
 * that our caller uses to update our [Boolean] parameter [focusState] to `true` or `false`. It is
 * passed down the hierarchy to where it is eventually used in a [Modifier.onFocusChanged] of a
 * [BasicTextField] as the [Modifier]'s `onFocusChanged` lambda argument where it is called with
 * the value of the [FocusState.isFocused] passed the lambda.
 * @param focusState the current value of the [MutableState] wrapped [Boolean] variable that our
 * caller saves the focus state of the keyboard in, it is updated using our [onTextFieldFocused]
 * lambda parameter by a [Modifier.onFocusChanged] of a [BasicTextField] further down the hierarchy.
 */
@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean
) {
    val swipeOffset: MutableFloatState = remember { mutableFloatStateOf(value = 0f) }
    var isRecordingMessage: Boolean by remember { mutableStateOf(value = false) }
    val a11ylabel: String = stringResource(id = R.string.textfield_desc)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 64.dp),
        horizontalArrangement = Arrangement.End
    ) {
        AnimatedContent(
            targetState = isRecordingMessage,
            label = "text-field",
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxHeight()
        ) { recording: Boolean ->
            Box(Modifier.fillMaxSize()) {
                if (recording) {
                    RecordingIndicator { swipeOffset.floatValue }
                } else {
                    UserInputTextField(
                        textFieldValue = textFieldValue,
                        onTextChanged = onTextChanged,
                        onTextFieldFocused = onTextFieldFocused,
                        keyboardType = keyboardType,
                        focusState = focusState,
                        modifier = Modifier.semantics {
                            contentDescription = a11ylabel
                            keyboardShownProperty = keyboardShown
                        }
                    )
                }
            }
        }
        RecordButton(
            recording = isRecordingMessage,
            swipeOffset = { swipeOffset.floatValue },
            onSwipeOffsetChange = { offset: Float -> swipeOffset.floatValue = offset },
            onStartRecording = {
                val consumed: Boolean = !isRecordingMessage
                isRecordingMessage = true
                consumed
            },
            onFinishRecording = {
                // handle end of recording
                isRecordingMessage = false
            },
            onCancelRecording = {
                isRecordingMessage = false
            },
            modifier = Modifier.fillMaxHeight()
        )
    }
}

/**
 * This Composable is used in a [Box] it timeshares with a [RecordingIndicator] in the [UserInputText]
 * Composable.
 */
@Composable
private fun BoxScope.UserInputTextField(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFieldFocused: (Boolean) -> Unit,
    keyboardType: KeyboardType,
    focusState: Boolean,
    modifier: Modifier = Modifier
) {
    var lastFocusState: Boolean by remember { mutableStateOf(value = false) }
    BasicTextField(
        value = textFieldValue,
        onValueChange = { onTextChanged(it) },
        modifier = modifier
            .padding(start = 32.dp)
            .align(alignment = Alignment.CenterStart)
            .onFocusChanged { state: FocusState ->
                if (lastFocusState != state.isFocused) {
                    onTextFieldFocused(state.isFocused)
                }
                lastFocusState = state.isFocused
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Send
        ),
        maxLines = 1,
        cursorBrush = SolidColor(LocalContentColor.current),
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
    )

    val disableContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
    if (textFieldValue.text.isEmpty() && !focusState) {
        Text(
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .padding(start = 32.dp),
            text = stringResource(R.string.textfield_hint),
            style = MaterialTheme.typography.bodyLarge.copy(color = disableContentColor)
        )
    }
}

@Composable
private fun RecordingIndicator(swipeOffset: () -> Float) {
    var duration by remember { mutableStateOf(Duration.ZERO) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            duration += 1.seconds
        }
    }
    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")

        val animatedPulse = infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                tween(2000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse",
        )
        Box(
            Modifier
                .size(56.dp)
                .padding(24.dp)
                .graphicsLayer {
                    scaleX = animatedPulse.value; scaleY = animatedPulse.value
                }
                .clip(CircleShape)
                .background(Color.Red)
        )
        Text(
            duration.toComponents { minutes, seconds, _ ->
                val min = minutes.toString().padStart(2, '0')
                val sec = seconds.toString().padStart(2, '0')
                "$min:$sec"
            },
            Modifier.alignByBaseline()
        )
        Box(
            Modifier
                .fillMaxSize()
                .alignByBaseline()
                .clipToBounds()
        ) {
            val swipeThreshold = with(LocalDensity.current) { 200.dp.toPx() }
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = swipeOffset() / 2
                        alpha = 1 - (swipeOffset().absoluteValue / swipeThreshold)
                    },
                textAlign = TextAlign.Center,
                text = stringResource(R.string.swipe_to_cancel_recording),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 *
 */
@Composable
fun EmojiSelector(
    onTextAdded: (String) -> Unit,
    focusRequester: FocusRequester
) {
    var selected by remember { mutableStateOf(EmojiStickerSelector.EMOJI) }

    val a11yLabel = stringResource(id = R.string.emoji_selector_desc)
    Column(
        modifier = Modifier
            .focusRequester(focusRequester) // Requests focus when the Emoji selector is displayed
            // Make the emoji selector focusable so it can steal focus from TextField
            .focusTarget()
            .semantics { contentDescription = a11yLabel }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            ExtendedSelectorInnerButton(
                text = stringResource(id = R.string.emojis_label),
                onClick = { selected = EmojiStickerSelector.EMOJI },
                selected = true,
                modifier = Modifier.weight(1f)
            )
            ExtendedSelectorInnerButton(
                text = stringResource(id = R.string.stickers_label),
                onClick = { selected = EmojiStickerSelector.STICKER },
                selected = false,
                modifier = Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.verticalScroll(rememberScrollState())) {
            EmojiTable(onTextAdded, modifier = Modifier.padding(8.dp))
        }
    }
    if (selected == EmojiStickerSelector.STICKER) {
        NotAvailablePopup(onDismissed = { selected = EmojiStickerSelector.EMOJI })
    }
}

/**
 *
 */
@Composable
fun ExtendedSelectorInnerButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = ButtonDefaults.buttonColors(
        containerColor = if (selected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        else Color.Transparent,
        disabledContainerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.74f)
    )
    TextButton(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .height(36.dp),
        colors = colors,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

/**
 *
 */
@Composable
fun EmojiTable(
    onTextAdded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        repeat(4) { x ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(EMOJI_COLUMNS) { y ->
                    val emoji = emojis[x * EMOJI_COLUMNS + y]
                    Text(
                        modifier = Modifier
                            .clickable(onClick = { onTextAdded(emoji) })
                            .sizeIn(minWidth = 42.dp, minHeight = 42.dp)
                            .padding(8.dp),
                        text = emoji,
                        style = LocalTextStyle.current.copy(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

private const val EMOJI_COLUMNS = 10

private val emojis: List<String> = listOf(
    "\ud83d\ude00", // Grinning Face
    "\ud83d\ude01", // Grinning Face With Smiling Eyes
    "\ud83d\ude02", // Face With Tears of Joy
    "\ud83d\ude03", // Smiling Face With Open Mouth
    "\ud83d\ude04", // Smiling Face With Open Mouth and Smiling Eyes
    "\ud83d\ude05", // Smiling Face With Open Mouth and Cold Sweat
    "\ud83d\ude06", // Smiling Face With Open Mouth and Tightly-Closed Eyes
    "\ud83d\ude09", // Winking Face
    "\ud83d\ude0a", // Smiling Face With Smiling Eyes
    "\ud83d\ude0b", // Face Savouring Delicious Food
    "\ud83d\ude0e", // Smiling Face With Sunglasses
    "\ud83d\ude0d", // Smiling Face With Heart-Shaped Eyes
    "\ud83d\ude18", // Face Throwing a Kiss
    "\ud83d\ude17", // Kissing Face
    "\ud83d\ude19", // Kissing Face With Smiling Eyes
    "\ud83d\ude1a", // Kissing Face With Closed Eyes
    "\u263a", // White Smiling Face
    "\ud83d\ude42", // Slightly Smiling Face
    "\ud83e\udd17", // Hugging Face
    "\ud83d\ude07", // Smiling Face With Halo
    "\ud83e\udd13", // Nerd Face
    "\ud83e\udd14", // Thinking Face
    "\ud83d\ude10", // Neutral Face
    "\ud83d\ude11", // Expressionless Face
    "\ud83d\ude36", // Face Without Mouth
    "\ud83d\ude44", // Face With Rolling Eyes
    "\ud83d\ude0f", // Smirking Face
    "\ud83d\ude23", // Persevering Face
    "\ud83d\ude25", // Disappointed but Relieved Face
    "\ud83d\ude2e", // Face With Open Mouth
    "\ud83e\udd10", // Zipper-Mouth Face
    "\ud83d\ude2f", // Hushed Face
    "\ud83d\ude2a", // Sleepy Face
    "\ud83d\ude2b", // Tired Face
    "\ud83d\ude34", // Sleeping Face
    "\ud83d\ude0c", // Relieved Face
    "\ud83d\ude1b", // Face With Stuck-Out Tongue
    "\ud83d\ude1c", // Face With Stuck-Out Tongue and Winking Eye
    "\ud83d\ude1d", // Face With Stuck-Out Tongue and Tightly-Closed Eyes
    "\ud83d\ude12", // Unamused Face
    "\ud83d\ude13", // Face With Cold Sweat
    "\ud83d\ude14", // Pensive Face
    "\ud83d\ude15", // Confused Face
    "\ud83d\ude43", // Upside-Down Face
    "\ud83e\udd11", // Money-Mouth Face
    "\ud83d\ude32", // Astonished Face
    "\ud83d\ude37", // Face With Medical Mask
    "\ud83e\udd12", // Face With Thermometer
    "\ud83e\udd15", // Face With Head-Bandage
    "\u2639", // White Frowning Face
    "\ud83d\ude41", // Slightly Frowning Face
    "\ud83d\ude16", // Confounded Face
    "\ud83d\ude1e", // Disappointed Face
    "\ud83d\ude1f", // Worried Face
    "\ud83d\ude24", // Face With Look of Triumph
    "\ud83d\ude22", // Crying Face
    "\ud83d\ude2d", // Loudly Crying Face
    "\ud83d\ude26", // Frowning Face With Open Mouth
    "\ud83d\ude27", // Anguished Face
    "\ud83d\ude28", // Fearful Face
    "\ud83d\ude29", // Weary Face
    "\ud83d\ude2c", // Grimacing Face
    "\ud83d\ude30", // Face With Open Mouth and Cold Sweat
    "\ud83d\ude31", // Face Screaming in Fear
    "\ud83d\ude33", // Flushed Face
    "\ud83d\ude35", // Dizzy Face
    "\ud83d\ude21", // Pouting Face
    "\ud83d\ude20", // Angry Face
    "\ud83d\ude08", // Smiling Face With Horns
    "\ud83d\udc7f", // Imp
    "\ud83d\udc79", // Japanese Ogre
    "\ud83d\udc7a", // Japanese Goblin
    "\ud83d\udc80", // Skull
    "\ud83d\udc7b", // Ghost
    "\ud83d\udc7d", // Extraterrestrial Alien
    "\ud83e\udd16", // Robot Face
    "\ud83d\udca9", // Pile of Poo
    "\ud83d\ude3a", // Smiling Cat Face With Open Mouth
    "\ud83d\ude38", // Grinning Cat Face With Smiling Eyes
    "\ud83d\ude39", // Cat Face With Tears of Joy
    "\ud83d\ude3b", // Smiling Cat Face With Heart-Shaped Eyes
    "\ud83d\ude3c", // Cat Face With Wry Smile
    "\ud83d\ude3d", // Kissing Cat Face With Closed Eyes
    "\ud83d\ude40", // Weary Cat Face
    "\ud83d\ude3f", // Crying Cat Face
    "\ud83d\ude3e", // Pouting Cat Face
    "\ud83d\udc66", // Boy
    "\ud83d\udc67", // Girl
    "\ud83d\udc68", // Man
    "\ud83d\udc69", // Woman
    "\ud83d\udc74", // Older Man
    "\ud83d\udc75", // Older Woman
    "\ud83d\udc76", // Baby
    "\ud83d\udc71", // Person With Blond Hair
    "\ud83d\udc6e", // Police Officer
    "\ud83d\udc72", // Man With Gua Pi Mao
    "\ud83d\udc73", // Man With Turban
    "\ud83d\udc77", // Construction Worker
    "\u26d1", // Helmet With White Cross
    "\ud83d\udc78", // Princess
    "\ud83d\udc82", // Guardsman
    "\ud83d\udd75", // Sleuth or Spy
    "\ud83c\udf85", // Father Christmas
    "\ud83d\udc70", // Bride With Veil
    "\ud83d\udc7c", // Baby Angel
    "\ud83d\udc86", // Face Massage
    "\ud83d\udc87", // Haircut
    "\ud83d\ude4d", // Person Frowning
    "\ud83d\ude4e", // Person With Pouting Face
    "\ud83d\ude45", // Face With No Good Gesture
    "\ud83d\ude46", // Face With OK Gesture
    "\ud83d\udc81", // Information Desk Person
    "\ud83d\ude4b", // Happy Person Raising One Hand
    "\ud83d\ude47", // Person Bowing Deeply
    "\ud83d\ude4c", // Person Raising Both Hands in Celebration
    "\ud83d\ude4f", // Person With Folded Hands
    "\ud83d\udde3", // Speaking Head in Silhouette
    "\ud83d\udc64", // Bust in Silhouette
    "\ud83d\udc65", // Busts in Silhouette
    "\ud83d\udeb6", // Pedestrian
    "\ud83c\udfc3", // Runner
    "\ud83d\udc6f", // Woman With Bunny Ears
    "\ud83d\udc83", // Dancer
    "\ud83d\udd74", // Man in Business Suit Levitating
    "\ud83d\udc6b", // Man and Woman Holding Hands
    "\ud83d\udc6c", // Two Men Holding Hands
    "\ud83d\udc6d", // Two Women Holding Hands
    "\ud83d\udc8f" // Kiss
)
