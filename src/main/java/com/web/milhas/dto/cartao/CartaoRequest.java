package com.web.milhas.dto.cartao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CartaoRequest(
    @NotBlank(message = "O nome personalizado do cartão é obrigatório")
    String nomePersonalizado,

    @NotBlank(message = "Os últimos 4 dígitos são obrigatórios")
    @Size(min = 4, max = 4, message = "Deve conter exatamente 4 dígitos")
    String ultimosDigitos,

    @NotNull(message = "O fator de conversão é obrigatório")
    BigDecimal fatorConversao,

    @NotNull(message = "O ID da bandeira é obrigatório")
    Long bandeiraId,

    @NotNull(message = "O ID do programa de pontos é obrigatório")
    Long programaPontosId
) {}