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

package com.example.owl

import android.app.Application
import coil.ComponentRegistry
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.compose.AsyncImage
import com.example.owl.ui.utils.UnsplashSizingInterceptor

/**
 * An [Application] that implements [ImageLoaderFactory] to provide Coil's image loading services.
 */
@Suppress("unused")
class OwlApplication : Application(), ImageLoaderFactory {

    /**
     * Create the singleton [ImageLoader]. This is used by [AsyncImage] to load images in the app.
     *
     * We create an instance of the [ImageLoader.Builder] with its `context` argument being the
     * [Application] context of the [OwlApplication] then call its [ImageLoader.Builder.components]
     * methdod and in its [ComponentRegistry.Builder] `builder` lambda argument we call the
     * [ComponentRegistry.Builder.add] method to add the [UnsplashSizingInterceptor] `interceptor`
     * to have it add query params to Unsplash urls to request sized images. Then we chain a call
     * the [ImageLoader.Builder.respectCacheHeaders] method with its `enable` argument `false` to
     * have Coil ignore the Unsplash cache headers, and finally call the [ImageLoader.Builder.build]
     * method to build the [ImageLoader] and return it.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context = this)
            .components {
                add(interceptor = UnsplashSizingInterceptor)
            }
            // Ignore the Unsplash cache headers as they set `Cache-Control:must-revalidate` which
            // requires a network operation even if the image is cached locally.
            .respectCacheHeaders(enable = false)
            .build()
    }
}
