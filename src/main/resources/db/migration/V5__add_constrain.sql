ALTER TABLE exam_question_state
ADD CONSTRAINT uq_session_question
UNIQUE (session_id, question_id);