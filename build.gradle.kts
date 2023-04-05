import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(kotlin("test"))
}

tasks {

    wrapper {
        gradleVersion = "8.0.2"
    }

    withType<JavaCompile> {
        enabled = false
        options.release.set(17)
    }

    withType<KotlinCompile> {
        compilerOptions.jvmTarget.set(JVM_17)
    }

    test {
        useJUnitPlatform()
    }

}
