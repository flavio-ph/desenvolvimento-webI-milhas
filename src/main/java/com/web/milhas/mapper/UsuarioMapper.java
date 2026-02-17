package com.web.milhas.mapper;

import com.web.milhas.dto.auth.RegisterRequest;
import com.web.milhas.dto.usuario.UsuarioResponse;
import com.web.milhas.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(UsuarioEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "twoFactorEnabled", ignore = true)
    @Mapping(target = "verificationCode", ignore = true)
    @Mapping(target = "verificationCodeExpiry", ignore = true)
    @Mapping(target = "fotoPerfil", ignore = true)
    @Mapping(target = "resetPasswordToken", ignore = true)
    @Mapping(target = "resetPasswordTokenExpiry", ignore = true)
    @Mapping(target = "cartoes", ignore = true)
    @Mapping(target = "saldos", ignore = true)
    @Mapping(target = "notificacoes", ignore = true)
    @Mapping(target = "senha", ignore = true)
    UsuarioEntity toEntity(RegisterRequest dto);
}