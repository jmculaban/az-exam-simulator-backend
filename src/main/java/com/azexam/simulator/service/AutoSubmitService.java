package com.azexam.simulator.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.azexam.simulator.repository.ExamSessionRepository;

@Service
public class AutoSubmitService {
  
  private final ExamSessionRepository examSessionRepository;
  private final ExamResultService examResultService;

  public AutoSubmitService(
    ExamSessionRepository examSessionRepository,
    ExamResultService examResultService
  ) {
    this.examSessionRepository = examSessionRepository;
    this.examResultService = examResultService;
  }

  // Runs every 30 seconds to check for expired sessions
  @Scheduled(fixedRate = 30000)
  public void autoSubmitExpiredExams() {

    var sessions = examSessionRepository.findActiveSessions();

    for (var session : sessions) {

      if (!"IN_PROGRESS".equals(session.getStatus())) {
        continue;
      }

      var startTime = session.getStartTime();
      var duration = session.getDurationMinutes();

      if (startTime == null || duration == null) {
        continue;
      }

      var endTime = startTime.plus(Duration.ofMinutes(duration));

      if (Instant.now().isAfter(endTime)) {
        try {
          examResultService.submitExam(session.getId());
        } catch (Exception e) {
          // Handle exception
        }
      }
    }
  }
}
