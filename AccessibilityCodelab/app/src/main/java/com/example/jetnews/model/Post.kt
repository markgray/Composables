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
import androidx.compose.foundation.lazy.LazyRow

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

/**
 * Contains some "trivia" about the [Post]. Its fields are displayed by the `PostMetadata` Composable
 * (file ui/article/PostContent.kt) which is added as an `item` to its [LazyColumn] by the `PostContent`
 * Composable (file ui/article/PostContent.kt), and also displayed by the `PostCardPopular` Composable
 * (file ui/home/PostCards.kt) which is used for the items in the [LazyRow] of `PostListPopularSection`
 * (file ui/home/HomeScreen.kt).
 *
 * @param author a [PostAuthor] instance naming the author of the [Post] in its [PostAuthor.name]
 * field, and a URL in its [PostAuthor.url] field that links to a page on "medium.com" which has
 * a list of all or the articles that author has posted to "medium.com" (not used by app).
 * @param date the date the [Post] was published.
 * @param readTimeMinutes an estimate of the number of minutes it will take to read the [Post].
 */
data class Metadata(
    val author: PostAuthor,
    val date: String,
    val readTimeMinutes: Int
)

/**
 * Contains the name of the author and a URL that links to a "medium.com" web page listing all of
 * the articles that the author has posted to "medium.com"
 *
 * @param name the name of the author.
 * @param url a URL that links to a "medium.com" web page listing all of the articles that the
 * author has posted to "medium.com"
 */
data class PostAuthor(
    val name: String,
    val url: String? = null
)

/**
 * The name of the "publication" that the [Post] was published to, and a URL to a png which has the
 * logo image of the "publication".
 *
 * @param name the name of the "publication" that the [Post] was published to, always "Android
 * Developers" for our samples (see the `publication` field in file data/posts/PostsData.kt)
 * @param logoUrl a URL to a png which has the logo image of the "publication", always
 * "https://cdn-images-1.medium.com/max/258/1*u7oZc2_5mrkcFaxkXEyfYA@2x.png" for our samples (see
 * the `publication` field in file data/posts/PostsData.kt)
 */
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
