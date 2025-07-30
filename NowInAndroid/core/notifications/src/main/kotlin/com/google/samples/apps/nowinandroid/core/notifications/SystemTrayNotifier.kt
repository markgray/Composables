/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.InboxStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The maximum number of notifications that can be displayed at once.
 */
private const val MAX_NUM_NOTIFICATIONS = 5

/**
 * The name of the activity to start when a notification is clicked.
 * This should be the fully qualified name of the activity.
 */
private const val TARGET_ACTIVITY_NAME = "com.google.samples.apps.nowinandroid.MainActivity"

/**
 * The request code for the news notification pending intent.
 */
private const val NEWS_NOTIFICATION_REQUEST_CODE = 0

/**
 * The ID of the summary notification for news updates.
 */
private const val NEWS_NOTIFICATION_SUMMARY_ID = 1

/**
 * The ID of the notification channel for news updates.
 */
private const val NEWS_NOTIFICATION_CHANNEL_ID = ""

/**
 * The group of notifications for news updates.
 */
private const val NEWS_NOTIFICATION_GROUP = "NEWS_NOTIFICATIONS"

/**
 * The deep link scheme and host for news updates.
 */
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.nowinandroid.apps.samples.google.com"

/**
 * The path for news updates.
 */
private const val DEEP_LINK_FOR_YOU_PATH = "foryou"

/**
 * The base path for news updates.
 */
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_FOR_YOU_PATH"

/**
 * The key for the news resource ID in a deep link.
 */
const val DEEP_LINK_NEWS_RESOURCE_ID_KEY: String = "linkedNewsResourceId"

/**
 * The deep link URI pattern for news updates.
 */
const val DEEP_LINK_URI_PATTERN: String = "$DEEP_LINK_BASE_PATH/{$DEEP_LINK_NEWS_RESOURCE_ID_KEY}"

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 * - @[Singleton]: This annotation indicates that only one instance of [SystemTrayNotifier] will be
 * created and shared throughout the application.
 * - @[Inject] constructor signifies that its dependencies will be provided by Hilt.
 * - @param:[ApplicationContext] it takes an application Context as a dependency injected by Hilt.
 *
 * @property context The application context injected by Hilt.
 */
@Singleton
internal class SystemTrayNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : Notifier {

    /**
     * Posts notifications for new or updated news resources.
     *
     * If the user hasn't granted the `POST_NOTIFICATIONS` permission, this function is a no-op.
     *
     * @param newsResources The list of news resources to post notifications for. The list is
     * truncated to [MAX_NUM_NOTIFICATIONS]. A summary notification is also posted with a title
     * summarizing the number of new news resources. The individual notifications are grouped
     * under the summary notification.
     */
    override fun postNewsNotifications(
        newsResources: List<NewsResource>,
    ) = with(receiver = context) {
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return
        }

        val truncatedNewsResources: List<NewsResource> =
            newsResources.take(n = MAX_NUM_NOTIFICATIONS)

        val newsNotifications: List<Notification> =
            truncatedNewsResources.map { newsResource: NewsResource ->
                createNewsNotification {
                    setSmallIcon(R.drawable.core_notifications_ic_nia_notification)
                        .setContentTitle(newsResource.title)
                        .setContentText(newsResource.content)
                        .setContentIntent(newsPendingIntent(newsResource))
                        .setGroup(NEWS_NOTIFICATION_GROUP)
                        .setAutoCancel(true)
                }
            }
        val summaryNotification: Notification = createNewsNotification {
            val title: String = getString(
                R.string.core_notifications_news_notification_group_summary,
                truncatedNewsResources.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(R.drawable.core_notifications_ic_nia_notification)
                // Build summary info into InboxStyle template.
                .setStyle(newsNotificationStyle(truncatedNewsResources, title))
                .setGroup(NEWS_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // Send the notifications
        val notificationManager: NotificationManagerCompat =
            NotificationManagerCompat.from(this)
        newsNotifications.forEachIndexed { index: Int, notification: Notification ->
            notificationManager.notify(
                truncatedNewsResources[index].id.hashCode(),
                notification,
            )
        }
        notificationManager.notify(NEWS_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    /**
     * Creates an [InboxStyle] notification for news updates.
     *
     * This function constructs an inbox-style notification, which is used for displaying a list of
     * items. Each news resource's title is added as a line in the notification. The notification's
     * big content title and summary text are set to the [String] parameter [title].
     *
     * @param newsResources The list of [NewsResource] objects to be displayed in the notification.
     * The title of each news resource will be a line in the inbox style.
     * @param title The title for the notification, used as the big content title and summary text.
     * @return An [InboxStyle] object configured with the news resources and title.
     */
    private fun newsNotificationStyle(
        newsResources: List<NewsResource>,
        title: String,
    ): InboxStyle = newsResources
        .fold(initial = InboxStyle()) { inboxStyle: InboxStyle, newsResource: NewsResource ->
            inboxStyle.addLine(newsResource.title)
        }
        .setBigContentTitle(title)
        .setSummaryText(title)
}

/**
 * Creates a notification for news updates.
 *
 * This function ensures that the notification channel for news updates exists before creating the
 * notification.
 *
 * @param block A lambda function that configures the notification builder.
 * @return The created notification.
 */
private fun Context.createNewsNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        NEWS_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block = block)
        .build()
}

/**
 * Ensures that the notification channel for news updates exists.
 *
 * This function is a no-op if the device's SDK version is lower than Android Oreo (API level 26),
 * as notification channels were introduced in that version.
 *
 * The notification channel is created with the ID [NEWS_NOTIFICATION_CHANNEL_ID], the name
 * specified by the string resource `R.string.core_notifications_news_notification_channel_name`,
 * and the description specified by the string resource
 * `R.string.core_notifications_news_notification_channel_description`.
 * The importance level is set to [NotificationManager.IMPORTANCE_DEFAULT].
 */
private fun Context.ensureNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        NEWS_NOTIFICATION_CHANNEL_ID,
        getString(R.string.core_notifications_news_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.core_notifications_news_notification_channel_description)
    }
    // Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

/**
 * Creates a [PendingIntent] that will open the app to the specific [newsResource] when the
 * notification is clicked.
 *
 * The [PendingIntent] is created with the [Intent.ACTION_VIEW] action, the deep link URI for the
 * [newsResource], and the [TARGET_ACTIVITY_NAME] component. The flags
 * [PendingIntent.FLAG_UPDATE_CURRENT] and [PendingIntent.FLAG_IMMUTABLE] are used.
 *
 * @param newsResource The news resource to create the deep link for.
 * @return The created [PendingIntent].
 */
private fun Context.newsPendingIntent(
    newsResource: NewsResource,
): PendingIntent? = PendingIntent.getActivity(
    this,
    NEWS_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = newsResource.newsDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

/**
 * Creates a deep link URI for the given news resource.
 *
 * This URI can be used to navigate directly to the news resource in the app.
 *
 * @return The deep link URI for the news resource.
 */
private fun NewsResource.newsDeepLinkUri() = "$DEEP_LINK_BASE_PATH/$id".toUri()
