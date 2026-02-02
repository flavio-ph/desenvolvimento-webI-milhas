package com.web.milhas.repository;

import com.web.milhas.entity.ParticipacaoPromocaoEntity;
import com.web.milhas.entity.PromocaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ParticipacaoPromocaoRepository extends JpaRepository<ParticipacaoPromocaoEntity, Long> {
    boolean existsByUsuarioIdAndPromocaoId(Long usuarioId, Long promocaoId);

    @Query("SELECT p.promocao FROM ParticipacaoPromocaoEntity p " +
            "WHERE p.usuario.id = :usuarioId " +
            "AND p.promocao.programaPontos.id = :programaId " +
            "AND :dataCompra BETWEEN p.promocao.dataInicio AND p.promocao.dataFim")
    Optional<PromocaoEntity> findPromocaoAtivaParaUsuario(@Param("usuarioId") Long usuarioId,
                                                          @Param("programaId") Long programaId,
                                                          @Param("dataCompra") LocalDate dataCompra);
}