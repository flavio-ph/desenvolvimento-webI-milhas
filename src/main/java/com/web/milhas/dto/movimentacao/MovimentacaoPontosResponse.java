package com.web.milhas.dto.movimentacao;

import com.web.milhas.entity.enums.TipoMovimentacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoPontosResponse(
    Long id,
    TipoMovimentacao tipo,
    BigDecimal quantidadePontos,
    LocalDateTime dataMovimentacao,
    String descricao
) {}