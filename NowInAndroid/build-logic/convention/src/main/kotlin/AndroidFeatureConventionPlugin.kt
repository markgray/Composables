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
import com.google.samples.apps.nowinandroid.configureGradleManagedDevices
import com.google.samples.apps.nowinandroid.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin that applies common configurations for Android feature modules.
 *
 * This plugin applies the following plugins:
 *  - `nowinandroid.android.library`
 *  - `nowinandroid.hilt`
 *  - `org.jetbrains.kotlin.plugin.serialization`
 *
 * It also configures the following:
 *  - Disables animations in tests.
 *  - Configures Gradle managed devices.
 *  - Adds dependencies for UI, design system, Hilt navigation, Lifecycle, Navigation, Tracing, and Kotlinx Serialization.
 *  - Adds test dependencies for Navigation testing and Lifecycle runtime testing.
 */
@Suppress("unused")
class AndroidFeatureConventionPlugin : Plugin<Project> {
    /**
     * Applies the Android Feature convention plugin to the [Project] parameter [target].
     *
     * This plugin applies the following plugins:
     *  - `nowinandroid.android.library`
     *  - `nowinandroid.hilt`
     *  - `org.jetbrains.kotlin.plugin.serialization`
     *
     * It also configures the Android library extension with the following:
     *  - Disables animations in tests.
     *  - Configures Gradle managed devices.
     *
     * Finally, it adds the following dependencies:
     *  - `core:ui`
     *  - `core:designsystem`
     *  - `androidx.hilt.navigation.compose`
     *  - `androidx.lifecycle.runtimeCompose`
     *  - `androidx.lifecycle.viewModelCompose`
     *  - `androidx.navigation.compose`
     *  - `androidx.tracing.ktx`
     *  - `kotlinx.serialization.json`
     *  - `androidx.navigation.testing` (testImplementation)
     *  - `androidx.lifecycle.runtimeTesting` (androidTestImplementation)
     *
     * @param target The [Project] to apply the [Plugin] to.
     */
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "nowinandroid.android.library")
            apply(plugin = "nowinandroid.hilt")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }

            dependencies {
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:designsystem"))

                "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                "implementation"(libs.findLibrary("androidx.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())

                "testImplementation"(libs.findLibrary("androidx.navigation.testing").get())
                "androidTestImplementation"(
                    libs.findLibrary("androidx.lifecycle.runtimeTesting").get(),
                )
            }
        }
    }
}
