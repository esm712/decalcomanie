package com.eightlow.decalcomanie.user.controller;

import com.eightlow.decalcomanie.auth.entity.UserCredential;
import com.eightlow.decalcomanie.auth.security.CustomUserDetails;
import com.eightlow.decalcomanie.auth.service.JwtService;
import com.eightlow.decalcomanie.perfume.dto.PerfumeDto;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.sns.dto.request.FeedInquiryRequest;
import com.eightlow.decalcomanie.sns.dto.response.FeedResponse;
import com.eightlow.decalcomanie.sns.service.ArticleService;
import com.eightlow.decalcomanie.user.dto.UserInfoDto;
import com.eightlow.decalcomanie.user.dto.request.UserInfoUpdateRequest;
import com.eightlow.decalcomanie.user.dto.response.CommonResponse;
import com.eightlow.decalcomanie.user.dto.response.FollowerResponse;
import com.eightlow.decalcomanie.user.dto.response.FollowingResponse;
import com.eightlow.decalcomanie.user.dto.response.ProfileResponse;
import com.eightlow.decalcomanie.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserApiController {

    private final UserService userService;
    private final JwtService jwtService;
    private final ArticleService articleService;
    private final EntityManager em;

    @Value("${spring.kakao.admin-key}")
    private String kakaoAdminKey;

    // 사용자 향수 등록
    @PostMapping("/perfume/register")
    public ResponseEntity<String> registerUserPerfume(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, Integer> request) {
        String userMessage = userService.registerUserPerfume(userDetails.getUserId(), request.get("perfumeId"));
        return new ResponseEntity<>(userMessage, HttpStatus.CREATED);
    }

    // 사용자 향수 삭제
    @PostMapping("/perfume/delete")
    public ResponseEntity<String> deleteUserPerfume(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, Integer> request) {
        String userMessage = userService.deleteUserPerfume(userDetails.getUserId(), request.get("perfumeId"));
        return new ResponseEntity<>(userMessage, HttpStatus.CREATED);
    }

    // 사용자 향 TOP 3 조회
    @GetMapping("/scent/top")
    public ResponseEntity<List<ScentDto>> getTopThreeScent(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getTopThreeScent(userDetails.getUserId()), HttpStatus.OK);
    }

    // 사용자 향수 조회
    @GetMapping("/perfume")
    public ResponseEntity<List<PerfumeDto>> getUserPerfume(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getUserPerfume(userDetails.getUserId()), HttpStatus.OK);
    }

    // 팔로우
    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> request) {
        return new ResponseEntity<>(userService.follow(userDetails.getUserId(), request.get("to")), HttpStatus.CREATED);
    }

    // 언팔로우
    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Map<String, String> request) {
        return new ResponseEntity<>(userService.unfollow(userDetails.getUserId(), request.get("to")), HttpStatus.CREATED);
    }

    // 팔로잉 목록 조회
    @GetMapping("/following")
    public ResponseEntity<List<FollowingResponse>> getFollowingUsers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getFollowingUsers(userDetails.getUserId()), HttpStatus.OK);
    }

    // 팔로우 목록 조회
    @GetMapping("/follower")
    public ResponseEntity<List<FollowerResponse>> getFollowers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getFollowers(userDetails.getUserId()), HttpStatus.OK);
    }

    // 다른 유저의 팔로잉 목록 조회
    @GetMapping("/following/{userId}")
    public ResponseEntity<CommonResponse> getOtherFollowingUsers(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String userId) {
        UserInfoDto userInfoDto = userService.getUserInfo(userId).toBuilder()
                .isMe(userId.equals(userDetails.getUserId()))
                .build();

        CommonResponse response = CommonResponse.builder()
                .targetUser(userInfoDto)
                .data(userService.getOtherFollowingUsers(userId, userDetails.getUserId()))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 다른 유저의 팔로우 목록 조회
    @GetMapping("/follower/{userId}")
    public ResponseEntity<CommonResponse> getOtherFollowers(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String userId) {
        UserInfoDto userInfoDto = userService.getUserInfo(userId).toBuilder()
                .isMe(userId.equals(userDetails.getUserId()))
                .build();

        CommonResponse response = CommonResponse.builder()
                .targetUser(userInfoDto)
                .data(userService.getOtherFollowers(userId, userDetails.getUserId()))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 사용자 개인 추천 향수
    @GetMapping("/recommend")
    public ResponseEntity<List<PerfumeDto>> recommend(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getUserPerfumeRecommend(userDetails.getUserId()), HttpStatus.OK);
    }


    // 닉네임 중복검사
    @GetMapping("/update/check/{nickname}")
    public ResponseEntity<Map<String, Boolean>> checkDuplicated(@PathVariable String nickname) {
        Map<String, Boolean> responseMap = new HashMap<>();
        responseMap.put("available", userService.checkDuplicated(nickname));
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserInfoUpdateRequest request) {
        Map<String, String> responseMap = new HashMap<>();
        String userId = userDetails.getUserId();
        String updatedNickname = userService.updateUserInfo(request, userId);
        responseMap.put("nickname", updatedNickname);

        HttpHeaders responseHeader = new HttpHeaders();

        String accessToken = jwtService.generateAccessToken(updatedNickname, userId);

        responseHeader.set("accessToken", accessToken);

        return new ResponseEntity<>(responseMap, responseHeader, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDto> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(userService.getUserInfo(userDetails.getUserId()), HttpStatus.OK);
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<String> withdrawUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserCredential userCredential = em.find(UserCredential.class, userDetails.getUserId());

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("target_id_type", "user_id");
        requestBody.add("target_id", userCredential.getKakaoUserNum());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        String url = "https://kapi.kakao.com/v1/user/unlink";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        userService.withdrawUser(userDetails.getUserId());

        return new ResponseEntity<>("회원 탈퇴 완료!", HttpStatus.OK);
    }

    @PostMapping("/bookmark")
    public ResponseEntity<List<FeedResponse>> getBookmark(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestBody @Valid FeedInquiryRequest feedInquiryRequest){
        List<FeedResponse> responses  = articleService.getBookmarkArticle(feedInquiryRequest, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String userId) {
        return new ResponseEntity<>(userService.getUserProfile(userId, userDetails.getUserId()), HttpStatus.OK);
    }

}

