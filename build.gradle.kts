plugins {
    id("com.android.application") version "8.4.0" apply false

    val kotlinVersion = "1.9.22"
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("org.jetbrains.kotlin.kapt") version kotlinVersion apply false

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false

    id("androidx.navigation.safeargs.kotlin") version "2.7.6" apply false

    // JUnit 5
    id("de.mannodermaus.android-junit5") version "1.10.0.0" apply false

    // Hilt
    id("com.google.dagger.hilt.android") version "2.50" apply false
}
