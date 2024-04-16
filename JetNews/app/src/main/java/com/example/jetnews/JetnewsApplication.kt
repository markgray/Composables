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

package com.example.jetnews

import android.app.Application
import android.content.Context
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.AppContainerImpl
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.ui.JetnewsDestinations

/**
 * This custom [Application] allows our app to access our [AppContainer] field [container] and our
 * constant [JETNEWS_APP_URI] by fetching a reference to this [JetnewsApplication] using either
 * [android.app.Activity.getApplication] or [Context.getApplicationContext] then casting the
 * [Application] returned to a [JetnewsApplication].
 */
class JetnewsApplication : Application() {
    companion object {
        /**
         * This is used to construct a [navDeepLink] for the [JetnewsDestinations.HOME_ROUTE] route
         * [composable]. In the `AndroidManifest.xml` file there is an `intent-filter` which echos
         * this in its `data` element where it specifies android:host="developer.android.com",
         * android:pathPrefix="/jetnews", and android:scheme="https". The widget of the app allows
         * you to select an article and it will be opened in this app (very nifty).
         */
        const val JETNEWS_APP_URI: String = "https://developer.android.com/jetnews"
    }

    /**
     * [AppContainer] instance used by the rest of classes to obtain dependencies. It has a reference
     * to our singleton [PostsRepository] in its [AppContainer.postsRepository] field and a reference
     * to our singleton [InterestsRepository] in its [AppContainer.interestsRepository] field.
     */
    lateinit var container: AppContainer

    /**
     * Called when the application is starting, before any activity, service, or receiver objects
     * (excluding content providers) have been created. First we call our super's implementation of
     * `onCreate`, then we initialize our [AppContainer] field with a new instance of [AppContainerImpl]
     * using `this` as its `applicationContext` [Context] argument.
     */
    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(applicationContext = this)
    }
}
