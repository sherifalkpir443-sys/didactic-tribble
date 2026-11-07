plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val novaApiBaseUrl = providers.gradleProperty("NOVA_API_BASE_URL").orElse("").get()

android {
    namespace = "com.nova.ai"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.nova.ai"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "0.2.0"
        buildConfigField("String", "NOVA_API_BASE_URL", "\"$novaApiBaseUrl\"")
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
