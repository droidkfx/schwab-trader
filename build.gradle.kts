plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "com.droidkfx.games.snake"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.6.1")

    // Kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Ktor
    implementation("io.ktor:ktor-client-core:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-client-java:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-server-core:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-server-netty:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-server-html-builder:${properties["ktor_version"]}")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}