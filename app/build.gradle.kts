import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "indrih.cleandemo"

        minSdkVersion(21)
        targetSdkVersion(28)

        versionName = "1.0.0"
        versionCode = 1

        buildToolsVersion = "29.0.0-rc1"
        testInstrumentationRunner = "android.support.test.runner.AndroidJEmptyResultRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

val supportAppcompatVersion = "1.0.2"
val supportVersion = "1.0.0"
val navVersion = "1.0.0"
val coroutineVersion = "1.1.1"
val koinVersion = "1.0.2"
val ankoVersion = "0.10.8"

dependencies {
    val kotlinVersion = property("kotlinVersion") as String
    implementation(fileTree("include" to "*.jar", "dir" to "libs"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    // KTX
    implementation("androidx.core:core-ktx:1.0.1")

    // AndroidX
    implementation("androidx.appcompat:appcompat:$supportAppcompatVersion")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-alpha3")
    implementation("androidx.cardview:cardview:$supportVersion")
    implementation("androidx.recyclerview:recyclerview:$supportVersion")
    implementation("androidx.exifinterface:exifinterface:$supportVersion")

    // Navigation
    implementation("android.arch.navigation:navigation-fragment:$navVersion")
    implementation("android.arch.navigation:navigation-ui:$navVersion")

    // Gson + Kotlin
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")

    // DI
    implementation("org.koin:koin-core:$koinVersion")
    implementation("org.koin:koin-android-scope:$koinVersion")
    implementation("org.koin:koin-androidx-scope:$koinVersion")

    // Anko
    implementation("org.jetbrains.anko:anko:$ankoVersion")
    implementation("org.jetbrains.anko:anko-appcompat-v7:$ankoVersion")

    // Clean Android library
    implementation(project(":cleanandroid"))

    // Тесты
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
}