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

package com.example.baselineprofiles_codelab.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * A composable that displays a quantity selector with increase and decrease buttons.
 *
 * The quantity selector shows a label ("Qty") and the current count, along with
 * buttons to increase and decrease the count.
 *
 * Our root composable is a [Row] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In its [RowScope] `content` composable lambda argument we first compose a
 * [CompositionLocalProvider] whose [ProvidedValue] `value` argument provides the [LocalContentAlpha]
 * value to be [ContentAlpha.medium] to its `content` composable lambda argument which is a [Text]
 * whose arguments are:
 *  - `text` is the string resource with id `R.string.quantity` ("Qty")
 *  - `style` is the [Typography.subtitle1] from our custom [MaterialTheme.typography]
 *  - `color` is the [JetsnackColors.textSecondary] from our custom [JetsnackTheme.colors]
 *  - `modifier` is a [Modifier.padding] that adds 18.dp padding to the end, with a
 *  [RowScope.align] whose `alignment` is [Alignment.CenterVertically]
 *
 * Next in the [Row] is a [JetsnackGradientTintedIconButton] whose arguments are:
 *  - `imageVector` is the [ImageVector] drawn by [Icons.Filled.Remove]
 *  - `onClick` is our [decreaseItemCount] lambda argument
 *  - `contentDescription` is the string resource with id `R.string.label_decrease` ("Decrease")
 *  - `modifier` is a [RowScope.align] whose `alignment` is [Alignment.CenterVertically]
 *
 * Next in the [Row] is a [Crossfade] whose `targetState` is our [count] argument, and whose
 * `modifier` argument is a [RowScope.align] whose `alignment` is [Alignment.CenterVertically].
 * And in its `content` composable lambda argument we accept the current animated [Int] value of
 * the [Crossfade] `targetState` argument in our `currentCount` variable and compose a [Text] whose
 * arguments are:
 *  - `text` is the current count `currentCount` converted to a string
 *  - `style` is the [Typography.subtitle2] from our custom [MaterialTheme.typography]
 *  - `fontSize` is 18.sp
 *  - `color` is the [JetsnackColors.textPrimary] from our custom [JetsnackTheme.colors]
 *  - `textAlign` is [TextAlign.Center]
 *  - `modifier` is a [Modifier.widthIn] whose `min` is 24.dp
 *
 * Finally in the [Row] is a [JetsnackGradientTintedIconButton] whose arguments are:
 *  - `imageVector` is the [ImageVector] drawn by [Icons.Filled.Add]
 *  - `onClick` is our [increaseItemCount] lambda argument
 *  - `contentDescription` is the string resource with id `R.string.label_increase` ("Increase")
 *  - `modifier` is a [RowScope.align] whose `alignment` is [Alignment.CenterVertically]
 *
 * @param count The current quantity.
 * @param decreaseItemCount A callback function to be invoked when the decrease button is clicked.
 * @param increaseItemCount A callback function to be invoked when the increase button is clicked.
 * @param modifier Modifier for styling and layout customization.
 */
@Composable
fun QuantitySelector(
    count: Int,
    decreaseItemCount: () -> Unit,
    increaseItemCount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        CompositionLocalProvider(value = LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = R.string.quantity),
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier
                    .padding(end = 18.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
        }
        JetsnackGradientTintedIconButton(
            imageVector = Icons.Default.Remove,
            onClick = decreaseItemCount,
            contentDescription = stringResource(id = R.string.label_decrease),
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        Crossfade(
            targetState = count,
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
        ) { currentCount: Int ->
            Text(
                text = "$currentCount",
                style = MaterialTheme.typography.subtitle2,
                fontSize = 18.sp,
                color = JetsnackTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 24.dp)
            )
        }
        JetsnackGradientTintedIconButton(
            imageVector = Icons.Default.Add,
            onClick = increaseItemCount,
            contentDescription = stringResource(id = R.string.label_increase),
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}

/**
 * Three Previews for the [QuantitySelector] composable using different configurations:
 *  - Light mode (assumed to be the "default")
 *  - Dark mode
 *  - Large font size
 */
@Preview("default")
@Preview("dark theme", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun QuantitySelectorPreview() {
    JetsnackTheme {
        JetsnackSurface {
            QuantitySelector(count = 1, decreaseItemCount = {}, increaseItemCount = {})
        }
    }
}

/**
 * Right to left preview of the [QuantitySelector] composable.
 */
@Preview("RTL")
@Composable
fun QuantitySelectorPreviewRtl() {
    JetsnackTheme {
        JetsnackSurface {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QuantitySelector(count = 1, decreaseItemCount = {}, increaseItemCount = {})
            }
        }
    }
}
