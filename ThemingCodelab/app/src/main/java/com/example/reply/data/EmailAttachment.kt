package com.example.reply.data

import androidx.annotation.DrawableRes


/**
 * An object class to define an attachment to email object.
 *
 * @param resId the resource ID of a drawable representing the attachment.
 * @param contentDesc a description of what the drawable shows.
 */
data class EmailAttachment(
    @DrawableRes val resId: Int,
    val contentDesc: String
)
