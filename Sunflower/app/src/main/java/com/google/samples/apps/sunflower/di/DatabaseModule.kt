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

package com.google.samples.apps.sunflower.di

import android.content.Context
import com.google.samples.apps.sunflower.data.AppDatabase
import com.google.samples.apps.sunflower.data.GardenPlantingDao
import com.google.samples.apps.sunflower.data.PlantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt [Module] that provides database-related dependencies.
 *
 * This [Module] is installed in the [SingletonComponent], meaning the provided instances
 * will be singletons and live as long as the application.
 */
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    /**
     * Provides the singleton instance of the [AppDatabase].
     *
     * This function is annotated with @[Singleton] to ensure that Dagger Hilt creates and provides
     * only one instance of the database throughout the application's lifecycle. The @[Provides]
     * annotation tells Hilt that this method is how to create an instance of [AppDatabase].
     * The @[ApplicationContext] qualifier is used to inject the application context, which is
     * required by the database builder.
     *
     * @param context The application context, injected by Hilt.
     * @return A singleton instance of [AppDatabase].
     */
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    /**
     * Provides the [PlantDao] from the [AppDatabase].
     * This DAO is used to access plant-related data from the database.
     * Dagger Hilt will inject this dependency wherever a [PlantDao] is required.
     * The @[Provides] annotation tells Hilt that this method is how to create an instance
     * of [PlantDao].
     *
     * @param appDatabase The singleton instance of the [AppDatabase].
     * @return An instance of [PlantDao].
     */
    @Provides
    fun providePlantDao(appDatabase: AppDatabase): PlantDao {
        return appDatabase.plantDao()
    }

    /**
     * Provides the [GardenPlantingDao] from the [AppDatabase].
     * This DAO is used to access data related to plants in the user's garden.
     * Dagger Hilt will inject this dependency wherever a [GardenPlantingDao] is required.
     * The @[Provides] annotation tells Hilt that this method is how to create an instance
     * of [GardenPlantingDao].
     *
     * @param appDatabase The singleton instance of the [AppDatabase].
     * @return An instance of [GardenPlantingDao].
     */
    @Provides
    fun provideGardenPlantingDao(appDatabase: AppDatabase): GardenPlantingDao {
        return appDatabase.gardenPlantingDao()
    }
}
