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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModel for the Welcome screen.
 *
 * @property userRepository The repository to handle user-related operations.
 */
class WelcomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    /**
     * Handles the navigation when the user clicks the "Continue" button.
     *
     * If the email is known, it navigates to the sign-in screen. Otherwise, it navigates to the
     * sign-up screen.
     *
     * @param email The email address entered by the user.
     * @param onNavigateToSignIn Callback to navigate to the sign-in screen.
     * @param onNavigateToSignUp Callback to navigate to the sign-up screen.
     */
    fun handleContinue(
        email: String,
        onNavigateToSignIn: (email: String) -> Unit,
        onNavigateToSignUp: (email: String) -> Unit,
    ) {
        if (userRepository.isKnownUserEmail(email = email)) {
            onNavigateToSignIn(email)
        } else {
            onNavigateToSignUp(email)
        }
    }

    /**
     * Sign in as a guest and show the survey. First we call the [UserRepository.signInAsGuest]
     * method of our [UserRepository] property [userRepository], then we call our [onSignInComplete]
     * lambda parameter.
     *
     * @param onSignInComplete Called when sign in is complete.
     */
    fun signInAsGuest(
        onSignInComplete: () -> Unit,
    ) {
        userRepository.signInAsGuest()
        onSignInComplete()
    }
}

/**
 * The [ViewModelProvider.Factory] for our [WelcomeViewModel] viewmodel.
 */
class WelcomeViewModelFactory : ViewModelProvider.Factory {
    /**
     * If our [Class] of [T] parameter [modelClass] is assignable from a [WelcomeViewModel] object
     * we return a new instance of [WelcomeViewModel] whose `userRepository` property is the
     * singleton [UserRepository], otherwise we throw an [IllegalArgumentException] "Unknown
     * ViewModel class".
     *
     * @return a new instance of [WelcomeViewModel].
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(userRepository = UserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
