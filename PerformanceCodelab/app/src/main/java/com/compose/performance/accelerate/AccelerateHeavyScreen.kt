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
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tracing.trace
import coil.compose.AsyncImage
import com.compose.performance.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

/**
 * A composable function that renders a screen designed to showcase performance
 * optimization techniques for heavy UI elements. This composable utilizes a [ViewModel]
 * to manage and provide the data displayed on the screen.
 *
 * This function is a higher-level stateful composable that delegates to the stateless
 * lower-level `AccelerateHeavyScreen(items, modifier)` overload.
 *
 * It handles the state management by collecting data from the [StateFlow] of [List] of
 * [HeavyItem] property [HeavyScreenViewModel.items] of our [HeavyScreenViewModel] parameter
 * [viewModel] using [StateFlow.collectAsState].
 *
 * @param modifier The [Modifier] to apply to the screen's layout.
 * @param viewModel The [HeavyScreenViewModel] instance used to provide data for
 * the screen. Defaults to a new instance obtained via [viewModel].
 */
@Composable
fun AccelerateHeavyScreen(
    modifier: Modifier = Modifier,
    viewModel: HeavyScreenViewModel = viewModel()
) {
    /**
     * The [State] wrapped [List] of [HeavyItem] that will be displayed on the screen.
     */
    val items: List<HeavyItem> by viewModel.items.collectAsState()
    AccelerateHeavyScreen(items = items, modifier = modifier)
}

/*
 * Stateless overload of stateful [AccelerateHeavyScreen]
 */

/**
 * Stateless overload of stateful [AccelerateHeavyScreen].
 * Composes a screen that displays a list of [HeavyItem] objects.
 *
 * This composable wraps the main screen content ([ScreenContent]) within a [ProvideCurrentTimeZone]
 * to manage timezone context. It also displays a [CircularProgressIndicator] while the list of items
 * is empty, indicating that data is still loading.
 *
 * @param items The list of [HeavyItem] objects to be displayed on the screen. If the list is empty,
 * a [CircularProgressIndicator] will be shown.
 * @param modifier [Modifier] to be applied to the root container of the screen.
 *
 * @see ProvideCurrentTimeZone
 * @see ScreenContent
 * @see CircularProgressIndicator
 */
@Composable
fun AccelerateHeavyScreen(items: List<HeavyItem>, modifier: Modifier = Modifier) {
    // TASK: Codelab task: Wrap this with timezone provider DONE
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


/**
 * Displays a grid of [HeavyItem]s in a vertically scrolling list.
 *
 * This composable function renders a grid layout using [LazyVerticalGrid] to efficiently
 * display a potentially large list of [HeavyItem]'s. It uses a fixed grid with two columns
 * and provides spacing between the items both vertically and horizontally.
 *
 * @param items A list of [HeavyItem] objects to be displayed in the grid. Each item will be
 * rendered using the [HeavyItem] composable.
 *
 * @see LazyVerticalGrid
 * @see GridCells
 * @see HeavyItem
 * @see Modifier.testTag
 * @see Modifier.fillMaxSize
 */
@Composable
fun ScreenContent(items: List<HeavyItem>) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .testTag(tag = "list_of_items"),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        columns = GridCells.Fixed(count = 2)
    ) {
        items(items = items) { item: HeavyItem -> HeavyItem(item = item) }
    }
}

/**
 * Displays a single [HeavyItem].
 *
 * This composable function renders a single [HeavyItem] with its description, published date,
 * its image, and its tags. It uses a [Column] layout to arrange the elements vertically.
 *
 * @param item The [HeavyItem] object to be displayed.
 * @param modifier [Modifier] to be applied to the root container of the item.
 */
@Composable
fun HeavyItem(item: HeavyItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = item.description, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(height = 8.dp))
        PublishedText(published = item.published)
        Spacer(modifier = Modifier.height(height = 8.dp))

        Box {
            AsyncImage(
                model = item.url,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 1f)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(size = 12.dp)),
                contentDescription = stringResource(id = R.string.performance_dashboard),
                placeholder = imagePlaceholder(),
                contentScale = ContentScale.Crop
            )

            ItemTags(
                tags = item.tags,
                modifier = Modifier.align(alignment = Alignment.BottomCenter)
            )
        }
    }
}

/**
 * TASK Codelab task: Improve this placeholder_vector.xml loading DONE
 * Returns a [Painter] that represents a placeholder image.
 *
 * This function is a composable that provides a vector-based placeholder image.
 * It's designed to be used in situations where an image is loading or unavailable.
 * The placeholder image is sourced from the application's resources, specifically
 * from `R.drawable.placeholder_vector`.
 *
 * This function also uses [trace] to provide performance tracing information. The "ImagePlaceholder"
 * tag will be used to identify the performance information related to drawing this placeholder.
 *
 *  @return A [Painter] object representing the placeholder image.
 */
@Composable
fun imagePlaceholder(): Painter = trace("ImagePlaceholder") {
    painterResource(id = R.drawable.placeholder_vector)
}

/**
 * TASK Codelab task: Remove the side effect from every item and hoist it to the parent composable DONE
 * Displays the formatted text representation of a publication instant.
 *
 * This composable formats a given [Instant] object into a human-readable string
 * based on the user's current local time zone and displays it as a [Text] composable.
 * It uses [Typography.labelMedium] from our custom [MaterialTheme.typography] as its
 * [TextStyle] for styling the text.
 *
 * @param published The [Instant] representing the publication time.
 * @param modifier Modifier to apply to the underlying [Text] composable.
 */
@Composable
fun PublishedText(published: Instant, modifier: Modifier = Modifier) {
    Text(
        text = published.format(timeZone = LocalTimeZone.current),
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
    )
}


/**
 * [ProvidableCompositionLocal] that provides the system's local time zone.
 *
 * This composition local holds the [TimeZone] that represents the user's current
 * system default time zone. It's useful for components that need to display or
 * manipulate date and time information in the user's local context.
 *
 * The initial value is obtained from [TimeZone.currentSystemDefault].
 *
 * Note: Changes to the system's default time zone after the composition local is
 * first accessed will not be reflected in the value provided by [LocalTimeZone].
 * If the system time zone changes during the lifecycle of the app and you need to
 * update your Composables to reflect this, you will need to re-read LocalTimeZone
 * value to recompose with the new value.
 */
val LocalTimeZone: ProvidableCompositionLocal<TimeZone> =
    compositionLocalOf { TimeZone.currentSystemDefault() }

/**
 * TASK Codelab task: Write a composition local provider that will always provide current TimeZone DONE
 * Provides the current system time zone to its composable children.
 *
 * This composable function sets up a listener for system time zone changes and updates
 * the [LocalTimeZone] composition local accordingly. It ensures that composables
 * within its scope can access the most up-to-date time zone information.
 *
 * It uses the following mechanisms:
 *   - `BroadcastReceiver`: To listen for the `Intent.ACTION_TIMEZONE_CHANGED` broadcast.
 *   - `DisposableEffect`: To register and unregister the receiver, ensuring proper cleanup.
 *   - `CoroutineScope`: To perform the receiver registration on an IO dispatcher.
 *   - `CompositionLocalProvider`: To make the current time zone available to child composables
 *     through the `LocalTimeZone` composition local.
 *   - `remember`: to save and manage the current time zone state.
 *   - `mutableStateOf`: to track changes to the time zone.
 *
 * @param content The composable content that will have access to the provided time zone.
 */
@Composable
fun ProvideCurrentTimeZone(content: @Composable () -> Unit) {
    // TASK Codelab task: move the side effect for TimeZone changes DONE
    // TASK Codelab task: create a composition local for current TimeZone DONE
    /**
     * The [Context] of the current composition.
     */
    val context: Context = LocalContext.current

    /**
     * The current system time zone.
     */
    var currentTimeZone: TimeZone by remember { mutableStateOf(TimeZone.currentSystemDefault()) }

    /**
     * A [CoroutineScope] that is bound to the IO dispatcher.
     */
    val scope: CoroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentTimeZone = TimeZone.currentSystemDefault()
            }
        }

        scope.launch(context = Dispatchers.IO) {
            trace(label = "PublishDate.registerReceiver") {
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
 * TASK Codelab task: remove unnecessary lazy layout DONE
 * Displays a horizontal row of item tags.
 *
 * This composable function takes a list of strings representing tags and displays them
 * in a horizontal row. The row is scrollable horizontally if the tags exceed the available width.
 * Each tag is displayed using the [ItemTag] composable function.
 *
 * @param tags The list of tags to display. Each tag will be rendered as a separate item.
 * @param modifier The modifier to apply to the row. This allows customization of the row's
 * layout, such as padding, size, and background. Defaults to an empty modifier.
 */
@Composable
fun ItemTags(tags: List<String>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(all = 4.dp)
            .fillMaxWidth()
            .horizontalScroll(state = rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(space = 2.dp)
    ) {
        tags.forEach { tagValue: String -> ItemTag(tag = tagValue) }
    }
}

/**
 * Displays a tag as a small, rounded chip-like element.
 *
 * This composable renders a given [tag] string within a visually distinct
 * container. It's designed for displaying concise, descriptive labels
 * or categories associated with an item.
 *
 * The tag is rendered with a primary background color and on-primary text color, using the
 * [Typography.labelSmall] text style from our custom [MaterialTheme.typography]. It includes
 * padding and rounded corners for visual appeal.
 *
 * @param tag The text content of the tag to be displayed.
 */
@Composable
fun ItemTag(tag: String): Unit = trace(label = "ItemTag") {
    Text(
        text = tag,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 10.sp,
        maxLines = 1,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(all = 2.dp)
    )
}
