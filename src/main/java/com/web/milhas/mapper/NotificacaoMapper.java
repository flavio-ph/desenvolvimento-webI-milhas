package com.web.milhas.mapper;

import com.web.milhas.dto.notificacao.NotificacaoResponse;
import com.web.milhas.entity.NotificacaoEntity;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificacaoMapper {

    NotificacaoResponse toResponse(NotificacaoEntity entity);

    List<NotificacaoResponse> toResponseList(List<NotificacaoEntity> entities);
}