package com.web.milhas.entity;

import com.web.milhas.entity.enums.StatusCompra;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "compra", schema = "milhas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CompraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorGasto;

    @Column(precision = 10, scale = 2)
    private BigDecimal pontosCalculados;

    @Column(nullable = false)
    private LocalDate dataCompra;

    // Requisito: "Exibir quanto tempo falta para os pontos serem creditados"
    private LocalDate dataCreditoPrevista;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCompra status;

    @ManyToOne
    @JoinColumn(name = "cartao_id", nullable = false)
    private CartaoEntity cartao;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ComprovanteCompraEntity> comprovantes;
}