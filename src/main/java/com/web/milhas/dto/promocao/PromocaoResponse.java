package com.web.milhas.dto.promocao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PromocaoResponse(
        Long id,
        String titulo,
        String descricao,
        String urlPromocao,
        BigDecimal bonusPorcentagem,
        LocalDate dataInicio,
        LocalDate dataFim,
        String nomeProgramaPontos,
        Long programaPontosId
) {}