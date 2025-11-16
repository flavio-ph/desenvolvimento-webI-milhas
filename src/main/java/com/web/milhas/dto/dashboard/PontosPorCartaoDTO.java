package com.web.milhas.dto.dashboard;

import java.math.BigDecimal;

public record PontosPorCartaoDTO(
        Long cartaoId,
        String nomeCartao,
        BigDecimal totalPontos
) {
}
