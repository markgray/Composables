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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Account
import com.example.reply.data.Email
import com.example.reply.ui.ReplyEmailDetail

/**
 * This Composable is used by [ReplyEmailDetail] to display each [Email] in the [Email.threads] list
 * of [Email] of the [Email] it is called with in a [LazyColumn]. Its root Composable is a [Column]
 * whose `modifier` argument chains a [Modifier.fillMaxWidth] to our [Modifier] parameter [modifier]
 * to have it take up the entire incoming width constraint, which is followed by a [Modifier.padding]
 * which adds 16.dp to all sides of its `content`, with a [Modifier.background] setting its background
 * color to the [ColorScheme.surface] color of our [MaterialTheme], and its `shape` to the
 * [Shapes.medium] shape of our [MaterialTheme] (a [RoundedCornerShape] with 16.dp rounded corners),
 * and at the end of the [Modifier] chain is another [Modifier.padding] that adds another 20.dp to
 * all sides of its `content`. The `content` of the [Column] is a [Row] followed by two [Text] widgets
 * and another [Row]:
 *
 *  - The first [Row] uses a [Modifier.fillMaxWidth] as its `modifier` argument to have it take up
 *  its entire incoming width constraint, and its `content` is a [ReplyProfileImage] displaying the
 *  `drawableResource` in the [Account.avatar] of the [Email.sender] field of [email], and whose
 *  `description` is the [Account.fullName] property of the [Email.sender] field of [email]. Next in
 *  the [Row] is a [Column] whose [RowScope] `Modifier.weight` of 1f causes it to take up all
 *  remaining space after its siblings are measured and placed to which is chained a [Modifier.padding]
 *  that adds 12.dp to the `horizontal` dp space along the left and right edges of the content, and
 *  4.dp to the `vertical` dp space along the top and bottom edge. The `verticalArrangement` argument
 *  is [Arrangement.Center] to center its children. The `content` of the [Column] is two [Text]
 *  widgets, the first displaying the [Account.firstName] of the [Email.sender] of [email] using as
 *  its `style` the [Typography.labelMedium] of our custom [MaterialTheme.typography], and the second
 *  [Text] displaying the [stringResource] whose ID is `R.string.twenty_mins_ago` ("20 mins ago")
 *  using the `style` [Typography.labelMedium] of our custom [MaterialTheme.typography]. Next in the
 *  [Row] is an [IconButton] whose `modifier` is a [Modifier.clip] that clips its content to a
 *  [CircleShape] with a [Modifier.background] that sets its `color` to [ColorScheme.surface] color
 *  of our [MaterialTheme], and its `content` is an [Icon] which displays [Icons.Filled.Star] if the
 *  [Email.isStarred] field of [email] is `true` or [Icons.Filled.StarBorder] if it is `false`, using
 *  as its `contentDescription` the [String] with resource ID `R.string.description_favorite`
 *  ("Favorite"), and as its `tint` the [ColorScheme.secondary] of our [MaterialTheme] if the
 *  [Email.isStarred] field of [email] is `true` or [ColorScheme.outline] if it is `false`.
 *
 *  - The first [Text] displays as its `text` the [Email.subject] field of [email], using the `style`
 *  [Typography.bodyMedium] of our custom [MaterialTheme.typography], with its `modifier` a
 *  [Modifier.padding] that adds 12.dp to its `top` and 8.dp to its `bottom`.
 *
 *  -The second [Text] displays as its `text` the [Email.body] field of [email], using the `style`
 *  [Typography.bodyLarge] of our custom [MaterialTheme.typography], with its `color` the
 *  [ColorScheme.onSurfaceVariant] of our [MaterialTheme].
 *
 *  - The bottom [Row] uses as its `modifier` argument a [Modifier.fillMaxWidth] to have it occupy
 *  its entire incoming width constraint, with a [Modifier.padding] that adds 20.dp to its vertical
 *  dp space along the top and bottom edges, and its `horizontalArrangement` uses a [Arrangement.spacedBy]
 *  to have its children spaced with 4.dp between them. Its `content` is two [Button] widgets, both
 *  of which use a [RowScope] `Modifier.weight` of 1f to have them share the available space equally.
 *  The `content` of the first uses a [Text] to display the [String] with resource ID `R.string.reply`
 *  ("Reply") and the second uses a [Text] to display the [String] with resource ID `R.string.reply_all`
 *  ("Reply All"). The `onClick` of both are "do nothing" lambdas.
 *
 * @param email the [Email] whose information we are to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller ([ReplyEmailDetail]) does not pass us one so the empty, default, or starter
 * [Modifier] that contains no elements is used instead.
 */
@Composable
fun ReplyEmailThreadItem(
    email: Email,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .background(color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
            .padding(all = 20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            ReplyProfileImage(
                drawableResource = email.sender.avatar,
                description = email.sender.fullName,
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
                    text = stringResource(id = R.string.twenty_mins_ago),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            IconButton(
                onClick = { /*Click Implementation*/ },
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = if (email.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = stringResource(id = R.string.description_favorite),
                    tint = if (email.isStarred) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                )
            }
        }

        Text(
            text = email.subject,
            style = MaterialTheme.typography.bodyMedium,
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
                onClick = { /*Click Implementation*/ },
                modifier = Modifier.weight(weight = 1f),
            ) {
                Text(
                    text = stringResource(id = R.string.reply),
                )
            }
            Button(
                onClick = { /*Click Implementation*/ },
                modifier = Modifier.weight(weight = 1f),
            ) {
                Text(
                    text = stringResource(id = R.string.reply_all),
                )
            }
        }
    }
}
