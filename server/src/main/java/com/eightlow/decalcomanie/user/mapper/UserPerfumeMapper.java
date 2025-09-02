package com.eightlow.decalcomanie.user.mapper;

import com.eightlow.decalcomanie.user.dto.UserPerfumeDto;
import com.eightlow.decalcomanie.user.entity.UserPerfume;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserPerfumeMapper {
    UserPerfume toEntity(UserPerfumeDto userPerfumeDto);

    UserPerfumeDto toDto(UserPerfume userPerfume);

    List<UserPerfume> toEntity(List<UserPerfumeDto> userPerfumeDtoList);

    List<UserPerfumeDto> toDto(List<UserPerfume> userPerfumeList);
}
