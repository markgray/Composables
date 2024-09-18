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

package com.example.jetnews.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jetnews.JetnewsApplication
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.posts.PostsRepository

/**
 * This is the Main Activity of our codelab's sample code.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call the [WindowCompat.setDecorFitsSystemWindows] method with the current Window for
     * the activity, and `false` for the `decorFitsSystemWindows` argument so that the framework
     * will not fit the content view to the insets and will just pass through the [WindowInsetsCompat]
     * to the content view. We initialize our [AppContainer] variable `val appContainer` to the
     * [JetnewsApplication.container] singleton instance which holds the singleton [PostsRepository]
     * in its [AppContainer.postsRepository] field and the singleton [InterestsRepository] in its
     * [AppContainer.interestsRepository] field. We then call the [setContent] method to Compose
     * the [JetnewsApp] composable into our activity using our `appContainer` variable as its
     * `appContainer` argument . The content will become the root view of our activity.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appContainer: AppContainer = (application as JetnewsApplication).container
        setContent {
            Box(modifier = Modifier.safeDrawingPadding()) {
                JetnewsApp(appContainer = appContainer)
            }
        }
    }
}
