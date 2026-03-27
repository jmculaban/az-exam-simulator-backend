package com.azexam.simulator.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.SaveAnswerRequest;
import com.azexam.simulator.model.ExamAnswer;
import com.azexam.simulator.service.ExamAnswerService;

@RestController
@RequestMapping("/api/exam-answers")
public class ExamAnswerController {
  
  private final ExamAnswerService examAnswerService;

  public ExamAnswerController(ExamAnswerService examAnswerService) {
    this.examAnswerService = examAnswerService;
  }

  @PostMapping
  public ResponseEntity<Void> saveAnswer(@RequestBody SaveAnswerRequest request) {
    
    examAnswerService.saveAnswer(request);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/session/{sessionId}")
  public ResponseEntity<List<ExamAnswer>> getAnswers(@PathVariable UUID sessionId) {

    return ResponseEntity.ok(examAnswerService.getAnswers(sessionId));
  }
}
