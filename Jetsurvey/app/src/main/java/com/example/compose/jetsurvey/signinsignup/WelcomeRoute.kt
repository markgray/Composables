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
 * Route for the Welcome screen. We start by initializing our [WelcomeViewModel] variable
 * `welcomeViewModel` to the existing [WelcomeViewModel] or newly created one with the owner
 * provided by `LocalViewModelStoreOwner` using [viewModel] with its `factory` argument our
 * [WelcomeViewModelFactory] method. Then we comopose a [WelcomeScreen] with the arguments:
 *  - `onSignInSignUp`: A lambda function that takes an email string as an argument and
 *  calls the [WelcomeViewModel.handleContinue] method with the provided email as its `email`
 *  argument, our [onNavigateToSignIn] lambda parameter as its `onNavigateToSignIn` argument, and
 *  our [onNavigateToSignUp] lambda parameter as its `onNavigateToSignUp` argument.
 *  - `onSignInAsGuest`: A lambda function that calls the [WelcomeViewModel.signInAsGuest]
 *  method with our [onSignInAsGuest] lambda parameter as its `onSignInComplete` argument.
 *
 * @param onNavigateToSignIn Navigates to the sign in screen.
 * @param onNavigateToSignUp Navigates to the sign up screen.
 * @param onSignInAsGuest Signs in as a guest.
 */
@Composable
fun WelcomeRoute(
    onNavigateToSignIn: (email: String) -> Unit,
    onNavigateToSignUp: (email: String) -> Unit,
    onSignInAsGuest: () -> Unit,
) {
    val welcomeViewModel: WelcomeViewModel = viewModel(factory = WelcomeViewModelFactory())

    WelcomeScreen(
        onSignInSignUp = { email: String ->
            welcomeViewModel.handleContinue(
                email = email,
                onNavigateToSignIn = onNavigateToSignIn,
                onNavigateToSignUp = onNavigateToSignUp,
            )
        },
        onSignInAsGuest = {
            welcomeViewModel.signInAsGuest(onSignInComplete = onSignInAsGuest)
        },
    )
}
