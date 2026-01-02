package com.example.greeting.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.greeting.adapters.grpc.generated.CreateGreetingRequest;
import com.example.greeting.adapters.grpc.generated.GetGreetingRequest;
import com.example.greeting.adapters.grpc.generated.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class GrpcGreetingIT {

  private static final int GRPC_PORT = 9090;

  @Container static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

  @Container
  static GenericContainer<?> redis =
      new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

  @Container
  static KafkaContainer kafka =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    registry.add("grpc.server.port", () -> GRPC_PORT);
  }

  private ManagedChannel channel;
  private GreetingServiceGrpc.GreetingServiceBlockingStub stub;

  @BeforeEach
  void setUp() {
    channel = ManagedChannelBuilder.forAddress("localhost", GRPC_PORT).usePlaintext().build();
    stub = GreetingServiceGrpc.newBlockingStub(channel);
  }

  @AfterEach
  void tearDown() throws InterruptedException {
    if (channel != null) {
      channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  @Test
  void shouldCreateGreetingViaGrpc() {
    var request = CreateGreetingRequest.newBuilder().setName("gRPC User").build();

    var response = stub.createGreeting(request);

    assertThat(response.getId()).isNotEmpty();
    assertThat(response.getMessage()).isEqualTo("Hello, gRPC User!");
  }

  @Test
  void shouldGetGreetingViaGrpc() {
    var createRequest = CreateGreetingRequest.newBuilder().setName("Get Test").build();
    var created = stub.createGreeting(createRequest);

    var getRequest = GetGreetingRequest.newBuilder().setId(created.getId()).build();
    var retrieved = stub.getGreeting(getRequest);

    assertThat(retrieved.getId()).isEqualTo(created.getId());
    assertThat(retrieved.getMessage()).isEqualTo("Hello, Get Test!");
  }

  @Test
  void shouldReturnNotFoundForNonExistentGreeting() {
    var request = GetGreetingRequest.newBuilder().setId(UUID.randomUUID().toString()).build();

    try {
      stub.getGreeting(request);
      assertThat(true).isFalse(); // Should not reach here
    } catch (StatusRuntimeException e) {
      assertThat(e.getStatus().getCode()).isEqualTo(io.grpc.Status.Code.NOT_FOUND);
    }
  }

  @Test
  void shouldReturnInvalidArgumentForBadUuid() {
    var request = GetGreetingRequest.newBuilder().setId("not-a-uuid").build();

    try {
      stub.getGreeting(request);
      assertThat(true).isFalse(); // Should not reach here
    } catch (StatusRuntimeException e) {
      assertThat(e.getStatus().getCode()).isEqualTo(io.grpc.Status.Code.INVALID_ARGUMENT);
    }
  }
}
