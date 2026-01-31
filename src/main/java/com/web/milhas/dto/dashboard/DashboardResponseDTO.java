package com.web.milhas.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;

public record DashboardResponseDTO (
        List<PontosPorCartaoDTO> pontosPorCartao,
        PrazoMedioRecebimentoDTO prazoMedio,
        BigDecimal pontosExpirando,
        List<MovimentacaoPontosResponse> ultimasMovimentacoes
) { }
