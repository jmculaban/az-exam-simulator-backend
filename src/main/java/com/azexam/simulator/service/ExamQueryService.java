package com.azexam.simulator.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.azexam.simulator.dto.ExamProgressResponse;
import com.azexam.simulator.dto.ExamTimerResponse;
import com.azexam.simulator.dto.NavigationDto;
import com.azexam.simulator.dto.QuestionResponse;
import com.azexam.simulator.dto.ResumeExamResponse;
import com.azexam.simulator.dto.SectionResponseDto;
import com.azexam.simulator.dto.UserExamHistoryResponse;
import com.azexam.simulator.model.ExamQuestionState;
import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.model.yaml.QuestionYaml;
import com.azexam.simulator.repository.ExamAnswerRepository;
import com.azexam.simulator.repository.ExamQuestionStateRepository;
import com.azexam.simulator.repository.ExamSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExamQueryService {
  
  private final ExamSessionService examSessionService;
  private final QuestionLoaderService questionLoaderService;
  private final ExamAnswerRepository examAnswerRepository;
  private final ExamSessionRepository examSessionRepository;
  private final ExamQuestionStateRepository examQuestionStateRepository;
  private final ObjectMapper objectMapper;

  public ExamQueryService(
      ExamSessionService examSessionService,
      QuestionLoaderService questionLoaderService,
      ExamAnswerRepository examAnswerRepository,
      ExamSessionRepository examSessionRepository,
      ExamQuestionStateRepository examQuestionStateRepository,
      ObjectMapper objectMapper) {
    this.examSessionService = examSessionService;
    this.questionLoaderService = questionLoaderService;
    this.examAnswerRepository = examAnswerRepository;
    this.examSessionRepository = examSessionRepository;
    this.examQuestionStateRepository = examQuestionStateRepository;
    this.objectMapper = objectMapper;
  }

  /**
   * Builds the full payload needed by a client to resume an exam.
   *
   * @param sessionId session id
   * @return resume response including sections, question state, and timer
   */
  public ResumeExamResponse resumeExam(UUID sessionId) {

    // 1. Get session details
    var session = examSessionService.getSession(sessionId);

    // 2. Load exam questions
    var exam = questionLoaderService.loadExam(session.getExamCode());
    var states = examQuestionStateRepository.findBySessionId(sessionId);

    // 3. Load existing answers for the session from DB
    var answers = examAnswerRepository.findBySessionId(sessionId);

    // 4. Convert answer list to a map for easy lookup
    Map<String, Object> answerMap = answers.stream()
      .collect(Collectors.toMap(
        a -> a.getQuestionId(),
        a -> extractAnswer(a.getAnswer())
      ));

    // 5. Process each sections
    Map<String, ExamQuestionState> stateMap = states.stream()
      .collect(Collectors.toMap(
        s -> s.getQuestionId(),
        s -> s
      ));

    var sections = exam.getSections().stream().map(section -> {

      var questions = section.getQuestions().stream().map(q -> {

        var state = stateMap.get(q.getId());

        return new QuestionResponse(
          q.getId(),
          q.getText(),
          q.getType(),
          resolveOptions(q),
          answerMap.get(q.getId()),
          answerMap.containsKey(q.getId()),
          state != null && Boolean.TRUE.equals(state.isFlagged()),
          state != null && Boolean.TRUE.equals(state.isVisited())
        );
      }).toList();

      return new SectionResponseDto(
        section.getId(),
        section.getTitle(),
        questions
      );
    }).toList();

    // 5. Construct and return response
    return new ResumeExamResponse(
      sessionId,
      session.getStatus(),
      session.getExamCode(),
      buildTimer(session),
      sections,
      buildNavigation(sections)
    );
  }
  
  /**
   * Returns answered/total progress for a session.
   *
   * @param sessionId session id
   * @return progress response
   */
  public ExamProgressResponse getProgress(UUID sessionId) {
    
    // 1. Get session
    var session = examSessionService.getSession(sessionId);

    // 2. Load exam questions
    var exam = questionLoaderService.loadExam(session.getExamCode());
    int total = exam.getSections().stream()
      .mapToInt(s -> s.getQuestions().size())
      .sum();

    // 3. Count answers in DB
    var answers = examAnswerRepository.findBySessionId(sessionId);
    int answered = (int)answers.stream()
      .map(a -> a.getQuestionId())
      .distinct()
      .count();

    // 4. Return response
    return new ExamProgressResponse(answered, total);
  }

  /**
   * Returns timer state for a session.
   *
   * @param sessionId session id
   * @return timer response
   */
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

  /**
   * Returns paginated exam history for a user with optional filters.
   *
   * @param userId user id
   * @param passed optional pass/fail filter
   * @param examCode optional exam code filter
   * @param page zero-based page index
   * @param size page size
   * @return page of history entries
   */
  public Page<UserExamHistoryResponse> getUserExamHistory(
      UUID userId,
      Boolean passed,
      String examCode,
      int page,
      int size) {
    
    var pageable = PageRequest.of(page, size);

    return examSessionRepository.findUserExamHistory(
      userId,
      passed,
      examCode,
      pageable
    );
  }

  private Object extractAnswer(String json) {
    try {
      return objectMapper.readValue(json, Object.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse answer JSON", e);
    }
  }

  private Object resolveOptions(QuestionYaml question) {
    if (question.getOptions() != null) return question.getOptions();
    if (question.getOptionMap() != null) return question.getOptionMap();
    return null;
  }

  private NavigationDto buildNavigation(List<SectionResponseDto> sections) {

    int total = 0, answered = 0, flagged = 0, notVisited = 0;

    for (var s : sections) {
      for (var q : s.getQuestions()) {
        total++;
        if (q.isAnswered()) answered++;
        if (q.isFlagged()) flagged++;
        if (!q.isVisited()) notVisited++;
      }
    }
    
    return new NavigationDto(total, answered, flagged, notVisited);
  }

  private ExamTimerResponse buildTimer(ExamSession session) {

    if ("SUBMITTED".equals(session.getStatus())) {
      return new ExamTimerResponse(0, true);
    }

    var endTime = session.getStartTime()
      .plus(Duration.ofMinutes(session.getDurationMinutes()));

    long remaining = Duration.between(Instant.now(), endTime).getSeconds();

    return new ExamTimerResponse(Math.max(remaining, 0), remaining <= 0);
  }
}
