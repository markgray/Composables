/*
 * Copyright 2018 Google LLC
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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
  alias(libs.plugins.compose.compiler)
}

android {
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "com.google.samples.apps.sunflower"
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    testInstrumentationRunner = "com.google.samples.apps.sunflower.utilities.MainTestRunner"
    versionCode = 1
    versionName = "0.1.6"
    vectorDrawables.useSupportLibrary = true

    // Consult the README on instructions for setting up Unsplash API key
    buildConfigField("String", "UNSPLASH_ACCESS_KEY", "\"" + getUnsplashAccess() + "\"")
    javaCompileOptions {
      annotationProcessorOptions {
        arguments["dagger.hilt.disableModulesHaveInstallInCheck"] = "true"
      }
    }
  }
  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
    }
    create("benchmark") {
      initWith(getByName("release"))
      signingConfig = signingConfigs.getByName("debug")
      isDebuggable = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules-benchmark.pro"
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
    compose = true
    dataBinding = true
    buildConfig = true
  }

  packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
  }

  namespace = "com.google.samples.apps.sunflower"
}

androidComponents {
  onVariants(selector().withBuildType("release")) {
    // Only exclude *.version files in release mode as debug mode requires
    // these files for layout inspector to work.
    it.packaging.resources.excludes.add("META-INF/*.version")
  }
}

dependencies {
  ksp(libs.androidx.room.compiler)
  ksp(libs.hilt.android.compiler)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.livedata.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.paging.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.material)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.gson)
  implementation(libs.okhttp3.logging.interceptor)
  implementation(libs.retrofit2.converter.gson)
  implementation(libs.retrofit2)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.hilt.android)
  implementation(libs.hilt.navigation.compose)
  implementation(libs.androidx.profileinstaller)

  // Compose
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.constraintlayout.compose)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.viewbinding)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.runtime.livedata)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.glide)
  implementation(libs.glide.compose)
  implementation(libs.accompanist.systemuicontroller)
  debugImplementation(libs.androidx.compose.ui.tooling)

  // Testing dependencies
  debugImplementation(libs.androidx.monitor)
  kspAndroidTest(libs.hilt.android.compiler)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.arch.core.testing)
  androidTestImplementation(libs.androidx.espresso.contrib)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.espresso.intents)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.uiautomator)
  androidTestImplementation(libs.androidx.work.testing)
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.guava)
  androidTestImplementation(libs.hilt.android.testing)
  androidTestImplementation(libs.accessibility.test.framework)
  androidTestImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.junit)
}

fun getUnsplashAccess(): String? {
  return project.findProperty("unsplash_access_key") as? String
}

