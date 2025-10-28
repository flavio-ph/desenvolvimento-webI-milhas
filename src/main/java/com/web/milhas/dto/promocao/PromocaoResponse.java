package com.web.milhas.dto.promocao;

import java.time.LocalDate;

public record PromocaoResponse(
    Long id,
    String titulo,
    String descricao,
    String urlPromocao,
    LocalDate dataInicio,
    LocalDate dataFim,
    String nomeProgramaPontos
) {}