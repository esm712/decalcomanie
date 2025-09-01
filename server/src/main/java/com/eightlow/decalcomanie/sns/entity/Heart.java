package com.eightlow.decalcomanie.sns.entity;

import com.eightlow.decalcomanie.sns.entity.pk.HeartPk;
import com.eightlow.decalcomanie.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@IdClass(HeartPk.class)
@Table(name="heart", indexes ={
        @Index(name = "idx_heart_articleId", columnList = "articleId")
})
public class Heart {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Article article;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;
}
