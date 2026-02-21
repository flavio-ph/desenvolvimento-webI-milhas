package com.web.milhas.dto.auth;

import com.web.milhas.entity.enums.UserRole;
import com.web.milhas.validation.SenhaForte;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "O nome é obrigatório") String nome,

        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "Formato de e-mail inválido") String email,

        @NotBlank(message = "A senha é obrigatória") @SenhaForte String senha,

        String telefone,
        String cpf,

        UserRole role) {
}