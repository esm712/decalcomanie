package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.perfume.entity.Scent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ScentMapper{
    ScentDto toDto(Scent scent);
    Scent toEntity(ScentDto dto);
    List<ScentDto> toDtoList(List<Scent> scents);
    List<Scent> toEntityList(List<ScentDto> scentDtos);
}
