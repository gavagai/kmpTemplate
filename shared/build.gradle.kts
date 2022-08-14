plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

// DON: --
val essentyVersion = "0.5.2"
val mviKotlinVersion = "3.0.0-beta01"
// DON: --
kotlin {
    android()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"

            // DON: --
            export("com.arkivanov.essenty:lifecycle:$essentyVersion")
            export("com.arkivanov.mvikotlin:mvikotlin:$mviKotlinVersion")
            // DON: --
        }
    }

// DON: --
    val ktorVersion = "2.0.2"
    val koinVersion = "3.2.0"
    val decomposeVersion = "1.0.0-alpha-02"
// DON: --
    sourceSets {
        val commonMain by getting {
            // DON: --
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

                // Kotlin datetime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.3")

                // Ktor client
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                // Koin core - https://insert-koin.io/docs/reference/koin-mp/kmp
                implementation("io.insert-koin:koin-core:$koinVersion")

                // MVIKotlin - https://github.com/arkivanov/MVIKotlin#readme
                api("com.arkivanov.mvikotlin:mvikotlin:$mviKotlinVersion")
                api("com.arkivanov.mvikotlin:mvikotlin-main:$mviKotlinVersion")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$mviKotlinVersion")
                implementation("com.arkivanov.mvikotlin:mvikotlin-logging:$mviKotlinVersion")
                // Decompose
                implementation("com.arkivanov.decompose:decompose:$decomposeVersion")
                implementation("com.arkivanov.decompose:extensions-compose-jetpack:$decomposeVersion")
            }
            // DON: --
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            // DON: --
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
                implementation("androidx.compose.runtime:runtime:1.2.0") // Compose compiler dependency
            }
            // DON: --
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            // DON: --
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
                api("com.arkivanov.essenty:lifecycle:$essentyVersion")
                api("com.arkivanov.mvikotlin:mvikotlin:$mviKotlinVersion")
            }
            // DON: --
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
}