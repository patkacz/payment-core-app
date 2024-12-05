plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.flatpay.log"
    compileSdk = 34 // Set the compile SDK version

    defaultConfig {
        minSdk = 27 // Minimum SDK version
        targetSdk = 34 // Target SDK version
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" // Add your proguard rules file if needed
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Set for Java compatibility
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8" // Set to Java 8 compatibility for Kotlin
    }
}

dependencies {
    // Here you can add dependencies required by your log module.
    // For example, if your logging relies on any specific Android libraries:
    implementation(libs.androidx.core.ktx)
    // Add other dependencies for the log module as needed
}