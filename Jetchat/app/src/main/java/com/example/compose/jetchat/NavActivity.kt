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

package com.example.compose.jetchat

import android.os.Bundle
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.conversation.ConversationFragment
import com.example.compose.jetchat.profile.ProfileFragment
import com.example.compose.jetchat.databinding.ContentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Main activity for the app.
 */
class NavActivity : AppCompatActivity() {
    /**
     * The [MainViewModel] that we use to control the open/close state of our [JetchatDrawer]
     */
    private val viewModel: MainViewModel by viewModels()

    /**
     * Called when the activity is starting. First we call the [enableEdgeToEdge] method to enable
     * edge-to-edge display for our app, then we call our super's implementation of `onCreate`. We
     * call the [setContentView] method to set our activities content to [ComposeView] that uses
     * the [apply] extension method to have it act as receiver for calls to set its property
     * [AbstractComposeView.consumeWindowInsets] to `false` to disable its `content` consuming the
     * [android.view.WindowInsets]. Next we call its [ComposeView.setContent] method to set the
     * Jetpack Compose UI content for this [ComposeView]. In the `content` lambda argument of the
     * call to [ComposeView.setContent] we start by initializing and remembering our [DrawerState]
     * variable `val drawerState` with an `initialValue` of [Closed], then we use our [State] wrapped
     * [Boolean] variable `val drawerOpen` by using the [StateFlow.collectAsStateWithLifecycle] method
     * of the [MainViewModel.drawerShouldBeOpened] field of our [MainViewModel] field [viewModel].
     * Then if `drawerOpen` is `true` we compose a [LaunchedEffect] with a `try` that calls the
     * [DrawerState.open] method of `drawerState` with a `finally` block that calls the method
     * [MainViewModel.resetOpenDrawerAction] of [viewModel] to set the [MutableStateFlow] wrapped
     * field that backs the [MainViewModel.drawerShouldBeOpened] field to `false` again, thereby
     * setting `drawerOpen` to `false` once that change propagates through the plumbing. Next we
     * initialize and remember our [CoroutineScope] variable `val scope`, then is the [DrawerState.isOpen]
     * property of `drawerState` returns `true` indicating that the [JetchatDrawer] is open we call
     * the [BackHandler] Composable to add its `onBack` lambda argument to the [OnBackPressedDispatcher]
     * and in that lambda we call the [CoroutineScope.launch] method of `scope` to launch a coroutine
     * that calls the [DrawerState.close] method of `drawerState` thereby closing the [JetchatDrawer].
     * After this we compose a [JetchatDrawer] into the UI whose `drawerState` argument is our
     * [DrawerState] variable `drawerState`, whose `onChatClicked` argument is a lambda that calls
     * our [findNavController] method and calls the [NavController.popBackStack] of the [NavController]
     * returned to the controller's back stack back to the destination with resource ID [R.id.nav_home]
     * (the [ConversationFragment]) and then it calls the [CoroutineScope.launch] method of `scope`
     * to launch a coroutine that calls the [DrawerState.close] method of `drawerState` thereby closing
     * the [JetchatDrawer]. Its `onProfileClicked` argument is a lambda that constructs a [Bundle]
     * variable `val bundle` that stores the `useId` [String] passed the lambda under the key "userId"
     * then calls our [findNavController] method and calls the [NavController.navigate] method of the
     * [NavController] returned to navigate to the destination with resource ID [R.id.nav_profile]
     * (the [ProfileFragment]) using `bundle` as the arguments to pass to the destination, and then
     * it calls the [CoroutineScope.launch] method of `scope` to launch a coroutine that calls the
     * [DrawerState.close] method of `drawerState` thereby closing the [JetchatDrawer]. The `content`
     * lambda argument of the [JetchatDrawer] is an [AndroidViewBinding] whose `factory` argument is
     * the [ContentMainBinding.inflate] method (which inflates the xml file with resource ID
     * [R.layout.content_main] as the content of the [AndroidViewBinding]).
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContentView(
            ComposeView(context = this).apply {
                consumeWindowInsets = false
                setContent {
                    val drawerState: DrawerState = rememberDrawerState(initialValue = Closed)
                    val drawerOpen: Boolean by viewModel.drawerShouldBeOpened
                        .collectAsStateWithLifecycle()

                    if (drawerOpen) {
                        // Open drawer and reset state in VM.
                        LaunchedEffect(Unit) {
                            // wrap in try-finally to handle interruption whiles opening drawer
                            try {
                                drawerState.open()
                            } finally {
                                viewModel.resetOpenDrawerAction()
                            }
                        }
                    }

                    // Intercepts back navigation when the drawer is open
                    val scope: CoroutineScope = rememberCoroutineScope()
                    if (drawerState.isOpen) {
                        BackHandler {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    }

                    JetchatDrawer(
                        drawerState = drawerState,
                        onChatClicked = {
                            findNavController().popBackStack(R.id.nav_home, false)
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        onProfileClicked = { useId: String ->
                            val bundle: Bundle = bundleOf("userId" to useId)
                            findNavController().navigate(R.id.nav_profile, bundle)
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    ) {
                        AndroidViewBinding(factory = ContentMainBinding::inflate)
                    }
                }
            }
        )
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar. We call our [findNavController] method and call the
     * [NavController.navigateUp] method of the [NavController] returned to attempt to navigate up
     * in the navigation hierarchy, and if that returns `false` indicating the the navigation was
     * unsuccessful we call our super's implementation of `onSupportNavigateUp`. In either case we
     * return the short circuiting inclusive or of their return values to our caller.
     *
     * @return `true` if Up navigation completed successfully and this Activity was finished,
     * `false` otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Hack for bug [findNavController() fails in onCreate](https://issuetracker.google.com/142847973)
     * We use the [FragmentManager.findFragmentById] method to find the [NavHostFragment] with ID
     * [R.id.nav_host_fragment] to initialize our [NavHostFragment] variable `val navHostFragment`
     * then return the [NavController] returned by its [NavHostFragment.navController] property to
     * the caller.
     *
     * @return the [NavController] of the [NavHostFragment] found in the inflated layout file with
     * resource ID [R.layout.content_main].
     */
    private fun findNavController(): NavController {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}
