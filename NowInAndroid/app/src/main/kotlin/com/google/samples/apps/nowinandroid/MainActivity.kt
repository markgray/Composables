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

package com.google.samples.apps.nowinandroid

import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreen.KeepOnScreenCondition
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import androidx.tracing.trace
import com.google.samples.apps.nowinandroid.MainActivityUiState.Loading
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.analytics.LocalAnalyticsHelper
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.ui.LocalTimeZone
import com.google.samples.apps.nowinandroid.ui.NiaApp
import com.google.samples.apps.nowinandroid.ui.NiaAppState
import com.google.samples.apps.nowinandroid.ui.rememberNiaAppState
import com.google.samples.apps.nowinandroid.util.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import javax.inject.Inject

/**
 * Main activity for the Now in Android app.
 *
 * This activity serves as the entry point and hosts the main UI of the application.
 * It handles the initialization of essential components like JankStats for performance
 * monitoring, network monitoring, and theme settings.
 *
 * The activity utilizes Jetpack Compose for building the UI and leverages Hilt for
 * dependency injection.
 *
 * It also manages the splash screen, keeping it visible until the UI state is fully loaded.
 * The theme of the app (dark/light, dynamic theming) is dynamically updated based on
 * user preferences and system settings.
 *
 * Edge-to-edge display is enabled to provide a more immersive user experience.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Lazily inject [JankStats], which is used to track jank throughout the app.
     */
    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>

    /**
     * Inject [NetworkMonitor] which is used to detect if the user is online or not.
     */
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    /**
     * Inject [TimeZoneMonitor] which is used to track the user's time zone.
     */
    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    /**
     * Inject [AnalyticsHelper] which is used to log app events.
     */
    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    /**
     * Inject [UserNewsResourceRepository] which is used to track the user's news resources.
     */
    @Inject
    lateinit var userNewsResourceRepository: UserNewsResourceRepository

    /**
     * The [ViewModel] that is used to store the UI state in a [StateFlow] of [MainActivityUiState]
     */
    private val viewModel: MainActivityViewModel by viewModels()

    /**
     * Called when the activity is first created.
     *
     * First we install a splash screen using [installSplashScreen], saving a reference to it in
     * [SplashScreen] variable `splashScreen`. Then we call our super's implementation of `onCreate`.
     * We initialize our [MutableState] wrapped [ThemeSettings] variable `var themeSettings` to a
     * new instance of [ThemeSettings] with the `darkTheme` argument the result of calling the
     * [ComponentActivity.isSystemInDarkTheme] method, and the `androidTheme` argument to the
     * result of calling the [Loading.shouldUseAndroidTheme] method, and the `disableDynamicTheming`
     * argument to the result of calling the [Loading.shouldDisableDynamicTheming] method.
     *
     * We use the [CoroutineScope] of the [LifecycleOwner.lifecycleScope] property and call its
     * [CoroutineScope.launch] method to launch a coroutine in whose [CoroutineScope] `block` lambda
     * argument we use the [Lifecycle.repeatOnLifecycle] function with [Lifecycle.State.STARTED] as
     * its `state` argument (its [CoroutineScope] `block` lambda will be executed whenever the
     * [Lifecycle] is at least in the [Lifecycle.State.STARTED] state), and in that lambda we use
     * the [combine] function to combine the [Flow] of [Boolean] returned by the [isSystemInDarkTheme]
     * method and the [StateFlow] of [MainActivityUiState] returned by the
     * [MainActivityViewModel.uiState] property of [MainActivityViewModel] property [viewModel]
     * accepting the [Boolean] in variable `systemDark` and the [MainActivityUiState] in variable
     * `uiState` respectively. We then construct a new instance of [ThemeSettings] with the `darkTheme`
     * argument the result of calling the [MainActivityUiState.shouldUseDarkTheme] method of `uiState`
     * with `systemDark` as the argument, the `androidTheme` argument to the result of calling the
     * [MainActivityUiState.shouldUseAndroidTheme] method of `uiState`, and the `disableDynamicTheming`
     * argument to the result of calling the [MainActivityUiState.shouldDisableDynamicTheming] method
     * of `uiState` producing a [Flow] of [ThemeSettings] that we chain to a [Flow.onEach] method
     * that sets `themeSettings` to the value of the [Flow] it receives, chained to a [Flow.map] method
     * that emits the [Boolean] value of the [ThemeSettings.darkTheme] property of the [ThemeSettings],
     * chained to a [Flow.distinctUntilChanged] method that emits only changed values, chained to a
     * [Flow.collect] method that collects the [Flow] in [Boolean] variable `darkTheme`. In the
     * [FlowCollector] lambda we wrap a [trace] whose label is "niaEdgeToEdge" and call the
     * [enableEdgeToEdge] method with the `statusBarStyle` argument the result of calling the
     * [SystemBarStyle.auto] method with the `lightScrim` argument [Color.TRANSPARENT], the `darkScrim`
     * argument [Color.TRANSPARENT], and the `darkTheme` argument `darkTheme`. The `navigationBarStyle`
     * argument is [SystemBarStyle.auto] with the `lightScrim` argument [lightScrim], the `darkScrim`
     * argument [darkScrim], and the `detectDarkMode` argument `darkTheme`.
     *
     * Next we call the [SplashScreen.setKeepOnScreenCondition] method of `splashScreen` with the
     * [KeepOnScreenCondition] `condition` argument the result of calling the
     * [MainActivityUiState.shouldKeepSplashScreen] method of [MainActivityViewModel.uiState].
     *
     * Finally we call the [ComponentActivity.setContent] method and in its `content` Composable
     * lambda argument we:
     *
     * **First** call the [rememberNiaAppState] method to remember and initialize our [NiaAppState]
     * variable `val appState` with the [NetworkMonitor] property [networkMonitor], the
     * [UserNewsResourceRepository] property [userNewsResourceRepository], and the
     * [TimeZoneMonitor] property [timeZoneMonitor].
     *
     * **Second** we initialize our [State] wrapped [TimeZone] variable `val currentTimeZone` to the
     * value that the [collectAsStateWithLifecycle] method returns for the [StateFlow] of [TimeZone]
     * of the [NiaAppState.currentTimeZone] property of `appState`.
     *
     * **Third** we call the [CompositionLocalProvider] method to provide [analyticsHelper] as the
     * [LocalAnalyticsHelper], and `currentTimeZone` as the [LocalTimeZone] to its `content`
     * Composable lambda argument in which we call the [NiaTheme] method with the `darkTheme` argument
     * the [ThemeSettings.darkTheme] property of `themeSettings`, the `androidTheme` argument the
     * [ThemeSettings.androidTheme] property of `themeSettings`, and the `disableDynamicTheming`
     * argument the [ThemeSettings.disableDynamicTheming] property of `themeSettings`. In the `content`
     * Composable lambda argument of [NiaTheme] we compose our [NiaApp] Composable with its `appState`
     * argument our [NiaAppState] variable `appState`.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in [onSaveInstanceState].
     * We do not override [onSaveInstanceState] so it is not used.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen: SplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // We keep this as a mutable state, so that we can track changes inside the composition.
        // This allows us to react to dark/light mode changes.
        var themeSettings: ThemeSettings by mutableStateOf(
            ThemeSettings(
                darkTheme = resources.configuration.isSystemInDarkTheme,
                androidTheme = Loading.shouldUseAndroidTheme,
                disableDynamicTheming = Loading.shouldDisableDynamicTheming,
            ),
        )

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    viewModel.uiState,
                ) { systemDark: Boolean, uiState: MainActivityUiState ->
                    ThemeSettings(
                        darkTheme = uiState.shouldUseDarkTheme(systemDark),
                        androidTheme = uiState.shouldUseAndroidTheme,
                        disableDynamicTheming = uiState.shouldDisableDynamicTheming,
                    )
                }
                    .onEach { themeSettings = it }
                    .map { it.darkTheme }
                    .distinctUntilChanged()
                    .collect { darkTheme: Boolean ->
                        trace("niaEdgeToEdge") {
                            // Turn off the decor fitting system windows, which allows us to handle insets,
                            // including IME animations, and go edge-to-edge.
                            // This is the same parameters as the default enableEdgeToEdge call, but we manually
                            // resolve whether or not to show dark theme using uiState, since it can be different
                            // than the configuration's dark theme value based on the user preference.
                            enableEdgeToEdge(
                                statusBarStyle = SystemBarStyle.auto(
                                    lightScrim = Color.TRANSPARENT,
                                    darkScrim = Color.TRANSPARENT,
                                ) { darkTheme },
                                navigationBarStyle = SystemBarStyle.auto(
                                    lightScrim = lightScrim,
                                    darkScrim = darkScrim,
                                ) { darkTheme },
                            )
                        }
                    }
            }
        }

        // Keep the splash screen on-screen until the UI state is loaded. This condition is
        // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
        // the UI.
        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        setContent {
            val appState: NiaAppState = rememberNiaAppState(
                networkMonitor = networkMonitor,
                userNewsResourceRepository = userNewsResourceRepository,
                timeZoneMonitor = timeZoneMonitor,
            )

            val currentTimeZone: TimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
                LocalTimeZone provides currentTimeZone,
            ) {
                NiaTheme(
                    darkTheme = themeSettings.darkTheme,
                    androidTheme = themeSettings.androidTheme,
                    disableDynamicTheming = themeSettings.disableDynamicTheming,
                ) {
                    NiaApp(appState = appState)
                }
            }
        }
    }

    /**
     * Called after [onRestoreInstanceState], [onRestart], or [onPause], for your activity to
     * start interacting with the user. This is a good place to begin animations, open exclusive
     * access devices (such as the camera), etc.
     *
     * First we call our super's implementation of `onResume`, then we set the
     * [JankStats.isTrackingEnabled] property of our [dagger.Lazy] wrapped [JankStats] field
     * [lazyStats] to `true` (it defaults to `false`).
     *
     * Keep in mind that [onResume] is not the best indicator that your activity is visible to the
     * user; a system window such as the keyguard may be in front. Use [onWindowFocusChanged]
     * to know for certain that your activity is visible to the user (for example, to resume a game).
     */
    override fun onResume() {
        super.onResume()
        lazyStats.get().isTrackingEnabled = true
    }

    /**
     * Called as part of the activity lifecycle when the activity is going into the background, but
     * has not (yet) been killed. The counterpart to [onResume].
     *
     * We call our super's implementation of `onPause`, then we set the [JankStats.isTrackingEnabled]
     * property of our [dagger.Lazy] wrapped [JankStats] field [lazyStats] to `false`.
     */
    override fun onPause() {
        super.onPause()
        lazyStats.get().isTrackingEnabled = false
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim: Int = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim: Int = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

/**
 * Class for the system theme settings.
 * This wrapping class allows us to combine all the changes and prevent unnecessary recompositions.
 */
data class ThemeSettings(
    val darkTheme: Boolean,
    val androidTheme: Boolean,
    val disableDynamicTheming: Boolean,
)
