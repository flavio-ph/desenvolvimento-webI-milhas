SET search_path TO milhas;

ALTER TABLE usuario
ADD COLUMN reset_password_token VARCHAR(255),
ADD COLUMN reset_token_expiry TIMESTAMP;