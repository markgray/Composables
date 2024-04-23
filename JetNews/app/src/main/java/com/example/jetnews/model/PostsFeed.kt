/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import com.example.jetnews.data.posts.impl.post1
import com.example.jetnews.data.posts.impl.post2
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.data.posts.impl.post4
import com.example.jetnews.data.posts.impl.post5
import com.example.jetnews.ui.home.PostCardHistory

/**
 * A container of [Post]s, partitioned into different categories.
 */
data class PostsFeed(
    /**
     * Displayed in the `PostListTopSection` of `PostList`. It is always global [Post] variable
     * [post4] in the global [PostsFeed] variable of dummy [Post] that this app uses. Title:
     * "Locale changes and the AndroidViewModel antipattern"
     */
    val highlightedPost: Post,
    /**
     * Displayed just below [highlightedPost] in a `PostListSimpleSection`. Its is always a [List]
     * of [post1] "A Little Thing about Android Module Paths", [post2] "Dagger in Kotlin: Gotchas
     * and Optimizations", and [post3] "From Java Programming Language to Kotlin--the idiomatic way".
     */
    val recommendedPosts: List<Post>,
    /**
     * Displayed just below [recommendedPosts] in a `PostListPopularSection` in a [Row] below the
     * title [Text] "Popular on Jetnews". It is always a [List] of global [Post] variable [post5]
     * "Collections and sequences in Kotlin", a copy of [post1] whose [Post.id] is overridden to be
     * "post6" titled "A Little Thing about Android Module Paths", and a copy of [post2] whose
     * [Post.id] is overridden to be "post7" titled "Dagger in Kotlin: Gotchas and Optimizations".
     */
    val popularPosts: List<Post>,
    /**
     * Displayed just below [popularPosts] in a `PostListHistorySection` in a [Column] of
     * [PostCardHistory]'s. It is always a [List] of a copy of [post3] whose [Post.id] is overridden
     * to be "post8" titled "From Java Programming Language to Kotlin -- the idiomatic way", of a
     * copy of [post4] whose [Post.id] is overridden to be "post9" titled "Locale changes and the
     * AndroidViewModel antipattern", and of a copy of [post5] whose [Post.id] is overridden to be
     * "post10" titled "Collections and sequences in Kotlin".
     */
    val recentPosts: List<Post>,
) {
    /**
     * Returns a flattened list of all posts contained in the feed.
     */
    val allPosts: List<Post> =
        listOf(highlightedPost) + recommendedPosts + popularPosts + recentPosts
}
