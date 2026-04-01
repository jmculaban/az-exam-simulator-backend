package com.azexam.simulator.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.model.User;
import com.azexam.simulator.model.yaml.ExamYaml;
import com.azexam.simulator.repository.ExamSessionRepository;
import com.azexam.simulator.repository.UserRepository;

/**
 * Manages creation and lifecycle updates of exam sessions.
 */
@Service
public class ExamSessionService {
  
  private final ExamSessionRepository sessionRepository;
  private final UserRepository userRepository;
  private final QuestionLoaderService questionLoader;
  private final UserService userService;
  
  public ExamSessionService(
    ExamSessionRepository sessionRepository,
    UserRepository userRepository,
    QuestionLoaderService questionLoader,
    UserService userService
  ) {
    this.sessionRepository = sessionRepository;
    this.userRepository = userRepository;
    this.questionLoader = questionLoader;
    this.userService = userService;
  }

  /**
   * Creates a new in-progress exam session for a user.
   *
   * @param examCode exam identifier
   * @param userId external user id string
   * @return persisted exam session
   */
  public ExamSession createSession(String examCode, String userId) {

    UUID internalUserId = userService.toInternalUserId(userId);
    
    User user = userRepository.findById(internalUserId)
      .orElseThrow(() -> new RuntimeException("User not found"));
    
    var exam = questionLoader.loadExam(examCode);
    int poolSize = questionLoader.getTotalQuestionCount(exam);
    int randomQuestionCount = exam.getRandomQuestionCount();

    if (poolSize < randomQuestionCount) {
      throw new RuntimeException(
        "Question bank must contain at least " + randomQuestionCount + " questions"
      );
    }

    var selectedQuestionIds = questionLoader.selectRandomQuestionIdsByWeight(exam, randomQuestionCount);
    
    ExamSession session = new ExamSession();
    session.setId(UUID.randomUUID());
    session.setExamCode(examCode);
    session.setUser(user);
    session.setStartTime(Instant.now());
    session.setStatus("IN_PROGRESS");

    // Set the duration for the exam session
    session.setDurationMinutes(exam.getDurationMinutes());
    session.setSelectedQuestionIds(selectedQuestionIds);

    return sessionRepository.save(session);
  }

  /**
   * Fetches an exam session by id.
   *
   * @param id session id
   * @return existing exam session
   */
  public ExamSession getSession(UUID id) {
    return sessionRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Exam session not found"));
  }

  /**
   * Retrieves all sessions for a specific user.
   *
   * @param userId user id
   * @return user sessions
   */
  public List<ExamSession> getSessionByUserId(UUID userId) {
    return sessionRepository.findByUser_Id(userId);
  }

  /**
   * Marks a session as submitted and stamps completion time.
   *
   * @param session session to update
   */
  public void markAsSubmitted(ExamSession session) {
    session.setStatus("SUBMITTED");
    session.setEndTime(Instant.now());
    sessionRepository.save(session);
  }

  /**
   * Loads and applies the session's frozen question subset.
   */
  public ExamYaml loadExamForSession(ExamSession session) {
    var exam = questionLoader.loadExam(session.getExamCode());
    var selectedIds = session.getSelectedQuestionIds();

    if (selectedIds == null || selectedIds.isEmpty()) {
      return exam;
    }

    return questionLoader.filterExamByQuestionIds(exam, selectedIds);
  }
}
