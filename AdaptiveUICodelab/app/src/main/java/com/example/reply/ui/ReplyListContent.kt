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

@file:Suppress("UnusedImport")

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
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Account
import com.example.reply.data.Email
import com.example.reply.ui.theme.replyDarkOutline
import com.example.reply.ui.theme.replyDarkSurface
import com.example.reply.ui.theme.replyLightOutline
import com.example.reply.ui.theme.replyLightSurface
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
 * This Composable is used to display an individual [Email] from the [List] of [Email] found in the
 * [ReplyHomeUIState.emails] field. It is used to populate a [LazyColumn] by [ReplyListOnlyContent]
 * and [ReplyListAndDetailContent]. Its root Composable is a [Card] whose `modifier` argument is our
 * [modifier] parameter. The `content` of the [Card] is a [Column] whose `modifier` argument is a
 * [Modifier.fillMaxWidth] with a [Modifier.padding] that adds 20.dp to all sides chained to it. The
 * `content` of the [Column] is a [Row] and two [Text] Composables, with the [Row] acting as a header
 * to the [Text]s which display the [Email.subject] and [Email.body] of our [Email] parameter [email].
 * The [Row] header holds a [ReplyProfileImage] which displays the [Account.avatar] drawable of the
 * [Email.sender] of [email], in a 40.dp by 40.dp [Image], followed by a [Column] holding a [Text]
 * displaying the [Account.firstName] of the [Email.sender] and a [Text] displaying the [Email.createAt]
 * date of the [email]. At the end of the [Row] is an [IconButton] with a do nothing `onClick` lambda,
 * holding a circular [Icon] that displays the `StarBorder` imageVector of [Icons.Default] (a star).
 *
 * @param email the [Email] instance that we are to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our two calling sites pass a [Modifier.padding] that adds 16.dp to the `horizontal`
 * (left and right edges) and 4.dp to the `vertical` (top and bottom edges) padding.
 */
@Composable
fun ReplyEmailListItem(
    email: Email,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = email.sender.avatar,
                    description = email.sender.fullName,
                    modifier = Modifier.size(size = 40.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(weight = 1f)
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
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.surface)
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
 * This Composable is used to display an individual [Email] from the [List] of [Email] found in the
 * [Email.threads] field of a "selected" [Email]. It is used to populate a [LazyColumn] by the
 * [ReplyListAndDetailContent] Composable. Its root Composable is a [Card] whose `modifier` argument
 * is our [modifier] parameter. The `content` of the [Card] is a [Column] whose `modifier` argument
 * is a [Modifier.fillMaxWidth] with a [Modifier.padding] that adds 20.dp to all sides chained to it.
 * The `content` of the [Column] is a [Row] and two [Text] Composables, with the [Row] acting as a
 * header to the [Text]s which display the [Email.subject] and [Email.body] of our [Email] parameter
 * [email]. The [Row] header holds a [ReplyProfileImage] which displays the [Account.avatar] drawable
 * of the [Email.sender] of [email], in a 40.dp by 40.dp [Image], followed by a [Column] holding a
 * [Text] displaying the [Account.firstName] of the [Email.sender] and a [Text] displaying the
 * [Email.createAt] date of the [email]. At the end of the [Row] is an [IconButton] with a do nothing
 * `onClick` lambda, holding a circular [Icon] that displays the `StarBorder` imageVector of [Icons.Default]
 * (a star). At the end of the [Column] is [Row] which holds a "Reply" [Button] and a "Reply All"
 * [Button].
 *
 * @param email the [Email] instance that we are to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [ReplyListAndDetailContent] caller passes a [Modifier.padding] that adds 16.dp to
 * the `horizontal` (left and right edges) and 4.dp to the `vertical` (top and bottom edges) padding.
 */
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
                .padding(all = 20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = email.sender.avatar,
                    description = email.sender.fullName,
                    modifier = Modifier.size(size = 40.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(weight = 1f)
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
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.surface)
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
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(weight = 1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Text(
                        text = stringResource(id = R.string.reply),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.weight(weight = 1f),
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
 * This Composable is used to display the drawable whose resource ID is found in the [Account.avatar]
 * field of the [Email.sender] field of an [Email]. Our root Composable is an [Image] whose `modifier`
 * argument adds a [Modifier.clip] of a [CircleShape] to our [modifier] parameter, with a `painter`
 * argument that uses a [painterResource] of our [drawableResource] parameter to draw the image, and
 * the `contentDescription` argument is our [description] parameter.
 *
 * @param drawableResource the resource ID of the jpeg we are to draw.
 * @param description the [String] we should use as the `contentDescription` of our [Image].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [ReplyEmailListItem] passes a [Modifier.size] to size us at 40.dp by 40.dp, as does
 * [ReplyEmailThreadItem]. [ReplySearchBar] passes a [Modifier.padding] that adds 12.dp to all sides
 * with a [Modifier.size] chained to it that sizes us at 32.dp by 32.dp
 */
@Composable
fun ReplyProfileImage(
    drawableResource: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.clip(shape = CircleShape),
        painter = painterResource(id = drawableResource),
        contentDescription = description,
    )
}

/**
 * This Composable renders a "fake" do nothing Search Bar and is used as the first `item` in the
 * [LazyColumn] of [ReplyListOnlyContent]. Its root Composable is a [Row] whose `modifier` argument
 * adds a [Modifier.fillMaxWidth] to our [Modifier] parameter [modifier], followed by [Modifier.padding]
 * which adds 16.dp padding to all sides, followed by a [Modifier.background] whose `color` argument
 * is the [ColorScheme.surface] color of the [MaterialTheme.colorScheme] ([replyDarkSurface] for our
 * [darkColorScheme] (the [Color] 0xFF1F1B16 a shade of black) or [replyLightSurface] for our
 * [lightColorScheme] (the [Color] 0xFFFCFCFC a shade of white)) and the `shape` used for the [Row]
 * is a [CircleShape] giving it nice rounded corners. The `verticalAlignment` argument of the [Row]
 * is [Alignment.CenterVertically] which centers its children vertically in the [Row]. The `content`
 * of the [Row] is an [Icon] whose `imageVector` displays the `Search` [ImageVector] of [Icons.Default]
 * (which is a magnifying glass), the `contentDescription` is the [String] "Search", its `modifier`
 * argument is a [Modifier.padding] that adds 16.dp padding to the `start` of the [Icon], and its
 * `tint` argument is the [ColorScheme.outline] color of the [MaterialTheme.colorScheme] ([replyDarkOutline]
 * for our [darkColorScheme] (the [Color] 0xFF9C8F80 a shade of brown) or [replyLightOutline] for our
 * [lightColorScheme] (the [Color] 0xFF817567 a shade of brown)). The [Icon] is followed by a [Text]
 * that displays the `text` "search replies" whose `modifier` argument is a `RowScope` `Modifier.weight`
 * of 1f which causes it to take all space remaining after its siblings are measured and placed, with
 * a [Modifier.padding] that adds 16.dp to all sides of the [Text]. The `style` of the [Text] is the
 * `bodyMedium` [TextStyle] of [MaterialTheme.typography] which uses a `fontWeight` of [FontWeight.Medium],
 * `fontSize` of 14.sp, `lineHeight` of 20.sp, and `letterSpacing` of 0.25.sp, and its `color` argument
 * is also the [ColorScheme.outline] color of the [MaterialTheme.colorScheme]. The last Composable in
 * the [Row] is a [ReplyProfileImage] whose `drawableResource` argument is the jpg with Resource ID
 * `R.drawable.avatar_6`, whose `description` argument is the [String] "Profile", and whose `modifier`
 * argument is a [Modifier.padding] that adds 12.dp to all sides, with a [Modifier.size] which sets
 * the size of the [ReplyProfileImage] to 32.dp
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [ReplySearchBar] caller passes [Modifier.fillMaxWidth] to have us fill the
 * [Constraints.maxWidth] of the incoming measurement constraints.
 *
 */
@Composable
fun ReplySearchBar(modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(all = 16.dp)
        .background(color = MaterialTheme.colorScheme.surface, shape = CircleShape),
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
