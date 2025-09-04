package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.BaseMapper;
import com.eightlow.decalcomanie.perfume.dto.AccordDto;
import com.eightlow.decalcomanie.perfume.entity.Accord;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

import java.util.List;

@Mapper(config = MapperConfig.class)
public interface AccordMapper extends BaseMapper<Accord, AccordDto> {
    @Override
    AccordDto toDto(Accord accord);

    @Override
    Accord toEntity(AccordDto accordDto);

    @Override
    List<AccordDto> toDtoList(List<Accord> accords);

    @Override
    List<Accord> toEntityList(List<AccordDto> accordDtos);

}
