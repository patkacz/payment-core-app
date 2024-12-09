plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.flatpay.log"
    compileSdk = 35 // Set the compile SDK version

    defaultConfig {
        minSdk = 27 // Minimum SDK version
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
        sourceCompatibility = JavaVersion.VERSION_17 // Set for Java compatibility
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Here you can add dependencies required by your log module.
    // For example, if your logging relies on any specific Android libraries:
    implementation(libs.androidx.core.ktx)
    // Add other dependencies for the log module as needed
}