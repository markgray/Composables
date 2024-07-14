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

package androidx.compose.samples.crane.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.details.launchDetailsActivity
import androidx.compose.samples.crane.home.OnExploreItemClicked
import androidx.compose.samples.crane.ui.crane_caption
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.samples.crane.details.DetailsActivity
import androidx.compose.samples.crane.home.CraneHomeContent
import androidx.compose.samples.crane.home.CraneScreen
import androidx.compose.samples.crane.home.HomeTabBar
import androidx.compose.samples.crane.home.MainActivity
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.text.TextStyle
import com.google.maps.android.compose.GoogleMap

/**
 * This is used in [CraneHomeContent] for the each of the three `pageContent` of the [HorizontalPager]
 * used as the `frontLayerContent` of its [BackdropScaffold]. These three [ExploreSection] are switched
 * by the [HomeTabBar] that is used as the `appBar` argument of the [BackdropScaffold]. The first is
 * used to display the [CraneScreen.Fly] "Explore Flights by Destination" [ExploreSection], the second
 * is used to display the [CraneScreen.Sleep] "Explore Properties by Destination" [ExploreSection],
 * and the third is used to display the [CraneScreen.Eat] "Explore Restaurants by Destination"
 * [ExploreSection]. In each case it displays its [List] of [ExploreModel] parameter [exploreList]
 * in a [LazyVerticalStaggeredGrid] using an [ExploreItemColumn] when its [WindowWidthSizeClass]
 * parameter is [WindowWidthSizeClass.Medium] or [WindowWidthSizeClass.Expanded] and using a
 * [ExploreItemRow] is its is [WindowWidthSizeClass.Compact] (a phone).
 *
 * Our root composable is a [Surface] whose `modifier` argument chains a [Modifier.fillMaxSize] to
 * our [Modifier] parameter [modifier], and whose `color` argument is [Color.White]. Its `content`
 * is a [Column] whose `modifier` argument is a [Modifier.padding] that adds 24.dp to the `start`,
 * 20.dp to the `top` and 24.dp to the end. The `content` of the [Column] holds:
 *  - a [Text] whose `text` is our [String] parameter [title]. and whose `style` is a copy of the
 *  [Typography.caption] of our [CraneTheme] custom [MaterialTheme] whose `color` is overridden to
 *  be [crane_caption] ([Color.DarkGray]).
 *  - a [Spacer] whose `modifier` is a [Modifier.height] that sets its height to 8.dp
 *  - a [LazyVerticalStaggeredGrid]
 *
 * The arguments of the [LazyVerticalStaggeredGrid] are:
 *  - `columns` (description of the size and number of staggered grid columns) is `minSize` of 200.dp
 *  [StaggeredGridCells.Adaptive] (Defines a grid with as many rows or columns as possible on the
 *  condition that every cell has at least 200.dp space and all extra space is distributed evenly)
 *  - `modifier` is a [Modifier.fillMaxWidth] to which is chained a [Modifier.imePadding] (causes
 *  the [LazyVerticalStaggeredGrid] to occupy its entire incoming width constraint, and adds padding
 *  to accommodate the ime insets)
 *  - `horizontalArrangement` is [Arrangement.spacedBy] of 8.dp (places children such that each two
 *  adjacent ones are spaced by 8.dp across the main axis)
 *
 * In the `content` lambda of the [LazyVerticalStaggeredGrid] we use the [itemsIndexed] extension
 * function to loop through all of the [ExploreModel] in our [List] of [ExploreModel] parameter
 * [exploreList] capturing the [ExploreModel] in our `exploreItem` variable. Then using a when switch
 * to branch based on the value of our [WindowWidthSizeClass] parameter:
 *  - [WindowWidthSizeClass.Medium] or [WindowWidthSizeClass.Expanded]: We compose an [ExploreItemColumn]
 *  whose `modifier` argument is a [Modifier.fillMaxWidth] to have it take up all or its incoming width
 *  constraint, its `item` argument is the current [ExploreModel] in our `exploreItem` variable, and its
 *  `onItemClicked` argument is our [onItemClicked] lambda parameter.
 *  - [WindowWidthSizeClass.Compact] (or any other): We compose an [ExploreItemRow] whose `modifier`
 *  argument is a [Modifier.fillMaxWidth] to have it take up all or its incoming width constraint,
 *  its `item` argument is the current [ExploreModel] in our `exploreItem` variable, and its
 *  `onItemClicked` argument is our [onItemClicked] lambda parameter.
 *
 * At the bottom of the `content` of the [LazyVerticalStaggeredGrid] is a single
 * [LazyStaggeredGridScope.item] whose `span` argument is [StaggeredGridItemSpan.FullLine] (causing
 * it to take up an entire line by itself) that holds a [Spacer] whose `modifier` argument is a
 * [Modifier.windowInsetsBottomHeight] whose `insets` argument is [WindowInsets.Companion.navigationBars]
 * (which sets the height to that of insets at the bottom of the screen where system UI places
 * navigation bars).
 *
 * @param widthSize the [WindowWidthSizeClass] of the device we are running on, one of
 * [WindowWidthSizeClass.Medium] (the majority of tablets in portrait and large unfolded inner
 * displays in portrait), [WindowWidthSizeClass.Expanded] (the majority of tablets in landscape and
 * large unfolded inner displays in landscape), or [WindowWidthSizeClass.Compact] (the majority of
 * phones in portrait).
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [CraneHomeContent] does not pass us any, so the empty, default, or starter
 * [Modifier] that contains no elements is used.
 * @param title the title of our contents, the [CraneScreen.Fly] tab uses "Explore Flights by Destination",
 * the [CraneScreen.Sleep] tab uses "Explore Properties by Destination" and the [CraneScreen.Eat] tab
 * uses "Explore Restaurants by Destination". This is displayed in a [Text] at the top of the [Column]
 * contents of our root [Surface] Composable.
 * @param exploreList the [List] of [ExploreModel] that we are to display.
 * @param onItemClicked an [OnExploreItemClicked] lambda that we pass to [ExploreItemColumn] and
 * [ExploreItemRow] in their `onItemClicked` argument. They in turn use it as the `onClick` lambda
 * argument of the [Modifier.clickable] that they use as the `modifier` argument of their [Column]
 * or [Row] root composables, with it being called with the [ExploreModel] that it is displaying.
 * It traces back up the Composable hierarchy to a lambda created in the `onCreate` override of
 * [MainActivity] that calls the [launchDetailsActivity] method to have it launch the [DetailsActivity]
 * to display a [GoogleMap] for the [ExploreModel] that was clicked.
 */
@Composable
fun ExploreSection(
    widthSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    title: String,
    exploreList: List<ExploreModel>,
    onItemClicked: OnExploreItemClicked
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(minSize = 200.dp),
                modifier = Modifier.fillMaxWidth().imePadding(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    itemsIndexed(exploreList) { _, exploreItem: ExploreModel ->
                        when (widthSize) {
                            WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
                                ExploreItemColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    item = exploreItem,
                                    onItemClicked = onItemClicked
                                )
                            }
                            else -> {
                                ExploreItemRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    item = exploreItem,
                                    onItemClicked = onItemClicked
                                )
                            }
                        }
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(
                            modifier = Modifier
                                .windowInsetsBottomHeight(insets = WindowInsets.navigationBars)
                        )
                    }
                }
            )
        }
    }
}

/**
 * Composable with large image card and text underneath. Used in [ExploreSection] in the `content`
 * of the [LazyVerticalStaggeredGrid] when the [WindowWidthSizeClass] parameter of [ExploreSection]
 * is either [WindowWidthSizeClass.Medium] (the majority of tablets in portrait and large unfolded
 * inner displays in portrait), or [WindowWidthSizeClass.Expanded] (the majority of tablets in
 * landscape and large unfolded inner displays in landscape). Our root Composable is a [Column]
 * whose `modifier` argument chains a [Modifier.clickable] to our [Modifier] parameter [modifier]
 * whose lambda argument calls our [onItemClicked] lambda parameter with our [ExploreModel] parameter
 * [item], and to that is chained a [Modifier.padding] that adds 12.dp padding to the `top` of the
 * [Column], and 12.dp padding to the `bottom` of the [Column]. The `content` of the [Column] is:
 *  - an [ExploreImageContainer] whose `modifier` arugment is a [Modifier.fillMaxWidth] holding a
 *  [ExploreImage] whose `item` argument is our [ExploreModel] parameter [item] (this downloads
 *  and displays the picture whose URL is the [ExploreModel.imageUrl] property of [item]).
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] setting its height to 8.dp.
 *  - a [Column] whose `modifier` argument is a [Modifier.fillMaxWidth] (occupies its entire incoming
 *  width constraint) to which is chained a [Modifier.wrapContentHeight] (allows its content to
 *  measure at its desired height without regard to the height constraints). The `content` of the
 *  inner [Column] consists of:
 *  - a [Text] whose `modifier` argument is a [Modifier.fillMaxWidth] (to have it take up its entire
 *  incoming width constraint), whose `text` is the [City.nameToDisplay] of the [ExploreModel.city]
 *  of our [ExploreModel] parameter [item], and whose [TextStyle] `style` argument is the
 *  [Typography.subtitle1] of our custom [MaterialTheme.typography].
 *  - a [Spacer] whose `modifier` argument is a [Modifier.height] that sets its height to 4.dp
 *  - a [Text] whose `modifier` argument is a [Modifier.fillMaxWidth] (to have it take up its entire
 *  incoming width constraint), whose `text` is the [ExploreModel.description] property of our
 *  [ExploreModel] parameter [item], and whose [TextStyle] `style` argument is a copy of the
 *  [Typography.caption] of our custom [MaterialTheme.typography] with its color overridden by
 *  [crane_caption] ([Color.DarkGray]).
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [ExploreSection] passes us a [Modifier.fillMaxWidth] to have us take up our
 * entire incoming width constraint.
 * @param item the [ExploreModel] whose information we are to display.
 * @param onItemClicked a lambda we should call with our [item] parameter when we are clicked. Traced
 * up the Composable hierarchy we end up with a lambda created in the `onCreate` override of
 * [MainActivity] that calls the [launchDetailsActivity] method to have it launch the [DetailsActivity]
 * to display a [GoogleMap] for the [ExploreModel] that it is called with.
 */
@Composable
private fun ExploreItemColumn(
    modifier: Modifier = Modifier,
    item: ExploreModel,
    onItemClicked: OnExploreItemClicked
) {
    Column(
        modifier = modifier
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        ExploreImageContainer(modifier = Modifier.fillMaxWidth()) {
            ExploreImage(item = item)
        }
        Spacer(modifier = Modifier.height(height = 8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.city.nameToDisplay,
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.height(height = 4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.description,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
        }
    }
}

@Composable
private fun ExploreItemRow(
    modifier: Modifier = Modifier,
    item: ExploreModel,
    onItemClicked: OnExploreItemClicked
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        ExploreImageContainer(modifier = Modifier.size(64.dp)) {
            ExploreImage(item)
        }
        Spacer(Modifier.width(24.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = item.city.nameToDisplay,
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
        }
    }
}

@Composable
private fun ExploreImage(item: ExploreModel) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(item.imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun ExploreImageContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(modifier.wrapContentHeight().fillMaxWidth(), RoundedCornerShape(4.dp)) {
        content()
    }
}
