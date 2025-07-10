/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module for providing the [NiaDatabase] instance.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    /**
     * Provides the [NiaDatabase] instance. We call [Room.databaseBuilder] to construct a new
     * instance of [RoomDatabase.Builder] with its `context` argument set to our [Context] parameter
     * [context], `klass` argument set to [NiaDatabase] and `name` argument set to "nia-database".
     * We call its [RoomDatabase.Builder.build] method to build the database and return the resulting
     * instance of [NiaDatabase]..
     *
     * @param context The application context.
     * @return The [NiaDatabase] instance.
     */
    @Provides
    @Singleton
    fun providesNiaDatabase(
        @ApplicationContext context: Context,
    ): NiaDatabase = Room.databaseBuilder(
        context = context,
        klass = NiaDatabase::class.java,
        name = "nia-database",
    ).build()
}
