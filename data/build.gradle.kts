plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
}

android {
  namespace = "info.sergeikolinichenko.mygithubrepos"
  compileSdk = 34

  defaultConfig {
    minSdk = 26

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

val retrofitVersion = "2.9.0"
val daggerVersion = "2.48"

dependencies {
  implementation(project(":domain"))

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

  // Retrofit2
  implementation ("com.squareup.retrofit2:retrofit:$retrofitVersion")
  implementation ("com.squareup.retrofit2:converter-gson:$retrofitVersion")
  implementation ("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")

  // Dagger 2
  implementation ("com.google.dagger:dagger:$daggerVersion")
  kapt ("com.google.dagger:dagger-compiler:$daggerVersion")
}