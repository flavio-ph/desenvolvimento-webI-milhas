package com.web.milhas.dto.programapontos;

import jakarta.validation.constraints.NotBlank;

public record ProgramaPontosDTO(

        Long id,
        @NotBlank(message = "O nome do programa de pontos é obrigatório")
        String nome
) {
}
