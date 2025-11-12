/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.example.composemail.ui.viewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Forward
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A toolbar composable for mail-specific actions.
 *
 * This toolbar is displayed as a floating [Surface] and contains buttons for common mail
 * actions like "Reply", "Forward", "Archive", and "Close".
 *
 * @param modifier The modifier to be applied to the toolbar's root [Surface].
 * @param onCloseMail A lambda function to be invoked when the "Close" button is clicked.
 */
@Composable
fun MailToolbar(
    modifier: Modifier,
    onCloseMail: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(size = 8.dp),
        color = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(all = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            VectorButton(
                modifier = Modifier,
                imageVector = Icons.AutoMirrored.Filled.Reply,
                onClick = {}
            )
            VectorButton(
                modifier = Modifier,
                imageVector = Icons.AutoMirrored.Filled.Forward,
                onClick = {}
            )
            VectorButton(
                modifier = Modifier,
                imageVector = Icons.Default.Archive,
                onClick = {}
            )
            VectorButton(
                modifier = Modifier,
                imageVector = Icons.Default.Close,
                onClick = onCloseMail
            )
        }
    }
}

/**
 * A clickable icon button built around an [Image] composable.
 *
 * This composable displays an [ImageVector] with a ripple effect on click and applies a tint
 * based on the [LocalContentColor]. It is designed to be a simple, reusable button component
 * for toolbar actions.
 *
 * @param modifier The modifier to be applied to the button's root `Image`.
 * @param imageVector The [ImageVector] to display inside the button.
 * @param contentDescription Text used by accessibility services to describe what this icon represents.
 * @param onClick The lambda to be executed when the button is clicked.
 */
@Composable
private fun VectorButton(
    modifier: Modifier,
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    Image(
        modifier = modifier
            .defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
            .clip(shape = RoundedCornerShape(size = 8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .padding(all = 4.dp),
        imageVector = imageVector,
        contentDescription = contentDescription,
        colorFilter = ColorFilter.tint(color = LocalContentColor.current)
    )
}

/**
 * A preview composable for displaying the [MailToolbar].
 *
 * This preview places the [MailToolbar] within a [Column] aligned to the end (right side)
 * of the screen to simulate its typical placement in a user interface.
 */
@Preview
@Composable
private fun MailToolbarPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp),
        horizontalAlignment = Alignment.End
    ) {
        MailToolbar(modifier = Modifier) {
            // Do nothing
        }
    }
}