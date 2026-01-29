// Local: desenvolvimento-webI-milhas/src/main/java/com/web/milhas/service/impl/SchedulerServiceImpl.java
package com.web.milhas.service.impl;

import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.enums.StatusCompra;
import com.web.milhas.entity.enums.TipoNotificacao;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.service.CompraService; 
import com.web.milhas.service.NotificacaoService;
import com.web.milhas.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final CompraRepository compraRepository;
    private final CompraService compraService; 
    private final NotificacaoService notificacaoService;

    @Override
    @Scheduled(cron = "0 0 1 * * *") // Executa diariamente à 01:00 AM
    public void verificarComprasVencidas() {
        log.info("Iniciando processamento automático de créditos...");

        LocalDate hoje = LocalDate.now();
        // Busca compras PENDENTES com data prevista menor ou igual a hoje
        List<CompraEntity> comprasParaCreditar = compraRepository
                .findByStatusAndDataCreditoPrevistaLessThanEqual(StatusCompra.PENDENTE, hoje);

        if (comprasParaCreditar.isEmpty()) {
            log.info("Nenhuma compra pendente para processar.");
            return;
        }

        for (CompraEntity compra : comprasParaCreditar) {
            try {
                // Chama o serviço central de crédito
                compraService.creditarCompra(compra.getId());

                // Envia a notificação real ao utilizador
                notificacaoService.criarNotificacao(
                        compra.getCartao().getUsuario(),
                        "Os seus pontos da compra '" + compra.getDescricao() + "' foram creditados!",
                        TipoNotificacao.CREDITO_REALIZADO,
                        compra
                );
                log.info("Compra ID {} creditada com sucesso.", compra.getId());
                
            } catch (Exception e) {
                log.error("Erro ao processar compra ID {}: {}", compra.getId(), e.getMessage());
            }
        }
    }
}