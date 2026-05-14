plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.lostandfound"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lostandfound"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    // Google Maps SDK
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    // Places SDK (Autocomplete)
    implementation("com.google.android.libraries.places:places:3.3.0")
    // Fused Location Provider (GPS / current location)
    implementation("com.google.android.gms:play-services-location:21.2.0")
}
