rootProject.name = "advent-of-code-2022"

plugins {
    id("com.gradle.enterprise") version ("3.14.1")
}

gradleEnterprise {
    buildScan {
        if (System.getenv("CI") == "true") {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
