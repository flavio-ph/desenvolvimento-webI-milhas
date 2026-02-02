package com.web.milhas.service.impl;

import com.web.milhas.dto.dashboard.DashboardResponseDTO;
import com.web.milhas.dto.dashboard.HistoricoMensalDTO;
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

        List<PontosPorCartaoDTO> pontosPorCartao = compraRepository.findPontosAgrupadosPorCartao(usuario.getId());
        Double diasMedios = movimentacaoRepository.findPrazoMedioRecebimento(usuario.getId());
        PrazoMedioRecebimentoDTO prazoMedio = new PrazoMedioRecebimentoDTO(diasMedios);

        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30);
        BigDecimal expirando = movimentacaoRepository.somarPontosExpirando(usuario.getId(), hoje, limite);

        BigDecimal totalPendentes = compraRepository.somarPontosPorStatus(usuario.getId(), StatusCompra.PENDENTE);
        LocalDate proximaData = compraRepository.findProximaDataCredito(usuario.getId(), StatusCompra.PENDENTE);
        
        Integer diasParaCredito = null;
        if (proximaData != null) {
            diasParaCredito = (int) ChronoUnit.DAYS.between(hoje, proximaData);
            if (diasParaCredito < 0) diasParaCredito = 0; 
        }

        ResumoPendentesDTO resumoPendentes = new ResumoPendentesDTO(totalPendentes, diasParaCredito);

        List<MovimentacaoPontosResponse> todas = new ArrayList<>();

        movimentacaoRepository.findBySaldoPontosUsuarioIdOrderByDataMovimentacaoDesc(usuario.getId())
                .forEach(m -> todas.add(new MovimentacaoPontosResponse(
                        m.getId(), m.getTipo(), m.getQuantidadePontos(),
                        m.getDataMovimentacao(), m.getDescricao(),
                        m.getSaldoPontos().getProgramaPontos().getNome())));

        compraRepository.findByCartaoUsuarioId(usuario.getId()).stream()
                .filter(c -> c.getStatus() == StatusCompra.PENDENTE)
                .forEach(c -> todas.add(new MovimentacaoPontosResponse(
                        c.getId(), TipoMovimentacao.ACUMULO, c.getPontosCalculados(),
                        c.getDataCompra().atStartOfDay(), c.getDescricao() + " (Pendente)",
                        c.getCartao().getProgramaPontos().getNome())));

        List<MovimentacaoPontosResponse> ultimas = todas.stream()
                .sorted((a, b) -> b.dataMovimentacao().compareTo(a.dataMovimentacao()))
                .limit(5)
                .toList();

        List<Object[]> dadosGrafico = movimentacaoRepository.findHistoricoAcumuloMensal(usuario.getId());
        List<HistoricoMensalDTO> historicoPontos = dadosGrafico.stream()
            .map(obj -> new HistoricoMensalDTO(
                    (String) obj[0], 
                    (BigDecimal) obj[1]
            ))
            .toList();
        return new DashboardResponseDTO(pontosPorCartao, prazoMedio, expirando, resumoPendentes, ultimas, historicoPontos);
    }
}