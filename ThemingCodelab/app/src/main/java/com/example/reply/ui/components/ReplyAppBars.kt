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

package com.example.reply.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.MainActivity
import com.example.reply.ui.ReplyEmailDetail
import com.example.reply.ui.ReplyEmailList
import com.example.reply.ui.ReplyHomeViewModel

/**
 * This Composable is the top `item` in the [LazyColumn] displayed by the [ReplyEmailList] Composable.
 * Its root widget is a [Row] whose modifier chains a [Modifier.fillMaxWidth] to its [Modifier]
 * parameter [modifier] (to have its content fill the entire width of the incoming measurement
 * constraints), followed by [Modifier.padding] that adds 16.dp to all of its sides, and
 * ending with a [Modifier.background] whose `color` argment is the [ColorScheme.background] of our
 * custom [MaterialTheme.colorScheme], Color(0xFFFFFBFF) (a shade of white) for its [lightColorScheme]
 * and Color(0xFF1F1B16) (a shade of black) for its [darkColorScheme]. Its `shape` argument is a
 * [CircleShape] (Circular Shape with all the corners sized as the 50 percent of the shape size).
 * The `verticalAlignment` argument of the [Row] is [Alignment.CenterVertically] which centers its
 * children vertically. The `content` of the [Row] is an [Icon] displaying the [ImageVector] drawn
 * by [Icons.Filled.Search] (`Icons.Default.Search` is an alias for [Icons.Filled.Search]) which is
 * a stylized magnifying glass, the `contentDescription` argument is the [stringResource] with ID
 * `R.string.search` ("Search"), whose `modifier` argument is a [Modifier.padding] that adds 16.dp
 * to its `start`, and its `tint` is the [ColorScheme.outline] of our custom [MaterialTheme.colorScheme],
 * Color(0xFF817567) for the [lightColorScheme] (light Gray) and Color(0xFF9C8F80) for the [darkColorScheme]
 * (also a light Gray). This is followed by a [Text] whose `text` argument is the [stringResource]
 * with ID `R.string.search_replies` ("Search replies"), whose `modifier` argument is a [RowScope]
 * `Modifier.weight` whose `weight` is 1f (the [Text] will take up all available space remaining
 * after its siblings are measured and placed), chained to this is a [Modifier.padding] that adds
 * 16.dp to all sides of the [Text]. The `style` argument of the text uses as its [TextStyle] the
 * [Typography.bodyMedium] of our custom [MaterialTheme.typography] which has a `fontWeight` of
 * [FontWeight.Medium], a `fontSize` of 14.sp, a `lineHeight` of 20.sp, and a `letterSpacing` of
 * 0.25.sp. The [Color] to apply to the text is the [ColorScheme.outline] of our custom
 * [MaterialTheme.colorScheme], Color(0xFF817567) for the [lightColorScheme] (light Gray) and
 * Color(0xFF9C8F80) for the [darkColorScheme] (also a light Gray). At the end of the [Row] is
 * a [ReplyProfileImage] Composable, whose `drawableResource` is the jpg whose ID is
 * `R.drawable.avatar_6`, whose `description` is the [stringResource] with ID `R.string.profile`
 * ("Profile"), and whose `modifier` argument is a [Modifier.padding] that adds 12.dp to all sides
 * with a [Modifier.size] which sets its `size` to 32.dp.
 *
 * @param modifier a [Modifier] instance which our caller can use to modify our behavior or appearance.
 * The [LazyColumn] in [ReplyEmailList] passes us a [Modifier.fillMaxWidth] which causes us to take
 * up all of the incoming width constraint.
 */
@Composable
fun ReplySearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(id = R.string.search),
            modifier = Modifier.padding(start = 16.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = stringResource(id = R.string.search_replies),
            modifier = Modifier
                .weight(weight = 1f)
                .padding(all = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        ReplyProfileImage(
            drawableResource = R.drawable.avatar_6,
            description = stringResource(id = R.string.profile),
            modifier = Modifier
                .padding(all = 12.dp)
                .size(size = 32.dp)
        )
    }
}

/**
 * This Composable is the top `item` in the [LazyColumn] displayed by the [ReplyEmailDetail] Composable.
 * Its root Composable is a [TopAppBar] whose `modifier` argument is our [Modifier] parameter [modifier],
 * whose `title` argument is a [Column] whose `modifier` argument is a [Modifier.fillMaxWidth] which
 * causes it to occupy the entire incoming width constraint, its `horizontalAlignment` (horizontal
 * alignment of the layout's children) is [Alignment.CenterHorizontally] if our [Boolean] parameter
 * [isFullScreen] is `true` (it always is), or [Alignment.Start] if it is `false` (never in our case).
 * The `content` of the [Column] are two [Text] widgets, the first displays the [Email.subject] field
 * of our [Email] parameter [email], using as its `style` [TextStyle] argument the
 * [Typography.titleMedium] of our custom [MaterialTheme.typography], and the `color` of the text
 * uses the [ColorScheme.onSurfaceVariant] color of our [MaterialTheme.colorScheme] (Color(0xFF4F4539)
 * for our [lightColorScheme] (a shade of black) and Color(0xFFD3C4B4) for our [darkColorScheme] (a
 * shade of orange). The second [Text] uses a [Modifier.padding] of 4.dp for its `top` and displays
 * the `text` formed by concatenating [List.size] of the [Email.threads] list of our [Email] parameter
 * [email] to the [String] whose resource ID is `R.string.messages` ("Messages"). Its `style`
 * [TextStyle] argument is the [Typography.labelMedium] of our custom [MaterialTheme.typography],
 * and the `color` of the text uses the [ColorScheme.outline] color of our [MaterialTheme.colorScheme]
 * (Color(0xFF817567) for our [lightColorScheme] (a shade of Gray) and Color(0xFF9C8F80) for our
 * [darkColorScheme] (a slightly different shade of Gray). The `navigationIcon` argument of the
 * [TopAppBar] (the navigation icon displayed at the start of the top app bar) is if our [Boolean]
 * parameter [isFullScreen] is `true` (it always is) a [FilledIconButton] whose `onClick` argument
 * is our [onBackPressed] lambda parameter, whose `modifier` argument is a [Modifier.padding] that
 * added 8.dp to `all` sides, and whose `colors` argument uses the [IconButtonDefaults.filledIconButtonColors]
 * with the `containerColor` set to [ColorScheme.surface], and `contentColor` set to [ColorScheme.onSurface].
 * The `content` Composable of the [FilledIconButton] is an [Icon] displaying the `imageVector` drawn
 * by [Icons.AutoMirrored.Filled.ArrowBack] using a [Modifier.size]
 * that sets its `size` to 14.dp. The `actions` of the [TopAppBar] (actions displayed at the end of
 * the top app bar) consist of a single [IconButton] whose `onClick` is a do nothing lambda, and whose
 * `content` is an [Icon] displaying the `imageVector` drawn by [Icons.Filled.MoreVert] using the `tint`
 * of [ColorScheme.onSurfaceVariant].
 *
 * @param email the [Email] we are to use for the info we display
 * @param isFullScreen if `true` we display a [FilledIconButton] at the beginning of our [TopAppBar]
 * (it is always `true`)
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance or behavior
 * (our callers do not pass us one, so the empty, default, or starter [Modifier] that contains no
 * elements is used instead).
 * @param onBackPressed a lambda which we will call if the `navigationIcon` at the beginning of our
 * [TopAppBar] is clicked. Our caller [ReplyEmailDetail] passes us its `onBackPressed` lambda parameter
 * which eventually resolves to a call of the [ReplyHomeViewModel.closeDetailScreen] method way back
 * in [MainActivity].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailDetailAppBar(
    email: Email,
    isFullScreen: Boolean,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (isFullScreen) {
                    Alignment.CenterHorizontally
                } else {
                    Alignment.Start
                }
            ) {
                Text(
                    text = email.subject,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "${email.threads.size} ${stringResource(id = R.string.messages)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        navigationIcon = {
            if (isFullScreen) {
                FilledIconButton(
                    onClick = onBackPressed,
                    modifier = Modifier.padding(all = 8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button),
                        modifier = Modifier.size(size = 14.dp)
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { /*Click Implementation*/ },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.more_options_button),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
