plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id ("kotlin-parcelize")
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

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Location
    implementation("com.google.android.gms:play-services-location:21.1.0") {
        exclude(group = "com.google.android.gms", module = "play-services-location")
    }

    //Glide
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")

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
    implementation("org.osmdroid:osmdroid-android:6.1.11")
    implementation ("org.osmdroid:osmdroid-wms:6.1.16")
    implementation ("org.osmdroid:osmdroid-mapsforge:6.1.16")
//    implementation ("com.github.MKergall:osmbonuspack:6.9.0")
//    implementation ("org.osmdroid:osmdroid-geopackage:6.1.16")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")

    implementation ("androidx.compose.ui:ui-viewbinding:1.6.0")

    implementation("io.coil-kt:coil-compose:2.5.0")

    //workmanager
    implementation ("androidx.work:work-runtime-ktx:2.8.1")


    implementation ("androidx.compose.material3:material3:1.1.2")

    //Testing
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    testImplementation ("io.mockk:mockk:1.13.8")
    testImplementation ("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation ("androidx.test:core-ktx:1.5.0")
    testImplementation ("org.robolectric:robolectric:4.11.1")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    testImplementation ("org.hamcrest:hamcrest-library:2.2")

}