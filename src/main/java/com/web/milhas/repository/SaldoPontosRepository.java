package com.web.milhas.repository;

import com.web.milhas.entity.SaldoPontosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaldoPontosRepository extends JpaRepository<SaldoPontosEntity, Long> {

    List<SaldoPontosEntity> findByUsuarioId(Long usuarioId);

    Optional<SaldoPontosEntity> findByUsuarioIdAndProgramaPontosId(Long usuarioId, Long programaPontosId);
}