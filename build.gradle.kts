// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false

}
buildscript {
    repositories {
        google() //
        mavenCentral()
    }
    dependencies {
        classpath ("com.google.gms:google-services:4.3.15") //
        // Autres classpath si nécessaire
    }
}
