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

package com.google.samples.apps.nowinandroid.core.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkRequest.Builder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.content.getSystemService
import androidx.tracing.Trace
import androidx.tracing.trace
import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.IO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * [NetworkMonitor] implementation that uses [ConnectivityManager] to listen to network
 * changes.
 *
 * @property context the application context, injected by Hilt.
 * @property ioDispatcher the coroutine dispatcher to be used for IO operations, injected by Hilt.
 */
internal class ConnectivityManagerNetworkMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(niaDispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
) : NetworkMonitor {

    /**
     * Network monitoring via [ConnectivityManager.NetworkCallback] requires the INTERNET permission.
     * Returns a flow that emits `true` when the network is available and `false` otherwise.
     *
     * We call the [callbackFlow] method an in its [ProducerScope] `block` lambda argument we start
     * by calling the [trace] method with the `label` "NetworkMonitor.callbackFlow" to wrap its
     * `block` lambda argument in calls to [Trace.beginSection] and [Trace.endSection] so that we
     * may reference the block in the tracing UI. In that `block` lambda argument, we initialize our
     * [ConnectivityManager] variable `connectivityManager` to the application context's
     * [ConnectivityManager] system service. If `connectivityManager` is null, we send a `false`
     * to the [ProducerScope.channel], close it and exit the [callbackFlow]. If `connectivityManager`
     * is not null, we initialize our [NetworkCallback] singleton `object` variable `callback`
     * to a new instance which we keep track of the [Network] objects which are currently available
     * in a [MutableSet] of [Network] variable `networks` and emit `true` and `false` to the [Flow]
     * of [Boolean] variable `isOnline` depending on whether `networks` contains any [Network].
     *
     * @return a flow that emits `true` when the network is available and `false` otherwise.
     */
    override val isOnline: Flow<Boolean> = callbackFlow {
        trace(label = "NetworkMonitor.callbackFlow") {
            val connectivityManager: ConnectivityManager? =
                context.getSystemService<ConnectivityManager>()
            if (connectivityManager == null) {
                channel.trySend(element = false)
                channel.close()
                return@callbackFlow
            }

            /**
             * The callback's methods are invoked on changes to *any* network matching the
             * [NetworkRequest], not just the active network. So we can simply track the presence
             * (or absence) of such [Network].
             */
            val callback: NetworkCallback = object : NetworkCallback() {

                /**
                 * [MutableSet] of [Network]s which are currently available.
                 */
                private val networks: MutableSet<Network> = mutableSetOf()

                /**
                 * Called when a network becomes available that satisfies the `NetworkRequest`.
                 * It may be called multiple times if multiple networks become available that satisfy
                 * the criteria of the `NetworkRequest`. We add our [Network] parameter [network] to
                 * the [MutableSet] of [Network] variable `networks` which are currently available.
                 * We then send a `true` to the [ProducerScope.channel].
                 *
                 * @param network The [Network] object of the satisfying network.
                 */
                override fun onAvailable(network: Network) {
                    networks += network
                    channel.trySend(true)
                }

                /**
                 * Called when a network is lost that was previously satisfying the `NetworkRequest`.
                 * It may be called multiple times if multiple networks are lost. We remove our
                 * [Network] parameter [network] from the [MutableSet] of [Network]s which are
                 * currently available. We then send a `false` to the [ProducerScope.channel] if
                 * the [MutableSet] of [Network] in variable `networks` which are currently available
                 * is empty.
                 *
                 * @param network The `Network` object of the network that was lost.
                 */
                override fun onLost(network: Network) {
                    networks -= network
                    channel.trySend(element = networks.isNotEmpty())
                }
            }

            trace(label = "NetworkMonitor.registerNetworkCallback") {
                val request: NetworkRequest = Builder()
                    .addCapability(/* capability = */ NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                connectivityManager.registerNetworkCallback(
                    /* request = */ request,
                    /* networkCallback = */ callback,
                )
            }

            /**
             * Sends the latest connectivity status to the underlying channel.
             */
            channel.trySend(element = connectivityManager.isCurrentlyConnected())

            awaitClose {
                connectivityManager.unregisterNetworkCallback(/* networkCallback = */ callback)
            }
        }
    }
        .flowOn(context = ioDispatcher)
        .conflate()

    /**
     * Returns `true` if the device is currently connected to the internet, `false` otherwise.
     *
     * It uses different approaches based on the Android version:
     *  - For Android M (API level 23) and above: It checks the capabilities of the active network.
     *  - For older versions: It checks the `isConnected` property of the active network info.
     *
     * In both cases, it returns `false` if no active network is found.
     */
    @Suppress("DEPRECATION")
    private fun ConnectivityManager.isCurrentlyConnected() = when {
        VERSION.SDK_INT >= VERSION_CODES.M ->
            activeNetwork
                ?.let(block = ::getNetworkCapabilities)
                ?.hasCapability(/* capability = */ NetworkCapabilities.NET_CAPABILITY_INTERNET)

        else -> activeNetworkInfo?.isConnected
    } ?: false
}
