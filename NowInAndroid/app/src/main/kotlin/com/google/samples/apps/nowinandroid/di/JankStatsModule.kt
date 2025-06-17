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

package com.google.samples.apps.nowinandroid.di

import android.app.Activity
import android.util.Log
import android.view.Window
import androidx.metrics.performance.FrameData
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.JankStats.OnFrameListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * Hilt module that provides singletons related to jank stats.
 *
 * It is installed in the [ActivityComponent] because [JankStats] requires an
 * [Activity].
 *
 * @see JankStats
 */
@Module
@InstallIn(ActivityComponent::class)
object JankStatsModule {
    /**
     * Provides an instance of [OnFrameListener] that logs jank frames for Hilt to inject. This
     * listener is specifically designed to log "janky" frames in your Android application
     * ([FrameData]s where [FrameData.isJank] is true).
     *
     * @see [JankStats.OnFrameListener]
     */
    @Provides
    fun providesOnFrameListener(): OnFrameListener = OnFrameListener { frameData: FrameData ->
        // Make sure to only log janky frames.
        if (frameData.isJank) {
            // We're currently logging this but would better report it to a backend.
            Log.v("NiA Jank", frameData.toString())
        }
    }

    /**
     * Provides the current [Window] of the apps [Activity] for Hilt to inject. It just returns
     * the [Window] that the [Activity.getWindow] method of its parameter [activity] returns.
     *
     * @param activity The current [Activity]
     * @return the current [Window] of its [Activity] parameter [activity]
     * @see [Activity.getWindow]
     */
    @Provides
    fun providesWindow(activity: Activity): Window = activity.window

    /**
     * Provides an instance of [JankStats] for Hilt to inject. This is created by calling the method
     * [JankStats.createAndTrack] with our [Window] parameter [window] as its `window` argument,
     * and our [OnFrameListener] parameter [frameListener] as its `frameListener` argument.
     * [JankStats.createAndTrack] creates a [JankStats] object, begins tracking jank data for the
     * `window`, and returns the new [JankStats] object.
     *
     * @param window the [Window] whose jank should be tracked, this is provided by our [providesWindow]
     * method (which just returns the [Window] of the current [Activity]).
     * @param frameListener an [OnFrameListener] to be added to the new [JankStats] object when it
     * is created. This is provided by our [providesOnFrameListener] method.
     * @return a new [JankStats] instance.
     */
    @Provides
    fun providesJankStats(
        window: Window,
        frameListener: OnFrameListener,
    ): JankStats = JankStats.createAndTrack(window = window, frameListener = frameListener)
}
