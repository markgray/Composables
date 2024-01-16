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

package com.example.compose.jetchat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.example.compose.jetchat.R

/**
 * This is used as the `navigationIcon` of the [CenterAlignedTopAppBar] used in [JetchatAppBar] and
 * at the beginning of the [Row] of [DrawerHeader]. Its `content` which layers one [Icon] over the
 * top of another [Icon]. We start by initializing our [Modifier] variable `val semantics` to a
 * [Modifier.semantics] using our [String] parameter [contentDescription] if it is not `null`, or
 * the an empty [Modifier] if it is `null`. Then our root Composable is a [Box] whose `modifier`
 * argument uses the [Modifier.then] method of our [Modifier] parameter [modifier] to append our
 * [Modifier] variable `semantics`. The `content` of the [Box] is an [Icon] displaying the drawable
 * with resource ID [R.drawable.ic_jetchat_back] using as its `tint` the [ColorScheme.primaryContainer]
 * color of our [MaterialTheme.colorScheme]. On top of this is then rendered a second [Icon] displaying
 * the drawable with resource ID [R.drawable.ic_jetchat_front] using as its `tint` the
 * [ColorScheme.primary] color of our [MaterialTheme.colorScheme].
 *
 * @param contentDescription if non-`null` a [String] to use as the `contentDescription` of our [Box].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [JetchatAppBar] passes us a [Modifier.size] that sets our size to 64.dp, with a
 * [Modifier.clickable] chained to that which calls the `onNavIconPressed` parameter of [JetchatAppBar],
 * and a [Modifier.padding] that adds 16.dp padding to all sides. [DrawerHeader] passes us a
 * [Modifier.size] that sets our size to 24.dp.
 */
@Composable
fun JetchatIcon(
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val semantics: Modifier = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        Modifier
    }
    Box(modifier = modifier.then(semantics)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_jetchat_back),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_jetchat_front),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
