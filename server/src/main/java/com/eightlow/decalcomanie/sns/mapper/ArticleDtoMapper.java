package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.ArticleDto;
import com.eightlow.decalcomanie.sns.dto.request.CreateArticleRequest;
import com.eightlow.decalcomanie.sns.dto.request.UpdateArticleRequest;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ArticleDtoMapper {
    ArticleDto fromCreateArticleRequest(CreateArticleRequest createArticleRequest);

    ArticleDto fromUpdateArticleRequest(UpdateArticleRequest updateArticleRequest);
}
