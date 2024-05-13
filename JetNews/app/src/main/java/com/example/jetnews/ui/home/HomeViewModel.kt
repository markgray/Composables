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

@file:Suppress("RedundantValueArgument")

package com.example.jetnews.ui.home

import androidx.compose.material3.Snackbar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.model.Post
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.utils.ErrorMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * UI state for the Home route.
 *
 * This is derived from [HomeViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface HomeUiState {

    /**
     * If `true` the [List] of [Post] dataset is being loaded.
     */
    val isLoading: Boolean

    /**
     * The [List] of [ErrorMessage] that is added to when [PostsRepository.getPostsFeed] returns a
     * [Result.Error], and removed from when [HomeViewModel.errorShown] is called after the error
     * [Snackbar] displaying the error has been dismissed.
     */
    val errorMessages: List<ErrorMessage>

    /**
     * The current search string that the user has entered.
     */
    val searchInput: String

    /**
     * There are no posts to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : HomeUiState

    /**
     * There are posts to render, as contained in [postsFeed].
     *
     * There is guaranteed to be a [selectedPost], which is one of the posts from [postsFeed].
     */
    data class HasPosts(
        /**
         * The [PostsFeed] returned by the [PostsRepository.getPostsFeed] method in the [Result]
         * of [PostsFeed] that it returns.
         */
        val postsFeed: PostsFeed,
        /**
         * The [Post] in the [PostsFeed] whose [Post.id] is the same as the ID of the selected [Post]
         * ir the [PostsFeed.highlightedPost]
         */
        val selectedPost: Post,
        /**
         * Used when the [WindowWidthSizeClass] of the device we are running on is not
         * [WindowWidthSizeClass.Expanded] (ie. its a phone). If `true` the selected [Post] is being
         * displayed instead of the [PostsFeed].
         */
        val isArticleOpen: Boolean,
        /**
         * The [Set] of [Post.id] of [Post]'s that the user has checked the "favorite" button on.
         */
        val favorites: Set<String>,
        /**
         * If `true` the [List] of [Post] dataset is being loaded.
         */
        override val isLoading: Boolean,
        /**
         * The [List] of [ErrorMessage] that is added to when [PostsRepository.getPostsFeed] returns a
         * [Result.Error], and removed from when [HomeViewModel.errorShown] is called after the error
         * [Snackbar] displaying the error has been dismissed.
         */
        override val errorMessages: List<ErrorMessage>,
        /**
         * The current search string that the user has entered.
         */
        override val searchInput: String
    ) : HomeUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class HomeViewModelState(
    /**
     * The [PostsFeed] returned by the [PostsRepository.getPostsFeed] method in the [Result]
     * of [PostsFeed] that it returns.
     */
    val postsFeed: PostsFeed? = null,
    /**
     * The [Post] in the [PostsFeed] whose [Post.id] is the same as the ID of the selected [Post]
     * ir the [PostsFeed.highlightedPost]
     */
    val selectedPostId: String? = null, // TODO back selectedPostId in a SavedStateHandle
    /**
     * Used when the [WindowWidthSizeClass] of the device we are running on is not
     * [WindowWidthSizeClass.Expanded] (ie. its a phone). If `true` the selected [Post] is being
     * displayed instead of the [PostsFeed].
     */
    val isArticleOpen: Boolean = false,
    /**
     * The [Set] of [Post.id] of [Post]'s that the user has checked the "favorite" button on.
     */
    val favorites: Set<String> = emptySet(),
    /**
     * If `true` the [List] of [Post] dataset is being loaded.
     */
    val isLoading: Boolean = false,
    /**
     * The [List] of [ErrorMessage] that is added to when [PostsRepository.getPostsFeed] returns a
     * [Result.Error], and removed from when [HomeViewModel.errorShown] is called after the error
     * [Snackbar] displaying the error has been dismissed.
     */
    val errorMessages: List<ErrorMessage> = emptyList(),
    /**
     * The current search string that the user has entered.
     */
    val searchInput: String = "",
) {

    /**
     * Converts this [HomeViewModelState] into a more strongly typed [HomeUiState] for driving
     * the ui. We branch on the value of [postsFeed]:
     *  - `null` we construct and return a [HomeUiState.NoPosts] whose [HomeUiState.NoPosts.isLoading]
     *  is our [isLoading] property, whose [HomeUiState.NoPosts.errorMessages] is our [errorMessages]
     *  property, and whose [HomeUiState.NoPosts.searchInput] is our [searchInput] property.
     *  - non-`null` we construct and return a [HomeUiState.HasPosts] whose [HomeUiState.HasPosts.postsFeed]
     *  is our [postsFeed] property, whose [HomeUiState.HasPosts.selectedPost] is the [Post] we `find`
     *  in the [List] of [Post] property [PostsFeed.allPosts] of [postsFeed] whose [Post.id] is equal
     *  to our [String] property [selectedPostId] defaulting to the [PostsFeed.highlightedPost] of
     *  [postsFeed] if none is found, whose [HomeUiState.HasPosts.isArticleOpen] property is our
     *  [Boolean] property [isArticleOpen], whose [HomeUiState.HasPosts.favorites] is our [Set] of
     *  [String] property [favorites], whose [HomeUiState.HasPosts.isLoading] is our [Boolean] property
     *  [isLoading], whose [HomeUiState.HasPosts.errorMessages] is our [List] of [ErrorMessage]
     *  property [errorMessages], and whose [HomeUiState.HasPosts.searchInput] is our [String] property
     *  [searchInput].
     */
    fun toUiState(): HomeUiState =
        if (postsFeed == null) {
            HomeUiState.NoPosts(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            HomeUiState.HasPosts(
                postsFeed = postsFeed,
                // Determine the selected post. This will be the post the user last selected.
                // If there is none (or that post isn't in the current feed), default to the
                // highlighted post
                selectedPost = postsFeed.allPosts.find {
                    it.id == selectedPostId
                } ?: postsFeed.highlightedPost,
                isArticleOpen = isArticleOpen,
                favorites = favorites,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

/**
 * ViewModel that handles the business logic of the Home screen
 *
 * @param postsRepository the [PostsRepository] that we should use to fetch our [PostsFeed] from.
 * @param preSelectedPostId use to set the [HomeViewModelState.selectedPostId] property of our
 * [MutableStateFlow] wrapped [HomeViewModelState] when we are first constructed.
 */
class HomeViewModel(
    private val postsRepository: PostsRepository,
    preSelectedPostId: String?
) : ViewModel() {

    /**
     * Tne [MutableStateFlow] wrapped [HomeViewModelState] that we use to update our publicly readable
     * [StateFlow] wrapped [HomeUiState]. Our initial value is a [HomeViewModelState] whose [Boolean]
     * property [HomeViewModelState.isLoading] is `true`, whose [HomeViewModelState.selectedPostId]
     * if the [String] `preSelectedPostId` we were constructed with, and whose [Boolean] property
     * [HomeViewModelState.isArticleOpen] is `true` if `preSelectedPostId` is not `null`.
     */
    private val viewModelState: MutableStateFlow<HomeViewModelState> = MutableStateFlow(
        HomeViewModelState(
            isLoading = true,
            selectedPostId = preSelectedPostId,
            isArticleOpen = preSelectedPostId != null
        )
    )

    /**
     * UI state exposed to the UI. Our [StateFlow] wrapped [HomeUiState] is created by applying the
     * [map] extension function to our [MutableStateFlow] wrapped [HomeViewModelState] property
     * [viewModelState] applying the `transform` [HomeViewModelState.toUiState] to each of the
     * [HomeViewModelState] passed it and emitting a [Flow] of [HomeUiState] which is chained to a
     * [Flow.stateIn] whose `scope` argument is [viewModelScope], whose `started` argument is
     * [SharingStarted.Eagerly] (sharing is started immediately and never stops), and whose
     * `initialValue` is the [HomeViewModelState.toUiState] of the [MutableStateFlow.value] of our
     * [MutableStateFlow] wrapped [HomeViewModelState] property  [viewModelState].
     */
    val uiState: StateFlow<HomeUiState> = viewModelState
        .map(transform = HomeViewModelState::toUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value.toUiState()
        )

    init {
        refreshPosts()

        /**
         * Observe for favorite changes in the repo layer
         */
        viewModelScope.launch {
            postsRepository.observeFavorites().collect { favorites: Set<String> ->
                viewModelState.update { it.copy(favorites = favorites) }
            }
        }
    }

    /**
     * Refresh posts and update the UI state accordingly. We first call the [MutableStateFlow.update]
     * method of [MutableStateFlow] wrapped [HomeViewModelState] property [viewModelState] to update
     * its [HomeViewModelState.isLoading] properyt to `true` (Ui state is refreshing). Then we use the
     * [CoroutineScope] of [viewModelScope] to [CoroutineScope.launch] a coroutine in whose [CoroutineScope]
     * lambda `block` we initialize our [Result] of [PostsFeed] variable `val result` to the value returned
     * by the [PostsRepository.getPostsFeed] method of our [PostsRepository] field [postsRepository].
     * Then we call the [MutableStateFlow.update] method of [viewModelState] to update it based on the
     * type of [Result] of [Result] of [PostsFeed] variable `result`:
     *  - [Result.Success] we copy the current [HomeViewModelState] with its [HomeViewModelState.postsFeed]
     *  changed to the [Result.Success.data] of `result`, and its [HomeViewModelState.isLoading] changed
     *  to `false`.
     *  - [Result.Error] we initialize our [List] of [ErrorMessage] variable `val errorMessages` to the
     *  current [HomeViewModelState.errorMessages] property appending a new instance of [ErrorMessage]
     *  whose [ErrorMessage.id] is the [UUID.getMostSignificantBits] of the random [UUID] returned
     *  by [UUID.randomUUID], and whose [ErrorMessage.messageId] is the resource ID [R.string.load_error]
     *  (points to the [String] ("Can't update latest news"). We then update [viewModelState] with a
     *  copy whose [HomeViewModelState.errorMessages] is our variable `errorMessages`, and whose
     *  [HomeViewModelState.isLoading] property is `false`.
     */
    fun refreshPosts() {
        // Ui state is refreshing
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result: Result<PostsFeed> = postsRepository.getPostsFeed()
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(postsFeed = result.data, isLoading = false)
                    is Result.Error -> {
                        val errorMessages: List<ErrorMessage> = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    /**
     * Toggle favorite of a post.
     *
     * @param
     */
    fun toggleFavourite(postId: String) {
        viewModelScope.launch {
            postsRepository.toggleFavorite(postId = postId)
        }
    }

    /**
     * Selects the given article to view more information about it.
     */
    fun selectArticle(postId: String) {
        // Treat selecting a detail as simply interacting with it
        interactedWithArticleDetails(postId = postId)
    }

    /**
     * Notify that an error was displayed on the screen
     */
    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState: HomeViewModelState ->
            val errorMessages: List<ErrorMessage> =
                currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    /**
     * Notify that the user interacted with the feed
     */
    fun interactedWithFeed() {
        viewModelState.update {
            it.copy(isArticleOpen = false)
        }
    }

    /**
     * Notify that the user interacted with the article details
     */
    fun interactedWithArticleDetails(postId: String) {
        viewModelState.update {
            it.copy(
                selectedPostId = postId,
                isArticleOpen = true
            )
        }
    }

    /**
     * Notify that the user updated the search query
     */
    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
    }

    /**
     * Factory for HomeViewModel that takes PostsRepository as a dependency
     */
    companion object {
        /**
         * TODO: Add kdoc
         */
        fun provideFactory(
            postsRepository: PostsRepository,
            preSelectedPostId: String? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    postsRepository = postsRepository,
                    preSelectedPostId = preSelectedPostId
                ) as T
            }
        }
    }
}
