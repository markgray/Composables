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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.reply.data.Account
import com.example.reply.data.Email

/**
 * This Composable is used to display the `drawable` whose resource ID is our [Int] parameter
 * [drawableResource]. It is used by the [ReplySearchBar] to display the jpg with resource ID
 * `R.drawable.avatar_6` and by both the [ReplyEmailListItem] and the [ReplyEmailThreadItem] to
 * display the [Account.avatar] of the [Email.sender] field of the [Email] whose information they
 * are displaying. Its `content` is an [Image] whose `painter` is a [painterResource] drawing the
 * drawable with the resource ID of our [Int] parameter [drawableResource], and whose `contentDescription`
 * is our [String] parameter [description]. Its `modifier` argument adds a [Modifier.size] that sizes
 * it to be 40.dp to our [Modifier] parameter [modifier], with a [Modifier.clip] appended to that which
 * clips it to the [CircleShape] `shape`.
 *
 * @param drawableResource the resource ID of the drawable that we are supposed to display.
 * @param description a [String] that we should use as the `contentDescription` of our [Image].
 * [ReplySearchBar] uses the [String] with resource ID `R.string.profile` ("Profile") and both
 * [ReplyEmailListItem] and [ReplyEmailThreadItem] use the [Account.fullName] property of the
 * [Email.sender] field of the [Email] whose information they are displaying.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [ReplySearchBar] uses a [Modifier.padding] that adds 12.dp to all sides, with a
 * [Modifier.size] which sizes us to be 32.dp, and [ReplyEmailListItem] and [ReplyEmailThreadItem]
 * do not pass us any, so the empty, default, or starter Modifier that contains no elements it used.
 */
@Composable
fun ReplyProfileImage(
    drawableResource: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .size(size = 40.dp)
            .clip(shape = CircleShape),
        painter = painterResource(id = drawableResource),
        contentDescription = description,
    )
}
