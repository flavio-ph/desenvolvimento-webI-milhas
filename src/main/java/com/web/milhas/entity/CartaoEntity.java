package com.web.milhas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "cartao", schema = "milhas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CartaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ex: "Meu Visa Infinite"
    @Column(nullable = false)
    private String nomePersonalizado;

    // Ex: 1234
    @Column(length = 4)
    private String ultimosDigitos;

    // Fator para cálculo de pontos (ex: 2.5 pontos por dólar/real)
    @Column(precision = 10, scale = 2)
    private BigDecimal fatorConversao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "bandeira_id", nullable = false)
    private BandeiraEntity bandeira;

    @ManyToOne
    @JoinColumn(name = "programa_pontos_id", nullable = false)
    private ProgamaPontosEntity programaPontos;

    @OneToMany(mappedBy = "cartao")
    private Set<CompraEntity> compras;
}