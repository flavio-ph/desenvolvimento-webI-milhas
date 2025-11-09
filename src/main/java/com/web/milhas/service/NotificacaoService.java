package com.web.milhas.service;

import com.web.milhas.dto.notificacao.NotificacaoResponse;
import com.web.milhas.entity.NotificacaoEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.TipoNotificacao;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.NotificacaoRepository;
import com.web.milhas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<NotificacaoResponse> listarMinhasNotificacoes(String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        return notificacaoRepository.findByUsuarioIdOrderByDataEnvioDesc(usuario.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public void criarNotificacao(UsuarioEntity destinatario, String mensagem, TipoNotificacao tipo) {
        NotificacaoEntity notificacao = new NotificacaoEntity();
        notificacao.setUsuario(destinatario);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setDataEnvio(LocalDateTime.now());
        notificacao.setLida(false);

        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void notificarTodos(String mensagem, TipoNotificacao tipo) {
        List<UsuarioEntity> todosUsuarios = usuarioRepository.findAll();
        todosUsuarios.forEach(usuario -> criarNotificacao(usuario, mensagem, tipo));
    }

    @Transactional
    public void marcarComoLida(Long idNotificacao, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        NotificacaoEntity notificacao = notificacaoRepository.findById(idNotificacao)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada."));

        if (!notificacao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Notificação não encontrada para este usuário.");
        }

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    private NotificacaoResponse mapToDTO(NotificacaoEntity entity) {
        return new NotificacaoResponse(
                entity.getId(),
                entity.getMensagem(),
                entity.isLida(),
                entity.getTipo(),
                entity.getDataEnvio()
        );
    }
}