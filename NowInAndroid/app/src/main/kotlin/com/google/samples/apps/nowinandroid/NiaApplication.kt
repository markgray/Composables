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

package com.google.samples.apps.nowinandroid

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.samples.apps.nowinandroid.sync.initializers.Sync
import com.google.samples.apps.nowinandroid.util.ProfileVerifierLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * [Application] class for NiA. It implements the [ImageLoaderFactory] interface so that Coil will
 * use its [newImageLoader] method to create a new [ImageLoader] instance which it will use for
 * all image loading operations.
 */
@HiltAndroidApp
class NiaApplication : Application(), ImageLoaderFactory {
    /**
     * Lazily inject [ImageLoader] so that it is not initialized until we need it.
     */
    @Inject
    lateinit var imageLoader: dagger.Lazy<ImageLoader>

    /**
     * [ProfileVerifierLogger] injected by Hilt
     */
    @Inject
    lateinit var profileVerifierLogger: ProfileVerifierLogger

    /**
     * Initializes the application. First we call our super's implementation of `onCreate`, then we
     * call [setStrictModePolicy] to set a thread policy that detects all potential problems on the
     * main thread, such as network and disk access. If a problem is found, the offending call will
     * be logged and the application will be killed. Then we call the [Sync.initialize] method to
     * initialize Sync: the system responsible for keeping data in the app up to date. Finally we
     * call the [profileVerifierLogger] method to log the profile verification status.
     */
    override fun onCreate() {
        super.onCreate()

        setStrictModePolicy()

        // Initialize Sync; the system responsible for keeping data in the app up to date.
        Sync.initialize(context = this)
        profileVerifierLogger()
    }

    /**
     * Returns a new [ImageLoader].
     */
    override fun newImageLoader(): ImageLoader = imageLoader.get()

    /**
     * Return true if the application is debuggable.
     */
    private fun isDebuggable(): Boolean {
        return 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
    }

    /**
     * Set a thread policy that detects all potential problems on the main thread, such as network
     * and disk access.
     *
     * If a problem is found, the offending call will be logged and the application will be killed.
     */
    private fun setStrictModePolicy() {
        if (isDebuggable()) {
            StrictMode.setThreadPolicy(
                Builder().detectAll().penaltyLog().build(),
            )
        }
    }
}
