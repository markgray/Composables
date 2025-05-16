/*
 * Copyright 2023 The Android Open Source Project
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

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Displays the sign up screen and manages UI state. We start by initializing our [SignUpViewModel]
 * variable `signUpViewModel` with a [SignUpViewModelFactory]. Then our root composable is a
 * [SignInSignUpScreen] whose arguments are:
 *  - `email`: is our [String] parameter [email].
 *  - `onSignUpSubmitted`: is a lambda that accepts the two [String]'s passed the lambda in variables
 *  `email` and `password`, then calls the [SignUpViewModel.signUp] method of our [SignUpViewModel]
 *  variable `signUpViewModel` with its `email` argument our `email` variable, its `password`
 *  argument our `password` variable, and its `onSignUpComplete` argument our [onSignUpSubmitted]
 *  lambda parameter.
 *  - `onSignInAsGuest`: is a lambda that calls the [SignUpViewModel.signInAsGuest] method of
 *  our [SignUpViewModel] variable `signUpViewModel` with its `onSignInComplete` argument our
 *  [onSignInAsGuest] lambda parameter.
 *  - `onNavUp`: is a our [onNavUp] lambda parameter.
 *
 * @param email (state) email to prepopulate the email field.
 * @param onSignUpSubmitted (event) submitted with email and password.
 * @param onSignInAsGuest (event) request to sign in as a guest.
 * @param onNavUp (event) send when the user clicks the "up" button.
 */
@Composable
fun SignUpRoute(
    email: String?,
    onSignUpSubmitted: () -> Unit,
    onSignInAsGuest: () -> Unit,
    onNavUp: () -> Unit,
) {
    val signUpViewModel: SignUpViewModel = viewModel(factory = SignUpViewModelFactory())
    SignUpScreen(
        email = email,
        onSignUpSubmitted = { email: String, password: String ->
            signUpViewModel.signUp(
                email = email,
                password = password,
                onSignUpComplete = onSignUpSubmitted
            )
        },
        onSignInAsGuest = {
            signUpViewModel.signInAsGuest(onSignInComplete = onSignInAsGuest)
        },
        onNavUp = onNavUp,
    )
}
