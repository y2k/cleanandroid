plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(28)
        versionName = "1.0.0"
        versionCode = 1
        testInstrumentationRunner = "android.support.test.runner.AndroidJEmptyResultRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

val navVersion = "1.0.0"
val coroutineVersion = "1.1.1"
val ankoVersion = "0.10.8"

dependencies {
    val kotlinVersion = property("kotlinVersion") as String
    implementation(fileTree("include" to "*.jar", "dir" to "libs"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    // Navigation
    implementation("android.arch.navigation:navigation-fragment:$navVersion")
    implementation("android.arch.navigation:navigation-ui:$navVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")

    // Anko
    implementation("org.jetbrains.anko:anko:$ankoVersion")
    implementation("org.jetbrains.anko:anko-appcompat-v7:$ankoVersion")

    // Тесты
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
}