SET search_path TO milhas;

CREATE TABLE bandeira (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE programa_pontos (
   id BIGSERIAL PRIMARY KEY,
   nome VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE cartao (
    id BIGSERIAL PRIMARY KEY,
    nome_personalizado VARCHAR(255) NOT NULL,
    ultimos_digitos VARCHAR(4),
    fator_conversao DECIMAL(10, 2),
    usuario_id BIGINT NOT NULL,
    bandeira_id BIGINT NOT NULL,
    programa_pontos_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (bandeira_id) REFERENCES bandeira(id),
    FOREIGN KEY (programa_pontos_id) REFERENCES programa_pontos(id)
);

CREATE TABLE saldo_pontos (
    id BIGSERIAL PRIMARY KEY,
    total_pontos DECIMAL(10, 2) NOT NULL,
    usuario_id BIGINT NOT NULL,
    programa_pontos_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (programa_pontos_id) REFERENCES programa_pontos(id)
);

CREATE TABLE compra (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255),
    valor_gasto DECIMAL(10, 2) NOT NULL,
    pontos_calculados DECIMAL(10, 2),
    data_compra DATE NOT NULL,
    data_credito_prevista DATE,
    status VARCHAR(50) NOT NULL,
    cartao_id BIGINT NOT NULL,
    FOREIGN KEY (cartao_id) REFERENCES cartao(id)
);

CREATE TABLE comprovante_compra (
    id BIGSERIAL PRIMARY KEY,
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_arquivo VARCHAR(100) NOT NULL,
    url_arquivo VARCHAR(255) NOT NULL,
    compra_id BIGINT NOT NULL,
    FOREIGN KEY (compra_id) REFERENCES compra(id)
);

CREATE TABLE movimentacao_pontos (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    quantidade_pontos DECIMAL(10, 2) NOT NULL,
    data_movimentacao TIMESTAMP NOT NULL,
    descricao VARCHAR(255),
    saldo_pontos_id BIGINT NOT NULL,
    FOREIGN KEY (saldo_pontos_id) REFERENCES saldo_pontos(id)
);

CREATE TABLE notificacao (
     id BIGSERIAL PRIMARY KEY,
     mensagem VARCHAR(255) NOT NULL,
     lida BOOLEAN NOT NULL DEFAULT false,
     tipo VARCHAR(50) NOT NULL,
     data_envio TIMESTAMP NOT NULL,
     usuario_id BIGINT NOT NULL,
     FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE promocao (
     id BIGSERIAL PRIMARY KEY,
     titulo VARCHAR(255) NOT NULL,
     descricao TEXT,
     url_promocao VARCHAR(255),
     data_inicio DATE,
     data_fim DATE,
     programa_pontos_id BIGINT NOT NULL,
     FOREIGN KEY (programa_pontos_id) REFERENCES programa_pontos(id)
);