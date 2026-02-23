package com.web.milhas.repository;

import com.web.milhas.dto.dashboard.PontosPorCartaoDTO;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.enums.StatusCompra;
import org.springframework.data.jpa.repository.EntityGraph; // Novo import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CompraRepository extends JpaRepository<CompraEntity, Long> {

    @EntityGraph(attributePaths = {"cartao", "cartao.programaPontos", "comprovantes"})
    List<CompraEntity> findByCartaoUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"cartao", "cartao.usuario", "cartao.programaPontos"})
    List<CompraEntity> findByStatusAndDataCreditoPrevistaLessThanEqual(StatusCompra status, LocalDate data);

    boolean existsByCartaoId(Long cartaoId);

    @Query("SELECT new com.web.milhas.dto.dashboard.PontosPorCartaoDTO(c.cartao.id, c.cartao.nomePersonalizado, SUM(c.pontosCalculados)) " +
            "FROM CompraEntity c " +
            "WHERE c.cartao.usuario.id = :usuarioId AND c.status = com.web.milhas.entity.enums.StatusCompra.CREDITADO " +
            "GROUP BY c.cartao.id, c.cartao.nomePersonalizado")
    List<PontosPorCartaoDTO> findPontosAgrupadosPorCartao(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COALESCE(SUM(c.pontosCalculados), 0) FROM CompraEntity c " +
            "WHERE c.cartao.usuario.id = :usuarioId AND c.status = :status")
    BigDecimal somarPontosPorStatus(@Param("usuarioId") Long usuarioId, @Param("status") StatusCompra status);

    @Query("SELECT MIN(c.dataCreditoPrevista) FROM CompraEntity c " +
            "WHERE c.cartao.usuario.id = :usuarioId AND c.status = :status")
    LocalDate findProximaDataCredito(@Param("usuarioId") Long usuarioId, @Param("status") StatusCompra status);

    org.springframework.data.domain.Page<CompraEntity> findByCartaoId(Long cartaoId, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<CompraEntity> findByCartaoUsuarioId(Long usuarioId, org.springframework.data.domain.Pageable pageable);

}