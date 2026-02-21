package com.web.milhas.service.impl;

import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.MovimentacaoPontosEntity;
import com.web.milhas.entity.PromocaoEntity;
import com.web.milhas.entity.SaldoPontosEntity;
import com.web.milhas.entity.enums.TipoMovimentacao;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.mapper.MovimentacaoMapper;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import com.web.milhas.repository.ParticipacaoPromocaoRepository;
import com.web.milhas.repository.SaldoPontosRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovimentacaoServiceImpl implements MovimentacaoService {

    private final MovimentacaoPontosRepository movimentacaoRepository;
    private final SaldoPontosRepository saldoPontosRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParticipacaoPromocaoRepository participacaoPromocaoRepository;
    private final MovimentacaoMapper movimentacaoMapper;

    @Override
    @Transactional
    public void registrarMovimentacao(SaldoPontosEntity saldo, TipoMovimentacao tipo, BigDecimal quantidade, String descricao, CompraEntity compraOrigem) {
        MovimentacaoPontosEntity mov = new MovimentacaoPontosEntity();
        mov.setSaldoPontos(saldo);
        mov.setTipo(tipo);
        mov.setQuantidadePontos(quantidade);
        mov.setDescricao(descricao);
        mov.setCompra(compraOrigem);

        movimentacaoRepository.save(mov);
    }

    @Override
    public Page<MovimentacaoPontosResponse> listarMovimentacoes(
            String emailUsuario,
            Integer mes,
            Integer ano,
            String programa,
            String status,
            Pageable pageable) {

        if (!usuarioRepository.existsByEmail(emailUsuario)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }

        String filtroPrograma = (programa == null || programa.equals("ALL") || programa.isEmpty()) ? null : programa;

        Page<MovimentacaoPontosEntity> paginaEntidades = movimentacaoRepository.filtrarMovimentacoes(
                emailUsuario, mes, ano, filtroPrograma, pageable);

        return paginaEntidades.map(movimentacaoMapper::toResponse);
    }

    @Override
    @Transactional
    public void gerarCreditoCompra(CompraEntity compra) {

        Long usuarioId = compra.getCartao().getUsuario().getId();
        Long programaId = compra.getCartao().getProgramaPontos().getId();
        LocalDate dataReferencia = compra.getDataCompra();

        // Correção aplicada: Inicialização dinâmica (Lazy) do saldo de pontos caso não exista
        SaldoPontosEntity saldo = saldoPontosRepository.findByUsuarioIdAndProgramaPontosId(usuarioId, programaId)
                .orElseGet(() -> {
                    SaldoPontosEntity novoSaldo = new SaldoPontosEntity();
                    novoSaldo.setUsuario(compra.getCartao().getUsuario());
                    novoSaldo.setProgramaPontos(compra.getCartao().getProgramaPontos());
                    novoSaldo.setTotalPontos(BigDecimal.ZERO);
                    return saldoPontosRepository.save(novoSaldo);
                });

        BigDecimal pontosBase = compra.getPontosCalculados();
        BigDecimal pontosFinais = pontosBase;
        String descricaoFinal = compra.getDescricao();

        Optional<PromocaoEntity> promocaoOpt = participacaoPromocaoRepository
                .findPromocaoAtivaParaUsuario(usuarioId, programaId, dataReferencia);

        if (promocaoOpt.isPresent()) {
            PromocaoEntity promo = promocaoOpt.get();

            BigDecimal bonus = promo.getBonusPorcentagem();
            BigDecimal multiplicador = BigDecimal.ONE.add(
                    bonus.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
            );

            pontosFinais = pontosBase.multiply(multiplicador);
            descricaoFinal += " (Bônus Promo: " + promo.getTitulo() + ")";
        }

        saldo.setTotalPontos(saldo.getTotalPontos().add(pontosFinais));
        saldoPontosRepository.save(saldo);

        MovimentacaoPontosEntity mov = new MovimentacaoPontosEntity();
        mov.setTipo(TipoMovimentacao.ACUMULO);
        mov.setQuantidadePontos(pontosFinais);
        mov.setDescricao(descricaoFinal);
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
}