// Top-level build file where you can add configuration options common to all sub-projects/modules.
allprojects {
    repositories {
        google() // Necesario para las dependencias de Firebase
        mavenCentral() // Dependencias comunes

        maven("https://jitpack.io")



    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false







}