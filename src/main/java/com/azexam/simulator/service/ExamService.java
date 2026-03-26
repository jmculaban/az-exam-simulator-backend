package com.azexam.simulator.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.repository.ExamSessionRepository;

@Service
public class ExamService {
  
  private final ExamSessionRepository repository;

  public ExamService(ExamSessionRepository repository) {
    this.repository = repository;
  }

  public ExamSession createSession(String examCode) {
    ExamSession session = new ExamSession();
    session.setId(UUID.randomUUID());
    session.setExamCode(examCode);
    session.setStartTime(LocalDateTime.now());
    session.setStatus("IN_PROGRESS");

    return repository.save(session);
  }
}
