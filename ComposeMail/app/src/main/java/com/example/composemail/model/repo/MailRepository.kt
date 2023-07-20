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

package com.example.composemail.model.repo

import com.example.composemail.model.data.MailInfoFull
import com.example.composemail.model.data.MailInfoPeek

/**
 * TODO: Add kdoc
 */
interface MailRepository {
    /**
     * TODO: Add kdoc
     */
    suspend fun connect()

    /**
     * TODO: Add kdoc
     */
    suspend fun getNextSetOfConversations(amount: Int): MailConversationsResponse

    /**
     * TODO: Add kdoc
     */
    suspend fun getFullMail(id: Int): MailInfoFull?
}

/**
 * TODO: Add kdoc
 */
data class MailConversationsResponse(
    /**
     * TODO: Add kdoc
     */
    val conversations: List<MailInfoPeek>,
    /**
     * TODO: Add kdoc
     */
    val page: Int
)