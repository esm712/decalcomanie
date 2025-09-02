package com.eightlow.decalcomanie.sns.entity;

import com.eightlow.decalcomanie.common.entity.BaseEntity;
import com.eightlow.decalcomanie.user.entity.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name="comment", indexes = {
        @Index(name = "idx_comment_articleId", columnList = "articleId")
})
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentId")
    private int commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    private String content;
}
