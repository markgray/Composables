/*
 * Copyright 2020 Google LLC
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

@file:Suppress("unused")

package com.google.samples.apps.sunflower.viewmodels

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.samples.apps.sunflower.data.UnsplashPhoto
import com.google.samples.apps.sunflower.data.UnsplashRepository
import com.google.samples.apps.sunflower.compose.gallery.GalleryScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for the [GalleryScreen]..
 *
 * This ViewModel is responsible for fetching and exposing a stream of paginated photos
 * from the Unsplash API related to a specific plant query. The query string is passed
 * via a [SavedStateHandle]. The fetched data is exposed as a [Flow] of [PagingData].
 *
 * The @[HiltViewModel] annotation identifies a ViewModel for construction injection.
 * The [ViewModel] annotated with [HiltViewModel] will be available for creation by the
 * `HiltViewModelFactory` and can be retrieved by using the [hiltViewModel] method. The
 * [HiltViewModel] containing a constructor annotated with @[Inject] will have its
 * dependencies defined in the constructor parameters injected by Dagger's Hilt.
 *
 * @param savedStateHandle A handle to the saved state of the ViewModel, used to retrieve
 * the plant name query passed from the previous screen.
 * @param repository The repository for fetching photo data from Unsplash.
 */
@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: UnsplashRepository
) : ViewModel() {

    /**
     * The query string to search for in the Unsplash API.
     *
     * This value is retrieved from the [SavedStateHandle] which is passed from the
     * previous screen, typically containing the name of a plant. It is used to
     * fetch relevant photos from the Unsplash repository.
     */
    private var queryString: String? = savedStateHandle["plantName"]


    /**
     * A mutable state flow that holds the paginated data of plant pictures.
     * This is a private property and is used to update the picture data from within the ViewModel.
     * It is initialized with `null` and populated by the [refreshData] function.
     */
    private val _plantPictures: MutableStateFlow<PagingData<UnsplashPhoto>?> =
        MutableStateFlow(value = null)

    /**
     * A public [Flow] of [PagingData] that contains the stream of plant pictures.
     *
     * This flow is collected by the UI to display a paginated list of photos. It is derived
     * from the private [MutableStateFlow] of [PagingData] of [UnsplashPhoto] property
     * [_plantPictures], filtering out any `null` values to ensure the UI only receives valid data.
     */
    val plantPictures: Flow<PagingData<UnsplashPhoto>> get() = _plantPictures.filterNotNull()

    init {
        refreshData()
    }


    /**
     * Refreshes the data by fetching a new stream of paginated photos from the repository.
     *
     * This function is called during the initialization of the ViewModel to load the initial
     * data. It launches a coroutine in the [viewModelScope] to perform the asynchronous
     * data fetching. The fetched data, a [Flow] of [PagingData], is then cached within the
     * [viewModelScope] to survive configuration changes and assigned to the [MutableStateFlow] of
     * [PagingData] of [UnsplashPhoto] property [_plantPictures]. Any exceptions during the fetch
     * are caught and printed to the stack trace.
     */
    fun refreshData() {
        viewModelScope.launch {
            try {
                _plantPictures.value = repository
                    .getSearchResultStream(query = queryString ?: "")
                    .cachedIn(scope = viewModelScope)
                    .first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}