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
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * A plugin that applies the Android Lint plugin and configures it to sensible defaults.
 *
 * This plugin can be applied to any project that has the Android Application or Library plugin
 * applied. If the project does not have either of these plugins applied, it will apply the
 * Android Lint plugin directly and configure it.
 *
 * The plugin configures the following Lint options:
 *  - `xmlReport`: `true`
 *  - `sarifReport`: `true`
 *  - `checkDependencies`: `true`
 *  - `disable`: `["GradleDependency"]`
 */
@Suppress("unused")
class AndroidLintConventionPlugin : Plugin<Project> {
    /**
     * Applies the plugin to the given project.
     *
     * If the project has the Android Application or Library plugin applied, this plugin will
     * configure the `lint` block of the respective extension. Otherwise, it will apply the
     * Android Lint plugin directly and configure it.
     *
     * @param target The project to apply the plugin to.
     */
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    configure<ApplicationExtension> { lint(Lint::configure) }

                pluginManager.hasPlugin("com.android.library") ->
                    configure<LibraryExtension> { lint(Lint::configure) }

                else -> {
                    apply(plugin = "com.android.lint")
                    configure<Lint>(Lint::configure)
                }
            }
        }
    }
}

/**
 * Configures the Lint extension with sensible defaults.
 *
 * This function sets the following options:
 *  - `xmlReport`: `true` - Generates an XML report after running lint.
 *  - `sarifReport`: `true` - Generates a SARIF report after running lint.
 *  - `checkDependencies`: `true` - Checks dependencies for issues.
 *  - `disable`: `["GradleDependency"]` - Disables the `GradleDependency` check, which can be noisy.
 */
private fun Lint.configure() {
    xmlReport = true
    sarifReport = true
    checkDependencies = true
    disable += "GradleDependency"
}
