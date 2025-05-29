plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.proyectoDam.easyfinances"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.proyectoDam.easyfinances"
        minSdk = 24
        targetSdk = 35

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = false

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    viewBinding{
        enable =true
    }
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }






}

dependencies {


    implementation ("org.apache.poi:poi:3.17")
    implementation("org.apache.poi:poi-ooxml:3.17") {
        exclude(group = "org.apache.xmlbeans", module = "xmlbeans")
    }




    //GRAFICOS
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


    implementation ("com.prolificinteractive:material-calendarview:1.4.3")





    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.4.0")

// Firebase Firestore
    implementation ("com.google.firebase:firebase-firestore-ktx:25.1.3")


    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:23.2.0") // última versión de Firebase Auth
    implementation ("com.google.firebase:firebase-core:21.1.1")


    implementation ("androidx.appcompat:appcompat:1.4.1") // o la última versión disponible
        // otras dependencias




    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}




