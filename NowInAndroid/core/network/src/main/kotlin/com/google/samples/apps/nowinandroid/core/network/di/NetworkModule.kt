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

package com.google.samples.apps.nowinandroid.core.network.di

import android.content.Context
import android.content.res.AssetManager
import androidx.tracing.trace
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.util.DebugLogger
import com.google.samples.apps.nowinandroid.core.network.BuildConfig
import com.google.samples.apps.nowinandroid.core.network.demo.DemoAssetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

/**
 * Module that provides network related classes. The meaning of the annotations:
 *  - @[Module]: This annotation marks the [NetworkModule] object as a Hilt module. Modules are
 *  responsible for providing instances of classes that cannot be constructor-injected (e.g.,
 *  interfaces, classes from external libraries, or classes that require complex setup).
 *  - @[InstallIn] ([SingletonComponent]::class): This annotation tells Hilt that the bindings
 *  defined in this module should be available in the [SingletonComponent]. The [SingletonComponent]
 *  is a Hilt component that lives as long as the application itself. This means that any
 *  dependencies provided by this module will be singletons (only one instance will be created and
 *  shared throughout the app).
 */
@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    /**
     * [Json] instance used for serialization and deserialization of network payloads.
     *  - @[Provides]: This annotation indicates that this function provides an instance of a type.
     *  - @[Singleton]: This annotation ensures that Hilt will create only one instance of [Json]
     *  and reuse it whenever Json is requested as a dependency.
     *
     * We return the [Json] returned by the [Json] method when it is called with its [JsonBuilder]
     * `builderAction` lambda argument a lambda that sets its [JsonBuilder.ignoreUnknownKeys]
     * property to `true`.
     */
    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Provides a singleton instance of [DemoAssetManager] which is used to access demo assets
     * from the "assets" folder.
     *  - @[Provides]: This annotation indicates that this function provides an instance of a type.
     *  - @[Singleton]: This annotation ensures that Hilt will create only one instance of
     *  [DemoAssetManager] and reuse it whenever it is requested as a dependency.
     *  - @[ApplicationContext]: This annotation injects the application context.
     *
     * We return the [DemoAssetManager] returned by the [DemoAssetManager] method when it is called
     * with its `function` argument the [AssetManager.open] method of the [AssetManager] property
     * [Context.assets] of our [Context] parameter [context].
     *
     * @param context The application context, used to access the assets folder, injected by Hilt.
     * @return A singleton instance of [DemoAssetManager].
     */
    @Provides
    @Singleton
    fun providesDemoAssetManager(
        @ApplicationContext context: Context,
    ): DemoAssetManager = DemoAssetManager(function = context.assets::open)

    /**
     * Provides a singleton instance of [Call.Factory] which is an OkHttp interface for creating
     * [Call] instances. A [Call] is a request that has been prepared for execution.
     *  - @[Provides]: This annotation indicates that this function provides an instance of a type.
     *  - @[Singleton]: This annotation ensures that Hilt will create only one instance of
     *  [Call.Factory] and reuse it whenever it is requested as a dependency.
     *
     * Our [Call.Factory] is built using an [OkHttpClient.Builder] whose `addInterceptor` method
     * is called to add an [HttpLoggingInterceptor] which logs HTTP request and response data.
     * In `debug` builds the log level is set to [HttpLoggingInterceptor.Level.BODY] and in `release`
     * builds it is set to the default [HttpLoggingInterceptor.Level.NONE].
     *
     * @return A singleton instance of [Call.Factory].
     */
    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory = trace(label = "NiaOkHttpClient") {
        OkHttpClient.Builder()
            .addInterceptor(
                interceptor = HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(level = HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .build()
    }

    /**
     * Provides a singleton [ImageLoader] instance for loading images with Coil.
     * This [ImageLoader] is configured to support SVG images and uses a custom [OkHttpClient]
     * for network requests. It also includes a [DebugLogger] in debug builds for logging.
     *  - @[Provides]: This annotation indicates that this function provides an instance of a type.
     *  - @[Singleton]: This annotation ensures that Hilt will create only one instance of
     *  [ImageLoader] and reuse it whenever it is requested as a dependency.
     *  - @[ApplicationContext]: This annotation injects the application context.
     *
     * @param okHttpCallFactory A Dagger [dagger.Lazy] wrapper around the [Call.Factory] for OkHttp.
     * This is used to lazily initialize the OkHttp client, preventing it from being instantiated
     * directly by Dagger.
     * @param application The application [Context], used to initialize the [ImageLoader], injected
     * by Hilt.
     * @return A configured [ImageLoader] instance.
     * @see <a href="https://github.com/coil-kt/coil/blob/main/coil-singleton/src/main/java/coil/Coil.kt">Coil Singleton</a>
     * for how Coil initializes its singleton ImageLoader.
     */
    @Provides
    @Singleton
    fun imageLoader(
        // We specifically request dagger.Lazy here, so that it's not instantiated from Dagger.
        okHttpCallFactory: dagger.Lazy<Call.Factory>,
        @ApplicationContext application: Context,
    ): ImageLoader = trace(label = "NiaImageLoader") {
        ImageLoader.Builder(context = application)
            .callFactory { okHttpCallFactory.get() }
            .components { add(factory = SvgDecoder.Factory()) }
            // Assume most content images are versioned urls
            // but some problematic images are fetching each time
            .respectCacheHeaders(enable = false)
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(logger = DebugLogger())
                }
            }
            .build()
    }
}
