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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.samples.crane.R
import androidx.compose.samples.crane.data.City
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.details.DetailsActivity
import androidx.compose.samples.crane.details.launchDetailsActivity
import androidx.compose.samples.crane.home.CraneScreen
import androidx.compose.samples.crane.home.MainActivity
import androidx.compose.samples.crane.home.MainViewModel
import androidx.compose.samples.crane.home.OnExploreItemClicked
import androidx.compose.samples.crane.ui.BottomSheetShape
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.samples.crane.ui.crane_caption
import androidx.compose.samples.crane.ui.crane_divider_color
import androidx.compose.samples.crane.ui.craneTypography
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest.Builder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This Composable displays a [List] of [ExploreModel] objects in its [ExploreList] Composable. The
 * [ExploreModel] is a data class holding a [City] in its [ExploreModel.city] field which allows one
 * to locate the city using the Google map api, as well as a description in its [String] field
 * [ExploreModel.description] and a URL for an appropriate photo in its [ExploreModel.imageUrl] field.
 * The [List] of [ExploreModel] objects depends on which tab is selected by the `HomeTabBar` that is
 * the `appBar` argument of the [BackdropScaffold] of the `CraneHomeContent` Composable (see the file
 * home/CraneHome.kt). The [CraneScreen.Fly] tab uses [MainViewModel.suggestedDestinations], the
 * [CraneScreen.Sleep] tab uses [MainViewModel.hotels], and the [CraneScreen.Eat] tab uses
 * [MainViewModel.restaurants].
 *
 * The root Composable is a [Surface] which adds a [Modifier.fillMaxSize] to our [modifier] parameter,
 * for its `modifier` argument, uses [Color.White] for its `color` background color argument, and
 * `shape` argument is a [BottomSheetShape] (which is a [RoundedCornerShape] whose `topStart` corner
 * is 20.dp, `topEnd` corner is 20.dp, and whose `bottomStart` and `bottomEnd` corners are both 0.dp
 * and it is defined in the file ui/CraneTheme.kt) The `content` of the [Surface] is a [Column] whose
 * `modifier` argument is a [Modifier.padding] whose `start` padding is 24.dp, whose `top` padding is
 *  20.dp, and whose `end` padding is 24.dp. The `content` of the [Column] is:
 *   - A [Text] whose `text` argument is our [String] parameter [title], and whose `style` argument
 *   is a copy of the `caption` [TextStyle] of [MaterialTheme.typography] whose `color` is modified
 *   to be the [crane_caption] color [Color.DarkGray] (defined in the file ui/CraneTheme.kt). The
 *   `caption` [TextStyle] uses `craneFontFamily` as its `fontFamily`, uses a `fontWeight` of
 *   [FontWeight.W500] and a `fontSize` of 12.sp (`craneFontFamily` uses the [Font] whose resource ID
 *   is [R.font.raleway_medium] for [FontWeight.W500] (file res/font/raleway_medium.ttf).
 *   - A [Spacer] whose `height` is 8.dp
 *   - A [Box] whose `modifier` argument is a `ColumnScope` `Modifier.weight` of 1f which causes it
 *   to take up all space left after its siblings in the [Column] are measured and placed.
 *
 * The inside of the [Box] is where all the interesting stuff takes place. We use the [LazyListState]
 * that [rememberLazyListState] returns to initialize our variable `val listState`, then call the
 * Composable [ExploreList] with its `exploreList` argument our [List] of [ExploreModel] parameter
 * [exploreList], with its `onItemClicked` argument our [onItemClicked] parameter, and its `listState`
 * argument our `listState` [LazyListState] variable. [ExploreList] will then display each of the
 * [ExploreModel]'s in the [List] of [ExploreModel]'s in a [LazyColumn] using an [ExploreItem] to
 * display each individual [ExploreModel]. It will use the [LazyListState] in our variable `listState`
 * as the `state` of the [LazyColumn] which will allow us to use `listState` to observe and control
 * scrolling from the [Box]. Next we initialize our [Boolean] variable `val showButton` by using the
 * [remember] method to remember a derived [State] which returns `true` if the `listState` method
 * [LazyListState.firstVisibleItemIndex] indicates that the index of the first item that is visible
 * is greater than 0. Then if `showButton` is `true` we initialize our [CoroutineScope] variable
 * `val coroutineScope` using the [rememberCoroutineScope] method, and call the [FloatingActionButton]
 * Composable using the `primary` [Color] of [MaterialTheme.colors] (`crane_purple_800` aka a [Color]
 * of 0xFF5D1049) in our [CraneTheme] custom [MaterialTheme]) as its `backgroundColor` argument, for
 * its `modifier` we use a`BoxScope` `Modifier.align` of [Alignment.BottomEnd], to which we add a
 * [Modifier.navigationBarsPadding] to add padding to accommodate the navigation bars insets, along
 * with an additional [Modifier.padding] of 8.dp to the bottom of the [FloatingActionButton]. The
 * `onClick` argument is a lambda which launches a coroutine using the `coroutineScope` [CoroutineScope]
 * which calls the [LazyListState.scrollToItem] method of `listState` to scroll to item 0 when the
 * [FloatingActionButton] is clicked. The `content` of the [FloatingActionButton] is a [Text] that
 * displays the [String] "Up!".
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance or behavior.
 * Our callers do not pass one so the empty, default, or starter [Modifier] that contains no elements
 * is used instead.
 * @param title the title of our contents, the [CraneScreen.Fly] tab uses "Explore Flights by Destination",
 * the [CraneScreen.Sleep] tab uses "Explore Properties by Destination" and the [CraneScreen.Eat] tab
 * uses "Explore Restaurants by Destination". This is displayed in a [Text] at the top of the [Column]
 * contents of our root [Surface] Composable.
 * @param exploreList the [List] of [ExploreModel] objects that our [ExploreList] Composable will display
 * in its [LazyColumn].
 * @param onItemClicked an [OnExploreItemClicked] lambda that we pass to [ExploreList] in its `onItemClicked`
 * argument. [ExploreList] passes this on to each [ExploreItem] in its [LazyColumn] which uses it as
 * the argument of the [Modifier.clickable] it adds to the [Row] in which it renders the [ExploreModel]
 * it is responsible for. All three of the tabs ([CraneScreen.Fly], [CraneScreen.Sleep], and
 * [CraneScreen.Eat]) use the `onExploreItemClicked` that their `CraneHomeContent` parent was called
 * with (file home/CraneHome.kt) which is the `onExploreItemClicked` that its `CraneHome` parent was
 * called with, which is the `onExploreItemClicked` that its parent `MainScreen` was called with (file
 * home/MainActivity.kt), which is a lambda that the `onCreate` override calls `MainScreen` with when
 * its has the `setContent` method compose `MainScreen` into the activity. That lambda calls the
 * [launchDetailsActivity] method to have it launch the [DetailsActivity] to display the [ExploreModel]
 * that was clicked.
 */
@Composable
fun ExploreSection(
    modifier: Modifier = Modifier,
    title: String,
    exploreList: List<ExploreModel>,
    onItemClicked: OnExploreItemClicked
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color.White, shape = BottomSheetShape) {
        Column(modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
            Spacer(Modifier.height(8.dp))
            // TODO Codelab: derivedStateOf step DONE
            // TODO: Show "Scroll to top" button when the first item of the list is not visible DONE
            Box(modifier = Modifier.weight(1f)) {
                val listState: LazyListState = rememberLazyListState()
                ExploreList(
                    exploreList = exploreList,
                    onItemClicked = onItemClicked,
                    listState = listState
                )

                // Show the button if the first visible item is past
                // the first item. We use a remembered derived state to
                // minimize unnecessary compositions
                val showButton: Boolean by remember {
                    derivedStateOf {
                        listState.firstVisibleItemIndex > 0
                    }
                }
                if (showButton) {
                    val coroutineScope: CoroutineScope = rememberCoroutineScope()
                    FloatingActionButton(
                        backgroundColor = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .navigationBarsPadding()
                            .padding(bottom = 8.dp),
                        onClick = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        }
                    ) {
                        Text("Up!")
                    }
                }
            }
        }
    }
}

/**
 * This Composable uses a [LazyColumn] to display [ExploreItem] Composables which render the info in
 * each of the [ExploreModel] objects in our [List] of [ExploreModel] parameter [exploreList]. Our
 * root Composable is our [LazyColumn], whose `modifier` argument is our [Modifier] parameter [modifier]
 * and whose `state` argument is our [LazyListState] parameter [listState]. The `content` of the
 * [LazyColumn] is an [items] whose `items` argument is our [exploreList] parameter. It loops through
 * all of the [ExploreModel] objects in that [List] feeding them as `exploreItem` to its `itemContent`
 * which consists of a [Column] whose `modifier` argument is a `LazyItemScope` `Modifier.fillParentMaxWidth`
 * which has the [Column] fill the maximum width of its parent's measurement constraints. The [Column]
 * holds an [ExploreItem] whose `modifier` argument is a `LazyItemScope` `Modifier.fillParentMaxWidth`,
 * whose `item` argument is the `exploreItem` [ExploreModel] it is to render, and whose `onItemClicked`
 * is our [OnExploreItemClicked] parameter [onItemClicked]. Below the [ExploreItem] in the [Column]
 * is a [Divider] whose `color` is [crane_divider_color] (which is defined to be [Color.LightGray] in
 * the file ui/CraneTheme.kt). Below all of the [items] is a `LazyListScope` `item` holding a [Spacer]
 * whose `modifier` is a [Modifier.windowInsetsBottomHeight] which sets the height to that of the
 * `statusBars` [WindowInsets] at the bottom of the screen.
 *
 * @param exploreList the [List] of [ExploreModel] objects we are to display in our [LazyColumn].
 * @param onItemClicked an [OnExploreItemClicked] lambda that the [ExploreItem]'s in our [LazyColumn]
 * should call with the [ExploreModel] they hold when it is clicked. It is passed down a series of
 * Composable parents from the `onCreate` override of [MainActivity] and is a lambda that calls the
 * [launchDetailsActivity] method to have it launch the [DetailsActivity] to display the [ExploreModel]
 * that was clicked.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or behavior.
 * Our caller does not pass us one so the empty, default, or starter [Modifier] that contains no elements
 * is used instead.
 * @param listState a [LazyListState] that our caller can use to observe and control scrolling of our
 * [LazyColumn]. Our caller [ExploreSection] uses it to observe when the first visible item is past
 * the first item of the [List], and to scroll to item 0 when its [FloatingActionButton] is clicked.
 */
@Composable
private fun ExploreList(
    exploreList: List<ExploreModel>,
    onItemClicked: OnExploreItemClicked,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        state = listState
    ) {
        items(items = exploreList) { exploreItem: ExploreModel ->
            Column(modifier = Modifier.fillParentMaxWidth()) {
                ExploreItem(
                    modifier = Modifier.fillParentMaxWidth(),
                    item = exploreItem,
                    onItemClicked = onItemClicked
                )
                Divider(color = crane_divider_color)
            }
        }
        item {
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.statusBars))
        }
    }
}

/**
 * Displays the information contained in its [ExploreModel] parameter [item], and calls its parameter
 * [onItemClicked] with that [item] if it is clicked. Our root Composable is a [Row] whose `modifier`
 * argument adds to our [Modifier] parameter [modifier] a [Modifier.clickable] that calls our
 * [OnExploreItemClicked] parameter [onItemClicked] with our [ExploreModel] parameter [item] and adds
 * a [Modifier.padding] whose `top` padding is 12.dp, and whose `bottom` padding is 12.sp on to that
 * as well. The `content` of the [Row] is:
 *  - An [ExploreImageContainer] (which is just a wrapper around a [Surface] whose size is 60.dp by
 *  60.dp, and whose shape is a [RoundedCornerShape] of 4.dp) whose `content` is a [Box] which holds
 *  an [Image] downloaded from the URL in the [ExploreModel.imageUrl] field of [item] or an [Image]
 *  of the drawable resource ID [R.drawable.ic_crane_logo] while the photo is being loaded (ie. when
 *  the [AsyncImagePainter.state] of the [AsyncImagePainter] variable `val painter` is
 *  [AsyncImagePainter.State.Loading])
 *  - A [Spacer] whose width is 24.dp
 *  - A [Column] whose top entry is a [Text] displaying the [City.nameToDisplay] field of the
 *  [ExploreModel.city] field of [item] using the [TextStyle] `h6` of [MaterialTheme.typography] (which
 *  is the [FontFamily] `craneFontFamily` for [FontWeight.W400] (the [Font] with resource ID
 *  [R.font.raleway_regular] and `fontSize` 20.sp). Below this is a [Spacer] that is 8.dp hight, followed
 *  by another [Text] displaying the [ExploreModel.description] field of [item] using a copy of the
 *  [TextStyle] `caption` of [MaterialTheme.typography] whose `color` is modified to be [crane_caption]
 *  ([Color.DarkGray]). The base [TextStyle] of `caption` defined by [craneTypography] is the [FontFamily]
 *  `craneFontFamily` for [FontWeight.W500] (the [Font] with resource ID [R.font.raleway_medium] and
 *  `fontSize` 12.sp).
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance or behavior.
 * Our caller calls us with a `LazyItemScope` `Modifier.fillParentMaxWidth` to have us fill the maximum
 * width of our parent's measurement constraints.
 * @param item the [ExploreModel] whose information we should display.
 * @param onItemClicked an [OnExploreItemClicked] we should call with our [ExploreModel] parameter
 * [item] as its argument. If you trace back up the Composable tree you will find that it is a lambda
 * created in the `onCreate` override of [MainActivity] that calls the [launchDetailsActivity] method
 * to have it launch the [DetailsActivity] to display the [ExploreModel] that was clicked.
 */
@Composable
private fun ExploreItem(
    modifier: Modifier = Modifier,
    item: ExploreModel,
    onItemClicked: OnExploreItemClicked
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        ExploreImageContainer {
            Box {
                val painter: AsyncImagePainter = rememberAsyncImagePainter(
                    model = Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )

                if (painter.state is AsyncImagePainter.State.Loading) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_crane_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(24.dp))
        Column {
            Text(
                text = item.city.nameToDisplay,
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
        }
    }
}

/**
 * This Composable is a wrapper around a [Surface] whose size is 60.dp by 60.dp, and whose shape is
 * a [RoundedCornerShape] of 4.dp
 *
 * @param content the Composable `content` for our [Surface],
 */
@Composable
private fun ExploreImageContainer(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.size(width = 60.dp, height = 60.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        content()
    }
}
