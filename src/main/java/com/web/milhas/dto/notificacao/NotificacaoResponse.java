package com.web.milhas.dto.notificacao;

import com.web.milhas.entity.enums.TipoNotificacao;
import java.time.LocalDateTime;

public record NotificacaoResponse(
    Long id,
    String mensagem,
    boolean lida,
    TipoNotificacao tipo,
    LocalDateTime dataEnvio
) {}