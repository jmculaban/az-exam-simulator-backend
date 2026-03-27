package com.azexam.simulator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.azexam.simulator.dto.CreateSessionRequest;
import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.service.ExamSessionService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/exam-sessions")
public class ExamSessionController {
  
  private final ExamSessionService examService;

  public ExamSessionController(ExamSessionService examService) {
    this.examService = examService;
  }

  @PostMapping
  public ResponseEntity<ExamSession> createSession(@RequestBody CreateSessionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      examService.createSession(
        request.getExamCode(), 
        request.getUserId()
      )
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExamSession> getSession(@PathVariable UUID id) {
      return ResponseEntity.ok(examService.getSession(id));
  }
}
