package com.eightlow.decalcomanie.sns.dto;

import com.eightlow.decalcomanie.common.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class ArticleDto extends BaseDto {
    private int articleId;
    private String userId;
    private String content;
    private int heart;
    private int comment;

}
