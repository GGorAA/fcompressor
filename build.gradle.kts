import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.30"
    application
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "me.ggoraa"
version = "2.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")

}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

dependencies {
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.7")
    implementation("org.apache.logging.log4j:log4j-core:2.7")
    implementation("com.danielflower.apprunner:javasysmon:0.3.5.0")
    implementation("me.tongfei:progressbar:0.9.0")
    implementation("dev.reimer:progressbar-ktx:0.1.0")
    implementation("commons-io:commons-io:2.8.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "me.ggoraa.fcompressor.MainKt"
}