package com.web.milhas.repository;

import com.web.milhas.entity.SaldoPontosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface SaldoPontosRepository extends JpaRepository<SaldoPontosEntity, Long> {

    @EntityGraph(attributePaths = { "usuario", "programaPontos" })
    List<SaldoPontosEntity> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = { "usuario", "programaPontos" })
    Optional<SaldoPontosEntity> findByUsuarioIdAndProgramaPontosId(Long usuarioId, Long programaPontosId);

    @EntityGraph(attributePaths = { "usuario", "programaPontos" })
    Page<SaldoPontosEntity> findAll(Pageable pageable);
}