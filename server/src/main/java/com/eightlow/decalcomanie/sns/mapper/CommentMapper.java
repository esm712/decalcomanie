package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.common.mapper.MapStructConfig;
import com.eightlow.decalcomanie.sns.dto.CommentDto;
import com.eightlow.decalcomanie.sns.entity.Comment;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CommentMapper {

    CommentDto toDto(Comment comment);
    Comment toEntity(CommentDto commentDto);

    default List<CommentDto> toDtoList(List<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = CommentDto.builder()
                    .commentId(comment.getCommentId())
                    .articleId(comment.getArticle().getArticleId())
                    .userId(comment.getUser().getUserId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .build();
            dtos.add(commentDto);
        }
        return dtos;
    };
    List<Comment> toEntityList(List<CommentDto> commentDtos);
}
