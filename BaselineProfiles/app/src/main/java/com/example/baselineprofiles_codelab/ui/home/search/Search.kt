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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.model.Filter
import com.example.baselineprofiles_codelab.model.SearchCategoryCollection
import com.example.baselineprofiles_codelab.model.SearchRepo
import com.example.baselineprofiles_codelab.model.SearchSuggestionGroup
import com.example.baselineprofiles_codelab.model.Snack
import com.example.baselineprofiles_codelab.model.SnackRepo
import com.example.baselineprofiles_codelab.ui.components.JetsnackDivider
import com.example.baselineprofiles_codelab.ui.components.JetsnackSurface
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import com.example.baselineprofiles_codelab.ui.utils.mirroringBackIcon
import kotlinx.coroutines.CoroutineScope

/**
 * A composable function that displays the search screen.
 *
 * This function handles the overall search flow, including:
 *  - Displaying the search bar.
 *  - Managing the search query and focus state.
 *  - Triggering the search when the query changes.
 *  - Displaying different content based on the search state, such as categories, suggestions,
 *  results, or no results.
 *
 * Our root composable is a [JetsnackSurface] whose `modifier` argument is a [Modifier.fillMaxSize].
 * In its `content` composable lambda argument we compose a [Column] in whose [ColumnScope] `content`
 * composable lambda argument we compose:
 *
 * **First** a [Spacer] whose `modifier` argument is a [Modifier.statusBarsPadding] to add
 * status bar height padding.
 *
 * **Second** a [SearchBar] whose arguments are:
 *  - `query`: the [SearchState.query] property of our [SearchState] parameter [state].
 *  - `onQueryChange`: a lambda that sets the [SearchState.query] property of our [SearchState]
 *  parameter [state] to the new query value passed the lambda.
 *  - `searchFocused`: the [SearchState.focused] property of our [SearchState] parameter [state].
 *  - `onSearchFocusChange`: a lambda that sets the [SearchState.focused] property of our
 *  [SearchState] parameter [state] to the new focus state value passed the lambda.
 *  - `onClearQuery`: a lambda that sets the [SearchState.query] property of our [SearchState]
 *  parameter [state] to an empty [TextFieldValue].
 *  - `searching`: the [SearchState.searching] property of our [SearchState] parameter [state].
 *
 * **Third** a [JetsnackDivider] to separate the search bar from the search results.
 *
 * **Fourth** a [LaunchedEffect] whose `key1` argument is the [TextFieldValue.text] of the
 * [SearchState.query] property of the [SearchState.query] property of our [SearchState]
 * parameter [state]. Inside the [CoroutineScope] `block` lambda argument of the [LaunchedEffect]
 * we set the [SearchState.searching] property of our [SearchState] parameter [state] to `true`,
 * call the [SearchRepo.search] function with the query text of the [SearchState.query] property
 * of our [SearchState] parameter [state] as the `query` argument, and then set the
 * [SearchState.searching] property of our [SearchState] parameter [state] to `false` when the
 * [SearchRepo.search] suspend function completes.
 *
 * **Fifth** a `when` statement branches on the value of the [SearchState.searchDisplay] property
 * of our [SearchState] parameter [state]:
 *  - [SearchDisplay.Categories] -> we compose a [SearchCategories] composable whose `categories`
 *  argument is the [SearchState.categories] property of our [SearchState] parameter [state].
 *  - [SearchDisplay.Suggestions] -> we compose a [SearchSuggestions] composable whose `suggestions`
 *  argument is the [SearchState.suggestions] property of our [SearchState] parameter [state], and
 *  whose `onSuggestionSelect` argument is a lambda that sets the [SearchState.query] property of our
 *  [SearchState] parameter [state] to a [TextFieldValue] whose `text` property is the suggestion
 *  [String] passed to the lambda.
 *  - [SearchDisplay.Results] -> we compose a [SearchResults] composable whose `searchResults`
 *  argument is the [SearchState.searchResults] property of our [SearchState] parameter [state],
 *  whose `filters` argument is the [SearchState.filters] property of our [SearchState] parameter
 *  [state], and whose `onSnackClick` argument is our lambda parameter [onSnackClick].
 *  - [SearchDisplay.NoResults] -> we compose a [NoResults] composable whose `query` argument is the
 *  [SearchState.query] property of our [SearchState] parameter [state].
 *
 * @param onSnackClick A lambda function that is invoked when a `SearchResult` displaying a [Snack]
 * is clicked in our [SearchResults] composable. It receives the [Snack.id] of the clicked snack as
 * its argument.
 * @param modifier The [Modifier] to be applied to the root layout of the search screen.
 * @param state The [SearchState] object that holds the current state of the search screen,
 * including the search query, focus, search results, and display mode.
 */
@Composable
fun Search(
    onSnackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    state: SearchState = rememberSearchState()
) {
    JetsnackSurface(modifier = modifier.fillMaxSize()) {
        Column {
            Spacer(modifier = Modifier.statusBarsPadding())
            SearchBar(
                query = state.query,
                onQueryChange = { state.query = it },
                searchFocused = state.focused,
                onSearchFocusChange = { state.focused = it },
                onClearQuery = { state.query = TextFieldValue("") },
                searching = state.searching
            )
            JetsnackDivider()

            LaunchedEffect(key1 = state.query.text) {
                state.searching = true
                state.searchResults = SearchRepo.search(query = state.query.text)
                state.searching = false
            }
            when (state.searchDisplay) {
                SearchDisplay.Categories -> SearchCategories(categories = state.categories)
                SearchDisplay.Suggestions -> SearchSuggestions(
                    suggestions = state.suggestions,
                    onSuggestionSelect = { suggestion: String ->
                        state.query = TextFieldValue(text = suggestion)
                    }
                )
                SearchDisplay.Results -> SearchResults(
                    searchResults = state.searchResults,
                    filters = state.filters,
                    onSnackClick = onSnackClick
                )
                SearchDisplay.NoResults -> NoResults(query = state.query.text)
            }
        }
    }
}

/**
 * Enum class representing the different display states of a search UI.
 *
 * This enum defines the various states that a search interface can be in,
 * indicating what content should be shown to the user.
 */
enum class SearchDisplay {
    /**
     * We should display a [SearchCategories] composable.
     */
    Categories,

    /**
     * We should display a [SearchSuggestions] composable.
     */
    Suggestions,

    /**
     * We should display a [SearchResults] composable.
     */
    Results,

    /**
     *
     */
    NoResults
}

/**
 * Remembers and creates a [SearchState] instance.
 *
 * This composable function is designed to provide a stable [SearchState] object
 * throughout recompositions. It leverages the `remember` function to ensure that
 * the state is only created once during the initial composition and then
 * reused across subsequent recompositions, unless the inputs change.
 *
 * @param query The current query text in the search field, represented as a [TextFieldValue].
 * Defaults to an empty query.
 * @param focused A boolean indicating whether the search field is currently focused.
 * Defaults to `false`.
 * @param searching A boolean indicating whether a search is currently in progress.
 * Defaults to `false`.
 * @param categories A list of [SearchCategoryCollection] representing available search categories.
 * Defaults to the categories retrieved from [SearchRepo.getCategories].
 * @param suggestions A list of [SearchSuggestionGroup] representing search suggestions.
 * Defaults to the suggestions retrieved from [SearchRepo.getSuggestions].
 * @param filters A list of [Filter] representing available filters for search results.
 * Defaults to the filters retrieved from [SnackRepo.getFilters].
 * @param searchResults A list of [Snack] representing the current search results.
 * Defaults to an empty list.
 * @return A [SearchState] object holding the current state of the search UI.
 */
@Composable
private fun rememberSearchState(
    query: TextFieldValue = TextFieldValue(""),
    focused: Boolean = false,
    searching: Boolean = false,
    categories: List<SearchCategoryCollection> = SearchRepo.getCategories(),
    suggestions: List<SearchSuggestionGroup> = SearchRepo.getSuggestions(),
    filters: List<Filter> = SnackRepo.getFilters(),
    searchResults: List<Snack> = emptyList()
): SearchState {
    return remember {
        SearchState(
            query = query,
            focused = focused,
            searching = searching,
            categories = categories,
            suggestions = suggestions,
            filters = filters,
            searchResults = searchResults
        )
    }
}

/**
 * Represents the state of the search screen.
 *
 * This class holds all the necessary information to display and interact with
 * the search functionality, including the current search query, focus state,
 * loading state, available categories, suggestions, filters, and search results.
 *
 * @property query The current text entered in the search field.
 * @property focused Indicates whether the search field is currently in focus.
 * @property searching Indicates whether a search is currently in progress.
 * @property categories A list of available search categories.
 * @property suggestions A list of suggestion groups to display when the search field is focused
 * and empty.
 * @property filters A list of active filters applied to the search.
 * @property searchResults A list of search results matching the current query and filters.
 * @property searchDisplay Determines which content to display on the screen based on the
 * current state, one of [SearchDisplay.Categories], [SearchDisplay.Suggestions],
 * [SearchDisplay.Results], or [SearchDisplay.NoResults].
 */
@Stable
class SearchState(
    query: TextFieldValue,
    focused: Boolean,
    searching: Boolean,
    categories: List<SearchCategoryCollection>,
    suggestions: List<SearchSuggestionGroup>,
    filters: List<Filter>,
    searchResults: List<Snack>
) {
    var query: TextFieldValue by mutableStateOf(query)
    var focused: Boolean by mutableStateOf(focused)
    var searching: Boolean by mutableStateOf(searching)
    var categories: List<SearchCategoryCollection> by mutableStateOf(categories)
    var suggestions: List<SearchSuggestionGroup> by mutableStateOf(suggestions)
    var filters: List<Filter> by mutableStateOf(filters)
    var searchResults: List<Snack> by mutableStateOf(searchResults)
    val searchDisplay: SearchDisplay
        get() = when {
            !focused && query.text.isEmpty() -> SearchDisplay.Categories
            focused && query.text.isEmpty() -> SearchDisplay.Suggestions
            searchResults.isEmpty() -> SearchDisplay.NoResults
            else -> SearchDisplay.Results
        }
}

/**
 * A search bar composable that allows users to enter a query.
 *
 * This composable displays a text field for search input, along with a clear/back button
 * and a loading indicator. It also displays a hint text when the query is empty.
 *
 * Our root composable is a [JetsnackSurface] whose arguments are:
 *  - `color`: [Color] the [JetsnackColors.uiFloated] property of our custom [JetsnackTheme.colors]
 *  - `contentColor`: [Color] the [JetsnackColors.textSecondary] property of our custom
 *  [JetsnackTheme.colors].
 *  - `shape`: [Shape] the [Shapes.small] of our custom [MaterialTheme.shapes].
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.fillMaxWidth], with
 *  a [Modifier.height] of 56.dp chained to that, and then a [Modifier.padding] that adds `24.dp`
 *  padding to the `horizontal` sides and `8.dp` padding to the `vertical` sides chained to that.
 *
 * In the `content` composable lambda argument of the [JetsnackSurface] we compose a [Box] whose
 * `modifier` argument is a [Modifier.fillMaxSize]. In the [BoxScope] `content` composable lambda
 * argument of the [Box] we first check if the [TextFieldValue.text] of [TextFieldValue] paramete
 * [query] text is empty. If it is, we compose a [SearchHint] composable.
 *
 * Then we compose a [Row] whose `verticalAlignment` argument is [Alignment.CenterVertically], and
 * whose `modifier` argument is a [Modifier.fillMaxSize], with a [Modifier.wrapContentHeight] chained
 * to that. In the [RowScope] `content` composable lambda argument of the [Row] we first check
 * if the [Boolean] parameter [searchFocused] is `true`. If it is, we compose an [IconButton]
 * whose `onClick` argument is our lambda parameter [onClearQuery], and whose `content` composable
 * lambda argument is an [Icon] whose arguments are:
 *  - `imageVector`: the [ImageVector] chosen to be drawn by our [mirroringBackIcon] method, either
 *  `Icons.AutoMirrored.Outlined.ArrowBack` or `Icons.AutoMirrored.Outlined.ArrowForward` depending
 *  on the current layout direction.
 *  - `tint`: [Color] the [JetsnackColors.iconPrimary] of our custom [JetsnackTheme.colors].
 *  - `contentDescription`: the string resource with id `R.string.label_back` ("Back").
 *
 * Next in the [RowScope] `content` composable lambda argument of the [Row] we compose a
 * [BasicTextField] whose arguments are:
 *  - `value`: our [TextFieldValue] parameter [query].
 *  - `onValueChange`: our lambda parameter [onQueryChange].
 *  - `modifier`: a [RowScope.weight] whose `weight` is `1f`, and then a [Modifier.onFocusChanged]
 *  whose `onFocusChange` lambda argument is a lambda that calls our lambda parameter
 *  [onSearchFocusChange] with the [FocusState.isFocused] property of the [FocusState] passed the
 *  lambda.
 *
 * Then if our [Boolean] parameter [searching] is `true`, we compose a [CircularProgressIndicator]
 * whose arguments are:
 *  - `color`: [Color] the [JetsnackColors.iconPrimary] of our custom [JetsnackTheme.colors].
 *  - `modifier`: is a [Modifier.padding] that adds `6.dp` padding to each `horizontal` side, with
 *  a [Modifier.size] whose `size` is `36.dp` chained to that.
 *
 * If our [Boolean] parameter [searching] is `false`, we compose a [Spacer] whose `modifier`
 * argument is a [Modifier.width] whose `width` is [IconSize] (to balance the arrow icon).
 *
 * @param query The current text value in the search bar.
 * @param onQueryChange Callback invoked when the text in the search bar changes.
 * @param searchFocused A boolean indicating whether the search bar is currently focused.
 * @param onSearchFocusChange Callback invoked when the focus state of the search bar changes.
 * @param onClearQuery Callback invoked when the clear/back button is clicked.
 * @param searching A boolean indicating whether a search is currently in progress.
 * @param modifier The [Modifier] to be applied to the search bar.
 */
@Composable
private fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    searchFocused: Boolean,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    searching: Boolean,
    modifier: Modifier = Modifier
) {
    JetsnackSurface(
        color = JetsnackTheme.colors.uiFloated,
        contentColor = JetsnackTheme.colors.textSecondary,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .height(height = 56.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (query.text.isEmpty()) {
                SearchHint()
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
            ) {
                if (searchFocused) {
                    IconButton(onClick = onClearQuery) {
                        Icon(
                            imageVector = mirroringBackIcon(),
                            tint = JetsnackTheme.colors.iconPrimary,
                            contentDescription = stringResource(id = R.string.label_back)
                        )
                    }
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(weight = 1f)
                        .onFocusChanged { state: FocusState ->
                            onSearchFocusChange(state.isFocused)
                        }
                )
                if (searching) {
                    CircularProgressIndicator(
                        color = JetsnackTheme.colors.iconPrimary,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(size = 36.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(width = IconSize)) // balance arrow icon
                }
            }
        }
    }
}

/**
 * The size of the search icon.
 */
private val IconSize = 48.dp

/**
 * [SearchHint] is a composable function that displays a visual hint
 * to the user indicating that they can search for something.
 *
 * It shows a search icon and a hint text, both in a subdued color,
 * to suggest the search functionality without being overly prominent.
 *
 * This composable is designed to be used within a search bar or a
 * similar context where the user is expected to input a search query.
 *
 * The hint includes:
 * - A search icon (Magnifying glass).
 * - Hint text ("Search Jetsnack").
 * - Subdued text color, which will be provided by the `JetsnackTheme`.
 *
 * Our root composable is a [Row] whose `verticalAlignment` argument is
 * [Alignment.CenterVertically], and whose `modifier` argument is a
 * [Modifier.fillMaxSize], with a [Modifier.wrapContentSize] chained to that.
 *
 * In the [RowScope] `content` composable lambda argument of the [Row] we compose:
 *
 * **First** an [Icon] whose arguments are:
 *  - `imageVector`: the [ImageVector] drawn by [Icons.Outlined.Search].
 *  - `tint`: [Color] the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors].
 *  - `contentDescription`: the string resource with id `R.string.label_search` ("Perform search").
 *
 * **Second** a [Spacer] whose `modifier` argument is a [Modifier.width] whose `width` is `8.dp`.
 *
 * **Third** a [Text] whose `text` argument is the string resource with id `R.string.placeholder_search`
 * ("Search Jetsnack"), and whose [Color] `color` argument is the [JetsnackColors.textHelp] of our
 * custom [JetsnackTheme.colors].
 */
@Composable
private fun SearchHint() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            tint = JetsnackTheme.colors.textHelp,
            contentDescription = stringResource(id = R.string.label_search)
        )
        Spacer(modifier = Modifier.width(width = 8.dp))
        Text(
            text = stringResource(id = R.string.search_jetsnack),
            color = JetsnackTheme.colors.textHelp
        )
    }
}

/**
 * Three Previews of the SearchBar Composable:
 *  - "default" preview with light theme
 *  - "dark theme" preview with dark theme
 *  - "large font" preview with `fontScale` set to `2f`
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun SearchBarPreview() {
    JetsnackTheme {
        JetsnackSurface {
            SearchBar(
                query = TextFieldValue(""),
                onQueryChange = { },
                searchFocused = false,
                onSearchFocusChange = { },
                onClearQuery = { },
                searching = false
            )
        }
    }
}
