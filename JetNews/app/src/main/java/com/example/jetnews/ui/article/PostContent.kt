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

package com.example.jetnews.ui.article

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetnews.R
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.model.Markup
import com.example.jetnews.model.MarkupType
import com.example.jetnews.model.Metadata
import com.example.jetnews.model.Paragraph
import com.example.jetnews.model.ParagraphType
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostAuthor
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.ui.home.HomeUiState

private val defaultSpacerSize = 16.dp

/**
 * Occupies the `content` lambda of the [Scaffold] used by `ArticleScreenContent` to display the
 * [Post] parameter [post]. Our root Composable is a [LazyColumn] whose `contentPadding` argument is
 * our [PaddingValues] parameter `contentPadding`, whose `modifier` argument chains a [Modifier.padding]
 * to our [Modifier] parameter [modifier] which adds [defaultSpacerSize] (16.dp) to both sides, and
 * whose `state` argument is our [LazyListState] parameter [state]. The `content` of the [LazyColumn]
 * is our [LazyListScope] extension function [postContentItems] with its `post` argument our [Post]
 * parameter [post].
 *
 * @param post the [Post] we are supposed to display.
 * @param modifier a [Modifier] instance that our caller can use to alter our appearance and/or
 * behavior. Our caller `ArticleScreenContent` passes us a [Modifier.nestedScroll] whose `connection`
 * is the [TopAppBarScrollBehavior.nestedScrollConnection] of the [TopAppBarDefaults.enterAlwaysScrollBehavior]
 * that if creates from its remembered [TopAppBarState] variable.
 * @param contentPadding this is the [PaddingValues] that the [Scaffold] passes its `content` lambda
 * argument.
 * @param state a [LazyListState] that can be hoisted to control and observe scrolling. It is hoisted
 * all the way up to the call to [ArticleScreen] in [HomeRoute] where it is picked from a [Map] of
 * [String] to [LazyListState] based on the [Post.id] of the [HomeUiState.HasPosts.selectedPost] of
 * the current [HomeUiState].
 */
@Composable
fun PostContent(
    post: Post,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    state: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier.padding(horizontal = defaultSpacerSize),
        state = state,
    ) {
        postContentItems(post = post)
    }
}

/**
 * Called as the `content` of the [LazyColumn] used by [PostContent]. The `content` is a [LazyListScope]
 * so all of our Composable use that [LazyListScope] as their `this`. Our first [LazyListScope.item]
 * adds to the [LazyColumn]:
 *  - a [PostHeaderImage] whose `post` argument is our [Post] parameter [post] which composes an
 *  [Image] which displays the drawable whose resource ID is the [Post.imageId] of [post]
 *  - a [Spacer] whose height is [defaultSpacerSize] (16.dp)
 *  - a [Text] whose `text` argument is the [Post.title] of [post], and whose [TextStyle] `style`
 *  argument is the [Typography.bodyMedium] of our custom [MaterialTheme.typography] (`fontSize` is
 *  14.sp, `lineHeight` is 20.sp, `letterSpacing` is 0.25.sp, and `lineBreak` is [LineBreak.Paragraph]
 *  (a Slower, higher quality line breaking for improved readability)).
 *  - a [Spacer] whose height is 8.dp
 *  - if the [Post.subtitle] of [post] is not `null` then a [Text] whose `text` is the [Post.subtitle]
 *  of [post] and whose [TextStyle] `style` argument is the [Typography.bodyMedium] of our custom
 *  [MaterialTheme.typography], and that is followed by a [Spacer] whose `height` is [defaultSpacerSize]
 *  (16.dp)
 *
 * Next we add a [LazyListScope.item] that composes a [PostMetadata] whose `metadata` argument is the
 * [Post.metadata] of [post], and `modifier` argument is a [Modifier.padding] that adds 24.dp to the
 * bottom of the [PostMetadata]. Finally we add a [LazyListScope.items] whose `items` argument is the
 * [List] of [Paragraph] field [Post.paragraphs] of [post]. In the `itemContent` lambda argument of
 * [LazyListScope.items] we compose a [Paragraph] whose `paragraph` argument is the [Paragraph] that
 * is passed to the lambda each time it loops through the [List] of [Paragraph].
 *
 * @param post the [Post] whose information we are to display.
 */
fun LazyListScope.postContentItems(post: Post) {
    item {
        PostHeaderImage(post = post)
        Spacer(modifier = Modifier.height(height = defaultSpacerSize))
        Text(text = post.title, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(height = 8.dp))
        if (post.subtitle != null) {
            Text(text = post.subtitle, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(height = defaultSpacerSize))
        }
    }
    item { PostMetadata(metadata = post.metadata, modifier = Modifier.padding(bottom = 24.dp)) }
    items(items = post.paragraphs) { Paragraph(paragraph = it) }
}

/**
 * Used to display the drawable whose resource ID is the [Post.imageId] field of our [Post] parameter
 * [post]. We start by initializing our [Modifier] variable `val imageModifier` to a [Modifier.heightIn]
 * that constrains its size to be at least 180.dp, to which is chained a [Modifier.fillMaxWidth] which
 * causes users of `imageModifier` to occupy their entire incoming width constraint, and at the end
 * of the chain is added a [Modifier.clip] which clips the composable using the [Modifier] to the
 * [Shape] of the [Shapes.medium] of our custom [MaterialTheme.shapes] (a [RoundedCornerShape] whose
 * `size` is 4.dp). Then our root composable is an [Image] whose [Painter] is created from the drawable
 * whose resource ID is the [Post.imageId] of [post], whose `modifier` argument is our [Modifier]
 * variable `imageModifier`, and whose `contentScale` argument is [ContentScale.Crop] (scales the
 * source uniformly (maintaining the source's aspect ratio) so that both dimensions (width and
 * height) of the source will be equal to or larger than the corresponding dimension of the
 * destination).
 *
 * @param post the [Post] whose [Post.imageId] is the drawable resource ID of the png that we are to
 * have our [Image] Composable draw.
 */
@Composable
private fun PostHeaderImage(post: Post) {
    val imageModifier: Modifier = Modifier
        .heightIn(min = 180.dp)
        .fillMaxWidth()
        .clip(shape = MaterialTheme.shapes.medium)
    Image(
        painter = painterResource(id = post.imageId),
        contentDescription = null, // decorative
        modifier = imageModifier,
        contentScale = ContentScale.Crop
    )
}

/**
 * Used to display the information in the [Metadata] instance found in the [Post.metadata] property
 * of the [Post] being displayed (our [metadata] parameter). Our root Composable is a [Row] whose
 * `modifier` argument adds a [Modifier.semantics] to our [Modifier] parameter [modifier] with its
 * `mergeDescendants` argument `true` to merge semantics so that accessibility services consider
 * the [Row] a single element. In the [RowScope] `content` lambda argument of [Row] we compose:
 *  - an [Image] whose [ImageVector] argument `imageVector` causes it to render the [ImageVector]
 *  drawn by [Icons.Filled.AccountCircle] (a stylized bust consisting of a white circle above a
 *  white circle segment all in a black circle). The `modifier` argument is a [Modifier.size] that
 *  sets its size to 40.dp, its `colorFilter` argument ([ColorFilter] to apply to the ImageVector
 *  when it is rendered onscreen) is a [ColorFilter.tint] whose `color` is the `current`
 *  [LocalContentColor] (preferred content color for a given position in the hierarchy), and its
 *  `contentScale` argument is [ContentScale.Fit] (scales the source uniformly (maintaining the
 *  source's aspect ratio) so that both dimensions (width and height) of the source will be equal
 *  to or less than the corresponding dimension of the destination).
 *  - a [Spacer] whose `width` is 8.dp
 *  - a [Column] containing:
 *  * a [Text] whose `text` is the [PostAuthor.name] of the [Metadata.author] of our [Metadata]
 *  parameter [metadata], whose [TextStyle] `style` argument is the [Typography.labelLarge] of our
 *  custom [MaterialTheme.typography] `fontSize` = 14.sp, `lineHeight` = 20.sp, `letterSpacing` =
 *  0.1.sp, `fontWeight` = [FontWeight.Medium], and whose `modifier` argument is a [Modifier.padding]
 *  that adds 4.dp to the top of the [Text].
 *  * a [Text] whose `text` is the [String] that the format [String] whose resource ID is
 *  [R.string.article_post_min_read] ("%1$s • %2$d min read") creates when it substitutes the values
 *  of the [Metadata.date] of [metadata] and the [Metadata.readTimeMinutes] of [metadata] for the
 *  format arguments, and the [TextStyle] `style` argument is the [Typography.bodySmall] of our
 *  custom [MaterialTheme.typography] `fontSize` = 12.sp, `lineHeight` = 16.sp, `letterSpacing` = 0.4.sp,
 *  and `lineBreak` = [LineBreak.Paragraph].
 *
 */
@Composable
private fun PostMetadata(
    metadata: Metadata,
    modifier: Modifier = Modifier
) {
    Row(
        // Merge semantics so accessibility services consider this row a single element
        modifier = modifier.semantics(mergeDescendants = true) {}
    ) {
        Image(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null, // decorative
            modifier = Modifier.size(size = 40.dp),
            colorFilter = ColorFilter.tint(color = LocalContentColor.current),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(width = 8.dp))
        Column {
            Text(
                text = metadata.author.name,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = stringResource(
                    id = R.string.article_post_min_read,
                    formatArgs = arrayOf(
                        metadata.date,
                        metadata.readTimeMinutes
                    )
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Used to display the data in its [Paragraph] parameter [paragraph] according to the [ParagraphStyling]
 * that the [ParagraphType.getTextAndParagraphStyle] extension function creates based on the [ParagraphType]
 * specified by its [Paragraph.type] property. We start by initializings our [TextStyle] variable
 * `textStyle`, our [ParagraphStyle] variable `paragraphStyle` and our [Dp] variable `trailingPadding`
 * by Destructuring the [ParagraphStyling] data class returned by the [ParagraphType.getTextAndParagraphStyle]
 * extension function for the [ParagraphType] found in the [Paragraph.type] property of [paragraph].
 * Then we initialize our [AnnotatedString] variable `val annotatedString` to the [AnnotatedString]
 * that the [paragraphToAnnotatedString] method returns when it is passed the arguments:
 *  - `paragraph` our [Paragraph] parameter [paragraph].
 *  - `typography` the [Typography] of our custom [MaterialTheme.typography]
 *  - `codeBlockBackground` the [ColorScheme.codeBlockBackground] of our custom [MaterialTheme.colorScheme]
 *  a copy of the [ColorScheme.onSurface] whose `alpha` is overridden to be .15f
 *
 * Then our root Composable is a [Box] whose `modifier` argument is a [Modifier.padding] that adds
 * our [Dp] variable `trailingPadding` to the bottom of the [Box]. The `content` of the [Box] uses
 * a `when` switch on the [ParagraphType] value of the [Paragraph.type] property of [paragraph]:
 *  - [ParagraphType.Bullet] causes a [BulletParagraph] to be composed with its `text` argument
 *  our [AnnotatedString] variable `annotatedString`, its `textStyle` argument is our [TextStyle]
 *  variable `textStyle` and its `paragraphStyle` argument is our [ParagraphStyle] variable
 *  `paragraphStyle`.
 *  - [ParagraphType.CodeBlock] causes a [CodeBlockParagraph] to be composed with its `text` argument
 *  our [AnnotatedString] variable `annotatedString`, its `textStyle` argument is our [TextStyle]
 *  variable `textStyle` and its `paragraphStyle` argument is our [ParagraphStyle] variable
 *  `paragraphStyle`.
 *  - [ParagraphType.Header] causes a [Text] to be composed whose `modifier` argument is a
 *  [Modifier.padding] that adds 4.dp to all sides of the [Text], with its `text` argument our
 *  [AnnotatedString] variable `annotatedString`, and its [TextStyle] `style` argument is the
 *  [TextStyle] created when the [TextStyle.merge] method of our [TextStyle] variable `textStyle`
 *  creates a new text style that is a combination of `textStyle` and our [ParagraphStyle] variable
 *  `paragraphStyle`,
 *  - `else` causes a [Text] to be composed whose `modifier` argument is a [Modifier.padding] that
 *  adds 4.dp to all sides of the [Text], with its `text` argument our [AnnotatedString] variable
 *  `annotatedString`, and its [TextStyle] `style` argument is our [TextStyle] variable `textStyle`.
 *
 * @param paragraph the [Paragraph] whose [Paragraph.text] we are to display according to its
 * [ParagraphType] property [Paragraph.type] and [List] of [Markup] field [Paragraph.markups].
 */
@Composable
private fun Paragraph(paragraph: Paragraph) {
    val (textStyle: TextStyle, paragraphStyle: ParagraphStyle, trailingPadding: Dp) =
        paragraph.type.getTextAndParagraphStyle()

    val annotatedString: AnnotatedString = paragraphToAnnotatedString(
        paragraph = paragraph,
        typography = MaterialTheme.typography,
        codeBlockBackground = MaterialTheme.colorScheme.codeBlockBackground
    )
    Box(modifier = Modifier.padding(bottom = trailingPadding)) {
        when (paragraph.type) {
            ParagraphType.Bullet -> {
                BulletParagraph(
                    text = annotatedString,
                    textStyle = textStyle,
                    paragraphStyle = paragraphStyle
                )
            }

            ParagraphType.CodeBlock -> {
                CodeBlockParagraph(
                    text = annotatedString,
                    textStyle = textStyle,
                    paragraphStyle = paragraphStyle
                )
            }

            ParagraphType.Header -> {
                Text(
                    modifier = Modifier.padding(all = 4.dp),
                    text = annotatedString,
                    style = textStyle.merge(other = paragraphStyle)
                )
            }

            else -> {
                Text(
                    modifier = Modifier.padding(all = 4.dp),
                    text = annotatedString,
                    style = textStyle
                )
            }
        }
    }
}

/**
 * Used to display the contents of a [Paragraph] whose [ParagraphType] property [Paragraph.type] is
 * [ParagraphType.CodeBlock].
 *
 * @param text the [AnnotatedString] that we are to display in our [Text].
 * @param textStyle the [TextStyle] we are to [TextStyle.merge] with our [ParagraphStyle] parameter
 * [paragraphStyle] to use as the [TextStyle] argument `style` of our [Text].
 * @param paragraphStyle the [ParagraphStyle] we are to [TextStyle.merge] with our [TextStyle]
 * parameter [textStyle] to use as the [TextStyle] argument `style` of our [Text].
 */
@Composable
private fun CodeBlockParagraph(
    text: AnnotatedString,
    textStyle: TextStyle,
    paragraphStyle: ParagraphStyle
) {
    Surface(
        color = MaterialTheme.colorScheme.codeBlockBackground,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(all = 16.dp),
            text = text,
            style = textStyle.merge(other = paragraphStyle)
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
                    .size(width = 8.sp.toDp(), height = 8.sp.toDp())
                    .alignBy {
                        // Add an alignment "baseline" 1sp below the bottom of the circle
                        9.sp.roundToPx()
                    }
                    .background(color = LocalContentColor.current, shape = CircleShape),
            ) { /* no content */ }
        }
        Text(
            modifier = Modifier
                .weight(weight = 1f)
                .alignBy(alignmentLine = FirstBaseline),
            text = text,
            style = textStyle.merge(other = paragraphStyle)
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
    val typography: Typography = MaterialTheme.typography
    var textStyle: TextStyle = typography.bodyLarge
    var paragraphStyle = ParagraphStyle()
    var trailingPadding = 24.dp

    when (this) {
        ParagraphType.Caption -> textStyle = typography.labelMedium
        ParagraphType.Title -> textStyle = typography.headlineLarge
        ParagraphType.Subhead -> {
            textStyle = typography.headlineSmall
            trailingPadding = 16.dp
        }

        ParagraphType.Text -> {
            textStyle = typography.bodyLarge.copy(lineHeight = 28.sp)
        }

        ParagraphType.Header -> {
            textStyle = typography.headlineMedium
            trailingPadding = 16.dp
        }

        ParagraphType.CodeBlock -> textStyle = typography.bodyLarge.copy(
            fontFamily = FontFamily.Monospace
        )

        ParagraphType.Quote -> textStyle = typography.bodyLarge
        ParagraphType.Bullet -> {
            paragraphStyle = ParagraphStyle(textIndent = TextIndent(firstLine = 8.sp))
        }
    }
    return ParagraphStyling(
        textStyle = textStyle,
        paragraphStyle = paragraphStyle,
        trailingPadding = trailingPadding
    )
}

private fun paragraphToAnnotatedString(
    paragraph: Paragraph,
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString {
    val styles: List<AnnotatedString.Range<SpanStyle>> = paragraph.markups
        .map {
            it.toAnnotatedStringItem(
                typography = typography,
                codeBlockBackground = codeBlockBackground
            )
        }
    return AnnotatedString(text = paragraph.text, spanStyles = styles)
}

/**
 * TODO: Add kdoc
 */
fun Markup.toAnnotatedStringItem(
    typography: Typography,
    codeBlockBackground: Color
): AnnotatedString.Range<SpanStyle> {
    return when (this.type) {
        MarkupType.Italic -> {
            AnnotatedString.Range(
                item = typography.bodyLarge.copy(fontStyle = FontStyle.Italic).toSpanStyle(),
                start = start,
                end = end
            )
        }

        MarkupType.Link -> {
            AnnotatedString.Range(
                item = typography.bodyLarge.copy(textDecoration = TextDecoration.Underline)
                    .toSpanStyle(),
                start = start,
                end = end
            )
        }

        MarkupType.Bold -> {
            AnnotatedString.Range(
                item = typography.bodyLarge.copy(fontWeight = FontWeight.Bold).toSpanStyle(),
                start = start,
                end = end
            )
        }

        MarkupType.Code -> {
            AnnotatedString.Range(
                item = typography.bodyLarge
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

private val ColorScheme.codeBlockBackground: Color
    get() = onSurface.copy(alpha = .15f)

/**
 * TODO: Add kdoc
 */
@Preview(name = "Post content")
@Preview(name = "Post content (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPost() {
    JetnewsTheme {
        Surface {
            PostContent(post = post3)
        }
    }
}
