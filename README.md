# Spring Microservice Monorepo Template

A **production-ready Gradle monorepo** for building Java microservices.

The goal is not to show off frameworks, but to give you a clean, modern baseline that:

- builds reliably
- scales to multiple services
- is easy to extend
- follows current enterprise conventions
- stays out of your way

I use this as a base for real projects.

---

## Project Structure

```
spring-microservice-monorepo/
├── buildSrc/                              # Gradle convention plugins
│   └── src/main/kotlin/
│       ├── common-java.gradle.kts         # Java 21, encoding, JUnit
│       ├── java-library-conventions.gradle.kts
│       ├── spring-boot-app.gradle.kts     # Spring Boot applications
│       └── protobuf-conventions.gradle.kts
│
├── shared-libs/                           # Shared libraries
│   ├── common-dto/                        # Cross-service DTOs
│   ├── common-utils/                      # Utility classes
│   └── common-test/                       # Test utilities (Testcontainers)
│
├── services/                              # Microservices
│   └── greeting-service/                  # Example service
│       ├── domain/                        # Core business logic
│       ├── adapters/                      # Input/output adapters
│       └── runtime/                       # Spring Boot application
│
├── gradle/libs.versions.toml              # Centralized version catalog
├── build.gradle.kts                       # Root build configuration
├── settings.gradle.kts                    # Module includes
└── compose.yaml                           # Local infrastructure
```

---

## Architecture: Ports and Adapters (Hexagonal)

Each service follows the **Ports and Adapters** (Hexagonal) architecture:

- The **core** (domain) knows nothing about frameworks, databases, or transports
- Everything that touches the outside world is an **adapter**
- The runtime module wires the whole system together

### Service Module Structure

```
services/[service-name]/
├── domain/
│   ├── core model + business rules
│   ├── inbound ports (use cases)
│   └── outbound ports (interfaces)
│
├── adapters/
│   ├── in/   (REST, Kafka consumers, gRPC, WebSocket)
│   └── out/  (persistence, messaging, external clients, caches)
│
└── runtime/
    ├── Spring Boot entrypoint
    ├── wiring & configuration
    └── database migrations
```

The core never depends on adapters. Adapters depend on the core. The runtime composes everything.

---

## What this template gives you

### Core stack
- **Java 21 (LTS)**
- **Spring Boot 3.5**
- **Gradle monorepo** with convention plugins (Kotlin DSL)
- **Type-safe project accessors** (`projects.services.greetingService.domain`)

### Runtime features
- REST API with validation
- gRPC server and client
- Kafka producer and consumer
- Redis caching
- Consistent error responses using **Problem Details**
- Database migrations with **Flyway**
- Actuator endpoints (health, liveness, readiness, metrics, prometheus)
- Structured logging (readable locally, JSON via profile)
- Trace/log correlation (OpenTelemetry + W3C)
- Distributed tracing through Kafka and gRPC

### Testing
- **Unit tests** (fast, no DB required)
- **Integration tests** with Testcontainers (PostgreSQL, Redis, Kafka)

### Build & packaging
- Gradle Wrapper (`./gradlew`)
- Convention plugins for consistent builds across services
- `compose.yaml` for local development

---

## Quick Start

### Requirements
- Java 21
- Docker (for integration tests and local infrastructure)

### Start local infrastructure

```bash
docker compose up -d
```

This starts PostgreSQL, Redis, Kafka, and Zookeeper.

### Build & run tests

```bash
# Build entire monorepo
./gradlew build

# Build specific service
./gradlew :services:greeting-service:runtime:build

# Run unit tests only
./gradlew test

# Run integration tests
./gradlew integrationTest
```

### Run the application

```bash
./gradlew :services:greeting-service:runtime:bootRun
```

### Enable JSON logging

```bash
SPRING_PROFILES_ACTIVE=json ./gradlew :services:greeting-service:runtime:bootRun
```

---

## Gradle Commands

```bash
# Build entire monorepo
./gradlew build

# Build specific service
./gradlew :services:greeting-service:runtime:build

# Run specific service
./gradlew :services:greeting-service:runtime:bootRun

# Run all tests
./gradlew check

# Run integration tests for specific service
./gradlew :services:greeting-service:runtime:integrationTest

# List all projects
./gradlew projects

# Apply code formatting
./gradlew spotlessApply
```

---

## Adding a New Service

1. **Create directories:**
   ```bash
   mkdir -p services/[service-name]/{domain,adapters,runtime}
   ```

2. **Add to `settings.gradle.kts`:**
   ```kotlin
   include("services:[service-name]:domain")
   include("services:[service-name]:adapters")
   include("services:[service-name]:runtime")
   ```

3. **Create build files using convention plugins:**

   `services/[service-name]/domain/build.gradle.kts`:
   ```kotlin
   plugins {
       id("java-library-conventions")
   }
   ```

   `services/[service-name]/adapters/build.gradle.kts`:
   ```kotlin
   plugins {
       id("protobuf-conventions")  // or java-library-conventions if no gRPC
   }

   dependencies {
       api(projects.services.[serviceName].domain)
       // Add your dependencies
   }
   ```

   `services/[service-name]/runtime/build.gradle.kts`:
   ```kotlin
   plugins {
       id("spring-boot-app")
   }

   dependencies {
       implementation(projects.services.[serviceName].adapters)
       // Add your dependencies
   }
   ```

4. **Add source code with appropriate packages** (e.g., `com.example.[servicename]`)

5. **Create `application.yml`** in `runtime/src/main/resources/`

---

## Shared Libraries

### common-dto
Cross-service DTOs with zero framework dependencies. Examples:
- `PageRequest` / `PageResponse`
- `ApiError`
- `AuditInfo`

### common-utils
Lightweight utility classes. Examples:
- `UuidGenerator`
- `JsonUtils`
- `DateTimeUtils`

### common-test
Shared test infrastructure. Examples:
- Testcontainers extensions
- Test data builders

Usage in a service:
```kotlin
dependencies {
    implementation(projects.sharedLibs.commonDto)
    testImplementation(projects.sharedLibs.commonTest)
}
```

---

## Infrastructure

### Redis

Used for caching. The `GreetingCache` port is implemented by `RedisGreetingCache`.

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```

### Kafka

Used for event-driven messaging:
- `KafkaGreetingEventPublisher` - publishes to `greeting-events` topic
- `KafkaGreetingEventListener` - consumes from the same topic

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: greeting-service-group
```

### gRPC

Server and client included:
- `GrpcGreetingService` - exposes `CreateGreeting` and `GetGreeting` RPCs
- `GrpcExternalGreetingClient` - calls external services

Proto file: `services/greeting-service/adapters/src/main/proto/greeting.proto`

```yaml
grpc:
  server:
    port: ${GRPC_SERVER_PORT:9090}
  client:
    external-greeting-service:
      address: static://${EXTERNAL_GRPC_HOST:localhost}:${EXTERNAL_GRPC_PORT:9091}
```

---

## Observability

### Metrics

Available at `/actuator/prometheus`:

| Component | Metrics |
|-----------|---------|
| Redis | `cache_greeting_hits_total`, `cache_greeting_misses_total` |
| Kafka | `kafka_greeting_events_published_total`, `kafka_greeting_events_received_total` |
| gRPC | `grpc_server_requests_seconds` (by method and status) |

### Tracing

Configured with Micrometer + OpenTelemetry. Trace context flows through HTTP, Kafka, and gRPC.

```bash
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
```

### Logging

All adapters log with `traceId` and `spanId` in MDC.

---

## Endpoints

### REST

```
POST /api/v1/greetings      - Create a greeting
GET  /api/v1/greetings/{id} - Get a greeting by ID
```

### gRPC

Port 9090 (default):
- `GreetingService.CreateGreeting`
- `GreetingService.GetGreeting`

### Actuator

```
/actuator/health
/actuator/health/liveness
/actuator/health/readiness
/actuator/metrics
/actuator/prometheus
```

---

## Database Migrations

Uses **Flyway** for schema management.

Location: `services/greeting-service/runtime/src/main/resources/db/migration/`

Naming convention:
```
V1__create_greetings_table.sql
V2__add_created_at_column.sql
```

---

## Integration Tests

```bash
# Run all tests including integration tests
./gradlew check

# Run integration tests separately
./gradlew :services:greeting-service:runtime:integrationTest
```

Integration tests use Testcontainers for PostgreSQL, Redis, and Kafka.

Test files in `services/greeting-service/runtime/src/test/java/`:
- `GreetingFlowIT.java` - REST API flow
- `RedisCacheIT.java` - Cache operations
- `KafkaMessagingIT.java` - Event publishing/consumption
- `GrpcGreetingIT.java` - gRPC server

---

## Convention Plugins

Located in `buildSrc/src/main/kotlin/`:

| Plugin | Purpose |
|--------|---------|
| `common-java` | Java 21 toolchain, UTF-8 encoding, JUnit Platform |
| `java-library-conventions` | For domain and adapter modules |
| `spring-boot-app` | For runtime modules (Spring Boot, test separation) |
| `protobuf-conventions` | For modules with gRPC/protobuf |

---

## Philosophy

This template prefers:
- explicit over clever
- boring over fragile
- small pieces over big frameworks
- things you can reason about at 2am
