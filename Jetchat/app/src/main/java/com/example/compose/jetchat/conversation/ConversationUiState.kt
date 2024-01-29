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

package com.example.compose.jetchat.conversation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.example.compose.jetchat.R
import com.example.compose.jetchat.data.meProfile
import com.example.compose.jetchat.data.colleagueProfile
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.profile.ProfileScreenState

/**
 * This is used as the UI state of the [ConversationContent] Composable
 */
class ConversationUiState(
    /**
     * This used as the `channelName` argument of the [ChannelNameBar] composable used as the `topBar`
     * of the [Scaffold] used in [ConversationContent]. It is displayed in a [Text] that is used as
     * part of the `title` argument of the [JetchatAppBar] used in [ChannelNameBar].
     */
    val channelName: String,
    /**
     * This used as the `channelMembers` argument of the [ChannelNameBar] composable used as the
     * `topBar` of the [Scaffold] used in [ConversationContent]. It is displayed in a [Text] that is
     * used as part of the `title` argument of the [JetchatAppBar] used in [ChannelNameBar].
     */
    val channelMembers: Int,
    /**
     * This is used to initialize our [MutableList] of [Message] field [_messages]
     */
    initialMessages: List<Message>
) {
    /**
     * Our [SnapshotStateList] wrapped [MutableList] of [Message] dataset. It is private to prevent
     * the rest of the app modifying it, read only access is provided by our [List] of [Message]
     * field [messages].
     */
    private val _messages: MutableList<Message> = initialMessages.toMutableStateList()

    /**
     * Read only access to our [SnapshotStateList] wrapped [MutableList] of [Message] dataset field
     * [_messages]. It is used as the `messages` argument of the [Messages] Composable used in
     * [ConversationContent]. It is used by [Messages] to feed data to the entries in its [LazyColumn].
     */
    val messages: List<Message> = _messages

    /**
     * Adds its [Message] parameter [msg] to the beginning of our [MutableList] of [Message] field
     * [_messages]. It is called by the `onMessageSent` lambda argument of the [UserInput] that is
     * used in [ConversationContent].
     *
     * @param msg the [Message] to add to the beginning of our [MutableList] of [Message] field
     * [_messages].
     */
    fun addMessage(msg: Message) {
        _messages.add(index = 0, element = msg) // Add to the beginning of the list
    }
}

/**
 * This data class is used to hold the information needed to display a single message.
 */
@Immutable
data class Message(
    /**
     * A [String] identifying the author of the message. It is displayed in a [Text] by the
     * [AuthorNameTimestamp] Composable, and used to select which [ProfileScreenState] to display
     * either the [meProfile] for "me" or [colleagueProfile] for any other [String] (lazy huh?)
     */
    val author: String,
    /**
     * The text of the [Message].
     */
    val content: String,
    /**
     * The timestamp of the [Message].
     */
    val timestamp: String,
    /**
     * The resources ID of a drawable that will be displayed after the [content] of the [Message].
     */
    val image: Int? = null,
    /**
     * The resource ID of a drawable to use as the avatar of the author of the [Message]. Notice
     * that there are only two, [R.drawable.ali] for the author "me" and [R.drawable.someone_else]
     * for everybody else.
     */
    val authorImage: Int = if (author == "me") R.drawable.ali else R.drawable.someone_else
)
