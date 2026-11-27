import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// CI passes these (version from scripts/next-version.sh). Local builds use the defaults.
val appVersionName = (findProperty("versionName") as String?) ?: "0.1.0"
val appVersionCode = (findProperty("versionCode") as String?)?.toInt() ?: 1

// Google Maps API key. Never commit it: CI reads the MAPS_API_KEY repository secret, local
// builds read MAPS_API_KEY from local.properties (which git ignores). Without a key the app
// still builds, and the map screen says the key is missing.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val mapsApiKey: String = System.getenv("MAPS_API_KEY")?.takeIf { it.isNotBlank() }
    ?: localProperties.getProperty("MAPS_API_KEY")?.takeIf { it.isNotBlank() }
    ?: ""

android {
    namespace = "io.github.amandhakar.pinpoint"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.amandhakar.pinpoint"
        minSdk = 26
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName

        manifestPlaceholders["mapsApiKey"] = mapsApiKey
        buildConfigField("boolean", "HAS_MAPS_KEY", mapsApiKey.isNotEmpty().toString())
    }

    signingConfigs {
        // Set by the Release workflow from repository secrets. Every release must be signed
        // with the same key, otherwise Android refuses to install it as an update.
        val keystore = System.getenv("KEYSTORE_FILE")
        if (keystore != null) {
            create("release") {
                storeFile = file(keystore)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // No fallback to the debug key: without the secrets the APK stays unsigned, so a
            // debug-signed build can never be published by accident.
            signingConfig = signingConfigs.findByName("release")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.06.01")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")
    implementation("androidx.core:core-ktx:1.16.0")

    // Google Maps for Compose. Brings in play-services-maps.
    implementation("com.google.maps.android:maps-compose:6.4.1")

    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation("junit:junit:4.13.2")
}
