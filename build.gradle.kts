import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(kotlin("test"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {

    wrapper {
        gradleVersion = "8.5"
    }

    withType<JavaCompile> {
        enabled = false
        options.release = 21
    }

    withType<KotlinCompile> {
        compilerOptions.jvmTarget = JVM_21
    }

    test {
        useJUnitPlatform()

        systemProperty("junit.jupiter.execution.parallel.enabled", true)
        systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    }

}
