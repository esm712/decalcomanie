package com.eightlow.decalcomanie.sns.service;

import java.util.List;

public interface GradeService {
    void createGradeFromRequest(int articleId, List<Integer> perfumes, List<Float> rates);

    void modifyGradeFromRequest(int articleId, List<Integer> perfumes, List<Float> rates);
}
