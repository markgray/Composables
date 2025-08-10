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

@file:Suppress("unused", "RedundantSuppression")

package com.example.owl.ui.utils

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.intercept.Interceptor
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.Dimension
import coil.size.Size
import coil.size.pxOrElse
import com.example.owl.R
import com.example.owl.ui.theme.compositedOnSurface
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * A wrapper around [AsyncImage], setting a default [contentScale] and showing
 * a placeholder while loading.
 *
 * Our root composable is an [AsyncImage] whose arguments are:
 *  - `model`: The URL of the image to load. We use our [String] parameter [url].
 *  - `contentDescription`: A description of the image for accessibility services. We use our [String]
 *  parameter [contentDescription].
 *  - `placeholder`: A placeholder to show while the image is loading. We use the [Painter] returned
 *  by [painterResource] for the drawable with resource ID `R.drawable.photo_architecture`. (a `webp`
 *  image of an odd looking building).
 *  - `modifier`: A modifier to apply to this layout node. We use our [Modifier] parameter [modifier].
 *  - `contentScale`: The scaling mode to apply to the image when it is loaded. We use our
 *  [ContentScale] parameter [contentScale]
 *
 * @param url The URL of the image to load.
 * @param contentDescription Text used by accessibility services to describe what this image
 * represents. This should always be provided unless this image is used for decorative purposes,
 * and does not represent a meaningful action that a user can take.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content (ex.
 * background)
 * @param contentScale Optional scale parameter used to determine the aspect ratio scaling to be used
 * if the bounds are a different size from the intrinsic size of the painter.
 * @param placeholderColor The color of the placeholder to be displayed while the image is loading.
 */
@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderColor: Color = MaterialTheme.colors.compositedOnSurface(0.2f)
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        placeholder = painterResource(id = R.drawable.photo_architecture),
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * A Coil [Interceptor] which appends query params to Unsplash urls to request sized images.
 */
object UnsplashSizingInterceptor : Interceptor {
    /**
     * Intercepts the request and appends query parameters to Unsplash URLs to request sized images.
     *
     * We start by initializing our [Any] variable `data` with the value of the [ImageRequest.data]
     * of the [Interceptor.Chain.request] of our [Interceptor.Chain] parameter [chain]. We initialize
     * our [Int] variable `widthPx` with the value of the [Size.width] of the [Interceptor.Chain.size]
     * defaulting to `-1` if it is not a [Dimension.Pixels]). We initialize our [Int] variable
     * `heightPx` with the value of the [Size.height] of the [Interceptor.Chain.size] defaulting to
     * `-1` if it is not a [Dimension.Pixels]). Then if `widthPx` is greater than `-1` and `heightPx`
     * is greater than `-1` and `data` is a [String] starting with `https://images.unsplash.com/photo-`
     * then we initialize our [HttpUrl] variable `url` with the value of the `data` converted to
     * an [HttpUrl] using the [toHttpUrl] method, then use the [HttpUrl.newBuilder] to create an
     * [HttpUrl.Builder] from it, then use the [HttpUrl.Builder.addQueryParameter] method to add
     * a query named `w` and a value of `widthPx` to it, then use the [HttpUrl.Builder.addQueryParameter]
     * method to add a query named `h` and a value of `heightPx` to it, and then call the
     * [HttpUrl.Builder.build] method to build it into an [HttpUrl]. We then initialize our
     * [ImageRequest] variable `request` by using the [ImageRequest.newBuilder] method of the
     * [Interceptor.Chain.request] of our [Interceptor.Chain] parameter [chain] to create a new
     * [ImageRequest.Builder] from it, then use the [ImageRequest.Builder.data] method to set the
     * `data` of the [ImageRequest] to `url`, and then use the [ImageRequest.Builder.build] method
     * to build it into an [ImageRequest]. Finally we call the [Interceptor.Chain.proceed] method
     * of our [Interceptor.Chain] parameter [chain] to proceed with the [ImageRequest] we just
     * built and return the result.
     *
     * On the other hand, if `widthPx` is not greater than `-1` or `heightPx` is not greater than
     * `-1` or `data` is not a [String] starting with `https://images.unsplash.com/photo-` then we
     * call the [Interceptor.Chain.proceed] method of our [Interceptor.Chain] parameter [chain] to
     * proceed with the [ImageRequest] of our [Interceptor.Chain] parameter [chain] and return
     * the result.
     *
     * @param chain The interceptor chain.
     * @return The image result.
     */
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val data: Any = chain.request.data
        val widthPx: Int = chain.size.width.pxOrElse { -1 }
        val heightPx: Int = chain.size.height.pxOrElse { -1 }
        if (widthPx > 0 && heightPx > 0 && data is String &&
            data.startsWith("https://images.unsplash.com/photo-")
        ) {
            val url: HttpUrl = data.toHttpUrl()
                .newBuilder()
                .addQueryParameter("w", widthPx.toString())
                .addQueryParameter("h", heightPx.toString())
                .build()
            val request: ImageRequest = chain.request.newBuilder().data(url).build()
            return chain.proceed(request = request)
        }
        return chain.proceed(request = chain.request)
    }
}
