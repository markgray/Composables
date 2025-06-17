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

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import com.google.samples.apps.nowinandroid.R
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaGradientBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationSuiteScaffold
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationSuiteScope
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTopAppBar
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.GradientColors
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LocalGradientColors
import com.google.samples.apps.nowinandroid.feature.settings.SettingsDialog
import com.google.samples.apps.nowinandroid.navigation.NiaNavHost
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass
import com.google.samples.apps.nowinandroid.feature.settings.R as settingsR

/**
 * Top-level stateful composable that represents screens for the application, it wraps its stateless
 * overload in a [NiaGradientBackground] which is in turn wrapped in a [NiaBackground], and if
 * it determines that the device is offline will have a [LaunchedEffect] show q snackbar to inform
 * the user that they are not connected to the internet.
 *
 * We start by initializing our [Boolean] variable `shouldShowGradientBackground` to `true` if
 * the [NiaAppState.currentTopLevelDestination] of our [NiaAppState] parameter [appState] is
 * [TopLevelDestination.FOR_YOU], and to `false` if it is not. We initialize and remember our
 * [MutableState] wrapped [Boolean] variable `var showSettingsDialog` to an initial value of `false`.
 *
 * Then our root composable is a [NiaBackground] whose `modifier` argument is our [Modifier] parameter
 * [modifier], and in its `content` composable lambda argument we compose a [NiaGradientBackground]
 * whose `gradientColors` argument is the current [LocalGradientColors] if `shouldShowGradientBackground`
 * is `true`, and a default [GradientColors] object if it is `false` (all [Color] arguments are
 * [Color.Unspecified]).
 *
 * In the `content` composable lambda argument of the [NiaGradientBackground] composable we start by
 * initializing and remembering our [SnackbarHostState] variable `val snackbarHostState` to a new
 * instance. Then we initialize our [State] wrapped [Boolean] variable `val isOffline` by calling
 * the [collectAsStateWithLifecycle] method of the [StateFlow] wrapped [Boolean] property
 * [NiaAppState.isOffline] of our [NiaAppState] parameter [appState]. We initialize or [String]
 * variable `val notConnectedMessage` to the [String] with the resource id [R.string.not_connected]
 * ("You are not connected to the internet"). Then we compose a [LaunchedEffect] whose `key1` argument
 * is `isOffline` and in whose [CoroutineScope] `block` composable lambda argument if `isOffline` is
 * `true` we call the [SnackbarHostState.showSnackbar] method of the [SnackbarHostState] variable
 * `snackBarHostState` to show a snackbar with the [String] variable `notConnectedMessage` as the
 * `message` argument and a `duration` of [Indefinite].
 *
 * Finally we compose our stateless [NiaApp] overload whose arguments are:
 *  - `appState`: is our [NiaAppState] parameter [appState].
 *  - `snackbarHostState`: is our [SnackbarHostState] variable `snackBarHostState`.
 *  - `showSettingsDialog`: is our [MutableState] wrapped [Boolean] variable `showSettingsDialog`.
 *  - `onSettingsDismissed`: is a lambda that sets `showSettingsDialog` to `false`.
 *  - `onTopAppBarActionClick`: is a lambda that sets `showSettingsDialog` to `true`.
 *  - `windowAdaptiveInfo`: is our [WindowAdaptiveInfo] parameter [windowAdaptiveInfo].
 *
 * @param appState [NiaAppState] that contains the [NetworkMonitor], [UserNewsResourceRepository],
 * [TimeZoneMonitor] used by the app, as well as the [NavHostController] used and a [CoroutineScope]
 * that is used as the `scope` parameter when converting [Flow]s to [StateFlow]s inside of
 * [NiaAppState].
 * @param modifier [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller, the `onCreate` override of `MainActivity` does not pass us any so the
 * empty, default, or starter Modifier that contains no elements is used.
 * @param windowAdaptiveInfo the current [WindowAdaptiveInfo] for the context, it contains the
 * [WindowSizeClass] and the [Posture] to use to decide how the layout is supposed to be adapted
 * for the device we are running on.
 */
@Composable
fun NiaApp(
    appState: NiaAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val shouldShowGradientBackground: Boolean =
        appState.currentTopLevelDestination == TopLevelDestination.FOR_YOU
    var showSettingsDialog: Boolean by rememberSaveable { mutableStateOf(false) }

    NiaBackground(modifier = modifier) {
        NiaGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

            val isOffline: Boolean by appState.isOffline.collectAsStateWithLifecycle()

            // If user is not connected to the internet show a snack bar to inform them.
            val notConnectedMessage: String = stringResource(R.string.not_connected)
            LaunchedEffect(key1 = isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = notConnectedMessage,
                        duration = Indefinite,
                    )
                }
            }

            NiaApp(
                appState = appState,
                snackbarHostState = snackbarHostState,
                showSettingsDialog = showSettingsDialog,
                onSettingsDismissed = { showSettingsDialog = false },
                onTopAppBarActionClick = { showSettingsDialog = true },
                windowAdaptiveInfo = windowAdaptiveInfo,
            )
        }
    }
}

/**
 * This is the stateless overload of [NiaApp] which is used by its stateful overload.
 *
 * We start by initializing our [State] wrapped [Set] of [TopLevelDestination] variable
 * `unreadDestinations` by calling the [collectAsStateWithLifecycle] method of the [StateFlow]
 * of [Set] of [TopLevelDestination] property [NiaAppState.topLevelDestinationsWithUnreadResources]
 * of our [NiaAppState] parameter [appState]. We initialize our [NavDestination] variable
 * `currentDestination` to the [NiaAppState.currentDestination] property of our [NiaAppState]
 * parameter [appState]. If our [Boolean] parameter [showSettingsDialog] is `true` we compose a
 * [SettingsDialog] whose `onDismiss` argument is a lambda calls our lambda parameter
 * [onSettingsDismissed].
 *
 * Finally our root composable is a [NiaNavigationSuiteScaffold] in whose [NiaNavigationSuiteScope]
 * `navigationSuiteItems` argument we use the [Iterable.forEach] method of the [List] of
 * [TopLevelDestination] property [NiaAppState.topLevelDestinations] of our [NiaAppState] parameter
 * [appState] to iterate through the [List] capturing each [TopLevelDestination] in variable
 * `destination`. We then initialize our [Boolean] variable `hasUnread` to `true` if our [Set] of
 * [TopLevelDestination] variable `unreadDestinations` contains our [TopLevelDestination] variable
 * `destination`, and to `false` if it does not. We intialize our [Boolean] variable `selected` to
 * `true` if our [NavDestination] variable `currentDestination` is in the hierarchy of the
 * [TopLevelDestination.baseRoute] of our [TopLevelDestination] variable `destination`, and to
 * `false` if it is not. We then compose a [NiaNavigationSuiteScope.item] whose arguments are:
 *  - `selected`: is our [Boolean] variable `selected`.
 *  - `onClick`: is a lambda that calls the [NiaAppState.navigateToTopLevelDestination] method
 *  of our [NiaAppState] parameter [appState] to navigate to the [TopLevelDestination] variable
 *  `destination`.
 *  - `icon`: is a lambda that composes an [Icon] whose `imageVector` argument is the [ImageVector]
 *  drawn by the [TopLevelDestination.unselectedIcon] of our [TopLevelDestination] variable
 *  `destination`, and whose `contentDescription` argument `null`.
 *  - `selectedIcon`: is a lambda that composes an [Icon] whose `imageVector` argument is the
 *  [ImageVector] drawn by the [TopLevelDestination.selectedIcon] of our [TopLevelDestination]
 *  variable `destination`, and whose `contentDescription` argument `null`.
 *  - `label`: is a lambda that composes a [Text] whose `text` argument is the [String] whose
 *  resource id is the [TopLevelDestination.iconTextId] of our [TopLevelDestination] variable
 *  `destination`.
 *  - `modifier`: is a [Modifier.testTag] whose `tag` argument is the string "NiaNavItem", chained
 *  to a [Modifier.notificationDot] is `hasUnread` is `true`, and to the empty [Modifier] if
 *  `hasUnread` is `false`.
 *
 * The `windowAdaptiveInfo` argument of the [NiaNavigationSuiteScaffold] is our [WindowAdaptiveInfo]
 * parameter [windowAdaptiveInfo], and in its `content` composable lambda argument we compose a
 * [Scaffold] whose arguments are:
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.semantics] whose that
 *  sets the `testTagsAsResourceId` property to `true`.
 *  - `containerColor`: is [Color.Transparent].
 *  - `contentColor`: is the [ColorScheme.onBackground] of our custom [MaterialTheme.colorScheme]
 *  - `contentWindowInsets`: is a [WindowInsets] whose `left`, `top`, `right`, and `bottom` are all 0.
 *  - `snackbarHost`: is a [SnackbarHost] whose `hostState` argument is our [SnackbarHostState]
 *  parameter [snackbarHostState], and whose `modifier` argument is a [Modifier.windowInsetsPadding]
 *  whose `insets` argument is a [WindowInsets.Companion.safeDrawing].
 *
 * In the `content` composable lambda argument of the [Scaffold] we capture the [PaddingValues]
 * passed the lambda in variable `padding` then compose a [Column] whose `modifier` argument is
 * a [Modifier.fillMaxSize] chained to a [Modifier.padding] whose `paddingValues` argument is our
 * [PaddingValues] variable `padding`, chained to a [Modifier.consumeWindowInsets] whose `paddingValues`
 * argument is our [PaddingValues] variable `padding`, and chained to a [Modifier.windowInsetsPadding]
 * whose `insets` argument is a [WindowInsets.Companion.safeDrawing] with only its
 * [WindowInsetsSides.Horizontal] values.
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we start by initializing
 * our [TopLevelDestination] variable `val destination` to the [NiaAppState.currentTopLevelDestination]
 * of our [NiaAppState] parameter [appState]. We initialize our [Boolean] variable
 * `var shouldShowTopAppBar` to `false`. Then if our [TopLevelDestination] variable `destination` is
 * not `null` we set our [Boolean] variable `shouldShowTopAppBar` to `true` and compose a
 * [NiaTopAppBar] whose arguments are:
 *  - `titleRes`: is the [TopLevelDestination.titleTextId] of our [TopLevelDestination] variable
 *  `destination`.
 *  - `navigationIcon`: is the [ImageVector] drawn by [NiaIcons.Search]
 *  - `navigationIconContentDescription`: is the [String] with the resource id
 *  `R.string.search_description` ("Search").
 *  - `actionIcon`: is the [ImageVector] drawn by [NiaIcons.Settings]
 *  - `actionIconContentDescription`: is the [String] with the resource id
 *  [settingsR.string.feature_settings_top_app_bar_action_icon_description] ("Settings").
 *  - `colors`: is a [TopAppBarDefaults.topAppBarColors] whose `containerColor` argument is
 *  [Color.Transparent].
 *  - `onActionClick`: is a lambda that calls our [onTopAppBarActionClick] lambda parameter.
 *  - `onNavigationClick`: is a lambda that calls the [NiaAppState.navigateToSearch] method of
 *  our [NiaAppState] parameter [appState].
 *
 * Finally we compose a [Box] whose `modifier` argument is a [Modifier.consumeWindowInsets] whose
 * `insets` argument is a [WindowInsets.Companion.safeDrawing] with only its [WindowInsetsSides.Top]
 * inset if `shouldShowTopAppBar` is `true` or a [WindowInsets] whose `left`, `top`, `right`, and
 * `bottom` are all 0 if it is `false`. In the [BoxScope] `content` composable lambda argument of
 * the [Box] we compose a [NiaNavHost] whose arguments are:
 *  - `appState`: is our [NiaAppState] parameter [appState].
 *  - `onShowSnackbar`: is a lambda that captures the two [String]s passed the lambda in variables
 *  `message` and `action`, and calls the [SnackbarHostState.showSnackbar] method of our
 *  [SnackbarHostState] parameter [snackbarHostState] to show a snackbar with the [String] variable
 *  `message` as the `message` argument, the [String] variable `action` as the `actionLabel` argument,
 *  and [Short] as the `duration` argument. If the call to [SnackbarHostState.showSnackbar] returns
 *  [ActionPerformed] (action on the Snackbar has been clicked before the time out passed) the lambda
 *  returns `true`.
 *
 * @param appState [NiaAppState] that contains the [NetworkMonitor], [UserNewsResourceRepository],
 * [TimeZoneMonitor] used by the app, as well as the [NavHostController] used and a [CoroutineScope]
 * that is used as the `scope` parameter when converting [Flow]s to [StateFlow]s inside of
 * [NiaAppState].
 * @param snackbarHostState [SnackbarHostState] that is used to show snackbars.
 * @param showSettingsDialog [Boolean] that is used to show the settings dialog if it is `true`.
 * @param onSettingsDismissed lambda that is used to dismiss the settings dialog.
 * @param onTopAppBarActionClick lambda that is used to open the settings dialog.
 * @param modifier a [Modifier] instance that our caller can use to modifiy our appearance and/or
 * behavior. Our caller, our statefull overload, does not pass us any so the empty, default, or
 * starter [Modifier] that contains no elements is used.
 * @param windowAdaptiveInfo the current [WindowAdaptiveInfo] for the context, it contains the
 * [WindowSizeClass] and the [Posture] to use to decide how the layout is supposed to be adapted
 * for the device we are running on.
 */
@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
)
internal fun NiaApp(
    appState: NiaAppState,
    snackbarHostState: SnackbarHostState,
    showSettingsDialog: Boolean,
    onSettingsDismissed: () -> Unit,
    onTopAppBarActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val unreadDestinations: Set<TopLevelDestination> by appState.topLevelDestinationsWithUnreadResources
        .collectAsStateWithLifecycle()
    val currentDestination: NavDestination? = appState.currentDestination

    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { onSettingsDismissed() },
        )
    }

    NiaNavigationSuiteScaffold(
        navigationSuiteItems = {
            appState.topLevelDestinations.forEach { destination: TopLevelDestination ->
                val hasUnread: Boolean = unreadDestinations.contains(destination)
                val selected: Boolean = currentDestination
                    .isRouteInHierarchy(route = destination.baseRoute)
                item(
                    selected = selected,
                    onClick = { appState.navigateToTopLevelDestination(topLevelDestination = destination) },
                    icon = {
                        Icon(
                            imageVector = destination.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = destination.selectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(text = stringResource(id = destination.iconTextId)) },
                    modifier = Modifier
                        .testTag(tag = "NiaNavItem")
                        .then(if (hasUnread) Modifier.notificationDot() else Modifier),
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ) {
        Scaffold(
            modifier = modifier.semantics {
                testTagsAsResourceId = true
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.windowInsetsPadding(insets = WindowInsets.safeDrawing),
                )
            },
        ) { padding: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
                    .consumeWindowInsets(paddingValues = padding)
                    .windowInsetsPadding(
                        insets = WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                // Show the top app bar on top level destinations.
                val destination: TopLevelDestination? = appState.currentTopLevelDestination
                var shouldShowTopAppBar = false

                if (destination != null) {
                    shouldShowTopAppBar = true
                    NiaTopAppBar(
                        titleRes = destination.titleTextId,
                        navigationIcon = NiaIcons.Search,
                        navigationIconContentDescription = stringResource(
                            id = settingsR.string.feature_settings_top_app_bar_navigation_icon_description,
                        ),
                        actionIcon = NiaIcons.Settings,
                        actionIconContentDescription = stringResource(
                            id = settingsR.string.feature_settings_top_app_bar_action_icon_description,
                        ),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                        onActionClick = { onTopAppBarActionClick() },
                        onNavigationClick = { appState.navigateToSearch() },
                    )
                }

                Box(
                    // Workaround for https://issuetracker.google.com/338478720
                    modifier = Modifier.consumeWindowInsets(
                        insets = if (shouldShowTopAppBar) {
                            WindowInsets.safeDrawing.only(sides = WindowInsetsSides.Top)
                        } else {
                            WindowInsets(left = 0, top = 0, right = 0, bottom = 0)
                        },
                    ),
                ) {
                    NiaNavHost(
                        appState = appState,
                        onShowSnackbar = { message: String, action: String? ->
                            snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = action,
                                duration = Short,
                            ) == ActionPerformed
                        },
                    )
                }

                // TODO: We may want to add padding or spacer when the snackbar is shown so that
                //  content doesn't display behind it.
            }
        }
    }
}

/**
 * This [Modifier] is used to draw a circular "notification dot" on a navigation item. We initialize
 * our [Color] variable `val tertiaryColor` to the [ColorScheme.tertiary] color of our custom
 * [MaterialTheme.colorScheme]. Then we call the [drawWithContent] method and in its [ContentDrawScope]
 * `onDraw` composable lambda argument we first call the [ContentDrawScope.drawContent] method to
 * draw the composable that this [Modifier] is applied to, then we call the [ContentDrawScope.drawCircle]
 * method to draw a circle whose `color` is our `tertiaryColor` variable, whose `radius` is 5.dp
 * converted to pixels, and whose `center` is offset from the `center` of the item this [Modifier]
 * is applied to by an [Offset] whose `x` coordinate is `64.dp` converted to pixels times `.45f` and
 * whose `y` coordinate is `32.dp` converted to pixels times minus `.45f` minus `6.dp` converted to
 * pixels.
 */
@SuppressLint("UnnecessaryComposedModifier")
private fun Modifier.notificationDot(): Modifier =
    composed {
        val tertiaryColor: Color = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            drawContent()
            drawCircle(
                color = tertiaryColor,
                radius = 5.dp.toPx(),
                // This is based on the dimensions of the NavigationBar's "indicator pill";
                // however, its parameters are private, so we must depend on them implicitly
                // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
                center = center + Offset(
                    x = 64.dp.toPx() * .45f,
                    y = 32.dp.toPx() * -.45f - 6.dp.toPx(),
                ),
            )
        }
    }

/**
 * If the provided [route] is one of the destinations in this [NavDestination]'s hierarchy,
 * return `true`.
 *
 * This functions is used to check if the [NavDestination] of the current navigation location is in
 * the hierarchy of the [TopLevelDestination.baseRoute] of each of the [TopLevelDestination]s
 * displayed by the [NiaNavigationSuiteScaffold] to determine if the navigation item for that
 * [TopLevelDestination] should be displayed as `selected` or not.
 *
 * @param route the [KClass] of the destination route to check for.
 * @return `true` if this [NavDestination] is found in the hierarchy of the [KClass] of the [route]
 * parameter, `false` if it is `null` or it is not found in the hierarchy.
 */
@Suppress("NullableBooleanElvis")
private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false
