package com.web.milhas.repository;

import com.web.milhas.entity.BandeiraEntity;
import com.web.milhas.entity.CartaoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Import essencial

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartaoRepository extends JpaRepository<CartaoEntity, Long> {

    long countByBandeira(BandeiraEntity bandeira);

    @EntityGraph(attributePaths = { "bandeira", "programaPontos" })
    List<CartaoEntity> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = { "bandeira", "programaPontos" })
    Page<CartaoEntity> findAll(Pageable pageable);

}