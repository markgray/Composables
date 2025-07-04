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

package com.google.samples.apps.nowinandroid.core.data

import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand.DEFAULT
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import kotlinx.datetime.Clock
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [UserNewsResource]
 */
class UserNewsResourceTest {

    /**
     * Tests that a [UserNewsResource] is constructed correctly from a [NewsResource] and a
     * [UserData].
     *
     * We start by initializing our [NewsResource] variable `newsResource1` to a [NewsResource]
     * containing some fake data. We then initialize our [UserData] variable `userData` to a
     * [UserData] containing some fake data. We initialize our [UserNewsResource] variable
     * `userNewsResource` to a [UserNewsResource] constructed from `newsResource1` and `userData`.
     * We use [assertEquals] to check that the fields in `userNewsResource` are equal to the fields
     * in `newsResource1`. We also use [assertEquals] to check that the fields in `userNewsResource`
     * are equal to the fields in `userData`.
     */
    @Test
    fun userNewsResourcesAreConstructedFromNewsResourcesAndUserData() {
        val newsResource1 = NewsResource(
            id = "N1",
            title = "Test news title",
            content = "Test news content",
            url = "Test URL",
            headerImageUrl = "Test image URL",
            publishDate = Clock.System.now(),
            type = "Article 📚",
            topics = listOf(
                Topic(
                    id = "T1",
                    name = "Topic 1",
                    shortDescription = "Topic 1 short description",
                    longDescription = "Topic 1 long description",
                    url = "Topic 1 URL",
                    imageUrl = "Topic 1 image URL",
                ),
                Topic(
                    id = "T2",
                    name = "Topic 2",
                    shortDescription = "Topic 2 short description",
                    longDescription = "Topic 2 long description",
                    url = "Topic 2 URL",
                    imageUrl = "Topic 2 image URL",
                ),
            ),
        )

        val userData = UserData(
            bookmarkedNewsResources = setOf("N1"),
            viewedNewsResources = setOf("N1"),
            followedTopics = setOf("T1"),
            themeBrand = DEFAULT,
            darkThemeConfig = FOLLOW_SYSTEM,
            useDynamicColor = false,
            shouldHideOnboarding = true,
        )

        val userNewsResource = UserNewsResource(newsResource = newsResource1, userData = userData)

        // Check that the simple field mappings have been done correctly.
        assertEquals(expected = newsResource1.id, actual = userNewsResource.id)
        assertEquals(expected = newsResource1.title, actual = userNewsResource.title)
        assertEquals(expected = newsResource1.content, actual = userNewsResource.content)
        assertEquals(expected = newsResource1.url, actual = userNewsResource.url)
        assertEquals(
            expected = newsResource1.headerImageUrl,
            actual = userNewsResource.headerImageUrl,
        )
        assertEquals(expected = newsResource1.publishDate, actual = userNewsResource.publishDate)

        // Check that each Topic has been converted to a FollowedTopic correctly.
        assertEquals(
            expected = newsResource1.topics.size,
            actual = userNewsResource.followableTopics.size,
        )
        for (topic in newsResource1.topics) {
            // Construct the expected FollowableTopic.
            val followableTopic = FollowableTopic(
                topic = topic,
                isFollowed = topic.id in userData.followedTopics,
            )
            assertTrue(actual = userNewsResource.followableTopics.contains(followableTopic))
        }

        // Check that the saved flag is set correctly.
        assertEquals(
            expected = newsResource1.id in userData.bookmarkedNewsResources,
            actual = userNewsResource.isSaved,
        )
    }
}
