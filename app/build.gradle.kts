plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.forecanow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.forecanow"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    //retrofit
//    implementation("com.squareup.retrofit2:retrofit:2.11.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
//    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
//
//    //location
//    implementation("com.google.android.gms:play-services-location:21.1.0") {
//        exclude(group = "org.osmdroid", module = "osmdroid-android")
//    }
//
//
//    val nav_version = "2.8.8"
//    implementation("androidx.navigation:navigation-compose:$nav_version")
//    //Serialization for NavArgs
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
//
//
//    //Room
//    implementation ("androidx.room:room-ktx:2.6.1")
//    implementation ("androidx.room:room-runtime:2.6.1")
//    ksp("androidx.room:room-compiler:2.6.1")
//
//
//    //ViewModel & livedata
//    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
//    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
//    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
//
//    val compose_version = "1.0.0"
//    implementation ("androidx.compose.runtime:runtime-livedata:$compose_version")
//    //Scoped API
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
//
//
//
//    //open street map
//    implementation ("org.osmdroid:osmdroid-android:6.1.16")
//    implementation ("org.osmdroid:osmdroid-mapsforge:6.1.16")
//    implementation ("org.osmdroid:osmdroid-geopackage:6.1.16")
//
//
//
//
//}

// Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Location - أضف exclude بشكل صحيح
    implementation("com.google.android.gms:play-services-location:21.1.0") {
        exclude(group = "com.google.android.gms", module = "play-services-location")
    }

    //Glide
    implementation ("com.github.bumptech.glide:compose:1.0.0-alpha.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.8")

    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.7")

    // OSMDroid
    implementation("org.osmdroid:osmdroid-android:6.1.16") {
        exclude(group = "com.google.android.gms", module = "play-services-location")
    }

    implementation ("org.osmdroid:osmdroid-mapsforge:6.1.16")

    implementation("io.coil-kt:coil-compose:2.5.0")

}