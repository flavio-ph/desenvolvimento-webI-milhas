package com.web.milhas.mapper;

import com.web.milhas.dto.saldo.SaldoPontosResponse;
import com.web.milhas.entity.SaldoPontosEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaldoMapper {

    @Mapping(source = "programaPontos.nome", target = "nomePrograma")
    SaldoPontosResponse toResponse(SaldoPontosEntity entity);
}