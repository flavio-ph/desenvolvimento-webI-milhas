package com.web.milhas.service;

import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.MovimentacaoPontosEntity;
import com.web.milhas.entity.SaldoPontosEntity;
import com.web.milhas.entity.enums.TipoMovimentacao;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoPontosRepository movimentacaoRepository;

    @Transactional
    public void registrarMovimentacao(SaldoPontosEntity saldo, TipoMovimentacao tipo, BigDecimal quantidade, String descricao, CompraEntity compraOrigem) {
        MovimentacaoPontosEntity mov = new MovimentacaoPontosEntity();
        mov.setSaldoPontos(saldo);
        mov.setTipo(tipo);
        mov.setQuantidadePontos(quantidade);
        mov.setDescricao(descricao);
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setCompra(compraOrigem);

        movimentacaoRepository.save(mov);
    }
}