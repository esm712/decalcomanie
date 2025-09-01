package com.eightlow.decalcomanie.perfume.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Getter
@Builder(toBuilder = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accord")
public class Accord {
    @Id
    @Column(name = "accordId")
    private int accordId;

    @Column(name = "weight")
    private float weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfumeId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Perfume perfume;

    @ManyToOne
    @JoinColumn(name = "scentId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Scent scent;
}

