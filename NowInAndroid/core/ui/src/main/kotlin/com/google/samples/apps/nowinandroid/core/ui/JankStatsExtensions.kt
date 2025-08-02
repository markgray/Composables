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

package com.google.samples.apps.nowinandroid.core.ui

import android.view.View
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalView
import androidx.metrics.performance.PerformanceMetricsState
import androidx.metrics.performance.PerformanceMetricsState.Holder
import kotlinx.coroutines.CoroutineScope

/**
 * Retrieves [PerformanceMetricsState.Holder] from current [LocalView] and
 * remembers it until the View changes.
 * @see PerformanceMetricsState.getHolderForHierarchy
 */
@Composable
fun rememberMetricsStateHolder(): Holder {
    val localView: View = LocalView.current

    return remember(key1 = localView) {
        PerformanceMetricsState.getHolderForHierarchy(localView)
    }
}

/**
 * Convenience function to work with [PerformanceMetricsState].
 * The [reportMetric] block is launched whenever one of the [keys] changes.
 *
 * It is re-launched if any of the [keys] are not equal to the previous composition.
 *
 * @param keys A set of unique keys that identify the current state.
 * @param reportMetric A block of code that will be executed when the keys change.
 * This block will have the [PerformanceMetricsState.Holder] as a receiver.
 *
 * @see TrackDisposableJank if you need to work with DisposableEffect to clean up added state.
 */
@Composable
fun TrackJank(
    vararg keys: Any,
    reportMetric: suspend CoroutineScope.(state: Holder) -> Unit,
) {
    val metrics: Holder = rememberMetricsStateHolder()
    LaunchedEffect(metrics, *keys) {
        reportMetric(metrics)
    }
}

/**
 * Convenience function to work with [PerformanceMetricsState] state that needs to be cleaned up.
 * The side effect is re-launched if any of the [keys] value is not equal to the previous composition.
 *
 * @param keys The keys to compare for re-launching the effect.
 * @param reportMetric The function to report the metric. It will be invoked within a
 * [DisposableEffectScope]. The [DisposableEffectResult] returned by this function will be used
 * to clean up the state when the effect is disposed.
 */
@Composable
fun TrackDisposableJank(
    vararg keys: Any,
    reportMetric: DisposableEffectScope.(state: Holder) -> DisposableEffectResult,
) {
    val metrics: Holder = rememberMetricsStateHolder()
    DisposableEffect(metrics, *keys) {
        reportMetric(this, metrics)
    }
}

/**
 * Track jank while scrolling anything that's scrollable.
 *
 * @param scrollableState the state of the scrollable component.
 * @param stateName the name of the state to be reported to JankStats.
 */
@Composable
fun TrackScrollJank(scrollableState: ScrollableState, stateName: String) {
    TrackJank(scrollableState) { metricsHolder: Holder ->
        snapshotFlow { scrollableState.isScrollInProgress }.collect { isScrollInProgress: Boolean ->
            metricsHolder.state?.apply {
                if (isScrollInProgress) {
                    putState(key = stateName, value = "Scrolling=true")
                } else {
                    removeState(key = stateName)
                }
            }
        }
    }
}
