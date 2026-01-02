package com.example.greeting.runtime.config;

import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcObservabilityConfiguration {

  private static final Logger logger =
      LoggerFactory.getLogger(GrpcObservabilityConfiguration.class);

  @GrpcGlobalServerInterceptor
  public ServerInterceptor grpcMetricsServerInterceptor(MeterRegistry meterRegistry) {
    return new MetricsServerInterceptor(meterRegistry);
  }

  static class MetricsServerInterceptor implements ServerInterceptor {
    private final MeterRegistry meterRegistry;

    MetricsServerInterceptor(MeterRegistry meterRegistry) {
      this.meterRegistry = meterRegistry;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

      String methodName = call.getMethodDescriptor().getFullMethodName();
      Timer.Sample sample = Timer.start(meterRegistry);

      logger.debug("gRPC server call started method={}", methodName);

      ServerCall<ReqT, RespT> wrappedCall =
          new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
              sample.stop(
                  Timer.builder("grpc.server.requests")
                      .description("gRPC server request duration")
                      .tag("method", methodName)
                      .tag("status", status.getCode().name())
                      .register(meterRegistry));
              logger.debug(
                  "gRPC server call completed method={} status={}", methodName, status.getCode());
              super.close(status, trailers);
            }
          };

      return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
          next.startCall(wrappedCall, headers)) {};
    }
  }
}
