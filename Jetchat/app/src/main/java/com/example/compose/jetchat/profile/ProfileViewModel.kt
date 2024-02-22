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
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.data.meProfile

/**
 * This is the [ViewModel] that is used by [ProfileFragment] to hold the [ProfileScreenState] that
 * it is supposed to be displaying.
 */
class ProfileViewModel : ViewModel() {

    /**
     * The [ProfileScreenState.userId] of the [ProfileScreenState] that we should supply to
     * [ProfileFragment] when it observes our [LiveData] wrapped [ProfileScreenState] field
     * [userData].
     */
    private var userId: String = ""

    /**
     * This method is called to set our [String] field [userId] and to update our [MutableLiveData]
     * wrapped [ProfileScreenState] field [_userData] to point to the [ProfileScreenState] whose
     * [ProfileScreenState.userId] is equal to our [userId] field (defaulting to [meProfile] if
     * our [String] parameter [newUserId] is `null`. We check if our [String] parameter [newUserId]
     * is not equal to our [String] field [userId] and if they are different we set [userId] to
     * [newUserId] if [newUserId] is not `null` or to the [ProfileScreenState.userId] property of
     * [meProfile] if it is. Then we set the [MutableLiveData.setValue] (kotlin `value` property)
     * of [_userData] to [meProfile] if [userId] is equal to the [ProfileScreenState.userId] of
     * [meProfile] or [userId] is equal to the [ProfileScreenState.displayName] of [meProfile], else
     * we set it to [colleagueProfile].
     *
     * @param newUserId a [String] that can be used to choose between [meProfile] and [colleagueProfile]
     * to be the [ProfileScreenState] that our [LiveData] wrapped [ProfileScreenState] field [userData]
     * supplies to [ProfileFragment] when it observes it as a [State] wrapped [ProfileScreenState]
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

    /**
     * The private [MutableLiveData] wrapped [ProfileScreenState] that the user has chosen to be
     * displayed by [ProfileFragment].
     */
    private val _userData = MutableLiveData<ProfileScreenState>()

    /**
     * Public read-only access to our [MutableLiveData] wrapped [ProfileScreenState] field [_userData].
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
