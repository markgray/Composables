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

@file:Suppress("UnusedImport")

package com.example.jetnews

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.AppContainerImpl
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository

/**
 * This custom [Application] allows any classes of this [Application] to use the method
 * [AppCompatActivity.getApplication] to get a reference to this [Application] and to use
 * that reference to access our fields and methods (in our case our [AppContainer] field
 * [container]).
 */
class JetnewsApplication : Application() {

    /**
     * [AppContainer] instance used by the rest of our classes to obtain dependencies. The field
     * [AppContainer.postsRepository] contains a reference to our [PostsRepository], and the field
     * [AppContainer.interestsRepository] contains a reference to our [InterestsRepository].
     */
    lateinit var container: AppContainer

    /**
     * Called when the application is starting, before any activity, service, or receiver objects
     * (excluding content providers) have been created. First we call our super's implementation of
     * `onCreate`, then we initialize our [AppContainer] field [container] with a new instance of
     * [AppContainerImpl] using our [Context] as its `applicationContext` argument.
     */
    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(applicationContext = this)
    }
}
