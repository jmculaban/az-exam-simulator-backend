package com.azexam.simulator.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.azexam.simulator.dto.AnswerDto;
import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.exception.BadRequestException;
import com.azexam.simulator.model.ExamAnswer;
import com.azexam.simulator.model.ExamResult;
import com.azexam.simulator.model.ExamSessionStatus;
import com.azexam.simulator.repository.ExamAnswerRepository;
import com.azexam.simulator.repository.ExamResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExamResultService {
  
  private final ExamSessionService sessionService;
  private final ExamResultRepository resultRepository;
  private final QuestionLoaderService questionLoader;
  private final ObjectMapper objectMapper;
  private final ExamAnswerRepository answerRepository;

  public ExamResultService(
    ExamSessionService sessionService,
    ExamResultRepository resultRepository,
    QuestionLoaderService questionLoader,
    ObjectMapper objectMapper,
    ExamAnswerRepository answerRepository
  ) {
    this.sessionService = sessionService;
    this.resultRepository = resultRepository;
    this.questionLoader = questionLoader;
    this.objectMapper = objectMapper;
    this.answerRepository = answerRepository;
  }

  public ExamResultResponse submitExam(UUID sessionId) {
    
    var existing = resultRepository.findBySessionId(sessionId);

    if (existing.isPresent()) {
      throw new BadRequestException("Exam already submitted");
    }

    var session = sessionService.getSession(sessionId);
    
    if (ExamSessionStatus.SUBMITTED.name().equals(session.getStatus())) {
      throw new BadRequestException("Exam already submitted");
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

        String parsedAnswer = extractAnswer(answerMap.get(q.getId()));

        if (parsedAnswer.equals(q.getCorrectAnswer())) {
          correct++;
        }
      }
    }

    int total = questions.size();
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
