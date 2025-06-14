package com.google.samples.apps.nowinandroid

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

/**
 * Represents the different flavor dimensions used in the Now in Android app.
 * Flavor dimensions are used to categorize product flavors.
 */
@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

/**
 * Each flavor has a corresponding dimension.
 * The content for the app can either come from local static data which is useful for demo
 * purposes, or from a production backend server which supplies up-to-date, real content.
 * These two product flavors reflect this behaviour.
 */
@Suppress("EnumEntryName")
enum class NiaFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    demo(FlavorDimension.contentType, applicationIdSuffix = ".demo"),
    prod(FlavorDimension.contentType),
}

/**
 * Configures product flavors for the application.
 *
 * This function iterates through all defined `NiaFlavor`s and registers them as product flavors
 * within the provided `commonExtension`. It sets the dimension for each flavor based on
 * `niaFlavor.dimension.name`.
 *
 * It also applies a `flavorConfigurationBlock` to each flavor, allowing for further customization.
 *
 * If the `commonExtension` is an `ApplicationExtension` and the current flavor is an
 * `ApplicationProductFlavor`, and the `niaFlavor` has a defined `applicationIdSuffix`,
 * this suffix is applied to the flavor's `applicationIdSuffix`.
 *
 * @param commonExtension The common extension of the build type (e.g., application, library).
 * @param flavorConfigurationBlock A lambda block to configure each product flavor. It receives the
 * `ProductFlavor` and the corresponding `NiaFlavor` as parameters. Defaults to an empty block.
 */
fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: NiaFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension: FlavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            NiaFlavor.values().forEach { niaFlavor: NiaFlavor ->
                register(niaFlavor.name) {
                    dimension = niaFlavor.dimension.name
                    flavorConfigurationBlock(this, niaFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (niaFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = niaFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
