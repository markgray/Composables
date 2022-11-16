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

package com.example.jetnews.data.posts

import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.model.Post
import com.example.jetnews.ui.home.HomeScreen
import com.example.jetnews.utils.addOrRemove
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex

/**
 * Simplified implementation of PostsRepository that returns a hardcoded list of
 * posts with resources synchronously.
 */
@Suppress("MemberVisibilityCanBePrivate")
class PostsRepository {
    /**
     * This [MutableStateFlow] will emit its value whenever it changes. [observeFavorites] returns
     * a reference to this private field as a [Flow] which a Composable could collect using the
     * `collectAsState` method which Collects values from the Flow and represents its latest value
     * via State (but no Composable in this app uses [observeFavorites]). It is updated by the
     * [toggleFavorite] method, but [toggleFavorite] is not called anywhere.
     */
    private val favorites = MutableStateFlow<Set<String>>(setOf())

    /**
     * Get a specific JetNews post. It searches the [List] of [Post] field [posts] using the [find]
     * exension method for a [Post] whose [Post.id] property is equal to our [String] parameter
     * [postId] and the first element it finds, or `null` if no such element was found. It is called
     * by the `ArticleScreen` Composable (see the file ui/article/ArticleScreen.kt)
     *
     * @param postId the [Post.id] string we are to search for in the [List] of [Post] field [posts].
     * @return the first [Post] in the [List] of [Post] field [posts] whose [Post.id] property matches
     * our parameter [postId] or `null` if no such [Post] was found.
     */
    fun getPost(postId: String?): Post? {
        return posts.find { it.id == postId }
    }

    /**
     * Get JetNews posts. Public read-only access to our [List] of [Post] field [posts]. It is called
     * by the [HomeScreen] Composable where it is used as its `posts` argument.
     *
     * @return a reference to our [List] of [Post] field [posts].
     */
    fun getPosts(): List<Post> {
        return posts
    }

    /**
     * Observe the current favorites. This method returns a read-only reference to our [MutableStateFlow]
     * field [favorites] which a Composable could collect from using the `collectAsState` method which
     * Collects values from the [Flow] and represents its latest value via State (but no Composable
     * in this app calls [observeFavorites]).
     *
     * @return a read-only reference to our private [MutableStateFlow] field [favorites]
     */
    fun observeFavorites(): Flow<Set<String>> = favorites

    /**
     * Toggle a [postId] to be a favorite or not. It retrieves the [MutableSet] of [TopicSelection]
     * from the [MutableStateFlow.value] of [favorites] to initialize the variable `val set`, then
     * uses the [MutableSet.addOrRemove] extension function to add [postId] to the `set` if it is
     * not already there or remove it if it is there. Finally it sets the value of [favorites] to
     * `set` (this will cause [favorites] to emit its new value to anyone collecting it). This
     * method is not called by anyone, but if it was it probably should be a suspend function with
     * its use of [favorites] protected by a [Mutex] like `toggleTopicSelection` does in
     * `InterestsRepository` (see the file data/interests/InterestsRepository.kt).
     *
     * @param postId the [String] whose presence in the [Set] of [String] held in the [MutableStateFlow]
     * field [favorites] needs to be toggled.
     */
    fun toggleFavorite(postId: String) {
        val set = favorites.value.toMutableSet()
        set.addOrRemove(postId)
        favorites.value = set
    }
}
