package com.eightlow.decalcomanie.perfume.controller;

import com.eightlow.decalcomanie.auth.security.CustomUserDetails;
import com.eightlow.decalcomanie.perfume.dto.BrandDto;
import com.eightlow.decalcomanie.perfume.dto.PerfumeDto;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.perfume.dto.request.PerfumeSearchRequest;
import com.eightlow.decalcomanie.perfume.dto.response.DailyRecommendResponse;
import com.eightlow.decalcomanie.perfume.dto.response.PerfumeNameResponse;
import com.eightlow.decalcomanie.perfume.dto.response.SearchResponse;
import com.eightlow.decalcomanie.perfume.service.PerfumeService;
import com.eightlow.decalcomanie.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/perfume")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PerfumeApiController {

    private final PerfumeService perfumeService;
    private final UserService userService;

    // 향수 검색
    @PostMapping("/search")
    public ResponseEntity<SearchResponse> searchPerfume(@RequestBody PerfumeSearchRequest request) {
        List<PerfumeDto> searchedPerfumes = perfumeService.findMatchingPerfumes(request);

        SearchResponse response = SearchResponse.builder()
                .searchedPerfumes(searchedPerfumes)
                .isLastPage(searchedPerfumes.size() < (request.getDataSize() == null ? 50 : request.getDataSize()))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 향수 상세보기
    @GetMapping("/detail/{perfumeId}")
    public ResponseEntity<PerfumeDto> perfumeDetail(@PathVariable("perfumeId") int perfumeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        PerfumeDto perfumeDto = perfumeService.getPerfume(perfumeId);

        if(perfumeDto != null) {
            PerfumeDto pdto = perfumeDto.toBuilder()
                    .picked(perfumeService.isPickedPerfume(perfumeId, userDetails.getUserId()))
                    .build();
            return new ResponseEntity<>(pdto, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 전체 브랜드 호출
    @GetMapping("/search/brand")
    public ResponseEntity<List<BrandDto>> getAllBrand() {
        List<BrandDto> brandList = perfumeService.findAllBrand();

        if(brandList != null) {
            return new ResponseEntity<>(brandList, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // 전체 향 호출
    @GetMapping("/search/scent")
    public ResponseEntity<List<ScentDto>> getAllScent() {
        List<ScentDto> scentList = perfumeService.findAllScent();

        if(scentList != null) {
            return new ResponseEntity<>(scentList, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // 향수 찜, 찜 해제
    @PostMapping("/pick")
    public ResponseEntity<Map<String, Boolean>> pick(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, Integer> request) {
        Map<String, Boolean> responseMap = new HashMap<>();
        responseMap.put("isPicked", perfumeService.pickPerfume(userDetails.getUserId(), request.get("perfumeId")));
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    // 찜한 향수 보기
    @GetMapping("/picked")
    public ResponseEntity<List<PerfumeDto>> picked(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(perfumeService.findAllPickedPerfume(userDetails.getUserId()), HttpStatus.OK);
    }

    @GetMapping("/search/names")
    public ResponseEntity<List<PerfumeNameResponse>> getNames(HttpServletRequest req) {
        return new ResponseEntity<>(perfumeService.findAllNames(), HttpStatus.OK);
    }

    @GetMapping("/today")
    public ResponseEntity<DailyRecommendResponse> getTodaysPerfume(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(perfumeService.recommendByOccasion(userDetails.getUserId()), HttpStatus.OK);
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<PerfumeDto>> updateRecommendPerfume(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.recommendUserPerfume(userDetails.getUserId());
        return new ResponseEntity<>(userService.getUserPerfumeRecommend(userDetails.getUserId()), HttpStatus.OK);
    }
}

