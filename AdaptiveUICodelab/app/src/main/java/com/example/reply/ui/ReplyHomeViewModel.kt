/*
 * Copyright 2022 Google LLC
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reply.data.Email
import com.example.reply.data.EmailsRepository
import com.example.reply.data.EmailsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * This is the [ViewModel] used to access the [StateFlow] of [ReplyHomeUIState] which holds the [List]
 * of [Email] objects read from the [EmailsRepository]. The `onCreate` override of [MainActivity] uses
 * the [collectAsState] extension method of [StateFlow] to collect the [ReplyHomeUIState] from the
 * [StateFlow] as a [State], and passes that [ReplyHomeUIState] as the `replyHomeUIState` argument
 * of the [ReplyApp] Composable that it uses as the root Composable of the app. [ReplyApp] then passes
 * it to any of its children that need the [Email] objects in the [ReplyHomeUIState.emails] list of
 * [Email].
 *
 * @param emailsRepository the instance of [EmailsRepositoryImpl] to use to read [Email] objects from.
 */
class ReplyHomeViewModel(
    private val emailsRepository: EmailsRepository = EmailsRepositoryImpl()
) : ViewModel() {

    /**
     * UI state exposed to the UI by our [uiState] field.
     */
    private val _uiState = MutableStateFlow(ReplyHomeUIState(loading = true))

    /**
     * Public read-only access to our [_uiState] field.
     */
    val uiState: StateFlow<ReplyHomeUIState> = _uiState

    init {
        observeEmails()
    }

    /**
     * This function lauches a coroutine on the [CoroutineScope] tied to this [ViewModel] returned
     * by the [viewModelScope] extension function whose `block` calls the [EmailsRepository.getAllEmails]
     * and processes the [Flow] of [List] of [Email] that it returns by using the [catch] extension
     * function on it to catch any exceptions thrown in order to set the [MutableStateFlow.value] of
     * [_uiState] to a new instance of [ReplyHomeUIState] whose `error` field contains the contents
     * of the [Throwable.message] field of the exception that was thrown (and then returning). If no
     * exception is thrown the [Flow.collect] method "collects" the [List] of [Email] emitted by the
     * [EmailsRepository.getAllEmails] and sets the [MutableStateFlow.value] of [_uiState] to a new
     * instance of [ReplyHomeUIState] whose `emails` field is that [List] of [Email].
     */
    private fun observeEmails() {
        viewModelScope.launch {
            emailsRepository.getAllEmails()
                .catch { ex: Throwable ->
                    _uiState.value = ReplyHomeUIState(error = ex.message)
                }
                .collect { emails: List<Email> ->
                    _uiState.value = ReplyHomeUIState(emails = emails)
                }
        }
    }
}

/**
 * This data class holds the "state" that the UI is supposed to display, and is used by the various
 * Composables to get the [Email] objects that they are interested in.
 *
 * @param emails the [List] of [Email] objects to be displayed
 * @param loading if `true` we are in the process of loading our [emails] field.
 * @param error contains the [Throwable.message] of any [Exception] that was thrown while downloading,
 * otherwise it is `null`.
 */
data class ReplyHomeUIState(
    val emails: List<Email> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)