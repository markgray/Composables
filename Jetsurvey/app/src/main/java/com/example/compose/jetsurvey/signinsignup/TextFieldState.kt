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

package com.example.compose.jetsurvey.signinsignup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue

/**
 * A class that holds the state of a text field, including its current text, focus state,
 * and validation status. Includes a [Saver] that saves the [TextFieldState.text] and
 * [TextFieldState.isFocusedDirty] properties in a [listSaver] to enable subclasses to be
 * saved across configuration changes.
 *
 * @param validator A function that takes the current text of the field and returns `true`
 * if the text is valid, `false` otherwise. Defaults to a function that always returns `true`.
 * @param errorFor A function that takes the current text of the field and returns a string
 * representing the error message to display if the text is invalid. Defaults to a function
 * that always returns an empty string.
 */
open class TextFieldState(
    private val validator: (String) -> Boolean = { true },
    private val errorFor: (String) -> String = { "" }
) {
    /**
     * The current text content of the text field.
     *
     * This property is observable and can be used to update the UI when the text changes.
     */
    var text: String by mutableStateOf("")

    /**
     * Whether the text field has ever been focused.
     *
     * This property is useful for determining whether to display validation errors.
     * For example, you might only want to show errors after the user has interacted
     * with the field at least once.
     */
    var isFocusedDirty: Boolean by mutableStateOf(false)

    /**
     * Whether the text field is currently focused.
     *
     * This property is observable and can be used to update the UI when the focus state changes.
     */
    var isFocused: Boolean by mutableStateOf(false)

    /**
     * Whether to display validation errors for the text field.
     *
     * This property is used in conjunction with [isFocusedDirty] to control when
     * errors are shown. Errors are typically only displayed after the user has
     * interacted with the field at least once and [enableShowErrors] has been called.
     */
    private var displayErrors: Boolean by mutableStateOf(false)

    /**
     * Whether the current text content of the text field is valid. This property is calculated by
     * calling our [validator] lambda property with the current [text].
     */
    open val isValid: Boolean
        get() = validator(text)

    /**
     * Updates the focus state of the text field.
     *
     * This function is called when the focus state of the text field changes. It updates the
     * [isFocused] property to reflect the new focus state and sets [isFocusedDirty] to `true`
     * if the field has been focused at least once.
     *
     * @param focused `true` if the text field is now focused, `false` otherwise.
     */
    fun onFocusChange(focused: Boolean) {
        isFocused = focused
        if (focused) isFocusedDirty = true
    }

    /**
     * Enables the display of validation errors for the text field.
     *
     * This function sets the [displayErrors] property to `true` if the text field
     * has been focused at least once (i.e., [isFocusedDirty] is `true`). This ensures
     * that errors are only shown after the user has interacted with the field.
     */
    fun enableShowErrors() {
        // only show errors if the text was at least once focused
        if (isFocusedDirty) {
            displayErrors = true
        }
    }

    /**
     * Whether validation errors should be displayed for the text field.
     *
     * This property returns `true` if the text field is currently invalid (as determined by
     * the `validator` function) and the `displayErrors` property is `true`.
     * `displayErrors` is typically set to `true` after the user has interacted with the
     * field at least once and `enableShowErrors` has been called.
     *
     * @return `true` if errors should be shown, `false` otherwise.
     */
    fun showErrors(): Boolean = !isValid && displayErrors

    /**
     * Returns the error message to display for the text field, or `null` if no error
     * should be displayed.
     *
     * This function first checks if errors should be shown by calling [showErrors].
     * If `showErrors` returns `true`, it then calls the [errorFor] function with the
     * current [text] to get the error message. Otherwise, it returns `null`.
     *
     * @return The error message string, or `null` if no error should be displayed.
     */
    open fun getError(): String? {
        return if (showErrors()) {
            errorFor(text)
        } else {
            null
        }
    }
}

/**
 * Custom Saver for [TextFieldState].
 *
 * This saver is used to save and restore the state of a [TextFieldState] instance
 * across configuration changes or process death. It saves the `text` and `isFocusedDirty`
 * properties of the [TextFieldState].
 *
 * @param state The [TextFieldState] instance to save and restore. This is used as the
 * target object to restore the saved values into.
 * @return A [Saver] that can save and restore a [TextFieldState].
 */
fun textFieldStateSaver(state: TextFieldState): Saver<TextFieldState, Any> =
    listSaver<TextFieldState, Any>(
        save = { listOf(it.text, it.isFocusedDirty) },
        restore = {
            state.apply {
                text = it[0] as String
                isFocusedDirty = it[1] as Boolean
            }
        }
    )
