package com.web.milhas.dto.usuario;

import com.web.milhas.entity.enums.UserRole;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        UserRole role,
        String fotoPerfil,
        LocalDateTime dataCadastro // Novo campo
) {}