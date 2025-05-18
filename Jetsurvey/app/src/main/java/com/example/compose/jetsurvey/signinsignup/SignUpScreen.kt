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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.theme.stronglyDeemphasizedAlpha
import com.example.compose.jetsurvey.util.supportWideScreen

/**
 * A stateful composable that provides the entire sign up screen. Our root composable is a
 * [Scaffold] whose arguments are:
 *  - `topBar`: is a [SignInSignUpTopAppBar] whose `topAppBarText` argument the [String] with resource
 *  ID `R.string.create_account` ("Create account"), and whose `onNavUp` argument is our [onNavUp]
 *  lambda parameter.
 *  - `content`: is a lambda that accepts the [PaddingValues] passed the lambda in variable
 *  `contentPadding` then composes a [SignInSignUpScreen] whose `onSignInAsGuest` argument is our
 *  [onSignInAsGuest] lambda parameter, whose `contentPadding` argument is our `contentPadding`
 *  variable, and whose `modifier` argument is a [Modifier.supportWideScreen] composable extension
 *  function. In the [SignInSignUpScreen] `content` composable lambda argument we compose a [Column]
 *  in whose [ColumnScope] `content` composable lambda argument we compose a [SignUpContent] whose
 *  `email` argument is our [email] parameter, and whose `onSignUpSubmitted` argument is our
 *  [onSignUpSubmitted] lambda parameter.
 *
 * @param email (state) The email to be prefilled in the email field.
 * @param onSignUpSubmitted (event) Event to be emitted when the user clicks the sign up button.
 * @param onSignInAsGuest (event) Event to be emitted when the user clicks the
 * "Sign in as guest" button.
 * @param onNavUp (event) Event to be emitted when the user clicks the up navigation icon.
 */
@Composable
fun SignUpScreen(
    email: String?,
    onSignUpSubmitted: (email: String, password: String) -> Unit,
    onSignInAsGuest: () -> Unit,
    onNavUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            SignInSignUpTopAppBar(
                topAppBarText = stringResource(id = R.string.create_account),
                onNavUp = onNavUp,
            )
        },
        content = { contentPadding: PaddingValues ->
            SignInSignUpScreen(
                onSignInAsGuest = onSignInAsGuest,
                contentPadding = contentPadding,
                modifier = Modifier.supportWideScreen()
            ) {
                Column {
                    SignUpContent(
                        email = email,
                        onSignUpSubmitted = onSignUpSubmitted
                    )
                }
            }
        }
    )
}

/**
 * The content of the sign up screen. This composable allows the user to enter an email and
 * password. Our root composable is a [Column] whose `modifier` argument is a [Modifier.fillMaxWidth].
 * In the [ColumnScope] `content` composable lambda argument we start by initializing and remembering
 * our [FocusRequester] variable `passwordFocusRequest` and our [FocusRequester] variable
 * `confirmationPasswordFocusRequest` to new instances. Then we initialize and remember our
 * [EmailState] variable `emailState` to a new instance with its `email` argument our [String]
 * parameter [email].
 *
 * We then compose an [Email] composable whose `emailState` argument is our [EmailState] variable
 * `emailState`, and whose `onImeAction` argument is a lambda that requests the focus for our
 * [FocusRequester] variable `passwordFocusRequest`. Below that we compose a [Spacer] whose
 * `modifier` argument is a [Modifier.height] with a `height` of 16.dp.
 *
 * We then initialize and remember our [PasswordState] variable `passwordState` to a new instance,
 * then compose a [Password] composable whose arguments are:
 *  - `label`: is the [String] with resource ID `R.string.password` ("Password").
 *  - `passwordState`: is our [PasswordState] variable `passwordState`.
 *  - `imeAction`: is [ImeAction.Next] (the default).
 *  - `onImeAction`: is a lambda that requests the focus for our [FocusRequester] variable
 *  `confirmationPasswordFocusRequest`.
 *  - `modifier`: is a [Modifier.focusRequester] composable extension function whose `focusRequester`
 *  argument is our [FocusRequester] variable `passwordFocusRequest`.
 *
 * Below the [Password] we compose a [Spacer] whose `modifier` argument is a [Modifier.height]
 * with a `height` of 16.dp.
 *
 * We then initialize and remember our [ConfirmPasswordState] variable `confirmPasswordState` to
 * a new instance whose `passwordState` argument is our [PasswordState] variable `passwordState`,
 * then compose another [Password] whose arguments are:
 *  - `label`: is the [String] with resource ID `R.string.confirm_password` ("Confirm password").
 *  - `passwordState`: is our [PasswordState] variable `confirmPasswordState`.
 *  - `onImeAction`: is a lambda that calls our [onSignUpSubmitted] lambda parameter with the
 *  [EmailState.text] of [EmailState] variable `emailState` as the email address, and the
 *  [PasswordState.text] of [PasswordState] variable `passwordState` as the password.
 *  - `modifier`: is a [Modifier.focusRequester] composable extension function whose `focusRequester`
 *  argument is our [FocusRequester] variable `confirmationPasswordFocusRequest`.
 *
 * Below the [Password] we compose a [Spacer] whose `modifier` argument is a [Modifier.height]
 * with a `height` of 16.dp.
 *
 * Next we compose a [Text] whose arguments are:
 *  - `text`: is the [String] with resource ID `R.string.terms_and_conditions` ("By continuing, you
 *  agree to our Terms of Service. We’ll handle your data according to our Privacy Policy.)
 *  - `style`: [TextStyle] is the [Typography.bodySmall] of our custom [MaterialTheme.typography].
 *  - `color`: [Color] is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *
 * Below the [Text] we compose a [Spacer] whose `modifier` argument is a [Modifier.height] with
 * a `height` of 16.dp.
 *
 * Finally we compose a [Button] whose arguments are:
 *  - `onClick`: is a lambda that calls our [onSignUpSubmitted] lambda parameter with the
 *  [EmailState.text] of our [EmailState] variable `emailState` and the [PasswordState.text]
 *  of our [PasswordState] variable `passwordState`.
 *  - `modifier`: is a [Modifier.fillMaxWidth].
 *  - `enabled`: is `true` if the [EmailState.isValid] property of our [EmailState] variable
 *  `emailState` is `true`, the [PasswordState.isValid] property of our [PasswordState] variable
 *  `passwordState` it `true`, and the [ConfirmPasswordState.isValid] property of our
 *  [ConfirmPasswordState] variable `confirmPasswordState` is `true`.
 *  - in the [RowScope] `content` composable lambda argument we compose a [Text] whose `text`
 *  argument is the [String] with resource ID `R.string.create_account` ("Create account").
 *
 * @param email (state) The email address to be prefilled in the email address field.
 * @param onSignUpSubmitted (event) Event to be emitted when the user clicks the sign up button.
 */
@Composable
fun SignUpContent(
    email: String?,
    onSignUpSubmitted: (email: String, password: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val passwordFocusRequest: FocusRequester = remember { FocusRequester() }
        val confirmationPasswordFocusRequest: FocusRequester = remember { FocusRequester() }
        val emailState: EmailState = remember { EmailState(email = email) }
        Email(emailState = emailState, onImeAction = { passwordFocusRequest.requestFocus() })

        Spacer(modifier = Modifier.height(height = 16.dp))

        val passwordState: PasswordState = remember { PasswordState() }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Next,
            onImeAction = { confirmationPasswordFocusRequest.requestFocus() },
            modifier = Modifier.focusRequester(focusRequester = passwordFocusRequest)
        )

        Spacer(modifier = Modifier.height(height = 16.dp))

        val confirmPasswordState: ConfirmPasswordState = remember {
            ConfirmPasswordState(passwordState = passwordState)
        }
        Password(
            label = stringResource(id = R.string.confirm_password),
            passwordState = confirmPasswordState,
            onImeAction = { onSignUpSubmitted(emailState.text, passwordState.text) },
            modifier = Modifier.focusRequester(focusRequester = confirmationPasswordFocusRequest)
        )

        Spacer(modifier = Modifier.height(height = 16.dp))

        Text(
            text = stringResource(id = R.string.terms_and_conditions),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = stronglyDeemphasizedAlpha)
        )

        Spacer(modifier = Modifier.height(height = 16.dp))

        Button(
            onClick = { onSignUpSubmitted(emailState.text, passwordState.text) },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailState.isValid &&
                passwordState.isValid && confirmPasswordState.isValid
        ) {
            Text(text = stringResource(id = R.string.create_account))
        }
    }
}

/**
 * Preview of the [SignUpScreen] screen.
 */
@Preview(widthDp = 1024)
@Composable
fun SignUpPreview() {
    JetsurveyTheme {
        SignUpScreen(
            email = null,
            onSignUpSubmitted = { _, _ -> },
            onSignInAsGuest = {},
            onNavUp = {},
        )
    }
}
