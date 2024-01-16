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

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.compose.jetchat.theme.JetchatTheme
import com.example.compose.jetchat.NavActivity

/**
 * This is used as the navigation drawer of the app in the call to `setContentView` in the `onCreate`
 * override of [NavActivity]. If consists of a [JetchatTheme] custom [MaterialTheme] wrapped
 * [ModalNavigationDrawer] whose `drawerState` argument is our [DrawerState] parameter [drawerState],
 * and whose `drawerContent` argument is a [ModalDrawerSheet] holding our [JetchatDrawerContent] which
 * uses our [onProfileClicked] lambda parameter as its `onProfileClicked` argument and our
 * [onChatClicked] parameter as its `onChatClicked` argument. The `content` argument of the
 * [ModalNavigationDrawer] is our [content] parameter. [NavActivity] calls us with an
 * [AndroidViewBinding] which holds the `View` inflated from the file layout/content_main.xml
 *
 * @param drawerState the [DrawerState] we should use for our [ModalNavigationDrawer].
 * @param onProfileClicked a lambda which should be called when a [ProfileItem] is clicked in our
 * [JetchatDrawerContent].
 * @param onChatClicked a lambda which should be called when a [ChatItem] is clicked in our
 * [JetchatDrawerContent].
 * @param content the `content` to be used by our [ModalNavigationDrawer], in our case it is an
 * [AndroidViewBinding].
 */
@Composable
fun JetchatDrawer(
    drawerState: DrawerState = rememberDrawerState(initialValue = Closed),
    onProfileClicked: (String) -> Unit,
    onChatClicked: (String) -> Unit,
    content: @Composable () -> Unit
) {
    JetchatTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    JetchatDrawerContent(
                        onProfileClicked = onProfileClicked,
                        onChatClicked = onChatClicked
                    )
                }
            },
            content = content
        )
    }
}
