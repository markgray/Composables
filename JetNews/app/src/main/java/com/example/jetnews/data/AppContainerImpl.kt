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

package com.example.jetnews.data

import android.app.Activity
import android.app.Application
import android.content.Context
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.impl.FakeInterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.FakePostsRepository
import com.example.jetnews.data.posts.impl.posts
import com.example.jetnews.model.PostsFeed

/**
 * Dependency Injection container at the application level. Our [JetnewsApplication] custom
 * [Application] has a [AppContainer] field [JetnewsApplication.container] which other classes
 * in the app can access using the [Application] returned by [Activity.getApplication].
 */
interface AppContainer {
    /**
     * The singleton [PostsRepository] that all classes in the app should use.
     */
    val postsRepository: PostsRepository

    /**
     * The singleton [InterestsRepository] that all classes in the app should use.
     */
    val interestsRepository: InterestsRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
@Suppress("unused")
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    /**
     * The singleton [PostsRepository] that all classes in the app should use. [FakePostsRepository]
     * implements [PostsRepository] using the dummy data found in the global [PostsFeed] variable
     * [posts].
     */
    override val postsRepository: PostsRepository by lazy {
        FakePostsRepository()
    }

    /**
     * The singleton [InterestsRepository] that all classes in the app should use.
     * [FakeInterestsRepository] implements [InterestsRepository] using private fields containing
     * dummy data.
     */
    override val interestsRepository: InterestsRepository by lazy {
        FakeInterestsRepository()
    }
}
