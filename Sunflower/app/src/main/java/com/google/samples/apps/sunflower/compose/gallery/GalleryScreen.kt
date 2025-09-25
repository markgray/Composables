/*
 * Copyright 2023 Google LLC
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

package com.google.samples.apps.sunflower.compose.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.compose.plantlist.PhotoListItem
import com.google.samples.apps.sunflower.data.UnsplashPhoto
import com.google.samples.apps.sunflower.data.UnsplashPhotoUrls
import com.google.samples.apps.sunflower.data.UnsplashUser
import com.google.samples.apps.sunflower.viewmodels.GalleryViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Displays a gallery of Unsplash photos of plants.
 *
 * This composable function is the entry point for the gallery screen. It observes the
 * [GalleryViewModel] for a list of [UnsplashPhoto]s and displays them in a vertically
 * scrollable grid.
 *
 * We call our stateless [GalleryScreen] composable from this function with the arguments:
 *   - `plantPictures`: The [Flow] of [PagingData] of [UnsplashPhoto]s returned by the
 *   [GalleryViewModel.plantPictures] property of our [GalleryViewModel] parameter [viewModel].
 *   - `onPhotoClick`: A lambda function that is called when a photo in the gallery is clicked,
 *   our lambda parameter [onPhotoClick].
 *   - `onUpClick`: A lambda function that is called when the "up" button in the top app bar
 *   is clicked, our lambda parameter [onUpClick].
 *   - `onPullToRefresh`: A lambda function that is called when the user initiates a pull-to-refresh
 *   action, a function reference to the [GalleryViewModel.refreshData] of our [GalleryViewModel]
 *   parameter [viewModel].
 *
 * @param viewModel The [GalleryViewModel] used to fetch plant photos. Injected by Hilt.
 * @param onPhotoClick Called when a photo in the gallery is clicked. The clicked
 * [UnsplashPhoto] is passed as a parameter.
 * @param onUpClick Called when the "up" button in the top app bar is clicked.
 */
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(value = LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        }, key = null
    ),
    onPhotoClick: (UnsplashPhoto) -> Unit,
    onUpClick: () -> Unit,
) {
    GalleryScreen(
        plantPictures = viewModel.plantPictures,
        onPhotoClick = onPhotoClick,
        onUpClick = onUpClick,
        onPullToRefresh = viewModel::refreshData,
    )
}
/**
 * Shows a [LazyVerticalGrid] of plant pictures.
 * TODO: Continue here.
 *
 * @param plantPictures Flow of PagingData holding the pictures to display
 * @param onPhotoClick callback when a photo is clicked
 * @param onUpClick callback when the Up button is clicked
 * @param onPullToRefresh callback when the user pulls to refresh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryScreen(
    plantPictures: Flow<PagingData<UnsplashPhoto>>,
    onPhotoClick: (UnsplashPhoto) -> Unit = {},
    onUpClick: () -> Unit = {},
    onPullToRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            GalleryTopBar(onUpClick = onUpClick)
        },
    ) { padding: PaddingValues ->

        val pagingItems: LazyPagingItems<UnsplashPhoto> =
            plantPictures.collectAsLazyPagingItems()

        var isRefreshing: Boolean by remember { mutableStateOf(value = false) }

        // TODO: LaunchedEffect to set isRefreshing = false when pagingItems.loadState.refresh completes.
        //  This can't be done in the same way as before because PullToRefreshBox doesn't expose
        //  a means of ending the refresh from outside the composable.

        Box(
            modifier = Modifier
                .padding(paddingValues = padding)
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    onPullToRefresh()
                },
                modifier = Modifier.align(alignment = Alignment.TopCenter),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count = 2),
                    contentPadding = PaddingValues(all = dimensionResource(id = R.dimen.card_side_margin))
                ) {
                    items(
                        count = pagingItems.itemCount,
                        key = pagingItems.itemKey { it.id }
                    ) { index ->
                        val photo: UnsplashPhoto = pagingItems[index] ?: return@items
                        PhotoListItem(photo = photo) {
                            onPhotoClick(photo)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryTopBar(
    onUpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.gallery_title))
        },
        modifier = modifier.statusBarsPadding(),
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
    )
}

@Preview
@Composable
private fun GalleryScreenPreview(
    @PreviewParameter(provider = GalleryScreenPreviewParamProvider::class) plantPictures: Flow<PagingData<UnsplashPhoto>>
) {
    GalleryScreen(plantPictures = plantPictures, onPullToRefresh = {})
}

private class GalleryScreenPreviewParamProvider :
    PreviewParameterProvider<Flow<PagingData<UnsplashPhoto>>> {

    override val values: Sequence<Flow<PagingData<UnsplashPhoto>>> =
        sequenceOf(
            element = flowOf(
                value = PagingData.from(
                    data = listOf(
                        UnsplashPhoto(
                            id = "1",
                            urls = UnsplashPhotoUrls("https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max"),
                            user = UnsplashUser("John Smith", "johnsmith")
                        ),
                        UnsplashPhoto(
                            id = "2",
                            urls = UnsplashPhotoUrls("https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max"),
                            user = UnsplashUser("Sally Smith", "sallysmith")
                        )
                    )
                )
            ),
        )
}
