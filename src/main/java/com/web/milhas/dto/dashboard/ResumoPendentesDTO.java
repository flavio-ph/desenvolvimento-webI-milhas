package com.web.milhas.dto.dashboard;

import java.math.BigDecimal;

public record ResumoPendentesDTO(
    BigDecimal totalPontos,
    Integer diasParaProximoCredito 
) {}