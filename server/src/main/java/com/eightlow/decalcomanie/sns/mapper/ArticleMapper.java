package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.ArticleDto;
import com.eightlow.decalcomanie.sns.entity.Article;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ArticleMapper {
    ArticleDto toDto(Article article);
    Article toEntity(ArticleDto articleDto);

    List<ArticleDto> toDtoList(List<Article> articles);
    List<Article> toEntityList(List<ArticleDto> articleDtos);
}