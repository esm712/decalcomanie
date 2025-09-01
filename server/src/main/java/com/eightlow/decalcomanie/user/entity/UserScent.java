package com.eightlow.decalcomanie.user.entity;

import com.eightlow.decalcomanie.perfume.entity.Scent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "userscent")
public class UserScent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userScentId")
    private int userScentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="scentId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Scent scent;
}

