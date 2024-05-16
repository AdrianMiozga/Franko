import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")

    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")

    id("androidx.navigation.safeargs.kotlin")

    // JUnit 5
    id("de.mannodermaus.android-junit5")

    // Hilt
    id("com.google.dagger.hilt.android")
}

val keystoreProperties = Properties()
val keystoreFile = rootProject.file("keystore.properties") as File

if (keystoreFile.exists()) {
    keystoreProperties.load(FileInputStream(keystoreFile))
}

android {
    namespace = "org.wentura.franko"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.wentura.franko"
        minSdk = 21
        targetSdk = 34
        versionCode = 7
        versionName = "0.1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (keystoreFile.exists()) {
        signingConfigs {
            create("release") {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }

        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

secrets {
    propertiesFileName = "secrets.properties"
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    implementation("com.firebaseui:firebase-ui-auth:8.0.2")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Navigation Framework
    val navigationVersion = "2.7.6"
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    // Preferences
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Coil
    implementation("io.coil-kt:coil:2.5.0")

    // Image Compression
    implementation("id.zelory:compressor:3.0.1")

    // Hilt
    val hiltVersion = "2.50"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

kapt {
    correctErrorTypes = true
}
