package com.web.milhas.dto.usuario;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
    Long id,
    String nome,
    String email,
    LocalDateTime dataCadastro
) {}