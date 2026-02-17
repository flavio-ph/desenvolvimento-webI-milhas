package com.web.milhas.mapper;

import com.web.milhas.dto.bandeira.BandeiraDTO;
import com.web.milhas.entity.BandeiraEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BandeiraMapper {

    @Mapping(target = "cards", ignore = true)
    BandeiraDTO toDTO(BandeiraEntity entity);

    @Mapping(target = "id", source = "id")
    BandeiraEntity toEntity(BandeiraDTO dto);
}
