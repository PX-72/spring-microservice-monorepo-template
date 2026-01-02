package com.example.greeting.adapters.in.rest.error;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setType(URI.create("urn:problem:validation"));
    problem.setTitle("Validation failed");
    problem.setDetail("One or more fields are invalid.");
    problem.setProperty("path", request.getRequestURI());

    var errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new FieldErrorItem(fe.getField(), fe.getDefaultMessage()))
            .toList();
    problem.setProperty("errors", errors);

    return problem;
  }

  @ExceptionHandler(ErrorResponseException.class)
  public ProblemDetail handleErrorResponse(ErrorResponseException ex, HttpServletRequest request) {
    var problem = ex.getBody();

    var properties = problem.getProperties();
    if (properties == null || !properties.containsKey("path")) {
      problem.setProperty("path", request.getRequestURI());
    }

    return problem;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
    var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problem.setType(URI.create("urn:problem:internal"));
    problem.setTitle("Internal error");
    problem.setDetail("An unexpected error occurred.");
    problem.setProperty("path", request.getRequestURI());
    return problem;
  }

  private record FieldErrorItem(String field, String message) {}
}
