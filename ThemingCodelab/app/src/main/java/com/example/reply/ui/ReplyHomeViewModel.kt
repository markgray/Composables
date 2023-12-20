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

package com.example.reply.ui

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.reply.data.Email
import com.example.reply.data.LocalEmailsDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * This is the [ViewModel] used by the app to hold state and receive events.
 */
class ReplyHomeViewModel : ViewModel() {

    /**
     * This is the private [MutableStateFlow] or [ReplyHomeUIState]. Its value is modified by our
     * methods [initEmailList], [setSelectedEmail], and [closeDetailScreen]. [uiState] provides
     * public read-only access to it, and its current value is collected and represented via [State]
     * in a lifecycle-aware manner in the `onCreate` override of [MainActivity].
     */
    private val _uiState = MutableStateFlow(ReplyHomeUIState(loading = true))

    /**
     * UI state exposed to the UI, public read-only access to [_uiState], its current value is
     * collected and represented via [State] in a lifecycle-aware manner in the `onCreate` override of
     * [MainActivity]. That value is passed to the [ReplyApp] Composable as its `replyHomeUIState`
     * argument, which is passed on to all Composables which need to be recomposed when the
     * [ReplyHomeUIState] changes.
     */
    val uiState: StateFlow<ReplyHomeUIState> = _uiState

    init {
        initEmailList()
    }

    /**
     * This method fetches the [List] of [Email] produced by the [LocalEmailsDataProvider.allEmails]
     * property, and modifies the value of [_uiState] to have its [ReplyHomeUIState.emails] property
     * point to it, and modifies [ReplyHomeUIState.selectedEmail] to point to the first [Email] in
     * that list.
     */
    private fun initEmailList() {
        val emails: List<Email> = LocalEmailsDataProvider.allEmails
        _uiState.value = ReplyHomeUIState(
            emails = emails,
            selectedEmail = emails.first()
        )
    }

    /**
     * Searches the [List] of [Email] in [ReplyHomeUIState.emails] for an [Email] whose [Email.id]
     * is equal to our [Long] parameter [emailId], then updates the current value of [_uiState] to
     * have its [ReplyHomeUIState.selectedEmail] point to it, as well as setting its
     * [ReplyHomeUIState.isDetailOnlyOpen] property to `true`. (Whenever the [MutableStateFlow] has
     * its value changed, it emits the new [ReplyHomeUIState], and that current value is collected
     * and represented via [State] in a lifecycle-aware manner in the `onCreate` override of
     * [MainActivity]).
     *
     * @param emailId the [Email.id] of the [Email] that we should set to be the
     * [ReplyHomeUIState.selectedEmail] of the current value of [_uiState].
     */
    fun setSelectedEmail(emailId: Long) {
        /**
         * We only set isDetailOnlyOpen to true when it's only single pane layout
         */
        val email = uiState.value.emails.find { it.id == emailId }
        _uiState.value = _uiState.value.copy(
            selectedEmail = email,
            isDetailOnlyOpen = true
        )
    }

    /**
     *
     */
    fun closeDetailScreen() {
        @Suppress("RedundantValueArgument")
        _uiState.value = _uiState
            .value.copy(
                isDetailOnlyOpen = false,
                selectedEmail = _uiState.value.emails.first()
            )
    }
}

/**
 *
 */
data class ReplyHomeUIState(
    /**
     *
     */
    val emails: List<Email> = emptyList(),
    /**
     *
     */
    val selectedEmail: Email? = null,
    /**
     *
     */
    val isDetailOnlyOpen: Boolean = false,
    /**
     *
     */
    val loading: Boolean = false,
    /**
     *
     */
    val error: String? = null
)
