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
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import com.google.samples.apps.nowinandroid.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude

/**
 * Firebase convention plugin for Android applications.
 */
@Suppress("unused")
class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    /**
     * Applies the Firebase convention plugin to the given [Project].
     *
     * This plugin applies the following plugins:
     *  - `com.google.gms.google-services`
     *  - `com.google.firebase.firebase-perf`
     *  - `com.google.firebase.crashlytics`
     *
     * It also adds the following dependencies:
     *  - `firebase-bom`
     *  - `firebase-analytics`
     *  - `firebase-performance` (with exclusions for protobuf-javalite and protolite-well-known-types)
     *  - `firebase-crashlytics`
     *
     * Finally, it disables the Crashlytics mapping file upload for all build types.
     * This feature should only be enabled if a Firebase backend is available and configured in
     * `google-services.json`.
     *
     * @param target The [Project] to apply the [Plugin] to.
     */
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.gms.google-services")
            apply(plugin = "com.google.firebase.firebase-perf")
            apply(plugin = "com.google.firebase.crashlytics")

            dependencies {
                val bom: Provider<MinimalExternalModuleDependency?> =
                    libs.findLibrary("firebase-bom").get()
                "implementation"(platform(bom))
                "implementation"(libs.findLibrary("firebase.analytics").get())
                "implementation"(libs.findLibrary("firebase.performance").get()) {
                    /*
                    Exclusion of protobuf / protolite dependencies is necessary as the
                    datastore-proto brings in protobuf dependencies. These are the source of truth
                    for Now in Android.
                    That's why the duplicate classes from below dependencies are excluded.
                    */
                    exclude(group = "com.google.protobuf", module = "protobuf-javalite")
                    exclude(group = "com.google.firebase", module = "protolite-well-known-types")
                }
                "implementation"(libs.findLibrary("firebase.crashlytics").get())
            }

            extensions.configure<ApplicationExtension> {
                buildTypes.configureEach {
                    // Disable the Crashlytics mapping file upload. This feature should only be
                    // enabled if a Firebase backend is available and configured in
                    // google-services.json.
                    configure<CrashlyticsExtension> {
                        mappingFileUploadEnabled = false
                    }
                }
            }
        }
    }
}
