import com.google.protobuf.gradle.*

plugins {
    id("java-library-conventions")
    id("com.google.protobuf")
    id("io.spring.dependency-management")
}

val springBootVersion = "3.5.0"
val grpcVersion = "1.68.0"
val protobufVersion = "4.28.2"

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
