/*
 * Copyright 2023 The Android Open Source Project
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

import com.android.build.gradle.api.AndroidBasePlugin
import com.google.samples.apps.nowinandroid.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * A convention plugin that configures Hilt for Android and ksp.
 *
 * This plugin applies the Hilt Android Gradle plugin and adds the Hilt dependencies
 * to the `implementation` and `ksp` configurations.
 *
 * It also configures Hilt for JVM modules by adding the Hilt core dependency to the
 * `implementation` configuration.
 */
@Suppress("unused")
class HiltConventionPlugin : Plugin<Project> {
    /**
     * Applies the Hilt convention plugin to the [Project] parameter [target].
     *
     * This plugin applies the KSP plugin, adds the Hilt compiler as a KSP dependency,
     * and configures Hilt dependencies based on whether the project is a JVM module or
     * an Android module.
     *
     * For JVM modules (projects with the `org.jetbrains.kotlin.jvm` plugin applied),
     * it adds the `hilt.core` library as an implementation dependency.
     *
     * For Android modules (projects with the `com.android.base` plugin applied),
     * it applies the `dagger.hilt.android.plugin` and adds the `hilt.android` library
     * as an implementation dependency.
     *
     * @param target The [Project] to which the [Plugin] is applied.
     */
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")

            dependencies {
                "ksp"(libs.findLibrary("hilt.compiler").get())
            }

            // Add support for Jvm Module, base on org.jetbrains.kotlin.jvm
            pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                dependencies {
                    "implementation"(libs.findLibrary("hilt.core").get())
                }
            }

            /** Add support for Android modules, based on [AndroidBasePlugin] */
            pluginManager.withPlugin("com.android.base") {
                apply(plugin = "dagger.hilt.android.plugin")
                dependencies {
                    "implementation"(libs.findLibrary("hilt.android").get())
                }
            }
        }
    }
}
