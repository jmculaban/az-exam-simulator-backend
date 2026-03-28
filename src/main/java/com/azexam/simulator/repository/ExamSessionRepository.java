package com.azexam.simulator.repository;

import com.azexam.simulator.model.ExamSession;
import com.azexam.simulator.dto.UserExamHistoryResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, UUID> {
  List<ExamSession> findByUser_Id(UUID userId);

  @Query("""
      SELECT s FROM ExamSession s
      WHERE s.status = 'IN_PROGRESS'
  """)
  List<ExamSession> findActiveSessions();

  @Query("""
      SELECT new com.azexam.simulator.dto.UserExamHistoryResponse(
        s.id,
        s.examCode,
        r.score,
        r.passed,
        r.submittedAt
      )
      FROM ExamResult r
      JOIN r.session s
      WHERE s.user.id = :userId
        AND (:passed IS NULL OR r.passed = :passed)
        AND (:examCode IS NULL OR s.examCode = :examCode)
      ORDER BY r.submittedAt DESC
  """)
  Page<UserExamHistoryResponse> findUserExamHistory(
    UUID userId, 
    Boolean passed, 
    String examCode, 
    Pageable pageable
  );
}
