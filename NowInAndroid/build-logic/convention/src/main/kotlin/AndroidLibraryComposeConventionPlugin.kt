/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import com.android.build.gradle.LibraryExtension
import com.google.samples.apps.nowinandroid.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * A convention plugin for configuring Android libraries with Jetpack Compose.
 *
 * This plugin applies the following plugins:
 *  - `com.android.library`
 *  - `org.jetbrains.kotlin.plugin.compose`
 *
 * It also configures Android Compose for the library using the [configureAndroidCompose] extension function.
 */
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    /**
     * Applies the Android Library and Kotlin Compose plugins to the project.
     * Also configures Android Compose for the library extension.
     *
     * @param target The [Project] to apply the [Plugin] and configuration to.
     */
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            val extension: LibraryExtension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }

}
