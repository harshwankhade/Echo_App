// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    //alias(libs.plugins.google.gms.google.services) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.diffplug.spotless") version "6.25.0"
}

// --- Spotless Configuration for Code Formatting ---
spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat("1.17.0")
        trimTrailingWhitespace()
        endWithNewline()
    }

    format("misc") {
        target("*.gradle.kts", "*.md", ".gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// --- Custom CI Tasks ---
tasks.register("ciChecks") {
    group = "verification"
    description = "Runs all code quality checks for CI pipeline (lint, formatting, tests)"
    dependsOn("spotlessCheck", "lint")
}

tasks.register("ciFormat") {
    group = "formatting"
    description = "Applies code formatting and fixes style issues"
    dependsOn("spotlessApply")
}

tasks.register("ciInfo") {
    group = "help"
    description = "Display available CI tasks"
    doLast {
        println("""
            |
            |╔════════════════════════════════════════════════════════════════╗
            |║                    ECHOAPP CI/CD TASKS                         ║
            |╚════════════════════════════════════════════════════════════════╝
            |
            |Code Quality Checks:
            |  ./gradlew ciChecks         - Run all code quality checks
            |  ./gradlew spotlessCheck    - Check code formatting
            |  ./gradlew spotlessApply    - Apply code formatting
            |  ./gradlew lint             - Run Android Lint analysis
            |
            |Code Formatting:
            |  ./gradlew ciFormat         - Apply code formatting fixes
            |
            |Help:
            |  ./gradlew ciInfo           - Display this help message
            |  ./gradlew tasks            - List all available Gradle tasks
            |
            |CI Pipeline:
            |  Local: ./gradlew ciChecks
            |  GitHub Actions: Configured in .github/workflows/android-ci.yml
            |
            |""".trimMargin())
    }
}

