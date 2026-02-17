package com.web.milhas.mapper;

import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.entity.MovimentacaoPontosEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MovimentacaoMapper {

    @Mapping(source = "saldoPontos.programaPontos.nome", target = "nomePrograma")
    MovimentacaoPontosResponse toResponse(MovimentacaoPontosEntity entity);

    List<MovimentacaoPontosResponse> toResponseList(List<MovimentacaoPontosEntity> entities);
}
