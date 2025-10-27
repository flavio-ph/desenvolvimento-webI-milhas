package com.web.milhas.repository;

import com.web.milhas.entity.PromocaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PromocaoRepository extends JpaRepository<PromocaoEntity, Long> {

    // Busca promoções ativas (dataFim >= hoje) para um programa específico
    List<PromocaoEntity> findByProgramaPontosIdAndDataFimGreaterThanEqual(Long programaPontosId, LocalDate hoje);
}