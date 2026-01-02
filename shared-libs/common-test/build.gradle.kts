plugins {
    id("java-library-conventions")
    id("io.spring.dependency-management")
}

val springBootVersion = "3.5.0"
val testcontainersVersion = "2.0.3"

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
        mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
    }
}

dependencies {
    api("org.junit.jupiter:junit-jupiter:5.12.2")
    api("org.testcontainers:testcontainers")
    api("org.testcontainers:junit-jupiter")
    api("org.testcontainers:postgresql")
    api("org.testcontainers:kafka")
    api("org.springframework.boot:spring-boot-starter-test")
}

// Shared test utilities
// Examples: PostgresContainerExtension, KafkaContainerExtension, TestDataBuilder
