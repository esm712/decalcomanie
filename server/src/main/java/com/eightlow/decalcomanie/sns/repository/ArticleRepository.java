package com.eightlow.decalcomanie.sns.repository;

import com.eightlow.decalcomanie.sns.dto.ArticleDto;
import com.eightlow.decalcomanie.sns.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
//    boolean existsById(int articleId);
    Optional<Article> findByArticleId(int articleId);

    List<Article> findByUserId(String userId);

    @Query("SELECT articleId, userId, content, heart, comment  FROM Article ORDER BY heart DESC")
    @Transactional
    List<Article> findArticlesOrderByHeart();

    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC")
    @Transactional
    List<Article> findArticlesOrderByCreateTime();
}
