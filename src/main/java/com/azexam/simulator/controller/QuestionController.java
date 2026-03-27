package com.azexam.simulator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azexam.simulator.dto.QuestionResponse;
import com.azexam.simulator.service.QuestionService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
  
  private final QuestionService questionService;

  public QuestionController(QuestionService questionService) {
    this.questionService = questionService;
  }

  @GetMapping("/session/{sessionId}")
  public ResponseEntity<List<QuestionResponse>> getQuestions(@PathVariable UUID sessionId) {
      return ResponseEntity.ok(questionService.getQuestions(sessionId));
  }
}
