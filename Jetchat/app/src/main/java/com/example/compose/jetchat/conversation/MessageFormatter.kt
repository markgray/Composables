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

package com.example.compose.jetchat.conversation

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

/**
 * Regex containing the syntax tokens
 */
val symbolPattern: Regex by lazy {
    @Suppress("RegExpSimplifiable")
    Regex(pattern = """(https?://[^\s\t\n]+)|(`[^`]+`)|(@\w+)|(\*[\w]+\*)|(_[\w]+_)|(~[\w]+~)""")
}

/**
 * Accepted annotations for the [ClickableText] Wrapper
 */
enum class SymbolAnnotationType {
    /**
     * This [SymbolAnnotationType] is added when the token matched is '@', and causes the [ClickableText]
     * to call its `authorClicked` lambda argument with the [AnnotatedString.Range.item]
     */
    PERSON,

    /**
     * This [SymbolAnnotationType] is added when the token matched is 'h', and causes the [ClickableText]
     * to call [UriHandler.openUri] method of its [UriHandler] with the [AnnotatedString.Range.item].
     */
    LINK
}

/**
 * Save some keystrokes.
 */
typealias StringAnnotation = AnnotatedString.Range<String>

/**
 * Pair returning styled content and annotation for ClickableText when matching syntax token
 */
typealias SymbolAnnotation = Pair<AnnotatedString, StringAnnotation?>

/**
 * Format a message following Markdown-lite syntax
 *  - @username -> bold, primary color and clickable element
 *  - http(s)://... -> clickable link, opening it into the browser
 *  - *bold* -> bold
 *  - _italic_ -> italic
 *  - ~strikethrough~ -> strikethrough
 *  - `MyClass.myMethod` -> inline code styling
 *
 * We initialize our [Sequence] of [MatchResult] variable `val tokens` to the result of calling the
 * [Regex.findAll] method of our [Regex] field [symbolPattern] with our [String] parameter [text]
 * (returns a sequence of all occurrences of the regular expression within the input string). Then
 * we return the [AnnotatedString] created by the [buildAnnotatedString] method when we use the
 * [AnnotatedString.Builder] passed our lambda block in which:
 *  - We initialize our [Int] variable `var cursorPosition` to 0.
 *  - If our [Boolean] parameter [primary] is `true` we initialize our [Color] variable
 *  `val codeSnippetBackground` to the [ColorScheme.secondary] color of our custom
 *  [MaterialTheme.colorScheme], and if it is `false` we initialize it to the [ColorScheme.surface]
 *
 * Then we loop over all of the [MatchResult] variable `var token` in our [Sequence] of [MatchResult]
 * variable `tokens`:
 *  - We call the [AnnotatedString.Builder.append] method to append the `text` returned by the
 *  [String.slice] method of our [String] parameter [text] between `cursorPosition` and the `first`
 *  element in the [MatchResult.range] of `token`.
 *  - We initialize our [AnnotatedString] variable `val annotatedString` and [AnnotatedString.Range]
 *  of [String] variable `val stringAnnotation` by using a destructuring declaration on the [Pair]
 *  that is returned by our method [getSymbolAnnotation] when it is called with `token` for its
 *  `matchResult` argument, [MaterialTheme.colorScheme] as its `colorScheme` argument, our [Boolean]
 *  parameter [primary] as its `primary`, and our [Color] variable `codeSnippetBackground` for its
 *  `codeSnippetBackground` argument.
 *  - We call the [AnnotatedString.Builder.append] method to append our [AnnotatedString] variable
 *  `annotatedString`.
 *  - If our [AnnotatedString.Range] of [String] variable `stringAnnotation` is not `null` we use a
 *  destructuring declaration on it to initialize our [String] variable `val item` to the [String]
 *  `item` property of `stringAnnotation`, [Int] variable `val start` to its `start` property,
 *  [Int] variable `val end` to its `end` property, and [String] variable `val tag` to its `tag`
 *  property. We then call the [AnnotatedString.Builder.addStringAnnotation] method to set `tag` as
 *  the tag for an anotation for the range `start` to `end`, with `item` as the `annotation`.
 *  - Then we set `cursorPosition` to the `last` element in the [MatchResult.range] of `token`, and
 *  loop around for the next [MatchResult].
 *
 * Having dealt with all the [MatchResult] we check it the [Sequence.none] method of `tokens` is
 * `false` and if so we call the [AnnotatedString.Builder.append] method to append the `text`
 * returned by the [String.slice] method of our [String] parameter [text] between `cursorPosition`
 * and the [String.lastIndex] of [text]. If it is `true` we just call the [AnnotatedString.Builder.append]
 * method to append our [String] parameter [text].
 *
 * @param text contains message to be parsed
 * @param primary used to select between two different color combinations. If `true` the user who
 * posted the [text] is "me" and the background will be the [ColorScheme.secondary] color of our
 * custom [MaterialTheme.colorScheme] and the text will be the [ColorScheme.inversePrimary] and if
 * `false` the background will be the [ColorScheme.surface] color and the text will be the
 * [ColorScheme.primary].
 * @return [AnnotatedString] with annotations used inside the ClickableText wrapper
 */
@Composable
fun messageFormatter(
    text: String,
    primary: Boolean
): AnnotatedString {
    val tokens: Sequence<MatchResult> = symbolPattern.findAll(input = text)

    return buildAnnotatedString {

        var cursorPosition = 0

        val codeSnippetBackground: Color =
            if (primary) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.surface
            }

        for (token: MatchResult in tokens) {
            append(text = text.slice(indices = cursorPosition until token.range.first))

            val (annotatedString, stringAnnotation) = getSymbolAnnotation(
                matchResult = token,
                colorScheme = MaterialTheme.colorScheme,
                primary = primary,
                codeSnippetBackground = codeSnippetBackground
            )
            append(text = annotatedString)

            if (stringAnnotation != null) {
                val (item: String, start: Int, end: Int, tag: String) = stringAnnotation
                addStringAnnotation(tag = tag, start = start, end = end, annotation = item)
            }

            cursorPosition = token.range.last + 1
        }

        if (!tokens.none()) {
            append(text.slice(indices = cursorPosition..text.lastIndex))
        } else {
            append(text = text)
        }
    }
}

/**
 * Map regex matches found in a message with supported syntax symbols.
 *
 * @param matchResult is a regex result matching our syntax symbols
 * @param colorScheme this is just our custom [MaterialTheme.colorScheme].
 * @param primary if `true` the user is "me" and we should use [ColorScheme.inversePrimary] for our
 * text, and if `false` we should use [ColorScheme.primary].
 * @param codeSnippetBackground the `background` color we should use for "code" text included between
 * backtick characters.
 * @return pair of [AnnotatedString] with optional [StringAnnotation] annotation (typealias to
 * [AnnotatedString.Range] of [String]) used inside the [ClickableText] wrapper
 */
private fun getSymbolAnnotation(
    matchResult: MatchResult,
    colorScheme: ColorScheme,
    primary: Boolean,
    codeSnippetBackground: Color
): SymbolAnnotation {
    return when (matchResult.value.first()) {
        '@' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
                    color = if (primary) colorScheme.inversePrimary else colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            ),
            StringAnnotation(
                item = matchResult.value.substring(1),
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.PERSON.name
            )
        )

        '*' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('*'),
                spanStyle = SpanStyle(fontWeight = FontWeight.Bold)
            ),
            null
        )

        '_' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('_'),
                spanStyle = SpanStyle(fontStyle = FontStyle.Italic)
            ),
            null
        )

        '~' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('~'),
                spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)
            ),
            null
        )

        '`' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('`'),
                spanStyle = SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    background = codeSnippetBackground,
                    baselineShift = BaselineShift(0.2f)
                )
            ),
            null
        )

        'h' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
                    color = if (primary) colorScheme.inversePrimary else colorScheme.primary
                )
            ),
            StringAnnotation(
                item = matchResult.value,
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.LINK.name
            )
        )

        else -> SymbolAnnotation(AnnotatedString(matchResult.value), null)
    }
}
