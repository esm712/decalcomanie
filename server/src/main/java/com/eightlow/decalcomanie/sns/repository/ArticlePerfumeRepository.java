package com.eightlow.decalcomanie.sns.repository;

import com.eightlow.decalcomanie.sns.entity.ArticlePerfume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticlePerfumeRepository extends JpaRepository <ArticlePerfume, Integer>{
    List<ArticlePerfume> findByArticle_ArticleId(int articleId);
    List<ArticlePerfume> findByPerfume_PerfumeId(int perfumeId);

    List<ArticlePerfume> findByArticle_ArticleIdAndPerfume_PerfumeIdIn(int articleId, List<Integer> perfumeId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ArticlePerfume ap SET ap.rate = :rate WHERE ap.article.articleId = :articleId AND ap.perfume.perfumeId = :perfumeId")
    void updateRateByArticleIdAndPerfumeId(@Param(value = "articleId") int articleId,
                                           @Param(value = "perfumeId") int perfumeId,
                                           @Param(value = "rate") float rate);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ArticlePerfume ap WHERE ap.article.articleId = :articleId")
    void deleteAllByArticleId(@Param(value = "articleId") int articleId);
}
