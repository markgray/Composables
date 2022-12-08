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

package com.example.android.codelab.animation.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.codelab.animation.R
import com.example.android.codelab.animation.ui.Amber600
import com.example.android.codelab.animation.ui.AnimationCodelabTheme
import com.example.android.codelab.animation.ui.Green300
import com.example.android.codelab.animation.ui.Green800
import com.example.android.codelab.animation.ui.Purple100
import com.example.android.codelab.animation.ui.Purple700
import com.example.android.codelab.animation.ui.Teal200
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Enum used to identify the two [HomeTab] Composables used in the [HomeTabBar] of our app. Since we
 * are only interested in the animations that occur when one or the other [HomeTab] is clicked, there
 * is no need to pretend the names of the members of the enum have any meaning.
 */
private enum class TabPage {
    Home, Work
}

/**
 * Shows the entire screen. It contains one animation, the "to do 1: Animate this color change" which
 * animates the `backgroundColor` argument of the [Scaffold] root Composable of [Home] as well as the
 * [HomeTabBar] that is used as the `topBar` of the [Scaffold] depending on the current tab selected
 * in the [HomeTabBar]: [Purple100] for [TabPage.Home] and [Green300] for any other [TabPage]. It uses
 * the method [animateColorAsState] to do this. Since it returns a [State] of [Color] any Composable
 * that has it as an argument will recompose every step of the animation between the colors.
 *
 * We start by using the [stringArrayResource] method to initialize our [Array] of [String] variable
 * `val allTasks` with the `string-array` with resource ID [R.array.tasks], and using the [toList]
 * extension function on the [Array] of [String] returned by the [stringArrayResource] method for the
 * `string-array` with resource ID [R.array.topics] to initialize our [List] of [String] variable
 * `val allTopics`. We initialize and remember our [TabPage] variable `var tabPage` as a [MutableState]
 * with the initial value of [TabPage.Home], initialize and remember our [Boolean] variable
 * `var weatherLoading` as a [MutableState] with the initial value of `false`, initialize and
 * remember our [SnapshotStateList] of [String] variable `val tasks` using the [mutableStateListOf]
 * method with the initial value of our [Array] of [String] variable `allTasks`, initialize and
 * remember our [String] variable `var expandedTopic` as a  [MutableState] with the initial value of
 * `null`, and initialize and remember our [Boolean] variable `var editMessageShown` as a [MutableState]
 * with the initial value of `false`.
 *
 * We then proceed to define two suspend methods for later use:
 *  - `loadWeather` which sets `weatherLoading` to `true` if it is `false` then simulates loading
 *  weather data by delaying for 3 seconds before setting `weatherLoading` back to `false`. An `if`
 *  statement in an item of the [LazyColumn] will cause a [LoadingRow] to be displayed while
 *  `weatherLoading` is `true`, and display a [WeatherRow] while `weatherLoading` is `false`.
 *  `loadWeather` is called by a [LaunchedEffect] during the initial composition and by the
 *  "Refresh" [IconButton] of the [WeatherRow] Composable.
 *  - `showEditMessage` which sets `editMessageShown` to `true` if it is `false` then delays for 3
 *  seconds before setting `weatherLoading` back to `false`. An [EditMessage] at the bottom of the
 *  `content` of the [Scaffold] of [Home] uses `editMessageShown` as the `visible` argument of
 *  [AnimatedVisibility] to animate the visibility of its `content` which is a [Text] displaying the
 *  `text` "Edit feature is not supported". `showEditMessage` is called by the `onClick` lambda of
 *  the [HomeFloatingActionButton].
 *
 * Next in the [Home] Composable is a [LaunchedEffect] with a key of [Unit] (keys are now required,
 * but any constant including [Unit] causes the [LaunchedEffect] to only execute once) which calls
 * `loadWeather` at the initial composition.
 *
 * We next initialize and remember our [LazyListState] variable `val lazyListState` using the
 * [rememberLazyListState] method (it is used as the [LazyListState] argument `state` of the
 * [LazyColumn] in the `content` of the [Scaffold] of [Home] and the [LazyListState.isScrollingUp]
 * extension property is used as the `extended` argument of the [HomeFloatingActionButton]
 * `floatingActionButton` of the [Scaffold] with an [AnimatedVisibility] using its value as its
 * `visible` argument to animate the visibility of an "Extended" [Text] of an [FloatingActionButton]
 * that displays the `text` "EDIT" (methinks that [isScrollingUp] returns `hasNotScrolledUpYet` since
 * the [Text] is only visible when the [LazyColumn] is at its top but I won't quibble with the name).
 *
 * The "to do 1: Animate this color change" step in the codelab is next, and all that is required is
 * to in initialize our [Color] variable `val backgroundColor` by an [animateColorAsState] whose
 * `targetValue` argument is an `if` expression which returns [Purple100] if `tabPage` (the currently
 * selected tab) is equal to [TabPage.Home] or else [Green300] if it is not equal. `backgroundColor`
 * is used as the `backgroundColor` argument of the [HomeTabBar] Composable that is the `topBar`
 * of the [Scaffold] as well as the `backgroundColor` argument of the [Scaffold] itself, and the
 * [animateColorAsState] will cause them to recompose as it smoothly animates between the colors when
 * the value of `tabPage` changes.
 *
 * The last step before calling the [Scaffold] root Composable is to initialize and remember our
 * [CoroutineScope] variable `val coroutineScope` to a a [CoroutineScope] bound to this point in the
 * composition using the [rememberCoroutineScope] method. We will use thie coroutine scope for event
 * handlers calling suspend functions. It is used in the `onRefresh` lambda argument of [WeatherRow]
 * to launch the `loadWeather` suspend function and in the `onClick` lambda argument of our
 * [HomeFloatingActionButton] to launch the `showEditMessage` suspend function.
 *
 * Now for the [Scaffold]. Its `topBar` argument (top app bar of the screen) is a [HomeTabBar] Composable
 * whose `backgroundColor` is the [animateColorAsState] wrapped [Color] variable `backgroundColor` we
 * discussed above, its `tabPage` argument is our `tabPage` variable and its `onTabSelected` argument
 * is a lambda that sets `tabPage` to the new [TabPage] that has been selected. The `backgroundColor`
 * argument of the [Scaffold] is also the [animateColorAsState] wrapped [Color] variable `backgroundColor`.
 * The `floatingActionButton` argument of the [Scaffold] is a [HomeFloatingActionButton] whose `extended`
 * argument is the [Boolean] value returned by the [LazyListState.isScrollingUp] extension method (which
 * returns `true` only when the [LazyColumn] `content` of the [Scaffold] is at its top, and the `onClick`
 * argument is a lambda which uses our [CoroutineScope] variable `coroutineScope` to launch a coroutine
 * which calls `showEditMessage` to have it set `editMessageShown` to `true` then delay for 3 seconds
 * and set it back to `false` (setting it to `true` causes the [EditMessage] Composable below the
 * [LazyColumn] in the `content` of the [Scaffold] to animate the visibility of a [Text] displaying
 * the message "Edit feature is not supported" to visible and setting it back to `false` causes the
 * reverse animation to invisible to occur.
 *
 * The `content` of the [Scaffold] holds a [LazyColumn] and the [EditMessage] Composable just mentioned.
 * The `contentPadding` argument of the [LazyColumn] (padding around the whole content) is a
 * [PaddingValues] that sets the `horizontal` padding to 16.dp, and the `vertical` padding to. 32.dp,
 * its `state` argument (state object to be used to control or observe the list's state) is our
 * [LazyListState] variable `lazyListState`, and its `modifier` argument is a [Modifier.padding] whose
 * `paddingValues` variable is the [PaddingValues] instance `padding` that the [Scaffold] passes as
 * an argument to its `content` lambda.
 *
 * The `content` of the [LazyColumn] is divided into three sections: "Weather", "Topics", and "Tasks".
 * The "Weather" section has four items in it:
 *  - an `item` displaying a [Header] Composable displaying the `title` "Weather"
 *  - an `item` holding a [Spacer] whose height is 16.dp
 *  - an `item` holding a [Surface] whose `modifier` argument is a [Modifier.fillMaxWidth] and whose
 *  `elevation` is 2.dp which has an if statement which displays a [LoadingRow] Composable if our
 *  `weatherLoading` variable is `true` or an [WeatherRow] Composable whose `onRefresh` lambda argument
 *  uses our [CoroutineScope] variable `coroutineScope` to launch a coroutine which calls `loadWeather`
 *  to have it set `weatherLoading` to `true`, delay 3 seconds then set `weatherLoading` back to `false`
 *  (causing the item to recompose and display the [LoadingRow] in the `true` path of the `if` for the
 *  3 seconds).
 *  - an `item` holding a [Spacer] whose height is 32.dp
 *
 * The "Topics" section has four items in it:
 *  - an `item` displaying a [Header] Composable displaying the `title` "Topics"
 *  - an `item` holding a [Spacer] whose height is 16.dp
 *  - an [items] which displays all of the [String] entries in the [List] of [String] variable
 *  `allTopics` using a [TopicRow] whose `topic` argument is the [String] from the [List], whose
 *  `expanded` argument is `true` if the [State] wrapped [String] variable `expandedTopic` is equal
 *  to the [String] from the [List], and whose `onClick` lambda sets`expandedTopic` to `null` if it
 *  is equal to the [String] from the [List], or else to the [String] from the [List] if it was not
 *  (the [TopicRow] will use an [Modifier.animateContentSize] to animate the visibility of a [Text]
 *  displaying some additional nonsense words when its `expanded` parameter toggles between `true`
 *  (visible) and `false` (invisible)).
 *  - an `item` holding a [Spacer] whose height is 32.dp
 *
 * The "Tasks" section has four items in it:
 *  - an `item` displaying a [Header] Composable displaying the `title` "Tasks"
 *  - an `item` holding a [Spacer] whose height is 16.dp
 *  - if our [List] of [String] variable `tasks` is empty (the user has "swiped" away all of the
 *  tasks) we display an `item` that consists of a [TextButton] whose `content` is a [Text] displaying
 *  the [String] "ADD TASKS", and whose `onClick` argument is a lambda that calls the [MutableList.clear]
 *  method of `tasks` (which is overridden by the [SnapshotStateList] that is wrapping `tasks`) to
 *  remove all entries from `tasks` and then call the [MutableCollection.addAll] method of `tasks`
 *  to add all of the strings in `allTasks` to it.
 *  - the bottom of the "Tasks" section (and the [LazyColumn]) is an [items] which retrieves each of
 *  the [String]'s in `tasks` to initialize its [String] variable `val task` (or `null` if the index
 *  is out of bounds of this list) and if `task` is not `null` it wraps in a [key] (utility composable
 *  that is used to "group" or "key" a block of execution inside of a composition) a [TaskRow] whose
 *  `task` argument is the `task` variable, and whose `onRemove` argument is a lambda that calls the
 *  [MutableCollection.remove] method of `tasks` to remove `task` from the [List].
 *
 * Below the [LazyColumn] at the bottom of the `content` of the [Scaffold] is an [EditMessage] whose
 * `shown` argument is our [MutableState] wrapped [Boolean] variable `editMessageShown`.
 *
 */
@Composable
fun Home() {
    // String resources.
    /**
     * All of the tasks that are initially loaded into our [SnapshotStateList] of [String] variable
     * `val tasks` which is itself used as the current task list that is displayed in [TaskRow] items
     * in the [LazyColumn] that is the `content` of our [Scaffold].
     */
    val allTasks: Array<String> = stringArrayResource(id = R.array.tasks)

    /**
     * All of the topics that are displayed in [TopicRow] items in the [LazyColumn] that is the
     * `content` of our [Scaffold].
     */
    val allTopics: List<String> = stringArrayResource(id = R.array.topics).toList()

    /**
     * The currently selected tab.
     */
    var tabPage: TabPage by remember { mutableStateOf(value = TabPage.Home) }

    /**
     * True if the weather data is currently loading.
     */
    var weatherLoading: Boolean by remember { mutableStateOf(value = false) }

    /**
     * Holds all the tasks currently shown on the task list.
     */
    val tasks: SnapshotStateList<String> = remember { mutableStateListOf(elements = allTasks) }

    /**
     * Holds the topic that is currently expanded to show its body.
     */
    var expandedTopic: String? by remember { mutableStateOf(value = null) }

    /**
     * True if the message about the edit feature is shown.
     */
    var editMessageShown: Boolean by remember { mutableStateOf(value = false) }

    /**
     * Simulates loading weather data. This takes 3 seconds. If `weatherLoading` is `false` we set
     * it to `true`, call [delay] to delay for 3,000 milliseconds then set `weatherLoading` to `false`
     * and return. If `weatherLoading` is `true` when we are called we do nothing and return.
     */
    suspend fun loadWeather() {
        if (!weatherLoading) {
            weatherLoading = true
            delay(3_000L)
            weatherLoading = false
        }
    }

    /**
     * Shows the message about edit feature.
     */
    suspend fun showEditMessage() {
        if (!editMessageShown) {
            editMessageShown = true
            delay(3000L)
            editMessageShown = false
        }
    }

    /**
     * Load the weather at the initial composition.
     */
    LaunchedEffect(Unit) {
        loadWeather()
    }

    val lazyListState: LazyListState = rememberLazyListState()

    /**
     * The background color. The value is changed by the current tab.
     */
    val backgroundColor: Color by animateColorAsState(if (tabPage == TabPage.Home) Purple100 else Green300)

    /**
     * The coroutine scope for event handlers calling suspend functions.
     */
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            HomeTabBar(
                backgroundColor = backgroundColor,
                tabPage = tabPage,
                onTabSelected = { select: TabPage -> tabPage = select }
            )
        },
        backgroundColor = backgroundColor,
        floatingActionButton = {
            HomeFloatingActionButton(
                extended = lazyListState.isScrollingUp(),
                onClick = {
                    coroutineScope.launch {
                        showEditMessage()
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
            state = lazyListState,
            modifier = Modifier.padding(paddingValues = padding)
        ) {
            // Weather
            item { Header(title = stringResource(id = R.string.weather)) }
            item { Spacer(modifier = Modifier.height(height = 16.dp)) }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    if (weatherLoading) {
                        LoadingRow()
                    } else {
                        WeatherRow(onRefresh = {
                            coroutineScope.launch {
                                loadWeather()
                            }
                        })
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(height = 32.dp)) }

            // Topics
            item { Header(title = stringResource(id = R.string.topics)) }
            item { Spacer(modifier = Modifier.height(height = 16.dp)) }
            items(allTopics) { topic: String ->
                TopicRow(
                    topic = topic,
                    expanded = expandedTopic == topic,
                    onClick = {
                        expandedTopic = if (expandedTopic == topic) null else topic
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(height = 32.dp)) }

            // Tasks
            item { Header(title = stringResource(id = R.string.tasks)) }
            item { Spacer(modifier = Modifier.height(height = 16.dp)) }
            if (tasks.isEmpty()) {
                item {
                    TextButton(onClick = { tasks.clear(); tasks.addAll(allTasks) }) {
                        Text(stringResource(id = R.string.add_tasks))
                    }
                }
            }
            items(count = tasks.size) { i: Int ->
                val task: String? = tasks.getOrNull(i)
                if (task != null) {
                    key(task) {
                        TaskRow(
                            task = task,
                            onRemove = { tasks.remove(task) }
                        )
                    }
                }
            }
        }
        EditMessage(shown = editMessageShown)
    }
}

/**
 * Shows the floating action button. Our root Composable is an [FloatingActionButton] whose `onClick`
 * argument is our [onClick] parameter. Its `content` consists of a [Row] root Composable that holds
 * an [Icon] whose `imageVector` argument is the system [ImageVector] `Edit` of [Icons.Default] which
 * resolves to [Icons.Filled.Edit] which is a stylized "pencil", and its `contentDescription` argument
 * is `null`. The other Composable in the [Row] is a [Text] that displays the `text` "EDIT" that is
 * wrapped in a [AnimatedVisibility] whose `visible` argument is our [Boolean] parameter [extended].
 * This [RowScope.AnimatedVisibility] animates the appearance and disappearance of its content when
 * the [AnimatedVisibility] is in a [Row]. The default animations are tailored specific to the [Row]
 * layout. The default `enter` animation is fading in while expanding horizontally when [extended]
 * changes to `true` and the default `exit` animation is fading out while shrinking horizontally when
 * [extended] changes to `false`. The [Scaffold] in our [Home] Composable uses this Composable as its
 * `topBar` argument with the [extended] argument the value of the [LazyListState.isScrollingUp]
 * property of the [LazyListState] of the [LazyColumn] it uses as the `content` of the [Scaffold].
 *
 * @param extended Whether the tab should be shown in its expanded state.
 * @param onClick the lambda that will be called when the [FloatingActionButton] is clicked.
 */
@Composable
private fun HomeFloatingActionButton(
    extended: Boolean,
    onClick: () -> Unit
) {
    // Use `FloatingActionButton` rather than `ExtendedFloatingActionButton` for full control on
    // how it should animate.
    FloatingActionButton(onClick = onClick) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )
            // Toggle the visibility of the content with animation.
            AnimatedVisibility(visible = extended) {
                Text(
                    text = stringResource(id = R.string.edit),
                    modifier = Modifier
                        .padding(start = 8.dp, top = 3.dp)
                )
            }
        }
    }
}

/**
 * Shows a message that the edit feature is not available when its [Boolean] parameter [shown] is
 * `true`, and uses an [AnimatedVisibility] to animate the visibility of that message when [shown]
 * changes from `false` to `true`, or from `true` to `false`. An [AnimatedVisibility] is our root
 * Composable with its `visible` argument our [shown] parameter (defines whether the content should
 * be visible), whose `enter` argument ([EnterTransition]'s used for the appearing animation) is a
 * [slideInVertically] whose `initialOffsetY` (lambda that takes the full Height of the content and
 * returns the initial offset for the slide-in) is minus the full Height (Enters by sliding down from
 * offset -fullHeight to 0) and whose `animationSpec` is a [tween] with `durationMillis` of 250ms,
 * and `easing` using [LinearOutSlowInEasing] (Incoming elements are animated using deceleration
 * easing, which starts a transition at peak velocity (the fastest point of an element’s movement)
 * and ends at rest). The `exit` argument of the [AnimatedVisibility] is a `slideOutVertically` whose
 * `targetOffsetY` argument is minus the `fullHeight` (Exits by sliding up from offset 0 to
 * -fullHeight) with a [tween] with a `durationMillis` of 250ms, and easing of [FastOutLinearInEasing]
 * as its `animationSpec` (Elements exiting a screen use acceleration easing, where they start at rest
 * and end at peak velocity).
 *
 * The `content` that the [AnimatedVisibility] is wrapping is a [Surface] whose `modifier` argument
 * is a [Modifier.fillMaxWidth] to have it take the entire width of the incoming constraints, whose
 * `color` argument (background color) the [Colors.secondary] of [MaterialTheme.colors] which is
 * [Teal200] in our [AnimationCodelabTheme] custom [MaterialTheme], and its `elevation` argument is
 * 4.dp (size of the shadow below the surface). The `content` of the [Surface] is a [Text] displaying
 * the `text` "Edit feature is not supported" with its `modifier` argument a [Modifier.padding] that
 * adds 16.dp to all sides of the [Text].
 *
 * @param shown when it changes to `true` we use [AnimatedVisibility] to animate the visibility of
 * our content to visible, and when it changes to `false` we use [AnimatedVisibility] to animate the
 * visibility of our content to invisible
 */
@Composable
private fun EditMessage(shown: Boolean) {
    AnimatedVisibility(
        visible = shown,
        enter = slideInVertically(
            // Enters by sliding down from offset -fullHeight to 0.
            initialOffsetY = { fullHeight: Int -> -fullHeight },
            animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            // Exits by sliding up from offset 0 to -fullHeight.
            targetOffsetY = { fullHeight: Int -> -fullHeight },
            animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.secondary,
            elevation = 4.dp
        ) {
            Text(
                text = stringResource(id = R.string.edit_message),
                modifier = Modifier.padding(all = 16.dp)
            )
        }
    }
}

/**
 * Returns whether the lazy list is currently scrolling up (no it doesn't). We initialize and remember
 * our [Int] variable `var previousIndex` as the [MutableState] wrapped value of the
 * [LazyListState.firstVisibleItemIndex] property of our [LazyListState] receiver (the index of the
 * first item that is visible), with that [LazyListState] (`this`) serving as the `key1` argument of
 * the [remember] call, and we initialize and remember our [Int] variable `var previousScrollOffset`
 * as the [MutableState] wrapped value of the [LazyListState.firstVisibleItemScrollOffset] property
 * of our [LazyListState] receiver (the scroll offset of the first visible item), with that
 * [LazyListState] (`this`) serving as the `key1` argument of the [remember] call. Finally we remember
 * (with our [LazyListState] receiver serving as the `key1` argument) and return the [State.value]
 * of a [Boolean] `DerivedState` calculated by a block which returns `true` if our `previousIndex`
 * variable is not equal to the current [LazyListState.firstVisibleItemIndex] AND it is greater than
 * it (the [LazyColumn] has scrolled up since the last time the block was executed) OR if our
 * `previousIndex` variable IS equal to the current [LazyListState.firstVisibleItemIndex] and our
 * `previousScrollOffset` is greater than or equal to the current [LazyListState.firstVisibleItemScrollOffset]
 * (the [LazyColumn] has scrolled up or stopped scrolling since the last time the block was executed).
 * An [also] extension function is chained to the `if` expression which sets `previousIndex` to the
 * current [LazyListState.firstVisibleItemIndex] and `previousScrollOffset` to the current
 * [LazyListState.firstVisibleItemScrollOffset].
 *
 * @return a [State] wrapped [Boolean] which communicates information about the scrolling state of
 * its [LazyListState] receiver (that information is more complex than the original comment and name
 * of this extension function suggests but it is still an interesting effect when animated and that
 * is all that really matters in an "Animation Codelab" so let it be).
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex: Int by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset: Int by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

/**
 * Shows the header label. Our root (and only) Composable is a [Text] whose `text` argument if our
 * [String] parameter [title] (The text to be displayed), whose `modifier` argument is a
 * [Modifier.semantics] that adds a [heading] to the layout node (The node is marked as heading for
 * accessibility), and whose `style` argument is [Typography.h5] of [MaterialTheme.typography] (since
 * our [AnimationCodelabTheme] custom [MaterialTheme] does not change the default this is the fifth
 * largest headline, reserved for short, important text or numerals and the font is Roboto, weight
 * normal, size 24.sp, and letter spacing 0.sp).
 *
 * @param title The title to be shown.
 */
@Composable
private fun Header(
    title: String
) {
    Text(
        text = title,
        modifier = Modifier.semantics { heading() },
        style = MaterialTheme.typography.h5
    )
}

/**
 * Shows a row for one topic.
 *
 * @param topic The topic title.
 * @param expanded Whether the row should be shown expanded with the topic body.
 * @param onClick Called when the row is clicked.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TopicRow(topic: String, expanded: Boolean, onClick: () -> Unit) {
    TopicRowSpacer(visible = expanded)
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 2.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
                // This `Column` animates its size when its content changes.
                .animateContentSize()
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
                Text(
                    text = topic,
                    style = MaterialTheme.typography.body1
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(height = 8.dp))
                Text(
                    text = stringResource(id = R.string.lorem_ipsum),
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
    TopicRowSpacer(visible = expanded)
}

/**
 * Shows a separator for topics.
 */
@Composable
fun TopicRowSpacer(visible: Boolean) {
    AnimatedVisibility(visible = visible) {
        Spacer(modifier = Modifier.height(height = 8.dp))
    }
}

/**
 * Shows the bar that holds 2 tabs.
 *
 * @param backgroundColor The background color for the bar.
 * @param tabPage The [TabPage] that is currently selected.
 * @param onTabSelected Called when the tab is switched.
 */
@Composable
private fun HomeTabBar(
    backgroundColor: Color,
    tabPage: TabPage,
    onTabSelected: (tabPage: TabPage) -> Unit
) {
    TabRow(
        selectedTabIndex = tabPage.ordinal,
        backgroundColor = backgroundColor,
        indicator = { tabPositions ->
            HomeTabIndicator(tabPositions = tabPositions, tabPage = tabPage)
        }
    ) {
        HomeTab(
            icon = Icons.Default.Home,
            title = stringResource(id = R.string.home),
            onClick = { onTabSelected(TabPage.Home) }
        )
        HomeTab(
            icon = Icons.Default.AccountBox,
            title = stringResource(id = R.string.work),
            onClick = { onTabSelected(TabPage.Work) }
        )
    }
}

/**
 * Shows an indicator for the tab.
 *
 * @param tabPositions The list of [TabPosition]s from a [TabRow].
 * @param tabPage The [TabPage] that is currently selected.
 */
@Composable
private fun HomeTabIndicator(
    tabPositions: List<TabPosition>,
    tabPage: TabPage
) {
    val transition: Transition<TabPage> = updateTransition(
        targetState = tabPage,
        label = "Tab indicator"
    )
    val indicatorLeft: Dp by transition.animateDp(
        transitionSpec = {
            if (TabPage.Home isTransitioningTo TabPage.Work) {
                // Indicator moves to the right.
                // Low stiffness spring for the left edge so it
                // moves slower than the right edge.
                spring(stiffness = Spring.StiffnessVeryLow)
            } else {
                // Indicator moves to the left.
                // Medium stiffness spring for the left edge so it
                // moves faster than the right edge.
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = "Indicator left"
    ) { page: TabPage ->
        tabPositions[page.ordinal].left
    }
    val indicatorRight: Dp by transition.animateDp(
        transitionSpec = {
            if (TabPage.Home isTransitioningTo TabPage.Work) {
                // Indicator moves to the right
                // Medium stiffness spring for the right edge so it
                // moves faster than the left edge.
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                // Indicator moves to the left.
                // Low stiffness spring for the right edge so it
                // moves slower than the left edge.
                spring(stiffness = Spring.StiffnessVeryLow)
            }
        },
        label = "Indicator right"
    ) { page: TabPage ->
        tabPositions[page.ordinal].right
    }
    val color: Color by transition.animateColor(
        label = "Border color"
    ) { page: TabPage ->
        if (page == TabPage.Home) Purple700 else Green800
    }
    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(all = 4.dp)
            .fillMaxSize()
            .border(
                BorderStroke(width = 2.dp, color = color),
                RoundedCornerShape(size = 4.dp)
            )
    )
}

/**
 * Shows a tab.
 *
 * @param icon The icon to be shown on this tab.
 * @param title The title to be shown on this tab.
 * @param onClick Called when this tab is clicked.
 * @param modifier The [Modifier].
 */
@Composable
private fun HomeTab(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(all = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(width = 16.dp))
        Text(text = title)
    }
}

/**
 * Shows the weather.
 *
 * @param onRefresh Called when the refresh icon button is clicked.
 */
@Composable
private fun WeatherRow(
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size = 48.dp)
                .clip(shape = CircleShape)
                .background(color = Amber600)
        )
        Spacer(modifier = Modifier.width(width = 16.dp))
        Text(text = stringResource(id = R.string.temperature), fontSize = 24.sp)
        Spacer(modifier = Modifier.weight(weight = 1f))
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(id = R.string.refresh)
            )
        }
    }
}

/**
 * Shows the loading state of the weather.
 */
@Composable
private fun LoadingRow() {
    // Creates an `InfiniteTransition` that runs infinite child animation values.
    val infiniteTransition: InfiniteTransition = rememberInfiniteTransition()
    val alpha: Float by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        // `infiniteRepeatable` repeats the specified
        // duration-based `AnimationSpec` infinitely.
        animationSpec = infiniteRepeatable(
            // The `keyframes` animates the value by specifying multiple timestamps.
            animation = keyframes {
                // One iteration is 1000 milliseconds.
                durationMillis = 1000
                // 0.7f at the middle of an iteration.
                0.7f at 500
            },
            // When the value finishes animating from 0f to 1f, it repeats by reversing the
            // animation direction.
            repeatMode = RepeatMode.Reverse
        )
    )
    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size = 48.dp)
                .clip(shape = CircleShape)
                .background(color = Color.LightGray.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(width = 16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 32.dp)
                .background(color = Color.LightGray.copy(alpha = alpha))
        )
    }
}

/**
 * Shows a row for one task.
 *
 * @param task The task description.
 * @param onRemove Called when the task is swiped away and removed.
 */
@Composable
private fun TaskRow(task: String, onRemove: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .swipeToDismiss(onDismissed = onRemove),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(width = 16.dp))
            Text(
                text = task,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

/**
 * The modified element can be horizontally swiped away.
 *
 * @param onDismissed Called when the element is swiped to the edge of the screen.
 */
@SuppressLint("ReturnFromAwaitPointerEventScope", "MultipleAwaitPointerEventScopes") // There is a right way to do this, so I assume this is okay.
private fun Modifier.swipeToDismiss(
    onDismissed: () -> Unit
): Modifier = composed {
    // This `Animatable` stores the horizontal offset for the element.
    val offsetX: Animatable<Float, AnimationVector1D> = remember { Animatable(0f) }
    pointerInput(key1 = Unit) {
        // Used to calculate a settling position of a fling animation.
        val decay: DecayAnimationSpec<Float> = splineBasedDecay(this)
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            while (true) {
                // Wait for a touch down event. Track the pointerId based on the touch
                val pointerId: PointerId = awaitPointerEventScope { awaitFirstDown().id }
                // Interrupt any ongoing animation.
                offsetX.stop() // Add this line to cancel any on-going animations
                // Prepare for drag events and record velocity of a fling.
                val velocityTracker = VelocityTracker()
                // Wait for drag events.
                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change: PointerInputChange ->
                        // Record the position after offset
                        // Get the drag amount change to offset the item with
                        val horizontalDragOffset: Float = offsetX.value + change.positionChange().x
                        // Need to call this in a launch block in order to run it separately
                        // outside of the awaitPointerEventScope
                        launch {
                            // Overwrite the `Animatable` value while the element is dragged.
                            // Instantly set the Animable to the dragOffset to ensure its moving
                            // as the user's finger moves
                            offsetX.snapTo(targetValue = horizontalDragOffset)
                        }
                        // Record the velocity of the drag.
                        velocityTracker.addPosition(timeMillis = change.uptimeMillis, position = change.position)
                        // Consume the gesture event, not passed to external
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                }
                // Dragging finished. Calculate the velocity of the fling.
                val velocity: Float = velocityTracker.calculateVelocity().x
                // Calculate where the element eventually settles after the fling animation.
                val targetOffsetX: Float = decay.calculateTargetValue(initialValue = offsetX.value, initialVelocity = velocity)
                // The animation should end as soon as it reaches these bounds.
                offsetX.updateBounds(
                    // Set the upper and lower bounds so that the animation stops when it
                    // reaches the edge
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )
                launch {
                    if (targetOffsetX.absoluteValue <= size.width) {
                        // Not enough velocity; Slide back to the default position.
                        offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                    } else {
                        // Enough velocity to slide away the element to the edge.
                        offsetX.animateDecay(initialVelocity = velocity, animationSpec = decay)
                        // The element was swiped away.
                        onDismissed()
                    }
                }
            }
        }
    }
        // Apply the horizontal offset to the element.
        .offset {
            IntOffset(x = offsetX.value.roundToInt(), y = 0)
        }
}

@Preview
@Composable
private fun PreviewHomeTabBar() {
    HomeTabBar(
        backgroundColor = Purple100,
        tabPage = TabPage.Home,
        onTabSelected = {}
    )
}

@Preview
@Composable
private fun PreviewHome() {
    AnimationCodelabTheme {
        Home()
    }
}
