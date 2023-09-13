plugins {
  id("java-library")
  id("org.jetbrains.kotlin.jvm")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

val retrofitVersion = "2.9.0"
val daggerVersion = "2.48"

dependencies {

  // Retrofit2
  implementation ("com.squareup.retrofit2:retrofit:$retrofitVersion")
  implementation ("com.squareup.retrofit2:converter-gson:$retrofitVersion")
  implementation ("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")

  // Dagger 2
  implementation ("com.google.dagger:dagger:$daggerVersion")
  annotationProcessor ("com.google.dagger:dagger-compiler:$daggerVersion")
  implementation ("com.squareup.okhttp3:logging-interceptor:4.4.0")
}