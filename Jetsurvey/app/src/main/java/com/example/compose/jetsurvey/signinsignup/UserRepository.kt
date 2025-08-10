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

@file:Suppress("RedundantSuppression")

package com.example.compose.jetsurvey.signinsignup

import androidx.compose.runtime.Immutable

/**
 * Represents the state of the current user.
 *
 * This sealed class has three possible states:
 *  - [LoggedInUser]: The user is logged in. Contains the user's email address.
 *  - [GuestUser]: The user is using the app as a guest.
 *  - [NoUserLoggedIn]: No user is currently logged in. This is the initial state.
 */
sealed class User {
    @Immutable
    data class LoggedInUser(val email: String) : User()
    object GuestUser : User()
    object NoUserLoggedIn : User()
}

/**
 * Singleton Repository that holds the logged in user.
 *
 * In a production app, this class would also handle the communication with the backend for
 * sign in and sign up.
 */
object UserRepository {

    /**
     * This property holds the current state of the user. It is initialized to [User.NoUserLoggedIn],
     * indicating that no user is logged in when the repository is first created.
     * The state of this property can be changed by calling the [signIn], [signUp], or [signInAsGuest]
     * methods. The current user state can be observed via the public [user] property.
     */
    private var _user: User = User.NoUserLoggedIn

    /**
     * Public property to observe the current user state. It returns the current value of the
     * private [_user] property. This allows other parts of the application to react to changes
     * in the user's login status.
     */
    @Suppress("unused")
    val user: User
        get() = _user

    /**
     * Signs in a user with the given email and password.
     *
     * In a production app, this function would make a network request to authenticate the user.
     * For this sample, it simply updates the user state to [User.LoggedInUser] with the provided
     * email address.
     *
     * @param email The email address of the user to sign in.
     * @param password The password of the user to sign in.
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    fun signIn(email: String, password: String) {
        _user = User.LoggedInUser(email = email)
    }

    /**
     * Signs up a new user with the given email and password.
     *
     * In a production app, this function would make a network request to create a new user account.
     * For this sample, it simply updates the user state to [User.LoggedInUser] with the provided
     * email address, effectively logging in the new user.
     *
     * @param email The email address of the new user.
     * @param password The password for the new user account.
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    fun signUp(email: String, password: String) {
        _user = User.LoggedInUser(email = email)
    }

    /**
     * Signs in the user as a guest.
     *
     * This function updates the user state to [User.GuestUser], allowing the user to
     * proceed with limited functionality without providing credentials.
     */
    fun signInAsGuest() {
        _user = User.GuestUser
    }

    /**
     * Checks if the given email address is associated with a known user.
     *
     * In a production app, this function would query a database or backend service
     * to determine if the email exists. For this sample, it uses a simple heuristic:
     * if the email contains the string "signup", it's considered an unknown user,
     * otherwise, it's considered a known user.
     *
     * @param email The email address to check.
     * @return `true` if the email is associated with a known user, `false` otherwise.
     */
    fun isKnownUserEmail(email: String): Boolean {
        // if the email contains "sign up" we consider it unknown
        return !email.contains("signup")
    }
}
