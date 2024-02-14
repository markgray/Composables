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

package com.example.compose.jetchat.data

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.compose.jetchat.R
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.ConversationUiState
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.data.EMOJIS.EMOJI_CLOUDS
import com.example.compose.jetchat.data.EMOJIS.EMOJI_FLAMINGO
import com.example.compose.jetchat.data.EMOJIS.EMOJI_MELTING
import com.example.compose.jetchat.data.EMOJIS.EMOJI_PINK_HEART
import com.example.compose.jetchat.data.EMOJIS.EMOJI_POINTS
import com.example.compose.jetchat.profile.ProfileScreenState

/**
 * This [List] of [Message] that is used to "seed" the [SnapshotStateList] wrapped [MutableList] of
 * [Message] dataset of the [ConversationContent] Composable.
 */
private val initialMessages: List<Message> = listOf(
    Message(
        author = "me",
        content = "Check it out!",
        timestamp = "8:07 PM"
    ),
    Message(
        author = "me",
        content = "Thank you!$EMOJI_PINK_HEART",
        timestamp = "8:06 PM",
        image = R.drawable.sticker
    ),
    Message(
        author = "Taylor Brooks",
        content = "You can use all the same stuff",
        timestamp = "8:05 PM"
    ),
    Message(
        author = "Taylor Brooks",
        content = "@aliconors Take a look at the `Flow.collectAsStateWithLifecycle()` APIs",
        timestamp = "8:05 PM"
    ),
    Message(
        author = "John Glenn",
        content = "Compose newbie as well $EMOJI_FLAMINGO, have you looked at the JetNews sample? " +
            "Most blog posts end up out of date pretty fast but this sample is always up to " +
            "date and deals with async data loading (it's faked but the same idea " +
            "applies) $EMOJI_POINTS https://goo.gle/jetnews",
        timestamp = "8:04 PM"
    ),
    Message(
        author = "me",
        content = "Compose newbie: I’ve scourged the internet for tutorials about async data " +
            "loading but haven’t found any good ones $EMOJI_MELTING $EMOJI_CLOUDS. " +
            "What’s the recommended way to load async data and emit composable widgets?",
        timestamp = "8:03 PM"
    )
)

/**
 * The global singleton [ConversationUiState] which seeds the private [SnapshotStateList] wrapped
 * [MutableList] of [Message] dataset backing its read-only [List] of [Message] field
 * [ConversationUiState.messages] with the [List] of [Message] argument [initialMessages]. It dataset
 * is then updated using its [ConversationUiState.addMessage] method.
 */
val exampleUiState: ConversationUiState = ConversationUiState(
    initialMessages = initialMessages,
    channelName = "#composers",
    channelMembers = 42
)

/**
 * Example colleague profile
 */
val colleagueProfile: ProfileScreenState = ProfileScreenState(
    userId = "12345",
    photo = R.drawable.someone_else,
    name = "Taylor Brooks",
    status = "Away",
    displayName = "taylor",
    position = "Senior Android Dev at Openlane",
    twitter = "twitter.com/taylorbrookscodes",
    timeZone = "12:25 AM local time (Eastern Daylight Time)",
    commonChannels = "2"
)

/**
 * Example "me" profile.
 */
val meProfile: ProfileScreenState = ProfileScreenState(
    userId = "me",
    photo = R.drawable.ali,
    name = "Ali Conors",
    status = "Online",
    displayName = "aliconors",
    position = "Senior Android Dev at Yearin\nGoogle Developer Expert",
    twitter = "twitter.com/aliconors",
    timeZone = "In your timezone",
    commonChannels = null
)

/**
 * Some Emoji constants used in some of the sample [Message]'s.
 */
object EMOJIS {
    /**
     * EMOJI 15
     */
    const val EMOJI_PINK_HEART: String = "\uD83E\uDE77"

    /**
     * EMOJI 14 🫠
     */
    const val EMOJI_MELTING: String = "\uD83E\uDEE0"

    /**
     * ANDROID 13.1 😶‍🌫️
     */
    const val EMOJI_CLOUDS: String = "\uD83D\uDE36\u200D\uD83C\uDF2B️"

    /**
     * ANDROID 12.0 🦩
     */
    const val EMOJI_FLAMINGO: String = "\uD83E\uDDA9"

    /**
     * ANDROID 12.0  👉
     */
    const val EMOJI_POINTS: String = " \uD83D\uDC49"
}
