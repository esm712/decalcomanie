package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.HeartDto;
import com.eightlow.decalcomanie.sns.entity.Heart;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface HeartMapper {
    HeartDto toDto(Heart heart);
    Heart toEntity(HeartDto heartDto);

    List<HeartDto> toDtoList(List<Heart> hearts);
    List<Heart> toEntityList(List<HeartDto> heartDtos);
}
