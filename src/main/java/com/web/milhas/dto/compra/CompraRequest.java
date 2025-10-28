package com.web.milhas.dto.compra;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CompraRequest(
    @NotBlank(message = "A descrição da compra é obrigatória")
    String descricao,

    @NotNull(message = "O valor da compra é obrigatório")
    BigDecimal valorGasto,

    @NotNull(message = "A data da compra é obrigatória")
    @PastOrPresent(message = "A data da compra não pode ser no futuro")
    LocalDate dataCompra,

    @NotNull(message = "O ID do cartão é obrigatório")
    Long cartaoId
) {}