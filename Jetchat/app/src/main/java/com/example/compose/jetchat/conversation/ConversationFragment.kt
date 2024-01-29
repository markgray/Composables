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

package com.example.compose.jetchat.conversation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.NavActivity
import com.example.compose.jetchat.profile.ProfileFragment
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.profile.ProfileScreenState
import com.example.compose.jetchat.theme.JetchatTheme
import kotlinx.coroutines.flow.StateFlow

/**
 * This is the `app:startDestination` of our navigation graph, its ID is [R.id.nav_home].
 */
class ConversationFragment : Fragment() {

    /**
     * The [MainViewModel] used by our activity.
     */
    private val activityViewModel: MainViewModel by activityViewModels()

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onViewCreated]. It is recommended to only inflate the layout in this method
     * and move logic that operates on the returned [View] to [onViewCreated]. We return a [ComposeView]
     * whose `context` argument is the [Context] returned by [LayoutInflater.getContext] (kotlin
     * `context` property) to which we [apply] a lambda block which calls its [View.setLayoutParams]
     * method (kotlin `layoutParams`) to set the layout parameters of both `width` and `height` to
     * [MATCH_PARENT]. Then we call its [ComposeView.setContent] method to Set the Jetpack Compose
     * UI content for the view to our [JetchatTheme] custom [MaterialTheme] wrapped [ConversationContent]
     * Composable whose `uiState` argument is the [exampleUiState] global [ConversationUiState], whose
     * `navigateToProfile` argument is a lambda which uses the variable name `user` to refer to the
     * [String] passed the lambda and creates a [Bundle] variable `val bundle` which has that `user`
     * [String] stored under the key "userId" and then calls the [NavController.navigate] method of
     * the [NavController] returned by [findNavController] with the `resId` argument [R.id.nav_profile]
     * (the resource ID of the [ProfileFragment]) and the `args` argument `bundle` ([ProfileFragment]
     * uses the [Bundle] as the to locate the [ProfileScreenState] for the user whose name is the
     * [String] stored in the [Bundle] under the key "userId"). The `onNavIconPressed` argument of
     * [ConversationContent] is a lambda which calls the [MainViewModel.openDrawer] method of our
     * [MainViewModel] field [activityViewModel] which changes the value read from its [StateFlow]
     * of [Boolean] field [MainViewModel.drawerShouldBeOpened] to `true` which causes a [LaunchedEffect]
     * in the [ComposeView] used in the `onCreate` override of [NavActivity] to call the [DrawerState.open]
     * method of the [DrawerState] used by its [JetchatDrawer] thereby opening the drawer.
     *
     * @param inflater – The [LayoutInflater] object that can be used to inflate any views in the
     * fragment.
     * @param container – If non-`null`, this is the parent view that the fragment's UI will be
     * attached to. The fragment should not add the view itself, but this can be used to generate
     * the LayoutParams of the view.
     * @param savedInstanceState – If non-`null`, this fragment is being re-constructed from a
     * previous saved state as given here.
     * @return the [View] for the fragment's UI, or `null`.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(context = inflater.context).apply {
        layoutParams = LayoutParams(/* width = */ MATCH_PARENT, /* height = */ MATCH_PARENT)

        setContent {
            JetchatTheme {
                ConversationContent(
                    uiState = exampleUiState,
                    navigateToProfile = { user: String ->
                        // Click callback
                        val bundle: Bundle = bundleOf("userId" to user)
                        findNavController().navigate(
                            resId = R.id.nav_profile,
                            args = bundle
                        )
                    },
                    onNavIconPressed = {
                        activityViewModel.openDrawer()
                    }
                )
            }
        }
    }
}
