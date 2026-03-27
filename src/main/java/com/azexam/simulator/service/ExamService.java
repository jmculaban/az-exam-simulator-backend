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
import com.azexam.simulator.model.ExamAnswer;
import com.azexam.simulator.model.ExamResult;
import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.model.User;
import com.azexam.simulator.model.yaml.ExamYaml;
import com.azexam.simulator.model.yaml.QuestionYaml;
import com.azexam.simulator.repository.ExamAnswerRepository;
import com.azexam.simulator.repository.ExamResultRepository;
import com.azexam.simulator.repository.ExamSessionRepository;
import com.azexam.simulator.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExamService {
  
  private final ExamSessionRepository sessionRepository;
  private final UserRepository userRepository;
  private final ExamAnswerRepository answerRepository;
  private final ExamResultRepository resultRepository;
  private final QuestionLoaderService questionLoaderService;

  public ExamService(
    ExamSessionRepository sessionRepository,
    UserRepository userRepository,
    ExamAnswerRepository answerRepository,
    ExamResultRepository resultRepository,
    QuestionLoaderService questionLoaderService
  ) {
    this.sessionRepository = sessionRepository;
    this.userRepository = userRepository;
    this.answerRepository = answerRepository;
    this.resultRepository = resultRepository;
    this.questionLoaderService = questionLoaderService;
  }

  public ExamSession createSession(String examCode, UUID userId) {
    
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));
    
    ExamSession session = new ExamSession();
    session.setId(UUID.randomUUID());
    session.setExamCode(examCode);
    session.setUser(user);
    session.setStartTime(LocalDateTime.now());
    session.setStatus("IN_PROGRESS");

    return sessionRepository.save(session);
  }

  public ExamResultResponse submitExam(UUID sessionId, List<AnswerDto> answers) {
    ObjectMapper mapper = new ObjectMapper();

    ExamSession session = sessionRepository.findById(sessionId)
      .orElseThrow(() -> new RuntimeException("Session not found"));

    if ("COMPLETED".equals(session.getStatus())) {
      throw new RuntimeException("Exam already submitted");
    }

    // 1. Save answers
    for (AnswerDto dto : answers) {
      ExamAnswer answer = new ExamAnswer();
      answer.setId(UUID.randomUUID());
      answer.setSessionId(sessionId);
      answer.setQuestionId(dto.getQuestionId());
      
      try {
        answer.setAnswer(mapper.writeValueAsString(dto.getAnswer()));
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to serialize answer", e);
      }
      
      answer.setCreatedAt(Instant.now());
      answer.setUpdatedAt(Instant.now());

      answerRepository.save(answer);
    }

    // 2. TEMP SCORING (replace later with YAML)    
    ExamYaml examYaml = questionLoaderService.loadExam(session.getExamCode());

    Map<String, String> correctAnswers = examYaml.getQuestions()
      .stream()
      .collect(Collectors.toMap(
        QuestionYaml::getId,
        QuestionYaml::getCorrectAnswer
      ));

    int correct = 0;

    for (AnswerDto dto : answers) {
      
      String correctAnswer = correctAnswers.get(dto.getQuestionId());
      
      if (correctAnswer != null && 
          correctAnswer.equals(dto.getAnswer())) {
        correct++;
      }
    }

    int total = answers.size();    
    int score = total == 0 ? 0 : (correct * 100 / total);

    // 3. Save result
    ExamResult result = new ExamResult();
    result.setId(UUID.randomUUID());
    result.setSessionId(sessionId);
    result.setScore(score);
    result.setCorrect(correct);
    result.setTotal(total);
    result.setPassed(score >= 70);
    result.setSubmittedAt(Instant.now());

    resultRepository.save(result);

    // 4. Update session status
    session.setStatus("COMPLETED");
    session.setEndTime(LocalDateTime.now());
    sessionRepository.save(session);

    // 5. Response
    return mapToResponse(result);
  }

  private ExamResultResponse mapToResponse(ExamResult result) {
    ExamResultResponse res = new ExamResultResponse();
    res.setScore(result.getScore());
    res.setCorrect(result.getCorrect());
    res.setTotal(result.getTotal());
    res.setPassed(result.getPassed());
    return res;
  }
}
