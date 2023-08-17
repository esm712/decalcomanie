package com.eightlow.decalcomanie.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArticleRequest {
    private int articleId;
    private String userId;
    private String content;
    private List<Integer> perfumeId;
    private List<Float> rate;
    private int heart;
    private int comment;
}
