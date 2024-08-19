/*
 * Copyright 2024 The Android Open Source Project
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
package com.compose.performance.accelerate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tracing.trace
import coil.compose.AsyncImage
import com.compose.performance.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

/**
 * This Composable is the first task in the codelab.
 */
@Composable
fun AccelerateHeavyScreen(
    modifier: Modifier = Modifier,
    viewModel: HeavyScreenViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    AccelerateHeavyScreen(items = items, modifier = modifier)
}

@Composable
fun AccelerateHeavyScreen(items: List<HeavyItem>, modifier: Modifier = Modifier) {
    // TODO: Codelab task: Wrap this with timezone provider DONE
    ProvideCurrentTimeZone {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            ScreenContent(items = items)

            if (items.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ScreenContent(items: List<HeavyItem>) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .testTag("list_of_items"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Fixed(2)
    ) {
        items(items) { item -> HeavyItem(item) }
    }
}

@Composable
fun HeavyItem(item: HeavyItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = item.description, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        PublishedText(item.published)
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            AsyncImage(
                model = item.url,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                contentDescription = stringResource(R.string.performance_dashboard),
                placeholder = imagePlaceholder(),
                contentScale = ContentScale.Crop
            )

            ItemTags(item.tags, Modifier.align(Alignment.BottomCenter))
        }
    }
}

/**
 * TODO Codelab task: Improve this placeholder_vector.xml loading DONE
 */
@Composable
fun imagePlaceholder() = trace("ImagePlaceholder") {
    painterResource(R.drawable.placeholder_vector)
}

/**
 * TODO Codelab task: Remove the side effect from every item and hoist it to the parent composable DONE
 */
@Composable
fun PublishedText(published: Instant, modifier: Modifier = Modifier) {
    Text(
        text = published.format(LocalTimeZone.current),
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
    )
}

val LocalTimeZone = compositionLocalOf { TimeZone.currentSystemDefault() }

/**
 * TODO Codelab task: Write a composition local provider that will always provide current TimeZone DONE
 */
@Composable
fun ProvideCurrentTimeZone(content: @Composable () -> Unit) {
    // TODO Codelab task: move the side effect for TimeZone changes DONE
    // TODO Codelab task: create a composition local for current TimeZone DONE
    val context = LocalContext.current

    var currentTimeZone: TimeZone by remember { mutableStateOf(TimeZone.currentSystemDefault()) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentTimeZone = TimeZone.currentSystemDefault()
            }
        }

        scope.launch(Dispatchers.IO) {
            trace("PublishDate.registerReceiver") {
                context.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))
            }
        }

        onDispose { context.unregisterReceiver(receiver) }
    }

    CompositionLocalProvider(
        value = LocalTimeZone provides currentTimeZone,
        content = content
    )
}

/**
 * TODO Codelab task: remove unnecessary lazy layout DONE
 */
@Composable
fun ItemTags(tags: List<String>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        tags.forEach { ItemTag(it) }
    }
}

@Composable
fun ItemTag(tag: String) = trace("ItemTag") {
    Text(
        text = tag,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 10.sp,
        maxLines = 1,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
            .padding(2.dp)
    )
}
