CREATE TABLE exam_question_state (
  id UUID PRIMARY KEY,
  session_id UUID NOT NULL,
  question_id VARCHAR(255) NOT NULL,
  visited BOOLEAN DEFAULT FALSE,
  flagged BOOLEAN DEFAULT FALSE,

  CONSTRAINT fk_exam_question_state_session
    FOREIGN KEY (session_id) REFERENCES exam_session(id)
);