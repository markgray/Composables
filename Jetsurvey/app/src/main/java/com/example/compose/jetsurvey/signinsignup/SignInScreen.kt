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

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.util.supportWideScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Sign in screen. We start by initializing and remembering our [SnackbarHostState] variable
 * `snackbarHostState` to a new instance, initializing and remembering our [CoroutineScope]
 * variable `scope` to a new instance, initializing our [String] variable `snackbarErrorText`
 * with the [String] whose resource ID is `R.string.feature_not_available` ("Feature not available")
 * and initializing our [String] variable `snackbarActionLabel` with the [String] whose resource
 * ID is `R.string.dismiss` ("Dismiss").
 *
 * Then we compose a [Scaffold] composable with the following arguments:
 *  - `topBar`: The top app bar of the screen, which is a [SignInSignUpTopAppBar] composable whose
 *  `topAppBarText` argument is the [String] whose resource ID is `R.string.sign_in` ("Sign in")`,
 *  and whose `onNavUp` argument is our [onNavUp] lambda parameter.
 *  - `content`: The content composable lambda argument of the [Scaffold] is a composable lambda that
 *  accepts the [PaddingValues] passed the lambda in variable `contentPadding`.
 *
 * We then compose the [SignInSignUpScreen] composable in the `content` argument of the [Scaffold]
 * with the following arguments:
 *  - `modifier`: is a [Modifier.supportWideScreen].
 *  - `contentPadding`: is the [PaddingValues] passed in variable `contentPadding`.
 *  - `onSignInAsGuest`: is our [onSignInAsGuest] lambda parameter.
 *
 * In the `content` composable lambda argument of the [SignInSignUpScreen] we compose a [Column]
 * whose `modifier` argument is a [Modifier.fillMaxWidth]. In the [ColumnScope] `content` composable
 * lambda argument of the [Column] we compose:
 *
 * **First** a [SignInContent] composable whose `email` argument is our [String] parameter [email],
 * and whose `onSignInSubmitted` argument is our [onSignInSubmitted] lambda parameter.
 *
 * **Second** a [Spacer] whose `modifier` argument is a [Modifier.height] with a `height` of 16.dp.
 *
 * **Third** a [TextButton] whose `onClick` argument is a lambda that uses our [CoroutineScope]
 * variable `scope` to launch a coroutine in which it calls the [SnackbarHostState.showSnackbar]
 * method of our [SnackbarHostState] variable `snackbarHostState` with the following arguments:
 *  - `message`: is our [String] variable `snackbarErrorText` ("Feature not available")
 *  - `actionLabel`: is our [String] variable `snackbarActionLabel` ("Dismiss")
 *
 * The `modifier` argument of the [TextButton] is a [Modifier.fillMaxWidth], and in the [RowScope]
 * `content` composable lambda argument of the [TextButton] we compose a [Text] whose `text` is
 * the [String] whose resource ID is `R.string.forgot_password` ("Forgot password?")
 *
 * Below the [Scaffold] we compose a [Box] whose `modifier` argument is a [Modifier.fillMaxSize].
 * In the [BoxScope] `content` composable lambda argument of the [Box] we compose a [ErrorSnackbar]
 * composable whose `snackbarHostState` argument is our [SnackbarHostState] variable `snackbarHostState`,
 * and whose `onDismiss` argument is a lambda that calls the [SnackbarData.dismiss] method of the
 * [SnackbarHostState.currentSnackbarData] property of our [SnackbarHostState] variable
 * `snackbarHostState`. The `modifier` argument of the [ErrorSnackbar] is a [BoxScope.align] whose
 * `alignment` is [Alignment.BottomCenter]. TODO: Need to figure out how this Box is used
 *
 * @param email Email to pre-fill the email field with.
 * @param onSignInSubmitted Callback to be invoked when the sign in form is submitted.
 * @param onSignInAsGuest Callback to be invoked when the "Sign in as guest" button is clicked.
 * @param onNavUp Callback to be invoked when the "Up" button in the top app bar is clicked.
 */
@Composable
fun SignInScreen(
    email: String?,
    onSignInSubmitted: (email: String, password: String) -> Unit,
    onSignInAsGuest: () -> Unit,
    onNavUp: () -> Unit,
) {

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val scope: CoroutineScope = rememberCoroutineScope()

    val snackbarErrorText: String = stringResource(id = R.string.feature_not_available)
    val snackbarActionLabel: String = stringResource(id = R.string.dismiss)

    Scaffold(
        topBar = {
            SignInSignUpTopAppBar(
                topAppBarText = stringResource(id = R.string.sign_in),
                onNavUp = onNavUp,
            )
        },
        content = { contentPadding: PaddingValues ->
            SignInSignUpScreen(
                modifier = Modifier.supportWideScreen(),
                contentPadding = contentPadding,
                onSignInAsGuest = onSignInAsGuest,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SignInContent(
                        email = email,
                        onSignInSubmitted = onSignInSubmitted,
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    TextButton(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = snackbarErrorText,
                                    actionLabel = snackbarActionLabel
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.forgot_password))
                    }
                }
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ErrorSnackbar(
            snackbarHostState = snackbarHostState,
            onDismiss = { snackbarHostState.currentSnackbarData?.dismiss() },
            modifier = Modifier.align(alignment = Alignment.BottomCenter)
        )
    }
}

/**
 * Sign in content. Our root composable is a [Column] whose `modifier` argument is a
 * [Modifier.fillMaxWidth]. In its [ColumnScope] `content` composable lambda argument we start by
 * initializing and remembering our [FocusRequester] variable `focusRequester` to a new instance,
 * amd initializing and remembering our [TextFieldState] variable `emailState` to a new instance
 * whose `inputs` argument is an array of our [String] parameter [email], and whose `stateSaver`
 * argument is our [EmailStateSaver] singleton and in its `init` block we return a [MutableState]
 * wrapped [EmailState] whose `email` argument is our [String] parameter [email].
 *
 * Next we compose an [Email] composable whose `emailState` argument is our [TextFieldState] variable
 * `emailState`, and whose `onImeAction` argument is a lambda that calls the [FocusRequester.requestFocus]
 * method of our [FocusRequester] variable `focusRequester`. And below that we comopse a [Spacer]
 * whose `modifier` argument is a [Modifier.height] with a `height` of 16.dp.
 *
 * We then initialize and remember our [PasswordState] variable `passwordState` to a new instance,
 * and initialize our lambda variable `onSubmit` with a lambda that checks if the [EmailState.isValid]
 * property of our [TextFieldState] variable `emailState` is `true` and if the [PasswordState.isValid]
 * property of our [PasswordState] variable `passwordState` is `true`, and if so it calls our
 * [onSignInSubmitted] lambda parameter with the [EmailState.text] of our [TextFieldState] variable
 * `emailState` and the [PasswordState.text] of our [PasswordState] variable `passwordState`.
 *
 * Next we compose a [Password] composable whose `label` argument is the [String] whose resource ID
 * is `R.string.password` ("Password"), `passwordState` argument is our [PasswordState] variable
 * `passwordState`, `modifier` argument is a [Modifier.focusRequester] whose `focusRequester`
 * argument is our [FocusRequester] variable `focusRequester`, and whose `onImeAction` argument
 * is a lambda that calls our lambda variable `onSubmit`. And below that we compose a [Spacer]
 * whose `modifier` argument is a [Modifier.height] with a `height` of 16.dp.
 *
 * We then compose a [Button] composable whose `onClick` argument is a lambda that calls our
 * lambda variable `onSubmit`, whose `modifier` argument is a [Modifier.fillMaxWidth] chained to a
 * [Modifier.padding] whose that adds `16.dp` to each `vertical` side and the `enabled` argument of
 * the [Button] is `true` if the [EmailState.isValid] property of our [TextFieldState] variable
 * `emailState` is `true` and if the [PasswordState.isValid] property of our [PasswordState] variable
 * `passwordState` is `true`. In its [RowScope] `content` composable lambda argument we compose
 * a [Text] whose `text` argument is the [String] whose resource ID is `R.string.sign_in` ("Sign in").
 *
 * @param email Email to pre-fill the email field with.
 * @param onSignInSubmitted Callback to be invoked when the sign in form is submitted.
 */
@Composable
fun SignInContent(
    email: String?,
    onSignInSubmitted: (email: String, password: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val focusRequester: FocusRequester = remember { FocusRequester() }
        val emailState: TextFieldState by rememberSaveable(
            inputs = arrayOf(email),
            stateSaver = EmailStateSaver
        ) {
            mutableStateOf(value = EmailState(email = email))
        }
        Email(emailState = emailState, onImeAction = { focusRequester.requestFocus() })

        Spacer(modifier = Modifier.height(height = 16.dp))

        val passwordState: PasswordState = remember { PasswordState() }

        val onSubmit: () -> Unit = {
            if (emailState.isValid && passwordState.isValid) {
                onSignInSubmitted(emailState.text, passwordState.text)
            }
        }
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            modifier = Modifier.focusRequester(focusRequester = focusRequester),
            onImeAction = { onSubmit() }
        )
        Spacer(modifier = Modifier.height(height = 16.dp))
        Button(
            onClick = { onSubmit() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = emailState.isValid && passwordState.isValid
        ) {
            Text(
                text = stringResource(id = R.string.sign_in)
            )
        }
    }
}

/**
 * Shows an error snackbar. Our root composable is a [SnackbarHost] whose `hostState` argument is
 * our [SnackbarHostState] parameter [snackbarHostState], whose `snackbar` argument is a lambda that
 * accepts the [SnackbarData] passed the lambda in variable `data` and composes a [Snackbar] composable
 * whose arguments are:
 *  - `modifier`: is a [Modifier.padding] whose `all` argument is `16.dp`.
 *  - `content`: is a lambda that composes a [Text] composable whose `text` argument is the
 *  [SnackbarVisuals.message] of the  [SnackbarData.visuals] of the [SnackbarData] passed in
 *  variable `data`, and whose [TextStyle] `style` is the [Typography.bodyMedium] of our custom
 *  [MaterialTheme.typography].
 *  - `action`: is a lambda which if the [SnackbarVisuals.actionLabel] of the [SnackbarData.visuals]
 *  of the [SnackbarData] passed in variable `data` is not `null` composes a [TextButton] composable
 *  whose `onClick` argument our [onDismiss] lambda parameter, and whose [RowScope] `content`
 *  composable lambda argument is a [Text] whose `text` is the [String] whose resource ID is
 *  `R.string.dismiss` ("Dismiss"), and whose [Color] `color` argument is the
 *  [ColorScheme.inversePrimary] of our custom [MaterialTheme.colorScheme].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.wrapContentHeight] whose
 *  `align` argument is [Alignment.Bottom].
 *
 * @param snackbarHostState The state object that is used to manage the queue of Snackbars.
 * @param modifier Modifier to be applied to the SnackbarHost.
 * @param onDismiss Callback to be invoked when the dismiss action is clicked.
 */
@Composable
fun ErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = { }
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data: SnackbarData ->
            Snackbar(
                modifier = Modifier.padding(all = 16.dp),
                content = {
                    Text(
                        text = data.visuals.message,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                action = {
                    data.visuals.actionLabel?.let {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = stringResource(id = R.string.dismiss),
                                color = MaterialTheme.colorScheme.inversePrimary
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Bottom)
    )
}

/**
 * Two previews of [SignInScreen]:
 *  - One with the light theme
 *  - One with the dark theme
 */
@Preview(name = "Sign in light theme", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Sign in dark theme", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SignInPreview() {
    JetsurveyTheme {
        SignInScreen(
            email = null,
            onSignInSubmitted = { _, _ -> },
            onSignInAsGuest = {},
            onNavUp = {},
        )
    }
}
