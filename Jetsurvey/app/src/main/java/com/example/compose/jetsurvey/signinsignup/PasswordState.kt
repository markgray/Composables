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

@file:Suppress("RedundantSuppression", "RedundantSuppression")

package com.example.compose.jetsurvey.signinsignup

/**
 * State for the password text field, using password validation. It is a subclass of [TextFieldState]
 * with its [TextFieldState.validator] method our [isPasswordValid] method, and its
 * [TextFieldState.errorFor] method our [passwordValidationError] method.
 */
class PasswordState :
    TextFieldState(validator = ::isPasswordValid, errorFor = ::passwordValidationError)

/**
 * State for the confirm password text field, with the validation logic applied in the
 * [passwordAndConfirmationValid] function. It is a subclass of [TextFieldState] whose [isValid]
 * property checks whether the text in the confirm password field matches the text in the associated
 * [passwordState], provided the text in the [passwordState] is a valid password. The [getError]
 * method returns the String produced by the [passwordConfirmationError] method if there is an error.
 *
 * @param passwordState the [PasswordState] associated with this confirm password field.
 */
class ConfirmPasswordState(private val passwordState: PasswordState) : TextFieldState() {
    /**
     * Whether the text in this confirm password field is valid. The validation logic is implemented
     * in the private [passwordAndConfirmationValid] method which returns `true` if the text in the
     * [passwordState] field is a valid password AND the text in this confirm password field matches
     * the text in the [passwordState] field.
     */
    override val isValid: Boolean
        get() = passwordAndConfirmationValid(
            password = passwordState.text,
            confirmedPassword = text
        )

    /**
     * Returns the error message for the confirm password field. If [showErrors] returns `true`,
     * it returns the error message produced by the [passwordConfirmationError] function, which
     * is "Passwords don't match". If [showErrors] returns `false`, it returns `null`.
     *
     * @return The error message string if errors should be shown, otherwise `null`.
     */
    override fun getError(): String? {
        return if (showErrors()) {
            passwordConfirmationError()
        } else {
            null
        }
    }
}

/**
 * Determines if the password and confirmation password are valid.
 * A password is considered valid if it meets the criteria defined in [isPasswordValid]
 * and matches the confirmed password.
 *
 * @param password The password string.
 * @param confirmedPassword The confirmed password string.
 * @return `true` if the password is valid and matches the confirmed password, `false` otherwise.
 */
private fun passwordAndConfirmationValid(password: String, confirmedPassword: String): Boolean {
    return isPasswordValid(password = password) && password == confirmedPassword
}

/**
 * Returns `true` if the password is valid, `false` otherwise. A password is considered valid if
 * its length is greater than 3.
 *
 * @param password the password string to validate.
 * @return `true` if the password is valid, `false` otherwise.
 */
private fun isPasswordValid(password: String): Boolean {
    return password.length > 3
}

/**
 * Returns the error message for an invalid password.
 *
 * @param password The password string (unused in the current implementation).
 * @return The static string "Invalid password".
 */
@Suppress("UNUSED_PARAMETER", "unused")
private fun passwordValidationError(password: String): String {
    return "Invalid password"
}

/**
 * Returns the error message for a password confirmation mismatch.
 *
 * @return The static string "Passwords don't match".
 */
private fun passwordConfirmationError(): String {
    return "Passwords don't match"
}
