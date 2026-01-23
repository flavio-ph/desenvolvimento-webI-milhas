package com.web.milhas.repository;

import com.web.milhas.entity.MovimentacaoPontosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimentacaoPontosRepository extends JpaRepository<MovimentacaoPontosEntity, Long> {

    List<MovimentacaoPontosEntity> findBySaldoPontosUsuarioId(Long usuarioId);

    @Query(value = "SELECT AVG( CAST(m.data_movimentacao AS date) - c.data_compra ) " +
            "FROM milhas.movimentacao_pontos m " +
            "JOIN milhas.compra c ON m.compra_id = c.id " +
            "JOIN milhas.saldo_pontos s ON m.saldo_pontos_id = s.id " +
            "WHERE s.usuario_id = :usuarioId " +
            "AND m.tipo = 'ACUMULO' " +
            "AND m.compra_id IS NOT NULL",
            nativeQuery = true)
    Double findPrazoMedioRecebimento(@Param("usuarioId") Long usuarioId);
}