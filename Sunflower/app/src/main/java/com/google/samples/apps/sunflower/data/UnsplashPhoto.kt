/*
 * Copyright 2020 Google LLC
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

package com.google.samples.apps.sunflower.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents a photo from Unsplash.
 *
 * Not all of the fields returned from the API are represented here; only the ones used in this
 * project are listed below. For a full list of fields, consult the API documentation
 * [here](https://unsplash.com/documentation#get-a-photo).
 *
 * The @[SerializedName] annotation that indicates the member should be serialized to JSON with the
 * provided name value as its field name, the @`field` annotation is known as a "use-site target",
 * and makes it clear that the annotation is applied to the backing field of the Kotlin property.
 */
@Suppress("unused")
data class UnsplashPhoto(
    /**
     * Unique photo ID.
     */
    @field:SerializedName("id") val id: String,
    /**
     * Data class that represents URLs available for a Unsplash photo. Although several photo sizes
     * are available, this project uses only uses the small sized photo. See [UnsplashPhotoUrls]
     */
    @field:SerializedName("urls") val urls: UnsplashPhotoUrls,
    /**
     * Data class that represents the user associated with the photo. See [UnsplashUser]
     */
    @field:SerializedName("user") val user: UnsplashUser
)
