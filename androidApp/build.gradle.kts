plugins {
    id("com.android.application")
    kotlin("android")
    //DON: --
    id("kotlin-parcelize")
    // DON: --
}

val composeCompilerVersion = "1.3.0" // DON
android {
    compileSdk = 33 // DON: was 32
    defaultConfig {
        applicationId = "com.teddyfreddy.kmp.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    // DON: --
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // DON: --
}

// DON: --
val material3Version = "1.0.0-alpha15"
val koinVersion = "3.2.0"
val composeLibraryVersion = "1.2.0"
val mviKotlinVersion = "3.0.0-beta01"
val essentyVersion = "0.5.2"
val decomposeVersion = "1.0.0-alpha-02"
// DON: --
dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")

    // DON: --
    implementation("androidx.compose.runtime:runtime:1.2.1") // DON: Compose compiler dependency (upgrade soon)

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.compose.ui:ui:$composeLibraryVersion")

    // Material Design
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")
    implementation("androidx.compose.material:material-icons-core:$composeLibraryVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeLibraryVersion")
    implementation("androidx.window:window:1.0.0") // Adaptive design

    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$composeLibraryVersion")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:$composeLibraryVersion")

    // Koin core - https://insert-koin.io/docs/reference/koin-mp/kmp
    implementation("io.insert-koin:koin-core:$koinVersion")
    // Koin main features for Android - https://insert-koin.io/docs/reference/koin-android/start
    implementation("io.insert-koin:koin-android:$koinVersion")
    // Koin for Jetpack Compose - https://insert-koin.io/docs/reference/koin-android/compose
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")

    // Essenty lifecycle (for MVIKotlin)
    implementation("com.arkivanov.essenty:lifecycle:$essentyVersion")
    // Decompose for Compose navigation, MVI sore lifecycle etc
    implementation("com.arkivanov.decompose:decompose:$decomposeVersion")
    implementation("com.arkivanov.decompose:extensions-compose-jetpack:$decomposeVersion")
    // MVIKotlin
    implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$mviKotlinVersion")
    // DON: --
}