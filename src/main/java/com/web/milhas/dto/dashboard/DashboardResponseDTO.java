package com.web.milhas.dto.dashboard;

import java.util.List;

public record DashboardResponseDTO (
        List<PontosPorCartaoDTO> pontosPorCartao,
        PrazoMedioRecebimentoDTO prazoMedio
) { }
