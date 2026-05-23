import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialze)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.proto)
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

    // Allow references to generated code
    kapt {
        correctErrorTypes = true
    }

    //PROTO PLUGIN
    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.24.1"
        }

        generateProtoTasks {
            all().forEach { task ->
                task.builtins {
                    id("java") {
                        option("lite")
                    }
                    id("kotlin") {
                        option("lite")
                    }
                }
            }
        }
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
    implementation("io.ktor:ktor-client-okhttp:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Logging (optional)
    implementation("io.ktor:ktor-client-logging:2.3.8")

    // ROOM DEPENDENCY
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // Fix for "No native library found" error in KAPT on some Windows environments
    kapt("org.xerial:sqlite-jdbc:3.45.1.0")

    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-paging:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")

    // PROTO DEPENDENCIES
    implementation("androidx.datastore:datastore:1.1.6")
    implementation("androidx.datastore:datastore-preferences:1.1.6")
    implementation("com.google.protobuf:protobuf-javalite:4.26.1")
    implementation("com.google.protobuf:protobuf-kotlin-lite:4.26.1")
}