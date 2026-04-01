ALTER TABLE users
ADD COLUMN external_user_id VARCHAR(255);

UPDATE users
SET external_user_id = id::text
WHERE external_user_id IS NULL;

ALTER TABLE users
ALTER COLUMN external_user_id SET NOT NULL;

ALTER TABLE users
ADD CONSTRAINT uq_users_external_user_id UNIQUE (external_user_id);
