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

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import com.google.samples.apps.nowinandroid.configureFlavors
import com.google.samples.apps.nowinandroid.configureGradleManagedDevices
import com.google.samples.apps.nowinandroid.configureKotlinAndroid
import com.google.samples.apps.nowinandroid.configurePrintApksTask
import com.google.samples.apps.nowinandroid.disableUnnecessaryAndroidTests
import com.google.samples.apps.nowinandroid.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Android libraries.
 *
 * This plugin applies common configurations and dependencies for Android library modules,
 * including:
 *  - The `com.android.library` plugin for Android library functionality.
 *  - The `org.jetbrains.kotlin.android` plugin for Kotlin support in Android.
 *  - The `nowinandroid.android.lint` plugin for linting.
 *  - Configuration for Kotlin, target SDK, test runner, and resource prefix.
 *  - Configuration for flavors and Gradle-managed devices.
 *  - Task to print APKs.
 *  - Disabling unnecessary Android tests.
 *  - Common dependencies for testing and tracing.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    /**
     * Applies the Android library conventions to the given project.
     *
     * This includes:
     *  - Applying the `com.android.library`, `org.jetbrains.kotlin.android`, and
     *  `nowinandroid.android.lint` plugins.
     *  - Configuring the Android library extension with:
     *      - Kotlin Android options.
     *      - Setting the target SDK to 35.
     *      - Setting the test instrumentation runner.
     *      - Disabling animations in tests.
     *      - Configuring product flavors.
     *      - Configuring Gradle managed devices.
     *      - Setting a resource prefix based on the module name.
     *  - Configuring the Android components extension with:
     *      - A task to print APK information.
     *      - Disabling unnecessary Android tests.
     *  - Adding common dependencies for testing and tracing: "kotlin.test", and
     *  "androidx.tracing.ktx"
     *
     * @param target The [Project] to apply the conventions to.
     */
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "nowinandroid.android.lint")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testOptions.animationsDisabled = true
                configureFlavors(this)
                configureGradleManagedDevices(this)
                // The resource prefix is derived from the module name,
                // so resources inside ":core:module1" must be prefixed with "core_module1_"
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                configurePrintApksTask(this)
                disableUnnecessaryAndroidTests(target)
            }
            dependencies {
                "androidTestImplementation"(libs.findLibrary("kotlin.test").get())
                "testImplementation"(libs.findLibrary("kotlin.test").get())

                "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
            }
        }
    }
}
