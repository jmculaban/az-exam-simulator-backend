package com.azexam.simulator.service;

import java.time.LocalDateTime;
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
  
  public ExamSessionService(
    ExamSessionRepository sessionRepository,
    UserRepository userRepository
  ) {
    this.sessionRepository = sessionRepository;
    this.userRepository = userRepository;
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

  public ExamSession getSession(UUID id) {
    return sessionRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Exam session not found"));
  }

  public List<ExamSession> getSessionByUserId(UUID userId) {
    return sessionRepository.findByUser_Id(userId);
  }
}
