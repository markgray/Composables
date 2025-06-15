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
import com.android.build.gradle.BaseExtension
import com.google.samples.apps.nowinandroid.configureBadgingTasks
import com.google.samples.apps.nowinandroid.configureGradleManagedDevices
import com.google.samples.apps.nowinandroid.configureKotlinAndroid
import com.google.samples.apps.nowinandroid.configurePrintApksTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * Gradle plugin for Android applications.
 *
 * This plugin applies common configurations and plugins for Android applications, including:
 *  - `com.android.application` for Android application builds.
 *  - `org.jetbrains.kotlin.android` for Kotlin support in Android projects.
 *  - `nowinandroid.android.lint` for custom lint checks.
 *  - `com.dropbox.dependency-guard` for managing dependencies.
 *
 * It also configures:
 *  - Kotlin Android options.
 *  - Default target SDK version.
 *  - Disabling animations in tests.
 *  - Gradle-managed devices.
 *  - Tasks for printing APKs and badging.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    /**
     * Applies the plugin to the given project.
     *
     * This function configures the project with the necessary plugins and settings for an Android
     * application.
     * It applies the following plugins:
     *  - `com.android.application`: For building Android applications.
     *  - `org.jetbrains.kotlin.android`: For Kotlin support in Android projects.
     *  - `nowinandroid.android.lint`: For custom lint checks.
     *  - `com.dropbox.dependency-guard`: For managing dependencies.
     *
     * It also configures the following:
     *  - Kotlin Android options using `configureKotlinAndroid`.
     *  - Sets the default target SDK to 35.
     *  - Disables animations in tests.
     *  - Configures Gradle-managed devices using `configureGradleManagedDevices`.
     *  - Configures tasks for printing APKs using `configurePrintApksTask`.
     *  - Configures badging tasks using `configureBadgingTasks`.
     *
     * @param target The [Project] to apply the [Plugin] to.
     */
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "nowinandroid.android.lint")
            apply(plugin = "com.dropbox.dependency-guard")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }
            extensions.configure<ApplicationAndroidComponentsExtension> {
                configurePrintApksTask(this)
                configureBadgingTasks(extensions.getByType<BaseExtension>(), this)
            }
        }
    }
}
