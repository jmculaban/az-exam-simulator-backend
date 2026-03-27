package com.azexam.simulator.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
  
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
}
