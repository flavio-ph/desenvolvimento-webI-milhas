package com.web.milhas.dto.compra;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.web.milhas.entity.enums.StatusCompra;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CompraResponse(
        Long id,
        String descricao,
        BigDecimal valorGasto,
        BigDecimal pontosCalculados,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataCompra,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataCreditoPrevista,

        StatusCompra status,
        Long cartaoId,
        String nomeCartao,
        Integer diasParaCredito
) {}