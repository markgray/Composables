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
 * This Composable is used for each of the 100 `items` in the [LazyColumn] of [ImageList].
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
        Spacer(Modifier.width(10.dp))
        Text(text = "Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

@Preview
@Composable
fun ImageListItemPreview() {
    ImageListItem(1)
}

@Composable
fun ImageList() {
    // We save the scrolling position with this state
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        items(100) {
            ImageListItem(it)
        }
    }
}

@Preview
@Composable
fun ImageListPreview() {
    ImageList()
}

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
                    scrollState.animateScrollToItem(0)
                }
            }) {
                Text("Scroll to the top")
            }

            Button(onClick = {
                coroutineScope.launch {
                    // listSize - 1 is the last index of the list
                    scrollState.animateScrollToItem(listSize - 1)
                }
            }) {
                Text("Scroll to the end")
            }
        }

        LazyColumn(state = scrollState) {
            items(listSize) {
                ImageListItem(it)
            }
        }
    }
}

@Preview
@Composable
fun ScrollingListPreview() {
    ScrollingList()
}
