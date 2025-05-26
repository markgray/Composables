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

package com.example.owl.ui.common

/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.owl.ui.theme.BlueTheme
import com.example.owl.ui.utils.NetworkImage

/**
 * A circular image with a white outline.
 *
 * Our root composable is a [Box] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.background] whose `color` argument is our [Color] parameter [outlineColor],
 * and whose `shape` argument is a [CircleShape]. In its [BoxScope] `content` composable lambda
 * argument, we compose a [NetworkImage] whose arguments are:
 *  - `url`: is our [String] parameter [url].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.padding] that adds our [Dp] parameter [outlineColor] padding to
 *  `all` sides`, chained to a [Modifier.fillMaxSize] and a [Modifier.clip] whose `shape` argument
 *  is a [CircleShape].
 *
 * @param url The url of the image to display.
 * @param modifier The modifier to apply to this layout node.
 * @param outlineSize The size of the outline.
 * @param outlineColor The color of the outline.
 */
@Composable
fun OutlinedAvatar(
    url: String,
    modifier: Modifier = Modifier,
    outlineSize: Dp = 3.dp,
    outlineColor: Color = MaterialTheme.colors.surface
) {
    Box(
        modifier = modifier.background(
            color = outlineColor,
            shape = CircleShape
        )
    ) {
        NetworkImage(
            url = url,
            contentDescription = null,
            modifier = Modifier
                .padding(all = outlineSize)
                .fillMaxSize()
                .clip(shape = CircleShape)
        )
    }
}

/**
 * Preview of [OutlinedAvatar]
 */
@Preview(
    name = "Outlined Avatar",
    widthDp = 40,
    heightDp = 40
)
@Composable
private fun OutlinedAvatarPreview() {
    BlueTheme {
        OutlinedAvatar(url = "")
    }
}
