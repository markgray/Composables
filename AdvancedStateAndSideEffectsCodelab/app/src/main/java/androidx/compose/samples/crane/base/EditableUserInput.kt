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

package androidx.compose.samples.crane.base

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.base.EditableUserInputState.Companion.Saver
import androidx.compose.samples.crane.home.CraneScreen
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.samples.crane.ui.captionTextStyle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview

/**
 * This class is the "saveable" `state` used by [CraneEditableUserInput]. It holds a [MutableState]
 * of [String] in its `var text` field whose reads and writes are observed by Compose when this
 * class is "remembered" using [rememberSaveable] which is done for a new class instance by the
 * Composable method [rememberEditableUserInputState]. The [Saver] in our companion object is used
 * by [rememberSaveable] to [Bundle] up the class to save using the saved instance state mechanism.
 *
 * @param hint the "hint" that will be displayed to the user when the Composable is first composed.
 * This will be the same value as the `initialText` parameter when the class is constructed and our
 * [Boolean] property [isHint] will return `true` until our [text] field is modified.
 * @param initialText the initial value to be passed to [mutableStateOf] when it creates our [String]
 * field [text]. This will be the same value as the `hint` parameter when the class is constructed
 * and our [Boolean] property [isHint] will return `true` until our [text] field is modified.
 */
class EditableUserInputState(private val hint: String, initialText: String) {
    /**
     * This [MutableState] contains the current text that should be displayed by the [BasicTextField]
     * as the input [String] shown in its text field (its `value` argument, and it is updated by its
     * `onValueChange` lambda argument). Note that it starts our equal to the [hint] field when the
     * [EditableUserInputState] is constructed by [rememberEditableUserInputState].
     */
    var text: String by mutableStateOf(value = initialText)

    /**
     * Returns `true` if our [text] field is still equal to our [hint] field.
     */
    val isHint: Boolean
        get() = text == hint

    companion object {
        /**
         * The [Saver] for our [EditableUserInputState] class. It uses the [listSaver] method to
         * create a `ListSaver` whose `save` lambda returns a [List] of [String] containing the
         * [hint] field and the [text] field of the [EditableUserInputState] it is saving, and its
         * `restore` lambda returns a new instance of [EditableUserInputState] whose `hint` argument
         * is the [String] at index 0 in the [List] of [String] passed the lambda and whose
         * `initialText` argument is the [String] at index 1 in the [List] of [String] passed the
         * lambda.
         */
        val Saver: Saver<EditableUserInputState, *> = listSaver(
            save = { listOf(it.hint, it.text) },
            restore = {
                EditableUserInputState(
                    hint = it[0],
                    initialText = it[1],
                )
            }
        )
    }
}

/**
 * Constructs a new instance of [EditableUserInputState] whose `hint` and `initialText` arguments
 * are the same as our [String] parameter [hint] and uses it as the `init` argument when it calls
 * [rememberSaveable]. The `inputs` argument of [rememberSaveable] is our [hint] parameter (when
 * it changes it will cause the state to reset and `init` to be rerun) and the `saver` argument is
 * the [EditableUserInputState.Saver] method. The call to [rememberSaveable] will cause compose to
 * "remember" the instance of [EditableUserInputState] created by `init` across recompositions and
 * the stored value will survive the activity or process recreation using the saved instance state
 * mechanism.
 */
@Composable
fun rememberEditableUserInputState(hint: String): EditableUserInputState =
    rememberSaveable(hint, saver = Saver) {
        EditableUserInputState(hint = hint, initialText = hint)
    }

/**
 * This is a wrapper around [CraneBaseUserInput] which uses a [BasicTextField] as the `content`
 * lambda of [CraneBaseUserInput]. In addition the `tintIcon` and `showCaption` arguments of the
 * [CraneBaseUserInput] are controlled by lambdas which return `true` if the user has changed the
 * text content of the [BasicTextField] from the original `hint` that our [EditableUserInputState]
 * parameter [state] was constructed with (the [EditableUserInputState.isHint] property returns
 * `false`). The arguments we call [CraneBaseUserInput] with are:
 *  - `caption` our [caption] parameter.
 *  - `tintIcon` a lambda which returns `true` if the [EditableUserInputState.isHint] property of
 *  our [state] parameter returns `false` (the user has changed the text)
 *  - `showCaption` a lambda which returns `true` if the [EditableUserInputState.isHint] property of
 *  our [state] parameter returns `false` (the user has changed the text)
 *  - `vectorImageId` our [vectorImageId] parameter.
 *
 * The arguments we call [BasicTextField] with are:
 *  - `value` the [EditableUserInputState.text] field of our [state] parameter.
 *  - `onValueChange` a lambda which sets the [EditableUserInputState.text] field of our [state]
 *  parameter to the new text content of the [BasicTextField].
 *  - `textStyle` if the [EditableUserInputState.isHint] property of our [state] parameter returns
 *  `true` (the text is still the same as the hint) we pass the [captionTextStyle] as the
 *  [TextStyle], otherwise we pass the `body1` [TextStyle] of [MaterialTheme.typography], with both
 *  using the `current` [LocalContentColor] as its `color`
 *  - `cursorBrush` we pass a [SolidColor] that uses the `current` [LocalContentColor] as its `value`
 *
 * This Composable is used by the `ToDestinationUserInput` Composable (file home/SearchUserInput.kt)
 * which is called by the `FlySearchContent` Composable (file home/HomeFeatures.kt) which is called
 * by the `SearchContent` Composable (file home/CraneHome.kt) when the [CraneScreen] `tabSelected`
 * is [CraneScreen.Fly]. `SearchContent` is the `backLayerContent` argument of the [BackdropScaffold]
 * used by the `CraneHomeContent` Composable (file home/CraneHome.kt), and `CraneHomeContent` is the
 * `content` of the [Scaffold] used by `CraneHome` (file home/CraneHome.kt) which is the `content` of
 * the [Surface] used by the `MainScreen` Composable (file home/MainActivity.kt), which is wrapped in
 * our [CraneTheme] custom [MaterialTheme] and used in the `onCreate` override of `MainActivity`
 * (file home/MainActivity.kt) in its call to `setContent` and is thus the root view of our activity.
 *
 * @param state a [EditableUserInputState] instance which has been "remembered" using our method
 * [rememberEditableUserInputState]. Our caller `ToDestinationUserInput` uses one constructed by
 * [rememberEditableUserInputState] using "Choose Destination" as the `hint` argument.
 * @param caption a caption [String] for the `caption` argument of our [CraneBaseUserInput]. Our
 * caller `ToDestinationUserInput` uses the [String] "To" when it calls us.
 * @param vectorImageId a drawable resource ID for the `vectorImageId` argument of our
 * [CraneBaseUserInput]. Our caller `ToDestinationUserInput` uses `R.drawable.ic_plane` when it
 * calls us.
 */
@Composable
fun CraneEditableUserInput(
    state: EditableUserInputState = rememberEditableUserInputState(hint = ""),
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null
) {
    // TODO Codelab: Encapsulate this state in a state holder DONE in state EditableUserInputState
    CraneBaseUserInput(
        caption = caption,
        tintIcon = { !state.isHint },
        showCaption = { !state.isHint },
        vectorImageId = vectorImageId
    ) {
        BasicTextField(
            value = state.text,
            onValueChange = { state.text = it },
            textStyle = if (state.isHint) {
                captionTextStyle.copy(color = LocalContentColor.current)
            } else {
                MaterialTheme.typography.body1.copy(color = LocalContentColor.current)
            },
            cursorBrush = SolidColor(LocalContentColor.current)
        )
    }
}

/**
 * Preview of our [CraneEditableUserInput] wrapped in our [CraneTheme] custom [MaterialTheme]. We
 * initialize our [EditableUserInputState] variable `val editableUserInputState` to the instance
 * that our [rememberEditableUserInputState] "remembers" and returns when called with the `hint`
 * argument "Choose Destination". Then wrapped in our [CraneTheme] custom [MaterialTheme] we call
 * [CraneEditableUserInput] with the arguments:
 *  - `state` our [EditableUserInputState] variable `editableUserInputState`
 *  - `caption` the [String] "To"
 *  - `vectorImageId` the drawable resource ID `R.drawable.ic_plane`.
 */
@Preview
@Composable
fun CraneEditableUserInputPreview() {
    val editableUserInputState = rememberEditableUserInputState(hint = "Choose Destination")
    CraneTheme {
        CraneEditableUserInput(
            state = editableUserInputState,
            caption = "To",
            vectorImageId = R.drawable.ic_plane
        )
    }
}
