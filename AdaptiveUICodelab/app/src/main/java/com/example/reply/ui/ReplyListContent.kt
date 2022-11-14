/*
 * Copyright 2022 Google LLC
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

package com.example.reply.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.utils.DevicePosture
import com.example.reply.ui.utils.ReplyContentType

/**
 * This Composable is used when the [ReplyContentType] chosen given the size of the device is
 * [ReplyContentType.LIST_ONLY]. Devices sized as [WindowWidthSizeClass.Compact] (most phones),
 * and devices sized as [WindowWidthSizeClass.Medium] that are not [DevicePosture.BookPosture] or
 * [DevicePosture.Separating] devices use this Composable to display their [Email] list.
 *
 * The root Composable is a [LazyColumn] whose `modifier` argument is our [Modifier] parameter
 * [modifier]. The top `item` in the [LazyColumn] is a [ReplySearchBar] Composable whose `modifier`
 * argument is a [Modifier.fillMaxWidth] to have it fill the [Constraints.maxWidth] of the incoming
 * [Constraints]. This is followed by an [items] whose `items` argument is the [ReplyHomeUIState.emails]
 * field of our [replyHomeUIState] parameter which display each of the [Email] objects in the [List]
 * of [Email] in a [ReplyEmailListItem] whose `modifier` argument uses a [Modifier.padding] to set
 * the `horizontal` padding to 16.dp, and the `vertical` padding to 4.dp, and whose `email` argument
 * is the particular [Email] object int the [List] that the [ReplyEmailListItem] is supposed to
 * display.
 *
 * @param replyHomeUIState the [ReplyHomeUIState] whose [ReplyHomeUIState.emails] field contains the
 * [List] of [Email] that we should display.
 * @param modifier a [Modifier] that our caller can use to modify our appearance and/or behavior.
 * Our [ReplyAppContent] caller calls us with a `ColumnScope` `Modifier.weight` of 1f to have us
 * use all space available in the [Column] of [ReplyAppContent] we are in once our siblings are
 * measured and placed.
 */
@Composable
fun ReplyListOnlyContent(
    replyHomeUIState: ReplyHomeUIState,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            ReplySearchBar(modifier = Modifier.fillMaxWidth())
        }
        items(items = replyHomeUIState.emails) { email: Email ->
            ReplyEmailListItem(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                email = email
            )
        }
    }
}

/**
 * This Composable is used when the [ReplyContentType] chosen given the size of the device is
 * [ReplyContentType.LIST_AND_DETAIL]. Devices sized as [WindowWidthSizeClass.Expanded] (the majority
 * of tablets in landscape and large unfolded inner displays in landscape) or when the size of the
 * device is [WindowWidthSizeClass.Medium] and the [DevicePosture] is [DevicePosture.BookPosture] or
 * [DevicePosture.Separating] (the majority of tablets in portrait and large unfolded inner displays
 * in portrait). It displays both the [List] of [Email] objects in the [ReplyHomeUIState.emails] that
 * [ReplyListOnlyContent] displays, and the [List] of [Email] objects in the [Email.threads] field
 * of the [Email] whose index in the [ReplyHomeUIState.emails] list is [selectedItemIndex].
 *
 * The root Composable is a [Row] whose `modifier` argument is our [Modifier] parameter [modifier],
 * and whose `horizontalArrangement` argument uses a [Arrangement.spacedBy] to place its children
 * with a `space` of 12.dp between them. The `content` of the [Row] consists of two [LazyColumn],
 * both of which add a `RowScope` `Modifier.weight` of 1f to our [Modifier] parameter [modifier] so
 * that they share the [Row] equally. The first displays all of the [Email] objects in the [List]
 * of [Email] field [ReplyHomeUIState.emails] of [replyHomeUIState] using a [ReplyEmailListItem]
 * Composable to display each [Email] and the second displays all of the [Email] objects in the
 * [List] of [Email] field [Email.threads] of the [Email] at index [selectedItemIndex] in the [List]
 * of [Email] field [ReplyHomeUIState.emails] of [replyHomeUIState] using a [ReplyEmailThreadItem].
 * The `modifier` argument of both the [ReplyEmailListItem] and [ReplyEmailThreadItem] Composables
 * is a [Modifier.padding] which adds 16.dp to the `horizontal` (left and right edges) and 4.dp to
 * the `vertical` (top and bottom edges) padding.
 *
 * @param replyHomeUIState the [ReplyHomeUIState] which holds the [List] of [Email] we are interested
 * in in its [ReplyHomeUIState.emails] field.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [ReplyAppContent] caller passes a `ColumnScope` `Modifier.weight` of 1f, which will
 * cause us to occupy all the space left after any unweighted siblings are measured and placed.
 * @param selectedItemIndex the index of the [Email] in our [List] of [Email] that should be considered
 * to be selected for the purpose of displaying the [List] of [Email] in its [Email.threads] field.
 * Our [ReplyAppContent] caller does not pass a value so this is always the default of 0.
 */
@Composable
fun ReplyListAndDetailContent(
    replyHomeUIState: ReplyHomeUIState,
    modifier: Modifier = Modifier,
    selectedItemIndex: Int = 0
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp)
    ) {
        LazyColumn(modifier = modifier.weight(weight = 1f)) {
            items(replyHomeUIState.emails) { email ->
                ReplyEmailListItem(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    email = email
                )
            }
        }
        LazyColumn(modifier = modifier.weight(weight = 1f)) {
            items(items = replyHomeUIState.emails[selectedItemIndex].threads) { email: Email ->
                ReplyEmailThreadItem(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    email = email
                )
            }
        }
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyEmailListItem(
    email: Email,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = email.sender.avatar,
                    description = email.sender.fullName,
                    modifier = Modifier.size(40.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = email.sender.firstName,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = email.createAt,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = email.subject,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyEmailThreadItem(
    email: Email,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = email.sender.avatar,
                    description = email.sender.fullName,
                    modifier = Modifier.size(40.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = email.sender.firstName,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = email.createAt,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = email.subject,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )

            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Text(
                        text = stringResource(id = R.string.reply),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Text(
                        text = stringResource(id = R.string.reply_all),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


/**
 *
 */
@Composable
fun ReplyProfileImage(
    drawableResource: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.clip(CircleShape),
        painter = painterResource(id = drawableResource),
        contentDescription = description,
    )
}

/**
 *
 */
@Composable
fun ReplySearchBar(modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(MaterialTheme.colorScheme.surface, CircleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(id = R.string.search),
            modifier = Modifier.padding(start = 16.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(text = stringResource(id = R.string.search_replies),
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        ReplyProfileImage(
            drawableResource = R.drawable.avatar_6,
            description = stringResource(id = R.string.profile),
            modifier = Modifier
                .padding(12.dp)
                .size(32.dp)
        )
    }
}