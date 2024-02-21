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

package com.example.compose.jetchat.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.NavActivity
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.components.ProfileItem
import com.example.compose.jetchat.theme.JetchatTheme

/**
 * This [Fragment] is navigated to when the user clicks one of the [ProfileItem]'s in the list of
 * "Recent Profiles" in the [JetchatDrawer] used by [NavActivity].
 */
class ProfileFragment : Fragment() {

    /**
     * The [ProfileViewModel] we use.
     */
    private val viewModel: ProfileViewModel by viewModels()

    /**
     * The [MainViewModel] used by the whole app.
     */
    private val activityViewModel: MainViewModel by activityViewModels()

    /**
     * Called when a fragment is first attached to its context. [onCreate] will be called after this.
     * First we call our supers implementation of `onAttach`. Then if [getArguments] returns a
     * non-`null` [Bundle] we call its [Bundle.getString] method to retrieve the [String] stored
     * under the key "userId" to initialize our [String] variable `val userId`, and call the
     * [ProfileViewModel.setUserId] method of our [ProfileViewModel] field [viewModel] with `userId`
     * to have it load the private field backing its public [ProfileScreenState] property
     * [ProfileViewModel.userData] with the [ProfileScreenState] whose [ProfileScreenState.userId]
     * field is equal to our `userId` variable.
     *
     * @param context the [Context] of the [Fragment], which we do not use.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Consider using safe args plugin
        val userId: String? = arguments?.getString("userId")
        viewModel.setUserId(newUserId = userId)
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onViewCreated]. It is recommended to only inflate the layout in this method
     * and move logic that operates on the returned View to [onViewCreated]. We start by using our
     * [LayoutInflater] parameter [inflater] to inflate our layout file [R.layout.fragment_profile]
     * using our [ViewGroup] parameter [container] for LayoutParams without attaching to it and
     * saving the [View] returned in our variable `val rootView`. Then we find the [ComposeView]
     * in `rootView` with ID [R.id.toolbar_compose_view] and use the [apply] extension function to
     * call its [ComposeView.setContent] function to set the Jetpack Compose UI content of the [View].
     * In the `content` lambda of the call we start by initializing and remembering our [MutableState]
     * wrapped [Boolean] variable `var functionalityNotAvailablePopupShown`to `false`. Next if
     * `functionalityNotAvailablePopupShown` is `true` we compose our Composable [FunctionalityNotAvailablePopup]
     * with its `onDismiss` argument a lambda which sets `functionalityNotAvailablePopupShown` to `false`
     * again ([FunctionalityNotAvailablePopup] is an [AlertDialog] that displays the [Text] "Functionality
     * not available" and calls its `onDismiss` lambda argument when its "CLOSE" [TextButton] is clicked).
     * Then wrapped in our [JetchatTheme] custom [MaterialTheme] we compose [JetchatAppBar] into the UI
     * with its `modifier` argument a [Modifier.wrapContentSize] to allow it to measure at its desired
     * size without regard for the incoming measurement minimum width or minimum height constraints.
     * Its `onNavIconPressed` lambda argument is a lambda which calls the [MainViewModel.openDrawer]
     * method of our [MainViewModel] field [activityViewModel]. Its `title` argument is a do-nothing
     * lambda. Its `actions` argument is a lambda that contains an [Icon] whose `imageVector` is the
     * [ImageVector] drawn by [Icons.Outlined.MoreVert], its `tint` [Color] argument is the
     * [ColorScheme.onSurfaceVariant] of our custom [MaterialTheme.colorScheme], its `modifier`
     * argument is a [Modifier.clickable] whose `onClick` lambda argument sets our [MutableState]
     * wrapped [Boolean] variable `functionalityNotAvailablePopupShown`to `true` causing our
     * [FunctionalityNotAvailablePopup] to "pop-up" advising the user "Functionality not available",
     * chained to the [Modifier.clickable] is a [Modifier.padding] that sets the `horizontal` padding
     * at each side to 12.dp and the `vertical` padding at top and bottom to 16.dp, and the last in
     * the chain is a [Modifier.height] that sets its height to 24.dp. The `contentDescription` argument
     * of the [Icon] is the [String] with resource ID [R.string.more_options] ("More options").
     *
     * Next we find the [ComposeView] in `rootView` with ID [R.id.profile_compose_view] and use the
     * [apply] extension function to call its [ComposeView.setContent] function to set the Jetpack
     * Compose UI content of the [View]. In the `content` lambda of the call we start by initializing
     * our [State] wrapped [ProfileScreenState] variable `val userData` using the [LiveData.observeAsState]
     * method of the [ProfileViewModel.userData] field of our [ProfileViewModel] field [viewModel].
     * Then we use the [rememberNestedScrollInteropConnection] method to create and remember the
     * [NestedScrollConnection] that we use to initialize our variable `val nestedScrollInteropConnection`
     * (enables Nested Scroll Interop between a View parent that implements `NestedScrollingParent3`
     * and a Compose child).
     *
     * Then wrapped in our [JetchatTheme] custom [MaterialTheme] we compose [ProfileError] into the UI
     * if `userData` is equal to `null`, of it is not `null` we compose [ProfileScreen] into the UI
     * with its `userData` argument our [State] wrapped [ProfileScreenState] variable `userData`, and
     * its `nestedScrollInteropConnection` argument our [NestedScrollConnection] variable
     * `nestedScrollInteropConnection`.
     *
     * Finally we return `rootView` to our caller to have it used as our UI.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate any views
     * @param container If non-`null`, this is the parent view that the fragment's UI will be
     * attached to. The fragment should not add the view itself, but this can be used to generate
     * the LayoutParams of the view.
     * @param savedInstanceState If non-`null`, this fragment is being re-constructed from a
     * previous saved state as given here.
     * @return the [View] for the fragment's UI.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {
            setContent {
                var functionalityNotAvailablePopupShown: Boolean by remember { mutableStateOf(false) }
                if (functionalityNotAvailablePopupShown) {
                    FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
                }

                JetchatTheme {
                    JetchatAppBar(
                        // Reset the minimum bounds that are passed to the root of a compose tree
                        modifier = Modifier.wrapContentSize(),
                        onNavIconPressed = { activityViewModel.openDrawer() },
                        title = { },
                        actions = {
                            // More icon
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        functionalityNotAvailablePopupShown = true
                                    })
                                    .padding(horizontal = 12.dp, vertical = 16.dp)
                                    .height(24.dp),
                                contentDescription = stringResource(id = R.string.more_options)
                            )
                        }
                    )
                }
            }
        }

        rootView.findViewById<ComposeView>(R.id.profile_compose_view).apply {
            setContent {
                val userData: ProfileScreenState? by viewModel.userData.observeAsState()
                val nestedScrollInteropConnection = rememberNestedScrollInteropConnection()

                JetchatTheme {
                    if (userData == null) {
                        ProfileError()
                    } else {
                        @Suppress("ReplaceNotNullAssertionWithElvisReturn") // The if statement catches `null` value.
                        ProfileScreen(
                            userData = userData!!,
                            nestedScrollInteropConnection = nestedScrollInteropConnection
                        )
                    }
                }
            }
        }
        return rootView
    }
}
