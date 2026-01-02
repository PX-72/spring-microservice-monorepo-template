plugins {
    id("protobuf-conventions")
}

val grpcSpringBootVersion = "3.1.0.RELEASE"

dependencies {
    api(projects.services.greetingService.domain)

    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // gRPC
    implementation("net.devh:grpc-spring-boot-starter:$grpcSpringBootVersion")
    implementation("io.grpc:grpc-netty-shaded:1.68.0")
    implementation("io.grpc:grpc-protobuf:1.68.0")
    implementation("io.grpc:grpc-stub:1.68.0")
    implementation("com.google.protobuf:protobuf-java:4.28.2")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // Observability
    implementation("io.micrometer:micrometer-core")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    main {
        proto {
            srcDir("src/main/proto")
        }
    }
}
