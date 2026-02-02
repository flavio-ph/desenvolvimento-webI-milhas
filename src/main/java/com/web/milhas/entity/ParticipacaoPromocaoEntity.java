package com.web.milhas.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "participacao_promocao", schema = "milhas")
public class ParticipacaoPromocaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "promocao_id", nullable = false)
    private PromocaoEntity promocao;

    @CreationTimestamp
    private LocalDateTime dataAdesao;
}

