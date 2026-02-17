package com.web.milhas.mapper;

import com.web.milhas.dto.cartao.CartaoResponse;
import com.web.milhas.entity.CartaoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartaoMapper {

    @Mapping(source = "bandeira.nome", target = "nomeBandeira")
    @Mapping(source = "programaPontos.nome", target = "nomeProgramaPontos")
    CartaoResponse toResponse(CartaoEntity entity);

    List<CartaoResponse> toResponseList(List<CartaoEntity> entities);
}