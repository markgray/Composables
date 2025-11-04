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
 * A repository that handles fetching mail data.
 *
 * This interface defines the contract for interacting with a mail data source,
 * abstracting the underlying implementation (e.g., network, local database).
 */
interface MailRepository {
    /**
     * Establishes a connection to the mail data source.
     *
     * This suspending function should be called before any other data-fetching operations
     * to ensure the repository is ready to serve requests.
     */
    suspend fun connect()

    /**
     * Fetches the next page of mail conversations.
     *
     * This function retrieves a paginated list of conversations, ideal for implementing
     * infinite scrolling. Each call to this function will fetch the next consecutive
     * set of conversations.
     *
     * @param amount The number of conversations to fetch in this page.
     * @return A [MailConversationsResponse] containing the list of conversation peeks and the
     * current page number.
     */
    suspend fun getNextSetOfConversations(amount: Int): MailConversationsResponse

    /**
     * Fetches the complete content of a single email by its unique ID.
     *
     * This suspending function retrieves the full details of an email, including its
     * body, attachments, and complete recipient list, unlike the preview version.
     * If the email with the specified ID is not found, it returns `null`.
     *
     * @param id The unique identifier of the email to fetch.
     * @return A [MailInfoFull] object containing the complete email details, or `null` if not found.
     */
    suspend fun getFullMail(id: Int): MailInfoFull?
}

/**
 * A data class representing a paginated response for mail conversations.
 *
 * This class is typically returned when fetching a list of conversations, providing both
 * the data for the current page and the page number.
 *
 * @param conversations A list of [MailInfoPeek] objects, representing the summary of each
 * conversation on the current page.
 * @param page The page number for this set of conversations.
 */
data class MailConversationsResponse(
    val conversations: List<MailInfoPeek>,
    val page: Int
)