package com.web.milhas.dto.saldo;

import java.math.BigDecimal;

public record SaldoPontosResponse(
    Long id,
    String nomePrograma,
    BigDecimal totalPontos
) {}