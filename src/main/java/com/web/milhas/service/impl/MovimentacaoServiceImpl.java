package com.web.milhas.service.impl;

import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.MovimentacaoPontosEntity;
import com.web.milhas.entity.SaldoPontosEntity;
import com.web.milhas.entity.enums.TipoMovimentacao;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import com.web.milhas.repository.SaldoPontosRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoServiceImpl implements MovimentacaoService {

    private final MovimentacaoPontosRepository movimentacaoRepository;
    private final SaldoPontosRepository saldoPontosRepository;
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
    public List<MovimentacaoPontosResponse> listarMovimentacoes(
            String emailUsuario,
            Integer mes,
            Integer ano,
            String programa,
            String status) {

        // Validação se o usuário existe
        if (!usuarioRepository.existsByEmail(emailUsuario)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }

        // Lógica para tratar o filtro "Todos" (ALL) vindo do front
        String filtroPrograma = (programa == null || programa.equals("ALL") || programa.isEmpty()) ? null : programa;

        // Chamada ao repositório com os filtros
        // Certifique-se de que seu MovimentacaoPontosRepository tem este método com esta assinatura
        return movimentacaoRepository.filtrarMovimentacoes(emailUsuario, mes, ano, filtroPrograma)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public void gerarCreditoCompra(CompraEntity compra) {

        Long usuarioId = compra.getCartao().getUsuario().getId();
        Long programaId = compra.getCartao().getProgramaPontos().getId();

        SaldoPontosEntity saldo = saldoPontosRepository.findByUsuarioIdAndProgramaPontosId(usuarioId, programaId)
                .orElseThrow(() -> new ResourceNotFoundException("Saldo de pontos não encontrado para o usuário/programa."));

        saldo.setTotalPontos(saldo.getTotalPontos().add(compra.getPontosCalculados()));
        saldoPontosRepository.save(saldo);

        MovimentacaoPontosEntity mov = new MovimentacaoPontosEntity();

        mov.setTipo(TipoMovimentacao.ACUMULO);
        mov.setQuantidadePontos(compra.getPontosCalculados());
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setDescricao(compra.getDescricao());

        mov.setDataValidade(LocalDate.now().plusMonths(24));

        mov.setSaldoPontos(saldo);
        mov.setCompra(compra);

        movimentacaoRepository.save(mov);
    }

    @Override
    public BigDecimal consultarPontosExpirando(String emailUsuario, int dias) {
        var usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.plusDays(dias);

        return movimentacaoRepository.somarPontosExpirando(usuario.getId(), hoje, dataLimite);
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