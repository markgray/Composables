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

package com.example.compose.jetsurvey.survey

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Manages the creation of photo Uris. The Uri is used to store the photos taken with the camera.
 *
 * @property appContext Application context needed to access package resources and cache dir.
 */
class PhotoUriManager(private val appContext: Context) {

    /**
     * Creates a new Uri to store the photo. The Uri is backed by a File with an unique name,
     * located in the app's cache directory.
     *
     * We start by initializing our [File] variable `photosDir` with a directory named [PHOTOS_DIR]
     * ("photos") located in the cache directory of our [Context] parameter [appContext]. Then we
     * call [File.mkdirs] on that variable to create the directory if it doesn't already exist.
     * We initialize our [File] variable `photoFile` with a file located in the `photosDir` directory
     * whose unique name is generated by our [generateFilename] function. We initiailize our [String]
     * variable `authority` with the package name of our [Context] parameter [appContext] concatenated
     * with our constant [String] variable [FILE_PROVIDER] ("fileprovider"). Then we return the [Uri]
     * that the [FileProvider.getUriForFile] function returns when passed our [Context] property
     * [appContext] as its `context`, our [String] variable `authority` as its `authority`, and
     * `photoFile` as its `file`.
     *
     * @return the new [Uri]
     */
    fun buildNewUri(): Uri {
        val photosDir = File(appContext.cacheDir, PHOTOS_DIR)
        photosDir.mkdirs()
        val photoFile = File(photosDir, generateFilename())
        val authority = "${appContext.packageName}.$FILE_PROVIDER"
        return FileProvider.getUriForFile(appContext, authority, photoFile)
    }

    /**
     * Creates a unique file name based on the time the photo is taken
     */
    private fun generateFilename() = "selfie-${System.currentTimeMillis()}.jpg"

    companion object {
        /**
         * The directory in which all the selfies will be stored
         */
        private const val PHOTOS_DIR = "photos"

        /**
         * The end of our authority string of our content provider.
         */
        private const val FILE_PROVIDER = "fileprovider"
    }
}
