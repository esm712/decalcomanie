package com.eightlow.decalcomanie.user.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.user.dto.UserDto;
import com.eightlow.decalcomanie.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    UserDto toDto(User user);
    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);
    List<User> toEntityList(List<UserDto> userDtos);
}
