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

package com.example.jetnews.ui.interests

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.ui.theme.JetnewsTheme

/**
 * TODO: Add kdoc
 */
@Composable
fun SelectTopicButton(
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val icon: ImageVector = if (selected) Icons.Filled.Done else Icons.Filled.Add
    val iconColor: Color = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }
    val borderColor: Color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    }
    val backgroundColor: Color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        border = BorderStroke(width = 1.dp, color = borderColor),
        modifier = modifier.size(width = 36.dp, height = 36.dp)
    ) {
        Image(
            imageVector = icon,
            colorFilter = ColorFilter.tint(color = iconColor),
            modifier = Modifier.padding(all = 8.dp),
            contentDescription = null // toggleable at higher level
        )
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(name = "Off")
@Preview(name = "Off (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SelectTopicButtonPreviewOff() {
    SelectTopicButtonPreviewTemplate(
        selected = false
    )
}

/**
 * TODO: Add kdoc
 */
@Preview(name = "On")
@Preview(name = "On (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SelectTopicButtonPreviewOn() {
    SelectTopicButtonPreviewTemplate(
        selected = true
    )
}

/**
 * TODO: Add kdoc
 */
@Composable
private fun SelectTopicButtonPreviewTemplate(
    selected: Boolean
) {
    JetnewsTheme {
        Surface {
            SelectTopicButton(
                modifier = Modifier.padding(all = 32.dp),
                selected = selected
            )
        }
    }
}
