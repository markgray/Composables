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

package com.google.samples.apps.nowinandroid.core.domain

import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.domain.TopicSortField.NAME
import com.google.samples.apps.nowinandroid.core.domain.TopicSortField.NONE
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case which obtains a list of topics that can be followed.
 *
 * The list of topics is combined with the user's followed topic IDs to determine the
 * followed state of each topic. The list can also be sorted by topic name.
 *
 * @property topicsRepository a data repository for [Topic] instances injected by Hilt.
 * @property userDataRepository a data repository for [UserData] instances injected by Hilt.
 */
class GetFollowableTopicsUseCase @Inject constructor(
    private val topicsRepository: TopicsRepository,
    private val userDataRepository: UserDataRepository,
) {
    /**
     * Returns a list of topics with their associated followed state.
     *
     * We use the [combine] method to combine the [Flow] of [UserData] property returned by the
     * [UserDataRepository.userData] property of our [UserDataRepository] property [userDataRepository]
     * with the [Flow] of [Topic] property returned by the [TopicsRepository.getTopics] property of
     * our [TopicsRepository] property [topicsRepository] and in the `transform` lambda argument we
     * accept the [UserData] in variable `userData` and the [List] of [Topic] in variable `topics`.
     * We then initialize our [List] of [FollowableTopic] variable `followedTopics` with the result
     * of applying the [Iterable.map] method of `topics` to emit a [FollowableTopic] for each
     * [Topic] in `topics`. The [FollowableTopic.isFollowed] property of each [FollowableTopic] is
     * `true` if the [Topic.id] of the [Topic] is in the [Set] of [String] of the
     * [UserData.followedTopics] property of `userData`. Then we switch on the [TopicSortField]
     * parameter [sortBy] to determine how to sort the [List] of [FollowableTopic]s. If [sortBy] is
     * [NAME] we return the [List] of [FollowableTopic]s sorted by the [Topic.name] property of
     * the [FollowableTopic.topic] property of each [FollowableTopic] in `followedTopics`, otherwise
     * we just return the [List] of [FollowableTopic]s in `followedTopics`.
     *
     * @param sortBy - the field used to sort the topics. Default NONE = no sorting.
     */
    operator fun invoke(sortBy: TopicSortField = NONE): Flow<List<FollowableTopic>> = combine(
        flow = userDataRepository.userData,
        flow2 = topicsRepository.getTopics(),
    ) { userData: UserData, topics: List<Topic> ->
        val followedTopics: List<FollowableTopic> = topics
            .map { topic: Topic ->
                FollowableTopic(
                    topic = topic,
                    isFollowed = topic.id in userData.followedTopics,
                )
            }
        when (sortBy) {
            NAME -> followedTopics.sortedBy { it.topic.name }
            else -> followedTopics
        }
    }
}

/**
 * A [TopicSortField] identifies a field by which a list of [FollowableTopic]s can be sorted.
 */
enum class TopicSortField {
    /**
     * Do not sort the list.
     */
    NONE,

    /**
     * Sorts the list of topics by [Topic.name].
     */
    NAME,
}
