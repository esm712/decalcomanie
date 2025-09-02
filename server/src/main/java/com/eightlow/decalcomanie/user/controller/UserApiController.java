package com.eightlow.decalcomanie.user.controller;

import com.eightlow.decalcomanie.auth.dto.OAuthToken;
import com.eightlow.decalcomanie.auth.entity.UserCredential;
import com.eightlow.decalcomanie.auth.service.JwtService;
import com.eightlow.decalcomanie.perfume.dto.PerfumeDto;
import com.eightlow.decalcomanie.perfume.dto.ScentDto;
import com.eightlow.decalcomanie.sns.dto.request.FeedInquiryRequest;
import com.eightlow.decalcomanie.sns.dto.response.FeedResponse;
import com.eightlow.decalcomanie.sns.service.IArticleService;
import com.eightlow.decalcomanie.user.dto.UserInfoDto;
import com.eightlow.decalcomanie.user.dto.request.UserInfoUpdateRequest;
import com.eightlow.decalcomanie.user.dto.response.CommonResponse;
import com.eightlow.decalcomanie.user.dto.response.FollowerResponse;
import com.eightlow.decalcomanie.user.dto.response.FollowingResponse;
import com.eightlow.decalcomanie.user.dto.response.ProfileResponse;
import com.eightlow.decalcomanie.user.service.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserApiController {

    private final IUserService userService;
    private final JwtService jwtService;
    private final IArticleService articleService;
    private final EntityManager em;

    @Value("${spring.kakao.admin-key}")
    private String kakaoAdminKey;

    // 사용자 향수 등록
    @PostMapping("/perfume/register")
    public ResponseEntity<String> registerUserPerfume(@RequestBody Map<String, Integer> request, HttpServletRequest req) {
        String userMessage = userService.registerUserPerfume((String)req.getAttribute("userId"), request.get("perfumeId"));
        return new ResponseEntity<>(userMessage, HttpStatus.CREATED);
    }

    // 사용자 향수 삭제
    @PostMapping("/perfume/delete")
    public ResponseEntity<String> deleteUserPerfume(@RequestBody Map<String, Integer> request, HttpServletRequest req) {
        String userMessage = userService.deleteUserPerfume((String)req.getAttribute("userId"), request.get("perfumeId"));
        return new ResponseEntity<>(userMessage, HttpStatus.CREATED);
    }

    // 사용자 향 TOP 3 조회
    @GetMapping("/scent/top")
    public ResponseEntity<List<ScentDto>> getTopThreeScent(HttpServletRequest req){
        return new ResponseEntity<>(userService.getTopThreeScent((String)req.getAttribute("userId")), HttpStatus.OK);
    }

    // 사용자 향수 조회
    @GetMapping("/perfume")
    public ResponseEntity<List<PerfumeDto>> getUserPerfume(HttpServletRequest req) {
        return new ResponseEntity<>(userService.getUserPerfume((String)req.getAttribute("userId")), HttpStatus.OK);
    }

    // 팔로우
    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestBody Map<String, String> request, HttpServletRequest req) {
        return new ResponseEntity<>(userService.follow((String)req.getAttribute("userId"), request.get("to")), HttpStatus.CREATED);
    }

    // 언팔로우
    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@RequestBody Map<String, String> request, HttpServletRequest req) {
        return new ResponseEntity<>(userService.unfollow((String)req.getAttribute("userId"), request.get("to")), HttpStatus.CREATED);
    }

    // 팔로잉 목록 조회
    @GetMapping("/following")
    public ResponseEntity<List<FollowingResponse>> getFollowingUsers(HttpServletRequest req) {
        return new ResponseEntity<>(userService.getFollowingUsers((String)req.getAttribute("userId")), HttpStatus.OK);
    }

    // 팔로우 목록 조회
    @GetMapping("/follower")
    public ResponseEntity<List<FollowerResponse>> getFollowers(HttpServletRequest req) {
        return new ResponseEntity<>(userService.getFollowers((String)req.getAttribute("userId")), HttpStatus.OK);
    }

    // 다른 유저의 팔로잉 목록 조회
    @GetMapping("/following/{userId}")
    public ResponseEntity<CommonResponse> getOtherFollowingUsers(@PathVariable String userId, HttpServletRequest req) {
        UserInfoDto userInfoDto = userService.getUserInfo(userId).toBuilder()
                .isMe(userId.equals((String)req.getAttribute("userId")))
                .build();

        CommonResponse response = CommonResponse.builder()
                .targetUser(userInfoDto)
                .data(userService.getOtherFollowingUsers(userId, (String)req.getAttribute("userId")))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 다른 유저의 팔로우 목록 조회
    @GetMapping("/follower/{userId}")
    public ResponseEntity<CommonResponse> getOtherFollowers(@PathVariable String userId, HttpServletRequest req) {
        UserInfoDto userInfoDto = userService.getUserInfo(userId).toBuilder()
                .isMe(userId.equals((String)req.getAttribute("userId")))
                .build();

        CommonResponse response = CommonResponse.builder()
                .targetUser(userInfoDto)
                .data(userService.getOtherFollowers(userId, (String)req.getAttribute("userId")))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 사용자 개인 추천 향수
    @GetMapping("/recommend")
    public ResponseEntity<List<PerfumeDto>> recommend(HttpServletRequest req) {
        return new ResponseEntity<>(userService.getUserPerfumeRecommend((String)req.getAttribute("userId")), HttpStatus.OK);
    }


    // 닉네임 중복검사
    @GetMapping("/update/check/{nickname}")
    public ResponseEntity<Map<String, Boolean>> checkDuplicated(@PathVariable String nickname) {
        Map<String, Boolean> responseMap = new HashMap<>();
        responseMap.put("available", userService.checkDuplicated(nickname));
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateUserInfo(@RequestBody UserInfoUpdateRequest request, HttpServletRequest req) {
        Map<String, String> responseMap = new HashMap<>();
        String userId = (String)req.getAttribute("userId");
        String updatedNickname = userService.updateUserInfo(request, userId);
        responseMap.put("nickname", updatedNickname);

        HttpHeaders responseHeader = new HttpHeaders();

        String accessToken = jwtService.generateAccessToken(updatedNickname, userId);

        responseHeader.set("accessToken", accessToken);

        return new ResponseEntity<>(responseMap, responseHeader, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDto> getUserInfo(HttpServletRequest req) {
        return new ResponseEntity<>(userService.getUserInfo((String)req.getAttribute("userId")), HttpStatus.OK);
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<String> withdrawUser(HttpServletRequest req) {
        UserCredential userCredential = em.find(UserCredential.class, req.getAttribute("userId"));

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

        userService.withdrawUser((String)req.getAttribute("userId"));

        return new ResponseEntity<>("회원 탈퇴 완료!", HttpStatus.OK);
    }

    @PostMapping("/bookmark")
    public ResponseEntity<List<FeedResponse>> getBookmark(@RequestBody @Valid FeedInquiryRequest feedInquiryRequest,
                                                          HttpServletRequest req){
        List<FeedResponse> responses  = articleService.getBookmarkArticle(feedInquiryRequest, (String)req.getAttribute("userId"));
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String userId, HttpServletRequest req) {
        return new ResponseEntity<>(userService.getUserProfile(userId, (String)req.getAttribute("userId")), HttpStatus.OK);
    }

}

