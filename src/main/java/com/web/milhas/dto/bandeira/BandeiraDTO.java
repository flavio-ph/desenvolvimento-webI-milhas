package com.web.milhas.dto.bandeira;

import jakarta.validation.constraints.NotBlank;

public record BandeiraDTO(

        Long id,
        @NotBlank(message = "O nome de bandeira é obrigatório")
        String nome

) {

}
