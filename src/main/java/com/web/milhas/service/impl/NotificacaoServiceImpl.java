package com.web.milhas.service.impl;

import com.web.milhas.dto.notificacao.NotificacaoResponse;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.NotificacaoEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.TipoNotificacao;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.mapper.NotificacaoMapper;
import com.web.milhas.repository.NotificacaoRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoServiceImpl implements NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoMapper notificacaoMapper; // Injeção do Mapper

    @Override
    public Page<NotificacaoResponse> listarMinhasNotificacoes(String emailUsuario, Pageable pageable) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Page<NotificacaoEntity> notificacoes = notificacaoRepository
                .findByUsuarioIdOrderByDataEnvioDesc(usuario.getId(), pageable);

        return notificacoes.map(notificacaoMapper::toResponse);
    }

    @Override
    @Transactional
    public void criarNotificacao(UsuarioEntity destinatario, String mensagem, TipoNotificacao tipo,
            CompraEntity compraRelacionada) {
        NotificacaoEntity notificacao = new NotificacaoEntity();
        notificacao.setUsuario(destinatario);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setDataEnvio(LocalDateTime.now());
        notificacao.setLida(false);
        notificacao.setCompra(compraRelacionada);

        notificacaoRepository.save(notificacao);
    }

    @Override
    @Transactional
    public void notificarTodos(String mensagem, TipoNotificacao tipo) {
        List<UsuarioEntity> todosUsuarios = usuarioRepository.findAll();
        todosUsuarios.forEach(usuario -> criarNotificacao(usuario, mensagem, tipo, null));
    }

    @Override
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

}