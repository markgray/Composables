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

package com.google.samples.apps.nowinandroid.core.model.data

/**
 * Class summarizing user interest data.
 *
 * @param bookmarkedNewsResources The set of news resources that the user has bookmarked.
 * @param viewedNewsResources The set of news resources that the user has viewed.
 * @param followedTopics The set of topics that the user has followed.
 * @param themeBrand The user's preferred theme brand.
 * @param darkThemeConfig The user's preferred dark theme configuration.
 * @param useDynamicColor Whether the user has enabled dynamic color.
 * @param shouldHideOnboarding `true` if the user has completed the onboarding process, in which case
 * the onboarding screen should be hidden.
 */
data class UserData(
    val bookmarkedNewsResources: Set<String>,
    val viewedNewsResources: Set<String>,
    val followedTopics: Set<String>,
    val themeBrand: ThemeBrand,
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
    val shouldHideOnboarding: Boolean,
)
