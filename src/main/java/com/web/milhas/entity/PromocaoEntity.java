package com.web.milhas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promocao", schema = "milhas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PromocaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String urlPromocao;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    @Column(name = "bonus_porcentagem", precision = 10, scale = 2)
    private BigDecimal bonusPorcentagem;

    @ManyToOne
    @JoinColumn(name = "programa_pontos_id", nullable = false)
    private ProgramaPontosEntity programaPontos;
}