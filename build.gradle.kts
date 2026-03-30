@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.20"
    id("jacoco")
    id("edu.sc.seis.launch4j") version "4.0.0"
//    id("dev.msfjarvis.tracelog") version "0.1.3"
}

group = "com.droidkfx.schwabtrader"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

configurations.all {
    resolutionStrategy {
        // Transitive dependency vulnerability from io.ktor:ktor-server-netty once that library
        // declares a newer version without the vulnerability, we can remove this.
        force("io.netty:netty-codec-http2:4.2.11.Final")
    }
}

dependencies {
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.13")
    implementation("ch.qos.logback:logback-classic:1.5.25")

    // Koin DI
    implementation("io.insert-koin:koin-core:4.1.1")

    // Swing Libraries
    implementation("com.formdev:flatlaf:3.6.1")

    // Kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")

    // Ktor
    implementation("io.ktor:ktor-client-core:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-client-java:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-server-core:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-server-netty:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-server-html-builder:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-server-content-negotiation:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${properties["ktor_version"]}")
    implementation("io.ktor:ktor-client-content-negotiation:${properties["ktor_version"]}")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:${properties["junit_version"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter:${properties["junit_version"]}")
    testImplementation("io.mockk:mockk:1.14.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.droidkfx.st.MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.build {
    dependsOn("jacocoTestReport")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    group = "Reporting"
    description = "Generate Jacoco coverage reports after running tests."
    reports {
        xml.required.set(true)
        html.required.set(true)
//        html.outputLocation.set(File("./reports/jacocoHtml"))
    }
    finalizedBy("jacocoTestCoverageVerification")
}

tasks.register<Copy>("releaseLocal") {
    val outputDir = ("${System.getenv("APPDATA")}\\..\\Local\\schwab-trader")

    dependsOn("createExe")
    from(tasks.createExe.get().outputs.files)
    into(outputDir)
    doLast {
        println("Released to: $outputDir")
    }
}

launch4j {
    outfile = "schwab-trader.exe"
    mainClassName = "com.droidkfx.st.MainKt"
    productName = "Schwab Trader"
    headerType = "gui"
    icon = "${projectDir}/src/main/resources/AppIcon.ico"
    chdir = "."
    jvmOptions = setOf("-Dlogback.configurationFile=logback-release.xml")
}

kotlin {
    jvmToolchain(21)
}