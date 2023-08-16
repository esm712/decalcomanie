package com.eightlow.decalcomanie.user.service;

import com.eightlow.decalcomanie.perfume.dto.PerfumeDto;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.user.dto.UserInfoDto;
import com.eightlow.decalcomanie.user.dto.request.UserInfoUpdateRequest;
import com.eightlow.decalcomanie.user.dto.response.FollowerResponse;
import com.eightlow.decalcomanie.user.dto.response.FollowingResponse;
import com.eightlow.decalcomanie.user.dto.response.ProfileResponse;

import java.util.List;

public interface IUserService {
    String modifyUserPerfume(String userId, int perfumeId);

    List<PerfumeDto> getUserPerfume(String userId);

    boolean isFollowing(String from, String to);

    String followUser(String from, String to);

    List<FollowingResponse> getFollowingUsers(String userId);

    List<FollowerResponse> getFollowers(String userId);

    UserInfoDto getUserInfo(String userId);

    // 사용자 개인 추천 향수
    List<PerfumeDto> recommendUserPerfume(String userId);

    List<FollowerResponse> getOtherFollowingUsers(String userId, String myId);

    List<FollowerResponse> getOtherFollowers(String userId, String myId);

    boolean checkDuplicated(String nickname);

    String updateUserInfo(UserInfoUpdateRequest request, String userId);

    void withdrawUser(String userId);

    // 사용자 추천 향수 캐시 조회
    List<PerfumeDto> getUserPerfumeRecommend(String userId);

    // 사용자 향 TOP 3 조회
    List<ScentDto> getTopThreeScent(String userId);

    ProfileResponse getUserProfile(String userId);
}
