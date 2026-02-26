package com.web.milhas.repository;

import com.web.milhas.entity.ComprovanteCompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprovanteCompraRepository extends JpaRepository<ComprovanteCompraEntity, Long> {
    Optional<ComprovanteCompraEntity> findByCompraId(Long compraId);
}