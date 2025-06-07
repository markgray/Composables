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

package com.example.owl.ui.fakes

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import coil.Coil
import coil.ComponentRegistry
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.Decoder
import coil.disk.DiskCache
import coil.fetch.Fetcher
import coil.map.Mapper
import coil.memory.MemoryCache
import coil.request.DefaultRequestOptions
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import kotlinx.coroutines.CompletableDeferred

/**
 * Replaces all remote images with a simple black drawable to make testing faster and hermetic.
 */
fun installTestImageLoader() {
    // Replace the singleton ImageLoader.
    Coil.setImageLoader(TestImageLoader())
}

/**
 * Copied from: https://coil-kt.github.io/coil/image_loaders/
 */
private class TestImageLoader : ImageLoader {

    /**
     * The default options that are used to fill in unset [ImageRequest] values.
     */
    override val defaults = DefaultRequestOptions()

    /**
     * A map of available [Mapper]s, [Fetcher]s, and [Decoder]s.
     */
    override val components = ComponentRegistry()

    /**
     * The [MemoryCache] that will be used to store and fetch bitmaps.
     */
    override val memoryCache: MemoryCache? get() = null

    /**
     * This is intentionally null, as we don't want to write to the disk cache in tests.
     */
    override val diskCache: DiskCache? get() = null

    /**
     * Enqueue the [request] to be executed.
     *
     * We call the [Target] `onStart` method of the [ImageRequest.target] of our [ImageRequest]
     * parameter [request] with its `placeholder` argument the [ImageRequest.placeholder] property
     * of our [ImageRequest] parameter [request] (this is called to start the request). Then we
     * initialize our [ColorDrawable] variable `result` to a new instance whose `color` is
     * [Color.BLACK]. We call the [Target] `onSuccess` method of the [ImageRequest.target] of our
     * [ImageRequest] parameter [request] with its `result` argument our [ColorDrawable] variable
     * `result` (this is called if the request completes successfully).
     *
     * Finally we return a [Disposable] `object` whose [Disposable.job] is a [CompletableDeferred]
     * whose `value` is a [newResult] whose `request` is our [ImageRequest] parameter [request]
     * and whose `drawable` is our [ColorDrawable] variable `result`. The [Disposable.isDisposed]
     * property always returns `true` (this disposable has been disposed), and the
     * [Disposable.dispose] is a do-nothing method (we do not have any resources that need to be
     * freed up).
     *
     * @param request The request to execute.
     * @return A [Disposable] which can be used to cancel or check the status of the request.
     */
    override fun enqueue(request: ImageRequest): Disposable {
        // Always call onStart before onSuccess.
        request.target?.onStart(placeholder = request.placeholder)
        val result = ColorDrawable(Color.BLACK)
        request.target?.onSuccess(result = result)
        return object : Disposable {
            /**
             * The [CompletableDeferred] that is completed when the image request finishes.
             */
            override val job = CompletableDeferred(
                value = newResult(
                    request = request,
                    drawable = result
                )
            )

            /**
             * Returns `true` if this disposable has been disposed.
             */
            override val isDisposed get() = true

            /**
             * Dispose the request to free up any resources that are no longer needed.
             */
            override fun dispose() {}
        }
    }

    /**
     * Execute the [request] to be executed.
     *
     * @param request The request to execute.
     * @return A [ImageResult] representing the result of the image request.
     */
    override suspend fun execute(request: ImageRequest): ImageResult {
        return newResult(request, ColorDrawable(Color.BLACK))
    }

    /**
     * Create a new [SuccessResult].
     *
     * @param request The [ImageRequest] that was executed.
     * @param drawable The success [Drawable].
     * @return A new [SuccessResult].
     */
    private fun newResult(request: ImageRequest, drawable: Drawable): SuccessResult {
        return SuccessResult(
            drawable = drawable,
            request = request,
            dataSource = DataSource.MEMORY_CACHE
        )
    }

    /**
     * This is not supported in tests.
     */
    override fun newBuilder() = throw UnsupportedOperationException()

    /**
     * Shut down this image loader and release any resources that were previously held.
     * This will also cancel any in-progress requests.
     */
    override fun shutdown() {}
}
