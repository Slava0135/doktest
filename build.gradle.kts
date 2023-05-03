plugins {
    kotlin("jvm") version "1.8.21"
    `java-gradle-plugin`
}

group = "com.github.slava0135"
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

kotlin {
    jvmToolchain(8)
}

gradlePlugin {
    plugins {
        create("doktest") {
            id = "com.github.slava0135.doktest"
            implementationClass = "doktest.DoktestPlugin"
        }
    }
}
