package com.eightlow.decalcomanie.perfume.service.implement;

import com.eightlow.decalcomanie.common.exception.CustomErrorCode;
import com.eightlow.decalcomanie.common.exception.CustomException;
import com.eightlow.decalcomanie.perfume.dto.*;
import com.eightlow.decalcomanie.perfume.dto.request.PerfumeSearchRequest;
import com.eightlow.decalcomanie.perfume.dto.response.DailyRecommendResponse;
import com.eightlow.decalcomanie.perfume.dto.response.PerfumeNameResponse;
import com.eightlow.decalcomanie.perfume.entity.*;
import com.eightlow.decalcomanie.perfume.mapper.*;
import com.eightlow.decalcomanie.perfume.repository.*;
import com.eightlow.decalcomanie.perfume.service.IPerfumeService;
import com.eightlow.decalcomanie.sns.dto.PerfumeRateDto;
import com.eightlow.decalcomanie.user.entity.QUser;
import com.eightlow.decalcomanie.user.entity.User;
import com.eightlow.decalcomanie.user.service.IUserService;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.eightlow.decalcomanie.perfume.entity.QPerfume.perfume;

@Service
@Transactional
@RequiredArgsConstructor
public class PerfumeServiceImpl implements IPerfumeService {
    private final BrandRepository brandRepository;
    private final ScentRepository scentRepository;
    private final AccordRepository accordRepository;
    private final PerfumePickRepository perfumePickRepository;
    private final PerfumeMapper perfumeMapper;
    private final IUserService userService;
    private final BrandMapper brandMapper;
    private final ScentMapper scentMapper;
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    private final QPerfumePick perfumePick = QPerfumePick.perfumePick;
    private final QUser user = QUser.user;

    // id로 개별 향수 조회
    @Override
    public PerfumeDto getPerfume(int perfumeId) {
        Perfume perfume = em.find(Perfume.class, perfumeId);

        if(perfume == null) {
            throw new CustomException(CustomErrorCode.PERFUME_NOT_FOUND);
        }

        return perfumeMapper.toDto(perfume);
    }

    // 전체 브랜드 조회
    @Override
    public List<BrandDto> findAllBrand() {
        return brandMapper.toDto(brandRepository.findAll());
    }

    // 전체 향 조회
    @Override
    public List<ScentDto> findAllScent() {
        return scentMapper.toDto(scentRepository.findAll());
    }

    // 검색 조건에 맞는 향수 조회
    @Override
    public List<PerfumeDto> findMatchingPerfumes(PerfumeSearchRequest condition) {
        List<Perfume> searchResult = queryFactory
                .selectFrom(perfume)
                .where(
                        scentEq(condition.getScent()),
                        brandEq(condition.getBrand()),
                        genderEq(condition.getGender()),
                        keywordEq(condition.getKeyword()),
                        lastOrderTypeDataEq(condition.getOrderType(), condition),
                        perfume.perfumeId.gt(condition.getLastPerfumeId() == null ? 1 : condition.getLastPerfumeId())
                )
                .orderBy(orderTypeEq(condition.getOrderType()))
                .orderBy(perfume.perfumeId.asc())
                .limit(condition.getDataSize() == null ? 50 : condition.getDataSize())
                .fetch();

        return perfumeMapper.toDto(searchResult);
    }

    // 향수 찜
    @Override
    @Transactional
    public boolean pickPerfume(String userId, int perfumeId) {
        Perfume perfume = em.find(Perfume.class, perfumeId);
        User user = em.find(User.class, userId);

        if(user == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        if(perfume == null) {
            throw new CustomException(CustomErrorCode.PERFUME_NOT_FOUND);
        }

        // 이미 같은 데이터가 있는지 확인
        PerfumePickId pd = PerfumePickId.builder()
                .user(userId)
                .perfume(perfumeId)
                .build();

        PerfumePick existingPick = em.find(PerfumePick.class, pd);

        // 같은 데이터가 이미 있으면 삭제하고, 없으면 새로 추가
        if (existingPick != null) {
            perfumePickRepository.delete(existingPick);
            updatePickCount(perfume, -1);
            return false;
        } else {
            PerfumePick newPick = PerfumePick.builder()
                    .user(user)
                    .perfume(perfume)
                    .build();

            perfumePickRepository.save(newPick);
            updatePickCount(perfume, 1);
            return true;
        }
    }

    // 이미 찜한 향수인지 체크
    @Override
    public boolean isPickedPerfume(int perfumeId, String userId) {
        // 이미 같은 데이터가 있는지 확인
        PerfumePickId pd = PerfumePickId.builder()
                .user(userId)
                .perfume(perfumeId)
                .build();

        PerfumePick existingPick = em.find(PerfumePick.class, pd);

        if (existingPick == null) return false;
        return true;
    }

    // 사용자가 찜한 향수 모두 조회
    @Override
    public List<PerfumeDto> findAllPickedPerfume(String userId) {
        List<PerfumePick> searchResult = queryFactory.selectFrom(perfumePick)
                .where(perfumePick.user.userId.eq(userId))
                .fetch();

        List<PerfumeDto> searchedPerfumes = new ArrayList<>();

        for (PerfumePick p : searchResult) {
            Perfume perfume = p.getPerfume();
            searchedPerfumes.add(perfumeMapper.toDto(perfume));
        }

        return searchedPerfumes;
    }

    // 검색창 자동완성을 위해 향수 이름만 가져오기
    @Override
    public List<PerfumeNameResponse> findAllNames() {
        List<PerfumeNameResponse> searchResult = queryFactory
                .select(Projections.constructor(PerfumeNameResponse.class, perfume.name, perfume.nameOrg))
                .from(perfume)
                .fetch();

        return searchResult;
    }

    @Override
    public DailyRecommendResponse recommendByOccasion(String userId) {
        String today = LocalDate.now().toString();
        String curTime = LocalTime.now().toString();

        User loginUser = em.find(User.class, userId);

        if(loginUser == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        List<Perfume> season = queryFactory
                .selectFrom(perfume)
                .orderBy(seasonEq(today))
                .limit(10)
                .fetch();

        List<Perfume> dayNight = queryFactory
                .selectFrom(perfume)
                .orderBy(timeEq(curTime))
                .limit(10)
                .fetch();

        List<Perfume> ageGender = queryFactory
                .select(perfume)
                .from(perfumePick)
                .join(perfumePick.perfume, perfume)
                .join(perfumePick.user, user)
                .where(
                        user.age.eq(loginUser.getAge()),
                        user.gender.eq(loginUser.getGender())
                )
                .groupBy(perfume)
                .limit(10)
                .fetch();

        List<Perfume> overall = queryFactory
                .select(perfume)
                .from(perfumePick)
                .join(perfumePick.perfume, perfume)
                .join(perfumePick.user, user)
                .where(
                        user.age.eq(loginUser.getAge()),
                        user.gender.eq(loginUser.getGender())
                )
                .groupBy(perfume)
                .orderBy(seasonEq(today))
                .orderBy(Expressions.numberTemplate(Double.class,
                        "({0} / ({0} + {1}))", perfume.day, perfume.night).desc())
                .limit(10)
                .fetch();

        Collections.sort(season, Comparator.comparing(Perfume :: getPick).reversed());
        Collections.sort(dayNight, Comparator.comparing(Perfume :: getPick).reversed());
        Collections.sort(ageGender, Comparator.comparing(Perfume :: getPick).reversed());
        Collections.sort(overall, Comparator.comparing(Perfume :: getPick).reversed());

        return DailyRecommendResponse.builder()
                .season(perfumeMapper.toDto(season))
                .dayNight(perfumeMapper.toDto(dayNight))
                .ageGender(perfumeMapper.toDto(ageGender))
                .overall(perfumeMapper.toDto(overall))
                .age(loginUser.getAge())
                .gender(loginUser.getGender())
                .curSeason(getSeason(today))
                .curTime(getTime(curTime))
                .isDrawerPerfumeExist(userService.getUserPerfume(userId).size() > 0)
                .isUserPerfumeExist(userService.getUserPerfumeRecommend(userId).size() > 0)
                .build();
    }

    @Override
    @Transactional
    public void updatePerfumeRate(PerfumeRateDto perfumeRateDto, int perfumeId, float rate) {
        Perfume perfume = em.find(Perfume.class, perfumeId);

        if(perfume == null) {
            throw new CustomException(CustomErrorCode.PERFUME_NOT_FOUND);
        }

        // rate 계산 파트
        int perfumeCnt = perfumeRateDto.getCnt();
        float perfumeRateSum = perfumeRateDto.getRateSum();

        float newPerfumeRate = (perfumeRateSum + rate) / (perfumeCnt + 1);

        perfume.updateRate(newPerfumeRate);
    }

    private String getTime(String curTime) {
        int time = Integer.parseInt(curTime.split(":")[0]);

        if(time >= 6 && time < 18) {
            return "day";
        }

        return "night";
    }

    private String getSeason(String today) {
        int month = Integer.parseInt(today.split("-")[1]);

        if(month >= 3 && month < 6) {
            return "spring";
        }

        if(month >= 6 && month < 9) {
            return "summer";
        }

        if(month >= 9 && month < 12) {
            return "fall";
        }

        return "winter";
    }

    // 향수 찜 또는 찜 해제 시에 향수 전체 찜 개수에 반영
    public void updatePickCount(Perfume perfume, int value) {
        if (perfume != null) {
            perfume.updatePick(value);
        }
    }

    private OrderSpecifier<?> orderTypeEq(int orderType) {
        if(orderType == 1) {
            return perfume.pick.desc();
        }

        return perfume.rate.desc();
    }

    private OrderSpecifier<Float> seasonEq(String today) {
        int month = Integer.parseInt(today.split("-")[1]);

        if(month >= 3 && month < 6) {
            return perfume.spring.desc();
        }

        if(month >= 6 && month < 9) {
            return perfume.summer.desc();
        }

        if(month >= 9 && month < 12) {
            return perfume.fall.desc();
        }

        return perfume.winter.desc();
    }

    private OrderSpecifier<Float> timeEq(String curTime) {
        int time = Integer.parseInt(curTime.split(":")[0]);

        if(time >= 6 && time < 18) {
            return perfume.day.desc();
        }

        return perfume.night.desc();
    }

    private BooleanExpression lastOrderTypeDataEq(int orderType, PerfumeSearchRequest condition) {
        if(orderType == 1) {
            return perfume.pick.loe(condition.getLastPick() == null ? 999999999 : condition.getLastPick());
        }

        return perfume.rate.loe(condition.getLastRate() == null ? 5 : condition.getLastRate());
    }

    private BooleanExpression keywordEq(String keyword) {
        return StringUtils.hasText(keyword) ? perfume.nameOrg.containsIgnoreCase(keyword).or(perfume.name.containsIgnoreCase(keyword)) : null;
    }

    private BooleanExpression brandEq(List<Integer> brand) {
        return brand.size() > 0 ? perfume.brand.brandId.in(brand) : null;
    }

    private BooleanExpression genderEq(List<Integer> gender) {
        return gender.size() > 0 ? perfume.gender.in(gender) : null;
    }

    private BooleanExpression scentEq(List<Integer> scent) {
        if(scent.size() > 0) {
            List<Integer> perfumeIds = new ArrayList<>();

            // 먼저 사용자가 선택한 향을 accord로 가지고 있는 향수의 id를 받아온다
            for (int i = 0; i < scent.size(); i++) {
                List<Integer> result = accordRepository.findPerfumeIdsByScentId(scent.get(i));

                for (int j = 0; j < result.size(); j++) {
                    perfumeIds.add(result.get(j));
                }
            }

            return perfume.perfumeId.in(perfumeIds);
        }

        return null;
    }

}
