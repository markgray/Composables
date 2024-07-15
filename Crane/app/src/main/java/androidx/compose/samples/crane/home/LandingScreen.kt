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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.samples.crane.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.delay

/**
 * How long will [LandingScreen] be displayed before calling its `onTimeout` lambda parameter.
 */
private const val SplashWaitTime: Long = 2000

/**
 * This Composable is called by the [MainScreen] Composable when the app is first starting up as a
 * "Splash Screen". It fills the screen with the drawable whose resource ID is [R.drawable.ic_crane_drawer]
 * (a stylized crane), delays for [SplashWaitTime] milliseconds (2,000) then calls its [onTimeout]
 * lambda parameter. The root Composable is an [Image] whose `modifier` argument adds
 * [Modifier.fillMaxSize] to our [Modifier] parameter [modifier] (causes it to fill the
 * entire width and height of the incoming measurement constraints), and to this is chained a
 * [Modifier.wrapContentSize] to Allow the [Image] to measure at its desired size.
 * We use the method [rememberUpdatedState] to initialize and remember our lambda variable
 * `val currentOnTimeout` to the [onTimeout] parameter (this function remembers a `mutableStateOf`
 * [onTimeout] and updates the value of `currentOnTimeout` to the new Value of [onTimeout] on each
 * recomposition of the [rememberUpdatedState] call. [rememberUpdatedState] should be used when
 * parameters or values computed during composition are referenced by a long-lived lambda or object
 * expression. Recomposition will update the resulting State without recreating the long-lived lambda
 * or object, allowing that object to persist without cancelling and resubscribing, or relaunching a
 * long-lived operation that may be expensive or prohibitive to recreate and restart. This may be
 * common when working with [LaunchedEffect] for example). Next we use [LaunchedEffect] with a `key1`
 * of [Unit] (the constant `key1` prevents it from being relaunched) to launch its lambda `block`
 * argument into the composition's [CoroutineContext]. The coroutine will be cancelled when the
 * [LaunchedEffect] leaves the composition. The lambda block calls [delay] with [SplashWaitTime] as
 * its `timeMillis` argument to delay [SplashWaitTime] milliseconds (2000), then calls the
 * `currentOnTimeout` lambda. While that is running in the background we call the [Image] Composable
 * to display the drawable whose resource ID is [R.drawable.ic_crane_drawer].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [MainScreen] passes us a [Modifier.alpha] that sets our `alpha` to an animated value
 * between 1f and 0 that varies as our state goes from [SplashState.Shown] to [SplashState.Completed]
 * @param onTimeout a lambda we should call when our [SplashWaitTime] delay is over. [MainScreen]
 * passes us a lambda which sets its [SplashState] values to [SplashState.Completed]
 */
@Composable
fun LandingScreen(modifier: Modifier = Modifier, onTimeout: () -> Unit) {
    /**
     * Adds composition consistency. This will always refer to the latest [onTimeout] function that
     * LandingScreen was recomposed with.
     */
    val currentOnTimeout by rememberUpdatedState(onTimeout)

    LaunchedEffect(Unit) {
        delay(SplashWaitTime)
        currentOnTimeout()
    }
    Image(
        painterResource(id = R.drawable.ic_crane_drawer),
        contentDescription = null,
        modifier
            .fillMaxSize()
            .wrapContentSize()
    )
}
