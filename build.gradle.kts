import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "A library for interacting with blocking JDBC drivers using Kotlin Coroutines."

plugins {
    maven
    kotlin("jvm") version "1.4.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("io.mockk:mockk:1.10.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xuse-experimental=kotlin.contracts.ExperimentalContracts")
    }
}

tasks.withType<Test> {
    failFast = true
    useJUnitPlatform()
}
