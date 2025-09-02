package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.sns.dto.ArticleDto;
import com.eightlow.decalcomanie.sns.dto.request.CreateArticleRequest;
import com.eightlow.decalcomanie.sns.dto.request.UpdateArticleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArticleDtoMapper {
    ArticleDto fromCreateArticleRequest(CreateArticleRequest createArticleRequest);

    ArticleDto fromUpdateArticleRequest(UpdateArticleRequest updateArticleRequest);
}
