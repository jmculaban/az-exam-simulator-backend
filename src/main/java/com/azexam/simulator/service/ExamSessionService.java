package com.azexam.simulator.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.model.User;
import com.azexam.simulator.repository.ExamSessionRepository;
import com.azexam.simulator.repository.UserRepository;

@Service
public class ExamSessionService {
  
  private final ExamSessionRepository sessionRepository;
  private final UserRepository userRepository;
  private final QuestionLoaderService questionLoader;
  
  public ExamSessionService(
    ExamSessionRepository sessionRepository,
    UserRepository userRepository,
    QuestionLoaderService questionLoader
  ) {
    this.sessionRepository = sessionRepository;
    this.userRepository = userRepository;
    this.questionLoader = questionLoader;
  }

  /**
   * Creates a new in-progress exam session for a user.
   *
   * @param examCode exam identifier
   * @param userId user id
   * @return persisted exam session
   */
  public ExamSession createSession(String examCode, UUID userId) {
    
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));
    
    var exam = questionLoader.loadExam(examCode);
    
    ExamSession session = new ExamSession();
    session.setId(UUID.randomUUID());
    session.setExamCode(examCode);
    session.setUser(user);
    session.setStartTime(Instant.now());
    session.setStatus("IN_PROGRESS");

    // Set the duration for the exam session
    session.setDurationMinutes(exam.getDurationMinutes());

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
}
