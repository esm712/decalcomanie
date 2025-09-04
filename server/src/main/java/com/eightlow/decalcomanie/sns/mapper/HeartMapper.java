package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.BaseMapper;
import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.HeartDto;
import com.eightlow.decalcomanie.sns.entity.Heart;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface HeartMapper extends BaseMapper<Heart, HeartDto> {
}
