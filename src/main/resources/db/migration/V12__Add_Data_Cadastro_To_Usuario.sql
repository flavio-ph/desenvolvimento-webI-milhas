ALTER TABLE milhas.usuario ADD COLUMN data_cadastro TIMESTAMP;

UPDATE milhas.usuario SET data_cadastro = CURRENT_TIMESTAMP WHERE data_cadastro IS NULL;