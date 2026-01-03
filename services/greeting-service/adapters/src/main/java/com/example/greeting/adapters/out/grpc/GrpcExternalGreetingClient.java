package com.example.greeting.adapters.out.grpc;

import com.example.greeting.adapters.grpc.generated.CreateGreetingRequest;
import com.example.greeting.adapters.grpc.generated.GetGreetingRequest;
import com.example.greeting.adapters.grpc.generated.GreetingServiceGrpc;
import com.example.greeting.domain.Greeting;
import com.example.greeting.domain.ports.out.ExternalGreetingClient;
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import java.util.UUID;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GrpcExternalGreetingClient implements ExternalGreetingClient {

  private static final Logger logger = LoggerFactory.getLogger(GrpcExternalGreetingClient.class);

  @GrpcClient("external-greeting-service")
  private GreetingServiceGrpc.GreetingServiceBlockingStub greetingStub;

  @Override
  public Optional<Greeting> fetchGreeting(UUID id) {
    logger.info("Fetching greeting from external service: {}", id);
    try {
      var request = GetGreetingRequest.newBuilder().setId(id.toString()).build();

      var response = greetingStub.getGreeting(request);
      return Optional.of(new Greeting(UUID.fromString(response.getId()), response.getMessage()));
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
        logger.debug("External greeting not found id={}", id);
        return Optional.empty();
      }
      if (e.getStatus().getCode() == io.grpc.Status.Code.DEADLINE_EXCEEDED) {
        logger.error("gRPC fetchGreeting timed out id={}", id);
        throw new RuntimeException("Timeout fetching greeting from external service", e);
      }
      logger.error("gRPC fetchGreeting failed id={} status={}", id, e.getStatus().getCode(), e);
      throw new RuntimeException("Failed to fetch greeting from external service", e);
    }
  }

  @Override
  public Greeting createRemoteGreeting(String name) {
    logger.info("Creating greeting on external service for name={}", name);
    try {
      var request = CreateGreetingRequest.newBuilder().setName(name).build();
      var response = greetingStub.createGreeting(request);
      var greeting = new Greeting(UUID.fromString(response.getId()), response.getMessage());
      logger.info("gRPC createRemoteGreeting completed greetingId={}", greeting.id());
      return greeting;
    } catch (StatusRuntimeException e) {
      if (e.getStatus().getCode() == io.grpc.Status.Code.DEADLINE_EXCEEDED) {
        logger.error("gRPC createRemoteGreeting timed out name={}", name);
        throw new RuntimeException("Timeout creating greeting on external service", e);
      }
      logger.error(
          "gRPC createRemoteGreeting failed name={} status={}", name, e.getStatus().getCode(), e);
      throw new RuntimeException("Failed to create greeting on external service", e);
    }
  }
}
