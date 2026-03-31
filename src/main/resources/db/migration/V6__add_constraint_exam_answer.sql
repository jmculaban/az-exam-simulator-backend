ALTER TABLE exam_answer
ADD CONSTRAINT uq_exam_answer
UNIQUE (session_id, question_id);