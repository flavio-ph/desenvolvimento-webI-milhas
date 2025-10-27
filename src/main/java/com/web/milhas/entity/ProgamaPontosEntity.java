package com.web.milhas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "programa_pontos", schema = "milhas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProgamaPontosEntity { // Nome da classe mantido igual ao nome do arquivo

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome; // Ex: Smiles, TudoAzul, Latam Pass

    @OneToMany(mappedBy = "programaPontos")
    private Set<SaldoPontosEntity> saldos;

    @OneToMany(mappedBy = "programaPontos")
    private Set<PromocaoEntity> promocoes;
}