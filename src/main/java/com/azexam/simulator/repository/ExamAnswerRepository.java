package com.azexam.simulator.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.azexam.simulator.model.ExamAnswer;

public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, UUID> {
  List<ExamAnswer> findBySessionId(UUID sessionId);
  Optional<ExamAnswer> findBySessionIdAndQuestionId(UUID sessionId, String questionId);
}
