package com.eightlow.decalcomanie.perfume.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.perfume.dto.BrandDto;
import com.eightlow.decalcomanie.perfume.entity.Brand;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface BrandMapper {
    BrandDto toDto(Brand brand);
    Brand toEntity(BrandDto brandDto);

    List<BrandDto> toDtoList(List<Brand> brands);
    List<Brand> toEntityList(List<BrandDto> brandDtos);
}
