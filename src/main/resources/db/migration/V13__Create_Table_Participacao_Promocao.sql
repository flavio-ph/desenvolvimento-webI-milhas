CREATE TABLE milhas.participacao_promocao (
      id BIGSERIAL PRIMARY KEY,
      usuario_id BIGINT NOT NULL,
      promocao_id BIGINT NOT NULL,
      data_adesao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT fk_participacao_usuario FOREIGN KEY (usuario_id) REFERENCES milhas.usuario(id),
      CONSTRAINT fk_participacao_promocao FOREIGN KEY (promocao_id) REFERENCES milhas.promocao(id),

      CONSTRAINT uk_usuario_promocao UNIQUE (usuario_id, promocao_id)
);