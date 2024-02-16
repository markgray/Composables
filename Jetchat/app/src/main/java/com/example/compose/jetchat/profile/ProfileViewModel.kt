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

package com.example.compose.jetchat.profile

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.data.meProfile

/**
 * TODO: Add kdoc
 */
class ProfileViewModel : ViewModel() {

    private var userId: String = ""

    /**
     * TODO: Add kdoc
     */
    fun setUserId(newUserId: String?) {
        if (newUserId != userId) {
            userId = newUserId ?: meProfile.userId
        }
        // Workaround for simplicity
        _userData.value = if (userId == meProfile.userId || userId == meProfile.displayName) {
            meProfile
        } else {
            colleagueProfile
        }
    }

    private val _userData = MutableLiveData<ProfileScreenState>()
    /**
     * TODO: Add kdoc
     */
    val userData: LiveData<ProfileScreenState> = _userData
}

/**
 * This data class holds all the information we know about the user whose user ID is the [String]
 * field [userId].
 *
 * @param userId a [String] uniquely identifies the person that this [ProfileScreenState] belongs to
 * in our case only "me" and "12345" are used so far.
 * @param photo the resource ID of a png picture of the user that this [ProfileScreenState] belongs to.
 * @param name the name of the user that this [ProfileScreenState] belongs to.
 * @param status the status of the user, either "Online" or "Away" in our case.
 * @param displayName this is used in a [Message] to refer to this [ProfileScreenState] when preceded
 * by an "@" character. Clicking on the [displayName] in the [Message] will cause the [ProfileFragment]
 * to be launched to display this [ProfileScreenState].
 * @param position the job title and company of the user.
 * @param twitter the user's twitter account.
 * @param timeZone the timezone that the user is in.
 * @param commonChannels unused apparently, but is the [String] "2" for [colleagueProfile] and `null`
 * for [meProfile] for what that is worth.
 */
@Immutable
data class ProfileScreenState(
    val userId: String,
    @DrawableRes val photo: Int?,
    val name: String,
    val status: String,
    val displayName: String,
    val position: String,
    val twitter: String = "",
    val timeZone: String?, // Null if me
    val commonChannels: String? // Null if me
) {
    /**
     * Returns `true` is the [userId] of this [ProfileScreenState] is equal to the
     * [ProfileScreenState.userId] of [meProfile].
     */
    fun isMe(): Boolean = userId == meProfile.userId
}
