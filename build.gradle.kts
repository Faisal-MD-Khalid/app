buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        //classpath("com.apollographql.apollo:apollo-gradle-plugin:2.5.9" )

    // Updated version of google-services plugin
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
}
