package com.web.milhas.repository;

import com.web.milhas.entity.NotificacaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<NotificacaoEntity, Long> {

    Page<NotificacaoEntity> findByUsuarioIdOrderByDataEnvioDesc(Long usuarioId, Pageable pageable);
}