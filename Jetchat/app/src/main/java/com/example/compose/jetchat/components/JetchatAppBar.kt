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

package com.example.compose.jetchat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.R
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.conversation.ChannelNameBar
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.profile.ProfileFragment

/**
 * This is essentially an adapter for the [CenterAlignedTopAppBar] that it contains that provides
 * JetChat specific default values to it, and defines an API for it that makes it easy to use. It is
 * used in the [ChannelNameBar] Composable which is used as the `topBar` of the [Scaffold] in the
 * [ConversationContent] Composable, and in [ProfileFragment] in a [ComposeView] as its "tool bar".
 * We pass [CenterAlignedTopAppBar] our parameters unchanged, but use a [JetchatIcon] for its
 * `navigationIcon` argument, with its `contentDescription` the [String] with resource ID
 * [R.string.navigation_drawer_open] ("Open navigation drawer"), and its `modifier` argument a
 * [Modifier.size] that sets its size to 64.dp, with a [Modifier.clickable] whose `onClick` argument
 * is our lambda parameter [onNavIconPressed] followed by a [Modifier.padding] that adds 16.dp to
 * all sides of the [JetchatIcon].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [ChannelNameBar] passes us its `modifier` parameter which is the empty, default, or
 * starter [Modifier] that contains no elements, and [ProfileFragment] passes us a
 * [Modifier.wrapContentSize] which causes us to ignore the minimum incoming constraints taking as
 * much space as needed, and centering in the maximum incoming constraints if they are larger. We
 * use it as the `modifier` argument of our [CenterAlignedTopAppBar].
 * @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will be
 * applied by this top app bar to set up its height and colors. A scroll behavior is designed to
 * work in conjunction with a scrolled content to change the top app bar appearance as the content
 * scrolls. [ChannelNameBar] passes us the [TopAppBarDefaults.pinnedScrollBehavior] that its
 * [ConversationContent] caller passes it, and [ProfileFragment] passes us none so `null` is used.
 * We use it as the `scrollBehavior` argument of our [CenterAlignedTopAppBar]..
 * @param onNavIconPressed a lambda which we use as the `onClick` argument of the [Modifier.clickable]
 * of the [JetchatIcon] that we use as the `navigationIcon` argument of our [CenterAlignedTopAppBar].
 * [ChannelNameBar] passes us a lambda passed down the hierarchy which ends up calling the
 * [MainViewModel.openDrawer] method (which changes a state which causes the drawer to open when
 * called), and [ProfileFragment] directly passes us the [MainViewModel.openDrawer] method in a
 * lambda.
 * @param title we use this Composable lambda as the `title` argument of our [CenterAlignedTopAppBar]
 * and it uses it as the title to be displayed in the top app bar.
 * @param actions we use this Composable lambda as the `actions` argument of our [CenterAlignedTopAppBar]
 * and it uses it as the actions displayed at the end of the top app bar. These should typically be
 * [Icon]'s. The default layout here is a [Row], so icons inside will be placed horizontally.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JetchatAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        actions = actions,
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            JetchatIcon(
                contentDescription = stringResource(id = R.string.navigation_drawer_open),
                modifier = Modifier
                    .size(size = 64.dp)
                    .clickable(onClick = onNavIconPressed)
                    .padding(all = 16.dp)
            )
        }
    )
}

/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun JetchatAppBarPreview() {
    JetchatTheme {
        JetchatAppBar(title = { Text("Preview!") })
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun JetchatAppBarPreviewDark() {
    JetchatTheme(isDarkTheme = true) {
        JetchatAppBar(title = { Text("Preview!") })
    }
}
