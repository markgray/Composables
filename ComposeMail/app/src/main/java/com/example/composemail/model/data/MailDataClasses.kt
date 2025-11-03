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

package com.example.composemail.model.data

import android.net.Uri
import java.time.Instant
import androidx.core.net.toUri

/**
 * A data class that holds a preview of a mail, to be displayed in a list of mails.
 *
 * @param id The unique identifier of the mail.
 * @param from The [Contact] who sent the mail.
 * @param timestamp The [Instant] the mail was sent.
 * @param subject The subject of the mail.
 * @param shortContent A short preview of the mail's content.
 */
data class MailInfoPeek(
    val id: Int,
    val from: Contact,
    val timestamp: Instant,
    val subject: String,
    val shortContent: String
) {
    companion object {
        /**
         * A default [MailInfoPeek] to be used for previews and testing.
         */
        val Default: MailInfoPeek = MailInfoPeek(
            id = -1,
            from = Contact.Default,
            timestamp = Instant.now(),
            subject = "Subject",
            shortContent = "Brief content of mail"
        )
    }
}

/**
 * A data class that holds the full content of a mail.
 *
 * @param id The unique identifier of the mail.
 * @param from The [Contact] who sent the mail.
 * @param to A list of [Contact]s who the mail was sent to.
 * @param timestamp The [Instant] the mail was sent.
 * @param subject The subject of the mail.
 * @param content The full content of the mail.
 * @param previousMailId The id of the previous mail in the thread, if it exists.
 * @param attachments A list of [Attachment]s included in the mail.
 */
data class MailInfoFull(
    val id: Int,
    val from: Contact,
    val to: List<Contact>,
    val timestamp: Instant,
    val subject: String,
    val content: String,
    val previousMailId: Int?,
    val attachments: List<Attachment>
) {

    /**
     * Compares this [MailInfoFull] to another object for equality.
     *
     * Two [MailInfoFull] objects are considered equal if they have the same [id],
     * as IDs are guaranteed to be unique.
     *
     * @param other The object to compare with.
     * @return `true` if the objects are equal, `false` otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MailInfoFull

        return id == other.id
    }

    /**
     * The hash code of a [MailInfoFull] is only it's [id], as it is guaranteed to be unique.
     */
    override fun hashCode(): Int {
        return id
    }

    companion object {
        /**
         * A default [MailInfoFull] to be used for previews and testing.
         */
        val Default: MailInfoFull = MailInfoFull(
            id = -1,
            from = Contact.Default,
            to = listOf(Contact.Me),
            timestamp = Instant.now(),
            subject = "Subject",
            content = "Full mail content.",
            previousMailId = null,
            attachments = listOf(
                Attachment(
                    fileName = "myFile.png",
                    uri = Uri.EMPTY
                )
            )
        )
    }
}

/**
 * A data class representing a file attached to an email.
 *
 * @param fileName The name of the file, including its extension.
 * @param uri The [Uri] pointing to the location of the file.
 */
data class Attachment(
    val fileName: String,
    val uri: Uri
) {
    /**
     * The name of the attached file, without its extension.
     */
    val nameWithoutExtension: String = fileName.substringBefore(".")

    /**
     * The file extension of the attached file.
     */
    val extension: String = fileName.substringAfter(".")
}

/**
 * A data class representing a contact with their name, profile picture, email, and phone number.
 *
 * @param name The full name of the contact.
 * @param profilePic The [Uri] for the contact's profile picture.
 * @param email The contact's email address.
 * @param phone The contact's phone number.
 */
data class Contact(
    val name: String,
    val profilePic: Uri,
    val email: String,
    val phone: String
) {
    companion object {
        /**
         * A default [Contact] to be used for previews and testing.
         */
        val Default: Contact = Contact(
            name = "John Doe",
            profilePic = "android.resource://com.example.composemail/drawable/avatar_1".toUri(),
            email = "johndoe@example.com",
            phone = "123 456 789"
        )

        /**
         * A special [Contact] representing the current user of the application.
         */
        val Me: Contact = Contact(
            name = "Me",
            profilePic = "android.resource://com.example.composemail/drawable/ic_no_profile_pic".toUri(),
            email = "me@example.com",
            phone = "987 654 321"
        )
    }
}