CREATE TABLE exam_question_state (
  id UUID PRIMARY KEY,
  session_id UUID NOT NULL,
  question_id VARCHAR(255) NOT NULL,
  visited BOOLEAN DEFAULT FALSE,
  flagged BOOLEAN DEFAULT FALSE,
);