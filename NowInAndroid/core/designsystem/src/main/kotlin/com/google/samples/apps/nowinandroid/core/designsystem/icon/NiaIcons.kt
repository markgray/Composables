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

package com.google.samples.apps.nowinandroid.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Grid3x3
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Upcoming
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Now in Android icons. Material icons are [ImageVector]s, custom icons are drawable resource IDs.
 */
object NiaIcons {
    val Add: ImageVector = Icons.Rounded.Add
    val ArrowBack: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack
    val Bookmark: ImageVector = Icons.Rounded.Bookmark
    val BookmarkBorder: ImageVector = Icons.Rounded.BookmarkBorder
    val Bookmarks: ImageVector = Icons.Rounded.Bookmarks
    val BookmarksBorder: ImageVector = Icons.Outlined.Bookmarks
    val Check: ImageVector = Icons.Rounded.Check
    val Close: ImageVector = Icons.Rounded.Close
    val Grid3x3: ImageVector = Icons.Rounded.Grid3x3
    val MoreVert: ImageVector = Icons.Default.MoreVert
    val Person: ImageVector = Icons.Rounded.Person
    val Search: ImageVector = Icons.Rounded.Search
    val Settings: ImageVector = Icons.Rounded.Settings
    val ShortText: ImageVector = Icons.AutoMirrored.Rounded.ShortText
    val Upcoming: ImageVector = Icons.Rounded.Upcoming
    val UpcomingBorder: ImageVector = Icons.Outlined.Upcoming
    val ViewDay: ImageVector = Icons.Rounded.ViewDay
}
