plugins {
    id("common-java")
    id("org.springframework.boot")
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

// Unit tests (exclude integration tests)
tasks.test {
    useJUnitPlatform()
    include("**/*Test.class")
    exclude("**/*IT.class")
}

// Integration tests
val integrationTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"
    useJUnitPlatform()
    include("**/*IT.class")
    shouldRunAfter(tasks.test)
}

tasks.check {
    dependsOn(integrationTest)
}
