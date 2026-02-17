package com.web.milhas.repository;

import com.web.milhas.entity.BandeiraEntity;
import com.web.milhas.entity.CartaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartaoRepository extends JpaRepository<CartaoEntity, Long> {

    @Query("SELECT c FROM CartaoEntity c JOIN FETCH c.bandeira JOIN FETCH c.programaPontos WHERE c.usuario.id = :usuarioId")
    List<CartaoEntity> findByUsuarioId(Long usuarioId);
    long countByBandeira(BandeiraEntity bandeira);

}