plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
   // id("com.apollographql.apollo") version "3.7.0"
    // Ensure this version matches with the runtime
   // id("org.jetbrains.kotlin.android") version "1.9.0"
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        mlModelBinding = true
    }
}

/*apollo {
    service("countries") {
        sourceFolder.set("com/example/myapplication")
        packageName.set("com.example.myapplication.graphql")
        schemaFile.set(file("src/main/graphql/com/example/myapplication/schema.graphqls"))
        introspection {
            endpointUrl.set("https://countries.trevorblades.com/graphql")
        }
    }
}*/

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // FirebaseUI dependencies
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation("com.firebaseui:firebase-ui-storage:8.0.2")

    // ML & TensorFlow
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")

    // Google Play Services dependencies
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Utility libraries
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.karumi:dexter:6.2.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // MPAndroidChart for charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Apollo GraphQL runtime (updated to version 3.7.0)
    //implementation("com.apollographql.apollo:apollo-runtime:3.7.0")
    //implementation("com.apollographql.apollo:apollo-coroutines-support:3.7.0") // If you plan to use coroutines

    // Kotlin Coroutines
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
   // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
