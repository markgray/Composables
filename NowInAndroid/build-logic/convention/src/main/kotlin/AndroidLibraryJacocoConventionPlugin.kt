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

import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.google.samples.apps.nowinandroid.configureJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * A convention plugin that configures Jacoco for Android library projects.
 *
 * This plugin applies the Jacoco plugin and enables test coverage for both unit and Android tests
 * in all build types. It also configures Jacoco using the `configureJacoco` extension function.
 */
@Suppress("unused")
class AndroidLibraryJacocoConventionPlugin : Plugin<Project> {
    /**
     * Applies the JaCoCo plugin and configures it for Android library projects.
     *
     * This function performs the following actions:
     * 1. Applies the "jacoco" plugin to the project.
     * 2. Retrieves the [LibraryExtension] to configure Android-specific settings.
     * 3. Enables JaCoCo coverage for both Android tests and unit tests for all build types.
     * 4. Calls the `configureJacoco` extension function to set up JaCoCo tasks and reports
     *    using the `LibraryAndroidComponentsExtension`.
     *
     * @param target The [Project] to which this plugin is applied.
     */
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "jacoco")

            val androidExtension: LibraryExtension = extensions.getByType<LibraryExtension>()

            androidExtension.buildTypes.configureEach {
                enableAndroidTestCoverage = true
                enableUnitTestCoverage = true
            }

            configureJacoco(extensions.getByType<LibraryAndroidComponentsExtension>())
        }
    }
}
