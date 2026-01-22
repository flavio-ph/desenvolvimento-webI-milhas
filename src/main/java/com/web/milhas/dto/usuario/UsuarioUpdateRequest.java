package com.web.milhas.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha,

        String telefone,
        String cpf,
        String fotoPerfil
) { }