package com.web.milhas.service.impl;

import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.enums.StatusCompra;
import com.web.milhas.entity.enums.TipoNotificacao;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.service.NotificacaoService;
import com.web.milhas.service.SaldoService;     
import com.web.milhas.service.SchedulerService;  
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final CompraRepository compraRepository;
    private final NotificacaoService notificacaoService;
    private final SaldoService saldoService;

    private static final Logger log = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Override
    @Scheduled(cron = "0 0 8 * * *")

    @Transactional
    public void verificarComprasVencidas() {
        log.info("Iniciando verificação agendada de compras vencidas...");

        LocalDate hoje = LocalDate.now();
        List<CompraEntity> comprasVencidas = compraRepository
                .findByStatusAndDataCreditoPrevistaBefore(StatusCompra.PENDENTE, hoje);

        if (comprasVencidas.isEmpty()) {
            log.info("Nenhuma compra pendente vencida encontrada.");
            return;
        }

        log.info("Encontradas {} compras vencidas. Processando...", comprasVencidas.size());

        for (CompraEntity compra : comprasVencidas) {
            boolean simularSucesso = Math.random() > 0.2;

            if (simularSucesso) {
                compra.setStatus(StatusCompra.CREDITADO);

                saldoService.creditarPontosCompra(compra);

                notificacaoService.criarNotificacao(
                        compra.getCartao().getUsuario(),
                        "Seus pontos da compra '" + compra.getDescricao() + "' foram creditados com sucesso!",
                        TipoNotificacao.CREDITO_REALIZADO,
                        compra
                );
                log.info("Compra ID {} atualizada para CREDITADO.", compra.getId());
            } else {
                compra.setStatus(StatusCompra.EXPIRADO);
                notificacaoService.criarNotificacao(
                        compra.getCartao().getUsuario(),
                        "ATENÇÃO: O prazo de crédito da compra '" + compra.getDescricao() + "' expirou. Verifique com a central.",
                        TipoNotificacao.EXPIRACAO_PRAZO,
                        compra
                );
                log.warn("Compra ID {} atualizada para EXPIRADO.", compra.getId());
            }

            compraRepository.save(compra);
        }

        log.info("Verificação agendada concluída.");
    }
}