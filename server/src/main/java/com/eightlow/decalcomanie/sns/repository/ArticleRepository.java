package com.eightlow.decalcomanie.sns.repository;

import com.eightlow.decalcomanie.sns.dto.ArticleDto;
import com.eightlow.decalcomanie.sns.entity.Article;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Article a SET a.comment = a.comment + 1 WHERE a.articleId = :articleId")
    void increaseCommentCount(@Param("articleId") int articleId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Article a SET a.comment = a.comment - 1 WHERE a.articleId = :articleId")
    void decreaseCommentCount(@Param("articleId") int articleId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Article a SET a.heart = a.heart + 1 WHERE a.articleId = :articleId")
    void increaseHeartCountByArticleId(@Param("articleId") int articleId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Article a SET a.heart = a.heart - 1 WHERE a.articleId = :articleId")
    void decreaseHeartCountByArticleId(@Param("articleId") int articleId);

    @Modifying
    @Query("UPDATE Article a SET a.user.userId = '00000000-0000-0000-0000-000000000000' WHERE a.user.userId = :userId")
    void setUserIdToGhostAccount(@Param("userId") String userId);
}
