package com.web.milhas.repository;

import com.web.milhas.entity.MovimentacaoPontosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoPontosRepository extends JpaRepository<MovimentacaoPontosEntity, Long> {

        List<MovimentacaoPontosEntity> findBySaldoPontosUsuarioIdOrderByDataMovimentacaoDesc(Long usuarioId);
        List<MovimentacaoPontosEntity> findByDataMovimentacaoBetweenAndTipo(LocalDateTime inicio, LocalDateTime fim, String tipo); 

<<<<<<< HEAD
    @Query(value = "SELECT AVG( CAST(m.data_movimentacao AS date) - c.data_compra ) " +
            "FROM milhas.movimentacao_pontos m " +
            "JOIN milhas.compra c ON m.compra_id = c.id " +
            "JOIN milhas.saldo_pontos s ON m.saldo_pontos_id = s.id " +
            "WHERE s.usuario_id = :usuarioId " +
            "AND m.tipo = 'ACUMULO' " +
            "AND m.compra_id IS NOT NULL",
            nativeQuery = true)
    Double findPrazoMedioRecebimento(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT COALESCE(SUM(m.quantidadePontos), 0) FROM MovimentacaoPontosEntity m " +
           "WHERE m.saldoPontos.usuario.id = :usuarioId " +
           "AND m.dataValidade BETWEEN :hoje AND :limite")
    BigDecimal somarPontosExpirando(@Param("usuarioId") Long usuarioId, 
                                    @Param("hoje") LocalDate hoje, 
                                    @Param("limite") LocalDate limite);

    @Query("SELECT m FROM MovimentacaoPontosEntity m " +
            "WHERE m.saldoPontos.usuario.email = :email " +
            "AND (:mes IS NULL OR MONTH(m.dataMovimentacao) = :mes) " +
            "AND (:ano IS NULL OR YEAR(m.dataMovimentacao) = :ano) " +
            "AND (:programaNome IS NULL OR m.saldoPontos.programaPontos.nome = :programaNome) " +
            "ORDER BY m.dataMovimentacao DESC")
    List<MovimentacaoPontosEntity> filtrarMovimentacoes(
            @Param("email") String email,
            @Param("mes") Integer mes,
            @Param("ano") Integer ano,
            @Param("programaNome") String programaNome
    );
=======
        @Query(value = "SELECT AVG( CAST(m.data_movimentacao AS date) - c.data_compra ) " +
                "FROM milhas.movimentacao_pontos m " +
                "JOIN milhas.compra c ON m.compra_id = c.id " +
                "JOIN milhas.saldo_pontos s ON m.saldo_pontos_id = s.id " +
                "WHERE s.usuario_id = :usuarioId " +
                "AND m.tipo = 'ACUMULO' " +
                "AND m.compra_id IS NOT NULL",
                nativeQuery = true)
        Double findPrazoMedioRecebimento(@Param("usuarioId") Long usuarioId);
        
        @Query("SELECT COALESCE(SUM(m.quantidadePontos), 0) FROM MovimentacaoPontosEntity m " +
                "WHERE m.saldoPontos.usuario.id = :usuarioId " +
                "AND m.dataValidade BETWEEN :hoje AND :limite")
        BigDecimal somarPontosExpirando(@Param("usuarioId") Long usuarioId, 
                                        @Param("hoje") LocalDate hoje, 
                                        @Param("limite") LocalDate limite);
                               
        @Query(value = "SELECT to_char(m.data_movimentacao, 'Mon') as mes, " +
               "SUM(m.quantidade_pontos) as pontos " +
               "FROM milhas.movimentacao_pontos m " +
               "WHERE m.saldo_pontos_id IN (SELECT id FROM milhas.saldo_pontos WHERE usuario_id = :usuarioId) " +
               "AND m.tipo = 'ACUMULO' " +
               "AND m.data_movimentacao >= CURRENT_DATE - INTERVAL '6 months' " +
               "GROUP BY to_char(m.data_movimentacao, 'Mon'), date_trunc('month', m.data_movimentacao) " +
               "ORDER BY date_trunc('month', m.data_movimentacao) ASC", 
        nativeQuery = true)
        List<Object[]> findHistoricoAcumuloMensal(@Param("usuarioId") Long usuarioId);                                
>>>>>>> 8c184e2586207bad9411f5a3eb0ec4c85725ae4c
}