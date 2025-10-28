package com.web.milhas.dto.cartao;

import java.math.BigDecimal;

public record CartaoResponse(
    Long id,
    String nomePersonalizado,
    String ultimosDigitos,
    BigDecimal fatorConversao,
    String nomeBandeira,
    String nomeProgramaPontos
) {}