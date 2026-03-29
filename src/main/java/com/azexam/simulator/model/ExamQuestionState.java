package com.azexam.simulator.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "exam_question_state")
@Data
public class ExamQuestionState {
  
  @Id
  private UUID id;

  private UUID sessionId;
  private String questionId;

  private boolean visited;
  private boolean flagged;
}
