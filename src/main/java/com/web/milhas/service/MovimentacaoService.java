package com.web.milhas.service;

import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.SaldoPontosEntity;
import com.web.milhas.entity.enums.TipoMovimentacao;

import java.math.BigDecimal;
import java.util.List;

public interface MovimentacaoService {


    void registrarMovimentacao(
            SaldoPontosEntity saldo,
            TipoMovimentacao tipo,
            BigDecimal quantidade,
            String descricao,
            CompraEntity compraOrigem
    );
    void gerarCreditoCompra(CompraEntity compra);
    List<MovimentacaoPontosResponse> listarMovimentacoes(String emailUsuario);
    BigDecimal consultarPontosExpirando(String emailUsuario, int dias);
}