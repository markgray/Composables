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

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import com.google.samples.apps.nowinandroid.core.data.repository.CompositeUserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import com.google.samples.apps.nowinandroid.core.testing.repository.TestNewsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.util.TestNetworkMonitor
import com.google.samples.apps.nowinandroid.core.testing.util.TestTimeZoneMonitor
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests [NiaAppState].
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
class NiaAppStateTest {

    /**
     * The [ComposeContentTestRule] test rule.
     */
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    // Create the test dependencies.

    /**
     * The [NetworkMonitor] used for testing.
     */
    private val networkMonitor = TestNetworkMonitor()

    /**
     * The [TimeZoneMonitor] used for testing.
     */
    private val timeZoneMonitor = TestTimeZoneMonitor()

    /**
     * The [UserNewsResourceRepository] used for testing.
     */
    private val userNewsResourceRepository =
        CompositeUserNewsResourceRepository(TestNewsRepository(), TestUserDataRepository())

    /**
     * Subject under test.
     */
    private lateinit var state: NiaAppState

    /**
     * Test that [NiaAppState.currentDestination] reflects the current destination of the
     * [NavHostController].
     *
     * @return [TestResult] from [runTest].
     */
    @Test
    fun niaAppState_currentDestination(): TestResult = runTest {
        var currentDestination: String? = null

        composeTestRule.setContent {
            val navController: TestNavHostController = rememberTestNavController()
            state = remember(key1 = navController) {
                NiaAppState(
                    navController = navController,
                    coroutineScope = backgroundScope,
                    networkMonitor = networkMonitor,
                    userNewsResourceRepository = userNewsResourceRepository,
                    timeZoneMonitor = timeZoneMonitor,
                )
            }

            // Update currentDestination whenever it changes
            currentDestination = state.currentDestination?.route

            // Navigate to destination b once
            LaunchedEffect(Unit) {
                navController.setCurrentDestination("b")
            }
        }

        assertEquals("b", currentDestination)
    }

    /**
     * Test that the top level destinations are correct.
     *
     * @return [TestResult] from [runTest].
     */
    @Test
    fun niaAppState_destinations(): TestResult = runTest {
        composeTestRule.setContent {
            state = rememberNiaAppState(
                networkMonitor = networkMonitor,
                userNewsResourceRepository = userNewsResourceRepository,
                timeZoneMonitor = timeZoneMonitor,
            )
        }

        assertEquals(3, state.topLevelDestinations.size)
        assertTrue(state.topLevelDestinations[0].name.contains("for_you", true))
        assertTrue(state.topLevelDestinations[1].name.contains("bookmarks", true))
        assertTrue(state.topLevelDestinations[2].name.contains("interests", true))
    }

    /**
     * Test that [NiaAppState.isOffline] is true when the [NetworkMonitor] is offline, and that
     * the [NiaAppState.isOffline] flow emits a new value when the network monitor changes.
     *
     * @return [TestResult] from [runTest].
     */
    @Test
    fun niaAppState_whenNetworkMonitorIsOffline_StateIsOffline(): TestResult =
        runTest(UnconfinedTestDispatcher()) {
            composeTestRule.setContent {
                state = NiaAppState(
                    navController = NavHostController(LocalContext.current),
                    coroutineScope = backgroundScope,
                    networkMonitor = networkMonitor,
                    userNewsResourceRepository = userNewsResourceRepository,
                    timeZoneMonitor = timeZoneMonitor,
                )
            }

            backgroundScope.launch { state.isOffline.collect() }
            networkMonitor.setConnected(false)
            assertEquals(
                true,
                state.isOffline.value,
            )
        }

    /**
     * Test that [NiaAppState.currentTimeZone] is the [TimeZone.currentSystemDefault] initially, and
     * that the [NiaAppState.currentTimeZone] flow emits a new value when the time zone monitor
     * changes.
     */
    @Test
    fun niaAppState_differentTZ_withTimeZoneMonitorChange(): TestResult =
        runTest(UnconfinedTestDispatcher()) {
            composeTestRule.setContent {
                state = NiaAppState(
                    navController = NavHostController(LocalContext.current),
                    coroutineScope = backgroundScope,
                    networkMonitor = networkMonitor,
                    userNewsResourceRepository = userNewsResourceRepository,
                    timeZoneMonitor = timeZoneMonitor,
                )
            }
            val changedTz: TimeZone = TimeZone.of("Europe/Prague")
            backgroundScope.launch { state.currentTimeZone.collect() }
            timeZoneMonitor.setTimeZone(zoneId = changedTz)
            assertEquals(
                changedTz,
                state.currentTimeZone.value,
            )
        }
}

/**
 * Remembers a [TestNavHostController] for testing purposes.
 *
 * This function creates and remembers a [TestNavHostController] with a predefined graph.
 * The graph includes three composable destinations: "a", "b", and "c".
 * The [ComposeNavigator] is added to the navigator provider.
 * The start destination of the graph is set to "a".
 *
 * @return A [TestNavHostController] instance.
 */
@Composable
private fun rememberTestNavController(): TestNavHostController {
    val context: Context = LocalContext.current
    return remember {
        TestNavHostController(context = context).apply {
            navigatorProvider.addNavigator(navigator = ComposeNavigator())
            graph = createGraph(startDestination = "a") {
                composable(route = "a") { }
                composable(route = "b") { }
                composable(route = "c") { }
            }
        }
    }
}
