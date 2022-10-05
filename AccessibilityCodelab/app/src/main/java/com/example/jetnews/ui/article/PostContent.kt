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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
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
import com.example.jetnews.ui.theme.JetnewsTypography
import com.example.jetnews.utils.supportWideScreen

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
 *  - an `item` which contains a [PostMetadata] Composable whose `metadata` argument is the [Metadata]
 *  in the [Post.metadata] field of [post], and this [PostMetadata] is followed by a [Spacer] whose
 *  `modifier` argument specifies a height of [defaultSpacerSize] (16.dp)
 *  - an `items` whose `items` argument is the [Post.paragraphs] list of [Paragraph] field of [post].
 *  The `itemContent` is a [Paragraph] Composable for every [Paragraph] in the [Post.paragraphs] list.
 *  - at the bottom of the [LazyColumn] is a [Spacer] whose `modifier` argument specifies a height
 *  of 48.dp
 *
 * @param post the [Post] whose information we are to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller ([ArticleScreen]) calls us with a [Modifier.padding] of the [PaddingValues]
 * passed as the parameter of the `content` lambda of the [Scaffold] of [ArticleScreen] to which it
 * adds a [Modifier.supportWideScreen] (file utils/Modifiers.kt) which supports wide screen by making
 * the content width max 840dp, centered horizontally using a [Modifier.fillMaxWidth] to which is
 * added a [Modifier.wrapContentWidth] whose `align` argument is [Alignment.CenterHorizontally] to
 * which is added a [Modifier.widthIn] whose `max` argument is 840.dp
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
        items(items = post.paragraphs) {
            Paragraph(paragraph = it)
        }
        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

/**
 * Used to display in an [Image] the drawable whose resource ID is in the [Post.imageId] field of
 * our [Post] parameter [post]. We start by initializing our [Modifier] variable `val imageModifier`
 * to a [Modifier.heightIn] whose `min` argument is 180.dp (constrains the height of the content to
 * be at least 180.dp) to which is added a [Modifier.fillMaxWidth] to have it occupy the entire width
 * allowed it by the incoming constraints, to which is added a [Modifier.clip] whose `shape` argument
 * is the `medium` [Shape] of [MaterialTheme.shapes] (which is a [RoundedCornerShape] of 4.dp in our
 * [JetnewsTheme] custom [MaterialTheme]) and this will Clip the content to that shape.
 *
 * Our Composables consist of an [Image] whose `painter` argument is the [Painter] that the method
 * [painterResource] returns for the resource ID in the [Post.imageId] field of our [Post] parameter
 * [post], whose `contentDescription` argument is `null`, whose `modifier` argument is our [Modifier]
 * variable `imageModifier`, and whose `contentScale` argument is [ContentScale.Crop] (which will
 * Scale the source uniformly, maintaining the source's aspect ratio). Below the [Image] is a
 * [Spacer] whose `modifier` argument uses a [Modifier.height] of [defaultSpacerSize] to set its
 * height to 16.dp
 *
 * @param post the [Post] whose [Post.imageId] drawable resource ID we are supposed to display in an
 * [Image].
 */
@Composable
private fun PostHeaderImage(post: Post) {
    val imageModifier: Modifier = Modifier
        .heightIn(min = 180.dp)
        .fillMaxWidth()
        .clip(shape = MaterialTheme.shapes.medium)
    Image(
        painter = painterResource(id = post.imageId),
        contentDescription = null,
        modifier = imageModifier,
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.height(defaultSpacerSize))
}

/**
 * This Composable displays the information found in its [Metadata] parameter [metadata]. Every
 * [Post] in our fake dataset holds an instance of [Metadata] in its [Post.metadata] field. First
 * we initialize our [Typography] variable `val typography` to [MaterialTheme.typography] (the
 * current [Typography] at the call site's position in the hierarchy). We then use a [Row] Composable
 * as our root Composable whose `modifier` argument is a [Modifier.semantics] whose `mergeDescendants`
 * argument is `true` (this adds the semantic key/value pair to the layout node for use in testing,
 * accessibility, etc. With `mergeDescendants` equal to `true` semantic information provided by the
 * owning component and its descendants will be treated as one logical entity). The content of the
 * [Row] is:
 *  - an [Image] displaying the `imageVector` [Icons.Filled.AccountCircle] (this is a stylized line
 *  drawing a head and shoulders, white on black, with a circle for the head and a irregular ovoid
 *  for the shoulders), the `contentDescription` argument is `null`, the `modifier` argument is a
 *  [Modifier.size] of 40.dp to set its size, the `colorFilter` argument is a [ColorFilter.tint] of
 *  the `current` [LocalContentColor] `color` (this sets the [ColorFilter] to apply for the [ImageVector]
 *  when it is rendered onscreen to one which uses the current `onPrimary` color using the [ColorFilter.tint]
 *  default [BlendMode.SrcIn] as the [BlendMode] used to blend source content which shows the source
 *  image, but only where the two images overlap), and the `contentScale` argument is [ContentScale.Fit]
 *  which scales the source uniformly maintaining the source's aspect ratio.
 *  - a [Spacer] whose [Modifier.width] `modifier` argument sets its width to 8.dp
 *  - a [Column] which holds a [Text] displaying the [PostAuthor.name] of the [Metadata.author] field
 *  of our parameter [metadata] using the `caption` [TextStyle] of `typography` (which is the [FontFamily]
 *  Montserrat, with `fontWeight` [FontWeight.Medium], `fontSize` of 12.sp, and `letterSpacing` 0.4.sp
 *  which is the [Font] with resource ID [R.font.montserrat_medium]), and the `modifier` argument of
 *  the [Text] is a [Modifier.padding] that adds 4.dp padding to the `top` of the [Text]. Below this
 *  is a [Text] wrapped in a [CompositionLocalProvider] which has its `content` use [ContentAlpha.medium]
 *  for its [LocalContentAlpha] (a medium level of content alpha, used to represent medium emphasis
 *  text such as placeholder text in a TextField). The [Text] `content` of the [CompositionLocalProvider]
 *  displays the [Metadata.date] and [Metadata.readTimeMinutes] fields of [metadata] as its `text`
 *  also using the `caption` [TextStyle] of `typography`.
 *
 * @param metadata the [Metadata] whose information we are supposed to display.
 */
@Composable
private fun PostMetadata(metadata: Metadata) {
    val typography: Typography = MaterialTheme.typography
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Image(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(size = 40.dp),
            colorFilter = ColorFilter.tint(color = LocalContentColor.current),
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

/**
 * This Composable converts the contents of its [Paragraph] parameter [paragraph] to an [AnnotatedString]
 * and displays it. First we destructure the [ParagraphStyling] object returned by the extension function
 * [ParagraphType.getTextAndParagraphStyle] when applied to the [ParagraphType] in the [Paragraph.type]
 * field of [paragraph] resulting in the variables:
 *  - `textStyle` the [TextStyle] that is appropriate for the [ParagraphType]
 *  - `paragraphStyle` the [ParagraphStyle] that is appropriate for the [ParagraphType]
 *  - `trailingPadding` the padding that should be added to the `bottom` of the [Box] which holds
 *  the rendering of the [Paragraph].
 *
 * Next we initialize our [AnnotatedString] variable `val annotatedString` to the [AnnotatedString]
 * that the [paragraphToAnnotatedString] creates when called with our [Paragraph] parameter [paragraph]
 * as its `paragraph` argument, [MaterialTheme.typography] as its `typography` argument, and the
 * `codeBlockBackground` [Color] of [MaterialTheme.colors] (which is a copy of `onSurface` with an
 * `alpha` of 0.15f, `onSurface` is the default [Color.Black] since our [JetnewsTheme] does not
 * specify it). The root Composable of this Composable is a [Box] whose `modifier` is a
 * [Modifier.padding] that set its `bottom` padding to `trailingPadding`. The `content` of the [Box]
 * depends on the [ParagraphType] that is in the [Paragraph.type] field of [paragraph]:
 *  - [ParagraphType.Bullet] causes a [BulletParagraph] to be displayed whose `text` argument is
 *  the [AnnotatedString] in `annotatedString`, whose `textStyle` argument is the [TextStyle] in
 *  `textStyle` and whose `paragraphStyle` argument is the [ParagraphStyle] in `paragraphStyle`.
 *  - [ParagraphType.CodeBlock] causes a [CodeBlockParagraph] to be displayed whose `text` argument
 *  is the [AnnotatedString] in `annotatedString`, whose `textStyle` argument is the [TextStyle] in
 *  `textStyle` and whose `paragraphStyle` argument is the [ParagraphStyle] in `paragraphStyle`.
 *  - [ParagraphType.Header] causes a [Text] to be displayed whose `modifier` argument is a
 *  [Modifier.padding] of 4.dp for all sides of the contents to which is chained a [Modifier.semantics]
 *  of [heading] to mark the node as a "heading" for accessibility. Its `text` argument is the
 *  [AnnotatedString] in `annotatedString`, and its `style` is the [TextStyle] `textStyle` to which
 *  [TextStyle.merge] is used to create a new text style that is a combination of `textStyle` and
 *  `paragraphStyle`.
 *  - For all other [ParagraphType] a [Text] is displayed whose `modifier` argument is a
 *  [Modifier.padding] of 4.dp for all sides of the contents, whose `text` argument is the
 *  [AnnotatedString] in `annotatedString`, and whose `style` is the [TextStyle] `textStyle`
 *
 * @param paragraph the [Paragraph] whose information we are to display.
 */
@Composable
private fun Paragraph(paragraph: Paragraph) {
    val (
        textStyle: TextStyle,
        paragraphStyle: ParagraphStyle,
        trailingPadding: Dp
    ) = paragraph.type.getTextAndParagraphStyle()

    val annotatedString: AnnotatedString = paragraphToAnnotatedString(
        paragraph = paragraph,
        typography = MaterialTheme.typography,
        codeBlockBackground = MaterialTheme.colors.codeBlockBackground
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
                    modifier = Modifier.padding(all = 4.dp)
                        .semantics { heading() },
                    text = annotatedString,
                    style = textStyle.merge(paragraphStyle)
                )
            }
            else -> Text(
                modifier = Modifier.padding(all = 4.dp),
                text = annotatedString,
                style = textStyle
            )
        }
    }
}

/**
 * This Composable is called to display a [Paragraph] whose [ParagraphType] field [Paragraph.type]
 * is [ParagraphType.CodeBlock]. Our root Composable is a [Surface] whose `color` argument sets its
 * background color to the `codeBlockBackground` [Color] of [MaterialTheme.colors] (which is a copy
 * of `onSurface` with an `alpha` of 0.15f, `onSurface` is the default [Color.Black] since our
 * [JetnewsTheme] does not specify it), whose `shape` argument is the `small` [Shape] of
 * [MaterialTheme.shapes] which is a [RoundedCornerShape] of 4.dp in our [JetnewsTheme], and whose
 * `modifier` argument is a [Modifier.fillMaxWidth] to have its `content` fill the maximum incoming
 * measurement constraints. It `content` Composable is a [Text] whose `modifier` argument is a
 * [Modifier.padding] that adds 16.dp to all sides of the contents of the [Text], whose `text` is
 * the [AnnotatedString] in our parameter [text], and whose `style` is our [TextStyle] parameter
 * `textStyle` to which [TextStyle.merge] is used to create a new text style that is a combination of
 * `textStyle` and our [ParagraphStyle] parameter `paragraphStyle`.
 *
 * @param text the [AnnotatedString] that we should display in our [Text]
 * @param textStyle the [TextStyle] that our [Text] should use to display our [text] parameter.
 * @param paragraphStyle the [ParagraphStyle] that our [Text] should use to display our [text]
 * parameter.
 */
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
            modifier = Modifier.padding(all = 16.dp),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }
}

/**
 * This Composable is called to display a [Paragraph] whose [ParagraphType] field [Paragraph.type]
 * is [ParagraphType.Bullet]. Our root Composable is a [Row] which contains a [Box] which is wrapped
 * in a [with] scope function that causes the  `current` [LocalDensity] (the [Density] to be used to
 * transform between density-independent pixelunits (DP) and pixel units or scale-independent pixel
 * units (SP) and pixel units) to be the `this` receiver for the conversions used by its [Modifier].
 * The `modifier` argument of the [Box] is a [Modifier.size] that sets its width to 8.sp (converted
 * to Dp) and its `height` to 8.sp (converted to Dp). A `RowScope` `Modifier.alignBy` is added to
 * the [Modifier.size] that adds an alignment "baseline" 1sp below the bottom of the circle, which
 * is created by a [Modifier.background] whose `color` argument is the `current` [LocalContentColor]
 * (the preferred content color for our position in the hierarchy), and whose `shape` argument is
 * [CircleShape] (Circular [RoundedCornerShape] with all the corners sized as the 50 percent of the
 * shape size). The other Composable in the [Row] is a [Text] whose `modifier` parameter is a
 * `RowScope` `Modifier.weight` with a `weight` of 1f (the [Text] gets all the space left over in
 * the [Row] after its sibling is measured and placed) to which is added a `RowScope` `Modifier.alignBy`
 * of [FirstBaseline] (the [HorizontalAlignmentLine] defined by the baseline of the first line of the
 * text contents of the [Text]), whose `text` argument is our [AnnotatedString] parameter [text],
 * and whose `style` is our [TextStyle] parameter `textStyle` to which [TextStyle.merge] is used to
 * create a new text style that is a combination of `textStyle` and our [ParagraphStyle] parameter
 * `paragraphStyle`.
 *
 * @param text the [AnnotatedString] that we should display in our [Text]
 * @param textStyle the [TextStyle] that our [Text] should use to display our [text] parameter.
 * @param paragraphStyle the [ParagraphStyle] that our [Text] should use to display our [text]
 * parameter.
 */
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
                .alignBy(FirstBaseline),
            text = text,
            style = textStyle.merge(paragraphStyle)
        )
    }
}

/**
 * This class only exists to be a return type, allowing the return of three values from a the extension
 * function [ParagraphType.getTextAndParagraphStyle].
 *
 * @param textStyle the [TextStyle] that is appropriate for the [ParagraphType] receiver.
 * @param paragraphStyle the [ParagraphStyle] that is appropriate for the [ParagraphType] receiver.
 * @param trailingPadding the bottom padding to be applied to the bottom of the [Box] holding the
 * rendering of a [Paragraph] whose type is [ParagraphType].
 */
private data class ParagraphStyling(
    val textStyle: TextStyle,
    val paragraphStyle: ParagraphStyle,
    val trailingPadding: Dp
)

/**
 * Extension function of [ParagraphType] that returns a [ParagraphStyling] instance that contains
 * styling information for rendering a [Paragraph] of type [ParagraphType]. The [ParagraphStyling]
 * class holds the [TextStyle] for this type of [Paragraph] in its [ParagraphStyling.textStyle] field,
 * the [ParagraphStyle] for this type of [Paragraph] in its [ParagraphStyling.paragraphStyle] field,
 * and the bottom padding to be applied to the bottom of the [Box] holding the rendering of a
 * [Paragraph] of this type in its [ParagraphStyling.trailingPadding] field.
 *
 * First we initialize our [Typography] variable `val typography` to [MaterialTheme.typography] (the
 * current [Typography] at the call site's position in the hierarchy, which is [JetnewsTypography]
 * in our case). We initialize our [TextStyle] variable `var textStyle` to the `body1` [TextStyle]
 * of `typography`. Our [JetnewsTheme] custom [MaterialTheme] supplies a [TextStyle] whose [FontFamily]
 * is `Domine` with a [FontWeight] of [FontWeight.Normal] (which is the [Font] with resource ID
 * [R.font.domine_regular]), a `fontSize` of 16.sp and `letterSpacing` of 0.5.sp (see the
 * [Typography.body1] entry of [JetnewsTypography] in the file ui/theme/Type.kt). We initialize our
 * [ParagraphStyle] variable `var paragraphStyle` to a new instance, and initialize our variable
 * `var trailingPadding` to 24.dp
 *
 * We then branch on the value of the [ParagraphType] we are being called on:
 *  - [ParagraphType.Caption] we set `textStyle` to the `body1` [TextStyle] of `typography`
 */
@Composable
private fun ParagraphType.getTextAndParagraphStyle(): ParagraphStyling {
    val typography: Typography = MaterialTheme.typography
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
