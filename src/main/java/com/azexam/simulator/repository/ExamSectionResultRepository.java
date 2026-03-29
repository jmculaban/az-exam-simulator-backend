package com.azexam.simulator.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.azexam.simulator.model.ExamSectionResult;

public interface ExamSectionResultRepository extends JpaRepository<ExamSectionResult, UUID> {
  
  List<ExamSectionResult> findBySessionId(UUID sessionId);
}
