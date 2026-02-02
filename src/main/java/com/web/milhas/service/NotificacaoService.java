package com.web.milhas.service;

import com.web.milhas.dto.notificacao.NotificacaoResponse;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.TipoNotificacao;
import java.util.List;

public interface NotificacaoService {

    List<NotificacaoResponse> listarMinhasNotificacoes(String emailUsuario);
    void criarNotificacao(UsuarioEntity destinatario, String mensagem, TipoNotificacao tipo, CompraEntity compraRelacionada);
    void notificarTodos(String mensagem, TipoNotificacao tipo);
    void marcarComoLida(Long idNotificacao, String emailUsuario);
}