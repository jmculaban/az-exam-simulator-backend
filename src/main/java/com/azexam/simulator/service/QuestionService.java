package com.azexam.simulator.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azexam.simulator.dto.QuestionResponse;

@Service
public class QuestionService {
  
  private final ExamSessionService sessionService;
  private final QuestionLoaderService questionLoader;

  public QuestionService(
    ExamSessionService sessionService,
    QuestionLoaderService questionLoader
  ) {
    this.sessionService = sessionService;
    this.questionLoader = questionLoader;
  }

  public List<QuestionResponse> getQuestions(UUID sessionId) {
    var session = sessionService.getSession(sessionId);
    
    return questionLoader.loadExam(session.getExamCode())
      .getQuestions()
      .stream()
      .map(q -> new QuestionResponse(
        q.getId(),
        q.getText(),
        q.getOptions()
      ))
      .toList();
  }
}
