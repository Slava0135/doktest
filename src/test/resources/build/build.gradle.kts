plugins {
    kotlin("jvm") version "1.8.20"
    id("com.github.slava0135.doktest")
}

group = "org.example"
version = "1.0-SNAPSHOT"

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

tasks.doktestTest {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
