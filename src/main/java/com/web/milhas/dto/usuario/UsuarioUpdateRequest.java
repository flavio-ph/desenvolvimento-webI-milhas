package com.web.milhas.dto.usuario;

import com.web.milhas.validation.SenhaForte;

public record UsuarioUpdateRequest(
        String nome,
        String senhaAtual,

        @SenhaForte String senha,

        String telefone,
        String cpf,
        String fotoPerfil) {
}