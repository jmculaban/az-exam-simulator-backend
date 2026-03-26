CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE exam_session (
  id UUID PRIMARY KEY,
  user_id UUID,
  exam_code VARCHAR(50),
  status VARCHAR(20),
  duration_minutes INT,
  start_time TIMESTAMP,
  end_time TIMESTAMP,

  CONSTRAINT fk_session_user
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE exam_answer (
  id UUID PRIMARY KEY,
  session_id UUID,
  question_id VARCHAR(50),
  answer JSONB,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(255),

  CONSTRAINT fk_answer_session
    FOREIGN KEY (session_id) REFERENCES exam_session(id)
);

CREATE TABLE exam_result (
  id UUID PRIMARY KEY,
  session_id UUID UNIQUE,
  score INT,
  correct INT,
  total INT,
  passed BOOLEAN,
  submitted_at TIMESTAMP,

  CONSTRAINT fk_result_session
    FOREIGN KEY (session_id) REFERENCES exam_session(id)
);