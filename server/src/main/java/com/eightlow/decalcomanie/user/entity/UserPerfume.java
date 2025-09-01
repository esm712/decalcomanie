package com.eightlow.decalcomanie.user.entity;

import com.eightlow.decalcomanie.perfume.entity.Perfume;
import com.eightlow.decalcomanie.user.dto.UserPerfumeId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@IdClass(UserPerfumeId.class)
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "userperfume")
public class UserPerfume {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="perfumeId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Perfume perfume;
}
