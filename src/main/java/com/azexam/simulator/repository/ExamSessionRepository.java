package com.azexam.simulator.repository;

import com.azexam.simulator.model.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, UUID> {
  List<ExamSession> findByUser_Id(UUID userId);

  @Query("""
      SELECT s FROM ExamSession s
      WHERE s.status = 'IN_PROGRESS'
  """)
  List<ExamSession> findActiveSessions();
}
