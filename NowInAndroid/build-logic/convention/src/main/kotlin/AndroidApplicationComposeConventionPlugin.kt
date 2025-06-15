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
import com.google.samples.apps.nowinandroid.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * A convention plugin for configuring Jetpack Compose in an Android application.
 *
 * This plugin applies the "com.android.application" and "org.jetbrains.kotlin.plugin.compose"
 * plugins and then configures Android Compose using the [configureAndroidCompose] extension
 * function.
 *
 * A [Plugin] represents an extension to Gradle. A plugin applies some configuration to a target
 * object. Usually, this target object is a [Project], but plugins can be applied to any type of
 * objects.
 */
class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    /**
     * Configures the Android Application project with Compose.
     * We use a [with] statement to apply to our [Project] parameter [target] the `plugin`
     * "com.android.application" and the `plugin` "org.jetbrains.kotlin.plugin.compose".
     * We then initialize our [ApplicationExtension] variable `extension` to the
     * [Project.extensions] of [target] of type [ApplicationExtension] and call our
     * [configureAndroidCompose] function with our [ApplicationExtension] variable `extension`
     * as its `commonExtension` parameter.
     *
     * @param target The [Project] to apply the configuration to.
     */
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            val extension: ApplicationExtension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(commonExtension = extension)
        }
    }

}
