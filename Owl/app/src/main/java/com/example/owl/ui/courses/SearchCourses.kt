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

@file:Suppress("UnusedImport")

package com.example.owl.ui.courses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.owl.R
import com.example.owl.model.Topic
import com.example.owl.model.topics
import com.example.owl.ui.theme.BlueTheme

/**
 * Displays a list of courses searching by topic.
 *
 * We start by using destructuring to initialize and remember our [TextFieldValue] variable
 * `searchTerm` and lambda taking [TextFieldValue] variable `updateSearchTerm` to the current
 * value and setter lambda for a [MutableState] wrapped [TextFieldValue] whose initial value is
 * the empty string.
 *
 * Our root composable is a [LazyColumn] whose `modifier` argument is our [Modifier] parameter
 * [modifier] chained to a [Modifier.statusBarsPadding] to add padding to accommodate the status
 * bars insets chained to a [Modifier.fillMaxHeight] to fill the maximum height its parent allows.
 * In the [LazyListScope] `content` composable lambda argument of the [LazyColumn] we:
 *
 * **First**: We compose a [LazyListScope.item] whose [LazyItemScope] `content` composable lambda
 * argument is an [AppBar] whose arguments are:
 *  - `searchTerm`: is [MutableState] wrapped [TextFieldValue] variable `searchTerm`.
 *  - `updateSearchTerm`: is our lambda taking [TextFieldValue] variable `updateSearchTerm` (the
 *  setter for `searchTerm`).
 *
 * **Second**: We initialize our [List] of [Topic] variable `filteredTopics` to the result returned
 * by our function [getTopics] whose `searchTerm` argument is the [TextFieldValue.text] of
 * `searchTerm` and whose `topics` argument is our [List] of [Topic] parameter [topics].
 *
 * **Third**: We compose a [LazyListScope.items] whose `items` argument is our [List] of
 * [Topic] variable `filteredTopics` and whose `key` argument is a lambda that accepts the current
 * [Topic] passed the lambda in variable `it` and returns the [Topic.name] of `it`. In the
 * [LazyItemScope] `itemContent` composable lambda argument of the [LazyListScope.items] we accept
 * the current [Topic] passed the lambda in variable `topic` and compose a [Text] whose `text`
 * arguments are:
 *  - `text`: is the [Topic.name] of `topic`.
 *  - `style`: is the [Typography.h5] or our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.fillMaxWidth] to fill the maximum width its parent allows, chained
 *  to a [Modifier.clickable] whose `onClick` argument is a do-nothing lambda, chained to a
 *  [Modifier.padding] that adds `16.dp` padding to the `start`, `8.dp` padding to the `top`,
 *  `16.dp` padding to the `end`, and `8.dp` padding to the `bottom`, chained to a
 *  [Modifier.wrapContentWidth] whose `align` argument is [Alignment.Start], chained to a
 *  [LazyItemScope.animateItem] to animate the item.
 *
 * @param topics (state) topics to display
 * @param modifier modifier for this element
 */
@Composable
fun SearchCourses(
    topics: List<Topic>,
    modifier: Modifier = Modifier
) {
    val (searchTerm: TextFieldValue, updateSearchTerm: (TextFieldValue) -> Unit) =
        remember { mutableStateOf(value = TextFieldValue(text = "")) }
    LazyColumn(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxHeight()
    ) {
        item { AppBar(searchTerm = searchTerm, updateSearchTerm = updateSearchTerm) }
        val filteredTopics: List<Topic> = getTopics(searchTerm = searchTerm.text, topics = topics)
        items(
            items = filteredTopics,
            key = { it.name }
        ) { topic: Topic ->
            Text(
                text = topic.name,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { /* todo */ })
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
                    .wrapContentWidth(align = Alignment.Start)
                    .animateItem()
            )
        }
    }
}

/**
 * Returns a list of [Topic]s from the [topics] list that match the [searchTerm].
 * If [searchTerm] is empty, returns all [topics].
 *
 * @param searchTerm The search term to filter by.
 * @param topics The list of [Topic]s to filter.
 * @return A list of [Topic]s that match the [searchTerm].
 */
private fun getTopics(
    searchTerm: String,
    topics: List<Topic>
): List<Topic> {
    return if (searchTerm != "") {
        topics.filter { it.name.contains(searchTerm, ignoreCase = true) }
    } else {
        topics
    }
}

/**
 * This is the [TopAppBar] of the [SearchCourses] Composable.
 *
 * Our root composable is a [TopAppBar] whose `elevation` argument is `0.dp`. In the [RowScope]
 * `content` composable lambda argument we:
 *
 * **First**: We compose an [Image] whose arguments are:
 *  - `painter`: is the [Painter] returned by [painterResource] for the drawable whose resource
 *  ID is `R.drawable.ic_search` (a stylized magnifying glass icon).
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.padding] whose that adds `16.dp` to all sides chained to a
 *  [RowScope.align] whose `alignment` argument is [Alignment.CenterVertically].
 *
 * **Second**: We compose a [BasicTextField] whose arguments are:
 *  - `value`: is our [TextFieldValue] parameter [searchTerm].
 *  - `onValueChange`: is our lambda parameter [updateSearchTerm].
 *  - `textStyle`: is a copy of [Typography.subtitle1] of our custom [MaterialTheme.typography]
 *  with its `color` property set to the current [LocalContentColor].
 *  - `maxLines`: is `1`.
 *  - `cursorBrush`: is a [SolidColor] whose `value` argument is the current [LocalContentColor].
 *  - `modifier`: is a [RowScope.weight] whose `weight` argument is `1f` chained to a [RowScope.align]
 *  whose `alignment` argument is [Alignment.CenterVertically].
 *
 * **Third**: We compose an [IconButton] whose `modifier` argument is a [RowScope.align] whose
 * `alignment` argument is [Alignment.CenterVertically], and whose `onClick` argument is a
 * do-nothing lambda. In the [IconButton] `content` composable lambda argument we compose an
 * [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Filled.AccountCircle].
 *  - `contentDescription`: is the string with resource ID `R.string.label_profile` ("Profile").
 *
 * @param searchTerm the current [TextFieldValue] that has been entered in the search field.
 * @param updateSearchTerm a lambda the [BasicTextField] can call with a new [TextFieldValue] to
 * have it update the value that is displayed.
 */
@Composable
private fun AppBar(
    searchTerm: TextFieldValue,
    updateSearchTerm: (TextFieldValue) -> Unit
) {
    TopAppBar(elevation = 0.dp) {
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.CenterVertically)
        )
        // TODO hint
        BasicTextField(
            value = searchTerm,
            onValueChange = updateSearchTerm,
            textStyle = MaterialTheme.typography.subtitle1.copy(
                color = LocalContentColor.current
            ),
            maxLines = 1,
            cursorBrush = SolidColor(value = LocalContentColor.current),
            modifier = Modifier
                .weight(weight = 1f)
                .align(alignment = Alignment.CenterVertically)
        )
        IconButton(
            modifier = Modifier.align(alignment = Alignment.CenterVertically),
            onClick = { /* todo */ }
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = stringResource(id = R.string.label_profile)
            )
        }
    }
}

/**
 * Preview of [SearchCourses].
 */
@Preview(name = "Search Courses")
@Composable
private fun FeaturedCoursesPreview() {
    BlueTheme {
        SearchCourses(topics = topics, modifier = Modifier)
    }
}
