/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.metrics.performance.PerformanceMetricsState
import androidx.metrics.performance.PerformanceMetricsState.Holder
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.TrackDisposableJank
import com.google.samples.apps.nowinandroid.feature.bookmarks.navigation.navigateToBookmarks
import com.google.samples.apps.nowinandroid.feature.foryou.navigation.navigateToForYou
import com.google.samples.apps.nowinandroid.feature.interests.navigation.navigateToInterests
import com.google.samples.apps.nowinandroid.feature.search.navigation.navigateToSearch
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination.BOOKMARKS
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination.FOR_YOU
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination.INTERESTS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone

/**
 * Remembers and creates an instance of [NiaAppState]
 *
 * We start by calling our method [NavigationTrackingSideEffect] with its `navController` argument
 * our [NavHostController] parameter [navController] in order to set it up for tracking navigation
 * events to be used with JankStats. We then use [remember] with the arguments our parameters
 * [navController], [coroutineScope], [networkMonitor], [userNewsResourceRepository], and
 * [timeZoneMonitor] to remember a new instance of [NiaAppState] constructed using those same values
 * and return it.
 *
 * @param networkMonitor The [NetworkMonitor] to use.
 * @param userNewsResourceRepository The [UserNewsResourceRepository] to use.
 * @param timeZoneMonitor The [TimeZoneMonitor] to use.
 * @param coroutineScope The [CoroutineScope] to use. Defaults to the [CoroutineScope] returned by
 * [rememberCoroutineScope].
 * @param navController The [NavHostController] to use. Defaults to the [NavHostController] returned
 * by [rememberNavController].
 */
@Composable
fun rememberNiaAppState(
    networkMonitor: NetworkMonitor,
    userNewsResourceRepository: UserNewsResourceRepository,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): NiaAppState {
    NavigationTrackingSideEffect(navController = navController)
    return remember(
        navController,
        coroutineScope,
        networkMonitor,
        userNewsResourceRepository,
        timeZoneMonitor,
    ) {
        NiaAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
            userNewsResourceRepository = userNewsResourceRepository,
            timeZoneMonitor = timeZoneMonitor,
        )
    }
}

/**
 * Represents the overall state of the Nia application.
 *
 * This class holds information about the current navigation state, network connectivity,
 * user data, and time zone. It provides functions to navigate between different parts
 * of the app and exposes a [StateFlow] for observing changes in the application's state.
 *
 * @param navController The [NavHostController] used for navigation.
 * @param coroutineScope The [CoroutineScope] used for managing coroutines that our class launches.
 * @param networkMonitor The [NetworkMonitor] used for observing network connectivity.
 * @param userNewsResourceRepository The [UserNewsResourceRepository] used for accessing user data.
 * @param timeZoneMonitor The [TimeZoneMonitor] used for observing time zone changes.
 */
@Stable
class NiaAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    userNewsResourceRepository: UserNewsResourceRepository,
    timeZoneMonitor: TimeZoneMonitor,
) {
    /**
     * The [MutableState] wrapped [NavDestination] property [previousDestination] is used to
     * store the previous [NavDestination] that the user was on when they navigated to a new
     * destination. This is used to determine if the user is navigating back or forward in the
     * navigation history.
     */
    private val previousDestination: MutableState<NavDestination?> =
        mutableStateOf<NavDestination?>(null)

    /**
     * The [State] wrapped [NavDestination] property [currentDestination] is used to store the
     * current [NavDestination] that the user is on. This is used to determine the current
     * [TopLevelDestination] and to determine if the user is navigating back or forward in the
     * navigation history.
     *
     * We start by initializing our [State] wrapped [NavDestination] variable `val currentDestination`
     * to the [NavDestination] returned by calling the [NavHostController.currentBackStackEntryFlow]
     * method of our [NavHostController] parameter [navController] and calling the [StateFlow.collectAsState]
     * method of the [StateFlow] wrapped [NavDestination] returned to turn it into a [State]. If this
     * is not `null` we set [previousDestination] to the [NavDestination] and return it. If this is
     * `null` we return the [NavDestination] property [previousDestination].
     */
    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
            val currentEntry: State<NavBackStackEntry?> = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            // Fallback to previousDestination if currentEntry is null
            return currentEntry.value?.destination.also { destination: NavDestination? ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    /**
     * The [TopLevelDestination] property [currentTopLevelDestination] is the first of the
     * [TopLevelDestination] enums whose [TopLevelDestination.route] property matches the
     * [NavDestination.route] of the [currentDestination] property, or `null` if there is no
     * [TopLevelDestination] that matches the [currentDestination].
     */
    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.firstOrNull { topLevelDestination: TopLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) == true
            }
        }

    /**
     * The [StateFlow] wrapped [Boolean] property [isOffline] is used to store the current
     * network connectivity status. This is used to determine if the user is connected to the
     * internet or not. We start by calling the [map] method of the [Flow] of [Boolean]
     * property [NetworkMonitor.isOnline] to invert the values it emits. We then call the
     * [stateIn] method to convert the [Flow] of [Boolean] to a [StateFlow] of [Boolean] with the
     * `scope` argument our [CoroutineScope] parameter [coroutineScope], the `started` argument
     * [SharingStarted.WhileSubscribed] with a `stopTimeoutMillis` of `5_000`, and the
     * `initialValue` argument `false`. This will convert the [Flow] of [Boolean] to a [StateFlow]
     * of [Boolean] that will emit the current network connectivity status.
     */
    val isOffline: StateFlow<Boolean> = networkMonitor.isOnline
        .map(transform = Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = false,
        )

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * The top level destinations that have unread news resources. We call the
     * [UserNewsResourceRepository.observeAllForFollowedTopics] method of our
     * [UserNewsResourceRepository] parameter [userNewsResourceRepository] to get a [Flow] of
     * [Set] of [TopLevelDestination]s that have unread news resources, and then call the
     * [Flow.combine] method to combine this [Flow] with the [Flow] of [List] of [UserNewsResource]
     * returned by the [UserNewsResourceRepository.observeAllBookmarked] method of our
     * [UserNewsResourceRepository] parameter [userNewsResourceRepository] and use the [setOfNotNull]
     * method to create a [Flow] of [Set] of [TopLevelDestination]s that have unread news resources,
     * which the [stateIn] method converts to a [StateFlow] of [Set] of [TopLevelDestination]s
     * that have unread news resources using the `scope` argument our [CoroutineScope] parameter
     * [coroutineScope], the `started` argument [SharingStarted.WhileSubscribed] with a
     * `stopTimeoutMillis` of `5_000`, and the `initialValue` argument `emptySet()`.
     */
    val topLevelDestinationsWithUnreadResources: StateFlow<Set<TopLevelDestination>> =
        userNewsResourceRepository.observeAllForFollowedTopics()
            .combine(userNewsResourceRepository.observeAllBookmarked()) { forYouNewsResources, bookmarkedNewsResources ->
                setOfNotNull(
                    FOR_YOU.takeIf { forYouNewsResources.any { !it.hasBeenViewed } },
                    BOOKMARKS.takeIf { bookmarkedNewsResources.any { !it.hasBeenViewed } },
                )
            }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = emptySet(),
            )

    /**
     * The [StateFlow] wrapped [TimeZone] property [currentTimeZone] is used to store the current
     * time zone. This is used to determine the time zone of the device. We start by calling the
     * [TimeZoneMonitor.currentTimeZone] property of our [TimeZoneMonitor] parameter
     * [timeZoneMonitor] to get a [Flow] of [TimeZone]s. We then call the [stateIn] method to
     * convert the [Flow] of [TimeZone]s to a [StateFlow] of [TimeZone] with the `scope` argument
     * our [CoroutineScope] parameter [coroutineScope], the `started` argument
     * [SharingStarted.WhileSubscribed] with a `stopTimeoutMillis` of `5_000`, and the
     * `initialValue` argument [TimeZone.currentSystemDefault].
     */
    val currentTimeZone: StateFlow<TimeZone> = timeZoneMonitor.currentTimeZone
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = TimeZone.currentSystemDefault(),
        )

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * We start by calling the [trace] method with the `name` argument "Navigation: ${topLevelDestination.name}"
     * to wrap its `block` lambda argument with a trace. In the `block` lambda argument we first the
     * initialize our [NavOptions] variable `val topLevelNavOptions` to a new instance of [NavOptions]
     * in whose [NavOptionsBuilder] `optionsBuilder` lambda argument we call the [NavOptionsBuilder.popUpTo]
     * method to pop up to the start destination of the graph to avoid building up a large stack of
     * destinations on the back stack as users select items, set the [NavOptionsBuilder.launchSingleTop]
     * property to `true` to avoid multiple copies of the same destination when reselecting the same
     * item, and set the [NavOptionsBuilder.restoreState] property to `true` to restore state when
     * reselecting a previously selected item. We then use a `when` statement to switch on the value
     * of our [TopLevelDestination] parameter [topLevelDestination]:
     *  - If it is [FOR_YOU] we call the [navigateToForYou] method of our [NavHostController]
     *  parameter [navController] with its `navOptions` argument our [NavOptions] variable
     *  `topLevelNavOptions`.
     *  - If it is [BOOKMARKS] we call the [navigateToBookmarks] method of our [NavHostController]
     *  parameter [navController] with its `navOptions` argument our [NavOptions] variable
     *  `topLevelNavOptions`.
     *  - If it is [INTERESTS] we call the [navigateToInterests] method of our [NavHostController]
     *  parameter [navController] with its `initialTopicId` argument `null` and its `navOptions`
     *  argument our [NavOptions] variable `topLevelNavOptions`.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions: NavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

            @Suppress("RedundantValueArgument")
            when (topLevelDestination) {
                FOR_YOU -> navController.navigateToForYou(navOptions = topLevelNavOptions)
                BOOKMARKS -> navController.navigateToBookmarks(navOptions = topLevelNavOptions)
                INTERESTS -> navController.navigateToInterests(
                    initialTopicId = null,
                    navOptions = topLevelNavOptions,
                )
            }
        }
    }

    /**
     * Navigates to the search destination by calling the [navigateToSearch] method of our
     * [NavHostController] parameter [navController].
     */
    fun navigateToSearch(): Unit = navController.navigateToSearch()
}

/**
 * Stores information about navigation events to be used with JankStats. We call our
 * [TrackDisposableJank] method with its `keys` argument our [NavHostController] parameter
 * [navController] and in its [DisposableEffectScope] `reportMetric` lambda argument we
 * initialize capture the [Holder] passed the lambda in variable `metricsHolder` then
 * initialize our [NavController.OnDestinationChangedListener] variable `val listener` to a new
 * instance in whose `onDestinationChanged` lambda argument we call the [PerformanceMetricsState.putState]
 * method of the [Holder.state] property of our [Holder] variable `metricsHolder` with the
 * `key` argument "Navigation" and the `value` argument the [NavDestination.route] of the
 * [NavDestination] passed to the lambda. We then call the [NavController.addOnDestinationChangedListener]
 * method of our [NavController] parameter [navController] with the `listener` argument
 * our [NavController.OnDestinationChangedListener] variable `listener`. We call the
 * [DisposableEffectScope.onDispose] method to register a [DisposableEffectResult] that calls the
 * [NavController.removeOnDestinationChangedListener] method of our [NavController] parameter
 * [navController] with the `listener` argument our [NavController.OnDestinationChangedListener]
 * variable `listener`.
 *
 * @param navController The [NavHostController] whose navigation events are being tracked.
 */
@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {
    TrackDisposableJank(navController) { metricsHolder ->
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            metricsHolder.state?.putState("Navigation", destination.route.toString())
        }

        navController.addOnDestinationChangedListener(listener = listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener = listener)
        }
    }
}
