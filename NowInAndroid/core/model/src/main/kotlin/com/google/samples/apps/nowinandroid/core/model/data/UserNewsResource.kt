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

package com.google.samples.apps.nowinandroid.core.model.data

import kotlinx.datetime.Instant

/**
 * A [NewsResource] with additional user information such as whether the user is following the
 * news resource's topics, whether they have saved (bookmarked) this news resource and whether they
 * have viewed it.
 *
 * @property id The unique identifier of the news resource.
 * @property title The title of the news resource.
 * @property content The content of the news resource.
 * @property url The URL of the news resource.
 * @property headerImageUrl The URL of the header image for the news resource.
 * @property publishDate The date the news resource was published.
 * @property type The type of the news resource.
 * @property followableTopics The [List] of [FollowableTopic] topics associated with the news resource.
 * @property isSaved Whether the user has saved (bookmarked) this news resource.
 * @property hasBeenViewed Whether the user has viewed this news resource.
 */
data class UserNewsResource internal constructor(
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    val headerImageUrl: String?,
    val publishDate: Instant,
    val type: String,
    val followableTopics: List<FollowableTopic>,
    val isSaved: Boolean,
    val hasBeenViewed: Boolean,
) {
    /**
     * Secondary constructor for [UserNewsResource] that takes a [NewsResource] and a [UserData].
     * We call the primary constructor with the arguments:
     *  - `id`: The [NewsResource.id] of our [NewsResource] parameter [newsResource].
     *  - `title`: The [NewsResource.title] of our [NewsResource] parameter [newsResource].
     *  - `content`: The [NewsResource.content] of our [NewsResource] parameter [newsResource].
     *  - `url`: The [NewsResource.url] of our [NewsResource] parameter [newsResource].
     *  - `headerImageUrl`: The [NewsResource.headerImageUrl] of our [NewsResource] parameter
     *  [newsResource].
     *  - `publishDate`: The [NewsResource.publishDate] of our [NewsResource] parameter
     *  [newsResource].
     *  - `type`: The [NewsResource.type] of our [NewsResource] parameter [newsResource].
     *  - `followableTopics`: The list of [FollowableTopic] associated with the news resource is
     *  created by using the [Iterable.map] method of the [NewsResource.topics] property of the
     *  [NewsResource] parameter [newsResource] to loop through the list of topics capturing the
     *  [Topic] in variable `topic` and creating a [FollowableTopic] with its `topic` property
     *  set to `topic` and its `isFollowed` property set to `true` if `topic.id` is in the
     *  [Set] of [String] property [UserData.followedTopics] of the [UserData] parameter [userData].
     *  - `isSaved`: `true` if the [NewsResource.id] of the [NewsResource] parameter [newsResource]
     *  is in the [Set] of [String] property [UserData.bookmarkedNewsResources] of the [UserData]
     *  parameter [userData].
     *  - `hasBeenViewed`: `true` if the [NewsResource.id] of the [NewsResource] parameter
     *  [newsResource] is in the [Set] of [String] property [UserData.viewedNewsResources] of the
     *  [UserData] parameter [userData].
     *
     * @param newsResource The [NewsResource] to convert to a [UserNewsResource].
     * @param userData The [UserData] to use when converting the [NewsResource] to a
     * [UserNewsResource].
     */
    constructor(newsResource: NewsResource, userData: UserData) : this(
        id = newsResource.id,
        title = newsResource.title,
        content = newsResource.content,
        url = newsResource.url,
        headerImageUrl = newsResource.headerImageUrl,
        publishDate = newsResource.publishDate,
        type = newsResource.type,
        followableTopics = newsResource.topics.map { topic: Topic ->
            FollowableTopic(
                topic = topic,
                isFollowed = topic.id in userData.followedTopics,
            )
        },
        isSaved = newsResource.id in userData.bookmarkedNewsResources,
        hasBeenViewed = newsResource.id in userData.viewedNewsResources,
    )
}

/**
 * Converts a list of [NewsResource]s to a list of [UserNewsResource]s based on information in the
 * [UserData] parameter [userData]. We use the [Iterable.map] method of our [List] of [NewsResource]
 * receiver to loop through the list of news resources capturing the [NewsResource] in variable `it`
 * and creating a [UserNewsResource] with its `newsResource` property set to `it` and its `userData`
 * property set to our [UserData] parameter [userData].
 */
fun List<NewsResource>.mapToUserNewsResources(userData: UserData): List<UserNewsResource> =
    map { UserNewsResource(newsResource = it, userData = userData) }
