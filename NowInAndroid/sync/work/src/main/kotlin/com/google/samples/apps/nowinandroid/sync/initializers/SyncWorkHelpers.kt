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

package com.google.samples.apps.nowinandroid.sync.initializers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import com.google.samples.apps.nowinandroid.sync.R

/**
 * The name of the Fcm topic to subscribe to for sync.
 */
@Suppress("unused") // It is used by FirebaseSyncSubscriber
const val SYNC_TOPIC: String = "sync"

/**
 * Notification ID for the sync foreground service
 */
private const val SYNC_NOTIFICATION_ID = 0

/**
 * Notification Channel ID for the sync foreground service
 */
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"

/**
 * Constraints for sync tasks:
 * - Requires a network connection.
 */
val SyncConstraints: Constraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(networkType = NetworkType.CONNECTED)
        .build()

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.syncForegroundInfo(): ForegroundInfo = ForegroundInfo(
    SYNC_NOTIFICATION_ID,
    syncWorkNotification(),
)

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service.
 *
 * If the [Build.VERSION.SDK_INT] is greater than or equal to [Build.VERSION_CODES.O], we construct
 * a [NotificationChannel] with `id` [SYNC_NOTIFICATION_CHANNEL_ID], `name` "Sync",  `importance`
 * [NotificationManager.IMPORTANCE_DEFAULT] and `description` "Background tasks for Now in Android"
 * and use the [NotificationManager] to create the notification channel. In any case we construct
 * a [NotificationCompat.Builder] whose `context` is `this` and whose `channelId` is
 * [SYNC_NOTIFICATION_CHANNEL_ID], use its [NotificationCompat.Builder.setSmallIcon] method to set
 * its small icon to `R.drawable.core_notifications_ic_nia_notification`, use its
 * [NotificationCompat.Builder.setContentTitle] method to set its title to "Now in Android", use
 * the [NotificationCompat.Builder.setPriority] method to set its priority to
 * [NotificationCompat.PRIORITY_DEFAULT] then use its [NotificationCompat.Builder.build] to build it
 * and return the [Notification] it builds.
 */
private fun Context.syncWorkNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            /* id = */ SYNC_NOTIFICATION_CHANNEL_ID,
            /* name = */ getString(R.string.sync_work_notification_channel_name),
            /* importance = */ NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(R.string.sync_work_notification_channel_description)
        }
        // Register the channel with the system
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(
        this,
        SYNC_NOTIFICATION_CHANNEL_ID,
    )
        .setSmallIcon(
            com.google.samples.apps.nowinandroid.core.notifications.R.drawable.core_notifications_ic_nia_notification,
        )
        .setContentTitle(getString(R.string.sync_work_notification_title))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}
