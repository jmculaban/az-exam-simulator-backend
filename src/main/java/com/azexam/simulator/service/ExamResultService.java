package com.azexam.simulator.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.azexam.simulator.dto.ExamResultResponse;
import com.azexam.simulator.dto.SectionResultDto;
import com.azexam.simulator.exception.BadRequestException;
import com.azexam.simulator.model.ExamAnswer;
import com.azexam.simulator.model.ExamResult;
import com.azexam.simulator.model.ExamSectionResult;
import com.azexam.simulator.model.ExamSessionStatus;
import com.azexam.simulator.repository.ExamAnswerRepository;
import com.azexam.simulator.repository.ExamResultRepository;
import com.azexam.simulator.repository.ExamSectionResultRepository;
import com.azexam.simulator.service.scoring.ScoringEngine;

/**
 * Computes and retrieves exam results.
 */
@Service
public class ExamResultService {
  
  private final ExamSessionService sessionService;
  private final ExamResultRepository resultRepository;
  private final ExamAnswerRepository answerRepository;
  private final ExamSectionResultRepository examSectionResultRepository;
  private final ScoringEngine scoringEngine;

  public ExamResultService(
    ExamSessionService sessionService,
    ExamResultRepository resultRepository,
    ExamAnswerRepository answerRepository,
    ExamSectionResultRepository examSectionResultRepository,
    ScoringEngine scoringEngine
  ) {
    this.sessionService = sessionService;
    this.resultRepository = resultRepository;
    this.answerRepository = answerRepository;
    this.examSectionResultRepository = examSectionResultRepository;
    this.scoringEngine = scoringEngine;
  }

  /**
   * Submits an exam session and computes score using stored answers.
   *
   * @param sessionId session id
   * @param isAutoSubmit true when submission is scheduler-triggered
   * @return computed exam result response
   */
  @Transactional
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
    
    var exam = sessionService.loadExamForSession(session);

    var answers = answerRepository.findBySessionId(sessionId);
    
    Map<String, String> answerMap = answers.stream()
      .collect(Collectors.toMap(
        ExamAnswer::getQuestionId,
        ExamAnswer::getAnswer
      ));

    int totalCorrect = 0;
    int totalQuestions = 0;

    List<ExamSectionResult> sectionResults = new ArrayList<>();

    for (var section : exam.getSections()) {
      
      int correct = 0;
      int total = section.getQuestions().size();

      for (var q : section.getQuestions()) {

        if (answerMap.containsKey(q.getId())) {

          if (scoringEngine.isCorrect(q, answerMap.get(q.getId()))) {
            correct++;
          }
        }
      }

      totalCorrect += correct;
      totalQuestions += total;

      double score = total == 0 ? 0 : (correct * 100.0) / total;

      var sectionResult = new ExamSectionResult();
      sectionResult.setId(UUID.randomUUID());
      sectionResult.setSessionId(sessionId);
      sectionResult.setSectionId(section.getId());
      sectionResult.setTitle(section.getTitle());
      sectionResult.setCorrect(correct);
      sectionResult.setTotal(total);
      sectionResult.setScore(score);

      sectionResults.add(sectionResult);
    }

    examSectionResultRepository.saveAll(sectionResults);

    int score = totalQuestions == 0 ? 0 : (totalCorrect * 100) / totalQuestions;

    ExamResult result = new ExamResult();
    result.setId(UUID.randomUUID());
    result.setSessionId(sessionId);
    result.setScore(score);
    result.setCorrect(totalCorrect);
    result.setTotal(totalQuestions);
    result.setPassed(score >= 70);
    result.setSubmittedAt(Instant.now());

    resultRepository.save(result);

    sessionService.markAsSubmitted(session);

    var sectionEntities = examSectionResultRepository.findBySessionId(sessionId);

    var sections = sectionEntities.stream()
      .map(s -> new SectionResultDto(
        s.getSectionId(),
        s.getTitle(),
        s.getCorrect(),
        s.getTotal(),
        s.getScore()
      )).toList();

    return new ExamResultResponse(
      result.getScore(), 
      result.getCorrect(), 
      result.getTotal(), 
      result.getPassed(),
      sections
    );
  }

  /**
   * Submits an exam initiated by a user request.
   *
   * @param sessionId session id
   * @return computed exam result response
   */
  public ExamResultResponse submitExam(UUID sessionId) {
    // user-triggered
    return submitExam(sessionId, false);
  }

  /**
   * Fetches a previously stored exam result.
   *
   * @param sessionId session id
   * @return existing exam result response
   */
  public ExamResultResponse getResult(UUID sessionId) {

    var result = resultRepository.findBySessionId(sessionId)
      .orElseThrow(() -> new RuntimeException("Exam result not found"));
    
    var session = sessionService.getSession(sessionId);

    var exam = sessionService.loadExamForSession(session);

    var answers = answerRepository.findBySessionId(sessionId);

    Map<String, String> answerMap = answers.stream()
      .collect(Collectors.toMap(
        ExamAnswer::getQuestionId,
        ExamAnswer::getAnswer
      ));

    var sectionResults = exam.getSections().stream().map(section -> {

      int correct = 0;
      int total = section.getQuestions().size();

      for (var q : section.getQuestions()) {

        if (answerMap.containsKey(q.getId())) {

          if (scoringEngine.isCorrect(q, answerMap.get(q.getId()))) {
            correct++;
          }
        }
      }

      double score = total == 0 ? 0 : (correct * 100.0) / total;

      return new SectionResultDto(
        section.getId(),
        section.getTitle(),
        correct,
        total,
        score
      );
    }).toList();

    return new ExamResultResponse(
      result.getScore(),
      result.getCorrect(),
      result.getTotal(),
      result.getPassed(),
      sectionResults
    );
  }
}
