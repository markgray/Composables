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

package com.example.jetnews.utils

import androidx.annotation.StringRes
import com.example.jetnews.R
import com.example.jetnews.data.Result
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.FakePostsRepository
import com.example.jetnews.ui.home.HomeUiState
import com.example.jetnews.ui.home.HomeViewModel
import java.util.UUID

/**
 * A [List] of these is created by [HomeViewModel.refreshPosts] for the [HomeUiState.errorMessages]
 * field of both [HomeUiState.NoPosts] and [HomeUiState.HasPosts] data classes, with a new instance
 * added every time the [PostsRepository.getPostsFeed] returns a [Result.Error] instead of a
 * [Result.Success] (this will happen every 5 requests thanks to the `shouldRandomlyFail` method of
 * [FakePostsRepository]).
 *
 * @param id the ID of this [ErrorMessage], [HomeViewModel.refreshPosts] uses [UUID.randomUUID] to
 * generate a [UUID] and uses the [UUID.getMostSignificantBits] to get the most significant 64 bits
 * when it creates a new [ErrorMessage] and this is the only place a new [ErrorMessage] is created.
 * @param messageId the resource ID of a [String] explaining this [ErrorMessage]. Always
 * [R.string.load_error] ("Can't update latest news").
 */
data class ErrorMessage(val id: Long, @StringRes val messageId: Int)
