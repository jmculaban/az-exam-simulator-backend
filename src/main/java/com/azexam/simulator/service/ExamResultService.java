package com.azexam.simulator.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.exception.BadRequestException;
import com.azexam.simulator.model.ExamAnswer;
import com.azexam.simulator.model.ExamResult;
import com.azexam.simulator.model.ExamSessionStatus;
import com.azexam.simulator.repository.ExamAnswerRepository;
import com.azexam.simulator.repository.ExamResultRepository;
import com.azexam.simulator.service.scoring.ScoringEngine;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExamResultService {
  
  private final ExamSessionService sessionService;
  private final ExamResultRepository resultRepository;
  private final QuestionLoaderService questionLoader;
  private final ObjectMapper objectMapper;
  private final ExamAnswerRepository answerRepository;
  private final ScoringEngine scoringEngine;

  public ExamResultService(
    ExamSessionService sessionService,
    ExamResultRepository resultRepository,
    QuestionLoaderService questionLoader,
    ObjectMapper objectMapper,
    ExamAnswerRepository answerRepository,
    ScoringEngine scoringEngine
  ) {
    this.sessionService = sessionService;
    this.resultRepository = resultRepository;
    this.questionLoader = questionLoader;
    this.objectMapper = objectMapper;
    this.answerRepository = answerRepository;
    this.scoringEngine = scoringEngine;
  }

  public ExamResultResponse submitExam(UUID sessionId, boolean isAutoSubmit) {
    
    var existing = resultRepository.findBySessionId(sessionId);

    if (existing.isPresent()) {
      throw new BadRequestException("Exam already submitted");
    }

    var session = sessionService.getSession(sessionId);
    
    if (ExamSessionStatus.SUBMITTED.name().equals(session.getStatus())) {
      throw new BadRequestException("Exam already submitted");
    }

    // Check timer
    var startTime = session.getStartTime();
    var duration = session.getDurationMinutes();

    var endTime = startTime.plus(Duration.ofMinutes(duration));
    var now = Instant.now();

    if (!isAutoSubmit && now.isAfter(endTime)) {
      throw new BadRequestException("Exam time expired");
    }
    
    var questions = questionLoader.loadExam(session.getExamCode()).getQuestions();

    var answers = answerRepository.findBySessionId(sessionId);
    
    Map<String, String> answerMap = answers.stream()
      .collect(Collectors.toMap(
        ExamAnswer::getQuestionId,
        ExamAnswer::getAnswer
      ));

    int correct = 0;

    for (var q: questions) {
      
      if (answerMap.containsKey(q.getId())) {

        if (scoringEngine.isCorrect(q, answerMap.get(q.getId()))) {
          correct++;
        }
      }
    }

    int total = questions.size();

    if (total == 0) {
      throw new BadRequestException("Exam has no questions");
    }

    int score = (correct * 100) / total;

    ExamResult result = new ExamResult();
    result.setId(UUID.randomUUID());
    result.setSessionId(sessionId);
    result.setScore(score);
    result.setCorrect(correct);
    result.setTotal(total);
    result.setPassed(score >= 70);

    result.setSubmittedAt(Instant.now());

    resultRepository.save(result);

    sessionService.markAsSubmitted(session);

    return new ExamResultResponse(score, correct, total, score >= 70);
  }

  public ExamResultResponse submitExam(UUID sessionId) {
    // user-triggered
    return submitExam(sessionId, false);
  }

  public ExamResultResponse getResult(UUID sessionId) {

    var result = resultRepository.findBySessionId(sessionId)
      .orElseThrow(() -> new RuntimeException("Exam result not found"));
    
    return new ExamResultResponse(
      result.getScore(),
      result.getCorrect(),
      result.getTotal(),
      result.getPassed()
    );
  }

  private String extractAnswer(String json) {
    try {
      return objectMapper.readValue(json, String.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to deserialize answer from JSON", e);
    }
  }
}
