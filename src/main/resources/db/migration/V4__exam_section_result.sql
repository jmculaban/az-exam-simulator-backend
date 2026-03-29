CREATE TABLE exam_section_result (
  id UUID PRIMARY KEY,
  session_id UUID NOT NULL,
  section_id VARCHAR(255),
  title VARCHAR(255),
  correct INT,
  total INT,
  score DOUBLE PRECISION,

  CONSTRAINT fk_exam_section_result_session
    FOREIGN KEY (session_id) REFERENCES exam_session(id)
);