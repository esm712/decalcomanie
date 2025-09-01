package com.eightlow.decalcomanie.user.entity;

import com.eightlow.decalcomanie.user.dto.UserPerfumeRecommendId;
import com.eightlow.decalcomanie.perfume.entity.Perfume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@IdClass(UserPerfumeRecommendId.class)
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "userperfumerecommend")
public class UserPerfumeRecommend {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfumeId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Perfume perfume;
}
