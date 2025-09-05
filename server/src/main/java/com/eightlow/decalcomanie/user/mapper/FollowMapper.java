package com.eightlow.decalcomanie.user.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.user.dto.FollowDto;
import com.eightlow.decalcomanie.user.entity.Follow;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface FollowMapper {
    FollowDto toDto(Follow follow);
    Follow toEntity(FollowDto followDto);

    List<FollowDto> toDtoList(List<Follow> follows);
    List<Follow> toEntityList(List<FollowDto> followDtos);
}
