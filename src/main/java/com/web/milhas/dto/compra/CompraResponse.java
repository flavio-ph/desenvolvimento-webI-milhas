package com.web.milhas.dto.compra;

import com.web.milhas.entity.enums.StatusCompra;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CompraResponse(
    Long id,
    String descricao,
    BigDecimal valorGasto,
    BigDecimal pontosCalculados,
    LocalDate dataCompra,
    LocalDate dataCreditoPrevista,
    StatusCompra status,
    Long cartaoId,
    String nomeCartao
) {}