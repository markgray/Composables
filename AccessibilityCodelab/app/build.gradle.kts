import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.jetnews"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
    signingConfigs {
        // We use a bundled debug keystore, to allow debug builds from CI to be upgradable
        getByName("debug") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    buildFeatures {
        compose = true
    }
    composeCompiler {
        // strong skipping is now default
    }
    packaging {
        resources {
            excludes += listOf("/META-INF/AL2.0", "/META-INF/LGPL2.1")
        }
    }
    namespace = "com.example.jetnews"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    implementation("androidx.compose.runtime:runtime:1.8.3")
    implementation("androidx.compose.ui:ui:1.8.3")
    implementation("androidx.compose.foundation:foundation-layout:1.8.3")
    implementation("androidx.compose.material:material:1.8.3")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.compose.foundation:foundation:1.8.3")
    implementation("androidx.compose.animation:animation:1.8.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.8.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.8.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.8.3")

    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")
    implementation("com.google.accompanist:accompanist-insets:0.30.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.navigation:navigation-compose:2.9.2")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.9.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")

    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.compose.ui:ui-test:1.8.3")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.8.3")

    debugImplementation("androidx.compose.ui:ui-test-manifest:1.8.3")

    // Robolectric dependencies
    testImplementation("androidx.compose.ui:ui-test-junit4:1.8.3")
    testImplementation("org.robolectric:robolectric:4.15.1")
}
