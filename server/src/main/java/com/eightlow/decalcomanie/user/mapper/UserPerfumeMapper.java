package com.eightlow.decalcomanie.user.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.user.dto.UserPerfumeDto;
import com.eightlow.decalcomanie.user.entity.UserPerfume;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface UserPerfumeMapper {
    UserPerfumeDto toDto(UserPerfume userPerfume);
    UserPerfume toEntity(UserPerfumeDto userPerfumeDto);

    List<UserPerfumeDto> toDtoList(List<UserPerfume> userPerfumes);
    List<UserPerfume> toEntityList(List<UserPerfumeDto> userPerfumeDtos);
}
