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

package com.google.samples.apps.nowinandroid.feature.bookmarks

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the bookmarks screen.
 *
 * @property userDataRepository The repository for user data, injected by Hilt.
 * @property userNewsResourceRepository The repository for user news resources, injected by Hilt.
 */
@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
) : ViewModel() {

    /*
     * Whether to display the "undo" action for removing a bookmark.
     */
    var shouldDisplayUndoBookmark: Boolean by mutableStateOf(value = false)

    /**
     * The ID of the last removed bookmark.
     */
    private var lastRemovedBookmarkId: String? = null

    /**
     * A StateFlow emitting the current state of the news feed.
     *
     * We call the [Flow.map] method of the [Flow] of [List] of [UserNewsResource] property
     * [UserNewsResourceRepository.observeAllBookmarked] of our [UserNewsResourceRepository]
     * property [userNewsResourceRepository] with the `transform` lambda function the
     * [NewsFeedUiState.Success] method of the [NewsFeedUiState] sealed class to convert it to
     * a [Flow] of [NewsFeedUiState], and we chain it to the [Flow.onStart] method with its
     * [FlowCollector] `action` lambda function a lambda function that calls the [FlowCollector.emit]
     * method to emit a [NewsFeedUiState.Loading] value when starting, and then we call the
     * [Flow.stateIn] method to convert it to a [StateFlow] of [NewsFeedUiState].
     */
    val feedUiState: StateFlow<NewsFeedUiState> =
        userNewsResourceRepository.observeAllBookmarked()
            .map<List<UserNewsResource>, NewsFeedUiState>(transform = NewsFeedUiState::Success)
            .onStart { emit(value = Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = Loading,
            )

    /**
     * Removes the given news resource from the saved resources. We call the [CoroutineScope.launch]
     * method of the [ViewModel] property [viewModelScope] to launch a new coroutine and in its
     * [CoroutineScope] `block` lambda argument we set our [MutableState] wrapped [Boolean] property
     * [shouldDisplayUndoBookmark] to `true` and set our [String] property [lastRemovedBookmarkId]
     * to our [String] parameter [newsResourceId] and finally call the
     * [UserDataRepository.setNewsResourceBookmarked] method of our [UserDataRepository] property
     * [userDataRepository] with its `newsResourceId` argument our [String] parameter [newsResourceId]
     * and its `bookmarked` argument `false`.
     *
     * @param newsResourceId The ID of the news resource to remove.
     */
    fun removeFromSavedResources(newsResourceId: String) {
        viewModelScope.launch {
            shouldDisplayUndoBookmark = true
            lastRemovedBookmarkId = newsResourceId
            userDataRepository.setNewsResourceBookmarked(
                newsResourceId = newsResourceId,
                bookmarked = false,
            )
        }
    }

    /**
     * Sets the viewed state for a news resource. We call the [CoroutineScope.launch] method of the
     * [ViewModel] property [viewModelScope] to launch a new coroutine and in its [CoroutineScope]
     * `block` lambda argument we call the [UserDataRepository.setNewsResourceViewed] method of our
     * [UserDataRepository] property [userDataRepository] with its `newsResourceId` argument our
     * [String] parameter [newsResourceId] and its `viewed` argument our [Boolean] parameter
     * [viewed].
     *
     * @param newsResourceId The ID of the news resource.
     * @param viewed Whether the news resource has been viewed.
     */
    fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(
                newsResourceId = newsResourceId,
                viewed = viewed,
            )
        }
    }

    /**
     * Re-bookmarks the last removed news resource and clears the undo state.
     * This function is typically called when the user clicks an "undo" button
     * after removing a bookmark.
     *
     * We call the [CoroutineScope.launch] method of the [ViewModel] property [viewModelScope] to
     * launch a new coroutine and in its [CoroutineScope] `block` lambda argument if our [String]
     * property [lastRemovedBookmarkId] is not null we call the
     * [UserDataRepository.setNewsResourceBookmarked] method of our [UserDataRepository] property
     * [userDataRepository] with its `newsResourceId` argument our [String] property
     * [lastRemovedBookmarkId], and its `bookmarked` argument `true`.
     *
     * Finally, we call the [clearUndoState] method to clear the undo state.
     */
    fun undoBookmarkRemoval() {
        viewModelScope.launch {
            lastRemovedBookmarkId?.let {
                userDataRepository.setNewsResourceBookmarked(newsResourceId = it, bookmarked = true)
            }
        }
        clearUndoState()
    }

    /**
     * Clears the undo state by resetting the [shouldDisplayUndoBookmark] property to `false` and
     * setting the [lastRemovedBookmarkId] property to `null`. This is typically called after the
     * undo action has been performed or if the undo message is dismissed.
     */
    fun clearUndoState() {
        shouldDisplayUndoBookmark = false
        lastRemovedBookmarkId = null
    }
}
