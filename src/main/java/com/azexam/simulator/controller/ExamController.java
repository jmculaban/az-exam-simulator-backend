package com.azexam.simulator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.azexam.simulator.dto.CreateSessionRequest;
import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.dto.SubmitExamRequest;
import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.service.ExamService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/exam-sessions")
public class ExamController {
  
  private final ExamService examService;

  public ExamController(ExamService examService) {
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

  @PostMapping("/{id}/submit")
  public ResponseEntity<ExamResultResponse> submitExam(
    @PathVariable("id") UUID id, 
    @RequestBody SubmitExamRequest request) {
      
      return ResponseEntity.ok(
        examService.submitExam(id, request.getAnswers())
      );
  }
}
