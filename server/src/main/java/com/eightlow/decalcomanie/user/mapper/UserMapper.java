package com.eightlow.decalcomanie.user.mapper;

import com.eightlow.decalcomanie.user.dto.UserDto;
import com.eightlow.decalcomanie.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    List<User> toEntity(List<UserDto> userDtoList);

    List<UserDto> toDto(List<User> userList);
}
