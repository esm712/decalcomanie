package com.eightlow.decalcomanie.perfume.service;

import com.eightlow.decalcomanie.perfume.dto.BrandDto;
import com.eightlow.decalcomanie.perfume.dto.PerfumeDto;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.perfume.dto.request.PerfumeSearchRequest;
import com.eightlow.decalcomanie.perfume.dto.response.DailyRecommendResponse;
import com.eightlow.decalcomanie.perfume.dto.response.PerfumeNameResponse;
import com.eightlow.decalcomanie.sns.dto.PerfumeRateDto;

import java.util.List;

public interface PerfumeService {
    PerfumeDto getPerfume(int perfumeId);

    List<BrandDto> findAllBrand();

    List<ScentDto> findAllScent();

    List<PerfumeDto> findMatchingPerfumes(PerfumeSearchRequest condition);

    boolean pickPerfume(String userId, int perfumeId);

    boolean isPickedPerfume(int perfumeId, String userId);

    List<PerfumeDto> findAllPickedPerfume(String userId);

    List<PerfumeNameResponse> findAllNames();

    DailyRecommendResponse recommendByOccasion(String userId);

    void updatePerfumeRate(PerfumeRateDto perfumeRateDto, int perfumeId, float rate);
}
