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

package com.example.baselineprofiles_codelab.ui.home.search

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.model.SearchRepo
import com.example.baselineprofiles_codelab.model.SearchSuggestionGroup
import com.example.baselineprofiles_codelab.ui.components.JetsnackSurface
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * A composable function that displays a list of search suggestions.
 *
 * Our root composable is a [LazyColumn], and in its [LazyListScope] `content` composable lambda
 * argument we use the [Iterable.forEach] method of our [List] of [SearchSuggestionGroup] parameter
 * [suggestions] to loop over each [SearchSuggestionGroup] capturing the [SearchSuggestionGroup] in
 * our variable `suggestionGroup`. In the `action` lambda argument we first call the
 * [LazyListScope.item] method to compose a [SuggestionHeader] composable with its `name` argument
 * the [SearchSuggestionGroup.name] of the current [SearchSuggestionGroup] in our variable
 * `suggestionGroup`.
 *
 * Next we call the [LazyListScope.items] method with its `items` argument the
 * [SearchSuggestionGroup.suggestions] property of the current [SearchSuggestionGroup] in our
 * variable `suggestionGroup`. In its [LazyItemScope] `itemContent` lambda argument we capture th
 * e [String] passed the lambda in our variable `suggestion` then compose a [Suggestion] composable
 * with its `suggestion` argument the current [String] in our variable `suggestion` and its
 * `onSuggestionSelect` argument our [onSuggestionSelect] lambda parameter, and its `modifier`
 * argument a [LazyItemScope.fillParentMaxWidth].
 *
 * Finally we call the [LazyListScope.item] method to compose a [Spacer] composable with its
 * `modifier` argument a [Modifier.height] of 4.dp.
 *
 * @param suggestions A list of [SearchSuggestionGroup] objects representing the search suggestions
 * to be displayed.
 * @param onSuggestionSelect A lambda function that should be invoked with one of the [String]'s in
 * the [List] of [String] property of [SearchSuggestionGroup.suggestions] when the [Suggestion]
 * displaying the [String] is clicked.
 */
@Composable
fun SearchSuggestions(
    suggestions: List<SearchSuggestionGroup>,
    onSuggestionSelect: (String) -> Unit
) {
    LazyColumn {
        suggestions.forEach { suggestionGroup: SearchSuggestionGroup ->
            item {
                SuggestionHeader(name = suggestionGroup.name)
            }
            items(items = suggestionGroup.suggestions) { suggestion: String ->
                Suggestion(
                    suggestion = suggestion,
                    onSuggestionSelect = onSuggestionSelect,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(height = 4.dp))
            }
        }
    }
}

/**
 * Displays a header for a suggestion section.
 *
 * This composable displays a text header with a specific style and layout,
 * typically used to introduce a section of suggested items or actions.
 *
 * TODO: Continue here.
 *
 * @param name The text to display as the header.
 * @param modifier The [Modifier] to be applied to the header.
 */
@Composable
private fun SuggestionHeader(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        style = MaterialTheme.typography.h6,
        color = JetsnackTheme.colors.textPrimary,
        modifier = modifier
            .heightIn(min = 56.dp)
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .wrapContentHeight()
    )
}

@Composable
private fun Suggestion(
    suggestion: String,
    onSuggestionSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = suggestion,
        style = MaterialTheme.typography.subtitle1,
        modifier = modifier
            .heightIn(min = 48.dp)
            .clickable { onSuggestionSelect(suggestion) }
            .padding(start = 24.dp)
            .wrapContentSize(align = Alignment.CenterStart)
    )
}

/**
 * Three previews of the [SearchSuggestions] composable:
 *  -"default" light theme
 *  -"dark theme" dark theme
 *  -"large font" large font size
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun PreviewSuggestions() {
    JetsnackTheme {
        JetsnackSurface {
            SearchSuggestions(
                suggestions = SearchRepo.getSuggestions(),
                onSuggestionSelect = { }
            )
        }
    }
}
