package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.BaseMapper;
import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.ArticlePerfumeDto;

import com.eightlow.decalcomanie.sns.entity.ArticlePerfume;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ArticlePerfumeMapper extends BaseMapper<ArticlePerfume, ArticlePerfumeDto> {
}
