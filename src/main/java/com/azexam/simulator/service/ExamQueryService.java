package com.azexam.simulator.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.azexam.simulator.dto.ExamProgressResponse;
import com.azexam.simulator.dto.ExamTimerResponse;
import com.azexam.simulator.dto.ResumeExamResponse;
import com.azexam.simulator.repository.ExamAnswerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExamQueryService {
  
  private final ExamSessionService examSessionService;
  private final QuestionLoaderService questionLoaderService;
  private final ExamAnswerRepository examAnswerRepository;
  private final ObjectMapper objectMapper;

  public ExamQueryService(
      ExamSessionService examSessionService,
      QuestionLoaderService questionLoaderService,
      ExamAnswerRepository examAnswerRepository,
      ObjectMapper objectMapper) {
    this.examSessionService = examSessionService;
    this.questionLoaderService = questionLoaderService;
    this.examAnswerRepository = examAnswerRepository;
    this.objectMapper = objectMapper;
  }

  public ResumeExamResponse resumeExam(UUID sessionId) {

    // 1. Get session details
    var session = examSessionService.getSession(sessionId);

    // 2. Load exam questions
    var exam = questionLoaderService.loadExam(session.getExamCode());
    var questions = exam.getQuestions();

    // 3. Load existing answers for the session from DB
    var answerList = examAnswerRepository.findBySessionId(sessionId);

    // 4. Convert answer list to a map for easy lookup
    Map<String, String> answerMap = answerList.stream()
      .collect(Collectors.toMap(
        a -> a.getQuestionId(),
        a -> extractAnswer(a.getAnswer())
      ));

    // 5. Construct and return response
    return new ResumeExamResponse(
      sessionId,
      session.getStatus(),
      questions,
      answerMap
    );
  }
  
  public ExamProgressResponse getProgress(UUID sessionId) {
    
    // 1. Get session
    var session = examSessionService.getSession(sessionId);

    // 2. Load exam questions
    var exam = questionLoaderService.loadExam(session.getExamCode());
    int total = exam.getQuestions().size();

    // 3. Count answers in DB
    var answers = examAnswerRepository.findBySessionId(sessionId);
    int answered = (int)answers.stream()
      .map(a -> a.getQuestionId())
      .distinct()
      .count();

    // 4. Return response
    return new ExamProgressResponse(answered, total);
  }

  public ExamTimerResponse getTimer(UUID sessionId) {
    
    var session = examSessionService.getSession(sessionId);

    // If already submitted, no time left
    if ("SUBMITTED".equals(session.getStatus())) {
      return new ExamTimerResponse(0, true);
    }

    var startTime = session.getStartTime();
    var duration = session.getDurationMinutes();

    var endTime = startTime.plus(Duration.ofMinutes(duration));

    var now = Instant.now();

    long remainingSeconds = Duration.between(now, endTime).getSeconds();

    if (remainingSeconds <= 0) {
      return new ExamTimerResponse(0, true);
    }

    return new ExamTimerResponse(remainingSeconds, false);
  }

  private String extractAnswer(String json) {
    try {
      return objectMapper.readValue(json, String.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse answer JSON", e);
    }
  }
}
