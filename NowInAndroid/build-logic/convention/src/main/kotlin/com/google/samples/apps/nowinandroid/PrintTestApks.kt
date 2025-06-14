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

package com.google.samples.apps.nowinandroid

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.BuiltArtifacts
import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.build.api.variant.HasAndroidTest
import com.android.build.api.variant.Variant
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.nio.file.Path

/**
 * Configures a task to print the location of the androidTest APK for each variant.
 * This is used by the CI to run tests on Firebase Test Lab.
 *
 * Note that this is not a general purpose solution, but rather a workaround for the lack of a
 * stable API to get the location of the androidTest APK.
 *
 * This is waiting for a fix for https://issuetracker.google.com/issues/260717658.
 *
 * The task is named `<variantName>PrintTestApk` and is of type [PrintApkLocationTask].
 * It depends on the [SingleArtifact.APK] artifact and the androidTest sources.
 *
 * If there are no androidTest sources, the task is not created.
 *
 * The task prints the location of the androidTest APK to standard output.
 *
 * @param extension The Android components extension.
 */
internal fun Project.configurePrintApksTask(extension: AndroidComponentsExtension<*, *, *>) {
    extension.onVariants { variant: Variant ->
        if (variant is HasAndroidTest) {
            val loader: BuiltArtifactsLoader = variant.artifacts.getBuiltArtifactsLoader()
            val artifact: Provider<Directory>? =
                variant.androidTest?.artifacts?.get(SingleArtifact.APK)
            val javaSources: Provider<out Collection<Directory>>? =
                variant.androidTest?.sources?.java?.all

            @Suppress("UnstableApiUsage")
            val kotlinSources: Provider<out Collection<Directory>>? =
                variant.androidTest?.sources?.kotlin?.all

            val testSources: Provider<out Collection<Directory>?>? =
                if (javaSources != null && kotlinSources != null) {
                    javaSources.zip(kotlinSources) { javaDirs, kotlinDirs ->
                        javaDirs + kotlinDirs
                    }
                } else javaSources ?: kotlinSources

            if (artifact != null && testSources != null) {
                tasks.register(
                    "${variant.name}PrintTestApk",
                    PrintApkLocationTask::class.java,
                ) {
                    apkFolder = artifact
                    builtArtifactsLoader = loader
                    variantName = variant.name
                    sources = testSources
                }
            }
        }
    }
}

/**
 * Task to print the location of the androidTest APK.
 * This is used by the CI to run tests on Firebase Test Lab.
 *
 * This task is not cacheable because it prints to standard output.
 */
@DisableCachingByDefault(because = "Prints output")
internal abstract class PrintApkLocationTask : DefaultTask() {

    /**
     * Folder containing the APKs.
     * This is the output of the [SingleArtifact.APK] artifact.
     */
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputDirectory
    abstract val apkFolder: DirectoryProperty

    /**
     * [Property] of [Directory] containing the test sources.
     * This is used to check if there are any test sources before printing the APK location.
     */
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    abstract val sources: ListProperty<Directory>

    /**
     * [BuiltArtifactsLoader] to load the APKs from the [apkFolder].
     * This is an internal property and should not be used by consumers.
     */
    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    /**
     * Name of the variant.
     * This is used to name the task.
     */
    @get:Input
    abstract val variantName: Property<String>

    /**
     * Prints the location of the androidTest APK to standard output.
     *
     * If there are no androidTest sources, the task does nothing.
     *
     * @throws RuntimeException If the androidTest sources cannot be checked.
     * @throws RuntimeException If the APKs cannot be loaded.
     * @throws RuntimeException If there is not exactly one APK.
     */
    @TaskAction
    fun taskAction() {
        val hasFiles: Boolean = sources.orNull?.any { directory: Directory ->
            directory.asFileTree.files.any {
                it.isFile && "build${File.separator}generated" !in it.parentFile.path
            }
        } ?: throw RuntimeException("Cannot check androidTest sources")

        // Don't print APK location if there are no androidTest source files
        if (!hasFiles) return

        val builtArtifacts: BuiltArtifacts = builtArtifactsLoader.get().load(apkFolder.get())
            ?: throw RuntimeException("Cannot load APKs")
        if (builtArtifacts.elements.size != 1)
            throw RuntimeException("Expected one APK !")
        val apk: Path = File(builtArtifacts.elements.single().outputFile).toPath()
        @Suppress("ReplacePrintlnWithLogging")
        println(apk)
    }
}
