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

package com.google.samples.apps.nowinandroid.core.testing.notifications

import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.notifications.Notifier

/**
 * Aggregates news resources that have been notified for addition
 */
class TestNotifier : Notifier {

    /**
     * A list of news resources that have been posted via [postNewsNotifications].
     * The items in the list are the lists of news resources that were passed to
     * [postNewsNotifications].
     */
    private val mutableAddedNewResources = mutableListOf<List<NewsResource>>()

    /**
     * A list of news resources that have been posted via [postNewsNotifications].
     * The items in the list are the lists of news resources that were passed to
     * [postNewsNotifications].
     */
    val addedNewsResources: List<List<NewsResource>> = mutableAddedNewResources

    /**
     * Adds the given [newsResources] to the list of posted news resources.
     *
     * @param newsResources The list of news resources to be posted.
     */
    override fun postNewsNotifications(newsResources: List<NewsResource>) {
        mutableAddedNewResources.add(newsResources)
    }
}
