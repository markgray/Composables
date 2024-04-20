/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetnews.data.posts.impl

import androidx.glance.appwidget.GlanceAppWidget
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.glance.ui.JetnewsGlanceAppWidget
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.ui.home.HomeUiState
import com.example.jetnews.ui.home.HomeViewModel
import com.example.jetnews.utils.ErrorMessage
import com.example.jetnews.utils.addOrRemove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

/**
 * Implementation of [PostsRepository] that returns a hardcoded list of
 * posts with resources after some delay in a background thread.
 */
class FakePostsRepository : PostsRepository {

    /**
     * This is the [MutableStateFlow] wrapped [Set] of [String] which represents the Post's which
     * the user has marked as their favorites. Our [observeFavorites] method returns a read-only
     * [Flow] of [Set] of [String] reference to this. [HomeViewModel] updates its [StateFlow] of
     * [HomeUiState] property [HomeViewModel.uiState] from this using the [stateIn] extension
     * function of [Flow] to convert the cold [Flow] that we return to a hot [StateFlow] that is
     * started in the [viewModelScope] coroutine scope, sharing the most recently emitted value from
     * a single running instance of the upstream flow with multiple downstream subscribers.
     * NOTE: There is an intermediate private [MutableStateFlow] wrapped `HomeViewModelState` which
     * a [viewModelScope] coroutine launched in the [HomeViewModel] init block updates by calling
     * the [Flow.collect] method of the value returned by [observeFavorites], which on update cause
     * the [HomeUiState] to be updated.
     */
    private val favorites = MutableStateFlow<Set<String>>(value = setOf())

    /**
     * This is the [MutableStateFlow] wrapped [PostsFeed] which is updated to our fake [PostsFeed]
     * global variable [posts] by our [getPostsFeed] method. Our [observePostsFeed] method returns
     * a read-only [Flow] of [PostsFeed] reference to this. Our [GlanceAppWidget] class
     * [JetnewsGlanceAppWidget] uses this as the `posts` argument of its `JetnewsContent` private
     * compose widget.
     */
    private val postsFeed = MutableStateFlow<PostsFeed?>(value = null)

    // Used to make suspend functions that read and update state safe to call from any thread

    /**
     * Tries to find the [Post] whose [Post.id] is equal to our [String] parameter [postId] in the
     * [List] of [Post] returned by the [PostsFeed.allPosts] property of [PostsFeed] global variable
     * [posts] and if successful returns that [Post] as the [Result.Success.data] of a new instance
     * of [Result.Success] otherwise it returns a [Result.Error] whose [Result.Error.exception] is
     * an [IllegalArgumentException] whose String argument is "Unable to find post".
     *
     * @param postId the [Post.id] of the [Post] we are to look for
     * @return a [Result.Success] whose [Result.Success.data] is the [Post] requested if we find it,
     * or a [Result.Error] whose [Result.Error.exception] is an [IllegalArgumentException] whose
     * [String] argument is "Unable to find post".
     */
    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(context = Dispatchers.IO) {
            val post: Post? = posts.allPosts.find { it.id == postId }
            if (post == null) {
                Result.Error(exception = IllegalArgumentException("Post not found"))
            } else {
                Result.Success(data = post)
            }
        }
    }

    /**
     * Delays for 800 milliseconds to pretend we're on a slow network, then if our [shouldRandomlyFail]
     * method returns `true` (which it does every fifth time it is called) returns a [Result.Error]
     * whose [Result.Error.exception] is a new instance of [IllegalStateException], and if
     * [shouldRandomlyFail] returns `false` it calls the [MutableStateFlow.update] methd of our
     * [MutableStateFlow] wrapped [PostsFeed] property [postsFeed] to set it to the latest value
     * of our global [PostsFeed] variable [posts] and then returns a [Result.Success] whose
     * [Result.Success.data] is [posts]. The [HomeViewModel.refreshPosts] method calls this method
     * and updates its private [MutableStateFlow] wrapped `HomeViewModelState` with either the
     * [PostsFeed] in a [Result.Success.data] returned or if a [Result.Error] is returned adds an
     * [ErrorMessage] to the [List] of [ErrorMessage] of the `HomeViewModelState`. The
     * [StateFlow] wrapped [HomeUiState] property [HomeViewModel.uiState] uses the [Flow.stateIn]
     * method to convert this cold [Flow] into a hot [StateFlow] that is started in the [viewModelScope]
     * coroutine scope, sharing the most recently emitted value from a single running instance of the
     * upstream flow with multiple downstream subscribers.
     *
     * @return a [Result.Error] whose [Result.Error.exception] is an [IllegalStateException] if our
     * [shouldRandomlyFail] method returns `true` this time, or a [Result.Success] whose
     * [Result.Success.data] is our [PostsFeed] global variable [posts].
     */
    override suspend fun getPostsFeed(): Result<PostsFeed> {
        return withContext(context = Dispatchers.IO) {
            delay(timeMillis = 800) // pretend we're on a slow network
            if (shouldRandomlyFail()) {
                Result.Error(exception = IllegalStateException())
            } else {
                postsFeed.update { posts }
                Result.Success(data = posts)
            }
        }
    }

    /**
     * Returns a read-only [Flow] of [Set] of [String] reference to our [MutableStateFlow] wrapped
     * [Set] of [String] property [favorites].
     */
    override fun observeFavorites(): Flow<Set<String>> = favorites

    /**
     * Returns a read-only [Flow] of [PostsFeed] reference to our [MutableStateFlow] wrapped
     * [PostsFeed] property [postsFeed]
     */
    override fun observePostsFeed(): Flow<PostsFeed?> = postsFeed

    /**
     * Toggles the presence of our [String] parameter [postId] in our [MutableStateFlow] wrapped
     * [Set] of [String] property [favorites].
     */
    override suspend fun toggleFavorite(postId: String) {
        favorites.update {
            it.addOrRemove(postId)
        }
    }

    /**
     * used to drive "random" failure in a predictable pattern, making the first request
     * always succeed
     */
    private var requestCount = 0

    /**
     * Randomly fail some loads to simulate a real network. This will fail by returning `true`
     * deterministically every 5 requests.
     *
     * @return `true` if the "download" should fail this time, or `false` if it should succeed.
     */
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}
