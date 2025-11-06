/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composemail.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MarkAsUnread
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atLeastWrapContent
import com.example.composemail.ui.components.ProfileButton
import com.example.composemail.ui.components.SearchBar
import com.example.composemail.ui.theme.Selection

/**
 * A toolbar that displays a search bar by default, and transforms into a selection toolbar
 * when one or more items are selected. The transition between the two states is animated.
 *
 * This composable uses its [selectionCountProvider] lambda parameter to get the number of selected
 * items. This is a performance optimization to avoid recomposing the [AnimatedContent] every time
 * the selection count changes. Instead, [AnimatedContent] only recomposes when its `targetState`
 * (whether `selectionCount` > 0) changes. The [SelectionToolbar] then reads the latest count
 * from the lambda when it is composed.
 *
 * We start by initializing and remembering our [State] wrapped [Boolean] variable `isInSelection`
 * to the [derivedStateOf] checking if our lambda parameter [selectionCountProvider] returns a
 * value greater than 0. Then we compose an [AnimatedContent] whose arguments are:
 *  - `targetState`: The current value of `isInSelection`.
 *  - `modifier`: is our [Modifier] parameter [modifier]
 *  - `label`: is the [String] "AnimatedContent"
 *
 * In the [AnimatedContentScope] `content` composable lambda argument of the [AnimatedContent]
 * we accept the [Boolean] passed the lambda in variable `isSelected`. If `isSelected` is `true`
 * we compose a [SelectionToolbar] with the arguments:
 *  - `modifier`: is an empty [Modifier] instance
 *  - `selectionCountProvider`: is our lambda parameter [selectionCountProvider]
 *  - `onUnselectAll`: is our lambda parameter [onUnselectAll].
 *
 * If `isSelected` is `false` we compose a [SearchToolbar] with the argument:
 *  - `modifier`: is a [Modifier.padding] that adds `4.dp` to `all` sides.
 *
 * @param modifier The [Modifier] to be applied to the toolbar.
 * @param selectionCountProvider A lambda that provides the current number of selected items.
 * @param onUnselectAll A lambda to be invoked when the user wishes to clear the selection.
 */
@Composable
fun TopToolbar(
    modifier: Modifier = Modifier,
    selectionCountProvider: () -> Int, // Lambda to avoid recomposing AnimatedContent on every count change
    onUnselectAll: () -> Unit
) {
    val isInSelection: Boolean by remember { derivedStateOf { selectionCountProvider() > 0 } }
    AnimatedContent(
        targetState = isInSelection,
        modifier = modifier,
        label = "AnimatedContent"
    ) { isSelected: Boolean ->
        if (isSelected) {
            SelectionToolbar(
                modifier = Modifier,
                selectionCountProvider = selectionCountProvider,
                onUnselectAll = onUnselectAll
            )
        } else {
            SearchToolbar(modifier = Modifier.padding(all = 4.dp))
        }
    }
}

/**
 * [ConstraintSet] for the [SearchToolbar].
 *
 * It constraints the `searchBar` to fill the available width between the start of the parent and
 * the `profileButton`, and constraints the `profileButton` to the end of the parent. Both views
 * are centered vertically.
 * TODO: Continue here.
 */
private val searchToolbarConstraintSet: ConstraintSet = ConstraintSet {
    val searchBar: ConstrainedLayoutReference = createRefFor(id = "searchBar")
    val profileB: ConstrainedLayoutReference = createRefFor(id = "profileButton")

    constrain(ref = searchBar) {
        width = Dimension.fillToConstraints.atLeastWrapContent
        height = Dimension.wrapContent
        centerVerticallyTo(other = parent)
        start.linkTo(anchor = parent.start)
        end.linkTo(anchor = profileB.start)
    }

    constrain(ref = profileB) {
        height = Dimension.value(dp = 40.dp)
        width = Dimension.value(dp = 40.dp)
        centerVerticallyTo(other = searchBar)
        end.linkTo(anchor = parent.end)
    }
}

@Composable
private fun SearchToolbar(modifier: Modifier) {
    ConstraintLayout(
        modifier = modifier,
        constraintSet = searchToolbarConstraintSet
    ) {
        SearchBar(modifier = Modifier.layoutId(layoutId = "searchBar"))
        ProfileButton(modifier = Modifier.layoutId(layoutId = "profileButton"))
    }
}

@Composable
private fun SelectionToolbar(
    modifier: Modifier,
    selectionCountProvider: () -> Int,
    onUnselectAll: () -> Unit
) {
    Row(
        modifier = modifier
            .heightIn(min = 40.dp)
            .background(color = Selection.backgroundColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Icon(
            modifier = Modifier.clickable {
                onUnselectAll()
            },
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null
        )
        Text(text = selectionCountProvider().toString())
        Spacer(modifier = Modifier.weight(weight = 1.0f, fill = true))
        Icon(imageVector = Icons.Default.Archive, contentDescription = null)
        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        Icon(imageVector = Icons.Default.MarkAsUnread, contentDescription = null)
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
    }
}

@Preview
@Composable
private fun TopToolbarPreview() {
    Column {
        TopToolbar(
            modifier = Modifier.width(intrinsicSize = IntrinsicSize.Min),
            selectionCountProvider = { 0 }
        ) {}
        TopToolbar(
            modifier = Modifier,
            selectionCountProvider = { 0 }
        ) {}
        TopToolbar(
            modifier = Modifier.fillMaxWidth(),
            selectionCountProvider = { 0 }
        ) {}
        TopToolbar(
            modifier = Modifier.width(IntrinsicSize.Min),
            selectionCountProvider = { 1 }
        ) {}
        TopToolbar(
            modifier = Modifier,
            selectionCountProvider = { 1 }
        ) {}
    }
}