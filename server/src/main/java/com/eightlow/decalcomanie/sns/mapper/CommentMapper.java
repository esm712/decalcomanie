package com.eightlow.decalcomanie.sns.mapper;

import com.eightlow.decalcomanie.sns.dto.CommentDto;
import com.eightlow.decalcomanie.sns.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "article", ignore = true)
    @Mapping(target = "user", ignore = true)
    Comment toEntity(CommentDto commentDTO);

    List<CommentDto> toDTO(List<Comment> comments);

    default List<CommentDto> toDTOs(List<Comment> comments) {
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
}
