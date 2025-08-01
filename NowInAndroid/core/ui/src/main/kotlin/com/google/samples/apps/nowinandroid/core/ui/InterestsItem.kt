/*
 * Copyright 2024 The Android Open Source Project
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

@file:Suppress("RedundantValueArgument")

package com.google.samples.apps.nowinandroid.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.component.DynamicAsyncImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaIconToggleButton
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.ui.R.string

/**
 * Displays an interest item in a [ListItem] with its name, description, image, and follow button.
 *
 * @param name The name of the interest.
 * @param following Whether the user is currently following this interest.
 * @param topicImageUrl The URL of the image for this interest.
 * @param onClick The callback invoked when this item is clicked.
 * @param onFollowButtonClick The callback invoked when the follow button is clicked.
 * @param modifier The modifier to be applied to the item.
 * @param iconModifier The modifier to be applied to the interest icon.
 * @param description The description of the interest.
 * @param isSelected Whether this item is currently selected.
 */
@Composable
fun InterestsItem(
    name: String,
    following: Boolean,
    topicImageUrl: String,
    onClick: () -> Unit,
    onFollowButtonClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    description: String = "",
    isSelected: Boolean = false,
) {
    ListItem(
        leadingContent = {
            InterestsIcon(topicImageUrl = topicImageUrl, modifier = iconModifier.size(size = 48.dp))
        },
        headlineContent = {
            Text(text = name)
        },
        supportingContent = {
            Text(text = description)
        },
        trailingContent = {
            NiaIconToggleButton(
                checked = following,
                onCheckedChange = onFollowButtonClick,
                icon = {
                    Icon(
                        imageVector = NiaIcons.Add,
                        contentDescription = stringResource(
                            id = string.core_ui_interests_card_follow_button_content_desc,
                        ),
                    )
                },
                checkedIcon = {
                    Icon(
                        imageVector = NiaIcons.Check,
                        contentDescription = stringResource(
                            id = string.core_ui_interests_card_unfollow_button_content_desc,
                        ),
                    )
                },
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                Color.Transparent
            },
        ),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                selected = isSelected
            }
            .clickable(enabled = true, onClick = onClick),
    )
}

/**
 * Displays an icon for an interest, using either a dynamic image if `topicImageUrl` is provided,
 * or a default icon if `topicImageUrl` is empty.
 *
 * @param topicImageUrl The URL of the image for the interest. If empty, a default icon is shown.
 * @param modifier The modifier to be applied to the icon.
 */
@Composable
private fun InterestsIcon(topicImageUrl: String, modifier: Modifier = Modifier) {
    if (topicImageUrl.isEmpty()) {
        Icon(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(all = 4.dp),
            imageVector = NiaIcons.Person,
            // decorative image
            contentDescription = null,
        )
    } else {
        DynamicAsyncImage(
            imageUrl = topicImageUrl,
            contentDescription = null,
            modifier = modifier,
        )
    }
}

/**
 * Preview of the [InterestsItem] composable.
 */
@Preview
@Composable
private fun InterestsCardPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "Description",
                following = false,
                topicImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

/**
 * Preview of the [InterestsItem] composable with a long name.
 */
@Preview
@Composable
private fun InterestsCardLongNamePreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "This is a very very very very long name",
                description = "Description",
                following = true,
                topicImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

/**
 * Preview of the [InterestsItem] composable with a long description.
 */
@Preview
@Composable
private fun InterestsCardLongDescriptionPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "This is a very very very very very very very " +
                    "very very very long description",
                following = false,
                topicImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

/**
 * Preview of the [InterestsItem] composable with an empty description.
 */
@Preview
@Composable
private fun InterestsCardWithEmptyDescriptionPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "",
                following = true,
                topicImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

/**
 * Preview of the [InterestsItem] composable when it is selected.
 */
@Preview
@Composable
private fun InterestsCardSelectedPreview() {
    NiaTheme {
        Surface {
            InterestsItem(
                name = "Compose",
                description = "",
                following = true,
                topicImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
                isSelected = true,
            )
        }
    }
}
