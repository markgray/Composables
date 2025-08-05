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

@file:Suppress("ktlint:standard:max-line-length")

package com.google.samples.apps.nowinandroid.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.PreviewParameterData.newsResources
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides a [List] of fake [UserNewsResource] for Composable previews.
 */
class UserNewsResourcePreviewParameterProvider : PreviewParameterProvider<List<UserNewsResource>> {

    /**
     * A sequence of sample [UserNewsResource] lists for Composable previews.
     */
    override val values: Sequence<List<UserNewsResource>> = sequenceOf(element = newsResources)
}

/**
 * An object that provides sample data for Composable previews.
 */
object PreviewParameterData {

    /**
     * Represents the user's data, including bookmarked news resources, viewed news resources,
     * followed topics, theme preferences, and onboarding status.
     *
     * - `bookmarkedNewsResources`: A [Set] of IDs of news resources that the user has bookmarked.
     * - `viewedNewsResources`: A [Set] of IDs of news resources that the user has viewed.
     * - `followedTopics`: A [Set] of IDs of topics that the user is following.
     * - `themeBrand`: The selected theme brand for the app.
     * - `darkThemeConfig`: The configuration for the dark theme.
     * - `shouldHideOnboarding`: A [Boolean] indicating whether the onboarding screen should be hidden.
     * - `useDynamicColor`: A [Boolean] indicating whether dynamic colors should be used.
     */
    private val userData: UserData = UserData(
        bookmarkedNewsResources = setOf("1", "3"),
        viewedNewsResources = setOf("1", "2", "4"),
        followedTopics = emptySet(),
        themeBrand = ThemeBrand.ANDROID,
        darkThemeConfig = DarkThemeConfig.DARK,
        shouldHideOnboarding = true,
        useDynamicColor = false,
    )

    /**
     * A list of [Topic] instances that can be used in previews.
     * Each topic includes an ID, name, short description, long description, image URL, and URL.
     *
     * - `id`: The unique identifier for the topic.
     * - `name`: The name of the topic.
     * - `shortDescription`: A brief overview of the topic.
     * - `longDescription`: A more detailed explanation of the topic.
     * - `imageUrl`: The URL for an image representing the topic.
     * - `url`: A URL related to the topic, if applicable.
     */
    val topics: List<Topic> = listOf(
        Topic(
            id = "2",
            name = "Headlines",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Topic(
            id = "3",
            name = "UI",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Topic(
            id = "4",
            name = "Testing",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
    )

    /**
     * A list of sample [UserNewsResource]s.
     * Each [UserNewsResource] contains a [NewsResource] and the associated [userData].
     */
    val newsResources: List<UserNewsResource> = listOf(
        UserNewsResource(
            newsResource = NewsResource(
                id = "1",
                title = "Android Basics with Compose",
                content = "We released the first two units of Android Basics with Compose, our first free course that teaches Android Development with Jetpack Compose to anyone; you do not need any prior programming experience other than basic computer literacy to get started. You’ll learn the fundamentals of programming in Kotlin while building Android apps using Jetpack Compose, Android’s modern toolkit that simplifies and accelerates native UI development. These two units are just the beginning; more will be coming soon. Check out Android Basics with Compose to get started on your Android development journey",
                url = "https://android-developers.googleblog.com/2022/05/new-android-basics-with-compose-course.html",
                headerImageUrl = "https://developer.android.com/images/hero-assets/android-basics-compose.svg",
                publishDate = LocalDateTime(
                    year = 2022,
                    monthNumber = 5,
                    dayOfMonth = 4,
                    hour = 23,
                    minute = 0,
                    second = 0,
                    nanosecond = 0,
                ).toInstant(TimeZone.UTC),
                type = "Codelab",
                topics = listOf(topics[2]),
            ),
            userData = userData,
        ),
        UserNewsResource(
            newsResource = NewsResource(
                id = "2",
                title = "Thanks for helping us reach 1M YouTube Subscribers",
                content = "Thank you everyone for following the Now in Android series and everything the " +
                    "Android Developers YouTube channel has to offer. During the Android Developer " +
                    "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
                    "thank you all.",
                url = "https://youtu.be/-fJ6poHQrjM",
                headerImageUrl = "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
                publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
                type = "Video 📺",
                topics = topics.take(2),
            ),
            userData = userData,
        ),
        UserNewsResource(
            newsResource = NewsResource(
                id = "3",
                title = "Transformations and customisations in the Paging Library",
                content = "A demonstration of different operations that can be performed " +
                    "with Paging. Transformations like inserting separators, when to " +
                    "create a new pager, and customisation options for consuming " +
                    "PagingData.",
                url = "https://youtu.be/ZARz0pjm5YM",
                headerImageUrl = "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
                publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
                type = "Video 📺",
                topics = listOf(topics[2]),
            ),
            userData = userData,
        ),
    )
}
