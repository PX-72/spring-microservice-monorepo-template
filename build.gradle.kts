plugins {
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
}

allprojects {
    group = "com.example.monorepo"
    version = "0.1.0-SNAPSHOT"
}

spotless {
    format("misc") {
        target("**/*.md", "**/*.yml", "**/*.yaml")
        trimTrailingWhitespace()
        endWithNewline()
    }
    java {
        target("**/src/**/*.java")
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
