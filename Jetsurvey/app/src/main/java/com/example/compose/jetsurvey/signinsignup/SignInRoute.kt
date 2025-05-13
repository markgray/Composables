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
 * Sign in route. We start by initializing our [SignInViewModel] variable using the [viewModel]
 * method with [SignInViewModelFactory] as the `factory`. Then we compose the [SignInScreen]
 * composable with the following arguments:
 *   - `email`: The pre-filled email address for the sign-in form, if any.
 *   - `onSignInSubmitted`: The lambda to execute when the sign-in form is submitted successfully.
 *   - `onSignInAsGuest`: The lambda to execute when the "Sign In as Guest" button is clicked.
 *   - `onNavUp`: The lambda to execute when navigating up.
 *
 * @param email The pre-filled email address for the sign-in form, if any.
 * @param onSignInSubmitted The action to perform when the sign-in form is submitted successfully.
 * @param onSignInAsGuest The action to perform when the "Sign In as Guest" button is clicked.
 * @param onNavUp The action to perform when navigating up.
 */
@Composable
fun SignInRoute(
    email: String?,
    onSignInSubmitted: () -> Unit,
    onSignInAsGuest: () -> Unit,
    onNavUp: () -> Unit,
) {
    val signInViewModel: SignInViewModel = viewModel(factory = SignInViewModelFactory())
    SignInScreen(
        email = email,
        onSignInSubmitted = { email: String, password: String ->
            signInViewModel.signIn(
                email = email,
                password = password,
                onSignInComplete = onSignInSubmitted
            )
        },
        onSignInAsGuest = {
            signInViewModel.signInAsGuest(onSignInComplete = onSignInAsGuest)
        },
        onNavUp = onNavUp,
    )
}
