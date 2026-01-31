package com.web.milhas.service.impl;

import com.web.milhas.dto.dashboard.DashboardResponseDTO;
import com.web.milhas.dto.dashboard.PontosPorCartaoDTO;
import com.web.milhas.dto.dashboard.PrazoMedioRecebimentoDTO;
import com.web.milhas.dto.dashboard.ResumoPendentesDTO;
import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.StatusCompra;
import com.web.milhas.entity.enums.TipoMovimentacao;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final CompraRepository compraRepository;
    private final MovimentacaoPontosRepository movimentacaoRepository;

    public DashboardResponseDTO getDashboardData(String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        // 1. Dados básicos
        List<PontosPorCartaoDTO> pontosPorCartao = compraRepository.findPontosAgrupadosPorCartao(usuario.getId());
        Double diasMedios = movimentacaoRepository.findPrazoMedioRecebimento(usuario.getId());
        PrazoMedioRecebimentoDTO prazoMedio = new PrazoMedioRecebimentoDTO(diasMedios);

        // 2. Pontos Expirando (Próximos 30 dias)
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30);
        BigDecimal expirando = movimentacaoRepository.somarPontosExpirando(usuario.getId(), hoje, limite);

        // 3. Resumo de Pendentes (Correção do erro de LocalDate -> Integer)
        BigDecimal totalPendentes = compraRepository.somarPontosPorStatus(usuario.getId(), StatusCompra.PENDENTE);
        LocalDate proximaData = compraRepository.findProximaDataCredito(usuario.getId(), StatusCompra.PENDENTE);
        
        Integer diasParaCredito = null;
        if (proximaData != null) {
            // Calcula a diferença em dias entre hoje e a data prevista
            diasParaCredito = (int) ChronoUnit.DAYS.between(hoje, proximaData);
            if (diasParaCredito < 0) diasParaCredito = 0; 
        }

        ResumoPendentesDTO resumoPendentes = new ResumoPendentesDTO(totalPendentes, diasParaCredito);

        // 4. Lista Unificada (Histórico Real + Pendentes)
        List<MovimentacaoPontosResponse> todas = new ArrayList<>();

        // Histórico Real
        movimentacaoRepository.findBySaldoPontosUsuarioIdOrderByDataMovimentacaoDesc(usuario.getId())
                .forEach(m -> todas.add(new MovimentacaoPontosResponse(
                        m.getId(), m.getTipo(), m.getQuantidadePontos(),
                        m.getDataMovimentacao(), m.getDescricao(),
                        m.getSaldoPontos().getProgramaPontos().getNome())));

        // Compras Pendentes
        compraRepository.findByCartaoUsuarioId(usuario.getId()).stream()
                .filter(c -> c.getStatus() == StatusCompra.PENDENTE)
                .forEach(c -> todas.add(new MovimentacaoPontosResponse(
                        c.getId(), TipoMovimentacao.ACUMULO, c.getPontosCalculados(),
                        c.getDataCompra().atStartOfDay(), c.getDescricao() + " (Pendente)",
                        c.getCartao().getProgramaPontos().getNome())));

        // Ordenação e limite
        List<MovimentacaoPontosResponse> ultimas = todas.stream()
                .sorted((a, b) -> b.dataMovimentacao().compareTo(a.dataMovimentacao()))
                .limit(5)
                .toList();

        return new DashboardResponseDTO(pontosPorCartao, prazoMedio, expirando, resumoPendentes, ultimas);
    }
}