/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.reply.data

import androidx.annotation.DrawableRes

/**
 * An object which represents an account which can belong to a user. A single user can have
 * multiple accounts.
 *
 * @param id Unique ID of this [Account], no two [Account]'s will share the same [id]
 * @param uid The ID of the user of the [Account], a single user can have multiple accounts.
 * @param firstName The first name of the user of the [Account].
 * @param lastName The last name of the user of the [Account].
 * @param email The primary email address associated with the [Account].
 * @param altEmail An alternate email address associated with the [Account].
 * @param avatar The resource of a drawable that can be used to represent the [Account] or its user.
 * @param isCurrentAccount No idea what the intended use of this is, it defaults to `false` for all
 * execpt the [Account] whose [Account.id] is 1L which is the first of the three accounts owned by
 * [Account.uid] 0L, whose is the "current user" of the app (aka "Jeff Hansen")
 */
data class Account(
    val id: Long,
    val uid: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val altEmail: String,
    @param:DrawableRes val avatar: Int,
    var isCurrentAccount: Boolean = false
) {
    /**
     * The "full name" of the owner of the [Account].
     */
    val fullName: String = "$firstName $lastName"
}
