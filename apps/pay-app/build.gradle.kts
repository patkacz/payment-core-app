plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.flatpay.pay_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.flatpay.pay_app"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    implementation(project(":log"))
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(project(":common"))
    implementation(libs.androidx.storage)
    implementation(libs.androidx.navigation.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.0.5") // Compose UI
    implementation("androidx.compose.material:material:1.0.5") // Material Design
    implementation("androidx.activity:activity-compose:1.4.0") // Activity Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1") // ViewModel for Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("com.airbnb.android:lottie-compose:4.2.2")
    implementation("io.coil-kt:coil-compose:2.4.0")

}

