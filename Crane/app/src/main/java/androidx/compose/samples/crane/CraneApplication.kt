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

package androidx.compose.samples.crane

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.samples.crane.util.UnsplashSizingInterceptor
import androidx.compose.samples.crane.base.ExploreSection
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.intercept.Interceptor
import dagger.hilt.android.HiltAndroidApp

/**
 * The custom [Application] for the app, it is annotated with HiltAndroidApp in order to mark it
 * as the [Application] class where the Dagger components should be generated. It implements the
 * [ImageLoaderFactory] interface which makes it the supplier of the singleton [ImageLoader] for
 * the `Coil.kt` private `newImageLoader` method to call when it needs the [ImageLoader]. The
 * [CraneApplication.newImageLoader] overload uses an [ImageLoader.Builder] to build the
 * [ImageLoader] with our custom [Interceptor] class [UnsplashSizingInterceptor] appended
 * to the end of the list of components, which will add query params to Unsplash urls to request
 * sized images. The [ImageLoader] is first needed by [ExploreSection] in its `ExploreImage`
 * composable when it calls [rememberAsyncImagePainter] to create the [AsyncImagePainter] that
 * it uses to download then render the [Image] that it holds.
 */
@HiltAndroidApp
class CraneApplication : Application(), ImageLoaderFactory {

    /**
     * Create the singleton [ImageLoader]. This is used by [rememberAsyncImagePainter] to load
     * images in the app thanks to some Hilt magic. We construct a [ImageLoader.Builder], call
     * its [ImageLoader.components] method to `add` our [UnsplashSizingInterceptor] custom
     * [Interceptor] to the [ImageLoader.Builder], then `build` and return the resulting
     * [ImageLoader].
     *
     * @return a new instance of [ImageLoader] which will add query params to Unsplash urls to
     * request sized images.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(UnsplashSizingInterceptor)
            }
            .build()
    }
}
