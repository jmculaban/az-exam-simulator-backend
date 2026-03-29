package com.azexam.simulator.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.azexam.simulator.repository.ExamSessionRepository;

/**
 * Periodically checks active sessions and submits expired exams.
 */
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
  /**
   * Scheduled job that auto-submits sessions whose timer has elapsed.
   */
  @Scheduled(fixedRate = 30000)
  public void autoSubmitExpiredExams() {

    System.out.println("Running auto-submit task at " + Instant.now());
    var sessions = examSessionRepository.findActiveSessions();

    System.out.println("Found " + sessions.size() + " active sessions to check");

    for (var session : sessions) {

      System.out.println("Checking session " + session.getId() + " started at " + session.getStartTime());

      if (!"IN_PROGRESS".equals(session.getStatus())) {
        continue;
      }

      var startTime = session.getStartTime();
      var duration = session.getDurationMinutes();

      if (startTime == null || duration == null) {
        System.out.println("Skipping session " + session.getId() + " due to missing start time or duration");
        continue;
      }

      var endTime = startTime.plus(Duration.ofMinutes(duration));

      if (Instant.now().isAfter(endTime)) {
        try {
          System.out.println("Auto-submitting: " + session.getId());
          examResultService.submitExam(session.getId(), true);
        } catch (Exception e) {
          // Handle exception
          System.out.println("Error auto-submitting session " + session.getId() + ": " + e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }
}
