package com.example.reply.data


/**
 * An enum class to define different types of email folders or categories.
 */
enum class MailboxType {
    /**
     * The [MailboxType] for [Email] which is in our [INBOX] folder.
     */
    INBOX,

    /**
     * The [MailboxType] for [Email] which is in our [DRAFTS] folder.
     */
    DRAFTS,

    /**
     * The [MailboxType] for [Email] which is in our [SENT] folder.
     */
    SENT,

    /**
     * The [MailboxType] for [Email] which is in our [SPAM] folder.
     */
    SPAM,

    /**
     * The [MailboxType] for [Email] which is in our [TRASH] folder.
     */
    TRASH
}
