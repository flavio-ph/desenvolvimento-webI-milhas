package com.web.milhas.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome
) { }
