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

import androidx.compose.runtime.saveable.Saver
import java.util.regex.Pattern

/**
 * Consider an email valid if there's some text before and after a "@"
 */
private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)$"

/**
 * State for the email text field.
 *
 * @param email initial value for the text field.
 */
class EmailState(val email: String? = null) :
    TextFieldState(validator = ::isEmailValid, errorFor = ::emailValidationError) {
    init {
        email?.let {
            text = it
        }
    }
}

/**
 * Returns an error to be displayed or null if no error was found.
 *
 * @param email [String] to flag as an invalid email.
 * @return [String] error message.
 */
private fun emailValidationError(email: String): String {
    return "Invalid email: $email"
}

/**
 * Returns true if the email is valid, false otherwise. A valid email is considered to be any string
 * that matches the [EMAIL_VALIDATION_REGEX], ie. any [String] that contains an "@" separating two
 * [String]s.
 *
 * @param email The email string to validate.
 * @return True if the email is valid, false otherwise.
 */
private fun isEmailValid(email: String): Boolean {
    return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
}

/**
 * The EmailStateSaver object. A [Saver] is used to save and restore the state of this subclass of
 * [TextFieldState] across configuration changes or process death. It saves the text and
 * [TextFieldState.isFocusedDirty] properties of the [TextFieldState].
 */
val EmailStateSaver: Saver<TextFieldState, Any> = textFieldStateSaver(state = EmailState())
