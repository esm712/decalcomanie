package com.eightlow.decalcomanie.sns.service;

import com.eightlow.decalcomanie.perfume.entity.Perfume;
import com.eightlow.decalcomanie.sns.entity.Article;
import com.eightlow.decalcomanie.sns.entity.ArticlePerfume;
import com.eightlow.decalcomanie.sns.repository.ArticlePerfumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class GradeServiceImpl implements GradeService {

    private final ArticlePerfumeRepository articlePerfumeRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void createGradeFromRequest(int articleId, List<Integer> perfumes, List<Float> rates) {
        // ArticlePerfume 테이블에 정보 저장 
        for (int i = 0; i < perfumes.size(); i++) {
            // TODO: 이 부분이 select로 데이터를 많이 가져옴 (개선 가능성 있음)
            Article article = entityManager.find(Article.class, articleId);
            Perfume perfume = entityManager.find(Perfume.class, perfumes.get(i));

            ArticlePerfume articlePerfume = ArticlePerfume.builder()
                    .article(article)
                    .perfume(perfume)
                    .rate(rates.get(i))
                    .build();
            articlePerfumeRepository.save(articlePerfume);
        }
    }

    @Override
    @Transactional
    public void modifyGradeFromRequest(int articleId, List<Integer> perfumes, List<Float> rates) {
        // ArticlePerfume의 rate 정보 수정
        for (int i = 0; i < perfumes.size(); i++) {
            articlePerfumeRepository.updateRateByArticleIdAndPerfumeId(articleId, perfumes.get(i), rates.get(i));
        }
    }
}
