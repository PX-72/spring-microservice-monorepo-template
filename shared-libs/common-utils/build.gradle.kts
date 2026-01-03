plugins {
    id("java-library-conventions")
}

dependencies {
    implementation(libs.slf4j.api)
}

// Utility classes - minimal dependencies
// Examples: UuidGenerator, JsonUtils, DateTimeUtils
