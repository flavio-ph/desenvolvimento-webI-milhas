package com.web.milhas.repository;

import com.web.milhas.entity.NotificacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<NotificacaoEntity, Long> {

    List<NotificacaoEntity> findByUsuarioIdOrderByDataEnvioDesc(Long usuarioId);
}