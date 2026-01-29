package com.web.milhas.service.impl;

import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.MovimentacaoPontosEntity;
import com.web.milhas.entity.enums.StatusCompra;
import com.web.milhas.entity.enums.TipoNotificacao;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import com.web.milhas.service.CompraService; 
import com.web.milhas.service.NotificacaoService;
import com.web.milhas.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final CompraRepository compraRepository;
    private final CompraService compraService; 
    private final NotificacaoService notificacaoService;
    private final MovimentacaoPontosRepository movimentacaoRepository;

    // --- TAREFA 1: CREDITAR PONTOS ---
    @Override
    @Scheduled(cron = "0 0 1 * * *") 
    public void verificarComprasVencidas() {
        log.info("Iniciando processamento automático de créditos...");

        LocalDate hoje = LocalDate.now();
        List<CompraEntity> comprasParaCreditar = compraRepository
                .findByStatusAndDataCreditoPrevistaLessThanEqual(StatusCompra.PENDENTE, hoje);

        if (comprasParaCreditar.isEmpty()) {
            return;
        }

        for (CompraEntity compra : comprasParaCreditar) {
            try {
                compraService.creditarCompra(compra.getId());

                // 1. CORREÇÃO AQUI: Passamos a 'compra' como 4º argumento
                notificacaoService.criarNotificacao(
                        compra.getCartao().getUsuario(),
                        "Crédito Realizado: Os seus pontos da compra '" + compra.getDescricao() + "' foram creditados!",
                        TipoNotificacao.CREDITO_REALIZADO,
                        compra // <--- AQUÍ: O objeto Compra
                );
                log.info("Compra ID {} creditada com sucesso.", compra.getId());
                
            } catch (Exception e) {
                log.error("Erro ao processar compra ID {}: {}", compra.getId(), e.getMessage());
            }
        }
    }

    // --- TAREFA 2: VERIFICAR VALIDADE DOS PONTOS ---
    @Override
    @Scheduled(cron = "0 0 8 * * *") 
    public void verificarValidadePontos() {
        log.info("🔎 Verificando validade e expiração de pontos...");

        LocalDateTime agora = LocalDateTime.now();
        
        // AVISO PRÉVIO
        LocalDateTime dataAviso = agora.minusMonths(23);
        List<MovimentacaoPontosEntity> vencendo = movimentacaoRepository.findByDataMovimentacaoBetweenAndTipo(
                dataAviso.minusDays(1), 
                dataAviso.plusDays(1), 
                "ACUMULO"
        );

        for (MovimentacaoPontosEntity mov : vencendo) {
            // 2. CORREÇÃO AQUI: Passamos 'null' pois não é uma compra específica
            notificacaoService.criarNotificacao(
                mov.getSaldoPontos().getUsuario(),
                "Seus pontos vão vencer! Atenção: " + mov.getQuantidadePontos() + " pontos expiram em 30 dias.",
                TipoNotificacao.AVISO_EXPIRACAO,
                null // <--- AQUÍ: Passamos null
            );
        }

        // EXPIRADO HOJE
        LocalDateTime dataVencimento = agora.minusMonths(24);
        List<MovimentacaoPontosEntity> vencidos = movimentacaoRepository.findByDataMovimentacaoBetweenAndTipo(
                dataVencimento.minusDays(1), 
                dataVencimento.plusDays(1), 
                "ACUMULO"
        );

        for (MovimentacaoPontosEntity mov : vencidos) {
            // 3. CORREÇÃO AQUI: Passamos 'null' também
            notificacaoService.criarNotificacao(
                mov.getSaldoPontos().getUsuario(),
                "Pontos Expirados: Infelizmente " + mov.getQuantidadePontos() + " pontos expiraram hoje.",
                TipoNotificacao.PONTOS_EXPIRADOS,
                null // <--- AQUÍ: Passamos null
            );
            
            log.warn("Pontos expirados para o usuário {}: {}", mov.getSaldoPontos().getUsuario().getId(), mov.getQuantidadePontos());
        }
    }
}