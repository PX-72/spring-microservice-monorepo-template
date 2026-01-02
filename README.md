# Spring Microservice Template

This repository is a starting point for building **production-ready Java microservices**.

The goal is not to show off frameworks, but to give you a clean, modern baseline that:

- builds reliably
- is easy to extend
- follows current enterprise conventions
- stays out of your way

I use this as a base for real projects.

---

### Architecture style: Ports and Adapters (Hexagonal)

This template follows the **Ports and Adapters** (also called **Hexagonal**) architecture.

- The **core** of the system (domain + use cases) knows nothing about frameworks, databases, messaging systems, or transports.
- Everything that touches the outside world is an **adapter**.
- The runtime module wires the whole system together.

This makes the service:
- easier to test
- easier to change infrastructure
- safer to extend with new transports (REST, Kafka, gRPC, MCP, WebSockets, etc.)

#### How it maps to this project

domain
- core model + business rules
- inbound ports (use cases)
- outbound ports (interfaces)

adapters/in/
- REST controllers
- Kafka consumers
- gRPC services
- WebSocket handlers
- MCP handlers

adapters/out/
- persistence (JPA, JDBC, etc.)
- messaging producers
- external service clients
- caches

runtime
- Spring Boot entrypoint
- wiring & configuration
- selecting which adapters are active

Inbound adapters translate external input into calls on **inbound ports**.
Outbound adapters implement **outbound ports** defined in the domain.

The core never depends on adapters.
Adapters depend on the core.
The runtime composes everything.

---

## What this template gives you

### Core stack
- **Java 21 (LTS)**
- **Spring Boot 3.5**
- **Gradle multi-module project** (Kotlin DSL)

---

### Runtime features
- REST API with validation
- gRPC server and client
- Kafka producer and consumer
- Redis caching
- Consistent error responses using **Problem Details**
- Database migrations with **Flyway**
- Actuator endpoints:
  - health / liveness / readiness
  - metrics / prometheus
- Structured logging
  - readable logs locally
  - JSON logs via profile
- Trace/log correlation (OpenTelemetry + W3C)
- Distributed tracing through Kafka and gRPC

### Testing
- **Unit tests** (fast, no DB required)
- **Integration tests** with Testcontainers (PostgreSQL, Redis, Kafka)

### Build & packaging
- Gradle Wrapper (`./gradlew`)
- Dockerfile for container builds
- `compose.yaml` for local development (PostgreSQL, Redis, Kafka, Zookeeper)

---

## Infrastructure

### Redis

Used for caching. The `GreetingCache` port is implemented by `RedisGreetingCache`.

Configuration in `application.yml`:
```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```

### Kafka

Used for event-driven messaging. The template includes:
- `KafkaGreetingEventPublisher` - publishes `GreetingCreatedEvent` to `greeting-events` topic
- `KafkaGreetingEventListener` - consumes events from the same topic

Configuration in `application.yml`:
```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: greeting-service-group
```

Trace context is automatically propagated through Kafka message headers (W3C Trace Context).

### gRPC

The template includes both server and client:
- `GrpcGreetingService` - gRPC server exposing `CreateGreeting` and `GetGreeting` RPCs
- `GrpcExternalGreetingClient` - gRPC client for calling external services

Proto file: `adapters/src/main/proto/greeting.proto`

Configuration in `application.yml`:
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

Available at `/actuator/prometheus`. Includes:

| Component | Metrics |
|-----------|---------|
| Redis | `cache_greeting_hits_total`, `cache_greeting_misses_total`, `cache_greeting_get_seconds`, `cache_greeting_put_seconds` |
| Kafka | `kafka_greeting_events_published_total`, `kafka_greeting_events_received_total`, `kafka_greeting_events_processed_total` |
| gRPC | `grpc_server_requests_seconds` (by method and status) |

### Tracing

Configured with Micrometer + OpenTelemetry bridge. Trace context flows through:
- HTTP requests (automatic)
- Kafka messages (via headers)
- gRPC calls (via metadata)

Set the OTLP endpoint to export traces:
```bash
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
```

### Logging

All adapters log with `traceId` and `spanId` in MDC. Log pattern includes these fields automatically.

---

## Why this exists

This is the setup I wish every new Java service started with:

- no magic
- no hidden coupling
- no accidental complexity
- no framework lock-in

It is intentionally boring in the right ways.

---

## Quick start

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
./gradlew build
```

### Run the application

```bash
./gradlew :runtime:bootRun
```

### Enable JSON logging

```bash
SPRING_PROFILES_ACTIVE=json ./gradlew :runtime:bootRun
```

---

## Endpoints

### REST

```
POST /api/v1/greetings     - Create a greeting
GET  /api/v1/greetings/{id} - Get a greeting by ID
```

### gRPC

Port 9090 (default). Services:
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

This template uses **Flyway** for database schema management.

Migrations live in:
```
runtime/src/main/resources/db/migration/
```

Naming convention:
```
V1__create_greetings_table.sql
V2__add_created_at_column.sql
```

On startup, Flyway automatically applies pending migrations.

---

## Integration Tests

Run all tests including integration tests:
```bash
./gradlew check
```

Or run integration tests separately:
```bash
./gradlew integrationTest
```

Integration tests use Testcontainers to spin up:
- PostgreSQL
- Redis
- Kafka

Test files:
- `GreetingFlowIT.java` - REST API flow
- `RedisCacheIT.java` - Cache operations
- `KafkaMessagingIT.java` - Event publishing and consumption
- `GrpcGreetingIT.java` - gRPC server

---

## How to extend this

1. Add your domain model in `domain`
2. Add persistence or external clients in `adapters/out`
3. Expose endpoints in `adapters/in`
4. Add database migrations in `runtime/src/main/resources/db/migration/`
5. Add integration tests as needed

---

## Philosophy

This template prefers:
- explicit over clever
- boring over fragile
- small pieces over big frameworks
- things you can reason about at 2am
