plugins {
    alias(libs.plugins.android.application)
    //alias(libs.plugins.google.gms.google.services)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.echo_app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.echo_app"
        minSdk = 24
        targetSdk = 36
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

    // --- Android Lint Configuration ---
    lint {
        abortOnError = true
        warningsAsErrors = true
        checkReleaseBuilds = true
        disable += "MissingTranslation"
    }
}

dependencies {

    // --- AndroidX Core ---
    implementation(libs.androidx.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // --- Lifecycle (ViewModel + LiveData) ---
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)

    // --- Firebase (via BOM for version management) ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)

    // --- WorkManager (Background tasks) ---
    implementation(libs.work.runtime)

    // --- Navigation Component ---
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // --- Glide (Image loading & caching) ---
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}