package com.eightlow.decalcomanie.user.service.implement;

import com.eightlow.decalcomanie.auth.respository.OAuthRepository;
import com.eightlow.decalcomanie.common.exception.CustomErrorCode;
import com.eightlow.decalcomanie.common.exception.CustomException;
import com.eightlow.decalcomanie.perfume.dto.PerfumeDto;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.perfume.entity.Perfume;
import com.eightlow.decalcomanie.perfume.entity.Scent;
import com.eightlow.decalcomanie.perfume.mapper.PerfumeMapper;
import com.eightlow.decalcomanie.perfume.mapper.ScentMapper;
import com.eightlow.decalcomanie.perfume.repository.PerfumePickRepository;
import com.eightlow.decalcomanie.perfume.repository.PerfumeRepository;
import com.eightlow.decalcomanie.sns.repository.ArticleRepository;
import com.eightlow.decalcomanie.sns.repository.BookMarkRepository;
import com.eightlow.decalcomanie.sns.repository.CommentRepository;
import com.eightlow.decalcomanie.user.dto.*;
import com.eightlow.decalcomanie.user.dto.request.UserInfoUpdateRequest;
import com.eightlow.decalcomanie.user.dto.response.FollowerResponse;
import com.eightlow.decalcomanie.user.dto.response.FollowingResponse;
import com.eightlow.decalcomanie.user.dto.response.ProfileResponse;
import com.eightlow.decalcomanie.user.entity.*;
import com.eightlow.decalcomanie.user.mapper.UserMapper;
import com.eightlow.decalcomanie.user.repository.*;
import com.eightlow.decalcomanie.user.service.IUserService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.*;

import static com.eightlow.decalcomanie.perfume.entity.QPerfume.perfume;
import static com.eightlow.decalcomanie.sns.entity.QArticle.article;
import static com.eightlow.decalcomanie.user.entity.QUserPerfumeRecommend.userPerfumeRecommend;
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserPerfumeRepository userPerfumeRepository;
    private final FollowRepository followRepository;
    private final UserScentRepository userScentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final BookMarkRepository bookMarkRepository;
    private final OAuthRepository oAuthRepository;
    private final PerfumePickRepository perfumePickRepository;
    private final UserMapper userMapper;
    private final ScentMapper scentMapper;
    private final PerfumeMapper perfumeMapper;
    private final EntityManager em;
    private final UserPerfumeRecommendRepository userPerfumeRecommendRepository;
    private final JPAQueryFactory queryFactory;
    private final PerfumeRepository perfumeRepository;

    /**
     * 사용자가 보유한 향수를 등록한다.
     * @param userId - 사용자 아이디
     * @param perfumeId - 향수 아이디
     * @return
     */
    @Override
    public String registerUserPerfume(String userId, Integer perfumeId) {
        UserPerfumeId userPerfumeId = new UserPerfumeId(userId, perfumeId);

        // 이미 등록되어 있는지 확인
        if(userPerfumeRepository.existsById(userPerfumeId)) {
            return "이미 등록된 향수입니다";
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Perfume perfume = perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PERFUME_NOT_FOUND));

        userPerfumeRepository.save(
                UserPerfume.builder()
                        .user(user)
                        .perfume(perfume)
                        .build()
        );

        return "향수가 등록되었습니다";
    }

    /**
     * 사용자가 보유한 향수를 삭제한다.
     * @param userId - 사용자 아이디
     * @param perfumeId - 향수 아이디
     * @return
     */
    @Override
    public String deleteUserPerfume(String userId, Integer perfumeId) {
        UserPerfumeId userPerfumeId = new UserPerfumeId(userId, perfumeId);

        UserPerfume userPerfume = userPerfumeRepository.findById(userPerfumeId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.SCENT_NOT_FOUND));

        userPerfumeRepository.delete(userPerfume);

        return "향수가 제거되었습니다";
    }

    @Override
    public List<PerfumeDto> getUserPerfume(String userId) {
        List<UserPerfume> userPerfumes = userPerfumeRepository.findByUser_UserId(userId);
        List<PerfumeDto> result = new ArrayList<>();

        for(UserPerfume perfume : userPerfumes) {
            result.add(perfumeMapper.toDto(perfume.getPerfume()));
        }

        return result;
    }

    // following = 팔로우 주체의 userId, followed = 팔로우를 할 사람의 userId
    @Override
    public String followUser(String following, String followed) {
        if(isFollowing(following, followed)) {
            followRepository.deleteByFollowingAndFollowed(following, followed);
            return "팔로우가 취소되었습니다";
        }

        Follow follow = Follow.builder()
                .following(following)
                .followed(followed)
                .build();

        followRepository.save(follow);
        return "팔로우가 완료되었습니다";
    }

    // 팔로잉 하고 있는 유저들의 정보를 가져온다
    @Override
    public List<FollowingResponse> getFollowingUsers(String userId) {
        // following 컬럼의 userId를 기준으로 조회
        List<Follow> myFollowing = followRepository.findByFollowing(userId);
        List<FollowingResponse> result = new ArrayList<>();

        if(myFollowing.size() > 0) {
            for(Follow follow : myFollowing) {
                // 사용자 정보와 좋아하는 향, 싫어하는 향의 정보들을 가져온다.
                UserInfoDto userInfoDto = getUserInfo(follow.getFollowed());

                // 반환 포맷에 맞는 response 생성
                FollowingResponse response = FollowingResponse.builder()
                        .userId(userInfoDto.getUser().getUserId())
                        .nickname(userInfoDto.getUser().getNickname())
                        .favorite(userInfoDto.getFavorities())
                        .hates(userInfoDto.getHates())
                        .picture(userInfoDto.getUser().getPicture())
                        .build();

                result.add(response);
            }
        }

        return result;
    }

    // 팔로워 목록 조회
    @Override
    public List<FollowerResponse> getFollowers(String userId) {
        List<Follow> myFollower = followRepository.findByFollowed(userId);
        List<FollowerResponse> result = new ArrayList<>();

        if(myFollower.size() > 0) {
            for(Follow follow : myFollower) {
                // 사용자 정보와 좋아하는 향, 싫어하는 향의 정보들을 가져온다.
                UserInfoDto userInfoDto = getUserInfo(follow.getFollowing());

                // 반환 포맷에 맞는 response 생성
                FollowerResponse response = FollowerResponse.builder()
                        .userId(userInfoDto.getUser().getUserId())
                        .nickname(userInfoDto.getUser().getNickname())
                        .favorite(userInfoDto.getFavorities())
                        .hates(userInfoDto.getHates())
                        .picture(userInfoDto.getUser().getPicture())
                        .isFollowing(isFollowing(userId, userInfoDto.getUser().getUserId()))
                        .build();

                result.add(response);
            }
        }

        return result;
    }

    // 팔로우 여부
    @Override
    public boolean isFollowing(String following, String followed) {
        Follow follow = followRepository.findByFollowingAndFollowed(following, followed);

        if(follow == null) return false;
        return true;
    }

    // 사용자 정보와 좋아하는 향, 싫어하는 향의 정보
    @Override
    public UserInfoDto getUserInfo(String userId) {
        User user = em.find(User.class, userId);

        if(user == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        List<ScentDto> favorite = new ArrayList<>();
        List<ScentDto> hate = new ArrayList<>();

        // userScent 테이블에서 팔로잉 하고 있는 사람의 좋아하는 향, 싫어하는 향을 조회
        List<UserScent> userScentList = userScentRepository.findUserScentByUser_UserId(userId);

        if(userScentList != null) {
            for (UserScent userScent : userScentList) {
                if(userScent.getStatus().getValue().equals("FAVORITE")) {
                    favorite.add(scentMapper.toDto(userScent.getScent()));
                } else if(userScent.getStatus().getValue().equals("HATE")) {
                    hate.add(scentMapper.toDto(userScent.getScent()));
                }
            }
        }

        return UserInfoDto.builder()
                .user(userMapper.toDto(user))
                .favorities(favorite)
                .hates(hate)
                .build();
    }

    @Override
    public List<FollowerResponse> getOtherFollowingUsers(String userId, String myId) {
        // following 컬럼의 userId를 기준으로 조회
        List<Follow> followings = followRepository.findByFollowing(userId);
        List<FollowerResponse> result = new ArrayList<>();

        if(followings.size() > 0) {
            for(Follow follow : followings) {
                // 사용자 정보와 좋아하는 향, 싫어하는 향의 정보들을 가져온다.
                UserInfoDto userInfoDto = getUserInfo(follow.getFollowed());

                FollowerResponse response = FollowerResponse.builder()
                        .userId(userInfoDto.getUser().getUserId())
                        .nickname(userInfoDto.getUser().getNickname())
                        .favorite(userInfoDto.getFavorities())
                        .hates(userInfoDto.getHates())
                        .picture(userInfoDto.getUser().getPicture())
                        .isFollowing(isFollowing(myId, userInfoDto.getUser().getUserId()))
                        .isFollowingButtonActivate(!userInfoDto.getUser().getUserId().equals(myId))
                        .build();

                result.add(response);
            }
        }

        return result;
    }

    @Override
    public List<FollowerResponse> getOtherFollowers(String userId, String myId) {
        List<Follow> followers = followRepository.findByFollowed(userId);
        List<FollowerResponse> result = new ArrayList<>();

        if(followers.size() > 0) {
            for(Follow follow : followers) {
                // 사용자 정보와 좋아하는 향, 싫어하는 향의 정보들을 가져온다.
                UserInfoDto userInfoDto = getUserInfo(follow.getFollowing());

                FollowerResponse response = FollowerResponse.builder()
                        .userId(userInfoDto.getUser().getUserId())
                        .nickname(userInfoDto.getUser().getNickname())
                        .favorite(userInfoDto.getFavorities())
                        .hates(userInfoDto.getHates())
                        .picture(userInfoDto.getUser().getPicture())
                        .isFollowing(isFollowing(myId, userInfoDto.getUser().getUserId()))
                        .isFollowingButtonActivate(!userInfoDto.getUser().getUserId().equals(myId))
                        .build();

                result.add(response);
            }
        }

        return result;
    }

    @Override
    public boolean checkDuplicated(String nickname) {
        User user = userRepository.findByNickname(nickname);
        if(user == null) return true;
        return false;
    }

    @Override
    @Transactional
    public String updateUserInfo(UserInfoUpdateRequest request, String userId) {
        User user = em.find(User.class, userId);

        if(user == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        user.updateNickname(request.getNickname());
        user.updatePicture(request.getPicture());

        userScentRepository.deleteAllByUser_UserId(userId);

        List<Integer> favorites = request.getFavorite();
        List<Integer> hates = request.getHate();

        for(int scentId : favorites) {
            Scent scent = em.find(Scent.class, scentId);

            if(scent == null) {
                throw new CustomException(CustomErrorCode.SCENT_NOT_FOUND);
            }

            userScentRepository.save(
                    UserScent.builder()
                            .scent(scent)
                            .user(user)
                            .status(Status.FAVORITE)
                            .build()
            );
        }

        for(int scentId : hates) {
            Scent scent = em.find(Scent.class, scentId);

            if(scent == null) {
                throw new CustomException(CustomErrorCode.SCENT_NOT_FOUND);
            }

            userScentRepository.save(
                    UserScent.builder()
                            .scent(scent)
                            .user(user)
                            .status(Status.HATE)
                            .build()
            );
        }

        return request.getNickname();
    }

    @Override
    @Transactional
    public void withdrawUser(String userId) {
        // 해당 회원의 서랍에 있는 향수 모두 삭제
        userPerfumeRepository.deleteAllByUser_UserId(userId);

        // 해당 회원이 찜한 향수 모두 삭제
        perfumePickRepository.deleteAllByUser_UserId(userId);

        // 해당 회원의 선호 향 정보 모두 삭제
        userScentRepository.deleteAllByUser_UserId(userId);

        // 해당 회원이 스크랩한 글 모두 스크랩 해제
        bookMarkRepository.deleteAllByUser_UserId(userId);

        // 해당 회원이 쓴 글의 userId 모두 유령프로필의 userId로 변경
        articleRepository.setUserIdToGhostAccount(userId);

        // 해당 회원이 쓴 댓글의 userId 모두 유령프로필의 userId로 변경
        commentRepository.setUserIdToGhostAccount(userId);

        // 팔로우 테이블에서 해당 회원이 포함된 모든 항목을 제거
        followRepository.deleteAllByFollowed(userId);
        followRepository.deleteAllByFollowing(userId);

        // 해당 회원의 userCredential 및 user 정보 삭제
        userRepository.deleteById(userId);
        oAuthRepository.deleteById(userId);
    }

    // 사용자 개인 추천 향수
    @Override
    public List<PerfumeDto> recommendUserPerfume(String userId) {
        // 사용자 향 단위 벡터 계산
        Map<ScentDto,Double> userPerfumeVector = userAccordVector(userId);

        // 사용자가 보유하지 않은 모든 향수들에 대한 향수 단위 벡터 계산
        Map<PerfumeDto,Map<ScentDto,Double>> allPerfumeVector = allAccordVector(userId);

        // 사용자 향 단위 벡터를 모든 향수 향 단위 벡터와 유사도 계산
        List<PerfumeWeight> result = calculateSimilarity(userPerfumeVector,allPerfumeVector);
        // Percentage로 정렬
        Collections.sort(result, Comparator.reverseOrder());
        // 탑 10 추출
        List<PerfumeDto> perfumeList = new ArrayList<>();
        for (PerfumeWeight pair : result) {
            perfumeList.add(pair.getFirst());
        }
        // result의 상단 10개 추출
        List<PerfumeDto> perfumeResultList = perfumeList.subList(0, Math.min(result.size(),10));

        // 사용자에게 추천하였던 향수 전부 삭제
        userPerfumeRecommendRepository.deleteAllByUser_UserId(userId);

        // 결과 한번에 DB 저장
        List<UserPerfumeRecommend> recommendList = new ArrayList<>();
        for(PerfumeDto p : perfumeResultList){
            Integer perfumeId = p.getPerfumeId();
            Perfume perfume = em.find(Perfume.class, perfumeId);
            User user = em.find(User.class, userId);
            if(user == null) {
                throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
            }
            if(perfume == null) {
                throw new CustomException(CustomErrorCode.PERFUME_NOT_FOUND);
            }

            UserPerfumeRecommend userPerfumeRecommend = UserPerfumeRecommend.builder()
                    .user(user)
                    .perfume(perfume)
                    .build();
            recommendList.add(userPerfumeRecommend);
        }
        userPerfumeRecommendRepository.saveAll(recommendList);
        return perfumeResultList;
    }

    // 사용자 향 단위 벡터와 모든 향수 향 단위 벡터와 유사도 계산
    private List<PerfumeWeight> calculateSimilarity(Map<ScentDto, Double> userPerfumeVector, Map<PerfumeDto, Map<ScentDto, Double>> allPerfumeVector) {
        List<PerfumeWeight> result = new ArrayList<>();
        for(PerfumeDto perfumeDto: allPerfumeVector.keySet()){
            Map<ScentDto, Double> perfumePercent = allPerfumeVector.get(perfumeDto);
            double similarity = 0.0;
            for(ScentDto scentDto :userPerfumeVector.keySet()){
                similarity += userPerfumeVector.get(scentDto)*(perfumePercent.get(scentDto) == null ? 0 : perfumePercent.get(scentDto));
            }
            result.add(new PerfumeWeight(perfumeDto,similarity));
        }
        return result;
    }

    // 유저가 보유한 향수들을 제외하고 향 단위 벡터를 계산하는 로직
    public Map<PerfumeDto, Map<ScentDto, Double>> allAccordVector(String userId) {
        Map<PerfumeDto, Map<ScentDto, Double>> result = new HashMap<>();

        List<UserPerfume> userPerfumes = userPerfumeRepository.findByUser_UserId(userId);

        List<PerfumeDto> unpossessedPerfumes = perfumeMapper.toDto(queryFactory.
                selectFrom(perfume)
                .where(
                        userPerfumeEq(userPerfumes)
                )
                .fetch());

        for(PerfumeDto perfumeDto : unpossessedPerfumes){
            Map<ScentDto, Double> scentPercent = calculate(perfumeDto);
            result.put(perfumeDto,scentPercent);
        }

        return result;
    }

    // 향수 하나에 대하여 향수 단위 벡터를 만드는 메소드
    private Map<ScentDto, Double> calculate(PerfumeDto perfumeDto) {
        Map<ScentDto, Double> result = new HashMap<>();
        List<ScentDto> accordList = perfumeDto.getAccord();
        int sum = sumScentWeight(accordList);
        for(ScentDto scentDto : accordList) {
            double percent = (double) scentDto.getWeight()/(double) sum;
            result.put(scentDto,percent);
        }
        return result;
    }

    // 사용자 향 단뒤 벡터 계산
    public Map<ScentDto,Double> userAccordVector(String userId) {
        // 사용자가 보유하고 있는 향수의 향들에대한 퍼센트 Map 생성
        Map<ScentDto,Double> userScentPercent = new HashMap<>();

        // 사용자가 보유하고 있는 향수 정보를 가져온다.
        List<UserPerfume> userPerfumes = userPerfumeRepository.findByUser_UserId(userId);
        int userPerfumeNum = userPerfumes.size(); // 사용자가 보유한 향수 개수

        // 사용자의 향수 x 향 테이블 계산
        // 사용자가 보유하고 있는 향수
        for(UserPerfume userPerfume : userPerfumes) {
            PerfumeDto perfumeDto = perfumeMapper.toDto(userPerfume.getPerfume());
            List<ScentDto> accordList = perfumeDto.getAccord();
            int sumScent = sumScentWeight(accordList);
            for(ScentDto scentDto : accordList){
                Double scentPercent = (double) scentDto.getWeight() / (double) sumScent;
                if(userScentPercent.containsKey(scentDto)){
                    double percent = userScentPercent.get(scentDto) + scentPercent;
                    userScentPercent.put(scentDto,percent);
                }else{
                    userScentPercent.put(scentDto,scentPercent);
                }
            }
        }

        // userScentPercent를 단위벡터로 변경
        for( ScentDto scentDto : userScentPercent.keySet()){
            double percent = userScentPercent.get(scentDto)/ (double) userPerfumeNum;
            userScentPercent.put(scentDto,percent);
        }

        return userScentPercent;
    }

    // 향수 향의 가중치의 합을 반환해주는 메소드
    private int sumScentWeight(List<ScentDto> accordList) {
        int sum = 0;
        for(ScentDto scentDto : accordList){
            sum += scentDto.getWeight();
        }
        return sum;
    }

    // 사용자 추천 향수 캐시 조회
    @Override
    public List<PerfumeDto> getUserPerfumeRecommend(String userId){
        List<UserPerfumeRecommend> searchResult = queryFactory.selectFrom(userPerfumeRecommend)
                .where(userPerfumeRecommend.user.userId.eq(userId))
                .fetch();

        List<PerfumeDto> result = new ArrayList<>();

        if(getUserPerfume(userId).size()==0){
            return result;
        }

        for(UserPerfumeRecommend p : searchResult){
            Perfume perfume = p.getPerfume();
            result.add(perfumeMapper.toDto(perfume));
        }

        return result;
    }

    // 사용자 TOP 3 향 조회
    @Override
    public List<ScentDto> getTopThreeScent(String userId) {
        List<ScentDto> result = new ArrayList<>();
        // 사용자가 보유하고 있는 향수의 향들에대한 퍼센트 Map 생성
        Map<ScentDto,Double> userScentPercent = new HashMap<>();

        // 사용자가 보유하고 있는 향수의 향들에대한 퍼센트 List 생성
        List<ScentPercent> userScentPercentList = new ArrayList<>();

        // 사용자가 보유하고 있는 향수 정보를 가져온다.
        List<UserPerfume> userPerfumes = userPerfumeRepository.findByUser_UserId(userId);

        // 사용자의 향수 x 향 테이블 계산
        // 사용자가 보유하고 있는 향수
        for(UserPerfume userPerfume : userPerfumes) {
            PerfumeDto perfumeDto = perfumeMapper.toDto(userPerfume.getPerfume());
            List<ScentDto> accordList = perfumeDto.getAccord();
            int sumScent = sumScentWeight(accordList);
            for(ScentDto scentDto : accordList){
                Double scentPercent = (double) scentDto.getWeight() / (double) sumScent;
                if(userScentPercent.containsKey(scentDto)){
                    double percent = userScentPercent.get(scentDto) + scentPercent;
                    userScentPercent.put(scentDto,percent);
                }else{
                    userScentPercent.put(scentDto,scentPercent);
                }
            }
        }

        // userScentPercent를 단위벡터로 변경
        for( ScentDto scentDto : userScentPercent.keySet()){
            userScentPercentList.add(new ScentPercent(scentDto,userScentPercent.get(scentDto)));
        }
        Collections.sort(userScentPercentList,Comparator.reverseOrder());
        for(ScentPercent pair :userScentPercentList){
            result.add(pair.getFirst());
        }

        return result.subList(0, Math.min(userScentPercentList.size(),3));
    }

    @Override
    public ProfileResponse getUserProfile(String userId, String myId) {
        UserInfoDto userInfoDto = getUserInfo(userId).toBuilder()
                .isFollowing(isFollowing(myId, userId))
                .isMe(userId.equals(myId) ? true: false)
                .build();

        ProfileResponse profileResponse = ProfileResponse.builder()
                .following(getFollowingUsers(userId).size())
                .follower(getFollowers(userId).size())
                .userInfo(userInfoDto)
                .build();

        return profileResponse;
    }

    private BooleanExpression articleIdEq(List<Integer> bookmarkArticleIds) {
        return bookmarkArticleIds.size() > 0 ? article.articleId.in(bookmarkArticleIds) : null;
    }

    private BooleanExpression userPerfumeEq(List<UserPerfume> userPerfumes) {
        if(userPerfumes.size() > 0) {
            List<Integer> perfumeIds = new ArrayList<>();

            for(UserPerfume userPerfume : userPerfumes) {
                perfumeIds.add(userPerfume.getPerfume().getPerfumeId());
            }

            return perfume.perfumeId.notIn(perfumeIds);
        }

        return null;
    }
}
