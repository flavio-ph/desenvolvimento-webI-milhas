-- =====================================================
-- V14: Índices de desempenho para queries frequentes
-- =====================================================

-- Compra: filtros por cartão, status e data de crédito
CREATE INDEX IF NOT EXISTS idx_compra_cartao_id ON milhas.compra(cartao_id);
CREATE INDEX IF NOT EXISTS idx_compra_status ON milhas.compra(status);
CREATE INDEX IF NOT EXISTS idx_compra_data_credito ON milhas.compra(data_credito_prevista);
CREATE INDEX IF NOT EXISTS idx_compra_status_data ON milhas.compra(status, data_credito_prevista);

-- Cartão: filtro por usuário
CREATE INDEX IF NOT EXISTS idx_cartao_usuario_id ON milhas.cartao(usuario_id);

-- Movimentação de pontos: filtro por usuario e data de movimentação
CREATE INDEX IF NOT EXISTS idx_movimentacao_saldo_id ON milhas.movimentacao_pontos(saldo_pontos_id);
CREATE INDEX IF NOT EXISTS idx_movimentacao_data ON milhas.movimentacao_pontos(data_movimentacao);

-- Notificação: listagem por usuário e status de leitura
CREATE INDEX IF NOT EXISTS idx_notificacao_usuario_id ON milhas.notificacao(usuario_id);
CREATE INDEX IF NOT EXISTS idx_notificacao_lida ON milhas.notificacao(lida);

-- Saldo: lookup por usuário
CREATE INDEX IF NOT EXISTS idx_saldo_usuario_id ON milhas.saldo_pontos(usuario_id);

-- Participação em promoção: lookup por usuário e promoção
CREATE INDEX IF NOT EXISTS idx_participacao_usuario_id ON milhas.participacao_promocao(usuario_id);
CREATE INDEX IF NOT EXISTS idx_participacao_promocao_id ON milhas.participacao_promocao(promocao_id);

-- Comprovante: lookup por compra
CREATE INDEX IF NOT EXISTS idx_comprovante_compra_id ON milhas.comprovante_compra(compra_id);
