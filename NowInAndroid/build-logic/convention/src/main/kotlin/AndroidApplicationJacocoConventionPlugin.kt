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

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.google.samples.apps.nowinandroid.configureJacoco
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * A Gradle plugin that configures JaCoCo for Android applications.
 *
 * This plugin applies the JaCoCo plugin and configures it to generate coverage reports for both
 * unit and Android tests. It also enables coverage for all build types.
 *
 * To use this plugin, apply it to your Android application project's `build.gradle` file:
 *
 * ```gradle
 * plugins {
 *     id("com.google.samples.apps.nowinandroid.android.application.jacoco")
 * }
 * ```
 */
@Suppress("unused")
class AndroidApplicationJacocoConventionPlugin : Plugin<Project> {
    /**
     * Applies the JaCoCo plugin and configures it for the given Android application project.
     *
     * This method enables JaCoCo for both unit and Android tests and ensures that coverage reports
     * are generated for all build types.
     *
     * @param target The Android application [Project] to configure.
     */
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "jacoco")

            val androidExtension: ApplicationExtension =
                extensions.getByType<ApplicationExtension>()

            androidExtension.buildTypes.configureEach {
                enableAndroidTestCoverage = true
                enableUnitTestCoverage = true
            }

            configureJacoco(extensions.getByType<ApplicationAndroidComponentsExtension>())
        }
    }
}
