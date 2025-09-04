package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.BaseMapper;
import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.perfume.entity.Scent;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ScentMapper extends BaseMapper<Scent, ScentDto> {
}
