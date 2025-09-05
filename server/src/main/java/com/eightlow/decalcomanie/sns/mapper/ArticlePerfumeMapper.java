package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.ArticlePerfumeDto;

import com.eightlow.decalcomanie.sns.entity.ArticlePerfume;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ArticlePerfumeMapper {
    ArticlePerfumeDto toDto(ArticlePerfume articlePerfume);
    ArticlePerfume toEntity(ArticlePerfumeDto articlePerfumeDto);

    List<ArticlePerfumeDto> toDtoList(List<ArticlePerfume> articlePerfumes);
    List<ArticlePerfume> toEntityList(List<ArticlePerfumeDto> articlePerfumeDtos);
}
