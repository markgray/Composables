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
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * ViewModel for the sign up screen.
 *
 * @param userRepository The repository for user data.
 */
class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

    /**
     * Sign up a new user with the given email and password. Considers all sign ups successful.
     * First we call the [UserRepository.signUp] method of our [UserRepository] property
     * [userRepository] with its `email` argument our [String] parameter [email] and its `password`
     * argument our [String] parameter [password]. Then we call our [onSignUpComplete] lambda
     * parameter.
     *
     * @param email The email address of the user.
     * @param password The password for the user.
     * @param onSignUpComplete A callback to be invoked when the sign-up process is complete.
     */
    fun signUp(
        email: String,
        password: String,
        onSignUpComplete: () -> Unit,
    ) {
        userRepository.signUp(email = email, password = password)
        onSignUpComplete()
    }

    /**
     * Sign in as a guest user. First we call the [UserRepository.signInAsGuest] method of our
     * [UserRepository] property [userRepository]. Then we call our [onSignInComplete] lambda
     * parameter.
     *
     * @param onSignInComplete A callback to be invoked when the guest sign-in process is complete.
     */
    fun signInAsGuest(
        onSignInComplete: () -> Unit,
    ) {
        userRepository.signInAsGuest()
        onSignInComplete()
    }
}

/**
 * Factory for creating instances of [SignUpViewModel].
 *
 * This factory is responsible for creating [SignUpViewModel] instances,
 * providing the necessary dependencies, such as the [UserRepository].
 *
 * It implements the [ViewModelProvider.Factory] interface, which is used
 * by the Android Architecture Components to create ViewModels.
 */
class SignUpViewModelFactory : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the [SignUpViewModel]. This method is used as the `factory` argument
     * of [viewModel]. We first check if the [modelClass] is assignable from [SignUpViewModel]. If
     * it is, we return a new instance of [SignUpViewModel] with the [UserRepository] dependency
     * injected. If the [modelClass] is not assignable from [SignUpViewModel], we throw an
     * [IllegalArgumentException] with the message "Unknown ViewModel class".
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created [SignUpViewModel]
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(userRepository = UserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
