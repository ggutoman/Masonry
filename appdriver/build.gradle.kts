plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    //kotlin serializer
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"

    //KOTLIN KAPT METHOD
    id("kotlin-kapt")

    //PARCELIZE METHOD
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "org.gag.appdriver"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Ktor Client
    implementation("io.ktor:ktor-client-core:2.3.8")
    implementation("io.ktor:ktor-client-okhttp:2.3.8") // Android engine
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Logging (optional)
    implementation("io.ktor:ktor-client-logging:2.3.8")

    //ROOM DEPENDENCY
    val room_version = "2.7.1"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    //ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")
    //kapt("androidx.room:room-compiler:$room_version")
    //kapt("android.arch.lifecycle:compiler:1.1.0")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    //kapt("android.arch.persistence.room:compiler:1.1.0")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")
}