/*
 * Copyright 2023 The Android Open Source Project
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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImagePainter.State.Error
import coil.compose.AsyncImagePainter.State.Loading
import coil.compose.rememberAsyncImagePainter
import com.google.samples.apps.nowinandroid.core.designsystem.R
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LocalTintTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.TintTheme

/**
 * A wrapper around [AsyncImage] and [rememberAsyncImagePainter] that loads an image from a URL,
 * applying a tint based on the current [LocalTintTheme] and displaying a placeholder
 * and loading indicator.
 *
 * We start by initializing our [Color] variable `iconTint` with the `current` value of the
 * [TintTheme.iconTint] of the [LocalTintTheme]. We initialize and remember our [MutableState]
 * wrapped [Boolean] variable `isLoading` to an initial value of `true`. We initialize and remember
 * our [MutableState] wrapped [Boolean] variable `isError` to an initial value of `false`.
 * We initialize and remember our [AsyncImagePainter] variable `imageLoader` using our [String]
 * parameter [imageUrl] as the `model` argument and a lambda that updates our `isLoading` and
 * `isError` variables when the state changes as its `onState` argument.
 *
 * We initialize our [Boolean] variable `isLocalInspection` with the value of the `current`
 * [LocalInspectionMode] (`true` if the composition is composed inside a Inspectable component).
 * Then our root composable is a [Box] whose `modifier` is our [Modifier] parameter [modifier],
 * and whose `contentAlignment` is [Alignment.Center]. In its [BoxScope] `content` composable lambda
 * argument if `isLoading` is `true` and `isLocalInspection` is `false` we display a
 * [CircularProgressIndicator] whose `modifier` argument is a [BoxScope.align] whose `alignment` is
 * [Alignment.Center], chained to a [Modifier.size] whose `size` is `80.dp`' The `color` argument of
 * the [CircularProgressIndicator] is the [ColorScheme.tertiary] of our custom
 * [MaterialTheme.colorScheme].
 *
 * In any case we then compose an [Image] whose `contentScale` is [ContentScale.Crop], `painter`
 * is either the [AsyncImagePainter] variable `imageLoader` if `isError` is `false` and
 * `isLocalInspection` is `false` or [placeholder] if not, whose `contentDescription` argument is our
 * [String] parameter [contentDescription]. The `colorFilter` argument is a [ColorFilter.tint]
 * whose `color` is the [Color] variable `iconTint` if it is not [Unspecified] or `null` if it is.
 *
 * @param imageUrl The URL of the image to load.
 * @param contentDescription Text used by accessibility services to describe what this image
 * represents. This should always be provided unless this image is used for decorative purposes,
 * and does not represent a meaningful action that a user can take.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content (ex.
 * background)
 * @param placeholder A [Painter] that is displayed while the image is loading or if loading fails.
 * Defaults to a generic placeholder image.
 */
@Composable
fun DynamicAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter = painterResource(id = R.drawable.core_designsystem_ic_placeholder_default),
) {
    val iconTint: Color = LocalTintTheme.current.iconTint
    var isLoading: Boolean by remember { mutableStateOf(value = true) }
    var isError: Boolean by remember { mutableStateOf(value = false) }
    val imageLoader: AsyncImagePainter = rememberAsyncImagePainter(
        model = imageUrl,
        onState = { state: AsyncImagePainter.State ->
            isLoading = state is Loading
            isError = state is Error
        },
    )
    val isLocalInspection: Boolean = LocalInspectionMode.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading && !isLocalInspection) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .size(size = 80.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        Image(
            contentScale = ContentScale.Crop,
            painter = if (isError.not() && !isLocalInspection) imageLoader else placeholder,
            contentDescription = contentDescription,
            colorFilter = if (iconTint != Unspecified) ColorFilter.tint(color = iconTint) else null,
        )
    }
}
