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

package com.example.jetnews.model

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent

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

/**
 * Data class that holds a single paragraph in a [Post]. [Post.paragraphs] holds a [List] of these
 * in each [Post].
 *
 * @param type the [ParagraphType] of this [Paragraph].  The `Paragraph` Composable (in the file
 * ui/article/PostContent.kt) uses the `ParagraphType.getTextAndParagraphStyle` extension function
 * to construct a `ParagraphStyling` (file ui/article/PostContent.kt) for the [Paragraph], and also
 * uses a `when` to choose which Composable to use to display the [Paragraph] in its [Box].
 * @param text the [String] containing the text of the [Paragraph].
 * @param markups a [List] of [Markup] instances which is interpreted by the `Markup.toAnnotatedStringItem`
 * extension function (file ui/article/PostContent.kt) to construct [AnnotatedString.Range] objects
 * for the portion of the [text] between the [Markup.start] and [Markup.end] that will reflect the
 * [MarkupType] specified by the [Markup.type] field of each [Markup]. These [AnnotatedString.Range]
 * objects are used by the `paragraphToAnnotatedString` method (file ui/article/PostContent.kt) to
 * create an [AnnotatedString] from the [text] of the [Paragraph].
 */
data class Paragraph(
    val type: ParagraphType,
    val text: String,
    val markups: List<Markup> = emptyList()
)

/**
 * Markup to be applied to the [Paragraph.text] string to produce an [AnnotatedString]. A [List] of
 * these is included in the [Paragraph.markups] field of each [Paragraph]. The [Markup]'s are
 * interpreted by the `Markup.toAnnotatedStringItem` extension function (file ui/article/PostContent.kt)
 * to construct [AnnotatedString.Range] objects for the portion of the [Paragraph.text] between the
 * [start] and [end] that will reflect the [MarkupType] specified by the [type] field of each [Markup].
 * These [AnnotatedString.Range] objects are used by the `paragraphToAnnotatedString` method
 * (file ui/article/PostContent.kt) to create an [AnnotatedString] from the [Paragraph.text] of the
 * [Paragraph].
 *
 * @param type the [MarkupType] of the [Markup], one of [MarkupType.Link], [MarkupType.Code],
 * [MarkupType.Italic] or [MarkupType.Bold].
 * @param start The start of the range where the [Markup] takes effect. It's inclusive.
 * @param end The end of the range where [Markup] takes effect. It's exclusive.
 * @param href Not actually used by the code (although there are 37 of them in the example [Post]'s),
 * I assume it was intended for use when links in text are clickable.
 */
data class Markup(
    val type: MarkupType,
    val start: Int,
    val end: Int,
    val href: String? = null
)

/**
 * The type of [Markup] to be applied to a range of text in an [AnnotatedString]. Choices are
 * [MarkupType.Link], [MarkupType.Code], [MarkupType.Italic] or [MarkupType.Bold].
 */
enum class MarkupType {
    /**
     * Uses [TextDecoration.Underline] for the span of text.
     */
    Link,

    /**
     * Uses [FontFamily.Monospace] as the `fontFamily` for the span of text.
     */
    Code,

    /**
     * Uses [FontStyle.Italic] as the `fontStyle` for the span of text.
     */
    Italic,

    /**
     * Uses [FontWeight.Bold] as the `fontWeight` for the span of text.
     */
    Bold,
}

/**
 * The type of the [Paragraph]. Determines how the [Paragraph.text] will be rendered.
 */
enum class ParagraphType {
    /**
     * The title of the [Post], the `ParagraphType.getTextAndParagraphStyle` extension function
     * (file ui/article/PostContent.kt) sets the [TextStyle] to be used to the [Typography.headlineLarge]
     * of [MaterialTheme.typography] ([FontWeight.SemiBold], `fontSize` of 30.sp, and `letterSpacing`
     * of 0.sp, using the `Montserrat` [FontFamily] file ui/theme/Type.kt).
     */
    Title,

    /**
     * The caption of the [Post], the `ParagraphType.getTextAndParagraphStyle` extension function
     * (file ui/article/PostContent.kt) sets the [TextStyle] to be used to the `body1` [TextStyle]
     * of [MaterialTheme.typography] ([FontWeight.Normal], `fontSize` of 16.sp, and `letterSpacing`
     * of 0.5.sp, using the `Domine` [FontFamily] file ui/theme/Type.kt).
     */
    Caption,

    /**
     * The header of a section of the [Post], the `ParagraphType.getTextAndParagraphStyle` extension
     * function (file ui/article/PostContent.kt) sets the [TextStyle] to be used to the `h5` [TextStyle]
     * of [MaterialTheme.typography] ([FontWeight.SemiBold], `fontSize` of 24.sp, and `letterSpacing`
     * of 0sp, using the `Montserrat` [FontFamily] file ui/theme/Type.kt), it also sets the
     * `trailingPadding` that will be used in the `ParagraphStyling` to 16.dp which will be added to
     * the bottom padding of the [Box] that the `Paragraph` Composable renders the text in (file
     * ui/article/PostContent.kt). The `Paragraph` Composable also switches using a when statement
     * and for [ParagraphType.Header] renders the [AnnotatedString] of the text of the [Paragraph]
     * in a [Text] whose `modifier` argument is a `Modifier.padding` of 4.dp to which it adds a
     * `Modifier.semantics` of a `heading` instance of [SemanticsPropertyReceiver].
     */
    Header,

    /**
     * The sub-heading of a section of the [Post], the `ParagraphType.getTextAndParagraphStyle` extension
     * function (file ui/article/PostContent.kt) sets the [TextStyle] to be used to the `h6` [TextStyle]
     * of [MaterialTheme.typography] ([FontWeight.SemiBold], `fontSize` of 20.sp, and `letterSpacing`
     * of 0sp, using the `Montserrat` [FontFamily] file ui/theme/Type.kt), it also sets the
     * `trailingPadding` that will be used in the `ParagraphStyling` to 16.dp which will be added to
     * the bottom padding of the [Box] that the `Paragraph` Composable renders the text in (file
     * ui/article/PostContent.kt).
     */
    Subhead,

    /**
     * The `text` of a [Paragraph], the `ParagraphType.getTextAndParagraphStyle` extension function
     * (file ui/article/PostContent.kt) sets the [TextStyle] to be used to a copy of the `body1`
     * [TextStyle] of [MaterialTheme.typography] whose `lineHeight` is 28.sp ([FontWeight.Normal],
     * `fontSize` of 16.sp, and `letterSpacing` of 0.5.sp, using the `Domine` [FontFamily] (file
     * ui/theme/Type.kt). It also modifies the `paragraphStyle` to have a `lineHeight` of 28.sp
     */
    Text,

    /**
     * A [Paragraph] containing code, the `ParagraphType.getTextAndParagraphStyle` extension function
     * (file ui/article/PostContent.kt) sets the [TextStyle] to be used to a copy of the `body1`
     * [TextStyle] of [MaterialTheme.typography] modifying its `fontFamily` to [FontFamily.Monospace]
     * ([FontWeight.Normal], `fontSize` of 16.sp, and `letterSpacing` of 0.5.sp). The `Paragraph`
     * Composable also switches using a when statement and for [ParagraphType.CodeBlock] renders the
     * [AnnotatedString] of the text of the [Paragraph] in a `CodeBlockParagraph` Composable (file
     * ui/article/PostContent.kt)
     */
    CodeBlock,

    /**
     * Not sure about the use of this [ParagraphType], the `ParagraphType.getTextAndParagraphStyle`
     * extension function (file ui/article/PostContent.kt) sets the [TextStyle] to be used to the
     * `body1` [TextStyle] of [MaterialTheme.typography] ([FontWeight.Normal], `fontSize` of 16.sp,
     * and `letterSpacing` of 0.5.sp, using the `Domine` [FontFamily] file ui/theme/Type.kt).
     */
    Quote,

    /**
     * A "Bullet Point", the `ParagraphType.getTextAndParagraphStyle` extension function (file
     * ui/article/PostContent.kt) sets the `paragraphStyle` to be used to a `ParagraphStyle` with
     * a [TextIndent] for the `firstLine` of 8.sp as its `textIndent` argument. The `Paragraph`
     * Composable also switches using a when statement and for [ParagraphType.Bullet] renders the
     * [AnnotatedString] of the text of the [Paragraph] in a `BulletParagraph` Composable (file
     * ui/article/PostContent.kt)
     */
    Bullet,
}
