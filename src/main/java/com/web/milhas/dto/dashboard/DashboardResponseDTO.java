package com.web.milhas.dto.dashboard;

import java.util.List;

import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;

public record DashboardResponseDTO (
        List<PontosPorCartaoDTO> pontosPorCartao,
        PrazoMedioRecebimentoDTO prazoMedio
) { }
