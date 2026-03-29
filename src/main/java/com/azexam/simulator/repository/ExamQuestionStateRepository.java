package com.azexam.simulator.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.azexam.simulator.model.ExamQuestionState;

public interface ExamQuestionStateRepository extends JpaRepository<ExamQuestionState, UUID> {
  
  List<ExamQuestionState> findBySessionId(UUID sessionId);

  Optional<ExamQuestionState> findBySessionIdAndQuestionId(UUID sessionId, String questionId);
}
