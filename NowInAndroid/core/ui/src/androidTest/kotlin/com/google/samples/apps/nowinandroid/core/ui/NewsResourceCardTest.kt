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

package com.google.samples.apps.nowinandroid.core.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.testing.data.followableTopicTestData
import com.google.samples.apps.nowinandroid.core.testing.data.userNewsResourcesTestData
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [NewsResourceCardExpanded] and [NewsResourceTopics].
 */
class NewsResourceCardTest {
    /**
     * The [AndroidComposeTestRule] used in these tests.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<
        ActivityScenarioRule<ComponentActivity>,
        ComponentActivity,
        > = createAndroidComposeRule<ComponentActivity>()

    /**
     * Tests that the meta data displays correctly when the resource type is known ("Codelab" in
     * this case).
     */
    @Test
    fun testMetaDataDisplay_withCodelabResource() {
        val newsWithKnownResourceType: UserNewsResource = userNewsResourcesTestData[0]
        lateinit var dateFormatted: String

        composeTestRule.setContent {
            NewsResourceCardExpanded(
                userNewsResource = newsWithKnownResourceType,
                isBookmarked = false,
                hasBeenViewed = false,
                onToggleBookmark = {},
                onClick = {},
                onTopicClick = {},
            )

            dateFormatted = dateFormatted(publishDate = newsWithKnownResourceType.publishDate)
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.core_ui_card_meta_data_text,
                    dateFormatted,
                    newsWithKnownResourceType.type,
                ),
            )
            .assertExists()
    }

    /**
     * Tests that the meta data displays correctly when the resource type is not known (empty in
     * this case).
     */
    @Test
    fun testMetaDataDisplay_withEmptyResourceType() {
        val newsWithEmptyResourceType: UserNewsResource = userNewsResourcesTestData[3]
        lateinit var dateFormatted: String

        composeTestRule.setContent {
            NewsResourceCardExpanded(
                userNewsResource = newsWithEmptyResourceType,
                isBookmarked = false,
                hasBeenViewed = false,
                onToggleBookmark = {},
                onClick = {},
                onTopicClick = {},
            )

            dateFormatted = dateFormatted(publishDate = newsWithEmptyResourceType.publishDate)
        }

        composeTestRule
            .onNodeWithText(text = dateFormatted)
            .assertIsDisplayed()
    }

    /**
     * Tests that the content description of the topic chip reflects the correct followed state.
     * When a topic is followed, the content description should indicate that it is followed.
     * When a topic is not followed, the content description should indicate that it is not followed.
     */
    @Test
    fun testTopicsChipColorBackground_matchesFollowedState() {
        composeTestRule.setContent {
            NewsResourceTopics(
                topics = followableTopicTestData,
                onTopicClick = {},
            )
        }

        for (followableTopic: FollowableTopic in followableTopicTestData) {
            val topicName: String = followableTopic.topic.name
            val expectedContentDescription: String = if (followableTopic.isFollowed) {
                "$topicName is followed"
            } else {
                "$topicName is not followed"
            }
            composeTestRule
                .onNodeWithText(text = topicName.uppercase())
                .assertContentDescriptionEquals(expectedContentDescription)
        }
    }

    /**
     * Tests that the unread dot is displayed when the item has not been viewed.
     */
    @Test
    fun testUnreadDot_displayedWhenUnread() {
        val unreadNews: UserNewsResource = userNewsResourcesTestData[2]

        composeTestRule.setContent {
            NewsResourceCardExpanded(
                userNewsResource = unreadNews,
                isBookmarked = false,
                hasBeenViewed = false,
                onToggleBookmark = {},
                onClick = {},
                onTopicClick = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(
                    R.string.core_ui_unread_resource_dot_content_description,
                ),
            )
            .assertIsDisplayed()
    }

    /**
     * Tests that the unread dot is not displayed when the item has been viewed.
     */
    @Test
    fun testUnreadDot_notDisplayedWhenRead() {
        val readNews: UserNewsResource = userNewsResourcesTestData[0]

        composeTestRule.setContent {
            NewsResourceCardExpanded(
                userNewsResource = readNews,
                isBookmarked = false,
                hasBeenViewed = true,
                onToggleBookmark = {},
                onClick = {},
                onTopicClick = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(
                    R.string.core_ui_unread_resource_dot_content_description,
                ),
            )
            .assertDoesNotExist()
    }
}
