package com.web.milhas.repository;

import com.web.milhas.entity.PromocaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface PromocaoRepository extends JpaRepository<PromocaoEntity, Long> {

    @EntityGraph(attributePaths = { "programaPontos" })
    List<PromocaoEntity> findByProgramaPontosIdAndDataFimGreaterThanEqual(Long programaPontosId, LocalDate hoje);

    @EntityGraph(attributePaths = { "programaPontos" })
    Page<PromocaoEntity> findAll(Pageable pageable);
}