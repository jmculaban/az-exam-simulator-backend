package com.azexam.simulator.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "exam_answer")
public class ExamAnswer {
  
  @Id
  private UUID id;

  @Column(name = "session_id")
  private UUID sessionId;

  @Column(name = "question_id")
  private String questionId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "answer", columnDefinition = "jsonb")
  private String answer;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "updated_by")
  private String updatedBy;
}
