package com.web.milhas.repository;

import com.web.milhas.entity.CartaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartaoRepository extends JpaRepository<CartaoEntity, Long> {

    List<CartaoEntity> findByUsuarioId(Long usuarioId);
}