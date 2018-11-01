import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.0"
}

group = "net.jxpress"
version = "0.0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("com.github.kittinunf.fuel", "fuel", "1.16.0")
    compile("com.github.kittinunf.fuel", "fuel-moshi", "1.16.0")
    compile("com.squareup.moshi", "moshi-kotlin", "1.7.0")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}