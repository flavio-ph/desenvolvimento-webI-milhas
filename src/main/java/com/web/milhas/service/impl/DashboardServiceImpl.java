package com.web.milhas.service.impl;

import com.web.milhas.dto.dashboard.*;
import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.MovimentacaoPontosEntity;
import com.web.milhas.entity.enums.TipoMovimentacao; 
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import com.web.milhas.repository.SaldoPontosRepository;
import com.web.milhas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SaldoPontosRepository saldoRepository;
    private final CompraRepository compraRepository;
    private final MovimentacaoPontosRepository movimentacaoRepository;

    @Override
    public DashboardResponseDTO getDashboardData(String email) {
        List<PontosPorCartaoDTO> pontosPorCartao = saldoRepository.findPontosPorPrograma(email);
        Double mediaDias = compraRepository.findMediaDiasCredito(email);
        PrazoMedioRecebimentoDTO prazoMedio = new PrazoMedioRecebimentoDTO(mediaDias != null ? mediaDias.intValue() : 0);

        List<MovimentacaoPontosEntity> ultimasEntities = movimentacaoRepository
                .findBySaldoPontosUsuarioEmailOrderByDataMovimentacaoDesc(
                        email, 
                        PageRequest.of(0, 5)
                );
        
        List<MovimentacaoPontosResponse> ultimasMovimentacoes = ultimasEntities.stream()
                .map(m -> new MovimentacaoPontosResponse(
                        m.getId(),
                        m.getTipo(),
                        m.getQuantidadePontos(),
                        m.getDataMovimentacao(),
                        m.getDescricao(),
                        m.getSaldoPontos().getProgramaPontos().getNome()
                ))
                .collect(Collectors.toList());

        LocalDateTime seisMesesAtras = LocalDateTime.now().minusMonths(6);
        
        List<MovimentacaoPontosEntity> movimentacoesBrutas = movimentacaoRepository
                .findBySaldoPontosUsuarioEmailAndTipoInAndDataMovimentacaoGreaterThanEqual(
                        email,
                        List.of(TipoMovimentacao.ACUMULO, TipoMovimentacao.BONUS), 
                        seisMesesAtras
                );

        Map<String, Double> agrupamentoPorMes = new LinkedHashMap<>();

        for (int i = 5; i >= 0; i--) {
            LocalDateTime dataRef = LocalDateTime.now().minusMonths(i);
            String nomeMes = dataRef.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
            nomeMes = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1).toLowerCase();
            agrupamentoPorMes.put(nomeMes, 0.0);
        }

        for (MovimentacaoPontosEntity m : movimentacoesBrutas) {
            String mesMovimentacao = m.getDataMovimentacao().getMonth()
                    .getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
            mesMovimentacao = mesMovimentacao.substring(0, 1).toUpperCase() + mesMovimentacao.substring(1).toLowerCase();

            if (agrupamentoPorMes.containsKey(mesMovimentacao)) {
                double totalAtual = agrupamentoPorMes.get(mesMovimentacao);
                agrupamentoPorMes.put(mesMovimentacao, totalAtual + m.getQuantidadePontos().doubleValue());
            }
        }

        List<HistoricoMensalDTO> historicoPontos = agrupamentoPorMes.entrySet().stream()
                .map(entry -> new HistoricoMensalDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());


        Double pontosExpirando = 0.0;

        return new DashboardResponseDTO(
                pontosPorCartao, 
                prazoMedio, 
                pontosExpirando, 
                historicoPontos, 
                ultimasMovimentacoes
        );
    }
}