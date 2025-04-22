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

package com.example.baselineprofiles_codelab.model

import androidx.annotation.StringRes
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Represents a message with a unique ID and a string resource ID.
 *
 * This data class encapsulates a message's unique identifier and the resource ID
 * corresponding to the message's text content. It is designed to be used with
 * string resources for localization and easy text management.
 *
 * @property id A unique Long identifier for the message.
 * @property messageId The resource ID (e.g., R.string.my_message) of the message string.
 */
data class Message(val id: Long, @StringRes val messageId: Int)

/**
 * [SnackbarManager] is a singleton object responsible for managing the display of snackbar messages.
 *
 * It maintains a list of messages to be displayed as snackbars and provides functions to add new
 * messages and to mark messages as shown, which removes them from the list.
 */
object SnackbarManager {

    /**
     * A mutable state flow that holds the list of messages.
     *
     * This flow emits a new list of [Message] whenever the list of messages is updated.
     * It is initialized with an empty list.
     *
     * This property is private, and its value should be updated via public methods
     * [showMessage] and [setMessageShown] and its value should be read through the
     * [StateFlow] wrapped [List] of [Message] property [messages].
     */
    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    
    /**
     * A [StateFlow] that emits a list of [Message] objects.
     *
     * This property provides a read-only view of the internal [MutableStateFlow] wrapped [List] of
     * [Message] property [_messages]. Changes to the underlying [_messages] list will be reflected
     * in the emissions of this [StateFlow].
     *
     * Subscribers to this [StateFlow] will receive the current list of messages upon subscription
     * and subsequently receive any updates to the list as new emissions.
     *
     * Note that this is a read-only view; modifications to the message list should be done through
     * the [showMessage] and [setMessageShown] methods that update the internal [_messages]
     * MutableStateFlow.
     */
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    /**
     * Displays a message to the user.
     *
     * This function adds a new message to the list of messages that will be
     * displayed to the user. The message is identified by a string resource ID.
     * Each message is assigned a random unique ID for tracking purposes.
     *
     * @param messageTextId The string resource ID of the message to display. This should be a
     * valid resource ID from your project's string resources (e.g., R.string.my_message).
     */
    fun showMessage(@StringRes messageTextId: Int) {
        _messages.update { currentMessages ->
            currentMessages + Message(
                id = UUID.randomUUID().mostSignificantBits,
                messageId = messageTextId
            )
        }
    }

    /**
     * Removes a message from the list of messages, effectively marking it as "shown".
     *
     * This function updates the internal list of messages ([_messages]) by filtering out the
     * message whose [Message.id] is our [Long] parameter [messageId]. It assumes that a message
     * with a specific ID is considered "shown" or processed when it's removed from the list.
     *
     * @param messageId The unique identifier of the message to be marked as shown (removed).
     */
    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}
