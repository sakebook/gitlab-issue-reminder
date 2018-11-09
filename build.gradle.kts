import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.0"
    "kotlin-kapt"
    id("com.google.cloud.tools.jib") version "0.9.13"
}

group = "com.github.sakebook"
version = "0.0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.kittinunf.fuel", "fuel", "1.16.0")
    implementation("com.github.kittinunf.fuel", "fuel-moshi", "1.16.0")
    implementation("com.squareup.moshi", "moshi-kotlin", "1.7.0")
    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

jib {
    container {
        jvmFlags = listOf("-Dfile.encoding=UTF-8") // for JP
    }
}