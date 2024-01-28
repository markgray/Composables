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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.R
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme

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
     * and move logic that operates on the returned [View] to [onViewCreated].
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
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)

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
