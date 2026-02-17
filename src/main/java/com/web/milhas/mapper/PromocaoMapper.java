package com.web.milhas.mapper;

import com.web.milhas.dto.promocao.PromocaoResponse;
import com.web.milhas.entity.PromocaoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromocaoMapper {

    @Mapping(source = "programaPontos.nome", target = "nomeProgramaPontos")
    @Mapping(source = "programaPontos.id", target = "programaPontosId")
    PromocaoResponse toResponse(PromocaoEntity entity);
}