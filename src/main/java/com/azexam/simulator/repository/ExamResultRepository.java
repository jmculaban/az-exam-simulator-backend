package com.azexam.simulator.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.azexam.simulator.model.ExamResult;

public interface ExamResultRepository extends JpaRepository<ExamResult, UUID> {
  Optional<ExamResult> findBySessionId(UUID sessionId);
}
