import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
}

android {
    compileSdk = 36
    defaultConfig {
        applicationId = "com.google.samples.apps.sunflower"
        minSdk = 23
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionCode = 1
        versionName = "0.1.6"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
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
        dataBinding = true
        compose = true
    }
    composeCompiler {
        // enableStrongSkippingMode = true is now default
    }
    packaging {
        resources {
            excludes += "/META-INF/AL2.0"
            excludes += "/META-INF/LGPL2.1"
        }
    }
    namespace = "com.google.samples.apps.sunflower"
}

dependencies {
    ksp("androidx.room:room-compiler:2.8.1")
    ksp("com.github.bumptech.glide:compiler:5.0.5")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.5")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.room:room-runtime:2.8.1")
    implementation("androidx.room:room-ktx:2.8.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.10.5")
    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")

    // Compose
    implementation("androidx.compose.runtime:runtime:1.9.2")
    implementation("androidx.compose.ui:ui:1.9.2")
    implementation("androidx.compose.foundation:foundation:1.9.2")
    implementation("androidx.compose.foundation:foundation-layout:1.9.2")
    implementation("androidx.compose.material:material:1.9.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.9.2")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.9.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.9.2")
    implementation("androidx.compose.material3:material3:1.4.0")

    // Testing dependencies
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")
    androidTestImplementation("androidx.work:work-testing:2.10.5")
    androidTestImplementation("com.google.truth:truth:1.4.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.9.2")
    testImplementation("junit:junit:4.13.2")
}
