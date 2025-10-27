package com.web.milhas.repository;

import com.web.milhas.entity.MovimentacaoPontosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoPontosRepository extends JpaRepository<MovimentacaoPontosEntity, Long> {

    List<MovimentacaoPontosEntity> findBySaldoPontosUsuarioId(Long usuarioId);
}