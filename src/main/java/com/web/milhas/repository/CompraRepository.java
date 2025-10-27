package com.web.milhas.repository;

import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.enums.StatusCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CompraRepository extends JpaRepository<CompraEntity, Long> {

    List<CompraEntity> findByCartaoUsuarioId(Long usuarioId);

    // Usado pelo Scheduler para verificar prazos expirados
    List<CompraEntity> findByStatusAndDataCreditoPrevistaBefore(StatusCompra status, LocalDate hoje);
}