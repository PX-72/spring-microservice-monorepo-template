plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // Plugin dependencies - allows convention plugins to apply these
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.5.0")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.6")
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.9.4")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
}
