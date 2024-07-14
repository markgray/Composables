/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.compose.samples.crane.util

import android.app.Application
import coil.intercept.Interceptor
import coil.request.ImageResult
import coil.size.Size
import okhttp3.HttpUrl
import coil.size.pxOrElse
import okhttp3.HttpUrl.Companion.toHttpUrl
import androidx.compose.samples.crane.CraneApplication
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.ImageRequest

/**
 * A Coil [Interceptor] which appends query params to Unsplash urls to request sized images. Our
 * [CraneApplication] custom [Application] implements the [ImageLoaderFactory] interface and Hilt
 * injects the [ImageLoader] that its [CraneApplication.newImageLoader] constructs when one is
 * needed. That factory appends this [Interceptor] to the end of the list of all the components that
 * the [ImageLoader] it constructs uses to fulfil image requests.
 */
object UnsplashSizingInterceptor : Interceptor {
    /**
     * This method intercepts requests to an [ImageLoader]'s image engine and if the [ImageRequest]
     * contains an Unsplash url appends query params to the url to request images whose width and
     * height match the [Size.width] and [Size.height] that the [ImageRequest] requested. We start
     * by initializing our [Any] variable `val data` to the [ImageRequest.data] field of the
     * [Interceptor.Chain.request] field of our parameter [chain], our [Int] variable `val widthPx`
     * to the [Size.width] in Px of the [Interceptor.Chain.size] field of [chain]. and our [Int]
     * variable `val heightPx` to the [Size.height] in Px of the [Interceptor.Chain.size] field of
     * [chain]. Then if `widthPx` is greater than 0, and `heightPx` is greater than 0, and `data` is
     * a [String] and `data` starts with the `prefix` "https://images.unsplash.com/photo-" we:
     *  - Initialize our [HttpUrl] variable `val url` by using the [toHttpUrl] extension function on
     *  `data` to create an [HttpUrl] from it, use the [HttpUrl.newBuilder] method to create a builder
     *  from that, then use the [HttpUrl.Builder.addQueryParameter] method to add the query parameter
     *  "w" with the [String] value of `widthPx` and the query parameter "h" with the [String] value
     *  of `heightPx` to the URL's query string, then use the [HttpUrl.Builder.build] method to build
     *  the [HttpUrl].
     *  - We then initialize our [ImageRequest] variable `val request` by using the
     *  [ImageRequest.newBuilder] method on the [Interceptor.Chain.request] field of [chain] to
     *  create a builder which we use to set the data to load to `url` then we use the
     *  [ImageRequest.Builder.build] method to build the [ImageRequest].
     *  - Finally we return the [ImageResult] that the [Interceptor.Chain.proceed] method returns
     *  when called with `request` as its `request` argument.
     *
     * If `data` is not a [String] or is not an Unsplash url we just return the [ImageResult] that
     * the [Interceptor.Chain.proceed] method returns when called with the [Interceptor.Chain.request]
     * of [chain] as its `request` argument.
     *
     * @param chain the [Interceptor.Chain] that is passed to us by the previous interceptors. When
     * done processing the [ImageRequest] we pass the [ImageRequest] on to the next interceptor in
     * the chain using [Interceptor.Chain.proceed] method. The [coil.intercept.EngineInterceptor]
     * is the last interceptor in the chain and will execute the [ImageRequest].
     * @return the [ImageResult] that will be returned by [coil.intercept.EngineInterceptor] which
     * represents the result of an executed [ImageRequest] (either a [coil.request.SuccessResult]
     * or a [coil.request.ErrorResult], both of which implement the [ImageResult] sealed class).
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
            return chain.proceed(request)
        }
        return chain.proceed(chain.request)
    }
}
