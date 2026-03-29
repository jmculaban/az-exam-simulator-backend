package com.azexam.simulator.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Centralized exception mapping for REST endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
  
  /**
   * Handles domain-level bad request exceptions.
   *
   * @param ex thrown bad request exception
   * @param request current HTTP request
   * @return standardized 400 error payload
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Map<String, String>> handleBadRequest(
    BadRequestException ex, 
    HttpServletRequest request) {
    
    Map<String, String> response = new HashMap<>();
    response.put("timestamp", Instant.now().toString());
    response.put("status", "400");
    response.put("error", ex.getMessage());
    response.put("path", request.getRequestURI());

    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Handles bean validation failures from request payloads.
   *
   * @param ex validation exception containing field errors
   * @param request current HTTP request
   * @return standardized 400 payload with validation details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationErrors(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {
        
    var errors = ex.getBindingResult().getFieldErrors()
      .stream()
      .map(err -> Map.of(
        "field", err.getField(),
        "message", err.getDefaultMessage()
      ))
      .toList();

    return ResponseEntity.badRequest().body(Map.of(
      "timestamp", Instant.now().toString(),
      "status", "400",
      "error", "Validation failed",
      "path", request.getRequestURI(),
      "validationErrors", errors
    ));
  }
}
