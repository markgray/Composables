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

package androidx.compose.samples.crane.details

import android.content.Context
import android.os.Bundle
import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.samples.crane.R
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView

/**
 * Remembers a new instance of [MapView], gives it the [Lifecycle] of the current [LifecycleOwner],
 * and adds a [DisposableEffect] whose `key1` is that [Lifecycle] and whose `key2` is that [MapView]
 * (whenever this Composable leaves the composition or the value of either key changes the `effect`
 * of the [DisposableEffect] will be run again).
 *
 * First we initialize our [Context] variable `val context` to the `current` [LocalContext]. We
 * initialize our [MapView] variable `val mapView` using [remember] (remembers the value produced by
 * its lambda argument, which will only be evaluated during the initial composition, recomposition
 * will always return the value produced by the initial composition), with the lambda constructing
 * a new instance of [MapView] for the [Context] `context` and using the [apply] extension function
 * to set the `id` of that [MapView] to [R.id.map].
 *
 * We initialize our [Lifecycle] variable `val lifecycle` to the [Lifecycle] of the `current`
 * [LocalLifecycleOwner], then call the [DisposableEffect] Composable with its `key1` argument
 * `lifecycle` and its `key2` argument `mapView` (whenever the value of either changes the `effect`
 * lambda argument will be run again). The `effect` lambda argument initializes its [LifecycleEventObserver]
 * variable `val lifecycleObserver` to the [LifecycleEventObserver] that our [getMapLifecycleObserver]
 * returns when passed `mapView` as its `mapView` argument, we then call the [Lifecycle.addObserver]
 * method of `lifecycle` to add `lifecycleObserver` as a [LifecycleObserver] that will be notified
 * when the [LifecycleOwner] changes state. Then we call `DisposableEffectScope` function `onDispose`
 * to provide its `onDisposeEffect` lambda argument to the [DisposableEffect] to run when it leaves
 * the composition or its keys change. That `onDisposeEffect` lambda calls the [Lifecycle.removeObserver]
 * method of `lifecycle` to remove the `lifecycleObserver` observer from the observers list.
 *
 * Finally we return `mapView` to our caller, the `CityMapView` Composable (see file
 * details/DetailsActivity.kt
 *
 * @return a new instance of [MapView] which has been remembered and had its ID set to [R.id.map]
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context: Context = LocalContext.current
    // TODO Codelab: DisposableEffect step. Make MapView follow the lifecycle DONE
    val mapView: MapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    val lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(key1 = lifecycle, key2 = mapView) {
        // Make MapView follow the current lifecycle
        val lifecycleObserver: LifecycleEventObserver = getMapLifecycleObserver(mapView = mapView)
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

/**
 * Constructs a [LifecycleEventObserver] whose [LifecycleEventObserver.onStateChanged] override
 * branches on the value of the [Lifecycle.Event] it receives, calling the corresponding [MapView]
 * lifecycle callbacks of [mapView]:
 *  - [Lifecycle.Event.ON_CREATE] calls the [MapView.onCreate] override of [mapView] with a new
 *  instance of [Bundle]
 *  - [Lifecycle.Event.ON_START] calls the [MapView.onStart] override of [mapView]
 *  - [Lifecycle.Event.ON_RESUME] calls the [MapView.onResume] override of [mapView]
 *  - [Lifecycle.Event.ON_PAUSE] calls the [MapView.onPause] override of [mapView]
 *  - [Lifecycle.Event.ON_STOP] calls the [MapView.onStop] override of [mapView]
 *  - [Lifecycle.Event.ON_DESTROY] calls the [MapView.onDestroy] override of [mapView]
 *  - all other values for the [Lifecycle.Event] throw [IllegalStateException].
 *
 * @param mapView the [MapView] instance that we build a [LifecycleEventObserver] for.
 * @return a [LifecycleEventObserver] that will propagate [Lifecycle] events to the appropriate
 * callbacks of [mapView].
 */
private fun getMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> throw IllegalStateException()
        }
    }

/**
 * [GoogleMap] extension function which calls the methods that need to be called to change the
 * "zoom" of the [GoogleMap] to be our [Float] parameter [zoom]. First we call the
 * [GoogleMap.resetMinMaxZoomPreference] method of our [GoogleMap] receiver to remove any previously
 * specified upper and lower zoom bounds, then we call the [GoogleMap.setMinZoomPreference] method
 * of our [GoogleMap] receiver to set the preferred lower bound for the camera zoom to be [zoom],
 * and finally we call the [GoogleMap.setMaxZoomPreference] method of our [GoogleMap] receiver to
 * set the preferred upper bound for the camera zoom to be [zoom].
 *
 * @param zoom the [Float] value that the "zoom" of our [GoogleMap] receiver should be set to.
 */
fun GoogleMap.setZoom(
    @FloatRange(from = MinZoom.toDouble(), to = MaxZoom.toDouble()) zoom: Float
) {
    resetMinMaxZoomPreference()
    setMinZoomPreference(zoom)
    setMaxZoomPreference(zoom)
}
