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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.theme.stronglyDeemphasizedAlpha
import com.example.compose.jetsurvey.util.supportWideScreen

/**
 * The Welcome screen. We start by initializing and remembering our [MutableState] wrapped [Boolean]
 * variable `showBranding` to `true`. Then we compose a [Scaffold] whose `modifier` argument is a
 * [Modifier.supportWideScreen]. In its `content` composable lambda argument we accept the
 * [PaddingValues] passed the lambda in variable `innerPadding` and then compose a [Column]
 * whose `modifier` argument is a [Modifier.padding] whose `paddingValues` argument is `innerPadding`
 * with a [Modifier.fillMaxWidth] chained to that, and at the end of the chain is a
 * [Modifier.verticalScroll] whose `state` argument is the remembered [ScrollState] returned by
 * [rememberScrollState].
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] first compose an
 * [ColumnScope.AnimatedVisibility] whose `visible` argument is our [MutableState] wrapped [Boolean]
 * variable `showBranding` and whose `modifier` argument is a [Modifier.fillMaxWidth]. In its
 * [AnimatedVisibilityScope] `content` lambda argument we compose our [Branding] composable.
 *
 * Below that we compose a [SignInCreateAccount] composable whose arguments are:
 *  - `onSignInSignUp`: is our [onSignInSignUp] lambda parameter.
 *  - `onSignInAsGuest`: is our [onSignInAsGuest] lambda parameter.
 *  - `onFocusChange`: is a lambda that accepts the [Boolean] passed the lambda in variable `focused`
 *  then sets our [MutableState] wrapped [Boolean] variable `showBranding` to the inverse of `focused`.
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `20.dp` to
 *  each `horizontal` side of the content.
 *
 * @param onSignInSignUp The callback invoked when the user taps on the "Sign in or create account"
 * button.
 * @param onSignInAsGuest The callback invoked when the user taps on the "Sign in as guest" button.
 */
@Composable
fun WelcomeScreen(
    onSignInSignUp: (email: String) -> Unit,
    onSignInAsGuest: () -> Unit,
) {
    var showBranding: Boolean by rememberSaveable { mutableStateOf(true) }

    Scaffold(modifier = Modifier.supportWideScreen()) { innerPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
        ) {
            AnimatedVisibility(
                visible = showBranding,
                modifier = Modifier.fillMaxWidth()
            ) {
                Branding()
            }

            SignInCreateAccount(
                onSignInSignUp = onSignInSignUp,
                onSignInAsGuest = onSignInAsGuest,
                onFocusChange = { focused: Boolean -> showBranding = !focused },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

/**
 * The branding section of the [WelcomeScreen]. It consists of a [Logo] composable displayed above
 * a [Text] that holds the tagline of the app, both of which are centered horizontally in a [Column].
 *
 * Our root composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier]
 * to which is chained a [Modifier.wrapContentHeight] whose `align` argument is
 * [Alignment.CenterVertically] (Align the content of this layout modifier to the center of the
 * vertical axis, sampling the main axis alignment from the incoming measurement constraint).
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we first compose a
 * [Logo] whose `modifier` argument is a [ColumnScope.align] whose `alignment` argument is
 * [Alignment.CenterHorizontally] with a [Modifier.padding] whose `horizontal` padding is `76.dp`.
 *
 * Below the [Logo] is a [Text] whose arguments are:
 *  - `text`: the [String] with resource ID `R.string.app_tagline`
 *  ("Better surveys with Jetpack Compose").
 *  - `style`: the [Typography.titleMedium] of our custom [MaterialTheme.typography]
 *  - `textAlign`: is [TextAlign.Center]
 *  - `modifier`: is a [Modifier.padding] whose `top` padding is `24.dp`, to which is chained a
 *  [Modifier.fillMaxWidth]
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller ([WelcomeScreen]) passes us a [Modifier.fillMaxWidth] causing us to occupy
 * the entire incoming width constraint.
 */
@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(horizontal = 76.dp)
        )
        Text(
            text = stringResource(id = R.string.app_tagline),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        )
    }
}

/**
 * Displays the Jetsurvey logo. There are two versions of the logo, one for light theme and one for
 * dark theme.
 *
 * We start by initializing our [Int] variable `assetId` to the drawable with resource ID
 * `R.drawable.ic_logo_light` if our [Boolean] parambeter [lightTheme] is `true` or to the
 * drawable with resource ID `R.drawable.ic_logo_dark` otherwise. Then we compose an [Image] whose
 * arguments are:
 *  - `painter`: is the [Painter] created from the drawable with resource ID `assetId`.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `contentDescription`: is `null`.`
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller ([Branding]) calls us with a [ColumnScope.align] whose `alignment` argument
 * is [Alignment.CenterHorizontally] (to center us horizontally in the [Column] that `Branding` uses
 * as its root Composable) chained to a [Modifier.padding] that adds `76.dp` to each `horizontal`
 * side.
 * @param lightTheme if `true` the drawable with resource ID `R.drawable.ic_logo_light` is displayed,
 * otherwise the drawable with resource ID `R.drawable.ic_logo_dark` is displayed. It defaults to
 * `true` if the `luminance` of the `current` [LocalContentColor] is less than 0.5f, otherwise it
 * defaults to `false`.
 */
@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId: Int = if (lightTheme) {
        R.drawable.ic_logo_light
    } else {
        R.drawable.ic_logo_dark
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
}

/**
 * This composable allows the user to sign in or create an account. It consists of a [Column]
 * holding a [Text] ("Sign in or create account"), an [Email] composable for email input, a
 * [Button] ("CONTINUE") that submits the email, and an [OrSignInAsGuest] composable that allows
 * the user to sign in as a guest.
 *
 * We start by initializing and remembering as a saveable our [MutableState] wrapped [TextFieldState]
 * variable `emailState` to a new instance.
 *
 * Then our root composable is a [Column] whose `modifier` argument is our [Modifier] parameter
 * [modifier], and whose `horizontalAlignment` argument is [Alignment.CenterHorizontally]. In the
 * [ColumnScope] `content` composable lambda argument of the [Column] we first compose a [Text]
 * whose arguments are:
 *  - `text`: the [String] with resource ID `R.string.sign_in_create_account` ("Sign in or create an
 *  account")
 *  - `style`: is the [Typography.bodyMedium] of our custom [MaterialTheme.typography]
 *  - `color`: is a copy of the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme]
 *  with its `alpha` argument set to [stronglyDeemphasizedAlpha].
 *  - `textAlign`: is [TextAlign.Center]
 *  - `modifier`: is a [Modifier.padding] whose `top` padding is `64.dp`, and whose `bottom` padding
 *  is `12.dp`.
 *
 * Next we initialize our lambda variable `onSubmit` to a lambda that calls our [onSignInSignUp]
 * lambda parameter with the [TextFieldState.text] of our [MutableState] wrapped [TextFieldState]
 * if its [TextFieldState.isValid] property is `true` or otherwise calls the
 * [TextFieldState.enableShowErrors] method of our [MutableState] wrapped [TextFieldState] variable
 * `emailState`.
 *
 * Then we call our [onFocusChange] lambda parameter with the [TextFieldState.isFocused] property of
 * our [MutableState] wrapped [TextFieldState] variable `emailState`.
 *
 * Then we compose an [Email] composable whose arguments are:
 *  - `emailState`: is our [MutableState] wrapped [TextFieldState] variable `emailState`.
 *  - `imeAction`: is [ImeAction.Done]
 *  - `onImeAction`: is our lambda variable `onSubmit`.
 *
 * Below the [Email] we compose a [Button] whose arguments are:
 *  - `onClick`: is our lambda variable `onSubmit`.
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] whose `top` padding
 *  is `28.dp`, and whose `bottom` padding is `3.dp`.
 *
 * In the [RowScope] `content` composable lambda argument` of the [Button] we compose a [Text]
 * whose arguments are:
 *  - `text`: the [String] with resource ID `R.string.user_continue` ("Continue")
 *  - `style`: is the [Typography.titleSmall] of our custom [MaterialTheme.typography].
 *
 * At the bottom of the [Column] we compose an [OrSignInAsGuest] composable whose arguments are:
 *  - `onSignInAsGuest`: is our [onSignInAsGuest] lambda parameter.
 *  - `modifier`: is a [Modifier.fillMaxWidth].
 *
 * @param onSignInSignUp The callback invoked when the user taps on the "Sign in or create account"
 * button.
 * @param onSignInAsGuest The callback invoked when the user taps on the "Sign in as guest" button.
 * @param onFocusChange The callback invoked when the "Sign in or create account" text field gains
 * or loses focus.
 * @param modifier The modifier to apply to this layout.
 */
@Composable
private fun SignInCreateAccount(
    onSignInSignUp: (email: String) -> Unit,
    onSignInAsGuest: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val emailState: TextFieldState by rememberSaveable(
        inputs = arrayOf(EmailStateSaver),
        stateSaver = EmailStateSaver
    ) {
        mutableStateOf(EmailState())
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.sign_in_create_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = stronglyDeemphasizedAlpha),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 64.dp, bottom = 12.dp)
        )
        val onSubmit: () -> Unit = {
            if (emailState.isValid) {
                onSignInSignUp(emailState.text)
            } else {
                emailState.enableShowErrors()
            }
        }
        onFocusChange(emailState.isFocused)
        Email(emailState = emailState, imeAction = ImeAction.Done, onImeAction = onSubmit)
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 3.dp)
        ) {
            Text(
                text = stringResource(id = R.string.user_continue),
                style = MaterialTheme.typography.titleSmall
            )
        }
        OrSignInAsGuest(
            onSignInAsGuest = onSignInAsGuest,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Two previews of [WelcomeScreen] with different themes:
 *  - Welcome screen with the light theme
 *  - Welcome screen with the dark theme
 */
@Preview(name = "Welcome light theme", uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Welcome dark theme", uiMode = UI_MODE_NIGHT_NO)
@Composable
fun WelcomeScreenPreview() {
    JetsurveyTheme {
        WelcomeScreen(
            onSignInSignUp = {},
            onSignInAsGuest = {},
        )
    }
}
