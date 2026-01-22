SET search_path TO milhas;

ALTER TABLE usuario
ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN verification_code VARCHAR(10),
ADD COLUMN verification_code_expiry TIMESTAMP;