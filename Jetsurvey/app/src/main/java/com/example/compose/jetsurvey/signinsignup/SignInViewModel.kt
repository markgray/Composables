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
 * ViewModel that handles the business logic of the Sign In screen.
 *
 * @property userRepository a repository for accessing the user data.
 */
class SignInViewModel(private val userRepository: UserRepository) : ViewModel() {

    /**
     * Signs in the user with the given email and password. Considers all sign ins successful
     *
     * @param email The email address of the user.
     * @param password The password of the user.
     * @param onSignInComplete A callback function that should be called when the sign-in process
     * is complete.
     */
    fun signIn(
        email: String,
        password: String,
        onSignInComplete: () -> Unit,
    ) {
        userRepository.signIn(email = email, password = password)
        onSignInComplete()
    }

    /**
     * Signs in the user as a guest.
     *
     * This function allows the user to sign in as a guest, without providing any credentials.
     *
     * @param onSignInComplete A callback function that should be called when the sign-in process
     * is complete.
     */
    fun signInAsGuest(
        onSignInComplete: () -> Unit,
    ) {
        userRepository.signInAsGuest()
        onSignInComplete()
    }
}

/**
 * Factory for creating [SignInViewModel] instances.
 */
class SignInViewModelFactory : ViewModelProvider.Factory {
    /**
     * Creates a new instance of [SignInViewModel].
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(userRepository = UserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
