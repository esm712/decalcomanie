package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.sns.dto.BookMarkDto;
import com.eightlow.decalcomanie.sns.entity.BookMark;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMarkMapper {
    BookMarkDto toDto(BookMark bookMark);
    BookMark toEntity(BookMarkDto bookMarkDto);
}
