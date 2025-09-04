package com.eightlow.decalcomanie.user.mapper;

import com.eightlow.decalcomanie.common.mapper.BaseMapper;
import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.user.dto.UserPerfumeDto;
import com.eightlow.decalcomanie.user.entity.UserPerfume;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface UserPerfumeMapper extends BaseMapper<UserPerfume, UserPerfumeDto> {
}
