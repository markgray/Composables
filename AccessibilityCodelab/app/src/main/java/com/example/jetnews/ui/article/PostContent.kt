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

package com.example.jetnews.ui.article

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.model.Markup
import com.example.jetnews.model.MarkupType
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Paragraph
import com.example.jetnews.model.ParagraphType
import com.example.jetnews.model.Post
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.theme.JetnewsTypography

/**
 * The default spacer size that is used by several [Spacer] Composables
 */
private val defaultSpacerSize = 16.dp

/**
 * Displays the contents of a [Post]. Our root Composable is a [LazyColumn] whose `modifier` argument
 * adds a [Modifier.padding] to our [modifier] parameter whose `horizontal` (space along the left and
 * right edges) argument is [defaultSpacerSize] (16.dp). The `content` of the [LazyColumn] is:
 *  - an `item` which contains a [Spacer] whose `modifier` argument uses a [Modifier.height] of
 *  [defaultSpacerSize] set to its height to 16.dp, followed by a [PostHeaderImage] Composable whose
 *  `post` argument is our [Post] parameter [post].
 *  - an `item` which contains a [Text] displaying the [Post.title] of our [Post] parameter [post]
 *  using as its `style` the `h4` [TextStyle] of [MaterialTheme.typography] (which is defined in
 *  our [JetnewsTypography] to be [FontFamily] `Montserrat` with `fontWeight` [FontWeight.SemiBold],
 *  `fontSize` of 30.sp, and `letterSpacing` of 0.sp), below this in the `item` is a [Spacer] whose
 *  `modifier` argument uses a [Modifier.height] of 8.dp set to its height.
 *  - If the [Post.subtitle] field of our parameter [post] is not `null` we compose an `item` which
 *  contains a [CompositionLocalProvider] which has [LocalContentAlpha] (the `CompositionLocal` of
 *  the preferred content alpha for a given position in the hierarchy) provide [ContentAlpha.medium]
 *  (A medium level of content alpha, used to represent medium emphasis text such as placeholder text
 *  in a TextField) to material design widgets when ask for the [LocalContentAlpha]. The `content`
 *  of the [CompositionLocalProvider] is a [Text] displaying the [Post.subtitle] of [post] (passed
 *  as `subtitle` to the lambda of the [let] extension function), using `body2` [TextStyle] of
 *  [MaterialTheme.typography] (which is defined in our [JetnewsTypography] to be [FontFamily]
 *  `Montserrat` with `fontWeight` [FontWeight.Medium], `fontSize` of 14.sp, and `letterSpacing` of
 *  0.25.sp)), and the `lineHeight` argument of the [Text] is 20.sp
 */
@Composable
fun PostContent(post: Post, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(horizontal = defaultSpacerSize)
    ) {
        item {
            Spacer(modifier = Modifier.height(defaultSpacerSize))
            PostHeaderImage(post = post)
        }
        item {
            Text(text = post.title, style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(8.dp))
        }
        post.subtitle?.let { subtitle ->
            item {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.body2,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(defaultSpacerSize))
            }
        }
        item {
            PostMetadata(metadata = post.metadata)
            Spacer(modifier = Modifier.height(24.dp))
        }
        items(post.paragraphs) {
            Paragraph(paragraph = it)
        }
        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun PostHeaderImage(post: Post) {
    val imageModifier = Modifier
        .heightIn(min = 180.dp)
        .fillMaxWidth()
        .clip(shape = MaterialTheme.shapes.medium)
    Image(
        painter = painterResource(post.imageId),
        contentDescription = null,
        modifier = imageModifier,
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.height(defaultSpacerSize))
}

@Composable
private fun PostMetadata(metadata: Metadata) {
    val typography = MaterialTheme.typography
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Image(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = metadata.author.name,
                style = typography.caption,
                modifier = Modifier.padding(top = 4.dp)
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "${metadata.date} • ${metadata.readTimeMinutes} min read",
                    style = typography.caption
                )
            }
        }
    }
}

@Composable
private fun Paragraph(paragraph: Paragraph) {
    val (textStyle, paragraphStyle, trailingPadding) = paragraph.type.getTextAndParagraphStyle()

    val annotatedString: AnnotatedString = paragraphToAnnotatedString(
        paragraph,
        MaterialTheme.typography,
        MaterialTheme.colors.codeBlockBackground
    )
    Box(modifier = Modifier.padding(bottom = trailingPadding)) {
        when (paragraph.type) {
            ParagraphType.Bullet -> BulletParagraph(
                text = annotatedString,
                textStyle = textStyle,
                paragraphStyle = paragraphStyle
            )
            ParagraphType.CodeBlock -> CodeBlockParagraph(
                text = annotatedString,
                textStyle = textStyle,
                paragraphStyle = paragraphStyle
            )
            ParagraphType.Header -> {
                Text(
                    modifier = Modifier.padding(4.dp)
                        .semantics { heading() },
                    text = annotatedString,
                    style = textStyle.merge(paragraphStyle)
                )
            }
            else -> Text(
                modifier = Modifier.padding(4.dp),
                text = annotatedString,
                style = textStyle
            )
        }
    }
}

@Composable
private fun CodeBlockParagraph(
    text: AnnotatedString,
    textStyle: TextStyle,
    paragraphStyle: ParagraphStyle
) {
    Surface(
        color = MaterialTheme.colors.codeBlockBackground,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }
}

@Composable
private fun BulletParagraph(
    text: AnnotatedString,
    textStyle: TextStyle,
    paragraphStyle: ParagraphStyle
) {
    Row {
        with(LocalDensity.current) {
            // this box is acting as a character, so it's sized with font scaling (sp)
            Box(
                modifier = Modifier
                    .size(8.sp.toDp(), 8.sp.toDp())
                    .alignBy {
                        // Add an alignment "baseline" 1sp below the bottom of the circle
                        9.sp.roundToPx()
                    }
                    .background(LocalContentColor.current, CircleShape),
            ) { /* no content */ }
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .alignBy(FirstBaseline),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }
}

private data class ParagraphStyling(
    val textStyle: TextStyle,
    val paragraphStyle: ParagraphStyle,
    val trailingPadding: Dp
)

@Composable
private fun ParagraphType.getTextAndParagraphStyle(): ParagraphStyling {
    val typography = MaterialTheme.typography
    var textStyle: TextStyle = typography.body1
    var paragraphStyle = ParagraphStyle()
    var trailingPadding = 24.dp

    when (this) {
        ParagraphType.Caption -> textStyle = typography.body1
        ParagraphType.Title -> textStyle = typography.h4
        ParagraphType.Subhead -> {
            textStyle = typography.h6
            trailingPadding = 16.dp
        }
        ParagraphType.Text -> {
            textStyle = typography.body1.copy(lineHeight = 28.sp)
            paragraphStyle = paragraphStyle.copy(lineHeight = 28.sp)
        }
        ParagraphType.Header -> {
            textStyle = typography.h5
            trailingPadding = 16.dp
        }
        ParagraphType.CodeBlock -> textStyle = typography.body1.copy(
            fontFamily = FontFamily.Monospace
        )
        ParagraphType.Quote -> textStyle = typography.body1
        ParagraphType.Bullet -> {
            paragraphStyle = ParagraphStyle(textIndent = TextIndent(firstLine = 8.sp))
        }
    }
    return ParagraphStyling(
        textStyle,
        paragraphStyle,
        trailingPadding
    )
}

private fun paragraphToAnnotatedString(
    paragraph: Paragraph,
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString {
    val styles: List<AnnotatedString.Range<SpanStyle>> = paragraph.markups
        .map { it.toAnnotatedStringItem(typography, codeBlockBackground) }
    return AnnotatedString(text = paragraph.text, spanStyles = styles)
}

/**
 * Extension function of a [Markup] which interprets it and produces an [AnnotatedString.Range] based
 * on its [Markup.type] that applies for the range [Markup.start] to [Markup.end].
 */
fun Markup.toAnnotatedStringItem(
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString.Range<SpanStyle> {
    return when (this.type) {
        MarkupType.Italic -> {
            AnnotatedString.Range(
                item = typography.body1.copy(fontStyle = FontStyle.Italic).toSpanStyle(),
                start = start,
                end = end
            )
        }
        MarkupType.Link -> {
            AnnotatedString.Range(
                item = typography.body1.copy(textDecoration = TextDecoration.Underline).toSpanStyle(),
                start = start,
                end = end
            )
        }
        MarkupType.Bold -> {
            AnnotatedString.Range(
                item = typography.body1.copy(fontWeight = FontWeight.Bold).toSpanStyle(),
                start = start,
                end = end
            )
        }
        MarkupType.Code -> {
            AnnotatedString.Range(
                item = typography.body1
                    .copy(
                        background = codeBlockBackground,
                        fontFamily = FontFamily.Monospace
                    ).toSpanStyle(),
                start = start,
                end = end
            )
        }
    }
}

private val Colors.codeBlockBackground: Color
    get() = onSurface.copy(alpha = .15f)

/**
 * Two Previews of our [PostContent] Composable displaying the [post3] sample [Post] in a [Surface]
 * that is wrapped by our [JetnewsTheme] custom [MaterialTheme]. The first is named "Post content"
 * and uses the default `LightThemeColors`, and the second is named "Post content (dark)" and uses
 * [UI_MODE_NIGHT_YES] as the `uiMode` argument of Preview so that the `DarkThemeColors` are used.
 */
@Preview("Post content")
@Preview("Post content (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPost() {
    JetnewsTheme {
        Surface {
            PostContent(post = post3)
        }
    }
}
