/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.data

import android.app.Application
import android.content.Context
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.JetnewsApp
import com.example.jetnews.ui.JetnewsNavGraph
import com.example.jetnews.ui.MainActivity

/**
 * Dependency Injection container at the application level. Our [JetnewsApplication] custom
 * [Application] has a [AppContainer] field [JetnewsApplication.container] that its `onCreate`
 * override sets to an instance of [AppContainerImpl] implementation and the `onCreate` override
 * of [MainActivity] retrieves the [AppContainer] that it uses as an argument to [JetnewsApp] from
 * there when it calls `setContent` to have it Compose [JetnewsApp] into the activity as its root
 * view. [JetnewsApp] then passes that reference to [JetnewsNavGraph] which passes a reference to
 * whichever repository in the [AppContainer] the screens that it navigates to needs.
 */
interface AppContainer {
    /**
     * A reference to or singleton [PostsRepository]
     */
    val postsRepository: PostsRepository

    /**
     * A reference to or singleton [InterestsRepository]
     */
    val interestsRepository: InterestsRepository
}

/**
 * Implementation for the [AppContainer] Dependency Injection container at the application level.
 * Variables are initialized lazily and the same instance is shared across the whole app.
 *
 * @param applicationContext the [Context] of our [JetnewsApplication] custom [Application]
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    /**
     * Our singleton [PostsRepository]
     */
    override val postsRepository: PostsRepository by lazy {
        PostsRepository()
    }

    /**
     * Our singleton [InterestsRepository]
     */
    override val interestsRepository: InterestsRepository by lazy {
        InterestsRepository()
    }
}
