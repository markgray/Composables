/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composemail.model

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.composemail.model.data.MailInfoFull
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.model.paging.createMailPager
import com.example.composemail.model.repo.MailRepository
import com.example.composemail.model.repo.OfflineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * An [AndroidViewModel] that holds the app's state.
 *
 * This class has a few responsibilities:
 *  - Expose the list of mails to the UI.
 *  - Hold the currently opened mail.
 *  - Expose methods to open and close a mail.
 *
 * @param application The [Application] that this [AndroidViewModel] is attached to.
 */
class ComposeMailModel(application: Application) : AndroidViewModel(application) {
    /**
     * The repository that this [AndroidViewModel] uses to get its data.
     */
    private val mailRepo: MailRepository =
        OfflineRepository(resources = getApplication<Application>().resources)

    /**
     * The currently opened mail.
     *
     * This is a private [MutableState] of [MailInfoFull] that is exposed as a public, immutable
     * value through the [openedMail] property. This is `null` when no mail is opened.
     */
    private var _openedMail: MutableState<MailInfoFull?> = mutableStateOf(value = null)

    /**
     * The currently opened mail.
     *
     * This provides readonly access to our [MutableState] of [MailInfoFull] property [_openedMail].
     * This is `null` when no mail is opened.
     */
    val openedMail: MailInfoFull?
        get() = _openedMail.value

    /**
     * A [Flow] of [PagingData] of [MailInfoPeek] that can be collected to show a list of
     * conversations.
     */
    val conversations: Flow<PagingData<MailInfoPeek>> =
        createMailPager(mailRepository = mailRepo).flow

    /**
     * A utility function to check whether a mail is open or not.
     *
     * @return `true` if a mail is open, `false` otherwise.
     */
    fun isMailOpen(): Boolean = _openedMail.value != null

    /**
     * Opens a mail given its [id].
     *
     * This function will retrieve the full mail content from the repository and update the
     * [openedMail] state. This is a suspending function that executes on the `IO` dispatcher.
     *
     * @param id The id of the mail to open.
     */
    fun openMail(id: Int) {
        viewModelScope.launch {
            var openedMailInfo: MailInfoFull?
            withContext(context = Dispatchers.IO) {
                openedMailInfo = mailRepo.getFullMail(id = id)
            }
            _openedMail.value = openedMailInfo
        }
    }

    /**
     * Closes the currently opened mail.
     *
     * This function updates the [MutableState] of [MailInfoFull] property [_openedMail] to `null`.
     */
    fun closeMail() {
        _openedMail.value = null
    }
}