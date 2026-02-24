package com.web.milhas.service;

import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.SaldoPontosEntity;
import com.web.milhas.entity.enums.TipoMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface MovimentacaoService {

    void registrarMovimentacao(SaldoPontosEntity saldo, TipoMovimentacao tipo, BigDecimal quantidade, String descricao, CompraEntity compraOrigem);
    void gerarCreditoCompra(CompraEntity compra);
    Page<MovimentacaoPontosResponse> listarMovimentacoes(
            String emailUsuario,
            Integer mes,
            Integer ano,
            String programa,
            Long cartaoId,
            String status,
            Pageable pageable);
    BigDecimal consultarPontosExpirando(String emailUsuario, int dias);
}