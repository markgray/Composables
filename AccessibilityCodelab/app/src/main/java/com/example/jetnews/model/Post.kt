/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.model

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn

/**
 * Data type that holds the data needed to display a single "Post".
 *
 * @param id a 12 digit hexadecimal number used to uniquely identify the [Post].
 * @param title The title of the [Post].
 * @param subtitle the subtitle of the [Post].
 * @param url a https url string to access the post.
 * @param publication the [Publication] that the post appears in (always uses [Publication.name]
 * of "Android Developers", and the same [Publication.logoUrl] for the png to display)
 * @param metadata the [Metadata] for the [Post] (contains `author`, `date` and `readTimeMinutes`
 * values.
 * @param paragraphs the [List] of [Paragraph] which the `PostContent` Composable displays in its
 * [LazyColumn] (see the file ui/article/PostContent.kt).
 * @param imageId the resource ID of a drawable to draw as a header [Image] at its top when the
 * [Post] is displayed. It is used in the `PostHeaderImage` Composable (file ui/article/PostContent.kt)
 * and in `PostCardPopular` (file ui/home/PostCards.kt)
 * @param imageThumbId the resource ID of a drawable to draw a thumbnail [Image] for the [Post]. It
 * is used in the `PostCardHistory` Composable (file ui/home/PostCards.kt)
 */
data class Post(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val url: String,
    val publication: Publication? = null,
    val metadata: Metadata,
    val paragraphs: List<Paragraph> = emptyList(),
    @DrawableRes val imageId: Int,
    @DrawableRes val imageThumbId: Int
)

data class Metadata(
    val author: PostAuthor,
    val date: String,
    val readTimeMinutes: Int
)

data class PostAuthor(
    val name: String,
    val url: String? = null
)

data class Publication(
    val name: String,
    val logoUrl: String
)

data class Paragraph(
    val type: ParagraphType,
    val text: String,
    val markups: List<Markup> = emptyList()
)

data class Markup(
    val type: MarkupType,
    val start: Int,
    val end: Int,
    val href: String? = null
)

enum class MarkupType {
    Link,
    Code,
    Italic,
    Bold,
}

enum class ParagraphType {
    Title,
    Caption,
    Header,
    Subhead,
    Text,
    CodeBlock,
    Quote,
    Bullet,
}
