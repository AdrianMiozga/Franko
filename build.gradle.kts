plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.5.3" apply false

    // JUnit 5
    id("de.mannodermaus.android-junit5") version "1.8.2.1" apply false

    // Hilt
    id("com.google.dagger.hilt.android") version "2.45" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
