SET search_path TO milhas;

ALTER TABLE movimentacao_pontos
ADD COLUMN compra_id BIGINT;

ALTER TABLE movimentacao_pontos
ADD CONSTRAINT fk_movimentacao_compra
FOREIGN KEY (compra_id) REFERENCES compra(id);

ALTER TABLE notificacao
ADD COLUMN compra_id BIGINT;

ALTER TABLE notificacao
ADD CONSTRAINT fk_notificacao_compra
FOREIGN KEY (compra_id) REFERENCES compra(id);