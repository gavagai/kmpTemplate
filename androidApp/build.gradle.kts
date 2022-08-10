plugins {
    id("com.android.application")
    kotlin("android")
}

val composeVersion = "1.3.0" // DON
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
        kotlinCompilerExtensionVersion = composeVersion
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // DON: --
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")

    // DON: --
    implementation("androidx.compose.runtime:runtime:1.2.0") // DON: upgrade to compose version soon

    // DON: --
}