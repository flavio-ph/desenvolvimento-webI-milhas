package com.web.milhas.dto.dashboard;

import java.math.BigDecimal;

public record HistoricoMensalDTO(
    String mes,
    BigDecimal pontos
) {}