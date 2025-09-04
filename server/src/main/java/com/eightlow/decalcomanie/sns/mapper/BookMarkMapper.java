package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.BaseMapper;
import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.BookMarkDto;
import com.eightlow.decalcomanie.sns.entity.BookMark;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface BookMarkMapper extends BaseMapper<BookMark, BookMarkDto> {
}
