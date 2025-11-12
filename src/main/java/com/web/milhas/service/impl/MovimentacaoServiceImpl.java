package com.web.milhas.service.impl;

import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.MovimentacaoPontosEntity;
import com.web.milhas.entity.SaldoPontosEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.TipoMovimentacao;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoServiceImpl implements MovimentacaoService {

    private final MovimentacaoPontosRepository movimentacaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
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

    @Override
    public List<MovimentacaoPontosResponse> listarMovimentacoes(String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        return movimentacaoRepository.findBySaldoPontosUsuarioId(usuario.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private MovimentacaoPontosResponse mapToDTO(MovimentacaoPontosEntity mov) {
        return new MovimentacaoPontosResponse(
                mov.getId(),
                mov.getTipo(),
                mov.getQuantidadePontos(),
                mov.getDataMovimentacao(),
                mov.getDescricao(),
                mov.getSaldoPontos().getProgramaPontos().getNome()
        );
    }
}