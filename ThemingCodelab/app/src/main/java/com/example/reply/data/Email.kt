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

/**
 * A simple data class to represent an Email.
 *
 * @param id Unique ID of this [Email], no two [Email]'s will share the same [id]
 * @param sender The [Account] that the [Email] was sent from.
 * @param recipients The [List] of [Account]'s that the [Email] was sent to.
 * @param subject The subject of the [Email].
 * @param body The contents of the [Email].
 * @param attachments A [List] of [EmailAttachment] attachments to the [Email].
 * @param isImportant [Boolean] flag indicating somebody thought the [Email] important if `true`.
 * @param isStarred The user? has "Starred" the [Email]?
 * @param mailbox The [MailboxType] of the mail box that the [Email] is in, one of [MailboxType.INBOX],
 * [MailboxType.DRAFTS], [MailboxType.SENT], [MailboxType.SPAM], or [MailboxType.TRASH].
 * @param createdAt How long ago was the [Email] created.
 * @param threads A [List] of [Email]'s constituting a "thread" that this [Email] belongs to.
 */
data class Email(
    val id: Long,
    val sender: Account,
    val recipients: List<Account> = emptyList(),
    val subject: String,
    val body: String,
    val attachments: List<EmailAttachment> = emptyList(),
    var isImportant: Boolean = false,
    var isStarred: Boolean = false,
    var mailbox: MailboxType = MailboxType.INBOX,
    val createdAt: String,
    val threads: List<Email> = emptyList()
)
