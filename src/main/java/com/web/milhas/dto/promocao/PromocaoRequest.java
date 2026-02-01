package com.web.milhas.dto.promocao;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PromocaoRequest(
        @NotBlank(message = "O título é obrigatório")
        String titulo,
        String descricao,
        String urlPromocao,
        @NotNull Double bonusPorcentagem,
        @NotNull(message = "A data de início é obrigatória") LocalDate dataInicio,
        @NotNull @FutureOrPresent LocalDate dataFim,
        @NotNull(message = "O ID do programa de pontos é obrigatório")
        Long programaPontosId) {
}
