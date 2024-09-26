/*
 * Copyright 2018 Google LLC
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

package com.google.samples.apps.sunflower

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.google.samples.apps.sunflower.databinding.ActivityGardenBinding

/**
 * This is the main activity of our app, and is used only to configure the decor view for
 * edge-to-edge display and to set the Activity's content view to the layout file with
 * resource ID [R.layout.activity_garden].
 */
class GardenActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * then we call the [WindowCompat.setDecorFitsSystemWindows] method with the current [Window] of
     * the activity and `false` for the `decorFitsSystemWindows` argument (set to false, the framework
     * will not fit the content view to the insets and will just pass through the [WindowInsetsCompat]
     * to the content view, ie "edge-to-edge mode"). Finally we call the [setContentView] method to
     * have it set our content view to the [ActivityGardenBinding] that [DataBindingUtil] inflates
     * from the layout file with resource ID [R.layout.activity_garden].
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Displaying edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView<ActivityGardenBinding>(this, R.layout.activity_garden)
    }
}
