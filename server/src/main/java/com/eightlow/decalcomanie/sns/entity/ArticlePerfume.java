package com.eightlow.decalcomanie.sns.entity;

import com.eightlow.decalcomanie.perfume.entity.Perfume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="articleperfume", indexes = @Index(name = "articleperfume_idx", columnList = "articleId"))
public class ArticlePerfume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int articlePerfumeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfumeId", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Perfume perfume;

    private float rate;
}
