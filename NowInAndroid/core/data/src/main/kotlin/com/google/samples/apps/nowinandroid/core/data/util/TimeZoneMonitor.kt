/*
 * Copyright 2024 The Android Open Source Project
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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.tracing.trace
import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.IO
import com.google.samples.apps.nowinandroid.core.network.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility for reporting current timezone the device has set.
 * It always emits at least once with default setting and then for each TZ change.
 */
interface TimeZoneMonitor {
    val currentTimeZone: Flow<TimeZone>
}

/**
 * [TimeZoneMonitor] implemented with a [BroadcastReceiver].
 *
 * @property context application [Context] injected by Hilt.
 * @property appScope application [CoroutineScope] injected by Hilt.
 * @property ioDispatcher [CoroutineDispatcher] injected by Hilt.
 */
@Singleton
internal class TimeZoneBroadcastMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @ApplicationScope appScope: CoroutineScope,
    @param:Dispatcher(niaDispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
) : TimeZoneMonitor {

    /**
     * Shared flow that emits the current time zone when collected, and upon every time zone change.
     *
     * It is backed by a [callbackFlow] that registers a [BroadcastReceiver] for the
     * [Intent.ACTION_TIMEZONE_CHANGED] intent.
     *
     * The flow is shared in the [appScope] with a `replay` of 1, ensuring that new collectors
     * immediately receive the latest time zone. The sharing is started when the first collector
     * subscribes and stops with a 5-second delay after the last collector unsubscribes.
     *
     * The flow emits on the [ioDispatcher] to avoid blocking the main thread.
     *
     * [distinctUntilChanged] is used to prevent multiple emissions of the same time zone,
     * as `trySend` might be called multiple times with the same value initially.
     * [conflate] is used to ensure that only the latest time zone is delivered to collectors
     * if they are not processing emissions fast enough.
     */
    override val currentTimeZone: SharedFlow<TimeZone> =
        callbackFlow {
            // Send the default time zone first.
            trySend(element = TimeZone.currentSystemDefault())

            // Registers BroadcastReceiver for the TimeZone changes
            val receiver: BroadcastReceiver = object : BroadcastReceiver() {
                /**
                 * Called when a broadcast is received.
                 *
                 * This method is called when the [BroadcastReceiver] receives an Intent broadcast.
                 * It checks if the action of the intent is [Intent.ACTION_TIMEZONE_CHANGED].
                 * If it is, it extracts the new time zone from the intent (if available, starting
                 * from Android R) or falls back to the system default time zone.
                 * The new time zone is then sent to the callback flow.
                 *
                 * @param context The Context in which the receiver is running.
                 * @param intent The Intent being received.
                 */
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action != Intent.ACTION_TIMEZONE_CHANGED) return

                    val zoneIdFromIntent: TimeZone? = if (VERSION.SDK_INT < VERSION_CODES.R) {
                        null
                    } else {
                        // Starting Android R we also get the new TimeZone.
                        intent.getStringExtra(Intent.EXTRA_TIMEZONE)?.let { timeZoneId: String ->
                            // We need to convert it from java.util.Timezone to java.time.ZoneId
                            val zoneId: ZoneId = ZoneId.of(
                                /* zoneId = */ timeZoneId,
                                /* aliasMap = */ ZoneId.SHORT_IDS,
                            )
                            // Convert to kotlinx.datetime.TimeZone
                            zoneId.toKotlinTimeZone()
                        }
                    }

                    // If there isn't a zoneId in the intent, fallback to the systemDefault,
                    // which should also reflect the change
                    trySend(element = zoneIdFromIntent ?: TimeZone.currentSystemDefault())
                }
            }

            trace(label = "TimeZoneBroadcastReceiver.register") {
                context.registerReceiver(
                    /* receiver = */ receiver,
                    /* filter = */ IntentFilter(Intent.ACTION_TIMEZONE_CHANGED),
                )
            }

            // Send here again, because registering the Broadcast Receiver can take up to several milliseconds.
            // This way, we can reduce the likelihood that a TZ change wouldn't be caught with the Broadcast Receiver.
            trySend(element = TimeZone.currentSystemDefault())

            awaitClose {
                context.unregisterReceiver(/* receiver = */ receiver)
            }
        }
            // We use to prevent multiple emissions of the same type, because we use trySend multiple times.
            .distinctUntilChanged()
            .conflate()
            .flowOn(context = ioDispatcher)
            // Sharing the callback to prevent multiple BroadcastReceivers being registered
            .shareIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                replay = 1,
            )
}
