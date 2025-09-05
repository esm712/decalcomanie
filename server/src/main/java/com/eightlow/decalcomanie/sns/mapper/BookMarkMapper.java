package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.BookMarkDto;
import com.eightlow.decalcomanie.sns.entity.BookMark;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface BookMarkMapper {
    BookMarkDto toDto(BookMark bookMark);
    BookMark toEntity(BookMarkDto bookMarkDto);

    List<BookMarkDto> toDtoList(List<BookMark> bookMarks);
    List<BookMark> toEntityList(List<BookMarkDto> bookMarkDtos);
}
