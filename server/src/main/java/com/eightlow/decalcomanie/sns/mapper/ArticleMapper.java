package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.BaseMapper;
import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.ArticleDto;
import com.eightlow.decalcomanie.sns.entity.Article;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ArticleMapper extends BaseMapper<Article, ArticleDto> {
}