package com.web.milhas.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record Verify2FARequestDTO(
        @NotBlank String email,
        @NotBlank String codigo
) {
}
