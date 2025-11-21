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

package androidx.compose.samples.crane.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.samples.crane.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.delay

/**
 * How long will [LandingScreen] be displayed before calling its `onTimeout` lambda parameter.
 */
// It is a Compose constant of sorts
private const val SplashWaitTime: Long = 2000

/**
 * This Composable is called by the [MainScreen] Composable when the app is first starting up as a
 * "Splash Screen". It fills the screen with the drawable whose resource ID is `R.drawable.ic_crane_drawer`
 * (a stylized crane), delays for [SplashWaitTime] milliseconds (2,000) then calls its [onTimeout]
 * lambda parameter. The root Composable is a [Box] whose `modifier` argument adds [Modifier.fillMaxSize]
 * to our [Modifier] parameter [modifier] (causes it to fill the [Constraints.maxWidth] and
 * [Constraints.maxHeight] of the incoming measurement constraints) and its `contentAlignment` argument
 * is [Alignment.Center] (centers its contents in the center of the [Box]). We use the method
 * [rememberUpdatedState] to initialize and remember our lambda variable `val currentOnTimeout` to
 * the [onTimeout] parameter (this function remembers a `mutableStateOf` [onTimeout] and updates
 * the value of `currentOnTimeout` to the new Value of [onTimeout] on each recomposition of the
 * [rememberUpdatedState] call. [rememberUpdatedState] should be used when parameters or values
 * computed during composition are referenced by a long-lived lambda or object expression.
 * Recomposition will update the resulting State without recreating the long-lived lambda or object,
 * allowing that object to persist without cancelling and resubscribing, or relaunching a long-lived
 * operation that may be expensive or prohibitive to recreate and restart. This may be common when
 * working with [LaunchedEffect] for example). Next we use [LaunchedEffect] with a `key1` of `true`
 * (the constant `key1` prevents it from being relaunched) to launch its lambda `block` argument
 * into the composition's [CoroutineContext]. The coroutine will be cancelled when the [LaunchedEffect]
 * leaves the composition. The lambda block calls [delay] with [SplashWaitTime] as its `timeMillis`
 * argument to delay [SplashWaitTime] milliseconds (2000), then calls the `currentOnTimeout` lambda.
 * While that is running in the background we call the [Image] Composable to have it fill our [Box]
 * with the drawable whose resource ID is `R.drawable.ic_crane_drawer`.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [MainScreen] does not pass us one so the, default, or starter [Modifier] that contains
 * no elements is used instead.
 * @param onTimeout a lambda we should call when our [SplashWaitTime] delay is over. [MainScreen]
 * passes us a lambda which sets its [Boolean] state variable `showLandingScreen` to `false` so that
 * the [CraneHome] Composable will be called instead of [LandingScreen] from then on.
 */
@Composable
fun LandingScreen(modifier: Modifier = Modifier, onTimeout: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // TASK Codelab: LaunchedEffect and rememberUpdatedState step DONE
        // TASK: Make LandingScreen disappear after loading data DONE
        // This will always refer to the latest onTimeout function that
        // LandingScreen was recomposed with
        val currentOnTimeout: () -> Unit by rememberUpdatedState(onTimeout)

        // Create an effect that matches the lifecycle of LandingScreen.
        // If LandingScreen recomposes or onTimeout changes,
        // the delay shouldn't start again.
        LaunchedEffect(true) {
            delay(timeMillis = SplashWaitTime)
            currentOnTimeout()
        }

        Image(painter = painterResource(id = R.drawable.ic_crane_drawer), contentDescription = null)
    }
}
