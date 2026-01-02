package com.example.greeting.adapters.in.grpc;

import com.example.greeting.adapters.grpc.generated.CreateGreetingRequest;
import com.example.greeting.adapters.grpc.generated.GetGreetingRequest;
import com.example.greeting.adapters.grpc.generated.GreetingResponse;
import com.example.greeting.adapters.grpc.generated.GreetingServiceGrpc;
import com.example.greeting.domain.services.GreetingService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class GrpcGreetingService extends GreetingServiceGrpc.GreetingServiceImplBase {

  private static final Logger logger = LoggerFactory.getLogger(GrpcGreetingService.class);

  private final GreetingService greetingService;

  public GrpcGreetingService(GreetingService greetingService) {
    this.greetingService = greetingService;
  }

  @Override
  public void createGreeting(
      CreateGreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
    logger.info("gRPC createGreeting called with name: {}", request.getName());
    try {
      var greeting = greetingService.createGreeting(request.getName());
      var response =
          GreetingResponse.newBuilder()
              .setId(greeting.id().toString())
              .setMessage(greeting.message())
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
      logger.info("gRPC createGreeting completed greetingId={}", greeting.id());
    } catch (Exception e) {
      logger.error("Error creating greeting via gRPC: {}", e.getMessage(), e);
      responseObserver.onError(
          Status.INTERNAL
              .withDescription("Failed to create greeting: " + e.getMessage())
              .asRuntimeException());
    }
  }

  @Override
  public void getGreeting(
      GetGreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
    logger.info("gRPC getGreeting called with id: {}", request.getId());
    try {
      UUID id = UUID.fromString(request.getId());
      var greetingOpt = greetingService.getGreeting(id);

      if (greetingOpt.isPresent()) {
        var greeting = greetingOpt.get();
        var response =
            GreetingResponse.newBuilder()
                .setId(greeting.id().toString())
                .setMessage(greeting.message())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        logger.info("gRPC getGreeting completed greetingId={}", greeting.id());
      } else {
        logger.debug("gRPC getGreeting not found id={}", request.getId());
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("Greeting not found with id: " + request.getId())
                .asRuntimeException());
      }
    } catch (IllegalArgumentException e) {
      responseObserver.onError(
          Status.INVALID_ARGUMENT
              .withDescription("Invalid UUID format: " + request.getId())
              .asRuntimeException());
    } catch (Exception e) {
      logger.error("Error getting greeting via gRPC", e);
      responseObserver.onError(
          Status.INTERNAL
              .withDescription("Failed to get greeting: " + e.getMessage())
              .asRuntimeException());
    }
  }
}
