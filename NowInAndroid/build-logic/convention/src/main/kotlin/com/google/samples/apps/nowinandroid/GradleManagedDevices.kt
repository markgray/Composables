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

package com.google.samples.apps.nowinandroid

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke

/**
 * Configures Gradle Managed Devices for the given [CommonExtension].
 *
 * This function creates three Gradle Managed Devices:
 *  - "Pixel 4" with API level 30 and "aosp-atd" system image.
 *  - "Pixel 6" with API level 31 and "aosp" system image.
 *  - "Pixel C" with API level 30 and "aosp-atd" system image.
 *
 * It also creates a device group named "ci" that includes the "Pixel 4" and "Pixel C" devices.
 *
 * @param commonExtension The [CommonExtension] to configure.
 * @see <https://developer.android.com/studio/test/gradle-managed-devices>
 */
internal fun configureGradleManagedDevices(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    val pixel4 = DeviceConfig("Pixel 4", 30, "aosp-atd")
    val pixel6 = DeviceConfig("Pixel 6", 31, "aosp")
    val pixelC = DeviceConfig("Pixel C", 30, "aosp-atd")

    val everyDevice: List<DeviceConfig> = listOf(pixel4, pixel6, pixelC)
    val ciDevices: List<DeviceConfig> = listOf(pixel4, pixelC)

    commonExtension.testOptions {
        @Suppress("UnstableApiUsage")
        managedDevices {
            allDevices {
                everyDevice.forEach { deviceConfig: DeviceConfig ->
                    maybeCreate(deviceConfig.taskName, ManagedVirtualDevice::class.java).apply {
                        device = deviceConfig.device
                        apiLevel = deviceConfig.apiLevel
                        systemImageSource = deviceConfig.systemImageSource
                    }
                }
            }
            groups {
                maybeCreate("ci").apply {
                    ciDevices.forEach { deviceConfig ->
                        targetDevices.add(allDevices[deviceConfig.taskName])
                    }
                }
            }
        }
    }
}

/**
 * Configuration for a Gradle Managed Device.
 *
 * @property device The name of the device. This should correspond to a device name in the Android SDK.
 * @property apiLevel The API level of the system image to use for the device.
 * @property systemImageSource The source of the system image to use for the device. Common values
 * include "aosp", "google", "aosp-atd", and "google-atd".
 * @property taskName A unique name for the Gradle task associated with this device configuration.
 * This is generated automatically based on the device name, API level, and system image source.
 */
private data class DeviceConfig(
    val device: String,
    val apiLevel: Int,
    val systemImageSource: String,
) {
    val taskName = buildString {
        append(device.lowercase().replace(" ", ""))
        append("api")
        append(apiLevel.toString())
        append(systemImageSource.replace("-", ""))
    }
}
