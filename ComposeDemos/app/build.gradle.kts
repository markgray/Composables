plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "androidx.demo.composedemos"
    compileSdk = 35
    defaultConfig {
        applicationId = "androidx.demo.composedemos"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeCompiler {
        // enableStrongSkippingMode = true is now default
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "androidx.demo.composedemos"
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui:1.8.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.8.1")
    implementation("androidx.compose.material:material:1.8.1")
    //noinspection GradleDependency TODO: 1.1.0 breaks compilation, keep an eye on this.
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    //noinspection GradleDependency TODO: 2.2.0 breaks compilation, keep an eye on this.
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.8.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.8.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.8.1")
}
