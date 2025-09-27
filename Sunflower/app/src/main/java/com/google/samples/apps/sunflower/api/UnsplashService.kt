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

package com.google.samples.apps.sunflower.api

import com.google.samples.apps.sunflower.BuildConfig
import com.google.samples.apps.sunflower.data.UnsplashSearchResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Used to connect to the Unsplash API to fetch photos
 */
interface UnsplashService {

    /**
     * Search for photos on Unsplash. The @[GET] annotation causes okhttp3 to produce a GET request
     * which has the string "search/photos" appended to it as the path. The @[Query] annotations
     * appends to the url the Query parameters "?query=[query]", "?page=[page], "?per_page=[perPage]"
     * and "?client_id=[clientId]" (the last defaulting to the value of `BuildConfig.UNSPLASH_ACCESS_KEY`")
     * with each of the Query parameters separated by an Ampersand (&).
     *
     * @param query The search terms.
     * @param page Page number to retrieve. (Optional; default: 1)
     * @param perPage Number of items per page. (Optional; default: 10)
     * @param clientId Your Unsplash application’s access key. (Default: [BuildConfig.UNSPLASH_ACCESS_KEY])
     * @return An [UnsplashSearchResponse] object containing the search results.
     */
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("client_id") clientId: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): UnsplashSearchResponse

    companion object {
        /**
         * The base URL for the Unsplash API.
         */
        private const val BASE_URL = "https://api.unsplash.com/"

        /**
         * Creates an instance of [UnsplashService]. We initialize our [HttpLoggingInterceptor]
         * variable `logger` to a new instance to which we chain an [apply] extension function which
         * sets its [HttpLoggingInterceptor.level] property to [Level.BASIC]. To initialize our
         * [OkHttpClient] variable `client` we use a [OkHttpClient.Builder] to which we chain a
         * [OkHttpClient.Builder.addInterceptor] that adds `logger` as an [Interceptor] and then we
         * use the [OkHttpClient.Builder.build] method to build the [OkHttpClient]. Finally we return
         * the [UnsplashService] created using an instance of [Retrofit.Builder] to which we chain
         * a [Retrofit.Builder.baseUrl] to set the API base URL to [BASE_URL], to which we chain a
         * [Retrofit.Builder.client] that sets the HTTP client used for requests to `client`, to
         * which we chain a [Retrofit.Builder.addConverterFactory] that adds a new instance of
         * [GsonConverterFactory] as a converter factory, to which we chain a [Retrofit.Builder.build]
         * to build the [Retrofit] instance and to this we chain a [Retrofit.create] to create a
         * [UnsplashService] from the [Retrofit] instance.
         *
         * @return The created [UnsplashService] instance.
         */
        fun create(): UnsplashService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }

            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor = logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UnsplashService::class.java)
        }
    }
}