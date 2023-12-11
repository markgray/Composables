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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.data.Account
import com.example.reply.ui.MainActivity
import com.example.reply.ui.ReplyEmailList
import com.example.reply.ui.ReplyHomeViewModel

/**
 * This Composable is used to display each [Email] in the [List] of [Email] passed to the [ReplyEmailList]
 * Composable in its [LazyColumn]. Its root Composable is a [Card] whose `modifier` argument is a
 * [Modifier.padding] chained to our [modifier] parameter that adds 16.dp to the  horizontal dp space
 * along the left and right edges of the content, and 5.dp to the vertical dp space along the top and
 * bottom edge, with a [Modifier.semantics] that adds the semantics key/value pair to the layout node
 * `selected` = [isSelected] for use in accessibility, and ending with a [Modifier.clickable] that
 * calls our [navigateToDetail] lambda parameter with the [Email.id] of our [Email] parameter [email].
 * Its `colors` argument starts with the [CardColors] that represents the default container and content
 * colors used for a [Card] and changes the `containerColor` if the [Email.isImportant] field of [email]
 * is `true` to the [ColorScheme.secondaryContainer] color of our [MaterialTheme] (Color(0xFFFBDEBC),
 * a light orange for our [lightColorScheme], and Color(0xFF56442A) a brown for our [darkColorScheme])
 * and if it is `false` it changes it to the [ColorScheme.surfaceVariant] color of our [MaterialTheme]
 * (Color(0xFFF0E0CF) an even lighter orange for our [lightColorScheme], and Color(0xFF4F4539) an even
 * darker brown for our [darkColorScheme]). The `content` of the [Card] is a [Column] whose `modifier`
 * argument is a [Modifier.fillMaxWidth] that causes it to take up the entire incoming width constraint
 * with a [Modifier.padding] chained to that that add 20.dp to all of its sides. The `content` of the
 * [Column] is a [Row] followed by two [Text] widgets:
 *
 *  - The [Row] uses a [Modifier.fillMaxWidth] as its `modifier` argument to have it take up its entire
 *  incoming width constraint, and its `content` is a [ReplyProfileImage] displaying the `drawableResource`
 *  in the [Account.avatar] of the [Email.sender] field of [email], and whose `description` is the
 *  [Account.fullName] property of the [Email.sender] field of [email]. Next in the [Row] is a [Column]
 *  whose [RowScope] `Modifier.weight` of 1f causes it to take up all remaining space after its siblings
 *  are measured and placed to which is chained a [Modifier.padding] that adds 12.dp to the horizontal
 *  dp space along the left and right edges of the content, and 4.dp to the vertical dp space along the
 *  top and bottom edge. The `verticalArrangement` argument is [Arrangement.Center] to center its children.
 *  The `content` of the [Column] is two [Text] widgets, the first displaying the [Account.firstName]
 *  of the [Email.sender] of [email] using as its `style` the [Typography.labelMedium] of our custom
 *  [MaterialTheme.typography], and the second [Text] displaying the [Email.createdAt] field of [email]
 *  using the `style` [Typography.labelMedium] of our custom [MaterialTheme.typography], and as its
 *  `color` the [ColorScheme.onSurfaceVariant] color of our [MaterialTheme]. Next in the [Row] is an
 *  [IconButton] whose `modifier` is a [Modifier.clip] that clips its content to a [CircleShape] with
 *  the content an [Icon] displaying the `imageVector` [Icons.Filled.StarBorder] (`Icons.Default` is
 *  an alias for [Icons.Filled]).
 *
 *  - The first [Text] in the outer [Column] displays the [Email.subject] field of [email], using the
 *  `style` [Typography.titleLarge] of our custom [MaterialTheme.typography], with its `modifier`
 *  a [Modifier.padding] that adds 12.dp to its `top`, and 8.dp to its `bottom`.
 *
 *  - The second [Text] in the outer [Column] displays the [Email.body] field of [email], using the
 *  `style` [Typography.bodyMedium] of our custom [MaterialTheme.typography], with its `maxLines`
 *  set to 2 (maximum number of lines for the text to span, wrapping if necessary), its `overflow`
 *  is [TextOverflow.Ellipsis] (how visual overflow should be handled: uses an ellipsis to indicate
 *  that the text has overflowed), and its `color` is the [ColorScheme.onSurfaceVariant] color of
 *  our [MaterialTheme].
 *
 * @param email the [Email] whose information we are to display.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller does not use one so the empty, default, or starter [Modifier] that contains
 * no elements is used.
 * @param isSelected a [Boolean] flag indicating that our [Email.id] is the same as the selected
 * [Email]. Our caller does not have a selected [Email] so this is always `false`.
 * @param navigateToDetail a lambda we should call with the [Email.id] of [Email] when our [Card] is
 * clicked. This eventually resolves to to a call of the [ReplyHomeViewModel.setSelectedEmail] method
 * way back in [MainActivity].
 */
@Composable
fun ReplyEmailListItem(
    email: Email,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    navigateToDetail: (Long) -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .semantics { selected = isSelected }
            .clickable { navigateToDetail(email.id) },
        colors = CardDefaults.cardColors(
            containerColor = if (email.isImportant) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
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
                        text = email.createdAt,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = { /*Click Implementation*/ },
                    modifier = Modifier.clip(shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = stringResource(id = R.string.description_favorite),
                    )
                }
            }

            Text(
                text = email.subject,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Text(
                text = email.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
