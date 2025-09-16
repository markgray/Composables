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

package androidx.compose.samples.crane.home

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.samples.crane.calendar.CalendarScreen
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.details.DetailsActivity
import androidx.compose.samples.crane.details.launchDetailsActivity
import androidx.compose.samples.crane.ui.CraneTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the application. The AndroidEntryPoint annotation Marks an Android component
 * class to be setup for injection with the standard Hilt Dagger Android components. This will
 * generate a base class that the annotated class should extend, either directly or via the Hilt
 * Gradle Plugin (as we do). This base class will take care of injecting members into the Android
 * class as well as handling instantiating the proper Hilt components at the right point in the
 * lifecycle. The name of the base class will be "Hilt_MainActivity.java".
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge-to-edge
     * display with the `statusBarStyle` argument a [SystemBarStyle.dark] whose `scrim` is
     * [Color.TRANSPARENT] (This lets the color of our app show through the system bar), then we
     * call our super's implementation of `onCreate`. We call [setContent] to compose its `content`
     * lambda into the activity. We wrap it all in our [CraneTheme] custom [MaterialTheme] to have
     * it supply values for the material widgets to use as their defaults. We initialize our
     * [WindowWidthSizeClass] variable `val widthSizeClass` to the the [WindowWidthSizeClass] of our
     * window's [WindowSizeClass]. We initialize and remember our [NavHostController] variable
     * `val navController` to a new instance. Then we compose a [NavHost] whose `navController`
     * argument is `navController`, and whose `startDestination` argument is `Routes.Home.route`.
     * The `builder` lambda argument consists of:
     *  - A [NavGraphBuilder.composable] whose `route` argument is `Routes.Home.route`, and in whose
     *  `content` lambda argument we initialize our [MainViewModel] variable `val mainViewModel` by
     *  the [hiltViewModel] method (Returns an existing HiltViewModel annotated [MainViewModel] or
     *  creates a new one scoped to the current navigation graph present on the [NavHostController]
     *  back stack. Then we compose a [MainScreen] Composable with its `widthSize` our
     *  [WindowWidthSizeClass] variable `widthSize`, its `onExploreItemClicked` lambda argument a
     *  lambda that calls the [launchDetailsActivity] with the `context` of this [MainActivity] and
     *  the [ExploreModel] that the lambda is called with, the `onDateSelectionClicked` lambda argument
     *  is a lambda that calls the [NavHostController.navigate] method of our [NavHostController]
     *  variable `navController` to navigate to the `route` `Routes.Calendar.route`, and its
     *  `mainViewModel` argument is our [MainViewModel] variable `mainViewModel`.
     *  - A [NavGraphBuilder.composable] whose `route` argument is `Routes.Calendar.route`, and in
     *  whose `content` lambda argument we initialize and remember our [NavBackStackEntry] variable
     *  `val parentEntry` to the topmost [NavBackStackEntry] for the route `Routes.Home.route`,
     *  then we initialize our [MainViewModel] variable `val parentViewModel` to the [MainViewModel]
     *  returned for the [NavBackStackEntry] variable `parentEntry` by the [hiltViewModel] method.
     *  Finally we compose a [CalendarScreen] whose `onBackPressed` lambda argument is a lambda
     *  that calls the [NavController.popBackStack] method of our [NavHostController] variable
     *  `navController`, and whose `mainViewModel` argument is our [MainViewModel] variable
     *  `parentViewModel`.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use this.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT))
        super.onCreate(savedInstanceState)

        setContent {
            CraneTheme {
                val widthSizeClass: WindowWidthSizeClass =
                    calculateWindowSizeClass(activity = this).widthSizeClass

                val navController: NavHostController = rememberNavController()
                NavHost(navController = navController, startDestination = Routes.Home.route) {
                    composable(route = Routes.Home.route) { navBackStackEntry: NavBackStackEntry ->
                        val mainViewModel: MainViewModel = hiltViewModel(
                            viewModelStoreOwner = navBackStackEntry,
                            key = null
                        )
                        MainScreen(
                            widthSize = widthSizeClass,
                            onExploreItemClicked = {
                                launchDetailsActivity(context = this@MainActivity, item = it)
                            },
                            onDateSelectionClicked = {
                                navController.navigate(route = Routes.Calendar.route)
                            },
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(route = Routes.Calendar.route) { navBackStackEntry: NavBackStackEntry ->
                        val parentEntry: NavBackStackEntry = remember(key1 = navBackStackEntry) {
                            navController.getBackStackEntry(route = Routes.Home.route)
                        }
                        val parentViewModel: MainViewModel = hiltViewModel<MainViewModel>(
                            viewModelStoreOwner = parentEntry,
                            key = null
                        )
                        CalendarScreen(
                            onBackPressed = { navController.popBackStack() },
                            mainViewModel = parentViewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * This class is used for the routes of the [NavHost] composed in `onCreate`.
 *
 * @param route the [String] to use as the route
 */
sealed class Routes(val route: String) {
    /**
     * Route which causes [MainScreen] to be composed into the UI.
     */
    data object Home : Routes(route = "home")

    /**
     * Route which causes [CalendarScreen] to be composed into the UI.
     */
    data object Calendar : Routes(route = "calendar")
}

/**
 * This is the screen that is displayed for the route `Routes.Home.route` that is defined in the
 * `onCreate` override. Our root Composable is a [Surface] whose `modifier` argument is a
 * [Modifier.windowInsetsPadding] that adds padding for the navigation bar to the bottom of the
 * screen so that the content doesn't enter that space, and the `color` argument is the
 * [Colors.primary] of our custom [MaterialTheme.colors]. In the `content` of the surface we
 * initialize and remember our [MutableTransitionState] of [SplashState] variable
 * `val transitionState` to the [MutableState.value] of the [MutableState] wrapped [SplashState]
 * property [MainViewModel.shownSplash] of our [MainViewModel] parameter [mainViewModel]. We
 * initialize our [Transition] of [SplashState] variable `val transition` to the value returned
 * by the [updateTransition] method for [MutableTransitionState] wrapped [SplashState] varible
 * `transitionState` (Creates a [Transition] and puts it in the `currentState` of the provided
 * `transitionState`. Whenever the `targetState` of the `transitionState` changes, the [Transition]
 * will animate to the new target state). We initialize our animated [Float] variable
 * `val splashAlpha` using the [Transition.animateFloat] method of `transition` with its
 * `transitionSpec` a [tween] of 100 milisecond `durationMillis` with its `targetValueByState`
 * lambda argument returning 1f if the current state is [SplashState.Shown] and 0f otherwise. We
 * initialize our animated [Float] variable `val contentAlpha` using the [Transition.animateFloat]
 * method of `transition` with its `transitionSpec` a [tween] of 300 milisecond `durationMillis`
 * with its `targetValueByState` lambda argument returning 0f the current state is
 * [SplashState.Shown] and 1f otherwise. We initialize our animated [Dp] variable
 * `val contentTopPadding` using the [Transition.animateDp] method of `transition` with its
 * `transitionSpec` a [spring] whose `stiffness` is [StiffnessLow], with its `targetValueByState`
 * lambda argument returning 100.dp if the current state is [SplashState.Shown] and 0.dp otherwise.
 *
 * The Composable content of the [Surface] is a [Box] holding a [LandingScreen] and a [MainContent]
 * composable with which one is visible controlled by their [Modifier.alpha]. The arguments passed
 * to the [LandingScreen] are:
 *  - `modifier` is a [Modifier.alpha] whose `alpha` argument is our animated [Float] variable
 *  `splashAlpha`.
 *  - `onTimeout` is a lambda which sets the [MutableTransitionState.targetState] of our variable
 *  `transitionState` to [SplashState.Completed], and sets the [MutableState.value] of the
 *  [MutableState] wrapped [SplashState] field [MainViewModel.shownSplash] to [SplashState.Completed].
 *
 * The arguments passed to the [MainContent] are:
 *  - `modifier` is a [Modifier.alpha] whose `alpha` argument is our animated [Float] variable
 *  `contentAlpha`
 *  - `topPadding` is our animated [Dp] variable `contentTopPadding`
 *  - `widthSize` is our [WindowWidthSizeClass] parameter [widthSize]
 *  - `onExploreItemClicked` is our [OnExploreItemClicked] parameter [onExploreItemClicked]
 *  - `onDateSelectionClicked` is our lambda parameter [onDateSelectionClicked]
 *  - `viewModel` is our [MainViewModel] parameter [mainViewModel]
 *
 * @param widthSize the [WindowWidthSizeClass] of the device we are running on, one of
 * [WindowWidthSizeClass.Compact], [WindowWidthSizeClass.Medium], or [WindowWidthSizeClass.Expanded]
 * @param onExploreItemClicked the [OnExploreItemClicked] lambda reference which should be called
 * with the [ExploreModel] for the city that the user has clicked. In our case it is a lambda that
 * calls the [launchDetailsActivity] method to launch the [DetailsActivity] to display a map that
 * corresponds to the [ExploreModel] it is called with.
 * @param onDateSelectionClicked a lambda that should be called when the user indicates that they
 * wish to select dates. It traces back to the `onCreate` override to be a lambda that calls the
 * [NavController.navigate] method to navigate to the route `Routes.Calendar.route` which displays
 * the [CalendarScreen] Composable.
 * @param mainViewModel the [MainViewModel] for the app. It is injected using [hiltViewModel] in
 * the `onCreate` override.
 */
@VisibleForTesting
@Composable
fun MainScreen(
    widthSize: WindowWidthSizeClass,
    onExploreItemClicked: OnExploreItemClicked,
    onDateSelectionClicked: () -> Unit,
    mainViewModel: MainViewModel
) {
    Surface(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.navigationBars.only(sides = WindowInsetsSides.Start + WindowInsetsSides.End)
        ),
        color = MaterialTheme.colors.primary
    ) {
        val transitionState: MutableTransitionState<SplashState> = remember {
            MutableTransitionState(initialState = mainViewModel.shownSplash.value)
        }
        val transition: Transition<SplashState> =
            rememberTransition(transitionState = transitionState, label = "splashTransition")
        val splashAlpha: Float by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 100) }, label = "splashAlpha"
        ) {
            if (it == SplashState.Shown) 1f else 0f
        }
        val contentAlpha: Float by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 300) }, label = "contentAlpha"
        ) {
            if (it == SplashState.Shown) 0f else 1f
        }
        val contentTopPadding: Dp by transition.animateDp(
            transitionSpec = { spring(stiffness = StiffnessLow) }, label = "contentTopPadding"
        ) {
            if (it == SplashState.Shown) 100.dp else 0.dp
        }

        Box {
            LandingScreen(
                modifier = Modifier.alpha(alpha = splashAlpha),
                onTimeout = {
                    transitionState.targetState = SplashState.Completed
                    mainViewModel.shownSplash.value = SplashState.Completed
                }
            )

            MainContent(
                modifier = Modifier.alpha(alpha = contentAlpha),
                topPadding = contentTopPadding,
                widthSize = widthSize,
                onExploreItemClicked = onExploreItemClicked,
                onDateSelectionClicked = onDateSelectionClicked,
                viewModel = mainViewModel
            )
        }
    }
}

/**
 * This is essentially just a convenience Composable that uses a [Column] to hold a [Spacer] whose
 * `modifier` argument uses the animated [Dp] parameter [topPadding] as the [Modifier.padding] for
 * the `top` of the [Spacer] (animating from 100.dp down to 0.dp), and holds the application's
 * [CraneHome] main Composable, which slides up in the window as the [Spacer] shrinks. Our root
 * Composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier] (in our
 * case this is a [Modifier.alpha] which animates the [Column] from invisible to visible). The
 * `content` of the [Column] is a [Spacer] whose `modifier` argument is a [Modifier.padding] that
 * uses our animated [Dp] parameter [topPadding] to animate the `top` padding of the [Spacer] from
 * 100.dp down to 0.dp. Below the shrinking [Spacer] is a [CraneHome] whose `widthSize` argument is
 * our [WindowWidthSizeClass] parameter [widthSize], whose `modifier` argument is our [Modifier]
 * parameter [modifier], whose `onExploreItemClicked` argument is our [OnExploreItemClicked]
 * parameter [onExploreItemClicked], whose `onDateSelectionClicked` arugment is our
 * [onDateSelectionClicked], and whose `viewModel` argument is our [MainViewModel] parameter
 * [viewModel].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [MainScreen] passes us a [Modifier.alpha] whose `alpha` argument is an
 * animated [Float] that goes from 0f to 1f causing us to go from invisible to visible.
 * @param topPadding used as the [Modifier.padding] for the `top` of the [Spacer] that is at the top
 * or our [Column]. Our caller [MainScreen] passes us an animated [Dp] that animates from 100.dp
 * down to 0.dp
 * @param widthSize the [WindowWidthSizeClass] of the device we are running on. One of
 * [WindowWidthSizeClass.Compact], [WindowWidthSizeClass.Medium], or [WindowWidthSizeClass.Expanded].
 * @param onExploreItemClicked a [OnExploreItemClicked] lambda that should be called with the
 * [ExploreModel] of a city that the user has clicked. It traces up the hierarchy to the `onCreate`
 * override where it is defined as a lambda that calls the [launchDetailsActivity] method with the
 * [ExploreModel] that [onExploreItemClicked] is called with.
 * @param onDateSelectionClicked a lambda that should be called when the user indicates that they
 * wish to select dates. It traces back to the `onCreate` override to be a lambda that calls the
 * [NavController.navigate] method to navigate to the route `Routes.Calendar.route` which displays
 * the [CalendarScreen] Composable.
 * @param viewModel the app's singleton [MainViewModel]. It is injected by [hiltViewModel] up in the
 * `onCreate` override.
 */
@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    widthSize: WindowWidthSizeClass,
    onExploreItemClicked: OnExploreItemClicked,
    onDateSelectionClicked: () -> Unit,
    viewModel: MainViewModel
) {

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.padding(top = topPadding))
        CraneHome(
            widthSize = widthSize,
            modifier = modifier,
            onExploreItemClicked = onExploreItemClicked,
            onDateSelectionClicked = onDateSelectionClicked,
            viewModel = viewModel
        )
    }
}

/**
 * Used for the state of the [LandingScreen] splash screen.
 */
enum class SplashState {
    /**
     * The [LandingScreen] should be visible.
     */
    Shown,

    /**
     * The [LandingScreen] should be invisible.
     */
    Completed
}
