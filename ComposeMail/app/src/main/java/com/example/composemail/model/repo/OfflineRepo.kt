/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("KotlinConstantConditions")

package com.example.composemail.model.repo

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.example.composemail.R
import com.example.composemail.model.data.Attachment
import com.example.composemail.model.data.Contact
import com.example.composemail.model.data.MailInfoFull
import com.example.composemail.model.data.MailInfoPeek
import kotlinx.coroutines.delay
import java.time.Instant

/**
 * Tag used for logging.
 */
private const val TAG = "OfflineRepo"

/**
 * A fake delay to simulate network calls.
 */
private const val DELAY_PER_MAIL_MS = 100L

/**
 * A list of names used to randomly generate contact information for fake emails.
 */
private val names = listOf(
    "Jacob",
    "Sophia",
    "Noah",
    "Emma",
    "Mason",
    "Isabella",
    "William",
    "Olivia",
    "Ethan",
    "Ava",
    "Liam",
    "Emily",
    "Michael",
    "Abigail",
    "Alexander",
    "Mia",
    "Jayden",
    "Madison",
    "Daniel",
)

/**
 * A list of file extensions used to randomly generate attachments for fake emails.
 */
private val fileExtensions = listOf(
    "png",
    "mp3",
    "mp4",
    "pdf"
)

/**
 * A list of words from a Lorem Ipsum passage, used to generate random email content.
 * The passage is deconstructed into individual words, and line breaks are removed.
 */
private val loremIpsumWords = LoremIpsum(100)
    .values
    .first()
    .filter { it != '\n' }
    .split(" ")

/**
 * An implementation of [MailRepository] that generates random fake data.
 *
 * This class is intended for use in development and preview environments where
 * network access is not available or desired. It creates a collection of procedurally
 * generated emails to populate the UI.
 *
 * @param resources An instance of [Resources] used to access drawable assets for profile pictures.
 */
class OfflineRepository(
    private val resources: Resources
) : MailRepository {
    /**
     * A cache of the full mail information ([MailInfoFull]) for all generated emails.
     * The key is the mail's unique ID, and the value is the [MailInfoFull] object.
     * This acts as an in-memory database to simulate retrieving the full details of an email
     * when it's selected from the list.
     */
    private val loadedMails: MutableMap<Int, MailInfoFull> = mutableMapOf()

    /**
     * Tracks whether this is the first time a request for conversations is being made.
     * This can be used to simulate different behaviors on the initial load versus subsequent
     * "infinite scroll" loads.
     */
    private var isFirstRequest = true

    /**
     * A unique identifier for the next mail to be generated. This is incremented each time a new
     * mail is created to ensure that every mail has a distinct ID.
     */
    private var currentId = 0

    /**
     * The timestamp of the last generated email, stored as seconds from the epoch.
     * This is used as a reference point to generate progressively older timestamps for
     * subsequent emails, ensuring they appear in a chronologically descending order.
     */
    private var lastTime = Instant.now().epochSecond

    /**
     * A list of drawable resource IDs for sample avatar images.
     * These are used to randomly assign a profile picture to generated contacts.
     */
    private val samplePictures: List<Int> =
        listOf(
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6,
            R.drawable.avatar_7,
            R.drawable.avatar_8,
            R.drawable.avatar_9,
            R.drawable.avatar_10,
            R.drawable.avatar_11,
            R.drawable.avatar_12,
            R.drawable.avatar_13,
            R.drawable.avatar_14,
            R.drawable.avatar_15,
            R.drawable.avatar_16,
        )

    /**
     * Simulates establishing a connection to a mail server.
     *
     * In a real-world scenario, this would handle tasks like authentication,
     * token verification, or session initialization. In this offline implementation,
     * it is a no-op.
     */
    override suspend fun connect() {
        // Consider it something similar as to establishing a connection with a Mail API, where you
        // might need to authenticate or verify tokens, start a session, etc.
        // ("Not yet implemented")
    }

    /**
     * A counter to keep track of the current "page" of conversations being fetched.
     * This simulates pagination in an API, where each request for more data
     * corresponds to fetching the next page. It's incremented with each call to
     * [getNextSetOfConversations].
     */
    private var pageCounter = 0

    /**
     * Retrieves the next "page" of conversation previews.
     *
     * This function simulates fetching a batch of emails from a remote server. It generates a
     * specified [amount] of new, random emails, adds them to the in-memory cache, and returns
     * a list of their previews ([MailInfoPeek]). A synthetic delay is introduced for each
     * generated mail to mimic network latency.
     *
     * This is designed to support features like infinite scrolling in a UI.
     *
     * @param amount The number of conversations to generate and return.
     * @return A [MailConversationsResponse] containing a list of [MailInfoPeek] objects and the
     * current page number.
     */
    override suspend fun getNextSetOfConversations(amount: Int): MailConversationsResponse {
        val conversations = ArrayList<MailInfoPeek>(amount)

        for (i in 0..amount) {
            val newMail = createNewMailWithThread()
            loadedMails[newMail.id] = newMail
            conversations.add(i, newMail.toMailInfoPeek())
            delay(DELAY_PER_MAIL_MS)
        }
        isFirstRequest = false
        return MailConversationsResponse(
            conversations,
            pageCounter++
        )
    }

    /**
     * Retrieves the full details of a specific email by its unique ID.
     *
     * This function simulates fetching an email from a local cache. If the email with the given
     * [id] is found in the [loadedMails] map, it is returned. Otherwise, a warning is logged
     * and a default, empty [MailInfoFull] object is returned. This method mimics the behavior
     * of retrieving detailed information for an email that has been selected from a list.
     *
     * @param id The unique identifier of the mail to retrieve.
     * @return The [MailInfoFull] object corresponding to the given [id], or a default
     * [MailInfoFull] object if not found. The return type is nullable to match the
     * interface, but this implementation never returns null.
     */
    @Suppress("RedundantNullableReturnType") // Inherited nullability
    override suspend fun getFullMail(id: Int): MailInfoFull? {
        // Add delay?
        return loadedMails[id] ?: kotlin.run {
            Log.w(TAG, "findMail: no mails with id = $id")
            MailInfoFull.Default
        }
    }

    /**
     * Generates a new, chronologically descending timestamp for a fake email.
     *
     * This function ensures that each new email appears older than the previous one. It
     * subtracts a random duration (between 30 minutes and 4 hours) from the timestamp of
     * the last generated email.
     *
     * @return An [Instant] representing the new, older timestamp.
     */
    private fun createNewTimestamp(): Instant {
        val range = IntRange(start = 1800, endInclusive = 3600 * 4)
        lastTime -= range.random()
        return Instant.ofEpochSecond(lastTime)
    }

    /**
     * Creates a new, randomly generated [MailInfoFull] object.
     *
     * This function is responsible for procedurally generating a complete email, including
     * a unique ID, a random sender, attachments, subject, and content. It also assigns
     * a chronologically descending timestamp to simulate a real inbox.
     *
     * The `previousMailId` is currently a placeholder and does not implement threading logic.
     *
     * @return A fully populated [MailInfoFull] object representing a new email.
     */
    private fun createNewMailWithThread(): MailInfoFull {
        val previousMailId: Int? = if (loadedMails.size > 1) {
            null // Add a logic to create Threads between Mails
        } else {
            null
        }
        val name = names.random()
        val attachments = mutableListOf<Attachment>()
        for (i in 0 until IntRange(0, 4).random()) {
            attachments.add(
                Attachment(
                    fileName = "myFile" + (i + 1) + "." + fileExtensions.random(),
                    uri = Uri.EMPTY
                )
            )
        }
        return MailInfoFull(
            id = currentId++,
            from = Contact(
                name = "$name Smith",
                profilePic = randomSampleImageUri(),
                email = "$name@smith.com",
                phone = "123 456 789"
            ),
            to = listOf(Contact.Me),
            timestamp = createNewTimestamp(),
            subject = "Mail Subject",
            content = generateRandomContent(),
            previousMailId = previousMailId,
            attachments = attachments
        )
    }

    /**
     * Generates a random block of "Lorem Ipsum" text to be used as email content.
     *
     * It selects a random number of words (between 10 and 200) from the pre-defined
     * [loremIpsumWords] list and joins them into a single string.
     *
     * @return A [String] containing the randomly generated content.
     */
    private fun generateRandomContent(): String {
        // 10 to 200 words
        val wordCount = IntRange(
            start = 10,
            endInclusive = (200).coerceAtMost(maximumValue = loremIpsumWords.size)
        ).random()

        // Pick a random offset from available words
        val wordOffset =
            IntRange(start = 0, endInclusive = loremIpsumWords.size - wordCount).random()

        // Rebuild it into a continuous String
        return loremIpsumWords
            .subList(fromIndex = wordOffset, toIndex = wordOffset + wordCount)
            .joinToString(separator = " ")
    }

    /**
     * Generates a `Uri` for a random sample avatar image from the app's drawable resources.
     *
     * This function selects a random resource ID from the [samplePictures] list and constructs a
     * `content://` style `Uri` that points to that drawable. This `Uri` can then be used by UI
     * components like Coil or Glide to load the image.
     *
     * @return A [Uri] pointing to a randomly selected drawable resource.
     */
    private fun randomSampleImageUri(): Uri {
        val pictureId = samplePictures.random()
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(pictureId))
            .appendPath(resources.getResourceTypeName(pictureId))
            .appendPath(resources.getResourceEntryName(pictureId))
            .build()
    }

    /**
     * Converts a [MailInfoFull] object into a [MailInfoPeek] object.
     *
     * This is an extension function that simplifies the creation of a mail preview from a full
     * mail object. It copies the essential fields (`id`, `from`, `timestamp`, `subject`) and
     * generates a `shortContent` by taking the first 20 words of the full `content`.
     *
     * @return A new [MailInfoPeek] instance.
     */
    private fun MailInfoFull.toMailInfoPeek(): MailInfoPeek =
        MailInfoPeek(
            id = this.id,
            from = this.from,
            timestamp = this.timestamp,
            subject = this.subject,
            // Shorten to up to 20 words
            shortContent = this.content.split(" ").take(20).joinToString(" ")
        )
}