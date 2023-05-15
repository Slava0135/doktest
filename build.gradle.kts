@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version "1.8.21"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.0"
}

group = "io.github.slava0135"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

gradlePlugin {
    group = "io.github.slava0135"
    website.set("https://github.com/Slava0135/doktest")
    vcsUrl.set("https://github.com/Slava0135/doktest.git")
    plugins {
        create("doktest") {
            id = "io.github.slava0135.doktest"
            displayName = "Plugin for testing code in KDoc comments"
            description = "A plugin that helps you verify Kotlin code in KDoc comments"
            tags.set(listOf("kotlin", "testing", "documentation", "docs"))
            implementationClass = "io.github.slava0135.doktest.DoktestPlugin"
        }
    }
}
