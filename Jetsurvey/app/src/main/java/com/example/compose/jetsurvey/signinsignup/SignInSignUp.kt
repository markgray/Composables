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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.JetsurveyTheme
import com.example.compose.jetsurvey.theme.stronglyDeemphasizedAlpha

/**
 * Sign in or Sign up screen content. Our root composable is a [LazyColumn] whose `modifier` argument
 * is our [Modifier] parameter `modifier`, and whose `contentPadding` argument is our [PaddingValues]
 * parameter [contentPadding]. In the [LazyListScope] `content` composable lambda argument we compose
 * an [LazyListScope.item], and in its [LazyItemScope] `content` composable lambda argument we compose
 * the following:
 *
 * **First** a [Spacer] whose `modifier` argument is a [Modifier.height] with a `height` of 44.dp.
 *
 * **Second** a [Box] whose `modifier` argument is a [Modifier.fillMaxWidth], chained to a
 * [Modifier.padding] that adds `20.dp` to each `horizontal` side. In the [BoxScope] `content`
 * composable lambda argument we compose our [content] lambda parameter.
 *
 * **Third** a [Spacer] whose `modifier` argument is a [Modifier.height] with a `height` of 16.dp.
 *
 * **Fourth** a [OrSignInAsGuest] composable whose `onSignInAsGuest` argument is our [onSignInAsGuest]
 * lambda parameter, and whose `modifier` argument is a [Modifier.fillMaxWidth], chained to a
 * [Modifier.padding] that adds `20.dp` to each `horizontal` side.
 *
 * @param onSignInAsGuest (event) to be triggered when sign in as guest is clicked
 * @param modifier modifier for this screen
 * @param contentPadding padding for the content, this is the [PaddingValues] that the [Scaffold]
 * passes its `content` composable lambda argument which we are composed in.
 * @param content (slot) the main content for this screen
 */
@Composable
fun SignInSignUpScreen(
    onSignInAsGuest: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        item {
            Spacer(modifier = Modifier.height(height = 44.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                content()
            }
            Spacer(modifier = Modifier.height(height = 16.dp))
            OrSignInAsGuest(
                onSignInAsGuest = onSignInAsGuest,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

/**
 * A [CenterAlignedTopAppBar] with a title, a navigation icon, and an action. Our root composable is
 * a [CenterAlignedTopAppBar] whose `title` argument is a lambda that composes a [Text] whose `text`
 * argument is our [String] parameter [topAppBarText], and whose `modifier` argument is a
 * [Modifier.fillMaxSize], chained to a [Modifier.wrapContentSize] that aligns the [Text] to the 
 * [Alignment.Center] of the [CenterAlignedTopAppBar]. The `navigationIcon` argument is a lambda that
 * composes an [IconButton] whose `onClick` argument is a lambda that calls our [onNavUp] lambda
 * parameter, and in the [IconButton]'s `content` composable lambda argument we compose an [Icon]
 * whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Filled.ChevronLeft]
 *  - `contentDescription`: is the [String] with resource ID `R.string.back` ("Back").
 *  - `tint`: [Color] is the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme].
 *
 * The `actions` argument of the [CenterAlignedTopAppBar] is a lambda that composes a [Spacer]
 * whose `modifier` argument is a [Modifier.width] with a `width` of `68.dp`. (to balance the
 * navigation icon.
 *
 * @param topAppBarText The text to display in the title of the top app bar.
 * @param onNavUp The callback to be invoked when the navigation icon is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class) // CenterAlignedTopAppBar is experimental in m3
@Composable
fun SignInSignUpTopAppBar(
    topAppBarText: String,
    onNavUp: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = topAppBarText,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavUp) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        // We need to balance the navigation icon, so we add a spacer.
        actions = {
            Spacer(modifier = Modifier.width(width = 68.dp))
        },
    )
}

/**
 * This composable is an [OutlinedTextField] configured for use as an email address input field. Its
 * `value` argument is the [TextFieldState.text] property of our [TextFieldState] parameter
 * [emailState], and its `onValueChange` lambda argument sets the [TextFieldState.text] property of
 * [emailState] to the new value. Its `label` argument is a lambda which composes a [Text] displaying
 * the string with resource ID `R.string.email` ("Email") and its `style` argument is the
 * [Typography.bodyMedium] of our custom  [MaterialTheme.typography]. The `modifier` argument of the
 * [OutlinedTextField] is a [Modifier.fillMaxWidth] to make the [OutlinedTextField] take up its entire
 * incoming width constraint, with an [Modifier.onFocusChanged] chained to it whose lambda argument
 * accepts the [FocusState] passed the lambda in variable `focusState` and calls the
 * [TextFieldState.onFocusChange] method of [emailState] with the `isFocused` property of the
 * [FocusState] passed the lambda as its argument. If the field is NOT focused it calls the
 * [TextFieldState.enableShowErrors] method of [emailState]. Its `textStyle` argument is the
 * [Typography.bodyMedium] of our custom [MaterialTheme.typography]. Its `isError` argument is the
 * [TextFieldState.showErrors] method of [emailState]. Its `keyboardOptions` argument is a copy of
 * [KeyboardOptions.Default] whose `imeAction` is our [ImeAction] parameter [imeAction] and whose
 * `keyboardType` is [KeyboardType.Email]. Its `keyboardActions` argument is a [KeyboardActions]
 * whose `onDone` lambda argument is a lambda that calls our [onImeAction] parameter. Its
 * `singleLine` argument is `true` so that the text will not wrap on multiple lines.
 *
 * Below this [OutlinedTextField] is an if statement which if the [TextFieldState.getError] method of
 * [emailState] returns a non-null [String] will compose a [TextFieldError] whose `textError` argument
 * is that [String].
 *
 * @param emailState the [TextFieldState] that holds the current text in the [OutlinedTextField] and
 * any errors that it may have. We use the default `remember { EmailState() }` to create and remember
 * a default [EmailState] if none is passed by our caller.
 * @param imeAction the [ImeAction] to be used for the keyboard.
 * @param onImeAction lambda called when the [ImeAction] is "Done".
 */
@Composable
fun Email(
    emailState: TextFieldState = remember { EmailState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = emailState.text,
        onValueChange = {
            emailState.text = it
        },
        label = {
            Text(
                text = stringResource(id = R.string.email),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState: FocusState ->
                emailState.onFocusChange(focused = focusState.isFocused)
                if (!focusState.isFocused) {
                    emailState.enableShowErrors()
                }
            },
        textStyle = MaterialTheme.typography.bodyMedium,
        isError = emailState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        singleLine = true
    )

    emailState.getError()?.let { error: String -> TextFieldError(textError = error) }
}

/**
 * This composable is an [OutlinedTextField] configured for use as a password input field.
 * It displays an icon which allows the user to toggle the visibility of the password.
 * We start by initializing and remembering our [MutableState] wrapped variable `showPassword` to
 * an initial value of `false`. Then our root composable is an [OutlinedTextField] whose `value`
 * argument is the [TextFieldState.text] property of our [TextFieldState] parameter [passwordState],
 * whose `onValueChange` lambda argument sets the [TextFieldState.text] property of [passwordState]
 * to the new value and calls its [TextFieldState.enableShowErrors] method. Its `modifier` argument
 * is our [Modifier] parameter [modifier] with a [Modifier.fillMaxWidth] chained to it to make the
 * [OutlinedTextField] occupy its entire incoming width constraint, with a [Modifier.onFocusChanged]
 * chained to that whose `onFocusChanged` lambda argument accepts the [FocusState] passed the lambda
 * in variable `focusState` and calls the [TextFieldState.onFocusChange] method of [passwordState]
 * with the `isFocused` property of the [FocusState] passed the lambda as its argument. If the field
 * is NOT focused it calls the [TextFieldState.enableShowErrors] method of [passwordState]. Its
 * `textStyle` argument is the [Typography.bodyMedium] of our custom [MaterialTheme.typography]. Its
 * `label` argument is a lambda that composes a [Text] whose `text` is our [String] parameter
 * [label], and whose `style` argument is the [Typography.bodyMedium] of our custom
 * [MaterialTheme.typography]. The `trailingIcon` argument is a lambda that composes an [IconButton]
 * whose `onClick` argument is a lambda which toggles the value of our [MutableState] of [Boolean]
 * variable `showPassword` (our flag which controls whether the password text is visible or hidden
 * with asterisks). The `content` of the [IconButton] is an [Icon] which depends on the the current
 * vqlue of our [MutableState] wrapped [Boolean] variable `showPassword`:
 *  - If `showPassword` is `true` the `imageVector` of the [Icon] is [Icons.Filled.Visibility]
 *  (eyeball) and its `contentDescription` is the string with resource ID `R.string.hide_password`
 *  ("Hide password").
 *  - If `showPassword` is `false` the `imageVector` of the [Icon] is [Icons.Filled.VisibilityOff]
 *  and its `contentDescription` is the string with resource ID `R.string.show_password`
 *  ("Show password")
 *
 * The `visualTransformation` argument (transforms the visual representation of the input value For
 * example, you can use [PasswordVisualTransformation] to create a password text field. By default,
 * no visual transformation is applied.) is a [VisualTransformation] which depends on the current
 * value of our [MutableState] wrapped [Boolean] variable `showPassword`:
 *  - If `showPassword` is `true` the `visualTransformation` is [VisualTransformation.None]
 *  - If `showPassword` is `false` the `visualTransformation` is [PasswordVisualTransformation]
 *
 * The `isError` argument of the [OutlinedTextField] is the [TextFieldState.showErrors] method of
 * [passwordState]. Its `supportingText` argument is a lambda which if the [TextFieldState.getError]
 * method of [passwordState] returns a non-null [String] will compose a [TextFieldError] whose
 * `textError` argument is that [String]. The `keyboardOptions` argument is a copy of the
 * [KeyboardOptions.Default] whose `imeAction` property is our [ImeAction] parameter [imeAction] and
 * whose `keyboardType` property is [KeyboardType.Password]. Its `keyboardActions` argument is a
 * [KeyboardActions] whose `onDone` lambda argument is a lambda that calls our [onImeAction]
 * parameter. Its `singleLine` argument is `true` so that the text will not wrap on multiple lines.
 *
 * @param label The label to be displayed inside the text field.
 * @param passwordState the [TextFieldState] that holds the current text in the [OutlinedTextField]
 * and any errors that it may have. We use the default `remember { PasswordState() }` to create and
 * remember a default [PasswordState] if none is passed by our caller.
 * @param modifier the [Modifier] to be applied to the [OutlinedTextField].
 * @param imeAction the [ImeAction] to be used for the keyboard.
 * @param onImeAction lambda called when the [ImeAction] is "Done".
 */
@Composable
fun Password(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val showPassword: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = passwordState.text,
        onValueChange = { newPassword: String ->
            passwordState.text = newPassword
            passwordState.enableShowErrors()
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState: FocusState ->
                passwordState.onFocusChange(focused = focusState.isFocused)
                if (!focusState.isFocused) {
                    passwordState.enableShowErrors()
                }
            },
        textStyle = MaterialTheme.typography.bodyMedium,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = stringResource(id = R.string.hide_password)
                    )
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(id = R.string.show_password)
                    )
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        isError = passwordState.showErrors(),
        supportingText = {
            passwordState.getError()?.let { error: String -> TextFieldError(textError = error) }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        singleLine = true
    )
}

/**
 * To be removed when [OutlinedTextField]s support error slot.
 * This composable is a [Row] whose `modifier` argument is a [Modifier.fillMaxWidth] to make it
 * occupy its entire incoming width constraint. The [RowScope] `content` composable lambda argument
 * of the [Row] contains:
 *  - A [Spacer] whose `modifier` argument is a [Modifier.width] that sets its `width` to 16.dp
 *  - A [Text] whose `text` argument is our [String] parameter [textError], whose `modifier` argument
 *  is a [Modifier.fillMaxWidth] to make it occupy the rest of the width of the [Row], and whose
 *  [Color] `color` argument is the [ColorScheme.error] color of our custom [MaterialTheme.colorScheme].
 *
 * @param textError the error text to be displayed.
 */
@Composable
fun TextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(width = 16.dp))
        Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.error
        )
    }
}

/**
 * This composable consists of a [Column] whose `modifier` argument is our [Modifier] parameter
 * [modifier], and whose `horizontalAlignment` argument is [Alignment.CenterHorizontally] to center
 * its children. The [ColumnScope] `content` composable lambda argument of the [Column] contains:
 *  - A [Text] displaying the string with resource ID `R.string.or` ("or") using the `style`
 *  [Typography.titleSmall] of our custom [MaterialTheme.typography], with its [Color] `color`
 *  argument a copy of the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme] with
 *  its `alpha` set to [stronglyDeemphasizedAlpha] (0.6f), and its `modifier` argument is a
 *  [Modifier.paddingFromBaseline] that adds 25.dp of padding to its `top`.
 *  - An [OutlinedButton] whose `onClick` argument is our lambda parameter [onSignInAsGuest], and
 *  whose `modifier` argument is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds
 *  20.dp to its `top` and 24.dp to its `bottom`. The [RowScope] `content` composable lambda argument
 *  of the [OutlinedButton] composes a [Text] displaying the string with resource ID
 *  `R.string.sign_in_guest` ("Sign in as guest").
 *
 * @param onSignInAsGuest (event) Swampy representation of dividing screen sections.
 * @param modifier [Modifier] to be applied to the [Column].
 */
@Composable
fun OrSignInAsGuest(
    onSignInAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.or),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = stronglyDeemphasizedAlpha),
            modifier = Modifier.paddingFromBaseline(top = 25.dp)
        )
        OutlinedButton(
            onClick = onSignInAsGuest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 24.dp),
        ) {
            Text(text = stringResource(id = R.string.sign_in_guest))
        }
    }
}

/**
 * Preview of the [SignInSignUpScreen].
 */
@Preview
@Composable
fun SignInSignUpScreenPreview() {
    JetsurveyTheme {
        Surface {
            SignInSignUpScreen(
                onSignInAsGuest = {},
                content = {}
            )
        }
    }
}
