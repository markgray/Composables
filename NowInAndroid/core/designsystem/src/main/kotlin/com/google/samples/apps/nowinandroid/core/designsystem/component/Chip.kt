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

package com.google.samples.apps.nowinandroid.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android filter chip with included leading checked icon as well as text content slot.
 *
 * Our root composable is a [FilterChip] whose arguments are:
 *  - `selected`: is our [Boolean] parameter [selected].
 *  - `onClick`: is a lambda that calls our [onSelectedChange] lambda parameter with the inverse of
 *  our [selected] parameter.
 *  - `label`: is a lambda that wraps our [label] composable lambda parameter in a [ProvideTextStyle]
 *  whose `value` is the [Typography.labelSmall] of our custom [MaterialTheme.typography].
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `leadingIcon`: if our [Boolean] parameter [selected] is `true`, we compose an [Icon] whose
 *  `imageVector` argument is the [ImageVector] drawn by [NiaIcons.Check] (a checkmark) and its
 *  `contentDescription` is `null`, if our [Boolean] parameter [selected] is `false`, we don't
 *  compose anything.
 *  - `shape`: is a [CircleShape].
 *  - `border`: is a [FilterChipDefaults.filterChipBorder] whose `enabled` argument is our [Boolean]
 *  parameter [enabled], whose `selected` argument is our [Boolean] parameter [selected],
 *  whose `borderColor` argument is the [ColorScheme.onBackground] of our custom
 *  [MaterialTheme.colorScheme], whose `selectedBorderColor` argument is the [ColorScheme.onBackground]
 *  of our custom [MaterialTheme.colorScheme], whose `disabledBorderColor` argument is a copy of the
 *  [ColorScheme.onBackground] of our custom [MaterialTheme.colorScheme] with its `alpha` value
 *  [NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA], whose `disabledSelectedBorderColor` argument is
 *  a copy of the [ColorScheme.onBackground] of our custom [MaterialTheme.colorScheme] with its
 *  `alpha` value [NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA], and whose `selectedBorderWidth`
 *  argument is [NiaChipDefaults.ChipBorderWidth].
 *  - `colors`: is a [FilterChipDefaults.filterChipColors] whose `labelColor` argument is the
 *  [ColorScheme.onBackground] of our custom [MaterialTheme.colorScheme], whose `iconColor` argument
 *  is the [ColorScheme.onBackground] of our custom [MaterialTheme.colorScheme], whose
 *  `disabledContainerColor` argument is a copy of the [ColorScheme.onBackground] of our custom
 *  [MaterialTheme.colorScheme] with its `alpha` value [NiaChipDefaults.DISABLED_CHIP_CONTAINER_ALPHA]
 *  if our [Boolean] parameter [selected] is `true`, or [Color.Transparent] if it is `false`,
 *  whose `disabledLabelColor` argument is a copy of the [ColorScheme.onBackground] of our custom
 *  [MaterialTheme.colorScheme] with its `alpha` value [NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA],
 *  whose `disabledLeadingIconColor` argument is a copy of the [ColorScheme.onBackground] of our
 *  custom [MaterialTheme.colorScheme] with its `alpha` value [NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA],
 *  whose `selectedContainerColor` argument is the [ColorScheme.primaryContainer] of our custom
 *  [MaterialTheme.colorScheme], whose `selectedLabelColor` argument is the [ColorScheme.onBackground]
 *  of our custom [MaterialTheme.colorScheme], and whose `selectedLeadingIconColor` argument is the
 *  [ColorScheme.onBackground] of our custom [MaterialTheme.colorScheme].
 *
 * @param selected Whether the chip is currently checked.
 * @param onSelectedChange Called when the user clicks the chip and toggles checked.
 * @param modifier Modifier to be applied to the chip.
 * @param enabled Controls the enabled state of the chip. When `false`, this chip will not be
 * clickable and will appear disabled to accessibility services.
 * @param label The text label content.
 */
@Composable
fun NiaFilterChip(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = {
            ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
                label()
            }
        },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = NiaIcons.Check,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        shape = CircleShape,
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.onBackground,
            selectedBorderColor = MaterialTheme.colorScheme.onBackground,
            disabledBorderColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            disabledSelectedBorderColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            selectedBorderWidth = NiaChipDefaults.ChipBorderWidth,
        ),
        colors = FilterChipDefaults.filterChipColors(
            labelColor = MaterialTheme.colorScheme.onBackground,
            iconColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = if (selected) {
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = NiaChipDefaults.DISABLED_CHIP_CONTAINER_ALPHA,
                )
            } else {
                Color.Transparent
            },
            disabledLabelColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground.copy(
                alpha = NiaChipDefaults.DISABLED_CHIP_CONTENT_ALPHA,
            ),
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onBackground,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
        ),
    )
}

/**
 * Two Previews ("Light theme" and "Dark theme") of the [NiaFilterChip] composable. Wrapped in a
 * [NiaTheme] custom [MaterialTheme] is a [NiaBackground] Composable. The [NiaBackground] `modifier`
 * argument is a [Modifier.size] that sets its `width` to 80.dp and its `height` to 20.dp. The
 * `content` argument of the [NiaBackground] is a [NiaFilterChip] whose `selected` argument is `true`,
 * whose `onSelectedChange` argument is a do nothing lambda, and whose `label` argument is a [Text]
 * displaying the text "Chip".
 */
@ThemePreviews
@Composable
fun ChipPreview() {
    NiaTheme {
        NiaBackground(modifier = Modifier.size(width = 80.dp, height = 20.dp)) {
            NiaFilterChip(selected = true, onSelectedChange = {}) {
                Text(text = "Chip")
            }
        }
    }
}

/**
 * Now in Android chip default values.
 */
object NiaChipDefaults {
    // TODO: File bug
    // FilterChip default values aren't exposed via FilterChipDefaults
    const val DISABLED_CHIP_CONTAINER_ALPHA: Float = 0.12f
    const val DISABLED_CHIP_CONTENT_ALPHA: Float = 0.38f
    val ChipBorderWidth: Dp = 1.dp
}
