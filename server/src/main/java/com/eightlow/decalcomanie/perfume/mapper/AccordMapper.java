package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.perfume.dto.AccordDto;
import com.eightlow.decalcomanie.perfume.entity.Accord;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface AccordMapper {
    AccordDto toDto(Accord accord);
    Accord toEntity(AccordDto accordDto);

    List<AccordDto> toDtoList(List<Accord> accords);
    List<Accord> toEntityList(List<AccordDto> accordDtos);
}
