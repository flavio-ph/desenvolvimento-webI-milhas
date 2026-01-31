package com.web.milhas.service.impl;

import com.web.milhas.dto.dashboard.DashboardResponseDTO;
import com.web.milhas.dto.dashboard.PontosPorCartaoDTO;
import com.web.milhas.dto.dashboard.PrazoMedioRecebimentoDTO;
import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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

        // 1. Pontos por cartão (existente)
        List<PontosPorCartaoDTO> pontosPorCartao =
                compraRepository.findPontosAgrupadosPorCartao(usuario.getId());

        // 2. Prazo médio (existente)
        Double diasMedios = movimentacaoRepository.findPrazoMedioRecebimento(usuario.getId());
        PrazoMedioRecebimentoDTO prazoMedio = new PrazoMedioRecebimentoDTO(diasMedios);

        // 3. Últimas Movimentações (Usando seu método findBySaldoPontosUsuarioId)
        // Pegamos a lista, ordenamos pela data mais recente e limitamos a 5 para o Dashboard
        List<MovimentacaoPontosResponse> ultimas = movimentacaoRepository
        .findBySaldoPontosUsuarioIdOrderByDataMovimentacaoDesc(usuario.getId())
        .stream()
        .limit(5)
        .map(entity -> new MovimentacaoPontosResponse(
                entity.getId(),
                entity.getTipo(),
                entity.getQuantidadePontos(),
                entity.getDataMovimentacao(),
                entity.getDescricao(),
                // ALTERE DE getPrograma() PARA getProgramaPontos()
                entity.getSaldoPontos().getProgramaPontos().getNome() 
        ))
        .toList();
        // 4. Pontos Expirando (Usando seu método somarPontosExpirando)
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(30);
        BigDecimal expirando = movimentacaoRepository.somarPontosExpirando(usuario.getId(), hoje, limite);

        return new DashboardResponseDTO(pontosPorCartao, prazoMedio, expirando, ultimas);
    }

}