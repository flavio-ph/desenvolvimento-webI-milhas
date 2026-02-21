package com.web.milhas.entity;

import com.web.milhas.entity.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_pontos", schema = "milhas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class MovimentacaoPontosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadePontos;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime dataMovimentacao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(nullable = false)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "saldo_pontos_id", nullable = false)
    private SaldoPontosEntity saldoPontos;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private CompraEntity compra;
}