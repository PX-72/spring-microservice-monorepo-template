plugins {
    id("protobuf-conventions")
}

dependencies {
    api(projects.services.greetingService.domain)

    // Spring Boot starters
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.redis)

    // Kafka
    implementation(libs.spring.kafka)

    // Database
    runtimeOnly(libs.postgresql)

    // gRPC
    implementation(libs.grpc.spring.boot.starter)
    implementation(libs.grpc.netty.shaded)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.protobuf.java)
    implementation(libs.jakarta.annotation.api)
    compileOnly(libs.javax.annotation.api) // Required for gRPC generated code

    // Observability
    implementation(libs.micrometer.core)

    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

sourceSets {
    main {
        proto {
            srcDir("src/main/proto")
        }
    }
}
