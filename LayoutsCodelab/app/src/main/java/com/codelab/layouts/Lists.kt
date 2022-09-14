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

package com.codelab.layouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

/**
 * An example [Column] whose children consist of 100 [Text] Composables displaying the text "Item #n"
 * where `n` is the index of the item in the [Column]. The `style` configuration for the text is the
 * `subtitle1` [TextStyle] from [MaterialTheme.typography] (`subtitle1` is the largest subtitle, and
 * is typically reserved for medium-emphasis text that is shorter in length). The default value from
 * [MaterialTheme] is: Font: Roboto, Weight: Normal, Size: 16px, and Letter spacing: 0.15px. Note:
 * this [Column] is not scroll-able.
 */
@Composable
fun SimpleColumn() {
    Column {
        repeat(100) {
            Text(text = "Item #$it", style = MaterialTheme.typography.subtitle1)
        }
    }
}

/**
 * This is a Preview of the [SimpleColumn] Composable.
 */
@Preview
@Composable
fun SimpleColumnPreview() {
    SimpleColumn()
}

/**
 * An example of a scroll-able [Column] whose children consist of 100 [Text] Composables displaying
 * the text "Item #n" where `n` is the index of the item in the [Column]. The `style` configuration
 * for the text is the `subtitle1` [TextStyle] from [MaterialTheme.typography] (`subtitle1` is the
 * largest subtitle, and is typically reserved for medium-emphasis text that is shorter in length).
 * The default value from [MaterialTheme] is: Font: Roboto, Weight: Normal, Size: 16px, and Letter
 * spacing: 0.15px. We initialize our [ScrollState] variable `val scrollState` using the method
 * [rememberScrollState] (we will save the scrolling position of our [Column] in this state). Then
 * we pass a [Modifier.verticalScroll] that uses `scrollState` as its `state` argument when we
 * invoke our [Column] Composable.
 */
@Composable
fun SimpleList() {
    // We save the scrolling position with this state
    val scrollState: ScrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(state = scrollState)) {
        repeat(100) {
            Text(text = "Item #$it", style = MaterialTheme.typography.subtitle1)
        }
    }
}

/**
 * The Preview of our [SimpleList] Composable
 */
@Preview
@Composable
fun SimpleListPreview() {
    SimpleList()
}

/**
 * An example of a scroll-able [LazyColumn] whose children consist of 100 [Text] Composables displaying
 * the text "Item #n" where `n` is the index of the item in the [LazyColumn]. The `style` configuration
 * for the text is the `subtitle1` [TextStyle] from [MaterialTheme.typography] (`subtitle1` is the
 * largest subtitle, and is typically reserved for medium-emphasis text that is shorter in length).
 * The default value from [MaterialTheme] is: Font: Roboto, Weight: Normal, Size: 16px, and Letter
 * spacing: 0.15px. We initialize our [ScrollState] variable `val scrollState` using the method
 * [rememberScrollState] (we will save the scrolling position of our [Column] in this state). Then
 * we use `scrollState` as the `state` argument of [LazyColumn], and use 100 as the `count` argument
 * to `items` to have it create the 100 [Text] Composables that are to be the children of the
 * [LazyColumn].
 */
@Composable
fun LazyList() {
    // We save the scrolling position with this state
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        items(count = 100) {
            Text(text = "Item #$it", style = MaterialTheme.typography.subtitle1)
        }
    }
}

/**
 * The Preview of the [LazyList] Composable.
 */
@Preview
@Composable
fun LazyListPreview() {
    LazyList()
}

/**
 * This Composable consists of a [Row] that holds a 50.dp by 50.dp [Image], a 10.dp [Spacer] and a
 * [Text] displaying the `text` "Item #$index" where "$index" refers to our [Int] parameter [index].
 * This Composable is used for each of the 100 `items` in the [LazyColumn] of [ImageList]. The root
 * Composable is a [Row] whose `verticalAlignment` argument is [Alignment.CenterVertically] (which
 * centers its children vertically in the space allotted to the [Row]). The children of the [Row]
 * consist of an [Image] which uses for its `painter` argument an [AsyncImagePainter] created using
 * the [rememberAsyncImagePainter] method for the `model` URL:
 * "https://developer.android.com/images/brand/Android_Robot.png" (this `painter` will execute an
 * `ImageRequest` asynchronously to download the URL and render the result). The `contentDescription`
 * argument of the [Image] is "Android Logo", and its `modifier` argument is a [Modifier.size] of
 * 50.dp (which sizes the [Image] to be 50.dp by 50.dp). The [Image] is followed by a [Spacer] that
 * uses a [Modifier.width] of 10.dp as its `modifier` argument to size it to be 10.dp wide. At the
 * end of the [Row] is a [Text] displaying the `text` "Item #$index" (where $index refers to our
 * [Int] parameter [index]) using the `subtitle1` [TextStyle] of [MaterialTheme.typography] (the
 * default value from [MaterialTheme] which is: Font: Roboto, Weight: Normal, Size: 16px, and Letter
 * spacing: 0.15px).
 *
 * @param index the number we should display in our [Text], in our case it will be the index of the
 * [ImageListItem] in the [LazyColumn] of [ImageList].
 */
@Composable
fun ImageListItem(index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberAsyncImagePainter(
                model = "https://developer.android.com/images/brand/Android_Robot.png"
            ),
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(width = 10.dp))
        Text(text = "Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

/**
 * This is the Preview of our [ImageListItem] Composable, created using an `index` argument of 1.
 */
@Preview
@Composable
fun ImageListItemPreview() {
    ImageListItem(index = 1)
}

/**
 * This Composable displays 100 [ImageListItem] Composables in a [LazyColumn], with each [ImageListItem]
 * displaying the item's index in the [LazyColumn] in its [Text] Composable. We initialize our
 * [LazyListState] variable `val scrollState` using the instance returned by [rememberLazyListState]
 * and pass `scrollState` as the `state` argument of a [LazyColumn] (state object to be used to
 * control or observe the list's state) then use an `items` call with its `count` argument set to
 * 100 to create 100 [ImageListItem] Composables whose `index` argument is the position of the item
 * in the [LazyColumn].
 */
@Composable
fun ImageList() {
    // We save the scrolling position with this state
    val scrollState: LazyListState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        items(count = 100) {
            ImageListItem(index = it)
        }
    }
}

/**
 * This is the Preview of our [ImageList] Composable.
 */
@Preview
@Composable
fun ImageListPreview() {
    ImageList()
}

/**
 * This Composable consists of a [Column] holding a [Row] with two [Button]'s labeled "Scroll to the
 * top" and "Scroll to the end". Below that [Row] in the [Column] is a [LazyColumn] which displays
 * 100 [ImageListItem] Composables.
 */
@Composable
fun ScrollingList() {
    val listSize = 100
    // We save the scrolling position with this state
    val scrollState = rememberLazyListState()
    // We save the coroutine scope where our animated scroll will be executed
    val coroutineScope = rememberCoroutineScope()

    Column {
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    // 0 is the first item index
                    scrollState.animateScrollToItem(index = 0)
                }
            }) {
                Text(text = "Scroll to the top")
            }

            Button(onClick = {
                coroutineScope.launch {
                    // listSize - 1 is the last index of the list
                    scrollState.animateScrollToItem(index = listSize - 1)
                }
            }) {
                Text(text = "Scroll to the end")
            }
        }

        LazyColumn(state = scrollState) {
            items(count = listSize) {
                ImageListItem(index = it)
            }
        }
    }
}

@Preview
@Composable
fun ScrollingListPreview() {
    ScrollingList()
}
