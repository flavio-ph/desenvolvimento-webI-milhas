package com.web.milhas.mapper;

import com.web.milhas.dto.compra.CompraResponse;
import com.web.milhas.entity.CompraEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompraMapper {

    @Mapping(source = "cartao.id", target = "cartaoId")
    @Mapping(source = "cartao.nomePersonalizado", target = "nomeCartao")
    @Mapping(target = "diasParaCredito", ignore = true) // Calculado dinamicamente no service
    CompraResponse toResponse(CompraEntity entity);
}