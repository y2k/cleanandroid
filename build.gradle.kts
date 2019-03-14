// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion = "1.3.21"
    extra["kotlinVersion"] = kotlinVersion
    setProperty("kotlinVersion", kotlinVersion)

    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.3.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://dl.bintray.com/indrih/maven/")
        maven("https://plugins.gradle.org/m2/")
    }
}

val clean by tasks.creating(Delete::class) {
    delete = setOf(rootProject.buildDir)
}