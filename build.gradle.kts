import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(kotlin("test"))
}

tasks {

    wrapper {
        gradleVersion = "7.6"
    }

    withType<JavaCompile> {
        enabled = false
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    test {
        useJUnitPlatform()
    }

}
