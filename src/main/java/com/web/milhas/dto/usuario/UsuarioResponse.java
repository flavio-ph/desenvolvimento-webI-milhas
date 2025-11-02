package com.web.milhas.dto.usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
    Long id,
    String nome,
    String email
) {}