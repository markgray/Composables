/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.example.composemail.model.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.model.repo.MailRepository

/**
 * The number of items to load on the initial page load.
 *
 * This value is typically larger than [PAGE_SIZE] to ensure that the initial display
 * is populated with a good amount of data, providing a better user experience.
 */
private const val INITIAL_LOAD_SIZE = 30

/**
 * The number of mail items to load per page.
 *
 * This value determines how many items are fetched at a time when the user scrolls
 * to the end of the list.
 */
private const val PAGE_SIZE = 15

/**
 * The threshold for triggering a new page load.
 *
 * This value determines how many items from the end of the loaded list the user must scroll to
 * before the Pager starts loading the next page. Setting this to a value greater than 0 helps
 * to preload data and provide a smoother scrolling experience.
 */
private const val REFRESH_THRESHOLD = 5

/**
 * Creates and configures a [Pager] for mail items.
 *
 * This pager is responsible for loading pages of mail data from the [MailRepository]
 * using a [MailPagingSource]. It is configured with specific page sizes and prefetch
 * distances to optimize performance for a mail list UI.
 *
 * @param mailRepository The repository from which to fetch mail data.
 * @return A [Pager] instance that can be used to create a `PagingData` flow.
 */
fun createMailPager(mailRepository: MailRepository): Pager<Int, MailInfoPeek> =
    Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            // Enable placeholders when loading indicators are supported, see MailItem.kt
            enablePlaceholders = true,
            prefetchDistance = REFRESH_THRESHOLD,
            initialLoadSize = INITIAL_LOAD_SIZE
        ),
        pagingSourceFactory = { MailPagingSource(mailRepo = mailRepository) }
    )