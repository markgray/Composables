/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.samples.apps.nowinandroid.core.network.demo.DemoAssetManager
import java.io.File
import java.io.InputStream
import java.util.Properties

/**
 * This class helps with loading Android `/assets` files, especially when running JVM unit tests.
 * It must remain on the root package for an easier [Class.getResource] with relative paths.
 * @see <a href="https://developer.android.com/reference/tools/gradle-api/7.3/com/android/build/api/dsl/UnitTestOptions">UnitTestOptions</a>
 */

internal object JvmUnitTestDemoAssetManager : DemoAssetManager {
    /**
     * The path to the Android Asset file. This property is only available when running JVM unit tests.
     * @see <a href="https://developer.android.com/studio/build/gradle-tips#access-build-config-fields-from-test">Access build config fields from test</a>
     */
    private val config =
        requireNotNull(value = javaClass.getResource("com/android/tools/test_config.properties")) {
            """
            Missing Android resources properties file.
            Did you forget to enable the feature in the gradle build file?
            android.testOptions.unitTests.isIncludeAndroidResources = true
            """.trimIndent()
        }

    /**
     * Lazily loaded [Properties] which contains the key "android_merged_assets" whose value is the
     * path to the directory that contains the `/assets` files.
     */
    private val properties = Properties().apply { config.openStream().use(block = ::load) }

    /**
     * The `assets` property is a [File] object that points to the merged assets directory.
     * It is initialized using the value of the "android_merged_assets" property from the
     * `properties` object.
     */
    private val assets = File(properties["android_merged_assets"].toString())

    /**
     * Opens an asset file as an [InputStream].
     *
     * @param fileName The name of the asset file to open.
     * @return An [InputStream] for reading the asset file.
     */
    override fun open(fileName: String): InputStream = File(assets, fileName).inputStream()
}
