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
 * TODO: Add kdoc
 */
data class MailInfoPeek(
    /**
     * TODO: Add kdoc
     */
    val id: Int,
    /**
     * TODO: Add kdoc
     */
    val from: Contact,
    /**
     * TODO: Add kdoc
     */
    val timestamp: Instant,
    /**
     * TODO: Add kdoc
     */
    val subject: String,
    /**
     * TODO: Add kdoc
     */
    val shortContent: String
) {
    companion object {
        /**
         * TODO: Add kdoc
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
 * TODO: Add kdoc
 */
data class MailInfoFull(
    /**
     * TODO: Add kdoc
     */
    val id: Int,
    /**
     * TODO: Add kdoc
     */
    val from: Contact,
    /**
     * TODO: Add kdoc
     */
    val to: List<Contact>,
    /**
     * TODO: Add kdoc
     */
    val timestamp: Instant,
    /**
     * TODO: Add kdoc
     */
    val subject: String,
    /**
     * TODO: Add kdoc
     */
    val content: String,
    /**
     * TODO: Add kdoc
     */
    val previousMailId: Int?,
    /**
     * TODO: Add kdoc
     */
    val attachments: List<Attachment>
) {
    // IDs are guaranteed to be unique, no need to use everything else for equals/hash
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MailInfoFull

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    companion object {
        /**
         * TODO: Add kdoc
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
 * TODO: Add kdoc
 */
data class Attachment(
    /**
     * TODO: Add kdoc
     */
    val fileName: String,
    /**
     * TODO: Add kdoc
     */
    val uri: Uri
) {
    /**
     * TODO: Add kdoc
     */
    val nameWithoutExtension: String = fileName.substringBefore(".")

    /**
     * TODO: Add kdoc
     */
    val extension: String = fileName.substringAfter(".")
}

/**
 * TODO: Add kdoc
 */
data class Contact(
    /**
     * TODO: Add kdoc
     */
    val name: String,
    /**
     * TODO: Add kdoc
     */
    val profilePic: Uri,
    /**
     * TODO: Add kdoc
     */
    val email: String,
    /**
     * TODO: Add kdoc
     */
    val phone: String
) {
    companion object {
        /**
         * TODO: Add kdoc
         */
        val Default: Contact = Contact(
            name = "John Doe",
            profilePic = "android.resource://com.example.composemail/drawable/avatar_1".toUri(),
            email = "johndoe@example.com",
            phone = "123 456 789"
        )

        /**
         * TODO: Add kdoc
         */
        val Me: Contact = Contact(
            name = "Me",
            profilePic = "android.resource://com.example.composemail/drawable/ic_no_profile_pic".toUri(),
            email = "me@example.com",
            phone = "987 654 321"
        )
    }
}